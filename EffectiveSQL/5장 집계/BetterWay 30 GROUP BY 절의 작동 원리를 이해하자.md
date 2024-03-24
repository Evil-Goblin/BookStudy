## GROUP BY
- 특정 유형으로 집계하려면 데이터를 분할해 그룹(그루핑 컬럼을 동일한 값별로 묶은 로우의 집합)으로 묶어야 할 때가 종종 있다.
  - GROUP BY 절로 구현할 수 있다.
  - 필요에 따라 HAVING 절이 오기도 한다.
- GROUP BY 절에 기술된 컬럼을 그루핑 컬럼이라고 하는데, 이 컬럼들이 SELECT 절에 꼭 포함되어야 하는 것은 아니다.
  - GROUP BY 절에서는 별칭을 사용할 수 없다.

```sql
SELECT select_list
FROM table_source
[ WHERE search_condition ]
[ GROUP BY group_by_expression ]
[ HAVING search_condition ]
[ ORDER BY order_expression [ ASC | DESC ] ]
```
- select 문에 사용하는 일반적인 구문이다.

> ISO SQL 표준에는 FROM 절이 없는 SELECT 문은 표준에 부합하지 않는다고 명시하지만, 많은 DBMS 에서 FROM 절을 옵션 항목으로 허용한다.

### 집계 쿼리 순서
1. FROM 절에서 데이터 집합을 만든다.
2. WHERE 절은 FROM 절에서 만든 데이터 집합을 조건에 맞게 걸러 낸다.
3. GROUP BY 절은 WHERE 절에서 필터링한(조건에 맞는 데이터를 걸러 낸) 데이터 집합을 집계한다.
4. HAVING 절은 GROUP BY 절에서 집계한 데이터 집합을 다시 조건에 맞게 필터링한다.
5. SELECT 절은 집계하고 필터링한 데이터 집합을 변환(보통 집계 함수로 처리)한다.
6. ORDER BY 절은 변환된 데이터 집합을 정렬한다.

### 집계 함수
- SELECT 절에는 있지만 GROUP BY 절에는 기술되지 않은 컬럼들은 반드시 집계 함수를 사용해야 한다.
  - 계산 결과가 집계나 상수 형태로 나올 수도 있다.
- 집계 함수는 일련의 값 집합에서 계산을 수행하고 단일 값을 반환하는 결정적 함수다.
  - 계산이 수행된 값 집합은 GROUP BY 절의 결과가 된다.
- 각 그룹에서는 한 개 이상의 집계 함수를 사용할 수 있는데, 해당 그룹에 있는 모든 로우에 대해 계산을 수행한다.
  - 집계 함수를 사용하지 않을 때 GROUP BY 절은 SELECT DISTINCT 와 동일하게 수행된다.

#### ISO SQL 표준 집계 함수
- COUNT()
  - 집합이나 그룹에 있는 로우의 개수를 반환한다.
- SUM()
  - 집합이나 그룹에 있는 값의 합계를 반환한다.
- AVG()
  - 집합이나 그룹에 있는 수치 값들의 평균을 반환한다.
- MIN()
  - 집합이나 그룹에 있는 가장 작은 값을 반환한다.
- MAX()
  - 집합이나 그룹에 있는 가장 큰 값을 반환한다.
- VAR_POP() , VAR_SAMP()
  - 집합이나 그룹에 있는 특정 컬럼에 대한 모 분산과 표본 분산 값을 반환한다.
- STDDEV_POP() , STDDEV_SAMP()
  - 집합이나 그룹에 있는 특정 커럼에 대한 모 표준 편차와 표본 표준 편차 값을 반환한다.

### GROUP BY 절 사용 예
- SELECT 절에 기술한 컬럼의 형태는 GROUP BY 절에 있는 컬럼의 형태에 영향을 준다.
  - 집계 함수에서 사용되지 않은 SELECT 절의 컬럼은 반드시 GROUP BY 절에서 사용해야 하기 때문

```sql
SELECT ColumnA, ColumnB
FROM Table1
GROUP BY ColumnA, ColumnB;

SELECT ColumnA + ColumnB
FROM Table1
GROUP BY ColumnA, ColumnB;

SELECT ColumnA + ColumnB
FROM Table1
GROUP BY ColumnA + ColumnB;

SELECT ColumnA + ColumnB + 상수
FROM Table1
GROUP BY ColumnA, ColumnB;

SELECT ColumnA + ColumnB + 상수
FROM Table1
GROUP BY ColumnA + ColumnB;

SELECT ColumnA + 상수 + ColumnB
FROM Table1
GROUP BY ColumnA, ColumnB;
```
- GROUP BY 절을 올바르게 사용한 예시이다.

```sql
SELECT ColumnA, ColumnB
FROM Table1
GROUP BY ColumnA + ColumnB;

SELECT ColumnA + 상수 + ColumnB
FROM Table1
GROUP BY ColumnA + ColumnB;
```
- 위와 같이 SELECT 절의 컬럼 표시 형태와 그루핑이 일치하지 않으면 그루핑은 허용되지 않는다.

