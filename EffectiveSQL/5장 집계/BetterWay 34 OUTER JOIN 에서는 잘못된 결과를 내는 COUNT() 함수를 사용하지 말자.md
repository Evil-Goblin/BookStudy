## COUNT 실수 예제
![예제_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/42c35733-285b-4035-91f8-2baadaa104ec)
- 예제에 사용될 데이터베이스 설계이다.

```sql
SELECT Recipe_Classes.RecipeClassDescription, COUNT(*) AS RecipeCount
FROM Recipe_Classes
         LEFT OUTER JOIN Recipes
                         ON Recipe_Classes.RecipeClassID = Recipes.RecipeClassID
GROUP BY Recipe_Classes.RecipeClassDescription;
```
- 모든 종류의 요리법과 종류별 요리 개수를 조회하는 쿼리이다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/06768360-d76d-4f5c-8b54-84362ba64a3e)
- 조회 결과이다.
- 종류별로 요리가 최소 하나는 있는 것 같지만 실제로는 요리가 없어서 NULL 이 포함된 경우에도 COUNT(*) 에 의해 집계된다.
  - 원하던 결과가 아니다.

```sql
SELECT Recipe_Classes.RecipeClassDescription, COUNT(Recipes.RecipeClassID) AS RecipeCount
FROM Recipe_Classes
         LEFT OUTER JOIN Recipes
                         ON Recipe_Classes.RecipeClassID = Recipes.RecipeClassID
GROUP BY Recipe_Classes.RecipeClassDescription;
```
- COUNT(*) 대신 Recipes.RecipeClassID 를 COUNT 하도록 하여 개선한다.
  - * 대신 컬럼 이름을 사용하면 NULL 값을 가진 로우를 무시한다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/5de53b41-bab2-43d8-b438-d9401a2fc952)
- 조회 결과이다.
- 이전과 달리 Soup 는 개수가 0으로 표시된다.

### 해결법
- 이 문제를 해결하는 방법으로 GROUP BY 는 효율적이지 못하다.
  - 종류별 요리의 개수가 몇 가지 되지 않기 때문에 개수 세기에는 서브쿼리를 사용하면 효율적이다.

```sql
SELECT Recipe_Classes.RecipeClassDescription,
       (SELECT COUNT(Recipes.RecipeClassID)
        FROM Recipes
        WHERE Recipes.RecipeClassID = Recipe_Classes.RecipeClassID) AS RecipeCount
FROM Recipe_Classes;
```
- Recipes 테이블의 모든 로우를 가져와 그루핑해 그룹별로 수를 세는 대신 서브쿼리를 사용하는 것이 더 빠를 것이다.
- 인덱스 컬럼의 개수를 센다면 데이터베이스 엔진은 실제 로우가 아닌 인덱스 항목의 개수를 셀 것이다.
- 물론 데이터베이스 엔진에 따라서 다르다.

## 정리
- NULL 값이 있는 로우를 포함해 모든 로우의 개수를 세려면 COUNT(*) 을 사용한다.
- 컬럼 값이 NULL 이 아닌 로우의 개수만 세려면 COUNT(<컬럼 이름>)을 사용한다.
- 연관성 있는 서브쿼리라 하더라도 GROUP BY 절보다는 서브쿼리를 사용하는 것이 더 효율적일 수 있다.
