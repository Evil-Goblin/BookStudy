## 연관성
- 서브쿼리의 일부 조건(WHERE, HAVING 절의 조건)이 주 쿼리에서 처리하는 로우 값에 의존한다면, 이 서브쿼리는 '연관성 있는' 서브쿼리다.
- 연관성 없는 서브쿼리는 외부 값에 의존하지 않고, 다른 쿼리에 내장되지 않은 독립적으로 수행되는 서브쿼리다.

![Recipes](https://github.com/Evil-Goblin/BookStudy/assets/74400861/690a7bb5-5bd8-4991-8911-5ecc5b917615)
- 예제에 사용할 Recipes 데이터베이스 설계이다.

### 연관성 없는 서브쿼리
- 연관성 없는 서브쿼리는 두 가지 경우에 사용된다.
  - FROM 절에서 필터링된 데이터 집합으로 사용한다.
  - WHERE 절의 IN 조건에 오는 단일 컬럼 데이터 집합이나 WHERE 또는 HAVING 절의 비교 조건에 오는 단일 값(스칼라 서브쿼리)으로 사용한다.

```sql
SELECT BeefRecipes.RecipeTitle
FROM 
  (SELECT Recipes.RecipeID, Recipes.RecipeTitle
   FROM (Recipes INNER JOIN Recipe_Ingredients 
      ON Recipes.RecipeID = Recipe_Ingredients.RecipeID) 
    INNER JOIN Ingredients 
      ON Ingredients.IngredientID = 
           Recipe_Ingredients.IngredientID
   WHERE Ingredients.IngredientName = 'Beef') AS BeefRecipes 
INNER JOIN
  (SELECT Recipe_Ingredients.RecipeID
   FROM Recipe_Ingredients INNER JOIN Ingredients
     ON Ingredients.IngredientID = 
          Recipe_Ingredients.IngredientID
   WHERE Ingredients.IngredientName = 'Garlic') AS GarlicRecipes 
  ON BeefRecipes.RecipeID = GarlicRecipes.RecipeID;
```
- FROM 절에서 연관성 없는 서브쿼리를 사용하는 예제이다.
  - 소고기와 마늘을 모두 사용하는 요리를 찾는 쿼리이다.
- 첫 번째 서브쿼리는 소고기를 첨가한 모든 요리의 ID 와 요리 이름을 반환한다.
- 두 번째 서브쿼리는 마늘을 첨가한 모든 요리의 ID 를 반환한다.
- RecipeID 컬럼으로 두 서브쿼리를 INNER JOIN 하면 정확한 답을 찾을 수 있다.
  - 두 서브쿼리 모두 필터링되었지만, WHERE 절의 조건은 서브쿼리 바깥쪽에 반환되는 값에 의존하지 않는다.
  - 즉, 두 서브쿼리는 독립적으로 수행될 수 있다.

```sql
SELECT Recipes.RecipeTitle
FROM Recipes
WHERE Recipes.RecipeClassID IN
  (SELECT RC.RecipeClassID 
   FROM Recipe_Classes AS RC
   WHERE RC.RecipeClassDescription IN 
    ('Salad', 'Soup', 'Main Course'));
```
- 연관성 없는 서브쿼리를 WHERE 절의 IN 조건에 사용하는 쿼리이다.
  - 샐러드, 수프, 메인 코스 요리를 찾는 쿼리이다.
- 서브쿼리 바깥쪽에 반환되는 값에 의존하지 않으므로, IN 조건에 사용되는 값 목록을 반환하는 서브쿼리는 독립적으로 수행될 수 있다.
- 주 쿼리의 FROM 절에서 Recipes 테이블과 Recipe_Class 테이블을 INNER JOIN 한 후 동일한 IN 절을 사용해도 똑같은 결과를 얻을 수 있다.
  - 하지만 서브쿼리가 조인보다는 약간 더 효율적이다.(적어도 SQL Server 에서는)

```sql
SELECT DISTINCT Recipes.RecipeTitle
FROM Recipes INNER JOIN Recipe_Ingredients 
    ON Recipes.RecipeID = Recipe_Ingredients.RecipeID 
  INNER JOIN Ingredients 
    ON Recipe_Ingredients.IngredientID = Ingredients.IngredientID
WHERE (Ingredients.IngredientName = 'Garlic') AND 
      (Recipe_Ingredients.Amount =
        (SELECT MAX(Amount)
         FROM Recipe_Ingredients INNER JOIN Ingredients 
            ON Recipe_Ingredients.IngredientID = 
                 Ingredients.IngredientID
         WHERE IngredientName = 'Garlic'));
```
- WHERE 절에서 스칼라 서브쿼리를 사용하는 쿼리이다.
  - 마늘을 가장 많이 사용하는 요리를 찾는 쿼리이다.
- 연관성 없는 서브쿼리에서는 서브쿼리 자체에서 SELECT MAX 를 아무 문제없이 사용할 수 있다.
  - MAX() 집계 함수가 단일 값을 반환하므로, WHERE 절에서 동등 연산자와 함께 사용할 비교 값을 반환하는 데 이 서브쿼리를 쓸 수 있다.

### 연관성 있는 서브쿼리
- 연관성 있는 서브쿼리는 WHERE 절이나 HAVING 절에서 주 쿼리가 제공하는 값에 의존하는 조건을 하나 이상 사용하는 서브쿼리이다.
  - 데이터베이스 엔진은 주 쿼리에서 반환되는 모든 로우에서 서브쿼리를 한 번씩 실행해야 한다.
  - 일반적으로 느리게 수행되지만, 일부 데이터베이스 시스템은 연관성 있는 서브쿼리가 포함된 쿼리를 최적화하므로 항상 느리다고는 할 수 없다.
- 연관성 있는 스칼라 서브쿼리를 사용하는 경우
  - SELECT 절에 값 하나를 반환
  - WHERE, HAVING 절의 비교 조건을 검사하는 단일 값을 제공
  - WHERE, HAVING 절의 IN 조건에 사용할 단일 컬럼 목록을 제공
  - WHERE, HAVING 절의 EXISTS 조건 검사를 위한 데이터 집합을 제공

```sql
SELECT Recipe_Classes.RecipeClassDescription,
       (SELECT COUNT(*)
        FROM Recipes
        WHERE Recipes.RecipeClassID =  
           Recipe_Classes.RecipeClassID) AS RecipeCount
FROM Recipe_Classes;
```
- SELECT 절에 값을 반환하려고 연관성 있는 스칼라 서브쿼리를 사용하는 예이다.
  - 연관성 있는 서브쿼리를 사용해 로우의 개수를 센다.
- Recipe_Classes 테이블의 값을 이용해 필터링을 수행하므로 연관성 있는 서브쿼리이다.
  - 데이터베이스 시스템은 Recipe_Classes 테이블의 모든 로우에서 한 번씩 이 서브쿼리를 수행해야 한다.
- 서브쿼리 대신 JOIN 이나 GROUP BY 를 사용하지 않은 이유
  - 연관성 있는 서브쿼리를 포함한 쿼리는 대부분의 데이터베이스 시스템에서 더 빠르게 수행된다.
  - GROUP BY 절을 사용하면 잘못된 결과를 얻을 수 있다.

```sql
SELECT Recipes.RecipeTitle
FROM Recipes
WHERE EXISTS 
  (SELECT NULL
   FROM Ingredients INNER JOIN Recipe_Ingredients
     ON Ingredients.IngredientID = 
          Recipe_Ingredients.IngredientID
   WHERE Ingredients.IngredientName = 'Beef'
      AND Recipe_Ingredients.RecipeID = Recipes.RecipeID)
AND EXISTS 
  (SELECT NULL
   FROM Ingredients INNER JOIN Recipe_Ingredients
     ON Ingredients.IngredientID = 
          Recipe_Ingredients.IngredientID
   WHERE Ingredients.IngredientName = 'Garlic'
      AND Recipe_Ingredients.RecipeID = Recipes.RecipeID);
```
- EXISTS 조건 검사용 데이터 집합을 반환하는 연관성 있는 서브쿼리이다.
  - 소고기와 마늘을 모두 사용하는 요리를 찾는 쿼리이다.
- 각 서브쿼리가 주 쿼리에 있는 Recipes 테이블을 참조하므로 데이터베이스 시스템은 Recipes 테이블의 모든 로우에서 두 서브쿼리를 실행해야 한다.
- 연관성 없는 서브쿼리를 이용하는 버전에 비해 이 쿼리가 훨씬 느리게 수행될 것으로 예상할 수도 있다.
  - 이 쿼리는 약간 더 많은 자원을 사용하긴 하지만, 대부분의 데이터베이스 시스템이 이런 유형의 쿼리를 최적화하기 때문에 그리 심하게 느리지는 않다.
  - 하지만 IngredientName 컬럼에 인덱스를 추가하면 EXISTS 를 사용한 쿼리의 성능이 더 좋아진다.
- 사거블(인덱스를 사용하는) 조건을 사용할 때 인덱스가 얼마나 중요한지 알 수 있다.
- IN 을 사용해서 문제를 해결할 수도 있다.
  - `EXISTS(SELECT Recipe_Ingredients.RecipeID...)` 대신 `Recipes.RecipeID IN (SELECT Recipe_Ingredients.RecipeID...)` 를 사용할 수 있다.
  - 만약 IngredientName 컬럼에 인덱스가 설정되어있다면 EXISTS 가 더 빠르게 수행된다.
  - 없더라도 EXISTS 가 좀 더 빠르게 수행된다.
    - 대부분의 옵티마이저가 조건에 맞는 첫 번째 로우를 만나자마자 서브쿼리의 수행을 중단하는 반면, IN 은 모든 로우를 가져오기 때문
  - 일반적인 JOIN 절은 일대다 관계에 있는 테이블 한 쌍을 조인할 때 중복된 로우을 가져올 수 있다.
    - EXISTS 를 사용하면 옵티마이저는 이 쿼리를 '세미 조인'으로 최적화하는데, 가장 바깥쪽에 있는 테이블의 로우는 중복되지 않는다.
  - 옵티마이저는 IN 조건을 사용한 것 처럼 안쪽 테이블에 있는 모든 데이터를 실제로 처리할 필요가 없다.

## 정리
- 연관성 있는 서브쿼리는 WHERE, HAVING 절에서 해당 서브쿼리가 포함된 주 쿼리에 있는 값에 의존하는 참조를 사용한다.
- 연관성 없는 서브쿼리는 주 쿼리에 있는 데이터에 의존하지 않으며, 독립적으로 수행될 수 있다.
- 보통 연관성 없는 서브쿼리는 FROM 절의 데이터 집합, IN 조건에 쓸 단일 컬럼 데이터 집합, WHERE 또는 HAVING 절의 비교 조건에 스칼라 값을 반환하는 데 사용된다.
- 연관성 있는 서브쿼리는 SELECT 절에 스칼라 값을 반환하거나, WHERE 또는 HAVING 절의 비교 조건을 검사하는 단일 값을 제공하거나, EXISTS 절에서 존재 유무를 검사하는 데이터 집합을 제공하는 데 사용된다.
- 연관성 있는 서브쿼리는 다른 방법에 비해 꼭 느리게 수행되는 것은 아니며, 올바른 결과를 반환하는 유일한 방법이 될 수 있다.
