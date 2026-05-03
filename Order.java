package com.siliconSpatula.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private final int id;

    private final Map<MenuItem, Integer> items;

    public Order(Map<MenuItem, Integer> items) {
        this.id    = COUNTER.getAndIncrement();
        this.items = new LinkedHashMap<>(items);
    }

    public Order(int id, Map<MenuItem, Integer> items) {
        this.id    = id;
        this.items = new LinkedHashMap<>(items);
    }

    public int getId() { return id; }

    public Map<MenuItem, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    //total ingredients for order (inventory deduction)
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

    public double getTotalPrice() {
        double sum = 0;
        for (Map.Entry<MenuItem, Integer> e : items.entrySet()) {
            sum += e.getKey().getPrice() * e.getValue();
        }
        return sum;
    }

    //if loading from file, reset the counter to the next available ID
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
