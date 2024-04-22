## 피벗
- '피벗'은 컬럼 중 한 값을 컬럼의 제목으로 사용하는 것이다.

![에이전시_데이터베이스_설게](https://github.com/Evil-Goblin/BookStudy/assets/74400861/ea857362-b740-44a0-b32a-3f532d2b03d4)
- 예시에 사용할 엔터테인먼트 에이전시 데이터베이스 설계이다.

```sql
SELECT A.AgtFirstName,
       A.AgtLastName, MONTH (E.StartDate) AS ContractMonth, SUM (E.ContractPrice) AS TotalContractValue
FROM Agents AS A INNER JOIN Engagements AS E
ON A.AgentID = E.AgentID
WHERE YEAR (E.StartDate) = 2015
GROUP BY A.AgtFirstName, A.AgtLastName, MONTH (E.StartDate);
```
- 마케팅 관리자가 2015년 월별 각 에이전트가 예약한 연주단 계약의 총합계 값을 보고서로 요청했다고 가정한다.
  - 월과 에이전트별 전체 계약 금액을 계산하는 쿼리이다.

![조회_결과_일부](https://github.com/Evil-Goblin/BookStudy/assets/74400861/d47ab3e4-c473-445e-abd3-0cde12c0354e)
- 위 쿼리의 조회 결과 중 일부이다.
- 하지만 만약 요구사항이 바뀐다면 어떻게 해야할까?
  - "분기별 데이터가 보고싶다. 맨 위에는 분기를 보여주고, 에이전트는 세로로 보여주며, 둘의 교차점에 분기별로 에이전트별 계약 금액을 보여줬으면 좋겠다. 첫 분기는 5월 1일부터 시작한다. 모든 에이전트의 예약 여부도 볼 수 있었으면 좋겠다."
- 아쉽게도 ISO SQL 표준에는 이 작업을 쉽게 처리할 수 있는 방법이 없다.
  - 각 데이터베이스 시스템은 나름대로 해결책을 구현해 놓았다.
    - DB2: DECODE
    - Access: TRANSFORM
    - SQL Server: PIVOT
    - Oracle: PIVOT, DECODE
    - PostgreSQL: CROSSTAB

```sql
SELECT A.AgtFirstName, A.AgtLastName, 
    YEAR(E.StartDate) AS ContractYear,
    SUM(CASE WHEN MONTH(E.StartDate) = 1 
             THEN E.ContractPrice END) AS January,
    SUM(CASE WHEN MONTH(E.StartDate) = 2 
             THEN E.ContractPrice END) AS February,
    SUM(CASE WHEN MONTH(E.StartDate) = 3 
             THEN E.ContractPrice END) AS March,
    SUM(CASE WHEN MONTH(E.StartDate) = 4 
             THEN E.ContractPrice END) AS April,
    SUM(CASE WHEN MONTH(E.StartDate) = 5 
             THEN E.ContractPrice END) AS May,
    SUM(CASE WHEN MONTH(E.StartDate) = 6 
             THEN E.ContractPrice END) AS June,
    SUM(CASE WHEN MONTH(E.StartDate) = 7 
             THEN E.ContractPrice END) AS July,
    SUM(CASE WHEN MONTH(E.StartDate) = 8 
             THEN E.ContractPrice END) AS August,
    SUM(CASE WHEN MONTH(E.StartDate) = 9 
             THEN E.ContractPrice END) AS September,
    SUM(CASE WHEN MONTH(E.StartDate) = 10 
             THEN E.ContractPrice END) AS October,
    SUM(CASE WHEN MONTH(E.StartDate) = 11 
             THEN E.ContractPrice END) AS November,
    SUM(CASE WHEN MONTH(E.StartDate) = 12 
             THEN E.ContractPrice END) AS December
FROM Agents AS A LEFT JOIN
    (SELECT En.AgentID, En.StartDate, En.ContractPrice
     FROM Engagements AS En
     WHERE En.StartDate >= '2015-01-01'
       AND En.StartDate < '2016-01-01') AS E
  ON A.AgentID = E.AgentID
GROUP BY AgtFirstName, AgtLastName, YEAR(E.StartDate);
```
- 요구 사항 중 월별 합계를 산출하는 문제는 탤리 테이블 없이 표준 SQL 로 해결이 가능하다.

![분기_탤리_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/a6e61d62-5fd7-427d-a571-10cab74d3b97)
- 하지만 더 간단한 해결책으로 분기를 미리 정의해 놓고 복잡한 CASE 절로 합계를 계산하는 대신, 각 분기 컬럼에 0이나 1값을 할당한 탤리 테이블을 사용하면 된다.
- 이는 분기 날짜별로 피벗할 수 있게 만든 탤리 테이블(ztblQuarters)이다.

```sql
SELECT AE.AgtFirstName, AE.AgtLastName, z.YearNumber,
    SUM(AE.ContractPrice * Z.Qtr_1st) AS First_Quarter,
    SUM(AE.ContractPrice * Z.Qtr_2nd) AS Second_Quarter,
    SUM(AE.ContractPrice * Z.Qtr_3rd) AS Third_Quarter,
    SUM(AE.ContractPrice * Z.Qtr_4th) AS Fourth_Quarter
FROM ztblQuarters AS Z CROSS JOIN 
  (SELECT A.AgtFirstName, A.AgtLastName, 
       E.StartDate, E.ContractPrice
   FROM Agents AS A LEFT JOIN Engagements AS E
    ON A.AgentID = E.AgentID) AS AE
WHERE (AE.StartDate BETWEEN Z.QuarterStart AND Z.QuarterEnd)
   OR (AE.StartDate IS NULL AND Z.YearNumber = 2015)
GROUP BY AgtFirstName, AgtLastName, YearNumber;
```
- 최종 쿼리에서 Agents 와 Engagements 테이블을 조인한 쿼리, 탤리 테이블에 대한 카티전 곱을 생성했다.
  - 결과로 QuarterStart 와 QuarterEnd 날짜 컬럼을 사용해 필터링한다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/8f92ae0a-e483-4c63-aa3e-8b19fbb778ee)
- 위 쿼리의 조회 결과이다.
  - 예약이 없는 에이전트의 집계 금액을 NULL 로 표현하기 위해 LEFT JOIN 을 수행했다.
- 물론 다른 대안이 있는데도 피벗 기능을 구현하려고 탤리 테이블을 사용하는 것이 항상 최선은 아니다.
  - 하지만 피벗이 필요한 데이터에 대한 조건으로 여러 변수를 사용할 때는 탤리 테이블이 좋은 선택이 될 수 있다.
  - 탤리 테이블에 로우만 추가하면 다른 값에서도 쿼리가 동작하기 때문

## 정리
- 데이터를 피벗해야 할 때 각 데이터베이스 시스템은 이것을 처리하는 구문을 제공할 것이다.
- 표준 SQL 만 사용하고 싶다면 집계 함수 내에서 각 로우의 값을 제공하는 CASE 표현식으로 데이터를 피벗할 수 있다.
- 피벗 대상 데이터에 대해 컬럼 범위를 결정하는 값이 가변적이라면, 탤리 테이블을 사용해 SQL 을 간단히 만드는 선택이 현명하다.
