## for-each
- 향상된 `for` 문(enhanced for statement)
```java
for (Element e : elements) {
    ...
}
```
- 일반 `for` 문과 달리 하나의 관용구만으로 처리가 가능하다.
- 다중 반복시에 그 효과가 더 커진다.
```java
enum Face { ONE, TWO, THREE, FOUR, FIVE, SIX }
Collection<Face> faces = EnumSet.allOf(Face.class);

for (Iterator<Face> i = faces.iterator(); i.hasNext(); ) {
    for (Iterator<Face> j = faces.iterator(); j.hasNext(); ) {
        System.out.println(i.next() + "" + j.next());
    }
}
```
- 기존의 `for` 문을 사용하는 경우 이와 같이 잘못사용될 여지가 있고 코드도 지저분해진다.

```java
for (Suit suit : suits)
    for (Rank rank : ranks)
        deck.add(new Card(suit, rank));
```
- `for-each` 문을 사용하면 이와같이 간단히 해결 가능하다.

## for-each 를 사용할 수 없는 경우
- 파괴적인 필터링
  - 컬렉션을 순회하면서 선택된 원소를 제거해야하는 경우 반복자의 `remove` 메서드를 호출해야한다.
  - 자바 8 부터는 `Collection` 의 `removeIf` 메서드를 이용해 컬렉션을 명시적으로 순회하는 일을 피할 수 있다.
- 변형
  - 리스트나 배열을 순회하면서 그 원소의 값 일부 혹은 전체를 교체해야 한다면 리스트의 반복자나 배열의 인덱스를 사용해야 한다.
- 병렬 반복
  - 여러 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해 엄격하고 명시적으로 제어해야 한다.

이러한 경우 일반적인 `for` 문을 사용하되 `for` 문의 문제점을 경계해야한다.

## for-each 와 Iterable
- `for-each` 문은 컬렉션과 배열은 물론 `Iterable` 인터페이스를 구현한 객체라면 무엇이든 순회가 가능하다.
- `Iterable` 을 처음부터 직접 구현하기는 까다롭지만, 원소들의 묶음을 표현하는 타입을 작성해야 한다면 `Iterable` 을 구현하는 쪽으로 고민할 필요가 있다.
  - 해당 타입에서 `Collection` 인터페이스는 구현하지 않기로 했더라도

## 정리
- 전통적인 `for` 문과 비교했을 때 `for-each` 문은 명료하고, 유연하고, 버그를 예방해준다.
  - 성능 저하도 없다.
- 가능한 모든 곳에서 `for` 문이 아닌 `for-each` 문을 사용하자.
