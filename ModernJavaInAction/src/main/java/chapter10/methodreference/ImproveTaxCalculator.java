package chapter10.methodreference;

import chapter10.domain.Order;

import java.util.function.DoubleUnaryOperator;

public class ImproveTaxCalculator {
    public DoubleUnaryOperator taxFunction = d -> d;

    public ImproveTaxCalculator with(DoubleUnaryOperator f) {
        taxFunction = taxFunction.andThen(f);
        return this;
    }

    public double calculate(Order order) {
        return taxFunction.applyAsDouble(order.getValue());
    }
}
