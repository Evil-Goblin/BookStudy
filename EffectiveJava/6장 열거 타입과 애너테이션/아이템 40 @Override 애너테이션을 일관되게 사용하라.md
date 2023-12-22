## @Override 어노테이션을 이용해 재정의를 명확히 하라.
```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Bigram bigram) {
        return bigram.first == first && bigram.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        HashSet<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size());
    }
}
```
- `equals` 와 `hashCode` 를 재정의하여 중복을 불허하는 `Set` 에 넣었으니 결과는 26으로 예상되지만 실제로는 260이 출력된다.
- `equals` 와 `hashCode` 를 재정의한 것처럼 보이지만 실제로는 `equals` 를 재정의 한 것이 아닌 `Overloading` 한 것이다.
  - 재정의되지 않은 `Object.equals` 는 레퍼런스의 동일 유무만을 체크하기 때문에 위 예제에서의 모든 인스턴스는 각각 별개로 인식되게 된다.
  ![equals_not_used](https://github.com/Evil-Goblin/BookStudy/assets/74400861/72c9ada7-5e20-45a9-820b-afaf137b6fa7)
  - IDE 는 답을 알고 있다. 중복정의한 `equals` 가 사용되지 않은 사실을
- 메서드 재정의 의도를 정확히 명시하기 위해 `@Override` 어노테이션을 사용하여야 한다.
```java
@Override
^^^^^^^^^ 메서드는 상위 클래스의 메서드를 재정의하지 않습니다.
public boolean equals(Bigram bigram) {
    return bigram.first == first && bigram.second == second;
}
```
- 위와 같이 `@Override` 어노테이션을 달아주면 에러가 표시된다.
  - 잘못된 부분을 명확히 알려주기 때문에 올바르게 수정이 가능하다.
```java
@Override
public boolean equals(Object o) {
    if (!(o instanceof Bigram bigram)) {
        return false;
    }
    return bigram.first == first && bigram.second == second;
}
```
- 에러를 근거로 올바르게 수정한다.

## 상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 어노테이션을 달자.
- 유일한 예외는 구체클래스에서 상위 클래스의 추상 메서드를 재정의하는 경우이다.
  - 구체 클래스임에도 구현하지 않은 추상 메서드가 남아 있다면 컴파일 오류가 발생하기 때문에 이 경우는 굳이 달지 않아도 된다.
  - 물론 일괄적으로 전부 붙여두어도 괜찮다.
  - 또한 IDE 는 자동으로 붙여준다.
- `@Override` 어노테이션을 통해 메서드의 시그니처가 올바른지 재차 확신할 수 있다.

## 정리
- 재정의한 모든 메서드에 `@Override` 어노테이션을 사용하면 실수했을 때 컴파일러가 알려준다.
- 구체 클래스에서 상위 클래스의 추상 메서드를 재정의하는 경우를 제외하곤 `@Override` 어노테이션을 사용하도록 하자.
  - 물론 이 경우에도 사용해도 된다.
