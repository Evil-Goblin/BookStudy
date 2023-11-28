package Chapter05;

import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparing;

public class Quiz {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        Q1(transactions);

        Q2(transactions);

        Q3(transactions);

        Q4(transactions);

        Q5(transactions);

        Q6(transactions);

        Q7(transactions);

        Q8(transactions);
    }

    private static void Q8(List<Transaction> transactions) {
        // 전체 트랜잭션 중 최솟값은 얼마인가
        int A8 = transactions.stream()
                .mapToInt(Transaction::getValue)
                .reduce(Integer.MAX_VALUE, Integer::min);
        System.out.println("A8 = " + A8);
    }

    private static void Q7(List<Transaction> transactions) {
        // 전체 트랜잭션 중 최댓값은 얼마인가
        int A7 = transactions.stream()
                .mapToInt(Transaction::getValue)
                .reduce(Integer.MIN_VALUE, Integer::max);
//        Integer A7 = transactions.stream()
//                .map(Transaction::getValue)
//                .max(Integer::compareTo).get();
        System.out.println("A7 = " + A7);
    }

    private static void Q6(List<Transaction> transactions) {
        // 켐브릿지에 거주하는 거래자의 모든 트랜잭션값을 출력
        transactions.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity()))
                .map(Transaction::getValue)
                .forEach(System.out::println);
    }

    private static void Q5(List<Transaction> transactions) {
        // 밀라노에 거래자가 있는가
        boolean A5 = transactions.stream()
                .anyMatch(t -> "Milan".equals(t.getTrader().getCity()));
        System.out.println("A5 = " + A5);
    }

    private static void Q4(List<Transaction> transactions) {
        // 모든 거래자의 이름을 알파벳순으로 정렬해서 반환
        String A4 = transactions.stream()
                .map(t -> t.getTrader().getName())
                .distinct()
                .sorted()
                .reduce("", String::concat);
//                .toList();
        System.out.println("A4 = " + A4);
    }

    private static void Q3(List<Transaction> transactions) {
        // 캠브릿지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬
        List<Trader> A3 = transactions.stream()
                .map(Transaction::getTrader)
                .filter(t -> "Cambridge".equals(t.getCity()))
                .distinct()
                .sorted(comparing(Trader::getName))
                .toList();
        System.out.println("A3 = " + A3);
    }

    private static void Q2(List<Transaction> transactions) {
        // 거래자가 근무하는 모든 도시를 중복 없이 나열
        List<String> A2 = transactions.stream()
                .map(t -> t.getTrader().getCity())
                .distinct()
                .toList();
        System.out.println("A2 = " + A2);
    }

    private static void Q1(List<Transaction> transactions) {
        // 2011년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리
        List<Transaction> A1 = transactions.stream()
                .filter(t -> t.getYear() == 2011)
                .sorted(comparing(Transaction::getValue))
                .toList();
        System.out.println("A1 = " + A1);
    }
}
