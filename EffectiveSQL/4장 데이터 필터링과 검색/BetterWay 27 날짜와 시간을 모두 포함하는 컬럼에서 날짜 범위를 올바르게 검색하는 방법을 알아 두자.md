## 날짜와 시간
![날짜_시간_데이터_타입](https://github.com/Evil-Goblin/BookStudy/assets/74400861/11b601e4-f1d6-43a3-aa51-1408642630bb)
- 날짜와 시간 데이터를 저장하는 데 사용할 수 있는 데이터 타입이다.

```sql
CREATE TABLE ProgramLogs
(
    LogID      int PRIMARY KEY,
    LogUserID  varchar(20)   NOT NULL,
    LogDate    datetime      NOT NULL,
    Logger     varchar(50)   NOT NULL,
    LogLevel   varchar(10)   NOT NULL,
    LogMessage varchar(1000) NOT NULL
);
```
- 예제로 사용할 로그 테이블이다.
- 특정 일자의 로그 메시지를 조회하는 예를 들어보자.

```sql
SELECT L.LogUserID, L.Logger, L.LogLevel, L.LogMessage
FROM ProgramLogs AS L
WHERE L.LogDate = CAST('7/4/2016' AS datetime);
```
- 이 쿼리는 문제가 있다.
- 7월 4일 데이터를 가져오려고 쿼리를 작성했지만, 해당 시스템이 영국으로 설정되어 있거나 언어가 프랑스어로 설정되어 있다면 결과가 달라질 수 있다.
  - 때문에 `yyyy-mm-dd` , `yyyymmdd` , `yyyy-mm-dd hh:mm:ss[.nnn]` 처럼 날짜 형식을 명확하게 기술해야 한다.
- 묵시적 날짜 변환 기능에 의존해서는 안된다.

> ISO 8601 날짜 형식인 `yyyy-mm-ddThh:mm:ss[.nnn]` 은 유효한 형식이지만, SQL 표준 일부는 아니다.  
> 날짜와 시간에 대한 ANSI SQL 표준 형식은 `yyyy-mm-dd hh:mm:ss` 인데, 이것은 'T' 구분자가 있는 ISO 8601 과는 맞지 않는다.  
> 모든 DBMS 가 ISO 8601 을 지원하는 것은 아니다.

```sql
SELECT L.LogUserID, L.Logger, L.LogLevel, L.LogMessage
FROM ProgramLogs AS L
WHERE L.LogDate = CONVERT(datetime, '2016-07-04', 120);
```
- 명시적 날짜 변환 함수를 사용하여 쿼리를 재작성하였다.
- 하지만 이 쿼리를 실행하면 반환되는 결과가 없을 것이다.
  - LogDate 커럶을 datetime 타입으로 정의했으므로 날짜와 시간을 갖고 있게 된다.
  - 이 쿼리에서는 시간 값을 주지 않았기 때문에 자동으로 `2016-07-04 00:00:00` 으로 변환된다.
  - 이 시간과 정확히 일치하는 정보가 없다면 반환되는 로우도 없을 것이다.
- `CAST(L.LogDate AS date)` 처럼 해당 컬럼에서 시간 정보를 제거할 수도 있지만, 이렇게 하면 해당 컬럼에 인덱스가 있을 때 인덱스를 사용할 수 없다.

```sql
SELECT L.LogUserID, L.Logger, L.LogLevel, L.LogMessage
FROM ProgramLogs AS L
WHERE L.LogDate BETWEEN CONVERT(datetime, '2016-07-04', 120)
          AND CONVERT(datetime, '2016-07-05', 120);
```
- BETWEEN 은 포괄적 연산자이다.
  - 때문에 `2016-07-05 00:00:00` 이란 데이터가 있다면 이 값도 결과에 포함되게 된다.

```sql
SELECT L.LogUserID, L.Logger, L.LogLevel, L.LogMessage
FROM ProgramLogs AS L
WHERE L.LogDate BETWEEN CONVERT(datetime, '2016-07-04', 120)
          AND CONVERT(datetime, '2016-07-04 23:59:59.999', 120);
```
- 보다 명확하게 날짜와 시간 형식을 명시했다.
- 하지만 이 쿼리도 문제가 있다.
  - SQL Server 에서 datetime 타입의 정확도는 3.33ms 이다.
  - 이는 SQL Server 가 `2016-07-04 23:59:59.999` 을 `2016-07-05 00:00:00` 으로 반올림한다는 의미이므로 잘못된 결과를 반환하게 된다.
- 모든 datetime 필드의 정밀도가 동일한 것은 아니다.
  - smalldatetime 타입의 컬럼은 여전히 반올림될 것이다.
- 새로운 버전이나 DBMS 의 변경에 따라 정밀도가 달라질 가능성도 있다.
- 더 안정적으로 해결하기 위해 포괄성을 가진 BETWEEN 을 사용하지 않는다.

```sql
SELECT L.LogUserID, L.Logger, L.LogLevel, L.LogMessage
FROM ProgramLogs AS L
WHERE L.LogDate >= CONVERT(datetime, '2016-07-04', 120)
  AND L.LogDate < CONVERT(datetime, '2016-07-05', 120);
```
- 가장 추천되는 쿼리이다.

```sql
SELECT L.LogUserID, L.Logger, L.LogLevel, L.LogMessage
FROM ProgramLogs AS L
WHERE L.LogDate >= CONVERT(datetime, @startDate, 120)
  AND L.LogDate < CONVERT(datetime, DATEADD(DAY, 1, @endDate), 120);
```
- 이 쿼리를 직접 실행할 때(날짜형 매개변수를 가진 저장 프로시저를 호출하는 식으로) 사용자는 종종 시작 일자와 끝 일자를 각각 '2016-07-04' , '2016-07-05' 으로 입력할 것이다.
  - 하지만 시작과 끝을 입력한다는 것은 끝 날짜를 포함하도록 설정하는 경우가 많기 때문에 아마도 '2016-07-04' 이상 '2016-07-06' 미만의 날짜일 것이다.
  - 때문에 위와 같이 DATEADD 함수를 사용해 쿼리를 작성하는 습관을 들이면 좋다.
    - 시작날짜 <= T < 끝날짜 + 1
- 핵심은 DBMS 의 날짜 구현 방식에 의존하기보다는 DATEADD 나 DBMS 가 제공하는 대용 함수를 사용해 명확히 정의된 방식으로 날짜를 연산하고, 사용자나 소프트웨어 프로그램이 끝 일자를 해석하는 방식의 차이를 보정하는 것이다.

## 정리
- 묵시적 날짜 변환에 의존하지 말고 명시적으로 날짜 변환 함수를 사용한다.
- 인덱스를 사용하지 못하므로 datetime 타입의 컬럼에 직접 함수를 사용하지 않는다.
- datetime 값은 반올림 오류가 있음을 기억하자.
  - BETWEEN 대신 >=, <= 연산자를 사용한다.
