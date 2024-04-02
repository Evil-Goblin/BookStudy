## 윈도우 함수
- SQL:2003 표준 이전에 정한 SQL 표준은 인접한 로우에 의존한 데이터를 다루지 못했다.
  - 이론적으로 주어진 조건과 일치하는 한 로우의 순서는 중요하지 않았다.

![이동_합계를_구한_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/c602ba5f-293c-486f-acf4-51daa3ca0906)
- SQL:2003 표준 이전에는 이 결과를 뽑는 쿼리를 작성하기가 매우 어려웠다.
  - 만들었다 해도 매우 비효율적이고 느리게 수행되었다.
- SQL:2003 표준에서 윈도우 함수 개념이 소개되었다.
  - ROW_NUMBER(), RANK() 함수도 추가되었다.
    - 이들은 윈도우 함수로만 사용할 수 있다.
- 윈도우
  - 대상 로우를 둘러싼 로우(해당 로우 앞이나 뒤에 있는 로우)의 집합을 의미한다.
  - SUM(), COUNT(), AVG() 등 집계 함수를 윈도우 함수로 사용할 수 있다.

```sql
SELECT
  o.OrderNumber, o.CustomerID, o.OrderTotal,
  SUM(o.OrderTotal) OVER (
    PARTITION BY o.CustomerID
    ORDER BY o.OrderNumber, o.CustomerID
    ) AS TotalByCustomer,
  SUM(o.OrderTotal) OVER (
    ORDER BY o.OrderNumber
    ) AS TotalOverall
FROM Orders AS o
ORDER BY o.OrderNumber, o.CustomerID;
```
- 이동 합계를 구하는 쿼리를 윈도우 함수로 구현하였다.
- 윈도우 함수 SUM() 을 사용한다.
- OVER 절에는 PARTITION BY 와 ORDER BY 를 사용한다.
  - PARTITION BY 는 윈도우를 어떻게 분할하는지 명시한다.
  - 이것을 생략하면 데이터베이스 시스템은 윈도우 함수를 전체 결과 집합에 적용한다.
  - TotalByCustomer 는 o.CustomerID 를 명시하는데, o.CustomerID 값이 같은 로우들에 SUM() 을 적용해야한다는 의미이다.
  - 이는 개념적으로 GROUP BY 절과 유사하지만, PARTITION 조건이 SUM() 함수 연산 대상이 되는 윈도우에만 그루핑을 수행해 독립적으로 적용된다는 주된 차이점이 있다.
  - 반면 GROUP BY 절은 전체 쿼리에 적용되며 그루핑 대상이나 집계 대상이 아닌 컬럼은 참조할 수 없다.
- TotalOverall 에는 PARTITION BY 절이 없다.
  - 쿼리가 반환하는 로우의 전체 집합에 대해 그루핑하는 것과 기능적으로 같다.
  - 즉, GROUP BY 절을 뺐을 때와 같다.

```sql
SELECT 
  t.AccountID, t.Amount,
  SUM(t.Amount) OVER (
    PARTITION BY t.AccountID
    ORDER BY t.TransactionID DESC
    ) - t.Amount AS TotalUnspent,
  SUM(t.Amount) OVER (
    ORDER BY t.TransactionID
    ) AS TotalOverall
FROM Transactions AS t
ORDER BY t.TransactionID;
```
- OVER 절 내에 정의된 조건은 다를 수 있다.
- 위의 쿼리는 OVER 절에서 서로 다른 조건을 가지는 쿼리이다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/ce4734a7-ff92-4e10-83ca-684f40fa3367)
- 위 쿼리의 조회 결과이다.
- 만약 윈도우 함수를 사용하지 않고 이 조회 결과를 얻으려면 개별적으로 각 윈도우를 계산하는 중첩된 SELECT 문을 여러 개 사용해야 할 것 이다.
- 윈도우 함수를 사용하면 각 OVER 절에 PARTITION BY 와 ORDER BY 를 명시할 수 있으므로, 문장 수준의 GROUP BY 절을 고수할 필요 없이 단일 문장만으로도 여러 데이터 범위에 걸쳐 집계할 수 있다.

## 정리
- 윈도우 함수는 주변을 둘러싼 로우를 인지하므로, 전통적인 집계 함수와 문장 수준의 그루핑보다는 쉽게 이동 집계 연산을 할 수 있다.
- 윈도우 함수는 데이터에 다르게 그리고 독립적으로 적용해야 하는 집계에 제시할 수 있는 훌륭한 대안이다.
- 윈도우 함수는 SUM(), COUNT(), AVG() 같은 기존 집계 함수와 함께 사용할 수 있으며, OVER 절과도 함께 사용한다.
- PARTITION BY 조건은 집계 표현식을 적용해야 하는 그룹을 명시하는 데 사용한다.
- ORDER BY 조건은 뒤에 이어서 나오는 로우들의 집계 표현식 계산을 수행하는 방법에 영향을 주므로 중요하다.
