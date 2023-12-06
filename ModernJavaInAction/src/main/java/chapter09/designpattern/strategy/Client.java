package chapter09.designpattern.strategy;

public class Client {
    public static void main(String[] args) {
        Validator numericValidator = new Validator(new IsNumeric());
        boolean aaaaIsNotNumber = numericValidator.validate("aaaa");
        System.out.println("aaaaIsNotNumber = " + aaaaIsNotNumber);

        Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
        boolean bbbbIsAllLowerCase = lowerCaseValidator.validate("bbbb");
        System.out.println("bbbbIsAllLowerCase = " + bbbbIsAllLowerCase);

        Validator lambdaNumericValidator = new Validator(s -> s.matches("\\d+"));
        boolean b1 = lambdaNumericValidator.validate("aaaa");
        System.out.println("b1 = " + b1);

        Validator lambdaLowerCaseValidator = new Validator(s -> s.matches("[a-z]+"));
        boolean b2 = lambdaLowerCaseValidator.validate("bbbb");
        System.out.println("b2 = " + b2);
    }
}
