## 쿼리 분석기
- SQL 문을 실행하기 전에 DBMS 의 옵티마이저는 최적으로 수행할 수 있는 방법을 결정한다.
- 실행 계획을 생성해서 이를 결정하게된다.
  - 실행 계획에 따라 SQL 문은 단계별로 수행된다.
- 옵티마이저는 소스 코드를 실행 프로그램으로 변환하는 컴파일러라고 할 수 있다.
  - SQL 문을 실행 계획으로 변환한다.

### DB2
- DB2 에서 실행 계획을 보기 위해서는 특정 시스템 테이블이 필요하다.

```db2
CALL SYSPROC.SYSINSTALLOBJECTS('EXPLAIN', 'C',
     CAST(NULL AS varchar(128)), CAST(NULL AS varchar(128)))
```
- SYSINSTALLOBJECTS 프로시저를 사용해 시스템 테이블을 생성한다.
- SYSTOOLS 스키마에 필요한 테이블을 만들고 SQL 문 앞에 EXPLAIN PLAN FOR 을 추가하여 실행 계획을 볼 수 있다.

```db2
EXPLAIN PLAN FOR SELECT CustomerID, SUM(OrderTotal)
FROM Orders
GROUP BY CustomerID;
```
- EXPLAIN PLAN FOR 를 사용한다고 해서 실제로 실행 계획을 보여주는 것은 아니다.
  - 생성한 실행 계획 테이블(시스템 테이블)에 실행 계획을 저장하는 역할을 한다.

### Access
- Access 에서 실행 계획을 보기 위해 특정 플래그 값을 설정해 쿼리를 컴파일할 때마다 데이터베이스 엔진이 SHOWPLAN.OUT 파일을 생성하게 해야 한다.
- 플래그 값을 설정하는 방법은 버전마다 다르다.
- 플래그 값 설정등은 필요할 때 찾아보도록 해야겠다.(Access 사용하는 경우가 적을 것 같아서 정리를 생략한다.)

### SQL Server
- 쿼리 기반이 아닌 UI 기반이라 직접 사용해보지도 못했는데 정리하기가 조금...
- 필요할 때 찾아보도록 해야겠다.

### MySQL
- EXPLAIN 단어를 SQL 문 앞에 두면 실행 계획을 만들 수 있다.

```mysql
EXPLAIN SELECT CustomerID, SUM(OrderTotal)
FROM Orders
GROUP BY CustomerID;
```
- MySQL 에서 실행 계획을 생성하는 쿼리이다.
- MySQL 은 표 형식으로 실행 계획을 보여준다.
  - MySQL Workbench 의 Visual Explain 기능을 사용해 실행 계획을 그래픽으로 볼 수 있다.

### Oracle
- Oracle 에서 실행 계획을 보려면 다음 두 단계를 수행해야 한다.
  - PLAN_TABLE 에 실행 계획을 저장
  - 보기 형식을 지정해 실행 계획을 본다.
- 실행 계획을 생성하려면 SQL 문 앞에 EXPLAIN PLAN FOR 키워드를 붙인다.

```oracle
EXPLAIN PLAN FOR SELECT CustomerID, SUM(OrderTotal)
FROM Orders
GROUP BY CustomerID;
```
- EXPLAIN PLAN FOR 명령을 실행한다고 해서 실제로 실행 계획이 보이는 것은 아니다.
  - 실행 계획이 PLAN_TABLE 에 저장된다.
- EXPLAIN PLAN FOR 명령은 시스템이 문장을 실행할 때 사용할 실행 계획과 동일한 실행 계획을 생성하지 않을 수도 있다.

```oracle
SELECT * FROM TABLE ( dbms_xplan.display )
```
- Oracle 9iR2 버전에서 소개된 DBMS_XPLAN 패키지는 PLAN_TABLE 에 이쓴ㄴ 실행 계획을 저거절한 형식으로 변환해 보여 준다.
- 위 코드는 현재 데이터베이스 세션에서 생성된 가장 최근의 실행 계획을 보여 준다.

