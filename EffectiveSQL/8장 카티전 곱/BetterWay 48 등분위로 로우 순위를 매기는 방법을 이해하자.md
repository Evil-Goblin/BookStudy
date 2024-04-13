## 등분위 조회
![판매_주문_데이터베이스_다이어그램](https://github.com/Evil-Goblin/BookStudy/assets/74400861/efd26d72-7d3a-46ea-afcc-6f1e79fed738)
- 예제에 사용될 데이터베이스 다이어그램이다.
- 특정 카테고리에 있는 제품의 판매량이 다른 것과 비교해 순위가 어떻게 나오는지 찾으려고 한다.

```sql
SELECT OD.ProductNumber,
       SUM(OD.QuantityOrdered * OD.QuotedPrice) AS ProductSales
FROM Order_Details AS OD
WHERE OD.ProductNumber IN
      (SELECT P.ProductNumber
       FROM Products AS P INNER JOIN Categories AS C
                                     ON P.CategoryID = C.CategoryID
       WHERE C.CategoryDescription = 'Accessories')
GROUP BY OD.ProductNumber;
```
- 액세서리에 속한 제품별 총 판매액을 계산하는 쿼리이다.
  - 제품별 판매액을 여러 곳에서 사용하기 때문에 액세서리 카테고리에 있는 제품 번호별 총 판매액 조회를 공통 테이블 표현식(CTE) 로 만들어야 합리적이다.

```sql
WITH ProdSale AS
  (SELECT OD.ProductNumber,
       SUM(OD.QuantityOrdered * OD.QuotedPrice) AS ProductSales
     FROM Order_Details AS OD
     WHERE OD.ProductNumber IN 
       (SELECT P.ProductNumber
        FROM Products AS P INNER JOIN Categories AS C
          ON P.CategoryID = C.CategoryID
        WHERE C.CategoryDescription = 'Accessories')
     GROUP BY OD.ProductNumber),

RankedCategories AS
  (SELECT Categories.CategoryDescription, Products.ProductName, 
          ProdSale.ProductSales, 
          RANK() OVER (
            ORDER BY ProdSale.ProductSales DESC
          ) AS RankInCategory
   FROM Categories INNER JOIN Products 
     ON Categories.CategoryID = Products.CategoryID
   INNER JOIN ProdSale
     ON ProdSale.ProductNumber = Products.ProductNumber),

ProdCount AS
  (SELECT COUNT(ProductNumber) AS NumProducts 
   FROM ProdSale) 

SELECT P1.CategoryDescription, P1.ProductName, 
    P1.ProductSales, P1.RankInCategory, 
    (CASE WHEN RankInCategory <= ROUND(0.2 * NumProducts, 0)
            THEN 'First' 
          WHEN RankInCategory <= ROUND(0.4 * NumProducts,0)
            THEN 'Second' 
          WHEN RankInCategory <= ROUND(0.6 * NumProducts,0)
            THEN 'Third' 
          WHEN RankInCategory <= ROUND(0.8 * NumProducts,0)
            THEN 'Fourth' 
          ELSE 'Fifth' END) AS Quintile
FROM RankedCategories AS P1 
CROSS JOIN ProdCount
ORDER BY P1.ProductSales DESC;
```
- 5분위수의 시작과 끝을 결정하기 위해 5등분할 제품의 총 개수가 있어야 한다.
  - 제품별로 5분위수를 결정하는 계산을 하려면 각 제품 로우에 이 값이 필요하다.
  - 서브쿼리 하나와 CROSS JOIN 으로 값을 모든 로우에서 사용하되 최종 SELECT 절에는 포함되지 않게 한다.
- 쿼리를 간단하게 만들기 위해 다른 모든 제품의 판매액과 현재 로우에 있는 제품의 판매액을 비교해 각 제품의 '순위'를 계산하는 서브쿼리가 필요하다.
  - RANK() 윈도우 함수를 사용해 보다 직관적으로 작성한다.
- 제품의 총 개수에 0.2, 0.4, 0.6, 0.8 을 곱해 각 5분위수 내에서 각 제품의 순위를 비교하는 복잡한 CASE 절이 필요하다.
- ROUND() 함수는 ISO SQL 표준에는 정의되지 않았지만, 모든 주요 DBMS 에서 지원한다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/148c43ea-b764-4e05-ab58-14a4f1d3e336)
![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/eb338f61-301f-41b8-929b-7bd717614b5f)
- 위 쿼리로 조회한 결과이다.
- ROUND() 함수를 사용하지 않으면 첫 번째 5분위수에는 로우 세 개가 들어가고, 나머지 5분위수에는 네 개씩 들어갈 것이다.
  - 현재는 세 번째 5분위수에 로우 세 개가 들어가 있고, 나머지에는 네 개씩 들어가 있다.
  - 제품의 총 개수가 5로 나누어떨어지지 않을 때 ROUND() 함수로 잔여 5분위수를 가운데 쪽으로 밀어 넣는다.
- 순위가 매겨진 임의의 데이터 집합을 동일한 비율로 분할할 때도 같은 기법을 사용할 수 있다.
  - 제곱수를 계산하려면 1을 동일 그룹의 개수로 나누고 그 결과의 배수를 구한다.
  - 그 후 배수들로 그룹을 분할한다.
  - 예) 10분위수로 분할하면 `1/10 = 0.10` 이 되고, 그 배수인 0.10, 0.20, ..., 0.88, 0.90 으로 그룹을 분할한다.

## 정리
- 수량 데이터 집합을 순위로 분할하는 것은 정보를 평가하는 흥미롭고 유용한 방법이다.
- 순위 값을 쉽게 만들려면 RANK() 윈도우 함수를 사용한다.
- 1을 그룹의 개수로 나누면 각 그룹에 대한 제곱수를 구할 수 있다.
