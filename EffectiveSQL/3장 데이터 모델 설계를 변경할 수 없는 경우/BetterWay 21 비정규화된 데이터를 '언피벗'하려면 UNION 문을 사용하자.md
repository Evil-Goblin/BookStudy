## UNION
- 두 개 이상의 SELECT 문으로 데이터 집합을 결합할 때 사용된다.

### UNION 쿼리를 사용할 때 적용되는 기본 규칙
- UNION 쿼리를 구성하는 각 쿼리는 컬럼의 개수가 동일해야 한다.
- 각 쿼리의 컬럼 순서도 일치해야 한다.
- 각 쿼리에서 사용된 커럶의 데이터 타입도 일치하거나 서로 호환해야 한다.

### UNION 예제
![비정규화된_엑셀_데이터](https://github.com/Evil-Goblin/BookStudy/assets/74400861/f2d614a7-60af-49a3-86e4-b877b46966e9)
```sql
SELECT Category, OctQuantity, OctScales
FROM SalesSummary;
```
- 10월 데이터를 추출하는 SQL 이다.
- 다른 달의 데이터는 쿼리를 변경해야 볼 수 있다.

```sql
SELECT Category, OctQuantity, OctScales
FROM SalesSummary
UNION
SELECT Category, NovQuantity, NovScales
FROM SalesSummary
UNION
SELECT Category, DecQuantity, DecScales
FROM SalesSummary
UNION
SELECT Category, JanQuantity, JanScales
FROM SalesSummary
UNION
SELECT Category, FebQuantity, FebScales
FROM SalesSummary;
```
- UNION 을 이용해 정규화된 뷰에 데이터를 합친다.
- UNION 쿼리를 구성하는 각 쿼리에서 사용된 컬럼 이름은 달라도 상관없다.

![UNION_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/b7ea86f1-f4f9-4c05-a92c-9061f25895ec)
- 데이터가 합쳐지긴 했지만 어느 달의 데이터인지 구분할 수 없다.
- 또한 컬럼 이름이 `OctQuantity` 와 같이 설정된다.
  - UNION 쿼리에서 컬럼 이름을 첫 번째 SELECT 문에서 가져왔기 때문

```sql
SELECT Category, 'Oct' AS SalesMonth, OctQuantity AS Quantity, OctScales AS SalesAmt
FROM SalesSummary
UNION
SELECT Category, 'Nov', NovQuantity, NovScales
FROM SalesSummary
UNION
SELECT Category, 'Dec', DecQuantity, DecScales
FROM SalesSummary
UNION
SELECT Category, 'Jan', JanQuantity, JanScales
FROM SalesSummary
UNION
SELECT Category, 'Feb', FebQuantity, FebScales
FROM SalesSummary;
```
- 개선된 UNION 쿼리이다.

![개선된_UNION_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/8b5d8b3a-fedf-46e9-8e2f-95700433dda8)
- 개선된 UNION 쿼리의 결과이다.

```sql
SELECT Category, 'Oct' AS SalesMonth, OctQuantity AS Quantity, OctScales AS SalesAmt
FROM SalesSummary
UNION
SELECT Category, 'Nov', NovQuantity, NovScales
FROM SalesSummary
UNION
SELECT Category, 'Dec', DecQuantity, DecScales
FROM SalesSummary
UNION
SELECT Category, 'Jan', JanQuantity, JanScales
FROM SalesSummary
UNION
SELECT Category, 'Feb', FebQuantity, FebScales
FROM SalesSummary
ORDER BY SalesMonth, Category;
```
![정렬된_UNION_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/d8a4d401-cd14-437b-902f-fcd9226a9b61)
- 만약 정렬을 추가한다면 마지막 SELECT 문에 ORDER BY 절을 추가하면 된다.

### UNION 쿼리 사용시 고려할 점
- UNION 쿼리는 중복 로우를 제거한다.
- 만약 중복되는 로우까지 추출하려면 UNION 대신 UNION ALL 을 사용한다.
- 만약 중복되는 데이터가 없다는 사실을 알고 있다면 UNION ALL 을 사용하는 것이 성능상 좋다.
  - 중복되는 로우를 제거하는 단계가 사라지기 때문

## 정리
- UNION 쿼리에서 각 SELECT 문은 컬럼의 개수가 동일해야 한다.
- 각 SELECT 문에서 사용하는 컬럼 이름이 달라도 문제는 없지만, 각 컬럼의 데이터 타입은 서로 호환되어야 한다.
- 데이터의 정렬 순서를 조정하려면 마지막 SELECT 문에 ORDER BY 절을 추가한다.
- 중복 로우를 제거할 필요가 없거나 중복 로우를 제거해서 일어난 성능 문제를 없애려면 UNION 대신 UNION ALL 을 사용한다.
