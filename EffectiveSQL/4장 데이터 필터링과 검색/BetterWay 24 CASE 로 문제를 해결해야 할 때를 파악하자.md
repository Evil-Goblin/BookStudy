## CASE
- 어떤 값이나 표현식이 정확한 결과를 산출하는지 검사해야 할 때 사용한다.
- CASE 는 IF...THEN...ELSE 문의 SQL 버전이다.
- 컬럼을 반환하는 SELECT 절 , 검색 조건이 있는 WHERE, HAVING 절 등 값 표현식을 사용하는 곳에서 사용할 수 있다.

### 용어 정리
- 값 표현식(Value Expression)
  - 리터럴, 컬럼 참조, 함수 호출, CASE 표현식, 스칼라 값을 반환하는 서브쿼리를 의미한다.
  - 값 표현식은 데이터 타입에 따라 +, -, *, /, || 같은 연산자와 결합될 수 있다.  
- 조회 조건(Search Condition)
  - 서술 논리 연산 앞에 NOT 과 AND 나 OR 을 붙인다.  
- 서술 논리 연산(Predicate)
  - TRUE , FALSE 를 반환하는 조건을 의미한다.
  - 서술 논리 연산은 비교, 범위, 집합 멤버, 패턴 매칭, NULL, 정량화, 존재 판별 등에 사용된다.
    - 비교는 =, <>, <, >, <=, >= 를 사용해 값 표현식 둘을 비교하는 것을 의미한다.
    - 범위는 하나의 값 표현식과 또 다른 값 표현식 사이 또는 그 사이가 아님을 확인하는 값 표현식이다.
    - 집합 멤버는 서브쿼리나 값 표현식 목록이 반환하는 목록 또는 그 목록이 아닌 값 표현식이다.
    - 패턴 매칭은 패턴 문자열과 같거나 패턴 문자열과 같지 않은 값 표현식이다.
    - NULL 은 NULL 키워드를 사용한(선택적으로 NOT) 값 표현식이다.
    - 정량화는 뒤에 비교 연산자, ALL, SOME, ANY 키워드, 서브쿼리가 오는 값 표현식이다.
    - 존재 판별은 다른 쿼리에서 반환한 값을 걸러 내는 서브쿼리에 EXISTS 키워드를 사용한 것이다.

### 단순형, 검색형
- CASE 문은 단순형과 검색형 두 가지 형태로 사용된다.

```sql
-- (코드를 단어로 치환하는 예제)
CASE Students.Gender
  WHEN 'M'
    THEN 'Male'
    ELSE 'Female'
END

CASE Students.Gender
  WHEN 'M' THEN 'Male'
  WHEN 'F' THEN 'Female'
  ELSE 'Unknown'
END

-- (섭씨 읽기를 화씨로 변환)
CASE Readings.Measure
  WHEN 'C'
    THEN (Temperature * 9 / 5) + 32
    ELSE Temperature
END

-- (고객 등급을 기준으로 할인율 반환)
CASE (SELECT Customers.Rating FROM Customers
    WHERE Customers.CustomerID = Orders.CustomerID)
  WHEN 'A' THEN 0.10
  WHEN 'B' THEN 0.05
  ELSE 0.00
END
```
- 단순형
  - 값 표현식이 다른 값 표현식과 같은지 비교해 일치하면 값 표현식 하나를 반환하고, 일치하지 않으면 다른 값 표현식 하나를 반환한다.

> ISO 표준에서는 CASE 문에 WHEN IS NULL 을 명시할 수 있다고 하지만, 대부분의 주요 DBMS 는 이 구문을 지원하지 않는다.  
> NULL 검사를 해야 한다면 검색형 CASE 문에서 WHEN 절에 NULLIF 나 <표현식> IS NULL 을 사용한다.

