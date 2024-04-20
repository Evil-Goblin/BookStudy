## 날짜와 시간
- 날짜와 시간 데이터를 다루는 것은 매우 까다롭다.
  - 대부분의 DBMS 가 날짜와 시간 데이터 타입, 함수, 수행 가능한 연산에 대해 SQL 표준을 완벽하게 따르지 않는다.

```sql
SELECT DATENAME(WEEKDAY, o.OrderDate)         AS OrderDateWeekDay,
       o.OrderDate,
       DATENAME(WEEKDAY, o.ShipDate)          AS ShipDateWeekDay,
       o.ShipDate,
       DATEDIFF(DAY, o.OrderDate, o.ShipDate) AS DeliveryLead
FROM Orders AS o
WHERE o.OrderDate >=
      DATEADD(MONTH, -2, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
  AND o.OrderDate < DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1);
```
- 날짜 함수 사용 예제이다.
- 요구 사항은 크지 않지만 짧은 쿼리 안에 함수가 많이 호출된다.
  - 코드가 많아질수록 가독성은 떨어진다.

```sql
CREATE TABLE DimDate
(
    DateKey                    int         NOT NULL,
    DateValue                  date        NOT NULL PRIMARY KEY,
    NextDayValue               date        NOT NULL,
    YearValue                  smallint    NOT NULL,
    YearQuarter                int         NOT NULL,
    YearMonth                  int         NOT NULL,
    YearDayOfYear              int         NOT NULL,
    QuarterValue               tinyint     NOT NULL,
    MonthValue                 tinyint     NOT NULL,
    DayOfYear                  smallint    NOT NULL,
    DayOfMonth                 smallint    NOT NULL,
    DayOfWeek                  tinyint     NOT NULL,
    YearName                   varchar(4)  NOT NULL,
    YearQuarterName            varchar(7)  NOT NULL,
    QuarterName                varchar(8)  NOT NULL,
    MonthName                  varchar(3)  NOT NULL,
    MonthNameLong              varchar(9)  NOT NULL,
    WeekdayName                varchar(3)  NOT NULL,
    WeekDayNameLong            varchar(9)  NOT NULL,
    StartOfYearDate            date        NOT NULL,
    EndOfYearDate              date        NOT NULL,
    StartOfQuarterDate         date        NOT NULL,
    EndOfQuarterDate           date        NOT NULL,
    StartOfMonthDate           date        NOT NULL,
    EndOfMonthDate             date        NOT NULL,
    StartOfWeekStartingSunDate date        NOT NULL,
    EndOfWeekStartingSunDate   date        NOT NULL,
    StartOfWeekStartingMonDate date        NOT NULL,
    EndOfWeekStartingMonDate   date        NOT NULL,
    StartOfWeekStartingTueDate date        NOT NULL,
    EndOfWeekStartingTueDate   date        NOT NULL,
    StartOfWeekStartingWedDate date        NOT NULL,
    EndOfWeekStartingWedDate   date        NOT NULL,
    StartOfWeekStartingThuDate date        NOT NULL,
    EndOfWeekStartingThuDate   date        NOT NULL,
    StartOfWeekStartingFriDate date        NOT NULL,
    EndOfWeekStartingFriDate   date        NOT NULL,
    StartOfWeekStartingSatDate date        NOT NULL,
    EndOfWeekStartingSatDate   date        NOT NULL,
    QuarterSeqNo               int         NOT NULL,
    MonthSeqNo                 int         NOT NULL,
    WeekStartingSunSeq         int         NOT NULL,
    WeekStartingMonSeq         int         NOT NULL,
    WeekStartingTueSeq         int         NOT NULL,
    WeekStartingWedSeq         int         NOT NULL,
    WeekStartingThuSeq         int         NOT NULL,
    WeekStartingFriSeq         int         NOT NULL,
    WeekStartingSatSeq         int         NOT NULL,
    JulianDate                 int         NOT NULL,
    ModifiedJulianDate         int         NOT NULL,
    ISODate                    varchar(10) NOT NULL,
    ISOYearWeekNo              int         NOT NULL,
    ISOWeekNo                  smallint    NOT NULL,
    ISODayOfWeek               tinyint     NOT NULL,
    ISOYearWeekName            varchar(8)  NOT NULL,
    ISOYearWeekDayOfWeekName   varchar(10) NOT NULL
);
```
- 비즈니스 요구 사항이나 효율성이 데이터베이스에 있는 날짜에 많이 의존할 때는 다른 방법을 채택하는 것이 더 효과적일 수 있다.
  - 날짜 함수 대신 날짜 테이블을 만들어 사용할 수 있다.
