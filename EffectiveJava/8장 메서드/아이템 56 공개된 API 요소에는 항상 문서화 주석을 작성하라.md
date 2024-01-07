## 문서화
- API 를 올바로 문서화하려면 고개된 모든 클래스, 인터페이스, 메서드, 필드 선언에 문서화 주석을 달아야 한다.
  - 직렬화할 수 있는 클래스의 경우 직렬화 형태에 관해서도 적어야 한다.
  - 문서가 잘 갖춰지지 않은 API 는 사용하기 어렵다.
  - 기본 생성자에는 주석을 달 방법이 없기 때문에 공개 클래스는 절대 기본 생성자를 사용하면 안된다.
  - 유지보수를 위해 공개되지 않은 클래스, 인터페이스, 생성자, 메서드, 필드에도 주석을 달 필요가 있다.
    - 공개된 API 만큼 친절하진 않더라도
- 메서드용 문서화 주석에는 해당 메서드와 클라이언트 사이의 규약을 명료하게 기술해야 한다.
  - 메서드가 어떻게 동작하는지가 아닌 무엇을 하는지를 기술한다.
  - 클라이언트가 해당 메서드를 호출하기 위한 전제조건을 나열해야 한다.
  - 메서드가 성공적으로 수행된 후에 만족해야 하는 사후조건도 모두 나열해야 한다.
  - 전제조건은 `@throws` 태그로 비검사 예외를 선언하여 암시적으로 기술한다.
    - 비검사 예외 하나가 전제조건 하나와 연결되도록
  - `@param` 태그를 이용하여 그 조건에 영향받는 매개변수에 기술할 수도 있다.
  - 부작용 또한 문서화해야 한다.
    - 사후조건으로 명확히 나타나지 않지만 시스템 상태에 어떠한 변화를 가져오는 것
    - 예를 들어 백그라운드 스레드를 시작시키는 메서드라면 그 사실을 문서에 밝혀야 한다.
- 메서드의 계약을 완벽히 기술하기 위해 모든 매개변수에 `@param` 태그, 반환 타입이 `void` 가 아니라면 `@return` 태그, 발생 가능한 모든 예외에 `@throws` 태그를 달아야 한다.
  - 지켜지고 있는 코딩 표준에서 허락하는 경우 `@return` 태그의 설명이 메서드 설명과 같을 때 `@return` 태그는 생략해도 괜찮다.
- 관례상 `@param` 태그와 `@return` 태그의 설명은 해당 매개변수가 뜻하는 값이나 반환값을 설명하는 명사구를 사용한다.
  - 드물게 명사구가 아닌 산술 표현식을 쓰기도 한다.
  - `BigInteger` 의 문서가 대표적인 예시라고 하지만 찾을 수 없었다.

## 예시
### 문서화 주석에 HTML 태그를 사용할 수 있다.
```java
/**
 * Returns the element at the specified position in this list.
 * 
 * <p>This method is <i>not</i> guaranteed to run in constant
 * time. In some implementations it may run in time proportional
 * to the element position
 * 
 * @param index index of element to return; must be
 *              non-negative and less than the size of this list
 * @return the element at the specified position in this list
 * @throws IndexOutOfBoundsException if the index is out of range
 *         ({@code index < 0 || index >= this.size()})
 */
E get(int index);
```
- 자바독 유틸리티가 문서화 주석을 HTML 로 변환하기 때문

### {@code} 를 통해 코드용 폰트로 렌더링한다.
- `IntelliJ` 에서는 별로 다르지 않다.
- 태그로 감싼 내용에 포함된 HTML 요소나 다른 자바독 태그를 무시한다.
- 덕분에 `<` 와 같은 기호를 사용할 수 있다.
- 만약 여러줄의 코드를 작성하는 경우 `<pre>{@code .. 코드 ... }</pre>` 형태로 사용할 수 있다.
```java
* <pre>{@code
*     Optional<Path> p =
*         uris.stream().filter(uri -> !isProcessedYet(uri))
*                       .findFirst()
*                       .map(Paths::get);
* }</pre>
```
- `Optional.map` 의 주석 중 일부로 `<pre>{@code .. 코드 ... }</pre>` 가 사용된 부분이다.
- `@` 기호에는 무조건 탈출문자를 붙여야 하니 문서화 주석 안의 코드에서 어노테이션을 사용하는 경우 주의가 필요하다.

