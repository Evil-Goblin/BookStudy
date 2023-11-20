package Chapter03;

import Chapter01.Apple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static Chapter01.Color.GREEN;
import static Chapter01.Color.RED;

public class ComparatorSample {
    public static void main(String[] args) {
        List<Apple> inventory = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            inventory.add(new Apple(RED, 145 + i));
            inventory.add(new Apple(GREEN, 145 + i));
        }

        // 역정렬
        inventory.sort(Comparator.comparing(Apple::getWeight).reversed());

        // Comparator 연결 (무게가 동일한 경우 색으로 추가 정렬)
        inventory.sort(Comparator.comparing(Apple::getWeight)
                .reversed()
                .thenComparing(Apple::getColor));
    }
}
