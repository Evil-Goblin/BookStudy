package chapter10.lambda;

import chapter10.domain.Trade;

import java.util.function.Consumer;

public class TradeBuilder {
    Trade trade = new Trade();

    public void quantity(int quantity) {
        trade.setQuantity(quantity);
    }

    public void price(double price) {
        trade.setPrice(price);
    }

    public void stock(Consumer<StockBuilder> consumer) {
        StockBuilder stockBuilder = new StockBuilder();
        consumer.accept(stockBuilder);
        trade.setStock(stockBuilder.stock);
    }
}
