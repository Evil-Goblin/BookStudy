## 불변 클래스의 직렬화
- 불변인 날짜 범위 클래스를 만드는 데 가변인 `Date` 필드를 이용한 경우 불변식을 지키고 유지하기 위해 `Date` 객체의 방어적 복사를 사용했었다.
- 이는 코드를 길어지게 만드는 주범이었다.

```java
public Period(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
    
    if (this.start.compareTo(this.end) > 0) {
        throw new IllegalArgumentException(this.start + "가 " + this.end + "보다 늦다.");
    }
}

public Date getStart() {
    return new Date(start.getTime());
}

public Date getEnd() {
    return new Date(end.getTime());
}
```
- 이 클래스를 직렬화 하는 경우 `Period` 객체의 물리적 표현이 논리적 표현과 부합하므로 기본 직렬화 형태를 사용해도 나쁘지 않다.
- 때문에 이 클래스 선언에 `implements Serializable` 을 추가하는 것으로 직렬화를 구현할 수 있을 것 같지만 불변식은 보장하지 못하게 된다.
- `readObject` 메서드가 실질적인 `public` 생성자이기 때문이다.
- 때문에 `readObject` 메서드 또한 인수가 유효한지 검사하고, 필요하다면 매개변수를 방어적으로 복사해야 한다.
- 만약 이를 지키지 않는다면 공격자로 하여금 해당 클래스의 불변식을 깨뜨릴 수 있는 여지를 만들어주게 된다.

### readObject 는 매개변수로 바이트 스트림을 받는 생성자이다.
- 불변식을 깨뜨릴 의도로 임의 생성한 바이트 스트림을 입력받게 되면 일반 생성자로는 생성해낼 수 없는 객체를 생성해낼 수 있다.
- 단적으로 `Period` 클래스 선언에 `implements Serializable` 만 추가하게 되면 시작 시각보다 종료 시각이 앞서는 `Period` 객체를 만들 수도 있다.
  - 이를 위해 `Period` 의 `readObject` 메서드가 `defaultReadObject` 를 호출한 다음 역직렬화된 객체가 유효한지 검사해야 한다.
- 또한 바이트 스트림 끝에 `private` 로 선언된 `Date` 필드로의 참조를 추가하여 `Period` 인스턴스를 가변으로 만들어 버릴 수도 있다.
  - 공격자는 `ObjectInputStream` 에서 `Period` 인스턴스를 읽은 후 스트림 끝에 추가된 '악의적인 객체 참조' 를 읽어 `Period` 객체의 내부 정보를 얻을 수 있다.
  - 이를 통해 `Period` 인스턴스가 불변이 아니게 된다.
- 이 문제의 근원은 `Period` 의 `readObject` 메서드가 방어적 복사를 충분히 하지 않은 데 있다.
  - **객체를 역직렬화할 때는 클라이언트가 소유해서는 안 되는 객체 참조를 갖는 필드를 모두 반드시 방어적으로 복사해야 한다.**
  - `readObject` 에서는 불변 클래스 안의 모든 `private` 가변 요소를 방어적으로 복사해야 한다.
- 생성자와 마찬가지로 `readObject` 메서드도 재정의 가능 메서드를 호출해서는 안 된다.

## 기본 readObject 메서드를 사용해도 좋은 경우 판별법
- `transient` 필드를 제외한 모든 필드의 값을 매개변수로 받아 유효성 검사 없이 필드에 대입하는 `public` 생성자를 추가해도 괜찮다면 기본 메서드를 사용해도 된다.
  - 아니라면 커스텀 `readObject` 메서드를 만들어 유효성 검사와 방어적 복사를 수행해야 한다.
  - 또는 프록시 패턴을 사용하는 방법도 있다.

## 정리
- `readObject` 메서드를 작성할 때는 언제나 `public` 생성자를 작성하는 자세로 임해야 한다.
- `readObject` 는 어떤 바이트 스트림이 넘어오더라도 유효한 인스턴스를 만들어내야 한다.
- 바이트 스트림이 진짜 직렬화된 인스턴스라고 가정해서는 안 된다.
  - 커스텀 직렬화를 사용하더라도 문제가 발생할 수 있다.

### 안전한 readObject 메서드를 작성하는 지침
- `private` 여야 하는 객체 참조 필드는 각 필드가 가리키는 객체를 방어적으로 복사하라.
  - 불변 클래스 내의 가변 요소가 여기 속한다.
- 모든 불변식을 검사하여 어긋나는게 발견되면 `InvalidObjectException` 을 던져다.
  - 방어적 복사 다음에는 반드시 불변식 검사가 뒤따라야 한다.
- 역직렬화 후 객체 그래프 전체의 유효성을 검사해야 한다면 `ObjectInputValidation` 인터페이스를 사용하라.
- 직접적이든 간접적이든, 재정의할 수 있는 메서드는 호출하지 말자.

