package model.menu;

import java.util.HashMap;
import java.util.Map;

import model.ApplianceType;
import model.Ingredient;
import model.MenuItem;

public abstract class Sauce extends MenuItem {

    public Sauce(String name, double price) {
        super(name, price, 1, ApplianceType.SAUCE_DISPENSER);
    }

    public static class KetchupSauce extends Sauce {
        public KetchupSauce() { super("Ketchup", 0.50); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.KETCHUP, 1);
            return req;
        }

        @Override public String getSaveToken() { return "KETCHUP_SAUCE"; }
    }

    public static class MayoSauce extends Sauce {
        public MayoSauce() { super("Mayo", 0.50); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.MAYO, 1);
            return req;
        }

        @Override public String getSaveToken() { return "MAYO_SAUCE"; }
    }

    public static class BarbecueSauce extends Sauce {
        public BarbecueSauce() { super("Barbecue Sauce", 0.75); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.BARBECUE, 1);
            return req;
        }

        @Override public String getSaveToken() { return "BARBECUE_SAUCE"; }
    }
}
