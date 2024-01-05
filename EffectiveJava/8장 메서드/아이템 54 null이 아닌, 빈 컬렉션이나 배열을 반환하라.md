## 예외시 null 을 반환할 때의 문제
```java
private final List<Cheese> cheesesInStock = ...;
public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? null : new ArrayList<>(cheesesInStock);
}
```
- `Stock` 이 비어있다면 `null` 을 리턴하도록 구성된 코드이다.

```java
List<Cheese> cheeses = shop.getCheeses();
if (cheeses != null && cheeses.contains(Cheese.STILTON)) {
    System.out.println("!!");
}
```
- 사용하는 쪽에서는 이와 같이 `null` 여부를 확인해야만 한다.
- 만약 `null` 체크를 누락하는 경우 언제 어떻게 에러가 발생할 지 모르게 된다.
- `null` 체크와 같은 특수한 상황에 대해서 예외처리를 하는 것 보다 동일한 로직을 수행할 수 있도록 하는 것이 좋은 것 같다.
  - 여담으로 최근 실무에서 작성하는 코드들을 이와 같이 변경한 적이 있다.
  - 예외인 경우의 리턴 값으로 분기를 하는 것이 아닌 같은 타입을 리턴하되 동작에서 예외로서 동작할 수 있도록 작성한다.
- 일부는 빈 컨테이너를 생성하는 비용보다 `null` 을 반환하는 것이 낫다고 생각하는 사람도 있다.

## 빈 컨테이너보다 null 을 반환하는 것이 낫다는 주장에 대한 반론
- 빈 컨테이너의 생성에 사용되는 자원 때문에 성능에 영향이 생긴다?
  - 성능 분석 결과로 이 할당이 성능 저하의 주범이라고 확인되지 않는 한 성능 차이는 크지 않다.
- 빈 컬렉션과 배열은 새로 할당하지 않고 반환이 가능하다.
  - 정적으로 할당된 불변 컬렉션을 반환함으로서 해결이 가능하다.
  - `Collections.emptyList` 메서드가 대표적인 예이다.
```java
public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? Collections.emptyList() : new ArrayList<>(cheesesInStock);
}
```
- 이와 같이 개선할 수 있다.
- 배열 또한 같은 방식으로 리턴 가능하다.

## 정리
- `null` 이 아닌, 빈 배열이나 컬렉션을 반환하라.
- `null` 을 반환하는 API 는 사용하기 어렵고 오류 처리 코드도 늘어난다.
  - 그렇다고 성능이 좋지도 않다.
