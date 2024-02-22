## 관계형 용어에서 관계(테이블)는 오직 한 주제나 액션만 기술해야 한다.
- 속성(컬럼)은 관계로 정의된 주제를 기술하는 유일한 특성과 관련된 데이터를 포함한다.
- 속성은 다른 관계의 속성을 포함하는 외래키가 될 수 있고, 이 외래키는 다른 관계에 있는 일부 튜플(로우)과 연관성을 제공한다.
- 단일 컬럼에 특성 값을 두 개 이상 저장하는 것은 좋지 않다.
  - 검색을 하거나 값을 집계할 때 특성 값을 분리하기가 어렵다.
  - 중요한 개별 특성은 자체 컬럼에 넣는 것을 고려해야 한다.
- 속성을 식별할 때는 어느 부분을 미세하게 쪼갤 것인지 신중히 검토해야 한다.
- 특성을 개별 컬럼으로 분할하면 개별 데이터 검색이나 그루핑을 수행하기 쉽다.
  - 보고서나 목록이 필요할 때도 간단히 분할된 데이터 조각들을 재결합할 수 있다.

### 여러 컬럼에 여러 속성이 있는 경우 문제점
![여러_칼럼_여러_속성](https://github.com/Evil-Goblin/BookStudy/assets/74400861/fc8c874e-ff39-48e0-bf46-07923695a525)
- 이 테이블은 다음과 같은 문제가 있다.
1. 성을 찾기가 어렵다.
   - 성이 `Smith` 인 사람을 찾으려고 할 때 `LIKE` 연산자와 와일드카드 `%`를 사용하면 성이 `Smithson` , `Blacksmith` 인 사람도 검색될 수 있다.
2. 이름을 검색할 때 효율성이 떨어지는 `LIKE` 나 `substring` 연산자를 사용해 이름을 추출해야 한다.
3. 거리 이름, 도시, 주, 우편번호를 쉽게 찾을 수 없다.
4. 데이터를 그룹으로 묶으면 그루핑된 데이터에서 우편번호, 주, 국가를 추출하기가 매우 어렵다.

### 속성을 분할
![컬럼당_특성_한개01](https://github.com/Evil-Goblin/BookStudy/assets/74400861/de991e19-5e8d-4370-af41-fe99fd59dc24)
![컬럼당_특성_한개02](https://github.com/Evil-Goblin/BookStudy/assets/74400861/bdacab19-5e36-4a9a-b8b7-d4fe100d5a87)
```sql
CREATE TABLE Authors (
    AuthorIo    int IDENTITY(1, 1),
    AuthFirst   varchar(20),
    AuthMid     varchar(15),
    AuthLast    varchar(30),
    AuthStNum   varchar(6),
    AuthStreet  varchar(40),
    AuthCity    varchar(30),
    AuthStProv  varchar(2),
    AuthPostal  varchar(10),
    AuthCountry varchar(35)
);
INSERT INTO Authors (AuthFirst, AuthMid, AuthLast, AuthStNum, AuthStreet, AuthCity, AuthStProv, AuthPostal, AuthCountry)
VALUES ('Douglas', 'J.', 'Steele', '555', 'Sherbourne St.', 'Toronto', 'ON', 'M4X 1W6', 'Canada');
```
- 이와 같이 컬럼당 특성 하나로 테이블을 분리할 수 있다.
- 이를 통해 하나 이상의 개별 특성에서 검색이나 그루핑을 쉽게 할 수 있다.

```sql
SELECT 
    AuthorId as AuthID, 
    CONCAT(AuthFirst,
       CASE 
        WHEN AuthMid IS NULL
        THEN ' '
        ELSE CONCAT(' ', AuthMid, ' ')
       END, AuthLast) AS AuthName,
    CONCAT(AuthStNum, ' ', AuthStreet, ' ', AuthCity, ', ', AuthStProv, ' ', AuthPostal, ', ', AuthContry) AS AuthAddress
FROM Authors;
```
- 원래의 데이터를 생성하는 SQL 문이다.

## 정리
- 올바른 테이블 설계는 개별 특성을 자체 컬럼에 할당한다.
  - 한 컬럼에 여러 특성이 포함되어 있으면 검색이나 그루핑 작업이 가능하다고 해도 어렵기 때문이다.
- 일부 어플리케이션에서는 주소나 전화번호 같은 컬럼의 데이터 일부를 걸러 내려면 최소 수준의 데이터 조각으로 분할해야 한다.
- 보고서나 목록을 뽑으려고 특성들을 재결합할 때는 SQL 의 문자열 연결 기능을 사용한다.
