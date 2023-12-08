package chapter15;

public class Client {
    public static void main(String[] args) {
//        SimpleCell c = new SimpleCell("C");
//        SimpleCell b = new SimpleCell("B");
//        SimpleCell a = new SimpleCell("A");
//
//        a.subscribe(c);
//
//        a.onNext(10);
//        b.onNext(20);

        MethodRefTest methodRefTest = new MethodRefTest();
        StringPublisher stringPublisher = new StringPublisher();
        stringPublisher.subscribe(methodRefTest::test);

        ArithmeticCell c = new ArithmeticCell("C");
        SimpleCell b = new SimpleCell("B");
        SimpleCell a = new SimpleCell("A");

        a.subscribe(c::setLeft);
        b.subscribe(c::setRight);

        a.onNext(10);
        b.onNext(20);
        a.onNext(15);
    }
}
