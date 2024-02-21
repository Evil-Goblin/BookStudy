## 반복 그룹
**Assignments**

| ID | DrawingNumber | Predecessor_1 | Predecessor_2 | Predecessor_3 | Predecessor_4 | Predecessor_5 |
|----|---------------|---------------|---------------|---------------|---------------|---------------|
| 1  | LO542B2130    | LS01847409    | LS02390811    | LS02390813    | LS02390817    | LS02390819    |
| 2  | LO426C2133    | LS02388410    | LS02495236    | LS02485238    | LS02495241    | LS02640008    |
| 3  | LO329W2843-1  | `LS02388418`  | LS02640036    | `LS02388418`  |               |               |
| 4  | LO873W1842-1  | LS02388419    | LS02741454    | LS02741456    | LS02769388    |               |
| 5  | LO690W1906-1  | LS02742130    |               |               |               |               |
| 6  | LO217W1855-1  | LS02388421    | LS02769390    |               |               |               |
- `DrawingNumber` 가 `Predecessor` 다섯 개와 연관되어있다.
- 도면 번호와 선행 번호 값 사이가 일대다 관계이다.
- 이 예제는 `Predecessor` 란 단일 속성이 반복 그룹임을 보여준다.
  - 또한 `ID` 가 3인 레코드에는 의도하지 않은 중복 값이 들어 있다.
- 반복 그룹의 또 다른 예로 1월, 2월, 3월 등 월을 컬럼으로 하는 것을 들 수 있다.
- 하지만 반복 그룹이 단일 속성에만 국한되는 것은 아니다.
- `Qantity1` , `ItemDescription1` , `Price1` , `Qantity2` , `ItemDescription2` , `Price2` , `Qantity3` , `ItemDescription3` , `Price3` , ... , `QantityN` , `ItemDescriptionN` , `PriceN`
  - 이 또한 반복 그룹 패턴이다.
- 반복 그룹은 쿼리를 만들어 속성별로 묶은 보고서를 생성하기가 어렵다.
  - 위의 내용에서 나중에 `Predecessor` 값을 추가하거나 이미 있는 `Predecessor` 를 제거할 일이 발생한다면 지금 설계로는 컬럼을 추가하거나 제거해야 한다.
  - 또 이 테이블의 데이터를 기준으로 만든 모든 쿼리(뷰), 보고서의 설계도 수정해야 한다.
  - **영향도(비용) 측면에서 컬럼은 비싸고 로우는 싸다.**
- 향후 입력될 유사한 데이터로 컬럼을 추가하거나 제거하도록 테이블을 설계했다면 주의해야 한다.
  - 필요할 때 로우를 추가하거나 제거하도록 설계하는 것이 훨씬 바람직하다.
- 이 예제에서는 `ID` 값을 외래키로 사용하는 `Predecessors` 테이블을 생성하는 것이 좋다.
  ![Drawings_Predecessors_ERD](https://github.com/Evil-Goblin/BookStudy/assets/74400861/b445d2fa-3313-48c2-b0c1-d7b08ff95f17)

```sql
SELECT ID AS DrawingID, Predecessor_1 AS Predecessor
FROM Assignments WHERE Predecessor_1 IS NOT NULL
UNION
SELECT ID AS DrawingID, Predecessor_2 AS Predecessor
FROM Assignments WHERE Predecessor_2 IS NOT NULL
UNION
SELECT ID AS DrawingID, Predecessor_3 AS Predecessor
FROM Assignments WHERE Predecessor_3 IS NOT NULL
UNION
SELECT ID AS DrawingID, Predecessor_4 AS Predecessor
FROM Assignments WHERE Predecessor_4 IS NOT NULL
UNION
SELECT ID AS DrawingID, Predecessor_5 AS Predecessor
FROM Assignments WHERE Predecessor_5 IS NOT NULL
ORDER BY DrawingID, Predecessor;
```
- 반복 그룹을 처리할 때는 `UNION` 쿼리를 사용한다.
- 정규화를 적용한 설계를 할 수 없다면, `UNION` 을 사용해 읽기 전용 뷰를 만들어 데이터를 '정규화'할 수 있다.
  - 위의 쿼리처럼 `UNION` 으로 쿼리를 덧붙여 `Predecessors` 테이블에 레코드를 추가하는 효과를 낼 수 있다.
- `UNION` 을 사용하려면 각 `SELECT` 문에서 사용되는 컬럼의 데이터 타입이동일해야 하며 나열 순서도 같아야 한다.
  - 첫 번째 `SELECT` 이후에는 `AS DrawingID` , `AS Predecessor` 를 붙일 필요가 없다.
  - `UNION` 은 맨 첫 번째 `SELECT` 문에 명시된 컬럼 이름을 취한다.
- 각 `SELECT` 문의 `WHERE` 절에 다른 조건을 붙이는 것도 가능하다.
- `UNION` 쿼리에서 `ORDER BY` 절은 마지막 `SELECT` 에서만 사용할 수 있다.
  - `ORDER BY 1, 2` 형태로 정렬 순서를 지정할 수도 있다.

## 정리
- 데이터베이스 정규화의 목표는 데이터의 반복 그룹을 제거하고 스키마 변경을 최소화하는 것이다.
- 데이터의 반복 그룹을 제거하면 인덱싱을 사용해 데이터 중복을 방지할 수 있고 쿼리도 간소화할 수 있다.
- 데이터의 반복 그룹을 제거하면 테이블 설계가 더 유연해진다.
  - 새로운 그룹을 추가할 때 테이블 설계를 바꿔서 새 컬럼을 추가하는 것이 아니라 단순히 또 다른 로우만 추가하면 되기 때문이다.
