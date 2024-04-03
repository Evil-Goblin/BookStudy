## ROW_NUMBER() , RANK()
- ROW_NUMBER() , RANK() 같은 집계 함수는 OVER 절과 함께 사용해야 한다.
  - 어떤 데이터가 다른 데이터보다 높은 순위인지 정의하지 않고서는 순위를 매길 수 없기 때문

```sql
SELECT 
  ROW_NUMBER() OVER (
    ORDER BY o.OrderDate
    ) AS OrderSequence,
  ROW_NUMBER() OVER (
    PARTITION BY o.CustomerID
    ORDER BY o.OrderDate
    ) AS CustomerOrderSequence,
  o.OrderNumber, o.CustomerID, o.OrderDate, o.OrderTotal,
  RANK() OVER (
    ORDER BY o.OrderTotal DESC
  ) AS OrderRanking,
  RANK() OVER (
    PARTITION BY o.CustomerID
    ORDER BY o.OrderTotal DESC
    ) AS CustomerOrderRanking
FROM Orders AS o
ORDER BY o.OrderDate;
```
- ROW_NUMBER() 와 RANK() 함수를 사용한 쿼리이다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/a2b87f48-82cf-4c51-94dc-ca8b08584feb)
- 해당 쿼리의 조회 결과이다.
- PARTITION BY 조건은 순위 함수가 효율적으로 그루핑하는 데 영향을 미친다.
  - OrderSequence 윈도우는 전체 데이터집합에 적용되는 반면, CustomerOrderSequence 윈도우는 CustomerID 로 그루핑한 집합에 적용된다.
  - 따라서 고객 순위를 매길 때 두 번째 ROW_NUMBER() 가 반환하는 순번은 '재시작'되어 해당 고객의 첫 번째, 두 번째 등 주문을 식별한다.
- RANK() 함수는 동일한 ORDER BY 조건을 사용하지 않는다.
  - 금액별 순위를 보기 원하므로 금액의 크고 적음에 따라 어떤 로우가 첫 번째, 두 번째 순위인지 결정된다.
  - ROW_NUMBER() 에서 처리했듯이, 그룹별로 순위를 분할해서 특정 고객에 대해 어떤 주문이 가장 큰지 식별할 수 있게 한다.
  - CustomerOrderRanking 윈도우는 고객별로 가장 큰 주문, 두 번째 주문 등이 어떤 주문인지 알 수 있게 분할한다.
- 순위가 동일한 로우에서 RANK() 함수의 연산이 어떻게 수행되는지 파악하는 것도 중요하다.
  - OrderNumber 기준 (1, 9) , (2, 10) 의 OrderTotal 이 같다.
  - 때문에 OrderRanking 값에는 7, 9가 빠져있다.
    - 6, 8순위의 주문이 순위를 공유하고 있기 때문이다.
  - 만약 동일 순위를 만들고 싶지 않다면 DENSE_RANK() 함수를 사용해도 된다.
    - 또는 동일 순위를 만들지 않도록 OVER 절을 다시 작성할 수도 있다.
- 이 함수들은 ORDER BY 조건을 필요로 한다.
  - 만약 ORDER BY 조건에 다른 컬럼을 명시하면 다른 결과를 반환한다.

## 정리
- ROW_NUMBER(), RANK() 를 비롯한 순위 함수는 항상 윈도우에서 사용해야 하므로 OVER 절 없이는 사용할 수 없다.
- 순위 함수를 사용할 때는 동일한 순위를 어떻게 처리할지 고려해야 한다.
  - 연속적인 순위를 보려면 DENSE_RANK() 함수를 사용한다.
- 순위 함수에서는 ORDER BY 조건을 반드시 사용해야 한다.
  - 순번이나 순위를 매기는 데 영향을 미치기 때문
