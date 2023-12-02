## 중첩 클래스 (nested class)
- 다른 클래스 안에 정의된 클래스를 말한다.
- 중첩 클래스는 자신을 감싼 바깥 클래스에서만 쓰여야 한다.
  - 그 외의 쓰임새가 있다면 톱레벨 클래스로 만들어야 한다.

### 중첩 클래스의 종류
- 정적 멤버 클래스
- 멤버 클래스
- 익명 클래스
- 지역 클래스

이 중 정적 멤버 클래스를 제외한 나머지는 내부 클래스(`inner class`)에 해당한다.
```java
public class TopLevelClass {
  static class StaticNestedClass {
    // 정적 멤버 클래스
  }

  class InnerClass {
    // 멤버 클래스
  }

  void localClass() {
    class LocalClass {
      // 지역 클래스
    }

    InnerClass AnonymousClass = new InnerClass() {
      // 익명 클래스
    };
  }
}
```

#### 정적 멤버 클래스
- 다른 클래스 안에 선언된다.
- 바깥 클래스의 `private` 멤버에도 접근할 수 있다.
- 이 외에는 일반 클래스와 같다.
- `private` 로 선언시 바깥 클래스에서만 접근할 수 있다.
- 바깥 클래스와 함께 쓰일 때만 유용한 `public` 도우미 클래스로 사용된다.

#### 비정적 멤버 클래스
- 비정적 멤버 클래스의 인스턴스는 바깥 클래스의 인스턴스와 암묵적으로 연결된다.
- 비 정적 멤버 클래스의 인스턴스 메서드에서 정규화된 `this` 를 사용해 바깥 인스턴스의 메서드를 호출하거나 바깥 인스턴스의 참조를 가져올 수 있다.
  - 정규화된 `this` 란 `클래스명.this` 형태로 바깥 클래스의 이름을 명시하는 용법을 말한다.
```java
public class InnerClassSample {
  class InnerClass {
    public void sayHello() {
      InnerClassSample.this.sayHello();
    }
  }

  private void sayHello() {
    System.out.println("hello world");
  }

  public InnerClass innerClass = new InnerClass();
}

class Client {
  public static void main(String[] args) {
    InnerClassSample innerClassSample = new InnerClassSample();
    innerClassSample.innerClass.sayHello();
  }
}
```
- 따라서 개념상 중첩 클래스의 인스턴스가 바깥 인스턴스와 독립적으로 존재할 수 있다면 정적 멤버 클래스로 만들어야 한다.
  - 비정적 멤버 클래스는 바깥 인스턴스 없이는 생성할 수 없기 때문이다.
- 비정적 멤버 클래스의 인스턴스와 바깥 인스턴스 사이의 관계는 멤버 클래스가 인스턴스화될 때 확립되며, 더 이상 변경할 수 없다.
- 일반적으로 바깥 클래스의 인스턴스 메서드에서 비정적 멤버 클래스의 생성자를 호출할 때 만들어지지만, 드물게 직접 `바깥 인스턴스의 클래스.new MemberClass(args)` 를 통해 수동으로 만들기도 한다.
```java
public class Client {
  public static void main(String[] args) {
    InnerClassSample innerClassSample = new InnerClassSample();
    InnerClassSample.InnerClass innerClass = innerClassSample.new InnerClass();
  }
}
```
- 이 관계 정보는 비정적 멤버 클래스의 인스턴스 안에 만들어져 메모리 공간을 차지하며, 생성 시간도 더 걸린다.
- 비정적 멤버 클래스는 어댑터를 정의할 때 자주 쓰인다.
  - 어떤 클래스의 인스턴스를 감싸 마치 다른 클래스의 인스턴스처럼 보이게 하는 뷰로 사용된다.
- 예를 들어 `Map` 인터페이스의 구현체들은 자신의 컬렉션 뷰를 구현할 때 비정적 멤버 클래스를 사용한다.
  - ex) `HashMap` 의 `EntrySet`
- `Set` , `List` 같은 인터페이스의 구현체들도 자신의 반복자를 구현할 때 비정적 멤버 클래스를 사용한다.
  - ex) `ArrayList` 의 `Itr`

##### 멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 `static` 을 붙여서 정적 멤버 클래스로 만들자.
- `static` 을 생략하면 바깥 인스턴스로의 숨은 외부 참조를 갖게 된다.
  - 이 참조를 저장하려면 시간과 공간이 소비된다.
- **가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못하는 메모리 누수가 생길 수 있다.**
- `priavte` 정적 멤버 클래스는 바깥 클래스가 표현하는 객체의 한 부분을 나타낼 때 쓴다.
  - `Map` 인스턴스의 경우 키-값 쌍을 표현하는 `Entry` 객체를 가지고 있고 모든 엔트리는 맵과 연관되어 있지만 엔트리의 메서드들(`getKey`, `getValue`)은 맵을 직접 사용하지 않는다.
  - 엔트리를 비정적 멤버 클래스로 표현하는 것은 낭비이다.

#### 익명 클래스
- 익명클래스는 바깥 클래스의 멤버가 아니다.
- 멤버와 달리, 쓰이는 시점에 선언과 동시에 인스턴스가 만들어진다.
- 오직 비정적인 문맥에서 사용될 때만 바깥 클래스의 인스턴스를 참조할 수 있다.
- 정적 문맥에서라도 상수 변수 이외의 정적 멤버는 가질 수 없다.
  - 상수 표현을 위해 초기화된 `final` 기본 타입과 문자열 필드만 가질 수 있다.
- 정적 팩터리 메서드를 구현할 때 사용된다.

#### 지역 클래스
- 네 가지 중첩 클래스 중 가장 드물게 사용된다.
- 지역 클래스는 지역 변수를 선언할 수 있는 곳이면 어디서든 선언할 수 있고, 유효 범위도 지역변수와 같다.
- 멤버 클래스처럼 이름이 있고 반복해서 사용할 수 있다.
- 익명 클래스처럼 비정적 문맥에서 사용될 때만 바깥 인스턴스를 참조할 수 있으며, 정적 멤버는 가질수 없고, 가독성을 위해 짧게 작성해야 한다.

## 정리
- 중첩 클래스에는 네 가지가 있으며, 각각의 쓰임이 다르다.
- 메서드 밖에서도 사용해야 하거나 메서드 안에 정의하기엔 너무 길다면 멤버 클래스로 만든다.
- 멤버 클래스의 인스턴스 각각이 바깥 인스턴스를 참조한다면 비정적으로, 그러히 않다면 정적으로 만들자
- 중첩 클래스가 한 메서드 안에서만 쓰이면 그 인스턴스를 생성하는 지점은 단 한 곳이고 해당 타입으로 쓰기에 적합한 클래스나 인터페이스가 이미 있다면 익명클래스로 만들고, 그렇지 않으면 지역 클래스로 만들자.