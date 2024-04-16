## 사용된 우편 라벨 건너뛰기

```sql
WITH SeqNumTbl AS
         (SELECT 1 AS SeqNum
          UNION ALL
          SELECT SeqNum + 1
          FROM SeqNumTbl
          WHERE SeqNum < 100),
     SeqList AS
         (SELECT SeqNum
          FROM SeqNumTbl)
SELECT ' ' AS CustName,
       ' ' AS CustStreetAddress,
       ' ' AS CustCityState,
       ' ' AS CustZipCode
FROM SeqList
WHERE SeqNum <= 3
UNION ALL
SELECT CONCAT(C.CustFirstName, ' ', C.CustLastName) AS CustName,
       C.CustStreetAddress,
       CONCAT(C.CustCity, ', ', C.CustState, ' ', C.CustZipCode)
                                                    AS CustCityState,
       C.CustZipCode
FROM Customers AS C
ORDER BY CustZipCode;
```
- 빈 라벨을 건너뛰는 목록을 생성하는 쿼리이다.
- 이 쿼리는 이전 'BetterWay 42 가능하면 서브쿼리 대신 공통 테이블 표현식을 사용하자.' 의 내용을 기반으로 CTE 를 사용해 해결하였다.
- 이 예제에서 사용된 라벨이 세 개라고 가정한다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/a8268268-e5e9-41fb-a373-05fbf419f755)
- 위 쿼리의 조회 결과이다.
- 빈 로우 세 개 다음에 데이터가 출력되었다.
- UNION 을 사용하는 것보다 UNION ALL 을 사용하는 것이 효율적이기 때문에 사용한다.
  - UNION 을 사용하면 데이터베이스는 중복된 데이터를 확인해 제거하는 추가 작업을 수행한다.

```sql
SELECT ' ' AS CustName,
       ' ' AS CustStreetAddress,
       ' ' AS CustCityState,
       ' ' AS CustZipCode
FROM ztblSeqNumbers
WHERE Sequence <= 3
UNION ALL
SELECT CONCAT(C.CustFirstName, ' ', C.CustLastName) AS CustName,
       C.CustStreetAddress,
       CONCAT(C.CustCity, ', ', C.CustState, ' ', C.CustZipCode)
                                                    AS CustCityState,
       C.CustZipCode
FROM Customers AS C
ORDER BY CustZipCode;
```
- 탤리 테이블을 사용하여 구현한 방법이다.
  - 일련번호가 있는 탤리 테이블을 사용한다.
  - 숫자 1~60이 들어 있는 ztblSeqNumbers 테이블을 활용한다.
- SQL Server 에서는 두 쿼리의 성능 차이를 무시할만하다.
  - Customers 테이블에 고객이 28명만 존재하기 때문일 것이다.
- 일부 시스템에서는 CTE 보다 탤리 테이블을 사용하는 것이 효율적이다.
  - 탤리 테이블의 일련번호 컬럼에 인덱스를 만들 수 있기 때문
- 두 해결책에 대한 SQL 문에 3이라는 값을 직접 넣었다.
  - 하지만 라벨의 개수는 시간이 지나면서 변할 수 있기 때문에 이것을 매개변수로 전달하면 보다 유연한 쿼리가 될 것이다.

```sql
CREATE FUNCTION MailingLabels(@skip AS int = 0)
  RETURNS Table AS RETURN
(SELECT ' ' AS CustName, ' ' AS CustStreetAddress, 
    ' ' AS CustCityState, ' ' AS CustZipCode
FROM ztblSeqNumbers
WHERE Sequence <= @skip
UNION ALL
SELECT  
    CONCAT(C.CustFirstName, ' ', C.CustLastName) AS CustName,
    C.CustStreetAddress,
    CONCAT(C.CustCity, ', ', C.CustState, ' ', C.CustZipCode) 
       AS CustCityState, C.CustZipCode
FROM Customers AS C);

SELECT *
FROM MailingLabels(5)
ORDER BY CustZipCode;
```
- 매개변수로 전달받은 값을 사용해 일련번호를 필터링하고 테이블 형태로 반환하는 함수를 만든다.
- 조회할 때 FROM 절에 함수를 사용하고 매개변수 값을 변경하면 된다.
- 최종 정렬은 이 함수를 호출하는 쿼리에서 수행한다.
  - 대부분의 데이터베이스 시스템은 테이블을 반환하는 함수 내에서는 ORDER BY 절을 허용하지 않는다.

### 테이블 반환 함수
- 테이블 전체를 반환하는 함수는 매우 유용하다.
  - 필터 조건에서 변경되는 변수 값에 의존하는 쿼리를 수행하고 싶을 때, 테이블 반환 함수를 사용하면 복잡한 SQL 은 한 번만 작성하고 매개변수 값으로 필터링된 데이터 집합을 반환할 수 있다.
- FROM 절에서 테이블 참조를 사용하는 곳이라면 어디에나 테이블 반환 함수를 활용할 수 있다.
- 성능 관점에서 테이블 반환 함수가 스칼라 함수를 사용한 SQL 쿼리보다 성능이 좋다.
- 데이터베이스 엔진에 따라 테이블을 조인할 때는 다른 알고리즘을 사용한다.
  - 스칼라 함수를 사용한 SQL 쿼리는 데이터베이스 엔진의 선택권을 심하게 제한할 가능성이 크며, 실용적인 목적에서 데이터베이스 엔진은 이런 쿼리를 완전히 처리해야 사용할 수 있는 블랙박스로 취급한다.
    - 로우별로 한 번 이상 스칼라 함수를 실행해야 하기 때문
  - 테이블 반환 함수는 투명하며, 데이터베이스 엔진이 함수를 파악할 수 있어 더 나은 실행 계획을 만드는 정보로 사용할 수 있다.
    - 이를 '인라이닝(Inlining)'이라고 한다.
- 데이터베이스 엔진은 테이블 반환 함수를 인라인화 할 수 있지만, 스칼라 함수에 의존하는 필터링이나 조인이 있는 쿼리에서는 불가능하다.
- 데이터베이스 시스템이 테이블 반환 함수를 인라인화할 수 있는지 여부는 해당 데이터베이스 관련 문서를 참고하자.

## 정리
- 빈 로우 생성 기능은 유용하다.
  - 특히 보고서 데이터를 뽑을 때 유용하다.
- 빈 로우를 생성할 때 재귀 CTE 나 탤리 테이블을 사용할 수 있다.
  - 일부 경우에는 탤리 테이블을 사용하는 것이 더 빠르다.
- 빈 로우의 개수를 매개변수 값으로 쉽게 전달하려면 이 매개변수를 받는 함수를 생성해 SELECT 문에서 호출한다.
