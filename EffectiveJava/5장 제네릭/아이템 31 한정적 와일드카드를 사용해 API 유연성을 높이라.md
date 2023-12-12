## 매개변수화 타입은 불공변이다.
- `List<Type1>` 과 `List<Type2>` 는 서로의 상위 타입도 하위 타입도 아니다.

```java
public class Stack<E> {
    public Stack();
    public void push(E e);
    public E pop();
    public boolean isEmpty();
}

public void pushAll(Iterable<E> src) {
    for (E e : src)
        push(e);
}
```
- 위의 제네릭 예제의 경우 컴파일은 문제없이 되지만 완벽하지 않다.
- `Iterable src` 의 원소 타입이 스택의 원소 타입과 일치하면 잘 동작한다.
- 하지만 `Stack<Number>` 로 선언한 후 `pushAll(int)` 로 호출하면 오류메세지가 발생한다.
  - `java: incompatible types: java.lang.Iterable<java.lang.Integer> cannot be converted to java.lang.Iterable<java.lang.Number>`

## 한정적 와일드카드 타입
- 위 예제와 같은 상황을 방지하기 위한 한정적 와일드카드 타입이라는 특별한 매개변수화 타입이 있다.
- `Iterable<? extends E>` 한정적 와일드카드 타입을 이용하여 이를 개선할 수 있다.
  - `pushAll` 의 입력 매개변수 타입은 `E` 의 `Iterable` 이 아닌 `E` 의 하위 타입의 `Iterable` 이어야 한다.
```java
public void pushAll(Iterable<? extends E> src) {
    for (E e : src)
        push(e);
}
```
- 매개변수 `src` 를 `E` 타입 자료구조에 넣는 함수이기 때문에 `E` 의 하위 타입이어야 한다.
- `E` 가 `src` 의 부모 타입이어야 `E` 타입 컬렉션에 `src` 의 요소들을 입력할 수 있다. 

```java
public void popAll(Collection<E> dst) {
    while (!isEmpty())
        dst.add(pop());
}

Stack<Number> numberStack = new Stack<>();
Collection<Object> objects = ...;
numberStack.popAll(objects);
```
- 비슷한 예제로 `popAll` 을 구현하였지만 이전과 동일한 문제로 `Number` 의 상위 타입인 `Object` 로 공변되지 않아 문제가 발생한다.
- `Collection<? super E> dst` 한정적 와일드카드 타입을 이용하여 개선한다.
  - `popAll` 의 입력 매개변수의 타입이 `E` 의 `Collection` 이 아니라 `E` 의 상위 타입의 `Collection` 이어야 한다.
```java
public void popAll(Collection<? super E> dst) {
    while (!isEmpty())
        dst.add(pop());
}
```
- 매개변수 `dst` 에 `E` 타입의 요소들을 넣는 함수이기 때문에 `E` 의 상위 타입이어야 한다.
- `dst` 가 부모 타입 컬렉션이어야 `E` 타입의 요소를 입력받을 수 있다.

## 유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라.
- 입력 매개변수가 생산자와 소비자 역할을 동시에 한다면 와일드카드 타입을 써도 좋을 게 없다.
- 와일드카드 타입의 공식은 다음과 같다.
> PECS: producer-extends, consumer-super
- 매개변수화 타입 `T` 가 생산자라면 `<? extends T>` 를 사용하고, 소비자라면 `<? super T>` 를 사용하라.

### 반환 타입에는 한정적 와일드카드 타입을 사용하면 안 된다.
```java
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2)
```
- 반환타입은 여전히 `Set<E>` 이다.
- 반환 타입에 한정적 와일드카드 타입을 사용하면 유연성을 높이지 못하고 클라이언트 코드에서도 와일드카드 타입을 써야하는 문제가 발생한다.

### 보다 복잡한 와일드카드 타입 활용
- 이전 장에서 사용한 `max` 함수에 와일드카드 타입을 활용한다.
```java
public static <E extends Comparable<? super E>> E max(List<? extends E> list)
```
- `PECS` 공식이 두번 적용되었다.
- 입력 매개변수에서는 `E` 인스턴스를 생산하기 때문에 `List<E>` 를 `List<? extends E>` 로 수정했다.
- 원래의 선언에서 `E` 가 `Comparable<E>` 를 확장한다고 정의했는데(`<E extends Comparable<E>>`), 이때 `Comparable<E>` 는 `E` 인스턴스를 소비한다.
- 때문에 `Comparable<E>` 를 `Comparable<? super E>` 로 대체한다.
  - `Comparable` 은 언제나 소비자이기 때문에 일반적으로 `Comparable<E>` 보다 `Comparable<? super E>` 를 사용하는 편이 낫다.
  - `Comparator` 또한 `Comparator<E>` 보다 `Comparator<? super E>` 를 사용하는 편이 낫다.

## 메서드를 정의할 때 타입 매개변수와 와일드카드 중 어느 것을 사용할까?
```java
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```
- 둘 중 어느 선언을 이용하는 것이 좋을까?
- `public API` 라면 간단한 두 번째가 낫다.
- **메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체하라**
  - 비한정적 타입 매개변수라면 비한정적 와일드카드로 바꾸고, 한정적 타입 매개변수라면 한정적 와일드카드로 바꾸면 된다.
- 하지만 문제는 `List<?>` 타입에는 `null` 이외에 어떤 값도 넣을 수 없다는 데 있다.
- 이 경우 와일드카드 타입의 실제 타입을 알려주는 `private` 도우미 메서드를 활용한다.
```java
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}

// 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```
- `swapHelper` 메서드는 리스트의 타입 `E` 를 알고 있기 때문에 타입 안전성을 지킬 수 있다.
- 메서드 내부에서 조금 복잡한 제네릭 메서드가 이용되었지만 외부에는 와일드카드 기반의 선언을 유지할 수 있다.
  - 클라이언트는 복잡한 `swapHelper` 의 존재를 모른 채 그 혜택을 누릴 수 있다.

## 정리
- 조금 복잡하더라도 와일드카드 타입을 적용하면 API 가 훨씬 유연해진다.
- 널리 쓰일 라이브러리를 작성한다면 반드시 와일드카드 타입을 적절히 사용해줘야 한다.
- `PECS` 공식을 기억하라
  - 생산자는 `extends` , 소비자는 `super` 를 사용한다.
  - `Comparable` 과 `Comparator` 는 둘 다 소비자이다.
