## COUNT() 집계 함수의 용도
- COUNT(*)
  - NULL 값과 중복된 값을 포함해 한 그룹의 항목 개수를 반환한다.
- COUNT(ALL <표현식>)
  - ALL 은 기본값이기 때문에 주로 COUNT(<표현식>)으로 사용한다.
  - 한 그룹의 각 로우에 있는 표현식을 평가해 NULL 이 아닌 값의 개수를 반환한다.
- COUNT(DISTINCT <표현식>)
  - 한 그룹에 있는 각 로우에 대해 지정한 표현식을 평가해서 유일하고 NULL 이 아닌 값의 개수를 반환한다.

보통 <표현식>에는 필드 이름이 오지만, 단일 데이터 값을 얻으려고 기호나 연산자의 조합이 올 수도 있다.

### 예제
![샘플_데이터](https://github.com/Evil-Goblin/BookStudy/assets/74400861/8ec86aad-19ba-45aa-aacc-7abe85a159cd)
- 예제에 사용할 샘플 데이터이다.
- `COUNT(*)` 을 사용하면 테이블의 로우 개수가 25개라는 것을 알 수 있다.
- 이 테이블의 모든 로우에는 CustomerID 컬럼 값이 있으므로 `COUNT(CustomerID)` 를 사용해도 동일한 25가 반환된다.
- 하지만 `COUNT(EmployeeID)` 를 사용하면 20이 반환된다.
  - EmployeeID 에 NULL 값을 가진 로우가 5개 있기 때문이다.
- `COUNT(DISTINCT CustomerID)` 를 사용하면 로우 25개 중 겹치치 않는 CustomerID 값이 18개 있음을 알 수 있다.

```sql
SELECT COUNT(*) AS TotalOrders
FROM OrdersTable
WHERE OrderTotal > 1000;
```
- 1,000.00 달러를 초과하는 주문을 검색하는 쿼리이다.
- `COUNT(CASE WHEN OrderTotal > 1000 THEN CustomerID END)` 를 사용해도 동일한 결과를 반환한다.
  - CASE 와 DISTINCT 를 결합해 사용하는 것도 가능하다.
  - `COUNT(DISTINCT CASE WHEN OrderTotal > 1000 THEN CustomerID END)` 를 통해 1,000.00 달러를 초과하는 주문 그룹에서 겹치지 않는 고객 데이터 15개를 반환한다.

```sql
SELECT COUNT(*) AS TotalRows,
  COUNT(CustomerID) AS TotalOrdersWithCustomers,
  COUNT(EmployeeID) AS TotalOrdersWithEmployees,
  COUNT(DISTINCT CustomerID) AS TotalUniqueCustomers,
  COUNT(CASE WHEN OrderTotal > 1000 THEN CustomerID END) AS TotalLargeOrders,
  COUNT(DISTINCT CASE WHEN OrderTotal > 1000 THEN CustomerID END) AS TotalUniqueCust_LargeOrders
FROM OrdersTable;
```
- 단일 쿼리에서 COUNT() 를 여러 개 사용해도 반환되는 로우는 한 개뿐이다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/a72c8136-0218-4f47-8bea-560d3ea9a62a)
- COUNT() 가 여러 개 있는 쿼리를 반환한 결과이다.

> COUNT() 함수는 정수 값을 반환하는데 반환 값이 2,147,483,647로 제한된다는 것을 의미한다.(signed int)  
> DB2, SQL Server 에서는 bigint 값을 반환하는 COUNT_BIG() 함수를 제공하는데, 반환 가능한 최댓값은 9,223,372,036,854,775,807 이다.(signed long long)  
> Access 에서는 COUNT() 와 DISTINCT 를 결합해서 사용할 수 없다.

## 정리
- 적절한 형식으로 COUNT() 함수를 사용하면 계산을 단순화할 수 있다.
- WHERE 절을 사용하지 않고 여러 계산을 결합하려면 COUNT() 함수의 매개변수로 다른 함수를 사용하는 방안을 고려한다.
