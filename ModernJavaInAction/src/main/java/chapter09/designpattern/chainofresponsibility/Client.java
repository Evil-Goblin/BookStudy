package chapter09.designpattern.chainofresponsibility;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Client {
    public static void main(String[] args) {
        ProcessObject<String> headerTextProcessing = new HeaderTextProcessing();
        ProcessObject<String> spellCheckerProcessing = new SpellCheckerProcessing();

        headerTextProcessing.setSuccessor(spellCheckerProcessing);

        String result = headerTextProcessing.handle("Aren`t labdas really sexy?!!");
        System.out.println("result = " + result);

        UnaryOperator<String> headerProcessing = text -> "From Raoul, Mario and Alan: " + text;
        UnaryOperator<String> spellCheckProcessing = text -> text.replaceAll("labda", "lambda");

        Function<String, String> pipeline = headerProcessing.andThen(spellCheckProcessing);
        result = pipeline.apply("Aren`t labdas really sexy?!!");
        System.out.println("result = " + result);
    }
}
