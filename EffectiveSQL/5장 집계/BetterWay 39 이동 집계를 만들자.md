## 이동 집계
- 이전의 예제에서는 어떤 경계도 명시하지 않아서 ORDER BY 조건을 명시했는지 여부에 따라 디폴트로 그 경계가 만들어졌다.

```sql
SELECT
  o.OrderNumber, o.CustomerID, o.OrderTotal,
  SUM(o.OrderTotal) OVER (
    PARTITION BY o.CustomerID
    ORDER BY o.OrderNumber, o.CustomerID
    RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS TotalByCustomer,
  SUM(o.OrderTotal) OVER (
    PARTITION BY o.CustomerID
    --RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    ) AS TotalOverall
FROM Orders AS o
ORDER BY o.OrderNumber, o.CustomerID;
```
- 이전 예제의 쿼리와 동일하지만, 윈도우 프레임의 경계를 지정한 쿼리이다.
- TotalOverall 에서 위도우 프레임 정의 부분을 주석 처리했다.
  - ORDER BY 조건 없이 윈도우 프레임을 정의하는 것은 유효하지 않기 때문
- 윈도우 함수 표현식을 생성할 때마다 기본 설정이 적용됨을 알 수 있다.

### RANGE
- RANGE 구문 경계 설정 옵션
  - BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
  - BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING
  - BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
- 처음 두 옵션에서 BETWEEN...AND... 구문을 사용하는 대신 줄여서 쓸 수 있다.
  - UNBOUNDED PRECEDING
  - UNBOUNDED FOLLOWING
- RANGE 를 사용하면 현재 로우를 다른 로우와 비교하고, ORDER BY 조건을 기준으로 그루핑한다.  
  - ORDER BY 조건에 대해 두 로우의 결과가 동일한지와 별개로 물리적 오프셋을 지정하고 싶을 수 있기 때문에 항상 바람직한 방법은 아니다.  
- 물리적 오프셋을 사용하려면 RANGE 대신 ROWS 를 명시해야한다.
  - BETWEEN N PRECEDING AND CURRENT ROW
  - BETWEEN CURRENT ROW AND N FOLLOWING
  - BETWEEN N PRECEDING AND N FOLLOWING
- 여기서 N 은 양의 정수를 의미한다.  
- 경우에 따라 CURRENT ROW 를 UNBOUNDED PRECEDING 이나 UNBOUNDED FOLLOWING 으로 대체해 사용할 수도 있다.  
- 윈도우 프레임의 크기를 자유롭게 설정하려면 ROWS 를 사용한다.
  - 단, 현재 로우를 기준으로 한 물리적 오프셋으로만 윈도우 프레임의 크기를 설정할 수 있다.
- 표현식으로 윈도우 프레임의 크기를 설정할 수는 없지만, 윈도우 프레임 설정을 적용하기 전에 데이터를 미리 처리하는 방식으로 한계를 극복할 수 있다.
  - 공통 테이블 표현식(CTE)으로 일부 그룹을 만들고 나서 CTE 에 윈도우 함수를 적용한다.

```sql
WITH PurchaseStatistics AS (
  SELECT
    p.CustomerID,
    YEAR(p.PurchaseDate) AS PurchaseYear,
    MONTH(p.PurchaseDate) AS PurchaseMonth,
    SUM(p.PurchaseAmount) AS PurchaseTotal,
    COUNT(p.PurchaseID) AS PurchaseCount
  FROM Purchases AS p
  GROUP BY
    p.CustomerID,
    YEAR(p.PurchaseDate),
    MONTH(p.PurchaseDate)
)
SELECT
  s.CustomerID, s.PurchaseYear, s.PurchaseMonth,
  LAG(s.PurchaseTotal, 1) OVER (
    PARTITION BY s.CustomerID, s.PurchaseMonth
    ORDER BY s.PurchaseYear
    ) AS PreviousMonthTotal,
  s.PurchaseTotal AS CurrentMonthTotal,
  LEAD(s.PurchaseTotal, 1) OVER (
    PARTITION BY s.CustomerID, s.PurchaseMonth
    ORDER BY s.PurchaseYear
    ) AS NextMonthTotal,
  AVG(s.PurchaseTotal) OVER (
    PARTITION BY s.CustomerID, s.PurchaseMonth
    ORDER BY s.PurchaseYear
    ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING
    ) AS MonthOfYearAverage
FROM PurchaseStatistics AS s
ORDER BY s.CustomerID, s.PurchaseYear, s.PurchaseMonth;
```
- 올바른 평균을 계산하기 위해 LAG, LEAD 함수를 사용했다.
- CustomerID 와 PurchaseMonth 로 분할함으로서 그룹에서 현재 월 이전 또는 이후가 아니라 해당 연도의 모든 월을 그루핑해서 한 연도의 월과 다른 연도의 월을 비교할 수 있게 된다.
  - 때문에 윈도우 프레임의 경계를 설정할 때 PRECEDING 과 FOLLOWING 앞에 각각 1이라는 물리적 오프셋을 명시했다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/820b4398-48b8-43b0-85a6-5511b1351469)
