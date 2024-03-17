## 없는 데이터 조회
- 무엇이 최선인지 명확히 답할 수 없다.
  - DBMS 엔진마다 편향성이 다르기 때문
- Access 와 MySQL 구버전의 경우 좌절성 조인을 선호한다.
- 나머지(SQL Server) 는 EXISTS 를 선호한다.

```sql
SELECT p.ProductNumber, p.ProductName
FROM Products AS p
WHERE p.ProductNumber
          NOT IN (SELECT ProductNumber FROM Order_Details);
```
- 재고를 관리한다고 가정하고 잘 팔리지 않는 제품을 찾기 위한 쿼리이다.
- 판매된 제품 목록에 해당하지 않는 제품들을 조회한다.
- 하지만 이는 효율적인 쿼리는 아니다.
  - 서브쿼리에서 Order_Details 테이블 전체를 검색해 판맨된 제품 목록을 추출하고, 중복된 값을 걸러 Products 테이블의 각 ProductNumber 값을 이 서브쿼리 목록과 일일이 비교해야 한다.

```sql
SELECT p.ProductNumber, p.ProductName
FROM Products AS p
WHERE NOT EXISTS
          (SELECT *
           FROM Order_Details AS od
           WHERE od.ProductNumber = p.ProductNumber);
```
- EXISTS 연산자를 이용해 개선하였다.
- 이론상 EXISTS 연산자는 NOT IN 보다 빠르다.
  - 서브쿼리가 반환하는 결과 집합이 클 때 더욱 빠르다.
  - 쿼리 엔진이 조건에 일치하는 레코드를 발견하면 동일한 레코드에서는 이후 비교 작업을 더는 진행하지 않기 때문

```sql
SELECT p.ProductNumber, p.ProductName
FROM Products AS p
         LEFT JOIN Order_Details AS od
                   ON p.ProductNumber = od.ProductNumber
WHERE od.ProductNumber IS NULL;
```
- LEFT JOIN 을 사용하고 WHERE 절에서 NULL 값을 찾는다.
  - 좌절성 조인 이라고도 부른다.
  
## 정리
- NOT IN 연산자는 이해하기는 쉽지만 일반적으로 가장 효율적인 접근 방법은 아니다.
- NOT IN 보다는 NOT EXISTS 연산자를 사용하는 것이 더 빠르다.
- '좌절성 조인' 이 효율적일 때도 있지만 DBMS 가 NULL 을 처리하는 방법에 따라 달라진다.
- 특정한 상황에 최선의 방법을 찾으려면 DBMS 쿼리 분석기를 사용한다.
