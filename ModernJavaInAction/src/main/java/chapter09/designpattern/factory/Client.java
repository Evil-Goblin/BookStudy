package chapter09.designpattern.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Client {
    public static void main(String[] args) {
        Product loan = ProductFactory.createProduct("loan");

        Map<String, Supplier<Product>> map = new HashMap<>();
        map.put("loan", Loan::new);
        map.put("stock", Stock::new);
        map.put("bond", Bond::new);

        Supplier<Product> p = map.get("loan");
        if (p != null) p.get();
    }
}
