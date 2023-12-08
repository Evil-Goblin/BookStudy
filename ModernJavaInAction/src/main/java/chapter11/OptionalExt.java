package chapter11;

import java.util.Optional;
import java.util.Properties;

public class OptionalExt {
    public static Optional<Integer> stringToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    public static int readDuration(Properties properties, String name) {
        return Optional.ofNullable(properties.getProperty(name))
                .flatMap(OptionalExt::stringToInt)
                .filter(i -> i > 0)
                .orElse(0);
    }


    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("a", "5");
        props.setProperty("b", "true");
        props.setProperty("c", "-3");

        int duration = readDuration(props, "a");
        System.out.println(duration);

        duration = readDuration(props, "b");
        System.out.println(duration);

        duration = readDuration(props, "c");
        System.out.println(duration);

        duration = readDuration(props, "d");
        System.out.println(duration);
    }
}
