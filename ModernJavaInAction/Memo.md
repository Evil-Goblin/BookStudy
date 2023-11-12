## Stream
- 스트림이란 한 번에 한 개씩 만들어지는 연속적인 데이터 항목들의 모임이다.
- 입력 스트림에서 데이터를 한 개씩 읽어들인다.
- 출력 스트림으로 데이터를 한 개씩 기록한다.

## Predicate
- 인수로 값을 받아 `True` 또는 `False` 를 반환하는 함수
- `Predicate.test(T t)` 문법으로 수행된다.
- 매개변수로 값을 받아 `boolean` 을 리턴하는 함수
- 매개변수의 타입을 제네릭 `T` 로 설정한다.
- `test` 메소드를 통해 함수형 인터페이스를 수행한다.
```java
public static boolean isGreenApple(Apple apple) {
    return GREEN.equals(apple.getColor());
}
```
- `Klass::isGreenApple` 문법을 이용하여 `Predicate<Apple>` 타입으로 사용할 수 있다.
- `Predicate<Apple> p; p.test(apple);` 이를 통해 `isGreenApple` 메소드가 수행된다.

## 메서드 참조
- `apple.getWeight()` -> `Apple::getWeight`
- 람다의 축양형이라고 할 수 있다.

### 메서드 참조를 만드는 방법
1. 정적 메서드 참조
   - Integer::parseInt
2. 다양한 형식의 인스턴스 메서드 참조
   - String::length
   - `(String s) -> s.toUpperCase()` -> `String::toUpperCase`
3. 기존 객체의 인스턴스 메서드 참조
   - expensiveTransaction::getValue
   - `() -> expensiveTransaction.getValue()` -> `expensiveTransaction::getValue`
   - `this` 에도 사용 가능하다.
     - `this::isValidName`
