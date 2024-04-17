## 순번 생성, 순위 만들기
![중개인_데이터베이스_모델](https://github.com/Evil-Goblin/BookStudy/assets/74400861/df85db8f-10e0-4b7c-b1b9-77f74f5c77dd)
- 예제에 사용될 데이터베이스 모델이다.
  - 주식을 사고파는 중개인에 대한 데이터베이스이다.
- 중개인은 구매 또는 판매할 수 있는 서로 다른 주식의 기록을 모두 보관한다.
  - 실제 구매, 판매 기록은 공통 거래 테이블에 저장된다.

![거래_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/17674da2-1bf6-4d0d-a9c3-faa998b38dc9)
- 주식 한 종목만 고려한다고 가정하고 위의 거래 테이블을 예시로 든다.
- 열 번째 주식 단위의 수익은 얼마인지 어떻게 알 수 있을까?
  - 첫 매수에서 12개를 샀으니 열 번째 주식의 원가는 27.10 달러이다.
  - 첫 매도에서 7개를 팔았으니 아직 열 번째 주식은 팔리지 않았다.
  - 이후 두 번째 매도에서 30.20달러에 열 번째 주식이 팔렸기 때문에 이 특정 주식의 이익은 3.10달러가 된다.
- 이 정보를 SQL 로 알 수 있을까?
- 이를 탤리 테이블과 윈도우 함수를 이용해 각 주식 단위 하나에 '로우' 하나를 할당하고, 로우에 원가와 매출을 할당해서 각 옵션의 이익을 계산할 수 있다.

```sql
WITH Buys AS (
  SELECT
    ROW_NUMBER() OVER (
      PARTITION BY t.StockID
      ORDER BY t.TransactionDate, t.TransactionID, c.Num
    ) AS TransactionSeq,
    c.Num AS StockSeq, 
    t.StockID,
    t.TransactionID, 
    t.TransactionDate, 
    t.Price AS CostOfProduct
  FROM Tally AS c 
  INNER JOIN Transactions AS t 
    ON c.Num <= t.Quantity
  WHERE t.TransactionTypeID = 1
), Sells AS (
  SELECT
    ROW_NUMBER() OVER (
      PARTITION BY t.StockID
      ORDER BY t.TransactionDate, t.TransactionID, c.Num
    ) AS TransactionSeq,
    c.Num AS StockSeq, 
    t.StockID,
    t.TransactionID, 
    t.TransactionDate, 
    t.Price AS RevenueOfProduct
  FROM Tally AS c 
  INNER JOIN Transactions AS t 
    ON c.Num <= t.Quantity
  WHERE t.TransactionTypeID = 2
)
SELECT 
  b.StockID,
  b.TransactionSeq,
  b.TransactionID AS BuyID,
  s.TransactionID AS SellID,
  b.TransactionDate AS BuyDate,
  s.TransactionDate AS SellDate,
  b.CostOfProduct,
  s.RevenueOfProduct,
  s.RevenueOfProduct - b.CostOfProduct AS GrossMargin
FROM Buys AS b
INNER JOIN Sells AS s
  ON b.StockID = s.StockID
  AND b.TransactionSeq = s.TransactionSeq
ORDER BY b.TransactionSeq;
```
- 개별 주식의 매수와 매도를 분리하는 쿼리이다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/a8b7d156-390b-4274-912f-136358bb5f0f)
- 매수 거래를 분리한 후 매도 거래를 분리한다.
- 명시된 순서에 따라 단위 하나의 원가를 매출과 짝짓는다.

```sql
SELECT
  ROW_NUMBER() OVER (
    PARTITION BY t.StockID
    ORDER BY t.TransactionDate, t.TransactionID, c.Num
  ) AS TransactionSeq,
  c.Num AS StockSeq, 
  t.StockID,
  t.TransactionID, 
  t.TransactionDate, 
  t.Price AS CostOfProduct
FROM Tally AS c 
INNER JOIN Transactions AS t 
  ON c.Num <= t.Quantity
WHERE t.TransactionTypeID = 1
```
- 매수 CTE 를 보다 자세히 보려고 한다.
- 거래 테이블과 탤리 테이블 간에 비동등 조인을 해서 매수 수량별로 단일 로우를 생성한다.
  - 개별 주식에서 올바른 순번이 만들어 지지만, 모든 '매수' 건에 대한 전역적인 순번이 필요하다.
  - '매도' 주식 중에서 상응하는 건과 짝지어야 하기 때문
- 때문에 ROW_NUMBER() 윈도우 함수에 탤리 테이블의 숫자와 거래 일자, 거래 ID 를 전달해 유일한 로우를 식별하고 정렬하여 순번을 생성했다.
  - 같은 날짜에 '매수', '매도' 건이 두 개 있을 때 이 둘을 구별하기 위해 거래 ID 까지 사용한다.
- 이 예제에서는 주식 한 종목만 고려했지만, 윈도우 함수가 PARTITION 절을 사용하기 때문에 주식 종목별로 순번이 초기화되어 다른 모든 주식 종목에도 적용이 가능하다.
- 만약 '매도'와 '매수'의 횟수 차이가 있는 경우 어떻게 될까
  - 위 쿼리에서는 INNER 조인을 사용했기 때문에 초과된 로우는 제외될 것이다.
  - 처리 방식은 처리 방침에 따라 다르기 때문에 필요한 경우 초과된 매수 건까지 계산하기 위해 LEFT JOIN , FULL OUTER JOIN 을 사용하는 것도 고려할만 하다.

## 정리
- 탤리 테이블과 윈도우 함수를 함께 사용해 좀 더 다양한 방식으로 순번을 생성하거나 필요한 윈도우 공식을 다른 방식으로 기술할 수 있다.
- 난데없이 불쑥 등장하는 레코드를 생성해야 할 때는 탤리 테이블과 비동등 조인을 하면 유용하다.
