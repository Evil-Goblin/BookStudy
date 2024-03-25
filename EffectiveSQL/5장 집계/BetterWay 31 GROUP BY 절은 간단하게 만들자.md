## 기능적 의존성
- SQL-92 표준 까지는 집계 연산을 수행하지 않는 모든 컬럼은 반드시 GROUP BY 절에 기술해야 했다.

```sql
SELECT c.CustomerID,
       c.CustFirstName,
       c.CustLastName,
       c.CustState,
       MAX(o.OrderDate)     AS LastOrderDate,
       COUNT(o.OrderNumber) AS OrderCount,
       SUM(o.OrderTotal)    AS TotalAmount
FROM Customers AS c
         LEFT JOIN Orders AS o
                   ON c.CustomerID = o.CustomerID
GROUP BY c.CustomerID, c.CustFirstName, c.CustLastName, c.CustState;
```
- SQL-92 표준을 따르는 GROUP BY 절에 컬럼을 여러 개 둔 집계 쿼리이다.
- Customers 테이블의 기본키인 CustomerID 별로 집계한다고 하자.
  - 기본키는 유일해야 하기 떄문에 다른 세 컬럼의 값이 무엇인지는 중요하지 않다.
- 이것을 **기능적 의존성**이라고 한다.
  - CustFirstName, CustLastName, CustState 컬럼은 기능적으로 CustomerID 에 의존한다.

```sql
SELECT c.CustomerID,
       c.CustFirstName,
       c.CustLastName,
       c.CustState,
       MAX(o.OrderDate)     AS LastOrderDate,
       COUNT(o.OrderNumber) AS OrderCount,
       SUM(o.OrderTotal)    AS TotalAmount
FROM Customers AS c
         LEFT JOIN Orders AS o
                   ON c.CustomerID = o.CustomerID
GROUP BY c.CustomerID;
```
- 현재의 SQL 표준을 따르도록 수정한 쿼리이다.
- 하지만 이 쿼리는 MySQL 과 PostgreSQL 에서만 작동한다.

```sql
SELECT c.CustomerID,
       c.CustFirstName,
       c.CustLastName,
       c.CustState,
       o.LastOrderDate,
       o.OrderCount,
       o.TotalAmount
FROM Customers AS c
         LEFT JOIN (SELECT t.CustomerID,
                           MAX(t.OrderDate)     AS LastOrderDate,
                           COUNT(t.OrderNumber) AS OrderCount,
                           SUM(t.OrderTotal)    AS TotalAmount
                    FROM Orders AS t
                    GROUP BY t.CustomerID) AS o
                   ON c.CustomerID = o.CustomerID;
```
- 서브쿼리로 동일한 쿼리를 GROUP BY 절에 기술하는 컬럼의 개수를 최소화한 쿼리이다.
- 이 쿼리는 실제로 집계되는 컬럼이 무엇인지 쉽게 파악할 수 있다는 큰 장점이 있다.

```sql
...
GROUP BY CustCity, CustSate, CustZip, YEAR(OrderDate), MONTH(OrderDate), EmployeeID
...
```
- 항상 기본키를 그루핑해서 집계하지 않는다.
- 위는 기본키로 그루핑 하지 않는 복잡한 형태의 GROUP BY 절이다.
- 이 GROUP BY 절에 빼도 되는 기능적 의존성 컬럼을 찾기 어렵다.
  - 그루핑하는데 필수적인 정보를 결정하려면 전체 쿼리를 분석하고 결과 집합을 연구해야 한다.
  - 이 쿼리의 최종 결과는 세부 정보를 담은 컬럼이 많아 원래 목적이 흐려졌기에 분석하고 이해하기 어렵다.
- 주요 테이블에 적용하는 기준을 결정하거나 최적화하려면 쿼리를 다시 작성해야 한다.
- 이러한 이유로 집계 쿼리는 데이터를 집계할 때 실제로 필요한 컬럼만 GROUP BY 절에 기술하는 방식으로 작성해야 좋다.
  - 세부적인 정보를 얻는 데 컬럼이 더 필요하다면, 이들을 GROUP BY 절에 추가하기보다 별도의 서브쿼리로 빼는 것이 좋다.

## 정리
- 현재 SQL 표준에서 더는 요구하지 않더라도, 몇몇 DBMS 에서는 집계되지 않는 컬럼을 GROUP BY 절에 추가해야 한다.
- GROUP BY 절에 컬럼을 과도하게 기술하면 쿼리의 성능에 악영향을 미칠 뿐만 아니라, 읽고 이해하고 재작성하기가 어렵다.
- 집계와 세부 정보 조회 두 가지 목적을 달성해야 하는 쿼리를 작성할 때는 먼저 서브쿼리에서 모든 집계를 수행한 후 세부 데이터를 담은 테이블과 조인해 해당 정보를 가져온다.
