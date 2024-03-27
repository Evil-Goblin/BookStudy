## 집계 없이 최댓값 최솟값 찾기
![예제_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/47a78969-272e-4852-acd9-f3aa951033d7)
- 예제로 이용할 테이블이다.
  - 일부 데이터는 생략한다.

```sql
SELECT Category, MAX(MaxABV) AS MaxAlcohol
FROM BeerStyles
GROUP BY Category;
```
- 카테고리별 알코올 도수가 가장 높은 것을 찾는 SQL 문이다.

```sql
SELECT Category, Country, MAX(MaxABV) AS MaxAlcohol
FROM BeerStyles
GROUP BY Category, Country;
```
- 조금 확장해서 가장 높은 도수뿐만 아니라 어느 나라의 어떤 스타일의 맥주인지까지 조회하려한다.
- 알코올 도수가 가장 높은 맥주의 원산지 국가를 추출하는 잘못된 SQL 이다.

![잘못된_SQL_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/32e2922b-6773-4f91-9b02-693a38af325c)
- 잘못된 SQL 의 결과이다.

```sql
SELECT l.Category, l.MaxABV AS LeftMaxABV, r.MaxABV AS RightMaxABV
FROM BeerStyles AS l
         LEFT JOIN BeerStyles AS r
                   ON l.Category = r.Category
                       AND l.MaxABV < r.MaxABV;
```
- BeerStyles 테이블을 셀프 조인해 각 로우의 MaxABV 값을 비교하는 쿼리이다.
- 카테고리별로 테이블에서 MaxABV 값이 가장 큰 로우를 찾아야 한다.
- 때문에 테이블을 셀프 조인해 해당 카테고리에 속하는 다른 로우의 MaxABV 값과 각 로우에 있는 MaxABV 값을 비교하면 원하는 로우를 찾을 수 있다.

![MaxABV_비교_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/291da24a-87a4-42b0-b200-e38b5db7cb55)
- 쿼리의 결과이다.
- 각 로우를 동일한 카테고리의 다른 로우와 비교한 후 MaxABV 값이 큰 로우만 반환한다.
- LEFT JOIN 이기 때문에 오른쪽 테이블의 MaxABV 값이 큰 로우가 없더라도 왼쪽 테이블에서 적어도 한 개 반환된다.

```sql
SELECT l.Category, l.Country, l.Style, l.MaxABV AS MaxAlcohol
FROM BeerStyles AS l
         LEFT JOIN BeerStyles AS r
                   ON l.Category = r.Category
                       AND l.MaxABV < r.MaxABV
WHERE r.MaxABV IS NULL
ORDER BY l.Category;
```
- 이전의 결과를 바탕으로 오른쪽 테이블이 NULL 인 경우가 가장 높은 도수를 표한한다는 것을 알았기에 이를 기반으로 세부 정보를 조회하는 쿼리를 만든다.

![올바른_쿼리_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/42da2798-82c3-4299-a51f-08771531315d)
- 카테고리별 도수가 가장 높은 항목의 세부 정보를 조회한 결과이다.
- 이 쿼리에 집계 함수가 없기에 GROUP BY 절을 사용하지 않았다.
- GROUP BY 절이 없기 때문에 다른 테이블과 쉽게 조인이 가능하다.
- `l.Category = r.Category` 는 `GROUP BY Category` 와 기능이 동일하다.
- `l.MaxABV < r.MaxABV` 는 `MAX(MaxABV)` 와 같은 역할을 한다.
  - `WHERE r.MaxABV IS NULL` 절이 최댓값만을 가져오기 때문

## GROUP BY 의 비용
- 집계 연산과 GROUP BY 절 모두 자원을 많이 소모하는 연산이다.
- `MaxAlcohol = (SELECT MAX(MaxAlcohol) FROM BeerStyles AS b2 WHERE b2.Category = BeerStyles.Category)` 처럼 사용할 수 있지만, 이것은 집계 함수뿐만 아니라 연관성 있는 서브쿼리도 사용한다.
- 연관성 있는 서브쿼리는 데이터베이스 엔진이 모든 로우에 대해 해당 서브쿼리를 실행하므로 치러야햘 비용이 큰 편이다.

## 정리
- '주'테이블을 셀프 조인할 때는 LEFT JOIN 을 사용한다.
- GROUP BY 절에 포함된 모든 컬럼은 ON 절에서 동등 연산자(=)를 사용할 수 있다.
- MAX() 나 MIN() 에 포함된 컬럼은 ON 절에서 '<' 나 '>' 를 사용할 수 있다.
- ON 절에 있는 컬럼을 인덱스로 만들면 성능을 높일 수 있다.
  - 특히 큰 데이터 집합을 다룰 때는 더욱 그렇다.
