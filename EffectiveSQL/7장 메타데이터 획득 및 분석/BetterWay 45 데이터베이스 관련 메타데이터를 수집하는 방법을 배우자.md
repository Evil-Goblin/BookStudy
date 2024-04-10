## 메타 데이터
- 메타 데이터는 '데이터의 데이터'를 의미한다.
- SQL 표준 중 하나로 `ISO/IEC 9075-11:2011 Part11: Information and Definition Schemas(SQL/Schemata)` 가 있다.
  - 이 표준은 INFORMATION_SCHEMA 를 정의한다.
  - 데이터베이스와 객체 정보를 SQL 로 참조하기 위해 도입되었다.
- 데이터베이스 논리 모델이 이상적으로 설계되어 있다면 적절한 물리적 모델을 만들 수 있지만, 실제로 설계대로 구현되었는지 확인하는 것도 필요하다.
  - 물리적인 데이터 모델이 DBMS 에 맞게 구현된다는 것은 테이블, 컬럼, 뷰 같은 각 객체가 데이터베이스에 생성되어 있을 뿐만 아니라, 데이터베이스 시스템이 이런 객체 정보를 시스템 테이블에 저장한다는 것을 의미한다.
  - 일련의 읽기 전용 뷰가 이런 시스템 테이블에 존재하는데, 이 뷰는 데이터베이스 객체를 재생성하는 데 필요한 모든 테이블, 뷰, 커럶, 프로시저, 제약 조건, 다른 모든 것 등 정보를 제공한다.

> INFORMATION_SCHEMA 는 SQL 표준이기는 하지만 Access, Oracle 은 제공하지 않는다.  
> Oracle 은 동일한 목적을 수행하는 내부 메타 데이터를 제공한다.

```sql
SELECT t.TABLE_NAME, t.TABLE_TYPE
FROM INFORMAION_SCHEMA.TABLES AS t
WHERE t.TABLE_TYPE IN ('BASE TABLE', 'VIEW');
```
- INFORMATION_SCHEMA.TABLES 를 조회하여 데이터베이스에 존재하는 테이블과 뷰 목록을 가져온다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/b66b14c7-ba86-46ee-b671-da7eedbacc36)
- 위의 INFORMATION_SCHEMA.TABLES 조회 결과이다.

```sql
SELECT tc.CONSTRAINT_NAME, tc.TABLE_NAME, tc.CONSTRAINT_TYPE
FROM INFORMATION_SCHEMA.TABLE_CONSRAINTS AS tc;
```
- TABLE_CONSTRAINTS 뷰를 조회하여 테이블에 생성된 각종 제약 조건 목록을 가져올 수 있다.

