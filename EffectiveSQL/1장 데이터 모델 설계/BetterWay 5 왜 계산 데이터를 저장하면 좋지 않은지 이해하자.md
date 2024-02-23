## 계산 데이터는 성능에 영향을 미친다.
```sql
CREATE TABLE Orders
(
    OrderNumber int NOT NULL,
    OrderDate   date NULL,
    ShipDate    date NULL,
    CustomerID  int NULL,
    EmployeeID  int NULL,
    OrderTotal  decimal(15, 2) NULL
);
```
- 얼핏 보면 `Orders` 테이블에 `OrderTotal` 컬럼을 두는 것이 좋을 것 같다.
  - 아마도 `Order_Details` 테이블의 수량 * 가격의 합일 것이다.
- 하지만 이런 계산 필드는 운영 중인 데이터베이스 성능에는 심각한 영향을 미칠 수 있다.
- 또한 `Order_Details` 테이블의 로우가 변경, 삽입, 삭제될 때마다 값을 재계산해야 하기 때문에 데이터 무결성을 유지하기 어렵다.
- 요즘의 많은 데이터베이스 시스템은 필드를 관리하는 방법을 제공하기 때문에 서버에서 수행되는 코드가 이런 계산을 대신한다.
- 계산 컬럼을 현행화하는 가장 원시적인 방법은 계산에 사용되는 원천 컬럼이 있는 테이블에 트리거를 추가하는 것이다.
  - 트리거는 대상 테이블에 데이터가 입력, 갱신, 삭제될 때 수행하는 코드이다.
  - 위의 `Orders` 의 예제에서 `OrderTotal` 컬럼 값을 재계산할 때는 `Order_Details` 테이블에 트리거가 필요하다.
  - 하지만 트리거는 정확하게 작성하기가 어렵고 비용도 비싸다.
- 몇몇 데이터베이스 시스템에서는 테이블을 생성할 때 계산 컬럼을 정의하는 방법을 제공한다.
  - 복잡한 코드를 작성하지 않아도 되는 부분에서 트리거보다 낫다.
- 최신 버전의 `RDBMS` 에서는 이미 계산 컬럼을 정의해 사용하는 기능을 지원한다.
  - `SQL Server` 에서는 `AS` 키워드 다음에 수행할 계산을 정의하는 표현식을 붙일 수 있다.

```sql
CREATE FUNCTION dbo.getOrderTotal(@orderId int)
RETURNS money
AS
BEGIN
    DECLARE @r money
    SELECT @r = SUM(Quantity * Price)
    FROM Order_Details WHERE OrderNumber = @orderId
    RETURN @r;
END;
GO

CREATE TABLE Orders
(
    OrderNumber int NOT NULL,
    OrderDate   date NULL,
    ShipDate    date NULL,
    CustomerID  int NULL,
    EmployeeID  int NULL,
    OrderTotal  money AS dbo.getOrderTotal(OrderNumber)
);
```
- `SQL Server` 을 이용한 함수와 테이블 정의 이다.
- 주의점으로 해당 함수가 다른 테이블에 있는 데이터에 의존하고 비결정적 함수이므로 계산 컬럼에 인덱스를 만들 수 없다는 것이다.
- 하지만 이런 식의 처리는 좋지 않다.
  - 이 함수는 비결정적 함수이고, 이 계산 컬럼은 테이블의 다른 실제 컬럼처럼 값이 지속되지 않기 때문이다.
  - 이 컬럼으로는 인덱스를 만들 수 없고, 컬럼을 참조할 때마다 각 로우에 대해 서버가 함수를 호출해야 하므로 서버에 많은 부하가 걸린다.
  - 차라리 결과가 필요할 때마다 `OrderID` 컬럼을 기준으로 집계해 계산하는 서브쿼리로 해당 테이블을 조인하는 것이 훨씬 효율적이다.

### 결정적 함수와 비결정적 함수
- 결정적(`Deterministic`) 함수는 특정 값 집합이 입력되면 언제나 동일한 결과를 반환한다.
  - `SQL Server` 의 내장 함수 `DATEADD()` 는 매개변수 세 개를 받는데, 동일한 값에 동일한 결과를 반환하기 때문에 결정적 함수이다.
- 비결정적(`Nondeterministic`) 함수는 특정 값 집합이 입력되더라도 매번 다른 값을 반환할 수도 있다.
  - `SQL Server` 의 `GETDATE()` 함수는 실행할 때마다 매번 다른 값을 반환하므로 비결정적 함수이다.

```sql
-- 테이블을 변경하려고 INTEGRITY 옵션을 끔
SET INTEGRITY FOR Order_Details OFF;
-- 표현식을 사용해 계산 컬럼을 생성
ALTER TABLE Order_Details
    ADD COLUMN ExtendedPrice decimal(15,2)
        GENERATED ALWAYS AS (QuantityOrdered * QuotedPrice);
-- INTEGRITY 옵션을 다시 켬
SET INTEGRITY FOR Order_Details IMMEDIATE CHECKED FORCE GENERATED;
-- 계산 컬럼에 인덱스를 생성
CREATE INDEX Order_Details_ExtendedPrice ON Order_Details (ExtendedPrice);
```
- `DB2` 에서 표현식을 사용한 테이블 컬럼 정의이다.
- `DB2` 는 결정적 함수 호출이나 표현식을 사용하는 컬럼을 정의할 수 있다.
- 결정적 함수 호출이나 표현식은 결정적 특성이 있기 때문에 테이블에 컬럼을 생성하고 인덱스도 만들 수 있다.
  - [다른 DB 에서 처리한 예제](https://github.com/gilbutITbook/006882)

## 문제점
- 대용량 온라인 데이터 입력용 테이블에서 계산 컬럼을 추가하는 것은 서버에 심각한 부하가 걸려 응답 시간이 현저히 느려진다.
  - `DB2` , `SQL Server` , `Oracle` 은 계산 컬럼에 인덱스를 만들 수 있고, 보통은 인덱스 덕분에 계산된 결과에 의존하는 쿼리가 빨리 수행될 것이다.
  - 비결정적 함수를 사용하는 경우 인덱스를 만들 수 없다는 것을 명심하자.
- 호출된 함수 값이 변할 때마다, 즉 `Order_Details` 테이블의 로우를 갱신, 삭제, 삽입할 때마다 부하가 걸린다.
  - 주문 정보를 입력하면 함수가 계산을 수행하고 인덱스 값을 저장해야 하기 때문에 응답 시간이 느려진다.

## 정리
- 많은 시스템에서 테이블을 정의할 때 계산 컬럼을 정의할 수 있지만 성능을 고려해야 한다.
  - 특히 비결정적 표현식이나 함수를 사용할 때는 더욱 그렇다.
- 트리거를 사용해 계산 컬럼을 일반 컬럼처럼 정의할 수 있지만 작성해야 할 코드가 복잡하다.
- 계산 컬럼은 데이터베이스 시스템에 추가적인 부하를 일으키므로 계산 컬럼으로 얻는 혜택이 부하를 일으키는 비용보다 클 때만 사용한다.
- 대부분의 경우 저장 공간이 증가하고 데이터 갱신이 느린 대신 일부 혜택을 보려고 계산 컬럼에 인덱스를 만들고 싶을 것이다.
- 인덱스 적용이 어려울 때는 테이블에 계산 결과를 저장해 놓는 방법 대신 뷰를 이용해 수행할 계산을 정의하는 방법을 종종 사용한다.
