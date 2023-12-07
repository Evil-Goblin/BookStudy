package chapter10.methodchain;

import chapter10.domain.Trade;

public class TradeBuilderWithStock {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;

    TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade) {
        this.builder = builder;
        this.trade = trade;
    }

    public MethodChainingOrderBuilder at(double price) {
        trade.setPrice(price);
        return builder.addTrade(trade);
    }
}
