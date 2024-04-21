## 일정 목록
```sql
CREATE TABLE Appointments
(
    AppointmentID   int IDENTITY (1, 1) PRIMARY KEY,
    ApptStartDate   date NOT NULL,
    ApptStartTime   time NOT NULL,
    ApptEndDate     date NOT NULL,
    ApptEndTime     time NOT NULL,
    ApptDescription varchar(50) NULL
);
```
- 예제에 사용할 Appointments 테이블이다.
- 각 약속의 시작과 끝은 날짜와 시간으로 구성되어 있으므로 DateTime 이나 Timestamp 데이터 타입을 사용하는 것이 더 적절하다.
- 날짜와 시간 필드를 분리해 값을 저장하는 것이 사거블쿼리를 만들기 쉽다.

![Appointments_table](https://github.com/Evil-Goblin/BookStudy/assets/74400861/ea2f08c1-7206-4b1f-97e0-7af188ed81e3)
- Appointments 테이블의 샘플 데이터이다.

```sql
CREATE TABLE DimDate
(
    DateKey  int PRIMARY KEY,
    FullDate date NOT NULL
);

CREATE INDEX iFullDate
    ON DimDate (FullDate);
```
- 날짜 테이블과 인덱스를 만든다.

```sql
SELECT D.FullDate,
       A.ApptDescription,
       CAST(A.ApptStartDate AS datetime) + CAST(A.ApptStartTime AS datetime) AS ApptStart,
       CAST(A.ApptEndDate AS datetime) + CAST(A.ApptEndTime AS datetime)     AS ApptEnd
FROM DimDate AS D
         LEFT JOIN Appointments AS A
                   ON D.FullDate = A.ApptStartDate
ORDER BY D.FullDate;
```
- 이전의 테이블들을 기반으로 날짜 테이블에 있는 모든 날짜와 약속이 있는 날을 조회한다.
- 만약 특정 기간에 잡힌 약속만 보고 싶다면 WHERE 절에서 Appointments 테이블이 아닌 DimDate 테이블에 있는 컬럼을 참조해야 한다.
  - 모든 날짜는 DimDate 테이블에 있기 때문

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/95a1e7af-c4a6-4e11-8551-233982cdddf9)
- 쿼리를 수행한 결과이다.

## 정리
- 날짜 테이블에 적절한 인덱스가 있는지 확인한다.
- 사용 중인 DBMS 에서 날짜와 시간을 제대로 처리하는 방법을 익히고 이에 맞게 설계한다.
- WHERE 절 조건에서 적합한 테이블 컬럼을 사용하는지 확인한다.
