package chapter10.methodreference;

import chapter10.domain.Order;

public class Client {
    public static void main(String[] args) {
        Order order = new Order();

        double calculate = new TaxCalculator().withTaxRegional()
                .withTaxGeneral()
                .calculate(order);

        double improveCalculate = new ImproveTaxCalculator()
                .with(Tax::regional)
                .with(Tax::surcharge)
                .calculate(order);
    }
}
