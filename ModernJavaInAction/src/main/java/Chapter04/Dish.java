package Chapter04;

public class Dish {
    public enum Type { MEAT, FISH, OTHER }

    private final String name;
    private final boolean vegetarian;
    private final int calorie;
    private final Type type;

    public Dish(String name, boolean vegetarian, int calorie, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calorie = calorie;
        this.type = type;
    }

    public int getCalorie() {
        return calorie;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