### 자기사용 패턴은 자바 8 에서 추가된 @implSpec 태그로 문서화한다.
- 클래스를 상속용으로 설계할 때는 자기사용 패턴에 대해서도 문서에 남겨야 한다.
```java
/**
 * Returns true if this collection is empty
 * 
 * @implSpec
 * This implementation return {@code this.size() == 0}.
 * 
 * @return true if this collection is empty
 */
public boolean isEmpty();
```
- 자바 11 까지도 자바독 명령줄에서 `-tag "implSpec:a:Implementation Requirements:"` 스위치를 켜주지 않으면 `@implSpec` 태그를 무시해버린다.
  - 몇버전 부터일지는 모르겠지만 17에서는 무시되지 않는 것 같긴 하다.

### {@literal}
- API 설명에 `<` , `>` , `&` 등의 HTML 메타문자를 포함시키려는 경우 `{@literal}` 을 사용하면 된다.

### 각 문서화 주석의 첫 번째 문장은 해당 요소의 요약 설명으로 간주된다.
- 요약 설명은 대상의 기능을 고유하게 기술해야 한다.
  - 한 클래스 안에서 요약 설명이 같은 멤버가 둘 이상이면 안된다.
  - 다중정의시 특히 더 조심해야한다.
- 요약 설명시 마침표를 주의해야 한다.
  - 요약 설명이 끝나는 판단 기준은 처음 발견되는 마침표까지 이다.
  - `{@leteral}` 로 감싸도록 하자
  - 자바 10 부터는 `{@summary 요약 설명}` 이라는 요약 설명 전용 태그가 추가되어 보다 깔끔하게 사용 가능하다.

### {@index} 태그를 사용하여 색인화할 수 있다.
  - `* This method compiles with the {@index IEEE 754} standard.`

### 제네릭, 열거 타입, 어노테이션은 특별히 주의해야한다.
- **모든 타입 매개변수에 주석을 달아야 한다.**
![Map<K, V>](https://github.com/Evil-Goblin/BookStudy/assets/74400861/eb01372e-2b23-4c03-86bb-1f1ade72efaa)

### 열거 타입을 문서화할 때는 상수들에도 주석을 달아야 한다.
```java
/**
 * An instrument section of a symphony orchestra.
 */
public enum OrchestraSection {
    /** Woodwinds, such as flute, clarinet, and oboe. */
    WOODWIND,
    
    /** Brass instruments, such as french horn and trumpet. */
    BRASS,
    
    /** Percussion instruments, such as timpani and cymbals. */
    PERCUSSION,
    
    /** Stringed instruments, such as violin and cello. */
    STRING;
}
```
- 열거 타입 자체와 `public` 메서드 또한 마찬가지이다.

### 어노테이션 타입을 문서화할 때는 멤버들에도 모두 주석을 달아야 한다.
```java
/**
 * Indicates that the annotated method is a test method that
 * must throw the designated exception to pass.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    /**
     * The exception that the annotated test method must throw
     * in order to pass. (The test is permitted to throw any
     * subtype of the type described by this class object.)
     */
    Class<? extends Throwable> value();
}
```

### 패키지, 모듈 설명 주석
- 패키지를 설명하는 문서화 주석은 `package-info.java` 파일에 작성한다.
- 모듈 관련 설명은 `module-info.java` 파일에 작성한다.

### thread-safe , 직렬화 가능성을 기술해야한다.
- 클래스 혹은 정적 메서드의 `thread-safe` 여부를 반드시 API 설명에 포함해야 한다.
- 직렬화할 수 있는 클래스는 직렬화 형태도 API 설명에 기술해야 한다.

## 주석의 상속
- 자바독은 메서드 주석을 '상속' 시킬 수 있다.
  - `Collection` 의 상속 인터페이스들이 대부분 그렇다.
  - 문서화 주석이 없는 API 요소는 상위 요소(클래스보다 인터페이스 우선) 에서 주석을 찾아준다.
- `{@inheritDoc}` 태그를 사용해 상위 타입의 문서화 주석 일부를 상속할 수 있다.

## JavaDoc Lint
- 자바 7 에서는 명령줄에서 `-Xdoclint` 스위치를 통해 자바독 문서를 잘 작성했는지 확인하는 기능을 제공한다.
- 자바 8 부터는 기본으로 작동한다.

## 정리
- 문서화 주석은 API 를 문서화하는 가장 좋은 방법이다.
- 공개 API 라면 빠짐없이 설명을 달아야 한다.
- 표준 규약을 일관되게 지키자.
- 문서화 주석에 HTML 태그를 사용할 수 있음을 기억하라
  - HTML 메타문자는 특별하게 취급해야 한다.
