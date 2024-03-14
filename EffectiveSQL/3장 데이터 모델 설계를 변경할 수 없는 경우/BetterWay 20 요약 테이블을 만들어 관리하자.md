## 요약 테이블
- 뷰를 사용하면 복잡한 쿼리를 단순화하고 데이터를 적절히 요약해 사용할 수 있다.
- 하지만 처리하는 데이터가 많다면 요약 테이블을 생성하는 것이 더 바람직할 수도 있다.
- 요약 테이블은 필요한 모든 데이터를 한 테이블로 모아 주므로 데이터 구조 파악이 쉽고 원하는 정보를 빠르게 파악할 수 있다.

### 요약 테이블 생성
- 세부 데이터를 요약하는 테이블을 생성하고, 트리거를 사용해 원본 테이블의 데이터가 변경될 때마다 요약 테이블의 데이터를 갱신한다.
  - 원본 테이블이 빈번하게 변경된다면 많은 부하를 주게 된다.
- 저장 프로시저를 사용해 주기적으로 요약 테이블을 갱신한다.
  - 요약 테이블 데이터를 모두 삭제한 뒤 다시 요약 데이터를 생성한다.
- DB2 는 요약 테이블 개념을 제공한다.
  - DB2 의 요약 테이블은 하나 이상의 원본 테이블 데이터를 집계해 관리할 수 있다.
  - 원본 테이블이 변경될 때마다 자동 또는 수동으로 데이터를 갱신할 수 있다.
  - DB2 의 요약 테이블은 사용자에게 빠른 결과를 제공한다.
  - 요약 테이블을 생성할 때 `ENABLE QUERY OPTIMIZATION` 을 명시하면 사용자 쿼리가 정보를 요청할 경우 옵티마이저는 요약 테이블에서 미리 요약된 정보를 사용할 수 있다.
  - 이 모든 작업은 어느 정도 비용이 들지만, 요약 데이터 관리를 위한 트리거나 저장 프로시저를 사용할 필요가 없다는 장점이 있다.

```db2
CREATE SUMMARY TABLE SalesSummary AS
(
    SELECT t5.RegionName              AS RegionName,
           t5.CountryCode             AS CountryCode,
           t6.ProductTypeCode         AS ProductTypeCode,
           t4.CurrentYear             AS CurrentYear,
           t4.CurrentQuarter          AS CurrentQuarter,
           t4.CurrentMonth            AS CurrentMonth,
           COUNT(*)                   AS RowCount,
           SUM(t1.Sales)              AS Sales,
           SUM(t1.Cost * t1.QUantity) AS Cose,
           SUM(t1.Quantity)           AS Quantity,
           SUM(t1.GrossProfit)        AS GrossProfit
    FROM Sales AS t1,
         Retailer AS t2,
         Product AS t3,
         datTime AS t4,
         Region AS t5,
         ProductType AS t6
    WHERE t1.RetailerId = t2.RetailerId
      AND t1.ProductId = t3.ProductId
      And t1.OrderDay = t4.DayKey
      AND t2.RetailerCountryCode = t5.CountryCode
      AND t3.ProductTypeId = t6.ProductTypeId
    GROUP BY t5.RegionName, t5.CountryCode, t6.ProductTypeCode, t4.CurrentYear, t4.CurrentQuarter, t4.CurrentMonth
)
    DATA INITIALLY DEFERRED REFRESH IMMEDIATE
    ENABLE QUERY OPTIMIZATION
    MAINTAINED BY SYSTEM
    NOT LOGGED INITIALLY;
```
- DB2 에서 테이블 여섯 개에 든 데이터를 요약해 `SalesSummary` 라는 요약 테이블을 생성하는 스크립트이다.
- 요약 테이블은 구체화된 쿼리 테이블(Materialized Query Table, MQT)의 한 타입이다.
- MQT 의 CREATE SQL 문에서 GROUP BY 절을 사용한 것이 요약 테이블이라고 보면 된다.
- MQT 에서는 FROM 절에 명시적인 INNER JOIN 사용을 제한해 두므로 WHERE 절에 조인 조건을 기술했다.
- REFRESH IMMEDIATE 절을 활성화하는 데 사용한 SELECT 절에 COUNT(*) 를 추가했다.
  - 옵티마이저가 MQT 를 사용할 때 필요한 사항이다.

#### 다른 데이터베이스
- Oracle 은 구체화된 뷰를 제공하여 비슷한 효과를 낼 수 있다.
- SQL Server 는 구체화된 뷰를 지원하지 않지만 뷰에 인덱스를 만들어 비슷한 효과를 낼 수 있다.

## 요약 테이블 단점
- 요약 테이블은 별도의 데이터를 저장하므로 저장 공간을 차지한다.
- 원본 테이블과 요약 테이블 간 데이터를 일관되게 유지하려면 관리 작업(트리거, 제약 조건, 저장 프로시저 등)이 필요하다.
- 사용자에게 필요한 집계 값을 미리 계산하고 요약 테이블에 담아 놓으려면 그 데이터가 무엇인지 미리 파악해 두어야 한다.
- 그루핑 조건이나 필터 조건이 다를 때는 오약 테이블이 여러개 필요하다.
- 스케줄을 만들어 요약 테이블 데이터를 갱신하도록 한다.
- SQL 을 사용해 요약 테이블 데이터를 주기적으로 관리해야 한다.
  - 예를 들어 요약 테이블에서 지난 12개월간 데이터를 보여주려면 이 테이블에서 1년 이상 지난 데이터는 제거하는 로직이 필요하다.

### 인라인 요약
- 추가적인 트리거, 제약조건, 저장 프로시저를 사용하면서 늘어나는 관리 비용을 없애는 방법으로 `강력한 SQL 프로그래밍을 위한 Transact SQL` 책에서 인라인 요약이란 형태로 제안되었다.
- 인라인 요약은 기존 테이블에 집계 컬럼을 추가하는 방법이다.
  - 보통 INSERT INTO 문을 사용해 데이터를 집계하고 저장할 것이다.
  - 집계 데이터에 포함되지 않는 컬럼 데이터는 NULL 이나 일부 고정된 날짜 데이터처럼 알려진 값으로 설정될 것이다.
- 인라인 요약의 장점은 요약 데이터와 요약 전 데이터를 합치거나 분리해서 손쉽게 질의할 수 있다는 것이다.
- 요약된 데이터는 특정 컬럼에서 알려진 값을 보면 쉽게 식별할 수 있지만, 이 점을 제외하면 요약전 데이터와 분간할 수 없다는 단점이 있다.
- 이 방법을 사용하려면 요약 전과 후의 데이터를 포함하는 테이블에서 모든 쿼리를 제대로 작성해야 한다.

## 정리
- 요약 데이터를 저장하면 집계에 필요한 처리를 최소화할 수 있다.
- 요약 데이터를 저장해 놓은 테이블을 사용하면 집계 작업을 할 때 좀 더 효율적으로 집계된 데이터가 포함된 필드에 인덱스를 만들 수 있다.
- 요약 작업은 다소 정적인 테이블에 적합하다.
  - 원천 테이블이 빈번히 변경된다면 요약 작업의 부하는 무시할 수 없을 정도로 커진다.
- 요약 작업을 수행하는 데 트리거를 사용할 수 있지만, 요약 테이블 데이터를 지우고 다시 생성하는 식으로 수행한다면 저장 프로시저를 사용하는 편이 더 낫다.
