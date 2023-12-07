package chapter10.lambda;

import chapter10.domain.Stock;

public class StockBuilder {
    Stock stock = new Stock();

    public void symbol(String symbol) {
        stock.setSymbol(symbol);
    }

    public void market(String market) {
        stock.setMarket(market);
    }
}
