package Chapter01;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static Chapter01.Color.GREEN;
import static Chapter01.Color.RED;

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

    static void prettyPrintApple(List<Apple> inventory, Function<Apple, String> function) {
        for (Apple apple : inventory) {
            String toString = function.apply(apple);
            System.out.println("toString = " + toString);
        }
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

        prettyPrintApple(inventory, apple -> {
            String characteristic = apple.getWeight() > 150 ? "heavy" : "light";
            return "A " + characteristic + " " + apple.getColor() + " apple";
        });
    }

}

