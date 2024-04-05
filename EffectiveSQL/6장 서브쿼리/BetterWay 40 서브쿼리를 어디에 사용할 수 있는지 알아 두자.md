## 서브쿼리
- 서브쿼리는 완전한 SELECT 문을 괄호로 둘러싸고 괄호 밖에는 AS 절과 함께 별칭을 주는 것이다.
- 다른 SELECT, UPDATE, INSERT, DELETE 문 내의 여러 곳에 서브쿼리를 사용할 수 있다.
- 서브쿼리는 컬럼과 로우 여러개를 포함한 데이터 집합 전체를 반환할 수 있다.
  - 테이블 서브쿼리
- 서브쿼리는 로우를 여러 개 가진 단일 컬럼만 반환하기도 한다.
  - 단일 컬럼 테이블 서브쿼리
- 값을 하나만 반환하는 서브쿼리
  - 스칼라 서브쿼리

### 서브쿼리 활용 방법
- 테이블 서브쿼리
  - 테이블이나 뷰의 이름, 테이블을 반환하는 저장 프로시저나 함수 이름을 사용할 수 있는 곳이라면 어디에나 활용할 수 있다.
- 단일 컬럼 테이블 서브쿼리
  - 테이블 서브쿼리나 값의 목록을 IN 조건으로 비교하는 곳이라면 어디에나 활용할 수 있다.
- 스칼라 서브쿼리
  - 컬럼 이름이나 컬럼 이름에 대한 표현식을 다른 용도로 사용하는 곳이라면 어디에나 활용할 수 있다.

## 테이블 서브쿼리
- 테이블 서브쿼리는 조인을 수행하기 전에 하나 이상의 집합에 대해 필터링이 필요한 여러 데이터 집합과 조인을 수행하는 FROM 절에서 특히 유용하다.

```sql
SELECT BeefRecipes.RecipeTitle
FROM 
  (SELECT Recipes.RecipeID, Recipes.RecipeTitle
   FROM (Recipes INNER JOIN Recipe_Ingredients
    ON Recipes.RecipeID = Recipe_Ingredients.RecipeID) 
      INNER JOIN Ingredients 
    ON Ingredients.IngredientID = 
      Recipe_Ingredients.IngredientID
   WHERE Ingredients.IngredientName = 'Beef') 
      AS BeefRecipes
  INNER JOIN
  (SELECT Recipe_Ingredients.RecipeID
   FROM Recipe_Ingredients INNER JOIN Ingredients
    ON Ingredients.IngredientID = 
      Recipe_Ingredients.IngredientID
   WHERE Ingredients.IngredientName = 'Garlic') 
      AS GarlicRecipes 
    ON BeefRecipes.RecipeID = GarlicRecipes.RecipeID;
```
- Recipes 에서 Beef 와 Garlic 을 사용하는 요리법을 모두 찾는다고 하자.
- Beef 를 사용하는 요리를 찾고, Garlic 을 사용하는 요리를 찾는 독립적인 테이블 서브쿼리를 두 개 만든 후 두 서브 쿼리를 조인해서 두 가지 재료를 모두 포함한 요리를 찾는다.

```sql
SELECT Customers.CustomerID, Customers.CustFirstName, 
  Customers.CustLastName, Orders.OrderNumber, Orders.OrderDate
FROM Customers
  INNER JOIN Orders
    ON Customers.CustomerID = Orders.CustomerID
WHERE EXISTS 
  (SELECT NULL
   FROM (Orders AS O2
      INNER JOIN Order_Details
        ON O2.OrderNumber = Order_Details.OrderNumber)
      INNER JOIN Products
        ON Products.ProductNumber = Order_Details.ProductNumber 
   WHERE Products.ProductName = 'Skateboard' 
    AND O2.OrderNumber = Orders.OrderNumber)
AND EXISTS 
  (SELECT NULL 
   FROM (Orders AS O3 
      INNER JOIN Order_Details
        ON O3.OrderNumber = Order_Details.OrderNumber)
      INNER JOIN Products
        ON Products.ProductNumber = Order_Details.ProductNumber 
   WHERE Products.ProductName = 'Helmet'
      AND O3.OrderNumber = Orders.OrderNumber);
```
- 자주 사용하지는 않지만 테이블 서브쿼리 용도 중 하나는 EXISTS 조건에 사용되는 서브쿼리이다.
- 동일한 주문에서 스케이트 보드와 헬멧을 구매한 모든 고객을 찾는다고 하자.
- EXISTS 와 주 쿼리에 있는 현재 OrderNumber 값과 Products 테이블에서 제품 이름이 'Skateboard' 나 'Helmet' 인 데이터를 필터링하는 연관성 있는 테이블 서브쿼리를 두 개 사용한다.
- EXISTS 조건이 사용되었을 때 서브쿼리의 SELECT 절에 나열한 정보는 무의미하므로 NULL 을 명시했다.

