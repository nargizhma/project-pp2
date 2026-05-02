package com.siliconSpatula.model.menu;

import com.siliconSpatula.model.ApplianceType;
import com.siliconSpatula.model.Ingredient;
import com.siliconSpatula.model.MenuItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract superclass for dessert items.
 * Desserts are prepared in the AirPot and take 4 seconds.
 */
public abstract class Dessert extends MenuItem {

    public Dessert(String name, double price) {
        super(name, price, 4, ApplianceType.AIRPOT);
    }

    /** Chocolate muffin cake – HEAVY_CREAM + DARK_CHOCOLATE + BUTTER + SUGAR */
    public static class MuffinCake extends Dessert {
        public MuffinCake() { super("Muffin Cake", 5.50); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.HEAVY_CREAM, 1);
            req.put(Ingredient.DARK_CHOCOLATE, 1);
            req.put(Ingredient.BUTTER, 1);
            req.put(Ingredient.SUGAR, 1);
            return req;
        }

        @Override public String getSaveToken() { return "MUFFIN_CAKE"; }
    }

    /** Cheesecake – HEAVY_CREAM + BUTTER + SUGAR + CHEESE */
    public static class Cheesecake extends Dessert {
        public Cheesecake() { super("Cheesecake", 6.00); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.HEAVY_CREAM, 1);
            req.put(Ingredient.BUTTER, 1);
            req.put(Ingredient.SUGAR, 2);
            req.put(Ingredient.CHEESE, 1);
            return req;
        }

        @Override public String getSaveToken() { return "CHEESECAKE"; }
    }
}
