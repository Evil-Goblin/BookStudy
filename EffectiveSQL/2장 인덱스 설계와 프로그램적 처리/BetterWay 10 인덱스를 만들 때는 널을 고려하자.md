## NULL
- 관계형 데이터베이스에서 NULL 은 '미지의 값', 컬럼에 데이터가 들어 있지 않음을 나타내는 특별한 값이다.
- NULL 은 다른 값, 또는 다른 NULL 과도 동등이나 비동등 연산이 불가능하다.
- NULL 인지 알기 위해 `IS NULL` 연산자를 사용해야 한다.

## INDEX
- 일반적으로 `WHERE` 절의 조건에 자주 사용되는 컬럼 또는 컬럼의 조합에 인덱스를 만들어서 쿼리 성능을 향상시킨다.
- 때문에 컬럼에 인덱스를 만들 때 해당 컬럼 값이 NULL 인지, NULL 값을 어떻게 처리하는지 고려해야 한다.
- 인덱스로 생성된 컬럼 값이 대부분 NULL 이라면 항상 NULL 이 아닌 값을 조회하지 않는 한 해당 인덱스는 많이 사용하지 않을 것이다.
  - 데이터베이스 시스템이 인덱스에서 NULL 을 제외하는 방법을 제공하지 않는다면 인덱스는 저장 공간만 낭비하게 된다.
  - 일부 데이터베이스 시스템은 빈 문자열을 NULL 로 처리하기 때문에 컬럼에 인덱스를 만들지 여부를 결정하기 어렵다.
- 데이터베이스 시스템마다 인덱스에 있는 NULL 값을 처리하는 방식이 다르다.
  - 모든 주요 데이터베이스 시스템의 공통적인 특성은 기본키에 속한 컬럼에 NULL 값을 허용하지 않는다.

## 데이터베이스별 NULL, 빈문자열 처리방식

### DB2
- `DB2` 기본키를 제외한 모든 인덱스에서 NULL 값을 인덱스 처리한다.

```db2
CREATE UNIQUE INDEX ProductUPC_IDX
    ON Products (ProductUPC)
    EXCLUDE NULL KEYS;
```
- 인덱스를 만들 때 `EXCLUDE NULL KEYS` 옵션을 명시하여 유일인덱스에서 명시적으로 NULL 값을 제거할 수 있다.
- 인덱스의 본래 목적대로 `DB2` 는 모든 NULL 값이 동일하다고 여긴다.
  - 유일 인덱스에 `WHERE NOT NULL` 을 명시하지 않은 채 인덱스 컬럼에 NULL 값을 가진 로우 두 개 이상 입력하면 중복 값 오류가 발생한다.
  - NULL 값을 가진 두번째 로우는 중복으로 인식되며, 유일 인덱스는 중복 값을 허용하지 않는다.

```db2
CREATE INDEX CustPhone_IDX
    ON Customers (CustPhoneNumber)
    EXCLUDE NULL KEYS;
```
- 비유일 인덱스에 `EXCLUDE NULL KEYS` 옵션을 추가하면 NULL 값이 인덱스에 저장되지 않는다.
  - 컬럼 대다수의 값이 NULL 인 경우 유용하다.
  - 인덱스에 NULL 이 없기 때문에 `IS NULL` 조건을 검사할 때 인덱스 대신 테이블 전체를 스캔한다.
  - 인덱스에서 NULL 값을 제거하면 인덱스 저장 공간을 저장할 수 있다.
- `DB2` 는 `VARCHAR` 와 `CHAR` 타입 컬럼에 있는 빈 문자열을 NULL 값으로 처리하지 않는다.
  - 하지만 `LUW`(리눅스, 유닉스, 윈도우)용 `DB2` 에선 오라클 호환 옵션을 설정하면 `VARCHAR` 타입 컬럼에 들어오는 빈 문자열을 NULL 값으로 저장한다.

### 액세스
- 액세스는 인덱스로 NULL 값을 가질 수 있다.
  - 기본키는 NULL 값을 포함할 수 없으므로 기본키 컬럼에는 NULL 을 저장할 수 없다.
- 인덱스의 NULL 무시 속성을 설정하면 인덱스에 NULL 값을 저장하지 않는다.
- 액세스는 모든 NULL 값을 동일하지 않다고 처리하기 때문에 유일 인덱스 컬럼에도 NULL 을 가진 로우를 여러 개 저장할 수 있다.
- 액세스는 `Text` 타입 컬럼에 있는 값 끝에 붙은 공백을 자동으로 제거한다.
  - 빈 문자열을 저장하면 NULL 이 저장된다.
  - 기본키 컬럼에 빈 문자열을 저장하면 오류가 발생한다.

