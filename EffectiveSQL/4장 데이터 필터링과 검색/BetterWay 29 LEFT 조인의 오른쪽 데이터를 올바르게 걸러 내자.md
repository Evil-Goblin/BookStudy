## 차집합
주문을 한 건도 하지 않은 고객을 조회하려고 한다.
- 차집합 연산을 수행하면 된다.
  - OUTER JOIN 과 IS NULL 을 조합해 사용하면 된다.
- 주문 고객 집합에 속하지 않는 고객을 찾으려면 전체 고객 집합에서 주문 고객 집합을 빼면 된다.
- 더 큰 데이터 집합(기본적으로 'LEFT' 조인의 '오른쪽' 에 있는 집합이나 테이블, 또는 그 반대) 에서 차감하는 결과 집합에 조건을 달 때는 실수가 발생하기 쉽다.

```sql
SELECT c.CustomerID, c.CustFirstName, c.CustLastName, o.OrderNumber, o.OrderDate, o.OrderTotal
FROM Customers AS c
         LEFT JOIN Orders AS o
                   ON c.CustomerID = o.CustomerID
WHERE o.OrderDate BETWEEN CAST('2015-10-01' AS DATE)
          AND CAST('2015-12-31' AS DATE);
```
- 모든 고객과 2015년 4/4분기 동안 주문한 고객의 정보를 조회하는 쿼리이다.
- 하지만 이 쿼리를 실행하면 모든 로우에 있는 주문 데이터가 조회되지만, 고객 일부가 빠져있게 된다.
  - 고객 중 주문을 하지 않은 고객은 조회되지 않는다.
- INNER JOIN 과 결과가 동일하다.

```sql
SELECT c.CustomerID, c.CustFirstName, c.CustLastName, o.OrderNumber, o.OrderDate, o.OrderTotal
FROM Customers AS c
         LEFT JOIN Orders AS o
                   ON c.CustomerID = o.CustomerID
WHERE (o.OrderDate BETWEEN CAST('2015-10-01' AS DATE)
    AND CAST('2015-12-31' AS DATE))
   OR o.OrderNumber IS NULL;
```
- 빠진 정보를 검색하기 위해 NULL 검사 조건을 달아야 한다.
- 하지만 여전히 모든 고객 로우를 보여주지는 않는다.
- 데이터베이스 엔진은 먼저 FROM 절을 처리한 후 WHERE 절을 적용하고, 마지막으로 SELECT 절에 명시한 컬럼을 반환한다.
  - `Customers LEFT JOIN Orders` 는 모든 고객 로우와 Orders 테이블에서 일치하는 로우를 반환한다.
  - WHERE 절이 적용되면 주문하지 않은 고객은 제거된다.
  - 이런 고객은 Orders 테이블에서 가져온 컬럼 값이 NULL 이기 때문이다.
  - NULL 은 어떤 값과도 비교할 수 없으므로 날짜 범위로 데이터를 걸러 내어 이런 로우를 제거한다.
- 이 쿼리는 주문하지 않은 모든 고객은 반환이 되지만 날짜 조건에서 제거되는 고객 정보가 있기 떄문에 원하던 결과가 아니게 된다.

```sql
SELECT c.CustomerID, c.CustFirstName, c.CustLastName, OFil.OrderNumber, OFil.OrderDate, OFil.OrderTotal
FROM Customers AS c
       LEFT JOIN
     (SELECT o.OrderNumber, o.CustomerID, o.OrderDate, o.OrderTotal
      FROM Orders AS o
      WHERE o.OrderDate BETWEEN CAST('2015-10-01' AS DATE)
              AND CAST('2015-12-31' AS DATE)) AS OFil
     ON c.CustomerID = OFil.CustomerID;
```
- 올바른 결과를 만들기 위해 차감하려는 집합과 조인하기 전 차감 집합에 대해 조건 검색을 한다.
- FROM 절에서 SELECT 문을 사용해(SQL 표준에서는 이를 파생 테이블이라고 한다.) 조건 검색한 집합을 미리 계산한다.
- 두 날짜 사이의 주문 집합을 먼저 가져온 후 Customers 테이블과 조인한다.

## 정리
- SQL 에서 차감 연산을 할 때는 OUTER JOIN 을 사용한다.
- LEFT 조인의 오른쪽에 대해 (또는 그 반대로) 밖에 있는 WHERE 절에서 필터 조건을 적용하면 원하는 결과를 얻지 못한다.
- 걸러 낸 부분 집합을 제대로 차감하려면, 데이터베이스 시스템이 외부 조인을 수행하기 전에 데이터를 걸러 내야 한다.
