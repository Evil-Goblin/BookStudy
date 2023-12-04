package chapter08;

import java.util.Map;

public class MapFactory {
    public static void main(String[] args) {
        Map<String, Integer> ofEntries = Map.ofEntries(Map.entry("Raphael", 30),
                Map.entry("Olivia", 25),
                Map.entry("Thibaut", 26));
        System.out.println("ofEntries = " + ofEntries);
    }
}
