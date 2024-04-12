## 카티전 곱
- 가끔 어느 레코드는 처리하고 어느 레코드는 처리하지 않는지 결정하려고 가능한 모든 조합의 목록을 생성할 때가 있다.
- 고객별로 어떤 제품은 주문하고 어떤 제품은 주문하지 않는지 알고 싶다고 할 때, 문제를 해결하는 방법은 다음과 같다.
  - 고객과 제품 간에 가능한 모든 조합의 목록을 만든다.
  - 각 고객별로 모든 구매 내역을 목록으로 만든다.
  - 가능한 모든 조합과 실제 구매 내역을 LEFT 조인해서 실제 구매 내역에 표시한다.
- 고객이 구매한 목록만으로는 구매하지 않은 목록을 결정하기 어렵다.
  - 가능한 모든 구매목록이 필요하다.(카티전 곱)
  - 두 결과 집합을 LEFT JOIN 해서 '오른쪽'(왼쪽 테이블은 카티전 곱, 오른쪽 테이블은 실제 구매 내역)에 있는 NULL 값을 찾으면 구매하지 않은 제품을 식별할 수 있다.

```sql
SELECT C.CustomerID, C.CustFirstName, C.CustLastName,
  P.ProductNumber, P.ProductName, P.ProductDescription
FROM Customers AS C, Products AS P;
```
> 모든 DBMS 에서는 JOIN 절 없이 FROM 절에 테이블을 나열할 수 있지만, 몇몇 DBMS 에서는 이 FROM 절을 FROM Customer AS c CROSS JOIN Products AS p 로 변경해야 할 것이다.

- 모든 고객 및 제품 목록을 얻는 카티전 곱 쿼리이다.
- 이를 통해 Customers 와 Products 테이블 간의 모든 조합 목록을 만들 수 있다.

```sql
SELECT O.OrderNumber, O.CustomerID, OD.ProductNumber 
FROM Orders AS O INNER JOIN Order_Details AS OD
  ON O.OrderNumber = OD.OrderNumber;
```
- Orders 와 Order_Details 테이블을 조인하면 각 고객이 구매한 제품 목록을 만들 수 있다.

```sql
SELECT CustProd.CustomerID, CustProd.CustFirstName, CustProd.CustLastName,
  CustProd.ProductNumber, CustProd.ProductName, 
  (CASE WHEN OrdDet.OrderCount > 0 
    THEN 'You purchased this!'
    ELSE ' ' 
  END) AS ProductOrdered
FROM
(SELECT C.CustomerID, C.CustFirstName, C.CustLastName,
  P.ProductNumber, P.ProductName, P.ProductDescription
FROM Customers AS C, Products AS P) AS CustProd
LEFT JOIN
(SELECT O.CustomerID, OD.ProductNumber, Count(*) AS OrderCount
FROM Orders AS O INNER JOIN Order_Details AS OD
  ON O.OrderNumber = OD.OrderNumber
GROUP BY O.CustomerID, OD.ProductNumber) AS OrdDet
  ON CustProd.CustomerID = OrdDet.CustomerID
  AND CustProd.ProductNumber = OrdDet.ProductNumber
ORDER BY CustProd.CustomerID, CustProd.ProductName;
```
- 이전 두 쿼리를 기반으로 LEFT JOIN 을 사용하면 카티전 곱이 반환한 결과에서 구매한 로우와 구매하지 않은 로우가 무엇인지 결정할 수 있다.

```sql
SELECT C.CustomerID, C.CustFirstName, C.CustLastName,
  P.ProductNumber, P.ProductName,
  (CASE WHEN C.CustomerID IN
    (SELECT Orders.CustomerID
     FROM Orders INNER JOIN Order_Details
       ON Orders.OrderNumber = Order_Details.OrderNumber
     WHERE Order_Details.ProductNumber = P.ProductNumber)
     THEN 'You purchased this!'
     ELSE ' ' 
  END) AS ProductOrdered
FROM Customers AS C, Products AS P
ORDER BY C.CustomerID, P.ProductName;
```
- LEFT JOIN 대신 IN 을 사용해 각 고객이 구매한 제품을 찾을 수도 있다.
- 쿼리 성능은 데이터양과 인덱스, DBMS 종류에 따라 다르기 때문에 어느 방법이 낫다고 할 수 없다.

![조회_결과_01](https://github.com/Evil-Goblin/BookStudy/assets/74400861/67cff61e-026a-482d-bca5-b0ccbf0de4a6)
![조회_결과_02](https://github.com/Evil-Goblin/BookStudy/assets/74400861/0dabd033-8386-4d28-b027-e5e1b02ca231)
- 두 쿼리의 실행 결과이다.
  - 모든 고객과 제품 목록, 고객이 구매한 제품을 표시한 결과 일부

## 정리
- 두 테이블 간에 레코드의 가능한 조합을 모두 만들려면 카티전 곱을 사용한다.
- 실제로 발생한 조합을 식별하려면 INNER JOIN 을 사용한다.
- 카티전 곱의 결과와 실제 발생한 조합 목록을 비교하려면 LEFT JOIN 사용을 고려한다.
- 카티전 곱과 LEFT JOIN 을 사용한 것과 같은 결과를 얻으려고 SELECT 절 CASE 문 내에서 IN 서브쿼리를 사용할 수 있지만, 쿼리 성능은 데이터양과 인덱스, 사용하는 DBMS 의 종류에 따라 다른다.
