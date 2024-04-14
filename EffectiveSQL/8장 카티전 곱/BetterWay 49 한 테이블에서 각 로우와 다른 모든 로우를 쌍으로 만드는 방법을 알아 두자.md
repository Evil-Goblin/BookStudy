## 조합
- 한 번에 두 팀씩 쌍으로 이루는 팀의 모든 조합 목록을 만드는 예시를 들어보자.

```sql
CREATE TABLE Teams
(
    TeamID    int         NOT NULL PRIMARY KEY,
    TeamName  varchar(50) NOT NULL,
    CaptainID int NULL
);
```
- Teams 테이블을 우선 생성한다.
- 각 팀이 다른 모든 팀과 경기하는 일정을 만들기 위해 한 번에 두 팀씩 가져올 팀들의 모든 조합(순열X)을 얻어야 한다.
  - 유일 값을 가진 컬럼이 최소한 하나라도 있다면, 어느 한 팀과 해당 팀의 유일한 ID 보다 작거나 큰 ID 값을 가진 다른 팀을 쌍으로 만드는 것은 간단하다.

```sql
SELECT Teams1.TeamID AS Team1ID, Teams1.TeamName AS Team1Name, Teams2.TeamID AS Team2ID, Teams2.TeamName AS Team2Name
FROM Teams AS Teams1
         CROSS JOIN Teams AS Teams2
WHERE Teams2.TeamID > Teams1.TeamID
ORDER BY Teams1.TeamID, Teams2.TeamID;
```
- 기준 테이블 복사본 두 개의 카티전 곱을 생성한 후 TeamID 값으로 필터링한다.

```sql
SELECT Teams1.TeamID AS Team1ID, Teams1.TeamName AS Team1Name, Teams2.TeamID AS Team2ID, Teams2.TeamName AS Team2Name
FROM Teams AS Teams1
         INNER JOIN Teams AS Teams2
                    ON Teams2.TeamID > Teams1.TeamID
ORDER BY Teams1.TeamID, Teams2.TeamID;
```
- 또는 이와 같이 비동등 조인으로 해결할 수도 있다.
  - SQL Server 에서는 두 쿼리가 모두 동일한 양의 자원을 사용하는데, 다른 시스템에서는 한 쿼리가 다른 것보다 빠를 수 있다.

![조회_결괴](https://github.com/Evil-Goblin/BookStudy/assets/74400861/3e29b8c9-ad15-4359-ab52-60e18b00ebdf)
- 위 쿼리의 조회 결과이다.

```sql
WITH TeamPairs AS
         (SELECT ROW_NUMBER()       OVER (ORDER BY Teams1.TeamID, Teams2.TeamID) 
      AS GameSeq, Teams1.TeamID AS Team1ID,
                 Teams1.TeamName AS Team1Name,
                 Teams2.TeamID   AS Team2ID,
                 Teams2.TeamName AS Team2Name
          FROM Teams AS Teams1
                   CROSS JOIN Teams AS Teams2
          WHERE Teams2.TeamID > Teams1.TeamID)
SELECT TeamPairs.GameSeq,
       CASE ROW_NUMBER() OVER (PARTITION BY TeamPairs.Team1ID 
        ORDER BY GameSeq) % 2 WHEN 0 THEN 
        CASE RANK() OVER (ORDER BY TeamPairs.Team1ID) % 3 
        WHEN 0 THEN 'Home' ELSE 'Away'
END
    ELSE 
    CASE RANK() OVER (ORDER BY TeamPairs.Team1ID) % 3 
    WHEN 0 THEN 'Away' ELSE 'Home'
END
END
AS Team1PlayingAt,
    TeamPairs.Team1ID, TeamPairs.Team1Name, 
    TeamPairs.Team2ID, TeamPairs.Team2Name
FROM TeamPairs
ORDER BY TeamPairs.GameSeq;
```
- 만약 한 라운드를 만들면서 각 팀이 홈팀과 원정팀을 번갈아가며 대략 같은 수의 홈 경기와 원정 경기를 하려면 위와 같이 윈도우 함수를 사용한다.
- TeamPairs CTE 는 원래의 쿼리를 기반으로 각 쌍에 로우 번호를 추가한 것이다.
- 주 쿼리에서는 모든 로우를 조사해(% 2) 첫 번째 팀에 'home', 'away' 중 무엇을 할당할지 결정한다.

### 조합의 다른 예시
- 제품 중 가장 인기 있는 조합을 알아내 진열하려고 한다.

```sql
SELECT Prod1.ProductNumber AS P1Num,
       Prod1.ProductName   AS P1Name,
       Prod2.ProductNumber AS P2Num,
       Prod2.ProductName   AS P2Name,
       Prod3.ProductNumber AS P3Num,
       Prod3.ProductName   AS P3Name
FROM Products AS Prod1
         CROSS JOIN Products AS Prod2
         CROSS JOIN Products AS Prod3
WHERE Prod1.ProductNumber < Prod2.ProductNumber
  AND Prod2.ProductNumber < Prod3.ProductNumber;
```
- Products 테이블에는 기본키인 ProductNumber 와 ProductName 컬럼이 있다.
- 위 쿼리는 한 번에 제품을 세 개 고르는 모든 조합을 찾는 쿼리이다.
- 비교 조건으로 < 을 사용하든 > 을 사용하든 상관 없지만, <> 연산자를 사용하면 조합이 아닌 순열이 조회된다.

## 정리
- N개의 항목에 대해 한 번에 K개를 고르는 모든 조합을 찾는 방법은 여러모로 쓸모가 많다.
- 조합을 찾는 기법은 유일한 컬럼이 있을 때 꽤 직관적이다.
- 조합별로 선택된 항목의 개수를 늘리려면 대상 테이블의 복사본을 쿼리에 추가해 처리한다.
- 대상 데이터가 매우 클 때는 주의해야 한다.
  - 결과로 로우가 수억 개 나올 수 있다.
