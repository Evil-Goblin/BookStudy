package Chapter04;

import java.util.*;
import java.util.stream.Collectors;

public class StreamSample {
    public static void main(String[] args) {
        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH)
        );

        whenJava7(menu);
        afterJava8(menu);
        afterJava8Parallel(menu);
    }

    private static void afterJava8(List<Dish> menu) {
        List<String> lowCaloricDishesName = menu.stream()
                .filter(d -> d.getCalorie() < 400)
                .sorted(Comparator.comparing(Dish::getCalorie))
                .map(Dish::getName)
                .toList();
        System.out.println("lowCaloricDishesName = " + lowCaloricDishesName);
    }

    private static void afterJava8Parallel(List<Dish> menu) {
        List<String> lowCaloricDishesName = menu.parallelStream()
                .filter(d -> d.getCalorie() < 400)
                .sorted(Comparator.comparing(Dish::getCalorie))
                .map(Dish::getName)
                .toList();
        System.out.println("lowCaloricDishesName = " + lowCaloricDishesName);
    }

    private static void whenJava7(List<Dish> menu) {
        List<Dish> lowCaloricDishes = new ArrayList<>();

        for (Dish dish : menu) {
            if (dish.getCalorie() < 400) {
                lowCaloricDishes.add(dish);
            }
        }

        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish o1, Dish o2) {
                return Integer.compare(o1.getCalorie(), o2.getCalorie());
            }
        });

        List<String> lowCaloricDishesName = new ArrayList<>();
        for (Dish dish : lowCaloricDishes) {
            lowCaloricDishesName.add(dish.getName());
        }
        System.out.println("lowCaloricDishesName = " + lowCaloricDishesName);
    }
}
