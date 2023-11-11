package Chapter01;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static Chapter01.Color.GREEN;
import static Chapter01.Color.RED;

enum Color {
    RED, GREEN
}

public class AppleFilter {
    static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();

        for (Apple apple : inventory) {
            if (GREEN.equals(apple.getColor())) {
                result.add(apple);
            }
        }

        return result;
    }

    static List<Apple> filterHeavyApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();

        for (Apple apple : inventory) {
            if (apple.getWeight() > 150) {
                result.add(apple);
            }
        }

        return result;
    }

    static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> predicate) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (predicate.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<Apple> inventory = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            inventory.add(new Apple(RED, 145 + i));
            inventory.add(new Apple(GREEN, 145 + i));
        }

        List<Apple> filteredGreenApples = filterGreenApples(inventory);
        List<Apple> filteredHeavyApples = filterHeavyApples(inventory);

        System.out.println("filteredGreenApples = " + filteredGreenApples);
        System.out.println("filteredHeavyApples = " + filteredHeavyApples);

        // 함수형 인터페이스 Predicate 사용
        List<Apple> filteredApplesIsGreen = filterApples(inventory, Apple::isGreenApple);
        List<Apple> filteredApplesIsHeavy = filterApples(inventory, Apple::isHeavyApple);

        System.out.println("filteredApplesIsGreen = " + filteredApplesIsGreen);
        System.out.println("filteredApplesIsHeavy = " + filteredApplesIsHeavy);

        // Predicate 를 람다식으로 전달
        List<Apple> filteredApplesLambdaGreen = filterApples(inventory, apple -> GREEN.equals(apple.getColor()));
        List<Apple> filteredApplesLambdaHeavy = filterApples(inventory, apple -> apple.getWeight() > 150);

        System.out.println("filteredApplesLambdaGreen = " + filteredApplesLambdaGreen);
        System.out.println("filteredApplesLambdaHeavy = " + filteredApplesLambdaHeavy);


    }

}

class Apple {

    private final Color color;
    private final int weight;

    public Apple(Color color, int weight) {
        this.color = color;
        this.weight = weight;
    }

    public Color getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Apple{" +
                "color=" + color +
                ", weight=" + weight +
                '}';
    }

    public static boolean isGreenApple(Apple apple) {
        return GREEN.equals(apple.getColor());
    }

    public static boolean isHeavyApple(Apple apple) {
        return apple.getWeight() > 150;
    }
}