- 쿼리의 실행 결과이다.
- 물리적 오프셋에 의존하는 쿼리가 일관적이라는 점이 중요하다.
  - 이 쿼리는 연도마다 항상 로우가 12개 있다고 가정한다.
  - 그렇지 않으면 PARTITION BY 와 ORDER BY 절이 제대로 동작하지 못한다.
- 특정 월에 매출이 없을 때도 해당 월에 대응하는 로우가 반환되어야 한다.

### RANGE, ROWS 사용법
- RANGE 와 ROWS 의 차이를 구분하는 것은 쉽지 않다.
- RANGE 는 논리적인 그루핑과 함께 동작해 ORDER BY 조건이 중복된 값을 반환할 때만 차이가 명확히 드러난다.

```sql
WITH PurchaseStatistics AS (
	SELECT 
		p.CustomerID,
		YEAR(p.PurchaseDate) AS PurchaseYear,
		MONTH(p.PurchaseDate) AS PurchaseMonth,
		SUM(p.PurchaseAmount) AS PurchaseTotal,
		COUNT(p.PurchaseID) AS PurchaseCount
	FROM Purchases AS p
	GROUP BY 
		p.CustomerID, 
		YEAR(p.PurchaseDate),
		MONTH(p.PurchaseDate)
)
SELECT
  s.CustomerID, s.PurchaseYear, s.PurchaseMonth,
  SUM(s.PurchaseCount) OVER (
    PARTITION BY s.PurchaseYear
    ORDER BY s.CustomerID
    RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS CountByRange,
  SUM(s.PurchaseCount) OVER (
    PARTITION BY s.PurchaseYear
    ORDER BY s.CustomerID
    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS CountByRows
FROM PurchaseStatistics AS s
ORDER BY s.CustomerID, s.PurchaseYear, s.PurchaseMonth;
```
- 이 쿼리는 동일한 경계 프레임에서 RANGE 와 ROWS 를 사용한 쿼리이다.
- ORDER BY 조건에 s.CustomerID 가 명시되어 있는데, 12개월에서 중복되므로 이 값은 유일하지 않다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/30fbc476-5c25-4de7-8fa2-b98e605ea80f)
- ORDER BY 조건에 PurchaseMonth 가 포함되어 있지 않기 때문에 PurchaseYear 값이 동일한 것 중에서 CustomerID 값이 같은 로우는 12개 뿐이다.
- RANGE 는 이들을 논리적으로 동일한 '그룹'으로 여겨 12개의 로우 합계가 모두 같다.
- ROWS 는 해당 그룹에 들어오는 로우의 누적 값을 반환하는데 이 값은 정렬되어 있지 않다.
  - 데이터베이스 엔진이 PurchaseMonth 값이 아닌 로우가 들어오는 순서대로 처리하기 때문
  - ORDER BY 조건에 PurchaseMonth 가 명시되어 있지 않았기 때문
- 때문에 해당 윈도우에 들어오는 마지막 로우가 12월이 아니라 3월이 되어 합계가 181이 된다.
- ORDER BY 는 결과 값을 극적으로 바꾸기 때문에 매우 중요하다.
- 따라서 윈도우 함수 표현식에서 PARTITION BY 와 ORDER BY 를 모두 사용할 때는 세심한 주의가 필요하다.

## 정리
- 윈도우 프레임의 경계를 기본 값이 아닌 값으로 변경할 때는 선택 사항일 때 조차도 ORDER BY 조건을 명시해야 한다.
- 윈도우 프레임 크기를 임의로 정의해야 할 때는 ROWS 를 명시해야 한다.
  - 이렇게 하면 선행 또는 후행 로우로 얼마나 많은 로우를 이 윈도우 프레임에 포함할지 결정할 수 있다.
- RANGE 를 사용할 때는 UNBOUNDED PRECEDING, CURRENT ROW, UNBOUNDED FOLLOWING 만 쓸 수 있다.
- 로우의 논리적인 그룹을 만들 때는 RANGE 를 사용하고, 물리적인 오프셋을 만들 때는 ROWS 를 사요한다.
  - ORDER BY 조건이 중복된 값을 반환하지 않는다면 이 둘은 동일한 결과를 반환한다.