```sql
-- (성별 및 결혼 상태에 따라 인사말 생성)
CASE WHEN Students.Gender = 'M' THEN 'Mr.'
  WHEN Students.MaritalStatus = 'S' THEN 'Ms.'
  ELSE 'Mrs.'
END

-- (제품 판매량에 따른 판매 등급 평가)
SELECT Products.ProductNumber,
       Products.ProductName,
       CASE
         WHEN
           (SELECT SUM(QuantityOrdered)
            FROM Order_Details
            WHERE Order_Details.ProductNumber = Products.ProductNumber) <= 200
           THEN 'Poor'
         WHEN
           (SELECT SUM(QuantityOrdered)
            FROM Order_Details
            WHERE Order_Details.ProductNumber = Products.ProductNumber) <= 500
           THEN 'Average'
         WHEN
           (SELECT SUM(QuantityOrdered)
            FROM Order_Details
            WHERE Order_Details.ProductNumber = Products.ProductNumber) <= 1000
           THEN 'Good'
         ELSE 'Excellent'
         END
FROM Products;

-- (직위에 따라 급여 인상 계산)
CASE Staff.Title
  WHEN 'Instructor'
  THEN ROUND(Salary * 1.05, 0)
  WHEN 'Associate Professor'
  THEN ROUND(Salary * 1.04, 0)
  WHEN 'Professor' THEN ROUND(Salary * 1.035, 0)
  ELSE Salary
END
```
- 검색형
  - 동등 검사 이외의 작업을 하고 싶거나 값 표현식 두 개 이상을 검사하고 싶을때 사용한다.
  - CASE 키워드 다음에 곧바로 값 표현식을 기술하는 대신 하나 이상의 검색 조건이 있는 WHERE 절을 사용하면 된다.
  - 검색 조건은 두 값 표현식 사이에 비교 연산자를 두어 사용할 수도 있지만, 범위, 집합 멤버, 패턴 매칭, NULL, 정량화 검사, 존재 여부 판별 등은 코드가 좀 더 복잡하다.
  - 데이터베이스 시스템은 TRUE 결과를 만나자마자 나머지 표현식 평가를 종료한다.

```sql
SELECT S.StudentID,
       S.LastName,
       S.FirstName, YEAR (SYSDATE) - YEAR (S.BirthDate) -
  CASE WHEN MONTH (S.BirthDate) < MONTH (SYSDATE)
  THEN 0
  WHEN MONTH (S.BirthDate) > MONTH (SYSDATE)
  THEN 1
  WHEN DAY (S.BirthDate) > DAY (SYSDATE)
  THEN 1
  ELSE 0
END AS Age
FROM Students AS S;
```
- CASE 는 어떻게 사용하느냐에 따라 그 형태가 매우 다양하다.(특히 검색형 CASE)
- 위와 같이 생년월일을 기준으로 나이를 계산하는 쿼리의 작성이 가능하다.

> DB2 에서는 SYSDATE 가 아닌 CURRENT DATE 특수 레지스터를 사용해야 한다.
> 오라클에서는 YEAR 대신 EXTRACT 를 사용하고, SQL Server 에서는 SYSDATETIME() 이나 GETDATE() 를 사용한다.
> Access 는 CASE 문을 지원하지 않지만 대신 IIf() 와 Date() 함수를 사용해 비슷한 결과를 얻을 수 있다.

```sql
SELECT CustomerID, CustFirstName, CustLastName
FROM Customers
WHERE (1 =
       (CASE
          WHEN CustomerID NOT IN
               (SELECT Orders.CUstomerID
                FROM Orders
                       INNER JOIN Order_Details
                                  ON Orders.OrderNumber = Order_Details.OrderNumber
                       INNER JOIN Products
                                  ON Order_Details.ProductNumber = Products.ProductNumber
                WHERE Products.ProductName = 'Skateboard')
            THEN 0
          WHEN CustomerID IN
               (SELECT Orders.CUstomerID
                FROM Orders
                       INNER JOIN Order_Details
                                  ON Orders.OrderNumber = Order_Details.OrderNumber
                       INNER JOIN Products
                                  ON Order_Details.ProductNumber = Products.ProductNumber
                WHERE Products.ProductName = 'Helmet')
            THEN 0
          ELSE 1 END));
```
- WHERE , HAVING 절의 조건식 일부로 CASE 를 사용할 수 있지만 다른 방법에 비해 그리 효율적이지 않다.
  - 조건을 여러 개 사용할 때 문제가 될 수 있다.
- 위의 예제에서는 스케이트 보드를 구매하지 않은 고객을 없앤 후 헬멧을 구매한 고객을 없애는 방식으로 쿼리가 구현되어 있다.

## 정리
- IF...THEN...ELSE 문제를 해결해야 할 때 CASE 만큼 강력한 도구는 없다.
- 값이 동일한지 검사할 때는 단순형을 사용하고, 복잡한 조건에는 검색형 CASE 를 사용한다.
- SELECT 절의 컬럼, WHERE, HAVING 절의 조건 일부를 포함해 값 표현식을 쓸 수 있는 곳에서는 CASE 를 사용할 수 있다.
