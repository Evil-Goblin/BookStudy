## 관계 대수
- 관계(테이블이나 뷰), 튜플(로우), 속성(컬럼) 들과 같은 모델로 수행할 수 있는 일련의 연산
- 종류
  - 선택(제한이라고도 한다.)
  - 추출
  - 조인
  - 교집합
  - 카티전 곱
  - 합집합
  - 나누기
  - 차집합

### 선택(제한)
- 선택은 로우를 선택한 후 필터링해서 원하는 데이터 집합을 얻는 것이다.
- FROM 절에서 원하는 데이터 집합의 원천이 무엇인지 정의한 후 WHERE 이나 HAVING 절을 사용해 반환되는 로우를 걸러내는 작업이다.

![선택_연산_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/e4513bab-8f43-437d-bef5-fcddccc3e1f1)
- 색칠된 부분이 선택 작업 결과로 반환된 로우이다.

### 추출
- 추출은 데이터베이스 시스템이 반환하는 컬럼과 표현식을 선택하는 연산을 의미한다.
- SQL 에서는 데이터베이스 시스템이 반환하는 컬럼을 정의하려고 집계함수, GROUP BY 절을 포함한 SELECT 절로 구현한다.

![추출_연산_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/8297359a-cea5-4ca4-a923-a080ce82a468)
- 색칠된 부분이 추출 연산의 결과로 반환되는 컬럼이다.

### 조인
- 조인은 키 값으로 연결된 데이터 집합이나 관련된 테이블을 연결하는 것이다.
- 모든 관계(테이블)는 반드시 유일한 식별자(기본키)를 가져야 하며, 관계가 형성된 또 다른 테이블은 이런 유일한 식별자의 복사본(외래키)을 가져야 한다.(관계형 모델의 핵심 요소)

