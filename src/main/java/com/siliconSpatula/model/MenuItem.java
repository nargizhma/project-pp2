package com.siliconSpatula.model;

import java.util.Map;

/**
 * Abstract base class for every item on The Silicon Spatula menu.
 *
 * Subclass hierarchy:
 * MenuItem
 * ├── HotFood
 * │ ├── Burger, ChickenStrips, Wrap, Fries
 * ├── Dessert
 * │ ├── MuffinCake, Cheesecake
 * ├── Beverage
 * │ ├── HotDrink (Coffee, Tea)
 * │ └── ColdDrink (Cola, Lemonade, Water)
 * └── Sauce
 * ├── KetchupSauce, MayoSauce, BarbecueSauce
 */
public abstract class MenuItem {

    protected String name;
    protected double price; // cash reward when successfully cooked
    protected int prepTimeSecs; // cooking animation delay in seconds
    protected ApplianceType applianceType; // which appliance handles this item

    public MenuItem(String name, double price, int prepTimeSecs, ApplianceType applianceType) {
        this.name = name;
        this.price = price;
        this.prepTimeSecs = prepTimeSecs;
        this.applianceType = applianceType;
    }

    // ── Abstract methods that every subclass must implement ────────────────

    /**
     * Returns the ingredients and quantities required to prepare this item.
     * e.g., { Ingredient.BUN → 1, Ingredient.BEEF_PATTY → 1 }
     */
    public abstract Map<Ingredient, Integer> getRequiredIngredients();

    /**
     * Returns the save-file token used to reconstruct this exact item
     * on load (e.g., "BURGER", "COLA"). Must be unique per subclass.
     */
    public abstract String getSaveToken();

    // ── Getters ────────────────────────────────────────────────────────────

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getPrepTimeSecs() {
        return prepTimeSecs;
    }

    public ApplianceType getApplianceType() {
        return applianceType;
    }

    @Override
    public String toString() {
        return name;
    }
}