- 위는 날짜 테이블을 만드는 DDL 문이다.
- 함수 호출 대신 날짜를 미리 계산해서 저장하기 위해 컬럼이 많은 단일 테이블을 생성하게 된다.

```sql
SELECT od.WeekDayNameLong      AS OrderDateWeekDay,
       o.OrderDate,
       sd.WeekDayNameLong      AS ShipDateWeekDay,
       o.ShipDate,
       sd.DateKey - od.DateKey AS DeliveryLead
FROM Orders AS o
         INNER JOIN Item55Example..DimDate AS od
                    ON o.OrderDate = od.DateValue
         INNER JOIN Item55Example..DimDate AS sd
                    ON o.ShipDate = sd.DateValue
         INNER JOIN Item55Example..DimDate AS td
                    ON td.DateValue = CAST(GETDATE() AS date)
WHERE od.MonthSeqNo = (td.MonthSeqNo - 1);
```
- 이전 쿼리를 날짜 테이블을 이용하도록 변경하였다.
  - 여러 함수와 복잡한 조건 대신 간단한 산술 연산과 조인만 수행한다.
  - DimDate 테이블을 세 번 조인해 매번 다른 날짜 컬럼을 가져온다.
- DimDate 테이블에 이미 순번 데이터를 미리 계산해서 저장해 두었기 때문에 단순한 산술 연산만 하면 된다.

