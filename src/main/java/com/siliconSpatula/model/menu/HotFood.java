package com.siliconSpatula.model.menu;

import com.siliconSpatula.model.ApplianceType;
import com.siliconSpatula.model.Ingredient;
import com.siliconSpatula.model.MenuItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract superclass for all hot-food items cooked on the Grill or AirPot.
 * HotFood items have a prepTimeSecs of 3 seconds (as required by brief).
 */
public abstract class HotFood extends MenuItem {

    public HotFood(String name, double price, ApplianceType applianceType) {
        super(name, price, 3, applianceType); // 3-second cook time for hot food
    }

    // ─────────────────────────────────────────────
    //  Concrete HotFood subclasses (inner classes)
    // ─────────────────────────────────────────────

    /** Classic beef burger – requires BUN + BEEF_PATTY + LETTUCE + TOMATO + CHEESE + PICKLE */
    public static class Burger extends HotFood {
        public Burger() { super("Burger", 8.50, ApplianceType.GRILL); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.BUN, 1);
            req.put(Ingredient.BEEF_PATTY, 1);
            req.put(Ingredient.LETTUCE, 1);
            req.put(Ingredient.TOMATO, 1);
            req.put(Ingredient.CHEESE, 1);
            req.put(Ingredient.PICKLE, 1);
            return req;
        }

        @Override public String getSaveToken() { return "BURGER"; }
    }

    /** Chicken strips – BUN + CHICKEN_STRIP + LETTUCE */
    public static class ChickenStrips extends HotFood {
        public ChickenStrips() { super("Chicken Strips", 7.00, ApplianceType.GRILL); }

        @Override
        public Map<Ingredient, Integer> getRequiredIngredients() {
            Map<Ingredient, Integer> req = new HashMap<>();
            req.put(Ingredient.CHICKEN_STRIP, 3);
            req.put(Ingredient.LETTUCE, 1);
            return req;
        }

        @Override public String getSaveToken() { return "CHICKEN_STRIPS"; }
    }

    /** KFC-style Twister wrap – LAVASH + CHICKEN_STRIP + LETTUCE + TOMATO + MAYO */
    public static class Twister extends HotFood {
        public Twister() { super("Twister Wrap", 9.00, ApplianceType.GRILL); }

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

    /** Fries – POTATO + OIL (cooked in AirPot) */
    public static class Fries extends HotFood {
        public Fries() { super("Fries", 4.00, ApplianceType.AIRPOT); }

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