![액세스_UI_인덱스_설정](https://github.com/Evil-Goblin/BookStudy/assets/74400861/1145acbd-3bec-47e2-9b0e-51c25b895b0f)
- UI 에서 인덱스 정의시 속성을 저장하는 화면이다.

```sql
CREATE INDEX CustPhoneIndex
    ON Customers (CustPhoneNumber) 
    WITH IGNORE NULL;
```
- `CREATE INDEX` 구문에서 `WITH IGNORE NULL` 을 이용해 NULL 무시 속성을 설정한다.
- `WITH DISALLOW NULL` 을 명시하여 인덱스에 NULL 값이 들어가는 것을 방지할 수 있다.

### SQL Server
- 인덱스에 NULL 값 입력이 가능하고 모든 NULL 을 동일하게 여긴다.
- 기본키 컬럼에 NULL 을 입력하지 못하며 유일키 컬럼에는 NULL 값을 하나만 저장할 수 있다.
- `SQL Server` 는 빈 `VARCHAR` 타입 문자열을 NULL 로 변환하지 않는다.

```tsql
CREATE INDEX CustPhone_IDX
    ON Customers (CustPhoneNumber)
    WHERE CustPhoneNumber IS NOT NULL;
```
- 인덱스에서 NULL 값을 제외하기 위해 필터링된 인덱스를 만든다.
- 쿼리에서 `CustPhoneNumber` 컬럼에 `IS NULL` 조건을 제시하면 `SQL Server` 는 해당 조건 검색을 수행할 때 이 필터링된 인덱스를 사용하지 않는다.
- 빈 문자열이 포함되어도 인덱스를 만들 수 있다.
  - 빈 문자열은 NULL 이 아니기 때문

### MySQL
- 기본키 컬럼에 NULL 값을 허용하지 않지만, 인덱스를 만들 때는 모든 NULL 값이 동일하지 않다고 처리한다.
  - NULL 값이 있는 컬럼에 유일 인덱스를 만들 수 있고, 이 컬럼을 포함한 로우도 여러 개 저장할 수 있다.
- 인덱스에 NULL 값을 허용하므로 NULL 값을 제거하는 옵션이 없다.
- `IS NULL` 과 `IS NOT NULL` 조건을 검사할 때 인덱스가 있으면 사용한다.
- 빈 문자열을 NULL 로 변환하지 않는다.
  - NULL 길이를 반환한 결과는 NULL
  - 빈 문자열 길이는 0 이다.

### 오라클
- 인덱스에 NULL 값을 허용하지 않는다.
- 기본키 컬럼에도 NULL 값을 넣을 수 없다.
- 여러 컬럼으로 된 복합키에 NULL 이 아닌 컬럼이 하나라도 있으면 인덱스를 만들 수 있다.
- 복합키를 구성하는 컬럼의 하나로 상수 값을 포함하거나 NULL 을 처리할 수 있는 함수 기반 인덱스로 NULL 값을 인덱스에 사용하도록 강제할 수 있다.
- 길이가 0인 `VARCHAR` 문자열을 NULL 과 동일하게 인식한다.
  - `CHAR` 타입 컬럼에 빈 문자열을 넣으면 이 컬럼에는 NULL 값이 아닌 공백이 들어간다.
- 모든 NULL 값이 동일하지 않다고 처리한다.
  - 길이가 0인 `VARCHAR` 문자열을 NULL 과 동일하게 인식한다.

```oracle
CREATE INDEX CustPhone_IDX
    ON Customers (CustPhoneNumber ASC, 1);
```
- NULL 값을 가질 수 있는 컬럼을 포함하는 복합 인덱스에 상수 값을 추가한다.

```oracle
CREATE INDEX CustPhone_IDX
    ON Customers (NVL(CustPhoneNumber, 'unknown'));
```
- `NVL()` 함수를 사용하여 NULL 값을 다른 값으로 대체할 수 있다.
- `NVL()` 함수로 인덱스를 만들면 NULL 값을 검사할 때 반드시 `NVL()` 함수를 사용해야 한다는 단점이 생긴다.
  - `WHERE NVL(CustPhoneNumber, 'unknown') = 'unknown'`

### PostgreSQL
- 기본키에 NULL 값을 넣을 수 없다.
- 모든 NULL 값이 동일하지 않다고 처리한다.
- 유일 인덱스를 만든 후 유일 인덱스 컬럼에 NULL 값을 여러 개 넣을 수 있다.
- 길이가 0인 문자열을 NULL 이나 그 반대로 자동 변환하지 않는다.
  - 이 둘을 다른 값으로 인식한다.

```postgresql
CREATE INDEX CustPhone_IDX
    ON Customers (CustPhoneNumber)
    WHERE CUstPhoneNumber IS NOT NULL;
```
- 인덱스에 NULL 값을 포함할 수 있지만, `WHERE` 조건을 정의하는 방식으로 NULL 값을 제외할 수 있다.

## 정리
- 인덱스로 만들 컬럼에 NULL 값을 허용할지 고려해야 한다.
- NULL 값을 검색하고 싶은데 컬럼 값의 대다수가 NULL 이라면, 이 컬럼은 인덱스로 만들지 않는 것이 낫다.
  - 이때 테이블을 재설계하는 편이 좋다.
- 컬럼에 있는 값을 좀 더 빠르게 검색하고 싶은데 대다수가 NULL 이라면, 데이터베이스가 지원하는 한 NULL 값을 제외하고 인덱스를 만드는 것이 좋다.
- 모든 데이터베이스 시스템은 각기 다른 방식으로 인덱스에 NULL 값을 넣을 수 있도록 지원한다.
  - NULL 값이 포함될 수 있는 컬럼을 인덱스로 만들기 전에 사용 중인 데이터베이스 시스템이 이 기능을 지원하는지 반드시 확인한다.