![제약_조건_조회](https://github.com/Evil-Goblin/BookStudy/assets/74400861/34b0ff52-96fa-4baa-a7e9-f83f697d0136)
- 제약 조건 목록 조회 결과이다.

```sql
SELECT T.TABLE_NAME
FROM (SELECT TABLE_NAME
      FROM INFORMATION_SCHEMA.TABLES
      WHERE TABLE_TYPE = 'BASE TABLE') AS T
       LEFT JOIN
     (SELECT TABLE_NAME, CONSTRAINT_NAME, CONSTRAINT_TYPE
      FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
      WHERE CONSTRAINT_TYPE = 'PRIMARY KEY') AS TC
     ON T.TABLE_NAME = TC.TABLE_NAME
WHERE TC.TABLE_NAME IS NULL;
```
- 만약 모든 테이블과 이 테이블에 정의된 모든 제약 조건을 알고 있는 상태라면 기본키가 없는 테이블을 골라낼 수 있다.

![기본키가_없는_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/c4771c72-049e-45ce-83a5-0150d0296021)
- 기본키가 없는 테이블의 조회 결과이다.

```sql
SELECT VCU.VIEW_NAME, VCU.TABLE_NAME, VCU.COLUMN_NAME
FROM INFORMATION_SCHEMA.VIEW_COLUMN_USAGE AS VCU;
```
- 뷰에 있는 특정 컬럼을 변경해야 한다면 INFORMATION_SCHEMA.VIEW_COLUMN_USAGE 뷰를 사용할 수 있다.
- 이 뷰는 어떤 뷰에서든 사용하는 테이블 컬럼 정보를 담고 있다.

![뷰_테이블_컬럼_조회](https://github.com/Evil-Goblin/BookStudy/assets/74400861/bae8daeb-572d-4a60-a29e-34d8a2e95667)
- 뷰에서 사용하는 테이블과 컬럼 목록 조회 결과이다.
- 뷰를 생성할 때 컬럼 이름에 별칭을 썼는지 여부나 컬럼을 WHERE, ON 절에서만 사용했는지 여부는 중요하지 않다.
  - 이런 정보는 테이블이나 컬럼을 변경할 때 영향을 미치는 객체 정보를 재빨리 파악하는 데 도움이 된다.

```sql
CREATE VIEW BeerStyles AS
SELECT Cat.CategoryDS AS Category, Cou.CountryNM AS Country, Sty.StyleNM AS Style, Sty.ABVHighNb AS MaxABV
FROM Styles AS Sty
       INNER JOIN Categories AS Cat
                  ON Sty.CategoryFK = Cat.CategoryID
       INNER JOIN Countries AS Cou
                  ON Sty.CountryFK = Cou.CountryID;
```
- BeerStyles 뷰를 생성하는 SQL 문이다.
- INFORMATION_SCHEMA.VIEW_COLUMN_USAGE 뷰는 CREATE VIEW 문에서 SELECT, ON 절을 비롯해 다른 곳에서 사용된 모든 컬럼 정보를 제공한다.

## INFORMATION_SCHEMA 사용시 장단점
- INFORMATION_SCHEMA 는 SQL 표준이므로 특정 DBMS 에서 작성한 쿼리를 다른 DBMS 나 버전에서도 사용할 수 있다.
- 하지만 DBMS 에 따라 일관성 있게 구현되지 않았다.
  - INFORMATION_SCHEMA.VIEW_COLUMN_USAGE 뷰는 MySQL 에는 없고 SQL Server, PostgreSQL 에만 있다.
- INFORMATION_SCHEMA 가 표준이기 때문에 표준에 있는 기능에 한해서만 해당 정보를 알 수 있다.
- 심지어 표준에 명시되었다 하더라도 INFORMATION_SCHEMA 에서 해당 기능 정보를 제공하지 못할 수도 있다.
  - 유일 인덱스를 참조하는 외래키를 생성할 때를 예로 들 수 있다.
  - 보통 외래키 정보는 INFORMATION_SCHEMA 의 REFERENTIAL_CONSTRAINTS, TABLE_CONSTRAINTS, CONSTRAINT_COLUMN_USAGE 뷰를 조인해서 가져온다.
  - 하지만 유일 인덱스는 제약 조건에 해당하지 않으므로 TABLE_CONSTRAINTS 에 해당 데이터가 없어 어느 컬럼이 이 '제약 조건'에 사용되었는지 알 수 없다.
- 다행히 모든 DBMS 는 다른 메타데이터 원천 정보를 제공하는데 이것으로 원하는 정보를 가져올 수 있다.
  - 물론 각 DBMS 별 메타데이터를 다른 DBMS 에서는 제공하지 않을 수도 있다는 단점이 있다.

```tsql
SELECT name, type_desc
FROM sys.objects
WHERE type_desc IN ('USER_TABLE', 'VIEW');
```
- SQL Server 의 시스템 테이블로부터 테이블과 뷰 목록을 가져오는 쿼리이다.
- 이전에 작성했었던 테이블과 뷰 콕록을 가져오는 쿼리와 동일한 정보를 반환한다.

```sql
SELECT name, type_desc
FROM sys.tables
UNION
SELECT name, type_desc
FROM sys.views;
```
- SQL Server 의 다른 시스템 테이블로 부터 동일한 정보를 조회하는 쿼리이다.

> 객체의 스키마 정보를 참조할 때 INFORMATION_SCHEMA 뷰는 사용하지 말자.  
> 객체의 스키마 정보를 찾는 유일하게 신뢰할 수 있는 방법은 sys.objects 카탈로그 뷰를 조회하는 것이다.  
> INFORMATION_SCHEMA 뷰는 새로운 기능을 모두 반영하도록 갱신되지 않으므로 불완전한 데이터를 보여 줄 수 있다.

## 정리
- 가능하면 SQL 표준인 INFORMATION_SCHEMA 뷰들을 사용한다.
- INFORMATION_SCHEMA 는 DBMS 마다 다를 수 있다.
- 사용 중인 DBMS 에서 메타 데이터를 조회하는 데 사용할 수 있는 비표준 명령을 학습한다.
- INFORMATION_SCHEMA 가 필요한 메타 데이터를 100% 포함하지 않는다는 점을 명심하고, 사용 중인 DBMS 와 관련된 시스템 테이블을 파악해 두자.
