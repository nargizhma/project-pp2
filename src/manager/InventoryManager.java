package manager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import model.Ingredient;
import model.MenuItem;

public class InventoryManager {

    public static final int    RESTOCK_AMOUNT = 10;
    public static final double STARTING_CASH  = 120.00;

    private final Map<Ingredient, Integer> inventory = new EnumMap<>(Ingredient.class);
    private double cash;

    //10 of each ingredient and 120 cash as a default start
    public InventoryManager() {
        cash = STARTING_CASH;
        for (Ingredient i : Ingredient.values()) {
            inventory.put(i, 10);
        }
    }

    //whether in stock there are enough ingredients for single menu item or not
    public boolean hasEnoughIngredients(MenuItem item) {
        return hasEnoughForMap(item.getRequiredIngredients());
    }

    //deducting ingredient count for single menu item
    public void consumeIngredients(MenuItem item) throws InsufficientIngredientsException {
        consumeForMap(item.getRequiredIngredients());
    }

    //whether it has enough ingredients for all menu items in the order or not (multi-item orders)
    public boolean hasEnoughForMap(Map<Ingredient, Integer> needed) {
        for (Map.Entry<Ingredient, Integer> e : needed.entrySet()) {
            if (inventory.getOrDefault(e.getKey(), 0) < e.getValue()) return false;
        }
        return true;
    }

    //deducting ingredient count for all menu items in the order (multi-item orders)
    //if one fails no ingredients are deducted
    public void consumeForMap(Map<Ingredient, Integer> needed)
            throws InsufficientIngredientsException {

        if (!hasEnoughForMap(needed)) {
            StringBuilder sb = new StringBuilder("Insufficient: ");
            for (Map.Entry<Ingredient, Integer> e : needed.entrySet()) {
                int have = inventory.getOrDefault(e.getKey(), 0);
                if (have < e.getValue()) {
                    sb.append(e.getKey().name().replace("_", " "))
                      .append(" (need ").append(e.getValue())
                      .append(", have ").append(have).append(")  ");
                }
            }
            throw new InsufficientIngredientsException(sb.toString().trim());
        }

        for (Map.Entry<Ingredient, Integer> e : needed.entrySet()) {
            inventory.merge(e.getKey(), -e.getValue(), Integer::sum);
        }
    }

    //restocking the ingredient and deducting the cost of restock from cash
    public void restock(Ingredient ingredient) throws InsufficientIngredientsException, InsufficientFundsException {
        double cost = ingredient.getRestockCost();
        if (cash < cost) {
            throw new InsufficientFundsException(cost, cash);
        }
        cash -= cost;
        inventory.merge(ingredient, RESTOCK_AMOUNT, Integer::sum);
    }

    public int getQuantity(Ingredient ingredient) {
        return Math.max(0, inventory.getOrDefault(ingredient, 0));
    }

    public Map<Ingredient, Integer> getInventorySnapshot() {
        return Collections.unmodifiableMap(inventory);
    }

    public double getCash()         { return cash; }
    public void addCash(double amt) { cash += amt; }
    public void setCash(double amt) { cash = amt; }

    //when loading from file, if data is corrupted and quantity is negative the quantity will be set to 0
    public void setQuantity(Ingredient ingredient, int qty) {
        inventory.put(ingredient, Math.max(0, qty));
    }
}