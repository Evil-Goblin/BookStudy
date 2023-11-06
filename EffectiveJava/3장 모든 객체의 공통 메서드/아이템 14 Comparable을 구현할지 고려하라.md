## 순서가 명확한 값 클래스를 작성시 Comparable 인터페이스를 구현하자
- 알파벳, 숫자, 연대 등

## Comparable
```java
public interface Comparable<T> {
  public int compareTo(T o);
}
```
![compareTo](https://github.com/Evil-Goblin/BookStudy/assets/74400861/23ac8a88-1183-4baf-94f6-63ac3e11020f)
> 이 객체와 주어진 객체의 순서를 비교한다.
> 이 객체가 주어진 객체보다 작으면 음의 정수, 같으면 0, 크면 양의 정수를 반환한다.
> 만약 이 객체와 비교할 수 없는 타입의 객체가 주어지면 ClassCastException 이 발생한다.
> 
> 다음 설명에서 sgn(표현식) 표기는 수학에서 말하는 부호 함수(signum function)를 뜻하며, 표현식의 값이 음수, 0, 양수일 때 -1, 0, 1을 반환하도록 정의했다.
> 
> - Comparable 을 구현한 클래스는 모든 x, y 에 대해 sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) 여야 한다.(따라서 x.compareTo(y)는 y.compareTo(x)가 예외를 던질 때에 한해 예외를 던져야 한다.)
> - Comparable 을 구현한 클래스는 추이성을 보장해야 한다. 즉, (x.compareTo(y) > 0 && y.compareTo(z) > 0) 이면 x.compareTo(z) > 0 이다.
> - Comparable 을 구현한 클래스는 모든 z에 대해 x.compareTo(y) == 0 이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z)) 이다.
> - 이번 권고가 필수는 아니지만 꼭 지키는 ㅔ 좋다. (x.compareTo(y) == 0) == (x.equals(y)) 여야 한다. Comparable 을 구현하고 이 권고를 지키지 않는 모든 클래스는 그 사실을 명시해야 한다. 다음과 같이 명시하면 적당할 것이다.
>   - *"주의: 이 클래스의 순서는 equals 메서드와 일관되지 않는다."*
- `compareTo` 메서드로 수행하는 동치성 검사도 `equals` 규약과 똑같이 반사성, 대칭성, 추이성을 충족해야 함을 뜻한다.

### 주의사항
- 기존 클래스를 확장한 구체 클래스에서 새로운 값 컴포넌트를 추가했다면 `compareTo` 규약을 지킬 방법이 없다.
  - `Comparable` 을 구현한 클래스를 확장해 값 컴포넌트를 추가하고 싶다면, 확장하는 대신 독립된 클래스를 만들고, 이 클래스에 원래 클래스의 인스턴스를 가리키는 필드를 둔다.
  - 이후 내부 인스턴스를 반환하는 '뷰' 메서드를 제공한다.
  - 이 방식을 통해 바깥 클래스에 우리가 원하는 `compareTo` 메서드를 구현해 넣을 수 있다.

### compareTo 메서드로 수행한 동치성 테스트의 결과가 equals 와 같아야 한다.
- `compareTo` 의 순서와 `equals` 의 결과가 일관되지 않은 클래스도 여전히 동작은 한다.
- 하지만 이 규약이 지켜지지 않은 클래스의 객체를 정렬된 컬렉션에 넣으면 해당 컬렉션이 구현한 인터페이스에 정의된 동작과 엇박자를 낼 것이다.
- 일반적인 컬렉션은 `equals` 메서드 규약을 따르지만 정렬된 컬렉션들은 동치성을 비교할 때 `equals` 대신 `compareTo` 를 사용하기 때문이다.

```java
public class CompareBigDecimal {
  public static void main(String[] args) {
    BigDecimal bigDecimal01 = new BigDecimal("1.0");
    BigDecimal bigDecimal02 = new BigDecimal("1.00");

    HashSet<BigDecimal> bigDecimalHashSet = new HashSet<>();
    bigDecimalHashSet.add(bigDecimal01);
    bigDecimalHashSet.add(bigDecimal02);
    System.out.println("bigDecimalHashSet = " + bigDecimalHashSet); // bigDecimalHashSet = [1.0, 1.00]

    TreeSet<BigDecimal> bigDecimalTreeSet = new TreeSet<>();
    bigDecimalTreeSet.add(bigDecimal01);
    bigDecimalTreeSet.add(bigDecimal02);
    System.out.println("bigDecimalTreeSet = " + bigDecimalTreeSet); // bigDecimalTreeSet = [1.0]
  }
}
```
- `HashSet` 의 경우 `equals` 규약을 따르기 때문에 두 `BigDecimal` 의 값이 다른 값으로 취급된다.
- 하지만 `TreeSet` 의 경우 정렬된 컬렉션이기 때문에 `compareTo` 로 비교되고 결과 두 `BigDecimal` 은 같은 값 취급을 받는다.