## 단일 컬럼 테이블 서브쿼리
- 단일 컬럼 테이블 서브쿼리는 테이블 서브쿼리를 사용할 수 있는 곳이라면 어디에나 활용 가능하다.
- 이 유형의 서브쿼리는 컬럼을 한 개만 반환하므로 반환되는 컬럼은 IN 이나 NOT IN 조건의 목록을 제공하는 데 사용된다.

```sql
SELECT Products.ProductName
FROM Products
WHERE Products.ProductNumber NOT IN 
  (SELECT Order_Details.ProductNumber 
   FROM Orders 
      INNER JOIN Order_Details
        ON Orders.OrderNumber = Order_Details.OrderNumber
   WHERE Orders.OrderDate 
    BETWEEN '2015-12-01' AND '2015-12-31');
```
- 2015년 12월에 주문하지 않은 제품의 목록을 모두 찾아야 한다고 한다.
- 이는 단일 컬럼 테이블 서브쿼리로 구현한 쿼리이다.
- IN 절을 사용할 수 있는 곳이라면 어디에나 단일 컬럼 테이블 서브쿼리를 활용할 수 있고, SELECT 절에 명시된 컬럼 목록에서 사용한 CASE 문 내에서도 활용 가능하다.

```sql
SELECT Employees.EmpFirstName, Employees.EmpLastName, 
  Customers.CustFirstName, Customers.CustLastName, 
  Customers.CustAreaCode, Customers.CustPhoneNumber, 
  (CASE WHEN Customers.CustomerID IN 
    (SELECT CustomerID 
     FROM Orders 
     WHERE Orders.EmployeeID = Employees.EmployeeID) 
        THEN 'Ordered from you.' 
        ELSE ' ' END) AS CustStatus
FROM Employees 
  INNER JOIN Customers
    ON Employees.EmpState = Customers.CustState;
```
- CASE 문에서 단일 컬럼 서브쿼리를 사용해 같은 주에 사는 직원과 고객 명단을 만든 후 판매 직원에게 어떤 고객이 주문을 했고 어떤 고객이 주문을 하지 않았는지 알려주는 식으로 해결한 쿼리이다.

## 스칼라 서브쿼리
- 스칼라 서브쿼리는 단일 로우에 있는 한 컬럼에 값을 0개 또는 한 개만 반환한다.
- 테이블 서브쿼리나 단일 컬럼 테이블 서브쿼리를 사용할 수 있는 곳이라면 어디에나 스칼라 서브쿼리를 활용할 수 있다.
- 하지만 스칼라 서브쿼리는 컬럼 이름이나 표현식을 다른 용도로 사용하는 곳에도 유용하다.
- 스칼라 서브쿼리는 다른 컬럼 및 연산자와 함께 표현식에서도 사용할 수 있다.

```sql
SELECT Products.ProductNumber, Products.ProductName, 
  (SELECT MAX(Orders.OrderDate) 
   FROM Orders 
      INNER JOIN Order_Details 
        ON Orders.OrderNumber = Order_Details.OrderNumber 
   WHERE Order_Details.ProductNumber = Products.ProductNumber) 
    AS LastOrder
FROM Products;
```
- 모든 제품과 집계 함수 MAX()를 사용해 각 제품에 대한 최근 주문 일자를 찾는 쿼리이다.
- MAX() 함수는 단일 값을 반환하므로 스칼라 서브쿼리에서도 사용할 수 있다.

```sql
SELECT Vendors.VendName, 
  AVG(Product_Vendors.DaysToDeliver) AS AvgDelivery
FROM Vendors 
  INNER JOIN Product_Vendors 
    ON (Vendors.VendorID = Product_Vendors.VendorID)
GROUP BY Vendors.VendName
HAVING AVG(Product_Vendors.DaysToDeliver) > 
  (SELECT AVG(DaysToDeliver) FROM Product_Vendors);
```
- 비교를 수행하는 곳에서도 단일 값을 반환하는 스칼라 서브쿼리를 사용할 수 있다.
- 판매자 전체의 평균을 웃도는 평균 배송일을 보유한 판매자를 모두 찾을 때 사용할 수 있는 쿼리이다.
- 여기서는 HAVING 절의 비교 값을 만드는 데 스칼라 서브쿼리를 사용한다.

## 정리
- 테이블이나 뷰의 이름, 테이블을 반환하는 함수나 저장 프로시저가 사용되는 곳이라면 어디에나 테이블 서브쿼리를 활용할 수 있다.
- 테이블 서브쿼리를 사용할 수 있는 곳과 IN 이나 NOT IN 조건의 목록을 생성해야 하는 곳이라면 어디에나 단일 컬럼을 반환하는 테이블 서브쿼리를 활용할 수 있다.
- 스칼라 서브쿼리는 컬럼 이름을 사용할 수 있는 곳이라면 어디에나 활용할 수 있다.
  - 즉 SELECT 절, SELECT 절에 있는 표현식, 비교 조건의 일부로 사용이 가능하다.
