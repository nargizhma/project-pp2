package com.siliconSpatula.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A customer order that can contain multiple MenuItems with quantities.
 * e.g. Burger x2 + Fries x3 + Cola x1
 *
 * Items are stored in insertion order (LinkedHashMap) so display and
 * save/load are deterministic.
 */
public class Order {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private final int id;

    // MenuItem -> how many of that item were ordered
    private final Map<MenuItem, Integer> items;

    /** Normal constructor - auto-assigns next ID. */
    public Order(Map<MenuItem, Integer> items) {
        this.id    = COUNTER.getAndIncrement();
        this.items = new LinkedHashMap<>(items);
    }

    /** Load constructor - restores a specific ID from file. */
    public Order(int id, Map<MenuItem, Integer> items) {
        this.id    = id;
        this.items = new LinkedHashMap<>(items);
    }

    public int getId() { return id; }

    /** Unmodifiable view of item->qty map. */
    public Map<MenuItem, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Merges all per-item ingredient requirements scaled by quantity
     * into one combined map. Used by InventoryManager for check+consume.
     */
    public Map<Ingredient, Integer> getTotalRequiredIngredients() {
        Map<Ingredient, Integer> total = new LinkedHashMap<>();
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            int qty = entry.getValue();
            for (Map.Entry<Ingredient, Integer> ing
                    : entry.getKey().getRequiredIngredients().entrySet()) {
                total.merge(ing.getKey(), ing.getValue() * qty, Integer::sum);
            }
        }
        return total;
    }

    /** Total price: sum of all items * their quantities. */
    public double getTotalPrice() {
        double sum = 0;
        for (Map.Entry<MenuItem, Integer> e : items.entrySet()) {
            sum += e.getKey().getPrice() * e.getValue();
        }
        return sum;
    }

    /** Resets the auto-increment counter (call only on load). */
    public static void resetCounter(int next) {
        COUNTER.set(next);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" + ");
        for (Map.Entry<MenuItem, Integer> e : items.entrySet()) {
            String qty = e.getValue() > 1 ? " x" + e.getValue() : "";
            sj.add(e.getKey().getName() + qty);
        }
        return String.format("#%d  [%s]  ($%.2f)", id, sj, getTotalPrice());
    }
}
