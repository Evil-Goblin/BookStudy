## SQL
- SQL 은 데이터베이스에 접근할 수 잇는 표준 언어로 알려져 있다.
- SQL 은 1986년에야 비로소 ANSI 표준이 되었고, 1987년에는 ISO 표준이 되었다.
- 그러나 SQL 구현체에서 표준을 완벽히 따라야 하는 것은 아니라서 데이터 베이스 종류에 따라 호환되지 않기도 한다.
  - 날짜와 시간에 대한 구문, 문자열 결합, NULL 처리, 대소문자 구분은 DBMS 별로 다르다.
- 효율적으로 SQL 문을 작성하려면 DBMS 가 어떤 종류의 SQL 을 사용하는지 이해해야 한다.

> http://troels.arvin.kd/db/rdbms/  
> 서로 다르게 구현된 SQL 을 비교해 놓은 내용

## 결과 집합 정렬
- SQL 표준에서는 다음 내용을 제외하고 NULL 과 NULL 이 아닌 값의 정렬 방법은 명시하지 않는다.
  - 임의의 NULL 값 두 개는 정렬 순서가 같다.
  - NULL 은 NULL 이 아닌 값의 상위나 하위로 정렬해야 한다.
- DBMS 별 차이
  - DB2
    - NULL 은 NULL 이 아닌 값보다 정렬 순서가 높다.
  - Access
    - NULL 은 NULL 이 아닌 값보다 정렬 순서가 낮다.
  - SQL Server
    - NULL 은 NULL 이 아닌 값보다 정렬 순서가 낮다.
  - MySQL
    - NULL 은 NULL 이 아닌 값보다 정렬 순서가 낮다.
    - 컬럼 이름 앞에 -(마이너스) 문자를 추가하면 ASC 는 DESC 로 바뀌고 DESC 는 ASC 로 바뀐다고 한다.
  - Oracle
    - NULL 은 NULL 이 아닌 값보다 정렬 순서가 높다.
    - `ORDER BY` 절에서 `NULLS FIRST` 나 `NULLS LAST` 를 추가하면 정렬 동작이 달라진다.
  - PostgreSQL
    - NULL 은 NULL 이 아닌 값보다 정렬 순서가 높다.
    - 8.3 버전부터 `ORDER BY` 절에서 `NULLS FIRST` 나 `NULLS LAST` 를 추가하면 정렬 동작이 달라진다.

## 반환 결과 집합의 개수 제한
- SQL 표준은 반환되는 로우의 개수를 제한하는 세 가지 방법을 제공한다.
  - `FETCH FIRST` 사용
  - `ROW_NUMBER() OVER` 같은 윈도우 함수 사용
    - `ROW_NUMBER() OVER`
      - 각 `PARTITION` 내에서 `ORDER BY` 절에 의해 정렬된 순서를 기준으로 고유한 값을 반환하는 함수
      - `ROW_NUMBER() OVER(PARTITION BY [그룹핑할 컬럼] ORDER BY [정렬할 컬럼])`
        - `PARTITION BY` 는 선택, `ORDER BY` 는 필수
      - 간단히 말하면 조회된 결과에 번호를 매긴다고 볼 수 있을 것 같다.
      - 파티션이 있는 경우 파티션 별로 각각 번호가 매겨진다.
  - 커서 사용

> 여기서의 '제한'은 결과 집합에서 로우를 n개만 뽑아내는 것으로 TOP-n 쿼리를 가리키는 것은 아니다.

- DBMS 별 구현
  - DB2
    - 표준 기반의 접근 방법을 모두 지원한다.
  - Access
    - 아무것도 지원하지 않는다.
  - SQL Server
    - `ROW_NUMBER()` 와 표준 기반의 커서만 지원한다.
  - MySQL
    - 표준 기반의 커서와 LIMIT 연산자로 지원한다.
  - Oracle
    - `ROW_NUMBER()` 와 표준 기반의 커서뿐만 아니라 `ROWNUM` 의사 컬럼을 이용한 방법도 지원한다.
  - PostgreSQL
    - 표준 기반의 접근 방법을 모두 지원한다.

## BOOLEAN 데이터 타입
- SQL 표준은 BOOLEAN 타입을 선택 옵션으로 명시하고, BOOLEAN 타입은 다음 값을 가질 수 있다고 명시했다.
  - TRUE
  - FALSE
  - UNKNOWN 또는 NULL (NOT NULL 제약 조건으로 걸리지 않는다면)
- DBMS 는 NULL 을 UNKNOWN 으로 해석한다.
- TRUE 가 FALSE 보다 큰 값으로 정의되어 있다.
- DBMS 별 구현
  - DB2
    - BOOLEAN 타입을 지원하지 않는다.
  - Access
    - NULL 이 아닌 Yes/No 타입을 제공한다.
  - SQL Server
    - BOOLEAN 타입을 지원하지 않지만, 대안으로 BIT 타입(0, 1, NULL 의 값을 가질 수 있다.)이 있다.
  - MySQL
    - BOOLEAN 타입을 지원하지만 표준을 따르지도 않는다(TINYINT(1) 타입의 별칭이다.).
  - Oracle
    - BOOLEAN 타입을 지원하지 않는다.
  - PostgreSQL
    - 표준을 따른다.
    - NULL 을 BOOLEAN 값으로 쓸 수 있지만 UNKNOWN 은 사용할 수 없다.

## UNIQUE 제약 조건
- SQL 표준에서는 DBMS 가 선택적 기능인 'NULL 허용'을 구현하지 않았다면 UNIQUE 제약 조건에 종속되는 컬럼이나 컬럼 집합은 NOT NULL 종속해야 한다고 명시한다.
  - UNIQUE 제약 조건이 있는 컬럼에는 NOT NULL 제약 조건이 있어야 하지만, 꼭 그럴 필요는 없다.
  - UNIQUE 제약 조건이 있는 컬럼에 NOT NULL 제약 조건이 없다면 이 컬럼은 NULL 값을 여러개 가질 수 있다.
- DBMS 별 구현
  - DB2
    - UNIQUE 제약 조건의 비선택적 부분을 따른다.
    - 'NULL 허용' 기능은 구현되지 않았다.
  - Access
    - 표준을 따른다.
  - SQL Server
    - 'NULL 허용' 기능을 제공하지만 NULL 값을 하나만 가질 수 있다.(표준의 두 번째 특징을 침해한다.)
  - MySQL
    - 'NULL 허용' 기능을 포함해 표준을 따른다.
  - Oracle
    - 'NULL 허용' 기능을 제공한다.
    - 단일 컬럼에 UNIQUE 제약 조건이 있을 때 이 컬럼은 다수의 NULL 값을 가질 수 있다.
    - 하지만 여러 컬럼에 UNIQUE 제약 조건이 명시되어 있을 때 로우 두개가 적어도 한 컬럼은 NULL 을, 나머지 컬럼에는 NULL 이 아닌 동일 값을 담고 있다면 이 제약 조건이 침해된 것으로 본다.
  - PostgreSQL
    - 'NULL 허용' 기능을 포함해 표준을 따른다.

## 정리
- SQL 문이 SQL 표준을 따르더라도 DBMS 에 따라 그 동작이 다를 수 있다.
- DBMS 별로 SQL 구현 내용이 다르므로 동일한 SQL 문이라도 성능은 다르다.
- 사용하는 DBMS 문서를 참고하는 습관을 들인다.
- SQL 차이점은 http://troels.arvin.kd/db/rdbms/ 을 참고한다.
