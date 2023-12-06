package chapter09.designpattern.templatemethod;

public class Database {
    public static Customer getCustomerWithId(int id) {
        return new Customer();
    }
}