![TreeSet](https://github.com/Evil-Goblin/BookStudy/assets/74400861/48156f02-659e-4586-b37b-1f02541b80ad)
- `TreeSet` 의 API 문서 일부분이다.
- `Set` 인터페이스는 `equals` 연산으로 정의되어 있지만 `TreeSet` 인스턴스는 `compareTo` 메서드를 사용하여 모든 요소 비교를 수행한다.

## 박싱된 기본 타입 클래스들에 새로 추가된 정적 메서드 compare 를 이용하라.
- `compareTo` 메서드에서 관계연사자 `<`, `>` 를 사용하는 방식은 거추장스럽고 오류를 유발하기 때문에 `compare` 를 이용하는 것이 좋다.
  ![Integer.compare](https://github.com/Evil-Goblin/BookStudy/assets/74400861/1523a642-4f0b-4da1-8b37-d4d051f42698)
  _Integer 클래스의 compare 메소드_

## 핵심 필드가 여러 개라면 가장 핵심적인 필드부터 비교해라.
```java
public class PriorityData implements Comparable {
  private int high, normal, low;

  @Override
  public int compareTo(Object o) {
    PriorityData pd = (PriorityData) o;
    int result = Integer.compare(high, pd.high);
    if (result == 0) {
      result = Integer.compare(normal, pd.normal);
      if (result == 0) {
        result = Integer.compare(low, pd.low);
      }
    }
    return result;
  }
}
```
- 우선 순위가 높은 순 부터 비교 결과가 0이 아니라면 바로 결과를 반환하면 된다.

```java
public class PriorityData implements Comparable {
  private int high, normal, low;

  private static final Comparator<PriorityData> COMPARATOR =
    Comparator.comparingInt((PriorityData pd) -> pd.high)
     .thenComparingInt(pd -> pd.normal)
     .thenComparingInt(pd -> pd.low);

  @Override
  public int compareTo(Object o) {
    PriorityData pd = (PriorityData) o;
    return COMPARATOR.compare(this, pd);
  }
}
```
- 자바 8에서는 `Comparator` 인터페이스가 일련의 비교자 생성 메서드와 팀을 꾸려 메서드 연쇄 방식으로 비교자를 생성할 수 있게 되었다.
- 간결함에서는 좋지만 성능 저하가 뒤따른다.

## 값의 차이를 기준으로 정렬하는 경우 주의
```java
static Comparator<Object> hashCodeOrder = new Comparator<Object>() {
  @Override
  public int compare(Object o1, Object o2) {
    return o1.hashCode() - o2.hashCode();
  }
};
```
- 위와 같이 사용해서는 안된다.
- 이 방식은 정수 오버플로우를 일으키거나 [부동소수점 계산 방식](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.20.1)에 따른 오류를 낼 수 있다.

```java
static Comparator<Object> hashCodeOrder = new Comparator<Object>() {
  @Override
  public int compare(Object o1, Object o2) {
    return Integer.compare(o1.hashCode(), o2.hashCode());
  }
};
```
```java
static Comparator<Object> hashCodeOrder = Comparator.comparingInt(o -> o.hashCode());
```
- 대신 위의 두가지 방법 증 하나를 사용하는 것이 좋다.

## 정리
- 순서를 고려해야 하는 값 클래스를 작성한다면 꼭 `Comparable` 인터페이스를 구현하여, 그 인스턴스들을 쉽게 정렬하고, 검색하고, 비교 기능을 제공하는 컬렉션과 어우러지도록 해야 한다.
- `compareTo` 메서드에서 필드의 값을 비교할 때 `<` , `>` 연산자는 쓰지 말아야 한다.
- 박싱된 기본 타입 클래스가 제공하는 정적 `compare` 메서드나 `Comparator` 인터페이스가 제공하는 비교자 생성 메서드를 사용하자.