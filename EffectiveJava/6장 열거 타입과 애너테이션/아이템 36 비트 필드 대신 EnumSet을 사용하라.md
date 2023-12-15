## 비트 필드 열거 상수
```java
public class Text {
    public static final int STYLE_BOLD          = 1 << 0;
    public static final int STYLE_ITALIC        = 1 << 1;
    public static final int STYLE_UNDERLINE     = 1 << 2;
    public static final int STYLE_STRIKETHROUGH = 1 << 3;

    public void applyStyle(int styles) {...}
}
```
- `text.applyStyles(STYLE_BOLD | STYLE_ITALIC);` 과 같이 비트별 `OR` 를 이용해 여러 상수를 하나의 집합으로 사용한다.
- 이러한 집합을 비트 필드라고 한다.
- 비트 필드를 사용하면 비트별 연산을 사용해 합집합과 교집합 같은 집합 연산을 효율적으로 수행할 수 있다.

### 단점
- 비트 필드는 정수 열거 상수의 단점을 그대로 지닌다.
- 비트 필드 값이 그대로 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기 어렵다.
- 비트 필드 하나에 녹아있는 모든 원소를 순회하기도 까다롭다.
  - 위의 예제의 필드들을 순회하려면 한땀한땀 작성해야 한다.
- 최대 몇 비트가 필요한지 API 작성시 미리 예측하여 적절한 타입을 선택해야 한다.(`int` or `long`)
  - API 를 수정하지 않고는 비트 수를 늘릴 수 없기 때문

## 비트 필드 열거 상수의 대안
- `EnumSet` 크래스는 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현해준다.
- `Set` 인터페이스를 구현하여, 타입 안전성이 지켜지고, 다른 `Set` 구현체와 함께 사용할 수 있다.

### EnumSet
- `EnumSet` 의 내부는 비트 벡터로 구현되어있다.
  - 원소가 총 64개 이하라면, 대부분의 경우 `EnumSet` 전체를 `long` 변수 하나로 표현하여 비트 필드에 비견되는 성능을 보여준다.
- `removeAll` , `retainAll` 같은 대량 작업은 비트를 효율적으로 처리할 수 있는 산술 연산을 써서 구현했다.
- 비트를 직접 다룰 때 겪는 오류를 `EnumSet` 이 처리해준다.

## 개선
```java
public class Text {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
    
    public void applyStyles(Set<Style> styles) {/*...*/}
}
```
- `text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));` 과 같이 이용할 수 있다.

## 정리
- 열거할 수 있는 타입을 한데 모아 집합 형태로 사용한다고 해도 비트 필드를 사용할 이유는 없다.
- `EnumSet` 클래스가 비트 필드 수준의 명료함과 성능을 제공하고 열거 타입의 장점까지 선사한다.
- `EnumSet` 의 유일한 단점은 불변 `EnumSet` 을 만들 수 없다는 것이다.
  - `Collections.unmodifiableSet` 으로 `EnumSet` 을 감싸 사용할 수는 있다.(명확성과 성능을 희생하게 된다.)
