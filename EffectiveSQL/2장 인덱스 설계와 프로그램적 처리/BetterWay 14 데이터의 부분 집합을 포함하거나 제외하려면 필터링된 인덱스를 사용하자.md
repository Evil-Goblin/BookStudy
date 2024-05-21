## 필터링된 인덱스
- 필터링된 인덱스(SQL Server) 나 부분 인덱스(PostgreSQL) 은 비클러스터드 인덱스이며, 테이블에 있는 일부 로우의 집합만 포함한다.
- 테이블의 로우 개수와 인덱스의 로우 개수 비율이 1:1인 전통적인 비클러스터드 인덱스에 비해 훨씬 적은 용량을 차지한다.
  - 필터링된 인덱스로 성능과 저장 용량을 모두 개선할 수 있다.
  - 인덱스에 있는 로우의 개수가 적기 때문에 필요한 I/O 작업도 적다.
- 필터링된 인덱스는 인덱스를 만들 때 `WHERE` 절을 추가해서 생성한다.
  - 데이터의 전체 값 중 차지하는 비율이 적고 `WHERE` 절에서 빈번히 사용되는 값이 있을 때, 전통 인덱스에 비해 성능이 월등히 좋아진다.
- 필터링된 인덱스를 만들 때 사용하는 `WHERE` 절에서는 결정적 함수만 사용할 수 있고 `OR` 연산자는 사용할 수 없다.
  - `SQL Server` 의 경우 필터 조건에 계산 컬럼, UDT(User Defined Type 사용자 정의 타입) 컬럼, 공간 데이터 타입 컬럼, hierarchyID 데이터 타입 컬럼 `BETWEEN`, `NOT IN`, `CASE` 문을 사용할 수 없다.

```tsql
CREATE NONCLUSTERED INDEX LowProducts
    ON Products (ProductNumber)
    WHERE QuantityOnHand < 10;
```
- 필터링이 적용된 컬럼은 인덱스에 포함할 필요가 없다.
- `QuantityOnHand` 는 필터링의 조건으로 사용되지만 인덱스에 포함할 필요가 없다.

```tsql
CREATE NONCLUSTERED INDEX PendingDocuments
    ON DocumentStatus (DocumentNumber, Status)
    WHERE Status IN ('Pending publication', 'Pending expiration');

CREATE NONCLUSTERED INDEX PendPubDocuments
    ON DocumentStatus (DocumentNumber, Status)
    WHERE Status = 'Pending publication';

CREATE NONCLUSTERED INDEX PendExpDocuments
    ON DocumentStatus (DocumentNumber, Status)
    WHERE Status = 'Pending expiration';
```
- `DocumentStatus` 테이블의 `Status` 컬럼에 여러 값들이 있을 것이고 이 상태를 검색해야할 때 생성할 수 있는 인덱스 예제이다.
 
```sql
-- 정렬 연산이 필요한 쿼리
SELECT ProductNumber, ProductName
FROM Products
WHERE CategoryID IN (1, 5, 9)
ORDER BY ProductName;

-- 정렬 연산을 피하기 위한 필터링된 인덱스
CREATE INDEX SelectProducts
  ON Products (ProductName, ProductNumber) WHERE CategoryID IN (1,5,9);
```
- 필터링된 인덱스를 사용하면 `ORDER BY` 절을 만족시켜야 하는 정렬 연산을 회피하는 데 인덱스를 사용하는 개념에서 더 확장시킬 수 있다.

## 한계
- 필터링된 인덱스는 `GETDATE()` 같은 날짜 함수를 사용할 수 없다.
- 날짜 컬럼에 특정한 범위 조건을 줄 수 없다.
- `WHERE` 절에서 사용하는 값과 정확히 일치하는 조건만 줄 수 있다.

## 정리
- 필터링된 인덱스는 적은 비율의 로우에 인덱스를 사용할 때 저장 용량을 절약할 수 있어 유용하다.
- 필터링된 인덱스는 로우의 하위 집합에서 유일한 제약 조건을 구현하는 데 사용할 수 있다.
  - ex) `WHERE active = 'Y'` 를 만족하는 로우
- 필터링된 인덱스는 정렬 연산을 피하는 데 사용할 수 있다.
- 테이블 파티셔닝이 다른 인덱스 관리의 오버헤드 없이 필터링된 인덱스와 비슷한 혜택을 줄 수 있는지 고려한다.
