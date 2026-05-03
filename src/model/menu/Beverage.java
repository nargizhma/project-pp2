package model.menu;

import java.util.HashMap;
import java.util.Map;

import model.ApplianceType;
import model.Ingredient;
import model.MenuItem;
import model.menu.Beverage.HotDrink;

public abstract class Beverage extends MenuItem {

    public Beverage(String name, double price, int prepSecs, ApplianceType applianceType) {
        super(name, price, prepSecs, applianceType);
    }

    public abstract static class HotDrink extends Beverage {
        public HotDrink(String name, double price) {
            super(name, price, 3, ApplianceType.AIRPOT);
        }

        public static class Latte extends HotDrink {
            public Latte() { super("Latte", 4.00); }

            @Override
            public Map<Ingredient, Integer> getRequiredIngredients() {
                Map<Ingredient, Integer> req = new HashMap<>();
                req.put(Ingredient.COFFEE_BEAN, 1);
                req.put(Ingredient.WATER, 1);
                req.put(Ingredient.MILK, 1);
                return req;
            }

            @Override public String getSaveToken() { return "LATTE"; }
        }

        public static class Americano extends HotDrink {
            public Americano() { super("Americano", 3.50); }

            @Override
            public Map<Ingredient, Integer> getRequiredIngredients() {
                Map<Ingredient, Integer> req = new HashMap<>();
                req.put(Ingredient.COFFEE_BEAN, 1);
                req.put(Ingredient.WATER, 1);
                req.put(Ingredient.MILK, 1);
                req.put(Ingredient.SUGAR, 1);
                return req;
            }

            @Override public String getSaveToken() { return "AMERICANO"; }
        }

        public static class Tea extends HotDrink {
            public Tea() { super("Tea", 2.50); }

            @Override
            public Map<Ingredient, Integer> getRequiredIngredients() {
                Map<Ingredient, Integer> req = new HashMap<>();
                req.put(Ingredient.TEA_LEAF, 1);
                req.put(Ingredient.WATER, 1);
                return req;
            }

            @Override public String getSaveToken() { return "TEA"; }
        }
    }

    public abstract static class ColdDrink extends Beverage {
        public ColdDrink(String name, double price) {
            super(name, price, 2, ApplianceType.DRINK_DISPENSER);
        }

        public static class Cola extends ColdDrink {
            public Cola() { super("Cola", 2.00); }

            @Override
            public Map<Ingredient, Integer> getRequiredIngredients() {
                Map<Ingredient, Integer> req = new HashMap<>();
                req.put(Ingredient.COLA_SYRUP, 1);
                req.put(Ingredient.WATER, 1);
                return req;
            }

            @Override public String getSaveToken() { return "COLA"; }
        }

        public static class Water extends ColdDrink {
            public Water() { super("Water", 1.00); }

            @Override
            public Map<Ingredient, Integer> getRequiredIngredients() {
                Map<Ingredient, Integer> req = new HashMap<>();
                req.put(Ingredient.WATER, 1);
                return req;
            }

            @Override public String getSaveToken() { return "WATER"; }
        }

        public static class Sprite extends ColdDrink {
            public Sprite() { super("Sprite", 2.00); }

            @Override
            public Map<Ingredient, Integer> getRequiredIngredients() {
                Map<Ingredient, Integer> req = new HashMap<>();
                req.put(Ingredient.WATER, 1);
                req.put(Ingredient.SPRITE_SYRUP, 1);
                return req;
            }

            @Override public String getSaveToken() { return "SPRITE"; }
        }

        public static class ColaZero extends ColdDrink {
            public ColaZero() { super("Cola Zero", 2.00); }

            @Override
            public Map<Ingredient, Integer> getRequiredIngredients() {
                Map<Ingredient, Integer> req = new HashMap<>();
                req.put(Ingredient.WATER, 1);
                req.put(Ingredient.COLAZERO_SYRUP, 1);
                return req;
            }

            @Override public String getSaveToken() { return "COLAZERO"; }
        }
    }
}