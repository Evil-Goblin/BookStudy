## 탤리 테이블 응용
![탤리테이블이_포함된_판매_주문_데이터베이스_설계](https://github.com/Evil-Goblin/BookStudy/assets/74400861/37bdb496-2bc2-47ed-99a5-c511dccf021f)
- 예제에 사용될 데이터베이스이다.
  - 탤리 테이블 두 개가 추가되어있다.
- 2015년 12월 구매액을 기준으로 할인 쿠폰을 지급하려고한다.
  - 1000달러 이상 구매 고객에게는 한 장
  - 2000달러 이상 구매 고객에게는 두 장
  - 5000달러 이상 구매 고객에게는 네 장
  - 5만달러 이상 구매 고객에게는 50장

![ztblPurchaseCoupons_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/973f29c2-0c34-4b3f-adaf-0d40198776a0)
- 구매 금액의 범위에 따른 쿠폰 개수를 담는 탤리 테이블 ztblPurchaseCoupons 테이블의 내용이다.
- 외에도 1부터 60까지 정수가 오름차순으로 들어 있는 ztblSeqNumbers 탤리 테이블이 있다.

```sql
WITH CustDecPurch AS
         (SELECT Orders.CustomerID,
                 SUM((QuotedPrice) * (QuantityOrdered)) AS Purchase
          FROM Orders
                   INNER JOIN Order_Details
                              ON Orders.OrderNumber = Order_Details.OrderNumber
          WHERE Orders.OrderDate Between '2015-12-01'
                    AND '2015-12-31'
          GROUP BY Orders.CustomerID),
     Coupons AS
         (SELECT CustDecPurch.CustomerID, ztblPurchaseCoupons.NumCoupons
          FROM CustDecPurch
                   CROSS JOIN ztblPurchaseCoupons
          WHERE CustDecPurch.Purchase BETWEEN
                    ztblPurchaseCoupons.LowSpend AND
                    ztblPurchaseCoupons.HighSpend)
SELECT C.CustFirstName,
       C.CustLastName,
       C.CustStreetAddress,
       C.CustCity,
       C.CustState,
       C.CustZipCode,
       CP.NumCoupons
FROM Coupons AS CP
         INNER JOIN Customers AS C
                    ON CP.CustomerID = C.CustomerID
         CROSS JOIN ztblSeqNumbers AS z
WHERE z.Sequence <= CP.NumCoupons;
```
- 2015년 12월 구매한 고객별 총 구매액을 알아야 하기 때문에 첫 번째 탤리 테이블에 있는 값을 찾은 후 NumCoupons 컬럼 값으로 고객별로 로우를 여러 개 만들어야 한다.
- 먼저 고객별 총 구매 금액을 계산하기 위한 CustDecPurch CTE 를 만든다.
- CustDecPurch 를 이용해 쿠폰 개수를 찾는 Coupons CTE 를 만든다.
- 마지막으로 쿠폰을 받을 고객을 식별하고, 쿠폰의 개수를 알아낸다.
- 이 쿼리는 쿠폰의 개수를 기준으로 적합한 배수만큼 반복하여 이름과 주소를 생성한다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/d7a136f1-d7e4-4c4d-8c48-a80d046886e9)
- 조회 결과의 일부이다.
  - NumCoupons 컬럼 값에 따라 고객 로우가 반복적으로 나온다.
- 탤리 테이블 하나로는 특정 고객이 받을 쿠폰 개수를 계산했다.
- 다른 탤리 테이블로는 쿠폰 한 장마다 고객 한 명을 할당해 로우로 만들고, 이 쿠폰 개수를 해당 고객 로우에 각각 넣었다.
- CTE 와 복잡한 CASE 표현식을 사용해 범위 값도 생성할 수 있는데, 오직 한 번만 실행할 때는 이 방법도 사용할 만하다.
  - 하지만 만약 다른 그룹을 이용해 동일한 작업을 수행한다고 했을때, CTE 와 CASE 표현식에서 코드를 수정하는 방법보다 탤리 테이블의 값을 변경하는 것이 훨씬 간단하다.

## 정리
- 데이터베이스에 없는 다른 값을 생성할 때는 탤리 테이블을 사용한다.
- 탤리 테이블에 범위 값이 있을 때 이 범위 값과 기존 데이터를 비교해 계산 값을 생성할 수 있다.
- 연속적인 값이 있는 탤리 테이블을 사용해 다른 탤리 테이블에 있는 값을 기준으로 로우를 생성할 수 있다.
