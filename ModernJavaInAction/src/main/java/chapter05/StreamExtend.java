package chapter05;

import chapter04.Dish;

import java.util.Arrays;
import java.util.List;

public class StreamExtend {
    public static void main(String[] args) {
        distinctSample();

        List<Dish> menu = Arrays.asList(
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("salmon", false, 450, Dish.Type.FISH),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("pork", false, 800, Dish.Type.MEAT)
        );

        takeWhileSample(menu); // predicate 가 True 인덱스까지 슬라이스
        // True True False Ture -> True True

        dropWhileSample(menu); // predicate 가 False 가 되는 인덱스부터 슬라이스
        // True True False True -> False True

        limitSample(menu); // 결과의 최대 요소 제한

        skipSample(menu); // 요소 스킵

        mapSample(menu); // 요소 변환

        flatMapSample();

        // anyMatch 적어도 한 요소가 Predicate 와 일치하는가
        // allMatch 모든 요소가 Predicate 와 일치하는가
        // noneMatch 모든 요소가 Predicate 와 일치하지 않는가

        findAnySample(menu);

        // findFirst 첫 번째 요소 반환

        reduceSample(); // args0: 초기값, args1: 연산 (args0: 누적된 값, args1: 이번 인덱스의 값)
        // overload 된 reduce 로 초기값을 받지 않는 메소드가 제공된다.
    }

    private static void reduceSample() {
        List<Integer> numbers = List.of(4, 5, 3, 9);
        Integer reduce = numbers.stream()
                .reduce(0, (a, b) -> {
                    // System.out.println("a = " + a); // a 누적된 값
                    // System.out.println("b = " + b); // 이번 인덱스의 값
                    return a + b;
                });
        System.out.println("reduce = " + reduce);

        numbers.stream()
                .reduce((a, b) -> {
                    // System.out.println("a = " + a); // 첫 인덱스가 a 두번째 인덱스가 b 로 제공되어 iter 가 수행된다.
                    // System.out.println("b = " + b); //
                    return a + b;
                }).ifPresent(System.out::println);

        List<Integer> one = List.of(1);
        one.stream()
                .reduce((a, b) -> {
                    // System.out.println("a = " + a); // 요소가 하나인 경우 reduce 의 연산조차 수행되지 않는다.
                    // System.out.println("b = " + b);
                    return a + b;
                })
                .ifPresent(System.out::println);
    }

    private static void findAnySample(List<Dish> menu) {
        menu.stream()
                .filter(Dish::isVegetarian)
                .findAny()
                .ifPresent(d -> System.out.println(d.getName()));
    }

    private static void flatMapSample() {
        List<String> list = Arrays.asList("Hello", "World");
        List<String> flatMapResult = list.stream()
                .map(word -> word.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .toList();
        System.out.println("flatMapResult = " + flatMapResult);
    }

    private static void mapSample(List<Dish> menu) {
        List<String> list = menu.stream()
                .map(Dish::getName)
                .toList();
        System.out.println("list = " + list);
    }

    private static void skipSample(List<Dish> menu) {
        List<Dish> list = menu.stream()
                .filter(Dish::isVegetarian)
                .skip(2)
                .toList();
        System.out.println("list = " + list);
    }

    private static void limitSample(List<Dish> menu) {
        List<Dish> list = menu.stream()
                .filter(Dish::isVegetarian)
                .limit(2)
                .toList();
        System.out.println("list = " + list);
    }

    private static void takeWhileSample(List<Dish> menu) {
        List<Dish> list = menu.stream()
                .takeWhile(dish -> dish.getCalorie() < 320)
                .toList();
        System.out.println("list = " + list);
    }

    private static void dropWhileSample(List<Dish> menu) {
        List<Dish> list = menu.stream()
                .dropWhile(dish -> dish.getCalorie() < 320)
                .toList();
        System.out.println("list = " + list);
    }

    private static void distinctSample() {
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct()
                .forEach(System.out::println);
    }
}
