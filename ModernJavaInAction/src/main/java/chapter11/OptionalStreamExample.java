package chapter11;

import java.util.Optional;

public class OptionalStreamExample {
    public static void main(String[] args) {
        Insurance insurance = null;
        Optional<Insurance> optionalInsurance = Optional.ofNullable(insurance);
        Optional<String> name = optionalInsurance.map(Insurance::getName);

        Person person = new Person();
        Optional<Person> optionalPerson = Optional.of(person);
        String result = optionalPerson.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }
}
