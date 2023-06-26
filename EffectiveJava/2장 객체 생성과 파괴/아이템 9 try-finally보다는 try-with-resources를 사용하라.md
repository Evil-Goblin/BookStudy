## 자원의 사용과 회수
대표적으로 `File`이 있다.\
사용한 후 `close`를 통해 자원을 회수해야한다.

```java
public class TryFinallySample {
    public static String firstLineOfFile(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        try {
            return bufferedReader.readLine();
        } finally {
            bufferedReader.close();
        }
    }
}
```
- 전통적으로 사용되는 `try-finally`구문이다.

```java
public class TryFinallySample {
    public static final int BUFFER_SIZE = 1024;

    public static void copy(String src, String dst) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(src);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = fileInputStream.read(buf)) >= 0) {
                    fileOutputStream.write(buf, 0, n);
                }
            } finally {
                fileOutputStream.close();
            }
        } finally {
            fileInputStream.close();
        }
    }
}
```
- 하지만 자원의 갯수가 늘어나게 된다면 코드가 더욱 지저분해진다.
- 만약 하드웨어 자체에 문제가 생겨서 `readLine`에서 에러가 발생하고 `finally`인 `close`에서도 에러가 발생하게 되면 스택트레이스가 완전히 덮어써져서 첫 번째로 발생한 예외에 대한 정보는 사라지게 된다.

## `try-with-resources` (Java 7 부터)
`try-with-resources`구문을 사용하기 위해서는 `AutoCloseable` 인터페이스를 구현해야 한다.

```java
public class TryWithResourcesSample {
    public static String firstLineOfFile(String path) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            return bufferedReader.readLine();
        }
    }

    public static final int BUFFER_SIZE = 1024;

    public static void copy(String src, String dst) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(src);
             FileOutputStream fileOutputStream = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = fileInputStream.read(buf)) >= 0) {
                fileOutputStream.write(buf, 0, n);
            }
        }
    }
}
```
- 위의 예제를 `try-with-resources`를 통해 구현한 코드이다.
- 코드의 길이도 짧고 가독성도 좋으면 문제를 진단하기도 좋다.
- 이전 코드에서와 같은 문제가 발생하여 `readLine`, `close`에 연속하여 예외가 발생한 경우 `close`에 발생한 예외는 숨겨지고 `readLine`에서 발생한 예외가 기록된다.
- 이처럼 숨겨진 예외들을 `suppressed exception`이라고 하며 `Java 7` 부터 적용되었다.

```java
public static String firstLineOfFile(String path) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
        return bufferedReader.readLine();
    } catch (IOException e) {
        throw e;
    }
}
```
- `catch` 절을 사용하는 것 또한 가능하다.

## `suppressd exception`
```java
public class ThrowExceptionSample implements AutoCloseable {

    public void throwing() {
        throw new RuntimeException();
    }

    @Override
    public void close() throws Exception {
        throw new RuntimeException();
    }
}
```
- `close`와 로직에서 에러를 던지도록 하였다.
- 이 클래스를 이용해 `try-finally`와 `try-with-resources`의 스택트레이스의 차이를 보려고한다.

```java
public class TryFinallySample {
    public void tryFinalException() throws Exception {
        ThrowExceptionSample throwExceptionSample = new ThrowExceptionSample();
        try {
            throwExceptionSample.throwing();
        } finally {
            throwExceptionSample.close();
        }
    }

    public static void main(String[] args) throws Exception {
        TryFinallySample tryFinallySample = new TryFinallySample();
        tryFinallySample.tryFinalException();
    }
}
```
```
Exception in thread "main" java.lang.RuntimeException
	at study.chapter2.item9.ThrowExceptionSample.close(ThrowExceptionSample.java:11)
	at study.chapter2.item9.TryFinallySample.tryFinalException(TryFinallySample.java:11)
	at study.chapter2.item9.TryFinallySample.main(TryFinallySample.java:17)
```
- `try-finally`의 경우 나중에 터진 에러에 대한 정보만 표시되어 먼저 터진 에러는 알 수 없다.

```java
public class TryWithResourcesSample {
    public void tryWithResourcesException() throws Exception {
        try (ThrowExceptionSample throwExceptionSample = new ThrowExceptionSample()) {
            throwExceptionSample.throwing();
        }
    }

    public static void main(String[] args) throws Exception {
        TryWithResourcesSample tryWithResourcesSample = new TryWithResourcesSample();
        tryWithResourcesSample.tryWithResourcesException();
    }
}
```
```
Exception in thread "main" java.lang.RuntimeException
	at study.chapter2.item9.ThrowExceptionSample.throwing(ThrowExceptionSample.java:6)
	at study.chapter2.item9.TryWithResourcesSample.tryWithResourcesException(TryWithResourcesSample.java:9)
	at study.chapter2.item9.TryWithResourcesSample.main(TryWithResourcesSample.java:15)
	Suppressed: java.lang.RuntimeException
		at study.chapter2.item9.ThrowExceptionSample.close(ThrowExceptionSample.java:11)
		at study.chapter2.item9.TryWithResourcesSample.tryWithResourcesException(TryWithResourcesSample.java:8)
		... 1 more
```
- `try-with-resources`의 경우 가장 먼저 터진 에러부터 출력해주고 후에 터진 에러를 `Suppressed`를 통해 보여준다.


## 정리
```
꼭 회수해야 하는 자원을 다룰때 try-finally 말고, try-with-resources를 사용하자.
코드도 짧고 가독성도 좋으며 문제 진단에도 좋다.
```
