## 뷰
- 뷰는 하나 이상의 테이블이나 다른 뷰에 SQL 쿼리를 미리 정의해 놓고 테이블처럼 데이터를 볼 수 있는 객체이다.
- 뷰를 이용해 역정규화에서 이슈 몇가지를 개선할 수 있다.

```sql
CREATE VIEW vCustomers AS
SELECT DISTINCT cs.CustFirstName, cs.CustLastName, cs.Address, cs.City, cs.Phone
FROM CustomerSales AS cs;

CREATE VIEW vAutomobileModels AS
SELECT DISTINCT cs.ModelYear, cs.Model
FROM CustomerSales AS cs;

CREATE VIEW vEmployees AS
SELECT DISTINCT cs.SalesPerson
FROM CustomerSales AS cs;
```
- 이전 `BetterWay 2` 에서 역정규화 하여 분할한 `CustomerSales` 테이블을 서로 다른 뷰로 만든 것이다.

```sql
CREATE VIEW vDrawings AS
SELECT a.ID AS DrawingID, a.DrawingNumber
FROM Assignments AS a;

CREATE VIEW vPredecessors AS
SELECT 1 AS PredecessorID, a.ID AS DrawingID, a.Predecessor_1 AS Predecessor
FROM Assignments AS a
WHERE a.Predecessor_1 IS NOT NULL
UNION
SELECT 2, a.ID, a.Predecessor_2
FROM Assignments AS a
WHERE a.Predecessor_2 IS NOT NULL
UNION
SELECT 3, a.ID, a.Predecessor_3
FROM Assignments AS a
WHERE a.Predecessor_3 IS NOT NULL
UNION
SELECT 4, a.ID, a.Predecessor_4
FROM Assignments AS a
WHERE a.Predecessor_4 IS NOT NULL
UNION
SELECT 5, a.ID, a.Predecessor_5
FROM Assignments AS a
WHERE a.Predecessor_5 IS NOT NULL;
```
- `BetterWay 3` 에서 UNION 쿼리를 사용해 반복 그룹이 있는 테이블을 정규화하였고 이를 뷰로 같은 결과를 낼 수 있다.
- 하지만 뷰는 오직 조회 용도로만 사용할 수 있다.
  - 위 예제에서 각각 DISTINCT 와 UNION 을 사용했기 때문에 이 뷰들은 갱신할 수 없다.
- 이를 해결하기 위해 일부 DBMS 는 뷰에 트리거를 사용해 원본 테이블의 데이터를 수정하는 로직을 작성할 수 있게 하였다.
> DB2, SQL Server, Oracle, PostgreSQL 은 뷰에 트리거를 사용할 수 있지만, MySQL 은 뷰에 트리거를 사용할 수 없다.

### 뷰를 사용하는 이유
- 특정 데이터에 집중
  - 뷰로 특정 작업에 사용하는 특정 데이터에 집중할 수 있다.
  - 이런 뷰는 하나 이상의 테이블에 있는 모든 로우나 WHERE 절로 걸러낸 로우를 반환한다.
  - 하나 이상의 테이블에 있는 컬럼의 일부 집합만 반환할 수도 있다.
- 컬럼 이름을 간소화 또는 명료화
  - 뷰를 사용하면 원래 컬럼 이름 대신 별칭을 사용해 좀 더 의미 있는 이름을 부여할 수 있다.
- 여러 테이블에 있는 데이터를 한눈에 보기
  - 뷰로 여러 테이블 데이터를 결합해 논리적인 단일 레코드로 통합해서 볼 수 있다.
- 데이터 조작 간소화
  - 뷰는 사용자가 데이터로 작업하는 방식을 간소하게 만들 수 있다.
  - 보고서용 복잡한 쿼리를 작성한다고 할 때, 사용자별 서브쿼리, 외부 조인, 일련의 테이블 그룹에서 데이터를 추출해서 집계하는 대신 뷰를 만들어 사용할 수 있다.
    - 데이터 접근을 간단히 할 수 있고, 각 사용자가 해당 쿼리를 생성하도록 강제하지 않고도 데이터의 일관성을 유지할 수 있다.
