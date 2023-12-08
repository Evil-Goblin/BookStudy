package chapter10.mix;

import chapter10.domain.Order;

import static chapter10.mix.MixedBuilder.*;

public class MixedClient {
    public static void main(String[] args) {
        Order order = forCustomer("BigBank",
                buy(t -> t.quantity(80)
                        .stock("IBM")
                        .on("NYSE")
                        .at(125.00)),
                sell(t -> t.quantity(50)
                        .stock("GOOGLE")
                        .on("NASDAQ")
                        .at(125.00))
        );

    }
}
