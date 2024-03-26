## HAVING
- 집계 전 로우를 필터링하는 역할을 WHERE 절이 수행하는 반면, HAVING 절은 집계된 후 로우에서 데이터를 필터링하는 역할을 한다.
- HAVING 절은 한 그룹의 집계 결과를 다른 집계 값과 비교하는 능력이 있다.
- HAVING 절로 다음 문제를 해결할 수 있다.
  - 평균 배송 시간이 모든 판매회사의 평균 배송 시간을 초과하는 판매자를 찾아라.
  - 특정 기간의 총 판매액이 동일한 카테고리에 속한 모든 제품의 평균 판매액보다 큰 제품을 찾아라(카테고리별 최고의 제품을 찾아라).
  - 임의의 일자에 주문한 총액이 1,000달러 이상인 고객을 모두 찾아라(일별 우량 고객을 찾아라).
  - 지난 분기 단일 제품 항목의 주문 비율을 계산하라.

![판매_주문_데이터베이스_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/f91f73bc-d9f6-4185-8027-a17e310e3551)
- 처음 두 문제의 예시를 위한 테이블이다.
- 배송 시간이 느린 판매자를 찾기 위해 Vendors 와 PurchaseOrders 테이블을 사용한다.

```tsql
SELECT v.VendName, AVG(DATEDIFF(DAY, p.OrderDate, p.DeliveryDate)) AS DeliveryDays
FROM Vendors AS v
       INNER JOIN PurchaseOrders AS p
                  ON v.VendorID = p.VendorID
WHERE p.DeliveryDate IS NOT NULL
  AND p.OrderDate BETWEEN '2015-10-01' AND '2015-12-31'
GROUP BY v.VendName
HAVING AVG(DATEDIFF(DAY, p.OrderDate, p.DeliveryDate)) >
       (SELECT AVG(DATEDIFF(DAY, p2.OrderDate, p2.DeliveryDate))
        FROM PurchaseOrders AS p2
        WHERE p2.DeliveryDate IS NOT NULL
          AND p2.OrderDate BETWEEN '2015-10-01' AND '2015-12-31');
```
- 2015년 마지막 분기 동안 배송 시간이 평균보다 느린 판매자를 찾는 쿼리이다.
- 대다수 DBMS 의 구현 내용과 ISO SQL 표준에 따르면 DeliveryDays 이름으로는 SELECT 절에서 계산을 수행하거나 HAVING 절을 사용하거나 동일한 표현식 내에서 사용할 수 없다.
  - 반드시 해당 표현식을 있는 그대로 재사용해야 한다.

```tsql
SELECT c.CategoryDescription,
       p.ProductName,
       SUM(od.QuotedPrice * od.QuantityOrdered) AS TotalSales
FROM Products AS p
       INNER JOIN Order_Details AS od
                  ON p.ProductNumber = od.ProductNumber
       INNER JOIN Categories AS c
                  ON c.CategoryID = p.CategoryID
       INNER JOIN Orders AS o
                  ON o.OrderNumber = od.OrderNumber
WHERE o.OrderDate BETWEEN '2015-10-01' AND '2015-12-31'
GROUP BY p.CategoryID, c.CategoryDescription, p.ProductName
HAVING SUM(od.QuotedPrice * od.QuantityOrdered) >
       (SELECT AVG(SumCategory)
        FROM (SELECT p2.CategoryID,
                     SUM(od2.QuotedPrice * od2.QuantityOrdered) AS SumCategory
              FROM Products AS p2
                     INNER JOIN Order_Details AS od2
                                ON p2.ProductNumber = od2.ProductNumber
                     INNER JOIN Orders AS o2
                                ON o2.OrderNumber = od2.OrderNumber
              WHERE p2.CategoryID = p.CategoryID
                AND o2.OrderDate BETWEEN '2015-10-01' AND '2015-12-31'
              GROUP BY p2.CategoryID, p2.ProductNumber) AS s
        GROUP BY CategoryID)
ORDER BY c.CategoryDescription, p.ProductName;
```
- 2015년 4분기 카테고리별 우량 판매자를 찾는 쿼리이다.
- 이 쿼리의 HAVING 절은 매우 복잡하다.
  - 먼저 현재 그룹의 카테고리에 속하는 제품별 판매액 합계를 계산한 후 이 합의 평균을 계산해야 한다.
  - 현재 그룹의 카테고리별로 필터링하는 것은 서브쿼리로 처리한다.
  - 특정 날짜 범위로 데이터를 제한하기 위해 Orders 테이블과 조인하면서 쿼리가 더 복잡해졌다.

```tsql
WITH CatProdData AS
       (SELECT c.CategoryID,
               c.CategoryDescription,
               p.ProductName,
               od.QuotedPrice,
               od.QuantityOrdered
        FROM Products AS p
               INNER JOIN Order_Details AS od
                          ON p.ProductName = od.ProductNumber
               INNER JOIN Categories AS c
                          ON c.CategoryID = p.CategoryID
               INNER JOIN Orders AS o
                          ON o.OrderNumber = od.OrderNumber
        WHERE o.OrderDate BETWEEN '2015-10-01' AND '2015-12-31')
SELECT d.CategoryDescription,
       d.ProductName,
       SUM(d.QuotedPrice * d.QuantityOrdered) AS TotalSales
FROM CatProdData AS d
GROUP BY d.CategoryID, d.CategoryDescription, d.ProductName
HAVING SUM(d.QuotedPrice * d.QuantityOrdered) >
       (SELECT AVG(SumCategory)
        FROM (SELECT d2.CategoryID,
                     SUM(d2.QuotedPrice * d2.QuantityOrdered) AS SumCategory
              FROM CatProdData AS d2
              WHERE d2.CategoryID = d.CategoryID
              GROUP BY d2.CategoryID, d2.ProductName) AS s
        GROUP BY CategoryID)
ORDER BY d.CategoryDescription, d.ProductName;
```
- CTE(공통 테이블 표현식) 으로 간략하게 만든 쿼리이다.
- CTE 를 사용하면 복잡한 조인과 날짜의 조건을 한 번만 처리해 놓고 서브 쿼리와 외부 쿼리에서 재사용할 수 있다.

## 정리
- 그루핑하기 전에 로우를 필터링할 때는 WHERE 절을 사용하고, 그루핑 후에는 HAVING 절을 사용한다.
- HAVING 절을 사용해 집계 표현식에 조건을 줄 수 있다.
- SELECT 절에 집계 표현식에 대한 별칭을 부여할 수는 있지만, 이를 HAVING 절에서 사용하려면 별칭이 아닌 표현식을 다시 사용해야 한다.
  - SELECT 절에 기술한 별칭은 사용할 수 없다.
- 집계 값이 복잡한 서브쿼리가 반환한 값과 다른 집계 값을 비교할 수 있다.
