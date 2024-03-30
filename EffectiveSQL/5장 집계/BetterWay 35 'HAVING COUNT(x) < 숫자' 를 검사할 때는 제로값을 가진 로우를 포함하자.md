## HAVING 절에 제로 값을 가진 로우를 포함하는 방법
![예제_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/42c35733-285b-4035-91f8-2baadaa104ec)
- 예제에 사용될 데이터베이스 설계이다.
- 세 개 미만의 향신료(Spice) 를 첨가한 메인 코스 요리를 찾는다고 가정한다.

```sql
SELECT Recipes.RecipeTitle,
       COUNT(Recipe_Ingredients.RecipeID) AS IngredCount
FROM (((Recipe_Classes
    INNER JOIN Recipes
    ON Recipe_Classes.RecipeClassID = Recipes.RecipeClassID)
    INNER JOIN Recipe_Ingredients
       ON Recipes.RecipeID = Recipe_Ingredients.RecipeID)
    INNER JOIN Ingredients
      ON Recipe_Ingredients.IngredientID =
         Ingredients.IngredientID)
         INNER JOIN Ingredient_Classes
                    ON Ingredients.IngredientClassID =
                       Ingredient_Classes.IngredientClassID
WHERE Recipe_Classes.RecipeClassDescription = 'Main Course'
  AND Ingredient_Classes.IngredientClassDescription = 'Spice'
GROUP BY Recipes.RecipeTitle
HAVING COUNT(Recipe_Ingredients.RecipeID) < 3;
```
- Recipe_Classes.RecipeClassDescription 값이 'Main Course' 이고 Ingredient_Classes.IngredientClassDescription 값이 'Spice' 인 로우를 찾는다.

![조회_결과](https://github.com/gilbutITbook/006882/assets/74400861/e3888e8e-df25-457b-b896-5749d66934a4)
- 쿼리의 결과이다.
- Recipe_Ingredients 테이블과 LEFT JOIN 을 하지 않아서 제로 카운트인 로우(향신료를 하나도 첨가하지 않은 요리)를 가져오지 못했다.
  - 원하던 결과가 아니다.

```sql
SELECT Recipes.RecipeTitle, 
  COUNT(RI.RecipeID) AS IngredCount
FROM (Recipe_Classes 
  INNER JOIN Recipes
    ON Recipe_Classes.RecipeClassID = Recipes.RecipeClassID) 
  LEFT OUTER JOIN
     (SELECT Recipe_Ingredients.RecipeID, 
         Ingredient_Classes.IngredientClassDescription
      FROM (Recipe_Ingredients
        INNER JOIN Ingredients
          ON Recipe_Ingredients.IngredientID = 
      Ingredients.IngredientID) 
        INNER JOIN Ingredient_Classes 
          ON Ingredients.IngredientClassID = 
         Ingredient_Classes.IngredientClassID) AS RI
    ON Recipes.RecipeID = RI.RecipeID AND RI.IngredientClassDescription = 'Spice' 
WHERE Recipe_Classes.RecipeClassDescription = 'Main course' 
GROUP BY Recipes.RecipeTitle
HAVING COUNT(RI.RecipeID) < 3;
```
- LEFT JOIN 을 이용하여 다시 작성하였지만 이 또한 잘못된 결과가 반환된다.
  - LEFT JOIN 의 '오른쪽' 테이블에 달아 높은 조건이 외부 조인의 효과를 없애 버리기 때문이다.
  - 결과 이전 쿼리와 같은 결과가 반환된다.

```sql
SELECT Recipes.RecipeTitle, 
  COUNT(RI.RecipeID) AS IngredCount
FROM (Recipe_Classes 
  INNER JOIN Recipes
    ON Recipe_Classes.RecipeClassID = Recipes.RecipeClassID) 
  LEFT OUTER JOIN
  (SELECT Recipe_Ingredients.RecipeID, 
    Ingredient_Classes.IngredientClassDescription
   FROM (Recipe_Ingredients
    INNER JOIN Ingredients
      ON Recipe_Ingredients.IngredientID = 
       Ingredients.IngredientID) 
    INNER JOIN Ingredient_Classes 
      ON Ingredients.IngredientClassID = 
       Ingredient_Classes.IngredientClassID
   WHERE 
     Ingredient_Classes.IngredientClassDescription = 'Spice') 
    AS RI
      ON Recipes.RecipeID = RI.RecipeID 
WHERE Recipe_Classes.RecipeClassDescription = 'Main course' 
GROUP BY Recipes.RecipeTitle
HAVING COUNT(RI.RecipeID) < 3;
```
- 조인을 수행하기 전에 조건을 서브쿼리 내로 옮긴다.

![올바른_조회_결과](https://github.com/gilbutITbook/006882/assets/74400861/4f4601e7-947d-492c-af1c-26f0fc0b6eba)
- 올바르게 조회된 결과이다.
- 첨가된 향신료의 개수가 0인 경우도 올바르게 조회가 되었다.
- **COUNT() 함수나 HAVING 절을 사용해 특정 값보다 작은 건을 찾으면서 제로 값을 처리할 때는 주의를 기울여야 한다.**

## 정리
- INNER JOIN 을 사용하면 제로 카운트를 찾는 작업이 제대로 작동하지 않을 것이다.
- LEFT JOIN 의 오른쪽에 조건을 달면 INNER JOIN 과 동일한 결과를 얻는다.
  - 이 조건을 서브쿼리로 밀어넣거나 ON 절에서 오른쪽 테이블에 달아 둔다.
- 제로 카운트를 찾는 과정에서 그 값이 1 이상일 때는 데이터에 문제가 있다는 의미다.
