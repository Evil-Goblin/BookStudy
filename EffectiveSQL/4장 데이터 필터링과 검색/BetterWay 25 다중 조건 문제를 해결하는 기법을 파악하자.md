## 복합 조건 쿼리
- 관계를 맺은 테이블에 적용된 복합 조건을 기준으로 다른 테이블에 있는 로우를 반환해야 할 때 복잡해진다.
- 이런 문제를 해결하는 방법 몇가지는 다음과 같다.
  - IS NULL 조건과 함께 INNER JOIN 이나 OUTER JOIN 사용
  - 서브쿼리와 IN 이나 NOT IN 사용
  - 서브쿼리와 함께 EXISTS 나 NOT EXISTS 사용

![Order_ERD](https://github.com/Evil-Goblin/BookStudy/assets/74400861/ce4e254d-2059-4862-b398-61e822689d1a)
- 예제 데이터베이스 설계이다.
- 우량 고객을 찾기 위해 스케이트보드뿐만 아니라 헬멧과 무릎 보호대, 장갑까지 구매한 고객 명단을 추출하려 한다.

```sql
SELECT c.CustomerID, c.CustFirstName, c.CustLastName
FROM Customers AS c
WHERE c.CustomerID IN
      (SELECT o.CustomerID
       FROM Orders AS o
                INNER JOIN Order_Details AS od
                           ON o.OrderNumber = od.OrderNumber
                INNER JOIN Products AS p
                           ON p.ProductNumber = od.ProductNumber
       WHERE p.ProductName
                 IN ('Skateboard', 'Helmet', 'Knee Pads', 'Gloves'));
```
- 잘못된 방식으로 작성된 쿼리이다.
- 이 쿼리는 스케이트보드 또는 헬멧 또는 무릎 보호대 또는 장갑을 주문한 고객 목록을 추출하기 때문에 원하는 결과가 나오지 않는다.

```sql
SELECT c.CustomerID, c.CustFirstName, c.CustLastName
FROM Customers AS c
         INNER JOIN
     (SELECT DISTINCT o.CustomerID
      FROM Orders AS o
               INNER JOIN Order_Details AS od
                          ON o.OrderNumber = od.OrderNumber
               INNER JOIN Products AS p
                          ON p.ProductNumber = od.ProductNumber
      WHERE p.ProductName = 'Skateboard') AS OSk
     ON c.CustomerID = OSk.CustomerID
         INNER JOIN
     (SELECT DISTINCT o.CustomerID
      FROM Orders AS o
               INNER JOIN Order_Details AS od
                          ON o.OrderNumber = od.OrderNumber
               INNER JOIN Products AS p
                          ON p.ProductNumber = od.ProductNumber
      WHERE p.ProductName = 'Helmet') AS OHel
     ON c.CustomerID = OHel.CustomerID
         INNER JOIN
     (SELECT DISTINCT o.CustomerID
      FROM Orders AS o
               INNER JOIN Order_Details AS od
                          ON o.OrderNumber = od.OrderNumber
               INNER JOIN Products AS p
                          ON p.ProductNumber = od.ProductNumber
      WHERE p.ProductName = 'Knee Pads') AS OKn
     ON c.CustomerID = OKn.CustomerID
         INNER JOIN
     (SELECT DISTINCT o.CustomerID
      FROM Orders AS o
               INNER JOIN Order_Details AS od
                          ON o.OrderNumber = od.OrderNumber
               INNER JOIN Products AS p
                          ON p.ProductNumber = od.ProductNumber
      WHERE p.ProductName = 'Gloves') AS OGl
     ON c.CustomerID = OGl.CustomerID;
```
- 쿼리가 훨씬 복잡하지만, 올바른 결과를 반환한다.
  - FROM 절에 서브쿼리를 네 개 추가해 네 가지 제품을 모두 구매한 고객만 찾기 때문

```sql
CREATE FUNCTION CustProd(@ProdName varchar(50)) RETURNS Table AS
RETURN
(SELECT Orders.CustomerID AS CustID
    FROM Orders
    INNER JOIN Order_Details
    ON Orders.OrderNumber = Order_Details.OrderNumber
    INNER JOIN Products
    ON Products.ProductNumber = Order_Details.ProductNumber
    WHERE ProductName = @ProdName);

SELECT C.CustomerID, C.CustFirstName, C.CustLastName
FROM Customers AS C
WHERE C.CustomerID IN
      (SELECT CustID FROM CustProd('Skateboard'))
  AND C.CustomerID IN
      (SELECT CustID FROM CustProd('Helmet'))
  AND C.CustomerID IN
      (SELECT CustID FROM CustProd('Knee Pads'))
  AND C.CustomerID IN
      (SELECT CustID FROM CustProd('Gloves'));
```
- 서브쿼리와 Customers 테이블에 대해 WHERE 절을 추가해 IN 조건을 달 수도 있다.
- 함수를 이용하여 SQL 문이 간단해진다.

```sql
SELECT c.CustomerID, c.CustFirstName, c.CustLastName
FROM Customers AS c
WHERE EXISTS
    (SELECT o.CustomerID
     FROM Orders AS o
              INNER JOIN Order_Details AS od
                         ON o.OrderNumber = od.OrderNumber
              INNER JOIN Products AS p
                         ON p.ProductNumber = od.ProductNumber
     WHERE p.ProductName = 'Skateboard'
       AND o.CustomerID = C.CostomerID)
  AND EXISTS(SELECT o.CustomerID
             FROM Orders AS o
                      INNER JOIN Order_Details AS od
                                 ON o.OrderNumber = od.OrderNumber
                      INNER JOIN Products AS p
                                 ON p.ProductNumber = od.ProductNumber
             WHERE p.ProductName = 'Helmet'
               AND o.CustomerID = C.CostomerID)
  AND EXISTS(SELECT o.CustomerID
             FROM Orders AS o
                      INNER JOIN Order_Details AS od
                                 ON o.OrderNumber = od.OrderNumber
                      INNER JOIN Products AS p
                                 ON p.ProductNumber = od.ProductNumber
             WHERE p.ProductName = 'Knee Pads'
               AND o.CustomerID = C.CostomerID)
  AND EXISTS(SELECT o.CustomerID
             FROM Orders AS o
                      INNER JOIN Order_Details AS od
                                 ON o.OrderNumber = od.OrderNumber
                      INNER JOIN Products AS p
                                 ON p.ProductNumber = od.ProductNumber
             WHERE p.ProductName = 'Gloves'
               AND o.CustomerID = C.CostomerID);
```
- EXISTS 를 사용해 WHERE 절을 구성하는 방법이다.

### 응용
- 스케이트보드는 구매했지만 헬멧, 장갑, 무릎 보호대는 모두 구매하지 않은 고객을 찾아보자.
  - 번역의 문제인지 아마도 보호장비 중 일부가 누락된 경우를 찾는 경우를 말하고 싶었던 것 같다.

```sql
SELECT c.CustomerID, c.CustFirstName, c.CustLastName
FROM Customers AS c
WHERE c.CustomerID IN
      (SELECT o.CustomerID
       FROM Orders AS o
                INNER JOIN Order_Details AS od
                           ON o.OrderNumber = od.OrderNumber
                INNER JOIN Products AS p
                           ON p.ProductNumber = od.ProductNumber
       WHERE p.ProductName = 'Skateboard')
  AND c.CustomerID NOT IN
      (SELECT o.CustomerID
       FROM Orders AS o
                INNER JOIN Order_Details AS od
                           ON o.OrderNumber = od.OrderNumber
                INNER JOIN Products AS p
                           ON p.ProductNumber = od.ProductNumber
       WHERE p.ProductName IN ('Helmet', 'Gloves', 'Knee Pads'));
```
- 이는 원하는대로 동작하지 않는다.
- 이 쿼리는 헬멧, 장갑, 무릎 보호대 중 하나라도 구매한 고객은 결과에 나오지 않는다.

```sql
SELECT C.CustomerID, C.CustFirstName, C.CustLastName
FROM Customers AS C
WHERE C.CustomerID IN
      (SELECT CustID FROM CustProd('Skateboard'))
  AND (C.CustomerID NOT IN
       (SELECT CustID FROM CustProd('Helmet'))
    OR C.CustomerID NOT IN
       (SELECT CustID FROM CustProd('Knee Pads'))
    OR C.CustomerID NOT IN
       (SELECT CustID FROM CustProd('Gloves')));
```
- 원하는대로 동작하는 올바른 쿼리이다.
- 이전에 만든 함수를 이용하였다.
- WHERE 절의 첫 번째 조건은 스케이트보드를 구매한 고객을 찾는 것이다.
- 나머지 조건은 헬멧 또는 장갑 또는 무릎 보호대를 구매하지 않은 고객을 찾는 것이다.
- 필요한 경우 AND, 가능성을 포함하는 경우는 OR 을 사용했다.

## 정리
- 관련된 테이블을 사용해 여러 조건을 검사해야 하는 문제를 올바르게 해결하는 방법은 간단하거나 직관적이지 않다.
- 하나 이상의 관련된 지식 테이블에 한 개 이상의 조건을 적용해 부모 테이블에 있는 로우를 추출하려면 서브쿼리에 대한 NULL 검사(좌절성 조인), IN 과 AND 또는 NOT IN 과 OR 을 포함한 INNER JOIN 이나 OUTER JOIN 을 사용해야 올바른 결과를 얻을 수 있다.