![INNERJOIN_OUTERJOIN](https://github.com/Evil-Goblin/BookStudy/assets/74400861/d9572945-4d62-49de-b5bc-21aafbf17a0b)
- INNER JOIN 과 OUTER JOIN 의 결과이다.
- INNER JOIN 은 두 테이블에서 일치하는 로우만 포함되었다.
- OUTER JOIN 은 1번 테이블의 모든 로우와 이와 일치하는 2번 테이블의 로우가 포함되었다.
  - 일치하지 않는 경우 NULL 값이 반환되었다.

> NATURAL JOIN 은 INNER JOIN 과 비슷하다.  
> 하지만 NATURAL JOIN 은 두 테이블에서 이름이 동일한 컬럼을 조인을 수행해 일치하는 로우만 반환하며 ON 절은 명시하지 않는다는 점이 다르다.  
> MySQL , Oracle , PostgreSQL 에서만 NATURAL JOIN 을 지원한다.

### 교집합
- 교집합은 컬럼이 동일한 두 집합에서 수행된다.
- 교집합의 결과는 이런 각 컬럼 값과 일치하는 모든 로우를 반환한다.
- DB2, SQL Server, Oracle, PostgreSQL 에서 교집합 기능을 지원한다.
- 교집합 기능을 지원하는 DBMS 는 한 데이터 집합에서 선택과 추출 작업을 수행한 후 첫 번째 데이터 집합과 두 번째 집합에서 `INTERSECT` 를 명시하면 된다.
- 교집합 기능을 지원하지 않는다면(Access, MySQL) 두 데이터 집합에 있는 모든 컬럼에 INNER JOIN 을 수행해 동일한 결과를 얻을 수 있다.

```postgresql
SELECT c.CustFirstName, c.CustLastName
FROM Customers AS c
WHERE c.CustomerID IN
      (SELECT o.CustomerID
       FROM Orders AS o
                INNER JOIN Order_Details AS od
                           ON o.OrderNumber = od.OrderNumber
                INNER JOIN Products AS p
                           ON p.ProductNumber = od.ProductNumber
       WHERE p.ProductName = 'Bike')
INTERSECT
SELECT c2.CustFirstName, c2.CustLastName
FROM Customers AS c2
WHERE c2.CustomerID IN
      (SELECT o.CustomerID
       FROM Orders AS o
                INNER JOIN Order_Details AS od
                           ON o.OrderNumber = od.OrderNumber
                INNER JOIN Products AS p
                           ON p.ProductNumber = od.ProductNumber
       WHERE p.ProductName = 'Skateboard');
```
- INTERSECT 를 사용해 Bike 와 Skateboard 를 모두 구매한 고객을 추출하는 쿼리이다.

```sql
SELECT c.CustFirstName, c.CustLastName
FROM (SELECT DISTINCT c.CustFirstName, c.CustLastName
      FROM Customers AS c
               INNER JOIN Orders AS o
                          ON c.CustomerID = o.CustomerId
               INNER JOIN Order_Details AS od
                          ON o.OrderNumber = od.OrderNumber
               INNER JOIN Products AS p
                          ON p.ProductNumber = od.ProductNumber
      WHERE p.ProductName = 'Bike') AS c
         INNER JOIN
     (SELECT DISTINCT c.CustFirstName, c.CustLastName
      FROM Customers AS c
               INNER JOIN Orders AS o
                          ON c.CustomerID = o.CustomerId
               INNER JOIN Order_Details AS od
                          ON o.OrderNumber = od.OrderNumber
               INNER JOIN Products AS p
                          ON p.ProductNumber = od.ProductNumber
      WHERE p.ProductName = 'Skateboard') AS c2
     ON c.CustFirstName = c2.CustFirstName
         AND c.CustLastName = c2.CustLastName;
```
- INNER JOIN 을 사용하여 교집합을 구현한 쿼리이다.
- INTERSECT 를 사용할 때 데이터베이스 시스템은 중복 로우를 제거한다.
  - DB2 와 PostgreSQL 은 중복을 포함해 모든 로우를 반환하는 INTERSECT ALL 을 지원한다.

### 카티전 곱
- 카티전 곱은 한 데이터 집합에 있는 모든 로우와 두 번째 데이터 집합에 있는 모든 로우를 결합한 결과를 반환한다.
- 반환 결과의 로우 개수가 첫 번째 집합의 전체 로우 개수와 두 번째 집합의 전체 로우 개수를 곱한 것과 같아 '곱'이라고 한다.
- 카티전 곱을 수행하려면 JOIN 절 없이 FROM 절에 해당 테이블만 명시한다.
- 주요 DBMS 는 카티전 곱을 지원한다.
  - 그중 일부는 CROSS JOIN 으로 명시해야 한다.

### 합집합
- 합집합 연산은 컬럼의 유형이 동일한 두 데이터 집합을 병합하는 것으로 SQL 에서는 UNION 키워드로 구현되어 있다.
- 교집합과 유사하게 한 데이터 집합에서 선택과 추출을 한 후 UNION 키워드를 추가하고 두 번째 집합에서 선택과 추출을 한다.
- UNION ALL 은 중복 로우를 제거하지 않기 때문에 중복된 로우를 찾아낼 수 있다.

### 나누기
- 데이터베이스 시스템에서 한 데이터 집합을 다른 집합으로 나누면 제수 데이터 집합의 모든 멤버를 포함하는 피제수 데이터 집합에 있는 로우가 모두 반환된다.
- 나누기 연산을 SQL 로 구현한 상용 DBMS 는 없지만 표준 SQL 을 사용해 나누기 연산과 동등한 결과를 얻을 수 있다.(BetterWay 26 에서 소개)

### 차집합
- 차집합 연산은 기본적으로 한 집합에서 다른 집합을 빼는 것이다.
- 합집합, 교집합처럼 차집합도 컬럼이 동일하거나 유사한 두 집합을 대상으로 작업해야 한다.
- DB2, SQL Server, PostgreSQL 모두 EXCEPT 키워드로 차집합 연산을 지원한다.
  - DB2 는 중복 로우를 제거하지 않는 EXCEPT ALL 도 지원한다.
- Oracle 은 MINUS 키워드를 사용한다.
- MySQL, Access 는 차집합 기능을 직접 지원하지 않아 OUTER JOIN 으로 유사하게 구현한다.
  - 이때 차감하는 집합에서는 NULL 값이 나올 것이다.

```postgresql
SELECT c.CustFirstName, c.CustLastName
FROM Customers AS c
WHERE c.CustomerID IN
      (SELECT o.CustomerID
       FROM Orders AS o
                INNER JOIN Order_Details AS od
                           ON o.OrderNumber = od.OrderNumber
                INNER JOIN Products AS p
                           ON p.ProductNumber = od.ProductNumber
       WHERE p.ProductName = 'Skateboard')
EXCEPT
SELECT c2.CustFirstName, c2.CustLastName
FROM Customers AS c2
WHERE c2.CustomerID IN
      (SELECT o.CustomerID
       FROM Orders AS o
                INNER JOIN Order_Details AS od
                           ON o.OrderNumber = od.OrderNumber
                INNER JOIN Products AS p
                           ON p.ProductNumber = od.ProductNumber
       WHERE p.ProductName = 'Helmet');
```
- 차집합 연산을 통해 스케이트보드만 주문하고 헬멧은 주문하지 않은 고객을 찾는 쿼리이다.

## 정리
- 관계형 모델은 집합에서 수행할 수 있는 여덟 가지 연산을 정의한다.
- 주요 DBMS 는 선택, 추출, 조인, 카티전 곱, 합집합 기능을 지원한다.
- 일부 DBMS 는 INTERSECT 와 EXCEPT 또는 MINUS 키워드를 사용해 교집합과 차집합 기능을 지원한다.
- 나누기 연산은 직접적으로 구현하지 않지만, SQL 의 다른 기능을 사용해 동일한 결과를 얻을 수 있다.
