## 나누기
- 나누기는 에드거 커드 박사가 쓴 `<The Relational Model for Database Management>(Addison-Wesley, 2000)` 에 정의된 여덟 가지 집합 연산 중 하나다.
- 이 연산은 큰 집합(피제수)을 작은 집합(제수)으로 나눠 몫을 구하는 것이다.
  - 피제수 집합의 모든 항목은 제수 집합과 완전히 일치한다.

### 나누기 문제 처리 시 문제점
- 주어진 작업에 대한 모든 요구 사항을 만족하는 작업을 모조리 찾아야 한다.
- 구성품을 만드는 데 필요한 모든 부품을 제공하는 공급자 목록을 모두 뽑아야 한다.
- 특정 집합의 제품을 주문하는 모든 고객 명단을 뽑아야 한다.

![나누기_연산](https://github.com/Evil-Goblin/BookStudy/assets/74400861/1ab66037-218b-4111-a69d-a2dcd01e3314)
- 바깥쪽 원은 고객이 구매한 모든 제품을 나타낸다.
- 작은 원 세 개는 특정 고객이 구매한 제품을 나타낸다.
  - 일부 제품은 고객 세명이 모두 구매했음을 알 수 있다.
- 가운데에 작은 원은 해당 제품을 구매한 고객을 찾으려고 관심을 기울여야 하는 제품이다.
- 이 예에서 고객 세 명은 관심 제품 집합에 있는 일부 항목을 구매했지만, A 고객은 관심 제품을 모두 구매했다.
  - 고객이 구매한 모든 제품을 관심 제품으로 나눈 결과는 관심 제품을 모두 구매한 A 고객이다.
- 나누기 연산은 단일 SQL 문으로 처리할 수 없다.
  - 원하는 결과를 얻기 위해 몇 가지 기능을 조합해 사용해야 한다.

```sql
CREATE VIEW CustomerProducts AS
SELECT DISTINCT c.CustomerID, c.CustFirstName, c.CustLastName, P.ProductName
FROM Customers AS c
       INNER JOIN Orders AS o
                  ON c.CustomerID = o.CustomerID
       INNER JOIN Order_Details AS od
                  ON o.OrderNumber = od.OrderNumber
       INNER JOIN Products AS p
                  ON p.ProductNumber = od.ProductNumber;
```
- 모든 고객과 그들이 구매한 제품이 있는 피제수 집합에 대한 뷰를 만드는 쿼리이다.
  - 한 곡개이 동일 제품을 여러 번 구매한 경우 이 제품과 고객당 로우 하나만 추출하기 위해 DISTINCT 를 사용한다.
- 제수 집합의 각 항목에 대한 서브쿼리와 IN 을 사용해 나누기 문제를 해결할 수도 있다.
  - 하지만 제수 집합이 작은 경우 수용 가능한 방법으로, 매우 큰 경우는 실행이 거의 불가능하다.
  - 때문에 피제수와 제수 집합에 대해 뷰를 만든다.

```sql
CREATE VIEW ProdsOfInterest AS
SELECT Products.ProductName
FROM Products
WHERE ProductName IN ('Skateboard', 'Helmet', 'Knee Pads', 'Gloves');
```
- 제수 집합(관심 제품)에 대한 뷰를 만드는 쿼리이다.

```sql
-- (서브쿼리를 사용해 고객 제품을 관심 제품으로 나누는 쿼리)
SELECT DISTINCT CP1.CUstomerID, CP1.CustFirstName, CP1.CustLastName
FROM CustomerProducts AS CP1
WHERE NOT EXISTS
        (SELECT ProductName
         FROM ProdsOfInterest AS PofI
         WHERE NOT EXISTS
                 (SELECT CustomerID
                  FROM CustomerProducts AS CP2
                  WHERE CP2.CustomerID = CP1.CustomerID
                    AND CP2.ProductName = PofI.ProductName));
```
- 나누기 연산을 수행하는 쿼리이다.
  - 이 방법은 커드 박사의 동료인 크리스 데이트(Chris Date) 가 쓴 `<The Database Relational Model: A Retrospective Review and Analysis>(Pearson, 2000)` 에 설명되어 있다.
- 이 쿼리는 고객 제품에서 제품 이름과 고객 ID 가 일치하는 로우가 아닌 것 중 관심 제품이 아닌 로우를 모두 추출한다.
  - 이 방법은 제수 집합(관심 제품) 이 비어 있을 때 쿼리는 모든 고객 제품 로우를 반환하는 부수 효과를 가진다.

```sql
-- (GROUP BY , HAVING 절을 사용해 두 집합을 분할)
SELECT CP.CustomerID, CP.CustFirstName, CP.CustLastName
FROM CustomerProducts AS CP
       CROSS JOIN ProdsOfInterest AS PofI
WHERE CP.ProductName = PofI.ProductName
GROUP BY CP.CustomerID, CP.CustFirstName, CP.CustLastName
HAVING COUNT(CP.ProductName) =
       (SELECT COUNT(ProductName) FROM ProdsOfInterest);
```
- GROUP BY 와 HAVING 절을 이용한 나누기 연산 구현이다.
- 이 방법은 조 셀코(Joe Celko) 가 쓴 `<SQL for Smarties: Advanced SQL Programming. Fifth Edition>(Morgan Kaufmann, 2014)` 에서 소개되었다.
- 이전 방법은 첫 번째 뷰에서 유일한 고객 체품을 가져오기 위해 DISTINCT 를 사용했다.
- 이 방법은 제품 개수를 사용해 문제를 해결하였다.
  - 제품 개수가 야기한 혼란으로 구매 내역을 중복해 가져오면 안 되기 때문
  - 예를 들어 스케이트보드와 헬멧과 장갑 두 쌍을(서로 다른 주문으로) 구매한 고객이 생성하는 로우의 개수는 네 개인데, 이 숫자는 관심 제품 뷰에 잇는 로우 건수와 일치해 혼란을 줄 수 있다.
  - DISTINCT 가 없으면 무릎 보호대를 구매하지 않은 고객도 결과로 선택되는 오류가 발생한다.
  - 두 번째 뷰에서는 제품 이름으로 Products 테이블에서 로우를 선택하므로 DISTINCT 가 필요 없다.
- 관심 제품에 있는 로우와 일치하는 모든 고객의 제품 로우를 찾았지만, 개수를 비교해 관심 제품에 있는 제품의 로우 개수와 일치하는 로우만 추출한다.
  - 제수 집합이 비어 있을 때 이 쿼리가 반환하는 로우의 개수는 0이며, 첫 번째 방법과는 다른 결과가 나온다.

## 정리
- 나누기는 여덟 가지 관계형 집합 연산 중 하나이지만, SQL 표준이나 주요 데이터베이스 시스템 모두에서 DIVIDE 키워드는 지원하지 않는다.
- 나누기 방법을 이용하면 두 번째 집합에 있는 모든 로우와 일치하는 첫 번째 집합의 로우를 찾을 수 있다.
- 제수 집합에 있는 각 로우를 검사하고(IN 을 사용한 서브쿼리), NOT EXISTS 와 GROUP BY/HAVING 절을 사용해 나누기 연산을 수행할 수 있다.
