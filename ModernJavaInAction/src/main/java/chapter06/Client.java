package chapter06;

import chapter04.Dish;

import java.util.*;
import java.util.stream.IntStream;

import static chapter06.Client.CaloricLevel.*;
import static java.util.List.*;
import static java.util.stream.Collectors.*;

public class Client {
    public enum CaloricLevel {
        DIET, NORMAL, FAT
    }
    public static void main(String[] args) {
        List<Dish> menu = of(
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

        리듀싱과요약(menu);

        그룹화(menu);

        분할(menu);

        ArrayList<Object> 발행누적합침 = menu.stream().collect(
                ArrayList::new,
                List::add,
                List::addAll
        );

        System.out.println("발행누적합침 = " + 발행누적합침);
    }

    private static void 분할(List<Dish> menu) {
        Map<Boolean, List<Dish>> isVegetarianMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian));
        System.out.println("isVegetarianMenu = " + isVegetarianMenu);

        Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = menu.stream().collect(
                partitioningBy(Dish::isVegetarian,
                        groupingBy(Dish::getType))
        );
        System.out.println("vegetarianDishesByType = " + vegetarianDishesByType);

        Map<Boolean, List<Integer>> partitionPrime = IntStream.range(2, 100).boxed()
                .collect(partitioningBy(Client::isPrime));
        System.out.println("partitionPrime = " + partitionPrime);
    }

    private static boolean isPrime(int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return IntStream.rangeClosed(2, candidateRoot)
                .noneMatch(i -> candidate % i == 0);
    }

    private static void 그룹화(List<Dish> menu) {
        Map<Dish.Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));
        System.out.println("dishesByType = " + dishesByType);

        Map<Dish.Type, List<Dish>> filtering = menu.stream().collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalorie() > 500, toList())));
        System.out.println("filtering = " + filtering);

        Map<Dish.Type, List<String>> mapping = menu.stream().collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
        System.out.println("mapping = " + mapping);

        Map<String, List<String>> dishTags = new HashMap<>();
        dishTags.put("pork", of("greasy", "salty"));
        dishTags.put("beef", of("salty", "roasted"));
        dishTags.put("chicken", of("fried", "crisp"));
        dishTags.put("french fries", of("greasy", "fried"));
        dishTags.put("rice", of("light", "natural"));
        dishTags.put("season fruit", of("fresh", "natural"));
        dishTags.put("pizza", of("tasty", "salty"));
        dishTags.put("prawns", of("tasty", "roasted"));
        dishTags.put("salmon", of("delicious", "fresh"));

        Map<Dish.Type, Set<String>> flatMapping = menu.stream()
                .collect(groupingBy(Dish::getType,
                        flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
        System.out.println("flatMapping = " + flatMapping);

        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> 다수준그룹화 = menu.stream().collect(
                groupingBy(Dish::getType,
                        groupingBy(dish -> {
                            if (dish.getCalorie() <= 400) {
                                return DIET;
                            } else if (dish.getCalorie() <= 700) {
                                return NORMAL;
                            } else {
                                return FAT;
                            }
                        }))
        );
        System.out.println("다수준그룹화 = " + 다수준그룹화);

        Map<Dish.Type, Long> typeCount = menu.stream().collect(groupingBy(Dish::getType, counting()));
        System.out.println("typeCount = " + typeCount);

        Map<Dish.Type, Dish> collectionAndThen = menu.stream().collect(groupingBy(Dish::getType,
                collectingAndThen(
                        maxBy(Comparator.comparingInt(Dish::getCalorie)),
                        Optional::get
                )));
        System.out.println("collectionAndThen = " + collectionAndThen);
    }

    private static void 리듀싱과요약(List<Dish> menu) {
        long collect = menu.stream().collect(counting());
        System.out.println("collect = " + collect);
        collect = menu.stream().count();
        System.out.println("collect = " + collect);

        Optional<Dish> maxBy = menu.stream().collect(maxBy(Comparator.comparingInt(Dish::getCalorie)));
        maxBy.ifPresent(System.out::println);

        int totalCalorie = menu.stream().collect(summingInt(Dish::getCalorie));
        System.out.println("totalCalorie = " + totalCalorie);

        String joining = menu.stream().map(Dish::getName).collect(joining());
        System.out.println("joining = " + joining);

        joining = menu.stream().map(Dish::getName).collect(joining(", "));
        System.out.println("joining = " + joining);
    }
}
