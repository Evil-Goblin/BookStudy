## 사거블
- 쿼리 성능 향상을 위해 적절한 인덱스를 만드는 것은 중요하다.
- 하지만 인덱스만으로는 부족하다.
- DBMS 엔진이 인덱스를 잘 활용하려면 쿼리의 서술 논술절(WHERE, ORDER BY, GROUP BY, HAVING 절)이 인덱스를 사용해야 한다.
- 이를 사거블(Search ARGument ABLE, Sargable) 이라고 한다.
- 때문에 쿼리가 사거블이 되지 않는 이유를 이해하는 것이 중요하다.

### 사거블 쿼리를 만드는 연산자
- 비교하려는 값에 따라 사용되는 연산자
  - =
  - \>
  - <
  - \>=
  - <=
  - BETWEEN
  - LIKE (검색 문자열 앞에 %를 붙이지 않을 때)
  - IS [NOT] NULL
- 사거블이지만 성능 향상 목적으로는 사용하지 않는 연산자.
  - <>
  - IN
  - OR
  - NOT IN
  - NOT EXISTS
  - NOT LIKE
- 인덱스를 사용하지 못할 때
  - WHERE 절 조건에서 한 개 이상의 필드에 대해 연산하는 함수를 사용하는 쿼리
    - 각 로우에서 함수가 연산을 수행하므로 인덱스 자체에 동일한 함수가 포함되어 있지 않다면 쿼리 옵티마이저는 인덱스를 사용하지 않을 것이다.
  - WHERE 절에서 필드에 대해 수치 연산을 하는 경우
  - LIKE '%something%' 처럼 %를 사용하는 경우

### 예시

```sql
CREATE TABLE Employees
(
  EmployeeID   int IDENTITY (1,1) PRIMARY KEY,
  EmpFirstName varchar(25) NULL,
  EmpLastName  varchar(25) NULL,
  EmpDOB       date NULL,
  EmpSalary    decimal(15, 2) NULL
);

CREATE INDEX [EmpFirstName]
  ON [Employees]([EmpFirstName] ASC);

CREATE INDEX [EmpLastName]
  ON [Employees]([EmpLastName] ASC);

CREATE INDEX [EmpDOB]
  ON [Employees]([EmpDOB] ASC);

CREATE INDEX [EmpSalary]
  ON [Employees]([EmpSalary] ASC);
```
- 각 필드에 인덱스를 설정한 예시용 테이블이다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE YEAR (EmpDOB) = 1950;
```
- 특정 연도 데이터를 조회하는 넌사거블 쿼리이다.
  - YEAR 함수를 호출해 비교하기 때문에 인덱스를 사용하지 못한다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE EmpDOB >= CAST('1950-01-01' AS Date)
  AND EmpDOB < CAST('1951-01-01' AS Date);
```
- 사거블 방식으로 동일한 데이터를 조회하는 쿼리이다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE LEFT (EmpLastName, 1) = 'S';
```
- 성이 특정 문자로 시작하는 직원을 조회하는 넌사거블 쿼리이다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE EmpLastName LIKE 'S%';
```
- 사거블 방식으로 동일한 데이터를 조회하는 쿼리이다.
- LIKE 연산자를 사용했어도 와일드카드 문자를 검색 문자열 뒤에만 붙여 넌사거블 쿼리가 되지 않는다.
- 그렇다고 항상 인덱스를 사용한다고 보장하지는 못한다.
- LIKE '%something%' 을 사거블 쿼리로 만드는 방법은 없다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE IsNull(EmpLastName, 'Viescas') = 'Viescas';
```
- IsNull() 함수를 사용한 넌사거블 쿼리이다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE EmpLastName = 'Viescas'
   OR EmpLastName IS NULL;
```
- 사거블 방식으로 동일한 데이터를 조회하는 쿼리이다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE EmpLastName = 'Viescas'
UNION ALL
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE EmpLastName IS NULL;
```
- OR 을 사용하면 EmpLastName 컬럼에 있는 인덱스를 사용하지 못할 수도 있다.
- 때문에 위의 쿼리가 더 안전하다고 할 수 있다.
  - 검색 값과 NULL 에 대해 별도로 필터링된 인덱스가 있을 때는 더욱 그렇다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE EmpSalary * 1.10 > 100000;
```
- 계산을 수행하므로 넌사거블 쿼리이다.
- EmpSalary 컬럼의 인덱스는 사용되지 않고 모든 로우에 대해서 계산이 수행된다.

```sql
SELECT EmployeeID, EmpFirstName, EmpLastName
FROM Employees
WHERE EmpSalary > 100000 / 1.10;
```
- 계산에 필드가 포함되어있지 않으면 사거블 쿼리가 된다.

## 정리
- 넌사거블 연산자를 사용하지 않는다.
- WHERE 절에서 하나 이상의 필드에 대해 연산하는 함수를 사용하지 않는다.
- WHERE 절에서 필드에 대한 수치 연산을 수행하지 않는다.
- LIKE 연산자를 사용할 때 % 문자는 검색 문자열 끝에 붙인다.
  - '%something' 이나 'some%thing' 처럼 사용하지 않는다.
