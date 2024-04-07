## 공통 테이블 표현식
- BetterWay25 에서 다중 조건 문제를 해결하기 위해 복잡한 조인이나 매개변수 값에 따른 조건을 처리하는 함수를 생성하여 해결하였었다.
- 하지만 함수를 사용하게 되면 최종 SQL 문에서 이 함수가 어떤 기능을 수행하는지 알 수 없게 된다.
- 함수를 수정하였을때 해당 함수를 사용하는 쿼리가 작동하지 않을 수도 있다.
- 이러한 문제를 해결하는 더 좋은 방법이 공통 테이블 표현식이다.

> Access 2016, MySQL 5.7 은 공통 테이블 표현식을 지원하지 않는다.

![판매_주문_데이터베이스_설계](https://github.com/Evil-Goblin/BookStudy/assets/74400861/47cea281-a29a-4fc2-9578-5b1d42045089)
- 예제에 사용될 데이터베이스 설계이다.

### CTE 로 쿼리를 단순하게 만들기 
```tsql
SELECT C.CustomerID, C.CustFirstName, C.CustLastName
FROM Customers AS C INNER JOIN
   (SELECT DISTINCT Orders.CustomerID
    FROM Orders INNER JOIN Order_Details
      ON Orders.OrderNumber = Order_Details.OrderNumber
    INNER JOIN Products 
      ON Products.ProductNumber = Order_Details.ProductNumber
    WHERE Products.ProductName = 'Skateboard') AS OSk
  ON C.CustomerID = OSk.CustomerID
INNER JOIN
   (SELECT DISTINCT Orders.CustomerID
    FROM Orders INNER JOIN Order_Details
      ON Orders.OrderNumber = Order_Details.OrderNumber
    INNER JOIN Products 
      ON Products.ProductNumber = Order_Details.ProductNumber
    WHERE Products.ProductName = 'Helmet') AS OHel
  ON C.CustomerID = OHel.CustomerID
INNER JOIN
   (SELECT DISTINCT Orders.CustomerID
    FROM Orders INNER JOIN Order_Details
      ON Orders.OrderNumber = Order_Details.OrderNumber
    INNER JOIN Products 
      ON Products.ProductNumber = Order_Details.ProductNumber
    WHERE Products.ProductName = 'Knee Pads') AS OKn
  ON C.CustomerID = OKn.CustomerID
INNER JOIN
   (SELECT DISTINCT Orders.CustomerID
    FROM Orders INNER JOIN Order_Details
      ON Orders.OrderNumber = Order_Details.OrderNumber
    INNER JOIN Products 
      ON Products.ProductNumber = Order_Details.ProductNumber
    WHERE Products.ProductName = 'Gloves') AS OGl
  ON C.CustomerID = OGl.CustomerID;
```
- 네 가지 제품을 모두 구매한 고객을 찾는 쿼리이다.
- 서브쿼리가 네 개나 존재하기 때문에 전체 쿼리를 읽고 이해하기가 어렵다.
- 이 서브쿼리들의 유일한 차이점은 ProductName 컬럼 값을 선택하는 부분이다.
  - 쿼리에서 ProductName 컬럼을 CTE 로 빼면, CTE 를 참조해 필요한 조건을 적용할 수 있다.

```tsql
WITH CustProd AS 
   (SELECT Orders.CustomerID, Products.ProductName
    FROM Orders INNER JOIN Order_Details
      ON Orders.OrderNumber = Order_Details.OrderNumber
    INNER JOIN Products 
      ON Products.ProductNumber = Order_Details.ProductNumber)
, SkateboardOrders AS 
    (SELECT DISTINCT CustomerID
    FROM CustProd
    WHERE ProductName = 'Skateboard')
, HelmetOrders AS 
    (SELECT DISTINCT CustomerID
    FROM CustProd
    WHERE ProductName = 'Helmet')
, KneepadsOrders AS 
    (SELECT DISTINCT CustomerID
    FROM CustProd
    WHERE ProductName = 'Knee Pads')
, GlovesOrders AS 
    (SELECT DISTINCT CustomerID
    FROM CustProd
    WHERE ProductName = 'Gloves')
SELECT C.CustomerID, C.CustFirstName, C.CustLastName
FROM Customers AS C INNER JOIN
  SkateboardOrders  AS OSk
  ON C.CustomerID = OSk.CustomerID
INNER JOIN
  HelmetOrders AS OHel
  ON C.CustomerID = OHel.CustomerID
INNER JOIN
  KneepadsOrders AS OKn
  ON C.CustomerID = OKn.CustomerID
INNER JOIN
  GlovesOrders AS OGl
  ON C.CustomerID = OGl.CustomerID;
```
- CTE 를 사용하면 코드양이 적고 간략해진다.
  - CTE 를 여러 개 만들어 한 CTE 내에서 다른 CTE 를 참조할 수 있다.
- CTE 의 가장 큰 장점은 일반적으로 사용하는 중첩된 서브쿼리 대신 맨 위에서 맨 아래까지 순차적으로 서브쿼리를 읽는 방식으로 복잡한 쿼리를 작성할 수 있다는 것이다.
- 뷰를 여러 개 만들고 이들을 조인하여 문제를 해결하는 방법도 생각해 볼 수 있지만, 이는 관리가 어렵다는 단점이 있다.
  - 각 뷰의 정의 내용을 조사해 최종 쿼리에서 합한 후 사용해야 한다.
    - 직접적으로 유용하지 않은 여러 뷰가 양산되는 문제를 처리해야하기 때문

### 재귀 CTE 사용하기
- CTE 는 재귀 작업이 가능하다.
- CTE 는 추가적인 로우를 생성하려고 자기 자신을 호출한다.
- 재귀 CTE 를 만들 때는 몇 가지 제약이 있다.
  - SQL Server 는 DISTINCT, GROUP BY, HAVING, 스칼라 집계, 서브쿼리, LEFT JOIN, OUTER JOIN 을 할 수 없다.(INNER 조인은 허용된다.)
- 재귀 CTE 를 생성할 때는 `WITH RECURSIVE` 키워드를 사용하라고 명시했지만, PostgreSQL 에서만 이 키워드가 필요하다.

```tsql
WITH SeqNumTbl AS 
  (SELECT 1 AS SeqNum
   UNION ALL
   SELECT SeqNum + 1
   FROM SeqNumTbl
   WHERE SeqNum < 100)
SELECT SeqNum 
FROM SeqNumTbl;
```
- 숫자 1~100 을 생성하는 쿼리이다.
  - 이 쿼리는 SQL Server 기준으로 작성된 쿼리라서 RECURSIVE 키워드가 사용되지 않는다.
- UNION 쿼리의 두 번째 SELECT 문에서 SeqNumTbl CTE 를 다시 호출해 이전 생성한 마지막 숫자에 1을 더하고, 그 수가 100이 되면 멈춘다.
  - 저장된 테이블을 사용하는 방법이 있는데 저장된 테이블을 사용하면 수행 속도가 빨라진다.
    - 저장된 테이블의 값에 인덱스를 만들 수 있기 때문
    - CTE 가 생성한 컬럼에는 인덱스를 만들 수 없다.
  - 이 경우는 저장된 탤리 테이블 대신 CTE 를 사용하였다.

![샘플](https://github.com/Evil-Goblin/BookStudy/assets/74400861/84d21950-2cc2-4596-98a6-c40f86a8e48f)
- 재귀 CTE 로 자기 참조 테이블을 사용해 계층 정보를 만들 수 있다.
- 위는 예제에 사용될 샘플 데이터이다.

```tsql
WITH MgrEmps 
   (ManagerID, ManagerName, EmployeeID, EmployeeName, 
      EmployeeLevel) AS 
  (SELECT ManagerID, CAST(' ' AS varchar(50)), EmployeeID, 
     CAST(CONCAT(EmpFirstName, ' ', EmpLastName) 
            AS varchar(50)), 0 AS EmployeeLevel
   FROM Employees
   WHERE ManagerID IS NULL
   UNION ALL
   SELECT e.ManagerID, d.EmployeeName, e.EmployeeID, 
     CAST(CONCAT(e.EmpFirstName, ' ', e.EmpLastName) 
        AS varchar(50)), EmployeeLevel + 1
   FROM Employees AS e
   INNER JOIN MgrEmps AS d
     ON e.ManagerID = d.EmployeeID )
SELECT ManagerID, ManagerName, EmployeeID, EmployeeName, 
    EmployeeLevel
FROM MgrEmps
ORDER BY ManagerID;
```
- 첫 번째 쿼리에서는 재귀의 시작점인 루트 로우를 가져오려고 ManagerID 값이 없는 직원을 찾는다.
- UNION 이 동작하도록 CAST 함수를 사용해 모든 이름 컬럼의 데이터 타입을 맞춘다.
- 원본 Employees 테이블이 있는 두 번째 쿼리에서는 직원과 해당 직원의 관리자를 찾는다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/c1e5c165-50d6-40c2-a9e0-16e2f9f43cd0)
- 재귀 CTE 를 사용해 뽑은 관리자와 직원 목록이다.

## 정리
- 공통 테이블 표현식(CTE)으로 동일한 서브쿼리를 한 번 이상 사용하는 복잡한 쿼리를 간단한게 만들 수 있다.
- 실수로 함수를 변경하면 쿼리가 제대로 작동하지 않을 수 있다.
  - CTE 를 사용하면 이런 상황에서 해방될 수 있다.
- CTE 를 사용해 정의한 서브쿼리는 동일한 SQL 내 다른 쿼리에서도 참조가 가능하다.
  - 결과적으로 전체 쿼리를 더 쉽게 이해할 수 있다.
- 저장된 탤리 테이블에서 다른 방식으로 찾을 수 있는 값을 생성하는 데 재귀 CTE 를 사용할 수는 있다.
  - 하지만 지정된 탤리 테이블은 컬럼에 인덱스를 만들 수 있으므로 더 효율적이다.
- 재귀 CTE 로 계층 관계를 탐색해서 의미 있는 방식으로 계층형 정보를 보여 줄 수 있다.
