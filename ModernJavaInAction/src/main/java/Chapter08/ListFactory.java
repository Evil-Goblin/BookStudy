package Chapter08;

import java.util.List;

public class ListFactory {
    public static void main(String[] args) {
        List<String> immutable = List.of("a", "b", "c"); // 불변 리스트를 만들기 때문에 변경 불가능하다.
//        immutable.add("d"); // UnsupportedOperationException
    }
}
