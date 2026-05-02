package com.siliconSpatula.model.menu;

import com.siliconSpatula.model.ApplianceType;
import com.siliconSpatula.model.Ingredient;
import com.siliconSpatula.model.MenuItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract superclass for all beverages.
 * Uses the DrinkDispenser appliance. Preparation is 2 seconds (quick pour).
 */
public abstract class Beverage extends MenuItem {

    public Beverage(String name, double price) {
        super(name, price, 2, ApplianceType.DRINK_DISPENSER);
    }

    // ── Hot drinks ─────────────────────────────────────────────────────────

    /** Espresso-style coffee – COFFEE_BEAN + WATER + MILK */
    public static class Coffee extends Beverage {
        public Coffee() { super("Coffee", 3.50); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.COFFEE_BEAN, 1);
            req.put(Ingredient.WATER, 1);
            req.put(Ingredient.MILK, 1);
            return req;
        }

        @Override public String getSaveToken() { return "COFFEE"; }
    }

    /** Black or green tea – TEA_LEAF + WATER */
    public static class Tea extends Beverage {
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

    // ── Cold drinks ────────────────────────────────────────────────────────

    /** Cola – COLA_SYRUP + WATER */
    public static class Cola extends Beverage {
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

    /** Still / sparkling water – just WATER */
    public static class Water extends Beverage {
        public Water() { super("Water", 1.00); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.WATER, 2);
            return req;
        }

        @Override public String getSaveToken() { return "WATER"; }
    }
}