- 중요 데이터 보호
  - 테이블에 민감하고 즁요한 데이터가 저장되어 있을 때 이 데이터를 뷰로 만들어 뺄 수 있다.
  - 예를 들어 고객의 신용 카드 정보를 노출하는 대신 신용 카드 숫자를 변형해서 실제 숫자는 숨긴 채 기능을 수행하는 함수를 사용하는 뷰를 만들 수 있다.
  - 이런 뷰를 특정 사용자만 볼 수 있게 할 수 있다.(DBMS 마다 다르다.)
  - 뷰는 컬럼이나 로우 수준의 보안 기능을 제공한다.
    - `WITH CHECK OPTION` 절로 사용자가 뷰에 걸린 제약 조건을 위반하는 데이터 삭제나 갱신 작업을 수행하는 것을 차단할 수 있다.
- 하위 호환성 제공
  - 하나 이상의 테이블 스키마를 변경해야 할 때 기존 테이블의 스키마와 동일한 뷰를 만들어 처리할 수 있다.
  - 이를 통해 기존 어플리케이션은 원래 테이블 데이터를 조회하는 쿼리를 수정 없이 사용할 수 있다.
  - 심지어 어플리케이션에서 데이터를 조작할 때라도 `INSTEAD OF` 트리거를 사용하면 주요 테이블의 뷰로 INSERT , UPDATE , DELETE 작업을 할 수 있다.
- 데이터 커스터마이징
  - 뷰를 만들면 여느 사용자가 다른 방식으로 동일한 데이터를, 심지어 똑같은 시간에 다른 방식으로 보게 할 수 있다.
  - 예를 들어 사용자의 로그인 ID 값에 따라 특정 사용자와 관련된 고객 정보를 조회하는 뷰를 만들어 사용할 수 있다.
- 요약 데이터 제공
  - 집계 함수(SUM() , AVERAGE() 등)를 사용한 뷰로 데이터 일부분의 계산 결과 값만 볼 수 있다.
- 데이터 가져오기와 내보내기
  - 뷰를 이용하면 다른 어플리케이션에 데이터를 내보낼 수 있다.
  - 원하는 데이터를 제공하는 뷰를 생성한 후 적당한 유틸리티를 이용해 해당 데이터를 내보낼 수 있다.
  - 가져오기용 뷰를 만들면 주요 테이블에 있는 모든 컬럼이 아닌 필요한 컬럼 데이터만 가져와 사용할 수도 있다.

## 뷰를 참조하는 뷰는 생성하지 말자.
- 뷰를 참조하는 뷰를 생성하는 것은 가능하다.
- 하지만 이는 큰 실수를 할 수 있고, 성능과 관리적 문제점을 일으킨다.
- 뷰의 사용 이점을 모두 갉아먹는 결과만 낳는다.
- 이미 만든 뷰를 다른 형태로 봐야한다면 적절한 필터나 그룹으로 묶어 기준 테이블을 직접 참조하는 새 뷰를 만드는게 좋다.

## 정리
- 사용자에게 직관적인 데이터를 제공하려면 뷰를 사용한다.
- 사용자가 정확히 필요한 데이터를 보거나(종종 수정하거나) 더는 필요 없는 데이터를 보지 않게 제한하려면 뷰를 사용한다.
  - 필요한 경우 `WITH CHECK OPTION` 을 사용한다.
- 복잡한 쿼리를 숨기고 재사용하려면 뷰를 사용한다.
- 여러 테이블에 있는 데이터를 취합해 보고용 데이터를 만들 때는 뷰를 사용한다.
- 뷰를 사용하면 코딩 규칙이나 명명법을 강화할 수 있다.
  - 기존 데이터베이스 설계를 변경해서 작업할 때 특히 유용하다.
