package chapter10.methodchain;

import chapter10.domain.Trade;

public class TradeBuilder {
    private final MethodChainingOrderBuilder builder;
    public final Trade trade = new Trade();

    TradeBuilder(MethodChainingOrderBuilder builder, Trade.Type type, int quantity) {
        this.builder = builder;
        trade.setType(type);
        trade.setQuantity(quantity);
    }

    public StockBuilder stock(String symbol) {
        return new StockBuilder(builder, trade, symbol);
    }
}
