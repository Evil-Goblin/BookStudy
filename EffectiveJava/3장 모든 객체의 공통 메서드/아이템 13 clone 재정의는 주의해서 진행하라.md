## clone 메서드는 인터페이스 Cloneable 에 선언되어 있지 않다.
- `Cloneable` 은 복제해도 되는 클래스임을 명시하는 용도이지만 의도한대로 사용하기 어렵다.
  ![Cloneable](https://github.com/Evil-Goblin/Evil-Goblin.github.io/assets/74400861/9ef0400d-72bf-4255-9e00-c25018e09b70)
- `clone` 메서드는 `Cloneable` 인터페이스에 선언되어있지 않고 `Object` 에 `protected` 로 선언되었있다.
- 때문에 `Cloneable` 을 구현하더라도 외부에서 `clone` 메서드를 호출할 수 없다.
- 위의 `Cloneable` 의 `API` 문서에도 나와있듯이 `Cloneable` 을 구현하지 않은 클래스에서 `clone` 을 호출시 `CloneNotSupportedException` 이 발생한다.

## clone 메서드의 일반 규약
![Object.clone](https://github.com/Evil-Goblin/Evil-Goblin.github.io/assets/74400861/c14d02fc-b1f6-4742-81f9-bda9ff94aaf2)
> 이 객체의 복사본을 생성해 반환한다. '복사'의 정확한 뜻은 그 객체를 구현한 클래스에 따라 다를 수 있다. 일반적인 의도는 다음과 같다. 어떤 객체 x에 대해 다음 식은 참이다.
> 
> x.clone() != x
> 
> 또한 다음 식도 참이다.
> 
> x.clone().getClass() == x.getClass()
> 
> 하지만 이상의 요구를 반드시 만족해야 하는 것은 아니다.
> 한편 다음 식도 일반적으로 참이지만, 역시 필수는 아니다.
> 
> x.clone().equals(x)
> 
> 관례상, 이 메서드가 반환하는 객체는 super.clone을 호출해 얻어야 한다. 이 클래스와(Object를 제외한) 모든 상위 클래스가 이 관례를 따른다면 다음 식은 참이다.
> 
> x.clone().getClass() == x.getClass()
> 
> 관례상, 반환된 객체와 원본 객체는 독립적이어야 한다. 이를 만족하려면 super.clone으로 얻은 객체의 필드 중 하나 이상을 반환 전에 수정해야 할 수도 있다.
- 생성자 연쇄(`constructor chaining`)와 흡사하다.
- `clone` 메서드가 `super.clone` 이 아닌, 생성자를 호출해 얻은 인스턴스를 반환해도 컴파일러는 문제로 보지 않는다.
- 하지만 이 클래스의 하위 클래스에서 `super.clone` 을 호출한다면 잘못된 클래스의 객체가 만들어지게 되어 하위 클래스의 `clone` 메서드가 제대로 동작하지 않게 된다.
- 만약 `clone` 을 재정의한 클래스가 `final` 이라면 걱정해야 할 하위 클래스가 없으니 이 관례는 무시해도 안전하다.
- 하지만 `final` 클래스의 `clone` 메서드가 `super.clone` 을 호출하지 않는다면 `Cloneable` 을 구현할 이유도 없다.

## clone 구현
```java
public class Member implements Cloneable {
    private final String name;
    private final int age;

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public Member clone() {
        try {
            return (Member) super.clone(); // 공변 반환 타이핑
            // 상위 클래스의 메서드가 반환하는 타입이 하위 타입일 수 있다.
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // Never Raise
        }
    }
}
```
- 불변 객체에 대해서 `clone` 을 구현한 내용이다.
- `super.clone` 을 통해 완벽한 복제본이 만들어 진다.
- `Cloneable` 을 구현하기 때문에 `CloneNotSupportedException` 이 절대 발생하지 않는다.(`Checked Exception` 이었으면 안됐다.)

```java
public class MutableObject implements Cloneable {
    private final List<String> list;

    public MutableObject(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public MutableObject clone() {
        try {
            return (MutableObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
```
- 가변 객체에 `clone` 을 구현했다.

```java
public class Client {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("a", "b", "c");
        MutableObject original = new MutableObject(list);
        MutableObject clone = original.clone();

        System.out.println("clone != original = " + (clone != original));
        System.out.println("clone.getClass() == original.getClass() = " + (clone.getClass() == original.getClass()));
        System.out.println("clone.getList() == original.getList() = " + (clone.getList() == original.getList()));
        // clone != original = true
        // clone.getClass() == original.getClass() = true
        // clone.getList() == original.getList() = true
    }
}
```
- 하지만 `clone` 은 얕은 복사를 지원하기 때문에 `MutableObject` 의 멤버 `List` 의 주소값만 복사되어 `clone` 과 `original` 이 같은 `List` 를 가리키게 된다.

## clone 메서드는 원본 객체에 아무런 해를 끼치지 않는 복제된 개체의 불변식을 보장해야한다.
- 레퍼런스 타입의 멤버들을 따로 `clone` 을 호출해준다.
```java
@Override
public MutableObject clone() {
  try {
    MutableObject clone = (MutableObject) super.clone();
    clone.list = new ArrayList<>(list);
    return clone;
  } catch (CloneNotSupportedException e) {
    throw new RuntimeException(e);
  }
}
```
- 예제를 `List` 로 하는 바람에 `clone` 을 할 수 없었다.(최대한 비슷하게 구현했다.)
- 또한 `final` 을 제거하고 진행하였다.
- 당연하게도 `final` 으로 선언하면 위의 방법을 사용할 수 없다.
- 결국 레퍼런스 타입에 대해 `deep copy` 를 수행하도록 하면 된다.

## clone 메서드를 구현할 때 재정의될 수 있는 메서드는 호출해서는 안된다.
- 만약 `clone` 이 하위 클래스에서 재정의한 메서드를 호출하면, 하위 클래스는 복제 과정에서 자신의 상태를 교정할 기회를 잃게 되어 원본과 복제본의 상태가 달라질 가능성이 크다.

## CloneNotSupportedException 을 던지지 말도록 한다.
- `Checked Exception` 을 명시하는 경우 사용하는 측에서 불편하기 때문에 이와 같이 절대 발생하지 않을 에러는 외부로 확산되지 않도록 한다.

## 상속용 클래스는 Cloneable` 을 구현해서는 안된다.
- 하위 클래스에서 `Cloneable` 구현 여부를 선택할 수 있도록 하거나 아예 막아버릴 수 있다.

## 요약
- `Cloneable` 을 구현하는 모든 클래스는 `clone` 을 재정의해야 한다.
- 접근 제한자는 `public` 으로, 반환 타입은 클래스 자신으로 변경한다.
- 먼저 `super.clone` 을 호출한 후 필요한 필드를 적절히 수정한다.(`deep copy`)
- 고유 ID 와 같은 값은 기본 타입이거나 불변일지라도 수정해야 한다.

## Cloneable 을 구현하기 보다 복사 생성자와 복사 팩터리를 사용하는 편이 좋다.
```java
public class ImproveClone {
  public ImproveClone(ImproveClone clone) {
    // clone logic
  }

  public static ImproveClone newInstance(ImproveClone clone) {
    // clone logic
  }
}
```
- 위와 같이 복사를 위한 복사 생성자, 복사를 위한 복사 팩터리를 제공하는 것이 `Cloneable/clone` 방식보다 나은 면이 많다.

## 정리
- 새로운 인터페이스를 만들 때는 절대 `Cloneable` 을 확장해서는 안 되며, 새로운 클래스도 이를 구현해서는 안 된다.
- `final` 클래스라면 `Cloneable` 을 구현해도 위험이 크지 않지만, 성능 최적화 관점에서 검토한 후 별다른 문제가 없을 때만 드물게 허용한다.
- 복제는 생성자와 팩터리를 이용하는 것이 낫다.
- 배열은 `clone` 메서드가 가장 깔끔하기 때문에 예외이다.