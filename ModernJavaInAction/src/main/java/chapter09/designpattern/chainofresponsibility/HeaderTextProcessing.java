package chapter09.designpattern.chainofresponsibility;

public class HeaderTextProcessing extends ProcessObject<String> {
    @Override
    protected String handleWork(String input) {
        return "From Raoul, Mario and Alan: " + input;
    }
}