### PostgreSQL
- SQL 문 앞에 EXPLAIN 키워드를 넣어 실행 계획을 볼 수 있다.

```postgresql
EXPLAIN SELECT CustomerID, SUM(OrderTotal)
FROM Orders
GROUP BY CustomerID;
```
- EXPLAIN 키워드 뒤에 다음 옵션 중 하나를 붙여 사용할 수 있다.
  - ANALYZE
    - EXPLAIN 명령을 수행하고 실제 실행 시간과 다른 통계 정보를 보여준다.
    - 기본 값은 FALSE
  - VERBOSE
    - 실행 계획과 관련된 추가 정보를 보여준다.
    - 기본 값은 FALSE
  - COSTS
    - 각 계획에서 측정한 시작 비용과 전체 비용 정보뿐만 아니라, 각 로우의 측정 너비와 로우 개수까지 보여 준다.
    - 기본 값은 TRUE
  - BUFFERS
    - 버퍼 사용 정보를 보여 준다.
    - ANALYZE 옵션이 활성화되었을 때만 사용 가능하다.
    - 기본 값은 FALSE
  - TIMING
    - 실제 시작 시각과 결과를 산출하는 데 소요된 시간을 보여 준다.
    - ANALYZE 옵션이 활성화되었을 때만 사용할 수 있다.
    - 기본 값은 TRUE
  - FORMAT
    - 결과 형식을 지정한다.
    - TEXT, XML, JSON, YAML 을 명시할 수 있다.
    - 기본 값은 TEXT

```postgresql
SET search_path = SalesOrdersSample;

PREPARE stmt (int) AS
SELECT * FROM Customers AS C
WHERE c.CustomerID = $1;

EXPLAIN EXECUTE stmt(1001);
```
- BIND 매개변수($1, $2...) 가 있는 SQL 문은 반드시 준비 작업을 먼저 수행해야 한다.
- 문장의 준비 작업이 끝난 후 EXPLAIN 을 통해 실행 계획을 볼 수 있다.
- pgAdmin 을 통해 실행 계획을 그래픽으로 표현할 수 있다.

## 정리
- 사용 중인 DBMS 에서 실행 계획을 얻는 방법을 알아둔다.
- 실행 계획을 생성하고 해석하는 방법은 DBMS 관련 문서를 참고한다.
- 실행 계획에 나온 정보는 시간이 지나면 바뀔 수 있다.
- DB2 에서는 먼저 시스템 테이블을 생성해야 한다.
  - DB2 는 실행 계획 정보를 시스템 테이블에 저장하고 예상 실행 계획을 생성한다.
- Access 에서는 레지스트리 키를 설치해야 한다.
  - 외부 텍스트 파일에 실행 계획을 저장해 실제 실행 계획을 만든다.
- SQL Server 에서는 실행 계획을 보여주는 초기화 작업이 없고, 그래픽으로 볼지 테이블 형식으로 볼지 선택할 숫 있다.
  - 예상 계획을 생성할지 실제 계획을 생성할지도 선택할 수 있다.
- MySQL 도 실행 계획을 보여 주는 초기화 작업은 없고, 예상 실행 계획을 만들어서 보여준다.
- Oracle 10g 와 그 이후 버전에서는 실행 계획을 보여주는 초기화 작업이 없다.
  - 그 이전 버전에서는 원하는 스키마에 시스템 테이블을 생성해야 했다.
  - 이런 시스템 테이블에는 실행 계획이 저장되어 있으며, 이를 이용해 예상 실행 계획을 만든다.
- PostgreSQL 은 실행 계획을 보여 주는 초기화 작업이 없다.
  - 하지만 BIND 매개변수가 있는 SQL 문은 준비 작업을 해야 한다.
  - 기본 SQL 문에서는 별도의 작업 없이 예상 실행 계획을 만든다.
  - 준비 작업이 필요한 SQL 문의 9.2 미만 버전에서는 예상 실행 계획을 만들지만, 9.2버전부터는 실제 실행 계획을 생성한다.
