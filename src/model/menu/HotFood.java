package model.menu;

import java.util.HashMap;
import java.util.Map;

import model.ApplianceType;
import model.Ingredient;
import model.MenuItem;

public abstract class HotFood extends MenuItem {

    public HotFood(String name, double price) {
        super(name, price, 3, ApplianceType.GRILL); // 3-second cook time for hot food
    }

    public static class Burger extends HotFood {
        public Burger() { super("Burger", 8.50); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.BUN, 2);
            req.put(Ingredient.BEEF_PATTY, 1);
            req.put(Ingredient.LETTUCE, 1);
            req.put(Ingredient.TOMATO, 1);
            req.put(Ingredient.CHEESE, 1);
            req.put(Ingredient.PICKLE, 1);
            return req;
        }

        @Override public String getSaveToken() { return "BURGER"; }
    }

    public static class ChickenStrips extends HotFood {
        public ChickenStrips() { super("Chicken Strips", 7.00); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.CHICKEN_STRIP, 3);
            return req;
        }

        @Override public String getSaveToken() { return "CHICKEN_STRIPS"; }
    }

    public static class Twister extends HotFood {
        public Twister() { super("Twister Wrap", 9.00); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.LAVASH, 1);
            req.put(Ingredient.CHICKEN_STRIP, 2);
            req.put(Ingredient.LETTUCE, 1);
            req.put(Ingredient.TOMATO, 1);
            req.put(Ingredient.MAYO, 1);
            return req;
        }

        @Override public String getSaveToken() { return "TWISTER"; }
    }

    public static class Fries extends HotFood {
        public Fries() { super("Fries", 4.00); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.POTATO, 2);
            req.put(Ingredient.OIL, 1);
            return req;
        }

        @Override public String getSaveToken() { return "FRIES"; }
    }
}
