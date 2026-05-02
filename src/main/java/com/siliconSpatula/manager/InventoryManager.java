package com.siliconSpatula.manager;

import com.siliconSpatula.model.Ingredient;
import com.siliconSpatula.model.MenuItem;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Manages ingredient inventory via a strictly encapsulated
 * HashMap<Ingredient, Integer>.
 *
 * The JavaFX UI never touches the map directly.
 * RestaurantEngine calls the public API methods defined here.
 *
 * New in this version:
 *   - hasEnoughForMap / consumeForMap work on a pre-built combined
 *     ingredient map (used by multi-item orders in RestaurantEngine).
 */
public class InventoryManager {

    public static final int    RESTOCK_AMOUNT = 10;
    public static final double RESTOCK_COST   = 5.00;
    public static final double STARTING_CASH  = 120.00;

    private final Map<Ingredient, Integer> inventory = new EnumMap<>(Ingredient.class);
    private double cash;

    public InventoryManager() {
        cash = STARTING_CASH;
        for (Ingredient i : Ingredient.values()) {
            inventory.put(i, 10);
        }
    }

    // ── Single-item API (kept for compatibility) ───────────────────────────

    /** True if the single MenuItem's ingredients are all available. */
    public boolean hasEnoughIngredients(MenuItem item) {
        return hasEnoughForMap(item.getRequiredIngredients());
    }

    /**
     * Deducts the single MenuItem's ingredients.
     * @throws InsufficientIngredientsException on any shortage (nothing deducted).
     */
    public void consumeIngredients(MenuItem item) throws InsufficientIngredientsException {
        consumeForMap(item.getRequiredIngredients());
    }

    // ── Combined-map API (used by multi-item orders) ───────────────────────

    /**
     * True if every ingredient in the supplied map is available in
     * sufficient quantity.  Does NOT modify inventory.
     */
    public boolean hasEnoughForMap(Map<Ingredient, Integer> needed) {
        for (Map.Entry<Ingredient, Integer> e : needed.entrySet()) {
            if (inventory.getOrDefault(e.getKey(), 0) < e.getValue()) return false;
        }
        return true;
    }

    /**
     * Atomically deducts all ingredients in the supplied map.
     * If any ingredient is short, nothing is deducted and an exception is thrown.
     *
     * @throws InsufficientIngredientsException on shortage.
     */
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

    // ── Restock ────────────────────────────────────────────────────────────

    /**
     * Adds RESTOCK_AMOUNT of the given ingredient and deducts RESTOCK_COST cash.
     * @throws InsufficientIngredientsException if not enough cash.
     */
    public void restock(Ingredient ingredient) throws InsufficientIngredientsException {
        if (cash < RESTOCK_COST) {
            throw new InsufficientIngredientsException(
                String.format("Not enough cash to restock. Need $%.2f, have $%.2f",
                    RESTOCK_COST, cash));
        }
        cash -= RESTOCK_COST;
        inventory.merge(ingredient, RESTOCK_AMOUNT, Integer::sum);
    }

    // ── Queries ────────────────────────────────────────────────────────────

    public int getQuantity(Ingredient ingredient) {
        return Math.max(0, inventory.getOrDefault(ingredient, 0));
    }

    public Map<Ingredient, Integer> getInventorySnapshot() {
        return Collections.unmodifiableMap(inventory);
    }

    // ── Cash ───────────────────────────────────────────────────────────────

    public double getCash()         { return cash; }
    public void addCash(double amt) { cash += amt; }
    public void setCash(double amt) { cash = amt; }

    // ── File-load only ─────────────────────────────────────────────────────

    /** Used exclusively by FileManager to restore a saved quantity. */
    public void setQuantity(Ingredient ingredient, int qty) {
        inventory.put(ingredient, Math.max(0, qty));
    }
}
