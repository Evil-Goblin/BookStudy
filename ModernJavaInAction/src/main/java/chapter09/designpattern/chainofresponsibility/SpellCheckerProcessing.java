package chapter09.designpattern.chainofresponsibility;

public class SpellCheckerProcessing extends ProcessObject<String> {
    @Override
    protected String handleWork(String input) {
        return input.replaceAll("labda", "lambda");
    }
}
