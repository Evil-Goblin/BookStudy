## ordinal
- 열거 타입 상수는 하나의 정숫값에 대응된다.
- 열거 타입은 해당 상수가 몇 번째 위치인지를 반환하는 `ordinal` 메서드를 제공한다.
- 하지만 최대한 `ordinal` 메서드의 사용을 지양하자.

### ordinal 사용을 지양하자.
```java
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, 
    SEPTET, OCTET, NONET, DECTET;

    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```
- 이와 같이 `ordinal` 에 의존하는 코드는 상수 선언 순서가 바뀌는 순간 오작동할 확률이 높다.
  - 이미 사용중인 값과 달라지기 때문
- 같은 값을 가지는 상수를 추가할 수 없다.
- 중간에 값을 비울 수 없다.
    - 위 예제의 경우 연주자의 수에 관련되어있지만 12명이 연주하는 값을 추가하려고한다면 11의 값을 의미하는 상수도 필요해진다.
- 쓰이지 않는 값이 많아질수록 실용성이 떨어진다.

## 개선
- 열거 타입 상수에 연결된 값을 `ordinal` 메서드를 사용하지 말고 인스턴스 필드에 저장하도록 한다.
```java
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5), 
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8), 
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);
    
    private final int numberOfMusicians;

    Ensemble(int numberOfMusicians) {
        this.numberOfMusicians = numberOfMusicians;
    }

    public int numberOfMusicians() {
        return numberOfMusicians;
    }
}
```
- `Enum` 의 API 문서에도 `ordinal` 의 사용을 지양하는 내용이 적혀있다.
  ![Enum.ordinal](https://github.com/Evil-Goblin/BookStudy/assets/74400861/ea7ca9af-2bd9-4e84-8b70-447c7dde60b6)
  - `ordinal` 메서드는 `EnumSet` 과 `EnumMap` 같은 열거 타입 기반 범용 자료에 쓸 목적으로 설계되었다.
  - 이러한 용도가 아니라면 `ordinal` 메소드의 사용을 지양하자.
