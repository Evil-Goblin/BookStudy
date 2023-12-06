package chapter09.designpattern.templatemethod;

import java.util.function.Consumer;

public abstract class OnlineBanking {
    public void processCustom(int id) {
        Customer c = Database.getCustomerWithId(id);
        makeCustomerHappy(c);
    }

    public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
        Customer c = Database.getCustomerWithId(id);
        makeCustomerHappy.accept(c);
    }

    abstract void makeCustomerHappy(Customer c);
}
