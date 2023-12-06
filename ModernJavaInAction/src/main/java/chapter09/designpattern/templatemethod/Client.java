package chapter09.designpattern.templatemethod;

public class Client {
    public static void main(String[] args) {
        new OnlineBankingLambda().processCustomer(1337, c -> System.out.println("c = " + c));
    }
}
