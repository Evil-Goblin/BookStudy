package chapter10.methodchain;

import chapter10.domain.Order;

import static chapter10.methodchain.MethodChainingOrderBuilder.*;

public class MethodChainClient {
    public static void main(String[] args) {
        Order order = forCustomer("BigBank")
                .buy(80)
                .stock("IBM")
                .on("NYSE")
                .at(125.00)
                .sell(50)
                .stock("GOOGLE")
                .on("NASDAQ")
                .at(375.00)
                .end();
    }
}