![DimDate_테이블의_샘플_데이터](https://github.com/Evil-Goblin/BookStudy/assets/74400861/ff70cd79-9022-49f6-8e71-2ceee5733fd3)
- DimDate 테이블의 샘플 데이터이다.
- MonthValue 컬럼에는 1에서 12까지 값이 있고, MonthSeqNo 컬럼은 매월마다 연속적으로 증가하는 값이 들어 있다.
  - SeqNo 컬럼을 사용하면 날짜 함수를 호출하지 않고도 날짜의 서로 다른 부분(월, 분기)에서 산술 연산을 쉽게 수행할 수 있다.
  - 인덱스를 만들면 좀 더 쉽게 사거블 쿼리를 생성할 수 있다.
- 평일과 쉬운 알고리즘으로 처리할 수 없는 다른 비즈니스에 특화된 영역을 지원하려고 이 날짜 테이블을 확장할 수 있다.
  - 향후 5년, 10년, 그 이상에 대해 처리하는 모든 로직을 계산하려면 어려움에 봉착할 수 있다.
  - 따라서 미리 이 계산을 하면 날짜 계싼을 많이 하는 쿼리를 훨씬 간단하게 만들 수 있다.
- 디스크 I/O 의 CPU 사용량도 고려해야 한다.
  - 이 날짜 테이블의 데이터는 디스크에 저장되어 있는 반면, 날짜 함수들은 메모리에서 수행된다.
  - 이 날짜 테이블 데이터가 메모리에 올라온다 하더라도, 여전히 간단한 인라인 함수에 비해 날짜 테이블은 훨씬 처리를 많이 한다.
  - 실제로 날짜 테이블을 사용한 쿼리가 함수를 사용한 쿼리보다 느리게 수행된다.
    - DimDate 테이블에서 추가로 읽기 작업이 일어나기 때문
  - 하지만 다르 테이블에 있는 여러 날짜를 읽고 이에 따라 계산을 수행하는 쿼리에서는 날짜 테이블을 사용해야 빠르게 수행된다.
- DimDate 테이블의 기존 데이터는 변경되지 않고 주기적으로 데이터가 추가만하므로, 데이터 웨어하우스의 디멘젼 테이블에 인덱스르르 생성하는 것처럼 이 테이블에도 인덱스 몇 개를 추가할 수 있다.
  - 데이터베이스 엔진은 테이블 전체를 읽는 대신 해당 인덱스만 찾아 데이터를 읽음으로 I/O 양이 감소한다.

## 날짜 테이블을 사용한 쿼리 최적화
- 이전 쿼리에서 기본키 외에 다른 인덱스가 없다면 차선 실행 계획을 만들 수 있다.
  - 최상의 실행 계획을 만드는 데 추출해야 하는 몇 가지 세부 정보가 있기 때문
- 방법중 하나가 Orders 테이블의 OrderDate 와 ShipDate 컬럼에 대응하는 DimDate 테이블의 WeekDayNameLong 컬럼 값을 찾는 것이다.

```sql
CREATE INDEX DimDate_WeekDayLong
ON DimDate (DateValue, WeekdayNameLong);
```
- 이와 같이 인덱스를 생성해서 날짜로 평일의 요일 이름을 빠르게 추출할 수 있다.

```sql
CREATE INDEX DimDate_WeekDayLong_MonthSeqNo
ON DimDate (DateValue, WeekdayNameLong, MonthSeqNo);
```
- 하지만 이 쿼리에 DimDate 테이블로 처리하는 작업이 하나만은 아니다.
  - MonthSeqNo 컬럼도 사용한다.
  - td.MonthSeqNo 와 od.MonthSeqNo 를 비교하는 조건을 위해 인덱스를 만든다.
- 쿼리에서 사용되는 모든 컬럼에 인덱스를 만들었다.
- 하지만 이 인덱스는 DateValue 를 기준으로 정렬되기 때문에 WHERE 절에서 MonthSeqNo 컬럼에 직접 접근할 수 없다.

```sql
CREATE INDEX DimDate_MonthSeqNo
ON DimDate (MonthSeqNo, DateValue, WeekdayNameLong);
```
- 때문에 MonthSeqNo 로 정렬되는 인덱스를 만든다.
- 여러 인덱스를 추가할 때마다 실행 계획이 좋아지고 있다.
  - 해시 조인이 중첩 루프 조인으로 바뀌고, 테이블 스캔이 인덱스 탐색으로 바뀐다.

```sql
CREATE INDEX Orders_OrderDate_ShipDate
ON Orders (OrderDate, ShipDate);
```
- 하지만 쿼리에서 DimDate 테이블만 사용하는 것이 아니기에 Orders 테이블 또한 고려대상이 된다.
  - OrderDate, ShipDate 에도 인덱스를 생성해준다.
- 이로 인해 쿼리가 완전히 최적화되었다.
  - 특정 쿼리에 대해 가능한 한 빠르게 답을 산출하는 실행 계획 전반에서 훨씬 작은 인덱스를 사용할 수 있게 되었다.
  - 일부 DBMS 에서는 필터링된 인덱스를 사용하면 더 빠르게 수행할 수 있으므로 시도할 만한 가치가 있다.
- 물론 이 쿼리가 DimDate 테이블을 사용하는 유일한 쿼리는 아니다.
  - 하지만 DimDate 테이블의 값은 자주 변경되지 않을 것이기에 필요한 만큼 인덱스를 생성해 데이터 페이지를 읽지 않고 인덱스로 가능한 한 빠르게 결과를 산출하는 많은 선택권을 데이터베이스 엔진에 제공해야 한다.
  - 이는 메모리든 디스크든 I/O 가 빨라지는 것을 의미한다.

## 정리
- 날짜와 날짜 기준 계산을 많이 수행하는 애플리케이션에서는 날짜 테이블을 사용해 로직을 극적으로 간단하게 만들 수 있다.
- 날짜 테이블을 이용해 평일, 휴일, 회계 연도 같은 애플리케이션에 특화된 영역까지 확장해 계산할 수 있다.
- 기본적으로 날짜 테이블은 디멘젼 테이블이므로 온라인 트랜잭션 프로세싱(OnLine Transaction Processing, OLTP) 시스템에서 다수의 인덱스를 만들어도 된다.
  - 가능하면 명시적으로 메모리에 데이터를 올려 디스크를 읽는 손실을 줄이고 옵티마이저의 계산을 개선하자.