### GROUP BY 정렬
- ISO SQL 표준에 따르면 GROUP BY 절은 결과 집합을 정렬하지 않는다.
- 정렬하기 위해선 ORDER BY 절을 사용해야 한다.
- 대부분의 DBMS 는 별도로 순서를 지정하지 않으면 GROUP BY 절에 사용된 컬럼에서 임시로 인덱스를 생성한 후 GROUP BY 절에 있는 컬럼 순서대로 정렬해 결과를 반환한다.
  - 하지만 결과 집합의 순서가 중요할 때는 반드시 원하는 순서대로 정렬하도록 ORDER BY 절을 기술해야 한다.

### GROUP BY 필터링
- 가능하면 데이터는 WHERE 절을 사용해 필터링한다.
  - 집계 대상의 데이터 양을 줄여주기 때문
- 집계된 결과에서 다시 필터링할 때는 `HAVING Count(*) > 5` 나 `HAVING Sum(Price) < 100` 처럼 HAVING 절을 사용해야 한다.

### ROLLUP , CUBE , GROUPING SETS
- ROLLUP , CUBE , GROUPING SETS 기능을 사용하면 좀 더 복잡한 형태의 그루핑 연산을 수행할 수 있다.
- 이들은 각 특정 그루핑 집합별로 집계 연산을 수행하고 이 특정 그룹별로 데이터를 분리해 FROM 과 WHERE 절에 따라 선택된 데이터를 집계한다.
- 그루핑 집합을 명시하지 않으면 집계 쿼리에서 GROUP BY 절을 포함하지 않은 것처럼 모든 로우가 단일 그룹으로 집계된다.

> Access , MySQL 을 포함해 몇몇 DBMS 는 ROLLUP 과 CUBE 기능을 사용한 그루핑을 지원하지 않는다.

![예시_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/47b36b4f-144f-42d7-bf5c-ce57eb8a9dcd)
- ROLLUP 을 사용하면 한 그룹에 있는 컬럼 집합별로 집계 값을 추가로 얻을 수 있다.

```sql
SELECT Color, Dimension, SUM(Quantity)
FROM Inventory
GROUP BY ROLLUP (Color, Dimension);
```
![쿼리_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/9b10a566-25fc-4d6e-aa64-039c02c5cd2e)
- ROLLUP 을 사용한 집계 결과이다.
- 색상별 총 수량과 전체 수량을 얻을 수 있다.
- 하지만 Dimension 별 총 수량은 얻지 못한다.
  - ROLLUP 이 오른쪽에서 왼쪽으로 연산하기 때문이다.

```sql
SELECT Color, Dimension, SUM(Quantity)
FROM Inventory
GROUP BY CUBE (Color, Dimension);
```
![쿼리_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/c85b7256-d052-463b-b232-9ab1a76b227c)
- 추가로 집계하려면 CUBE 를 사용한다.

```sql
SELECT Color, Dimension, SUM(Quantity)
FROM Inventory
GROUP BY GROUPING SETS ((Color), (Dimension), ());
```
![쿼리_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/1df6c652-6bdb-426c-a1d9-33f9c4ea1673)
- GROUPING SETS 을 사용한 결과이다.
- 집계 연산을 좀 더 확장해 추가적인 그루핑을 수행하고 싶다면 GROUPING SETS 을 사용한다.
- ROLLUP, CUBE 와 달리 모든 조합에서 원하는 값을 산출하려고 그루핑할 항목을 정확히 명시했다.
- ROLLUP 과 CUBE 뿐만 아니라 GROUPING SETS 은 쿼리 하나로 여러 쿼리를 UNION 으로 연결한 것과 동일한 결과를 산출한다.

```sql
SELECT Color, NULL AS Dimension, SUM(Quantity)
FROM Inventory
GROUP BY Color
UNION
SELECT NULL, Dimension, SUM(Quantity)
FROM Inventory
GROUP BY Dimension
UNION
SELECT NULL, NULL, SUM(Quantity)
FROM Inventory;
```
- 위는 이전의 GROUPING SETS 과 같은 결과를 얻기 위해 GROUP BY 문을 UNION 으로 연결한 쿼리이다.

## 정리
- 집계 수행 전 WHERE 절이 적용된다.
- GROUP BY 절은 필터링된 뎅이터 집합을 집계한다.
- HAVING 절은 집계된 데이터 집합을 다시 필터링한다.
- ORDER BY 절은 변형된 데이터 집합을 정렬한다.
- SELECT 절에서 집계 함수나 집계 계산에 포함되지 않은 컬럼은 GROUP BY 절에 명시해야 한다.
- ROLLUP, CUBE, GROUPING SETS 을 사용하면, 여러 집계 쿼리를 UNION 으로 연결하는 대신 쿼리 하나로 좀 더 가능한 조합 결과를 산출할 수 있다.
