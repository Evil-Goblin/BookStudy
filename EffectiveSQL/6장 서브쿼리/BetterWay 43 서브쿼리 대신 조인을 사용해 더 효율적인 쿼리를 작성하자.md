## 서브쿼리 대신 조인
- 이전 장(BetterWay 41) 에서는 조인보다는 서브쿼리가 더 성능이 낫다고 기술되어있었다.(적어도 SQL Server 에서는)
- 아마도 서브쿼리보다 조인의 성능이 좋은 경우에 대한 예시를 다루는 내용 같다.

![맥주_스타일_데이터_모델](https://github.com/Evil-Goblin/BookStudy/assets/74400861/19113a85-9671-4169-963e-15d1cb169fb7)
- 예제에 사용될 데이터이다.

```tsql
SELECT StyleNm
FROM Styles
WHERE CountryFK IN 
  (SELECT CountryID 
  FROM Countries 
  WHERE CountryNM = 'Belgium');
```
- 서브쿼리를 사용해 벨기에산 맥주를 조회하는 쿼리이다.
- Countries 테이블에 대한 서브쿼리를 실행해 ID 값을 가져온 후 IN 절을 사용해 Styles 테이블에서 이 값과 같은 CountryFK 값을 가진 로우를 가져온다.
- 하지만 IN 절 조건에서 서브쿼리가 반환한 값과 Styles 테이블에서 값이 동일한 로우를 찾으려면 서브쿼리를 먼저 실행해야 한다.
  - 서브쿼리에 있는 테이블의 크기가 매우 작은 것이 아니라면, 조인을 사용하는 것이 더 효율적이다.
    - 물론 이 경우에는 매우 작다.
  - 데이터베이스 엔진은 조인에 대한 최적화를 더 잘 수행하기 때문

```tsql
SELECT S.StyleNm
FROM Styles AS S INNER JOIN Countries AS C
  ON S.CountryFK = C.CountryID
WHERE C.CountryNM = 'Belgium';
```
- 조인을 사용해 벨기에산 맥주를 조화하는 쿼리이다.
- 조인에서 유의해야 할 점이 있다.
  - 조인은 잠재적으로 결과를 변경할 수 있다는 점이다.
  - 두 테이블 중 한쪽에 불필요하게 중복된 값이 있을 때(Belgium 이라는 국가 이름이 여러 개 있는 경우) 원하는 결과가 반환되지 않을 수 있다.

```tsql
SELECT S.StyleNm
FROM Styles AS S
WHERE EXISTS (SELECT NULL FROM Countries WHERE CountryNM = 'Belgium' 
  AND Countries.CountryID = S.CountryFK);
```
- 서브쿼리가 아닌 EXISTS 절을 사용하는 방법도 있다.
- 이 방법을 사용하면 조인으로 중복된 결과를 생성하는 잠재적인 문제를 피할 수 있다.
- 조인이나 서브쿼리만큼 직관적으로 보이지는 않지만, 데이터베이스는 서브쿼리 전체를 처리하지 않고 단순히 명시된 관계 정보만 확인해 true, false 만 반환하면 된다.
- EXISTS 연산자를 사용했으니 서브쿼리가 수반되어야 하지만, 옵티마이저는 이 서브쿼리를 세미조인으로 변환한다.

```tsql
SELECT S.StyleNm
FROM Styles AS S LEFT JOIN Countries AS C
  ON S.CountryFK = C.CountryID
WHERE C.CountryNM = 'Belgium'
  OR C.CountryNM IS NULL;
```
- 조인이 선호되는 경우도 있다.
  - 예제에서 사용한 Countries 테이블에는 컬럼이 두 개뿐이지만, 두 번째 테이블에 있는 컬럼을 포함해야 한다면 조인을 사용한다.
  - 게다가 외래키에 값이 없을 가능성이 있다면, 위 예제와 같이 조건과 일치하거나 값이 없는 로우를 가져오는 데 LEFT 조인을 사용한다.

## 정리
- 주어진 문제를 절차적인 순서대로 분해해서 해결해야 바람직한 방법이라는 생각은 금물이다.
  - SQL 은 로우별이 아닌 데이터 집합을 대상으로 최적의 작업을 수행한다.
- 다양한 접근법을 어떻게 처리하는지 점검해서 DBMS 의 옵티마이저가 선호하는 해결책을 결정한다.
- 조인을 사용할 때는 적절한 인덱스가 있는지 확인한다.
