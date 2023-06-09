## 인스턴스화를 막는 경우
- 일반적으로 유틸적인 기능을 하는 클래스에 해당한다.
- `static` 변수, 메소드 등을 가지도록 하여 사용하는 경우
 
## 인스턴스화를 막는 방법
- `추상 클래스`로 만든다.
- 생성자를 `private`로 만든다.

## `private` 생성자를 만들어야 하는 이유
- 생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 만들어준다.
- 이는 매개변수를 받지 않는 `public` 생성자이기 때문에 사용자 입장에서 의도한 것인지를 구분할 수 없다.
- `private` 생성자를 명시하여 인스턴스화를 막으려는 의도를 분명히 하는게 좋은 것 같다.

## 추상 클래스로 만드는 것으로는 인스턴스화를 막을 수 없다.
- 추상 클래스는 본디 상속하여 사용하기 때문에 상속하여 인스턴스화 해버리면 막을 수 없다.
- 사용자 입장에서 상속을 이용하라는 오해를 부를 수 있다.

## 추상 클래스의 `static`메소드
- 결국 유틸기능을 위해 설계하는 경우가 대부분이기 때문에 `static` 메소드를 사용하는 경우가 많다.
- 이런 경우 해당 클래스의 인스턴스를 만들어도 인스턴스가 아무 기능을 할 수 없기 때문에 사실상 인스턴스화를 막는다고 볼 수 있다.
- ex) `Spring StringUtils.class`
    ```java
    public abstract class StringUtils {
    
        ...
        ...
    
        public static boolean hasLength(@Nullable CharSequence str) {
            return (str != null && str.length() > 0);
        }
	    
        ...
        ...
	
    }
    ```
- `Spring` 프레임워크의 `StringUtils` 클래스의 일부분이다.
- 이와 같이 해당 클래스의 모든 메소드, 변수는 `static` 으로 이루어져있다.
- 그렇기때문에 `StringUtils` 클래스가 인스턴스화 된다고 하더라도 어떠한 기능도 갖지 않는다.
- 인스턴스화 할 수 있다는 사실이 마음에 안들기는 하지만....