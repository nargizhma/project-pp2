package com.siliconSpatula.engine;

import com.siliconSpatula.appliance.*;
import com.siliconSpatula.manager.InsufficientIngredientsException;
import com.siliconSpatula.manager.InventoryManager;
import com.siliconSpatula.model.*;
import com.siliconSpatula.model.menu.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Consumer;

/**
 * Core simulation engine for The Silicon Spatula.
 *
 * Key improvements:
 *  - Orders are multi-item (Map<MenuItem, Integer>)
 *  - Per-appliance cooking lock: different-appliance orders run in parallel
 *  - Error messages correctly list what is missing for each item
 *  - Random order generator sometimes builds combo orders (2-3 items)
 */
public class RestaurantEngine {

    // ── State ──────────────────────────────────────────────────────────────
    private final Queue<Order>     orderQueue = new ArrayDeque<>();
    private final InventoryManager inventory  = new InventoryManager();
    private final List<IAppliance> appliances = new ArrayList<>();
    private final Random           rng        = new Random();

    // ── JavaFX Timeline ────────────────────────────────────────────────────
    private Timeline generatorTimeline;
    private boolean  running = false;

    // ── Per-appliance busy tracking ────────────────────────────────────────
    // Instead of one boolean, we track which ApplianceType is currently busy.
    // Orders that require a free appliance can be cooked concurrently.
    private final Set<ApplianceType> busyAppliances = new HashSet<>();

    // ── Callbacks to UI ───────────────────────────────────────────────────
    private Consumer<String> logCallback;
    private Runnable         refreshCallback;

    public RestaurantEngine() {
        appliances.add(new Grill());
        appliances.add(new AirPot());
        appliances.add(new DrinkDispenser());
        appliances.add(new SauceDispenser());
    }

    // ── Callback setters ───────────────────────────────────────────────────

    public void setLogCallback(Consumer<String> cb)  { this.logCallback     = cb; }
    public void setRefreshCallback(Runnable cb)       { this.refreshCallback = cb; }

    // ── Timeline control ───────────────────────────────────────────────────

    public void startSimulation() {
        if (running) return;
        running = true;
        scheduleNext();
    }

    public void stopSimulation() {
        running = false;
        if (generatorTimeline != null) generatorTimeline.stop();
        log("Simulation stopped. Safe to close.");
    }

    private void scheduleNext() {
        if (!running) return;
        int delaySecs = 2 + rng.nextInt(4); // 2-5 seconds randomly
        generatorTimeline = new Timeline(
            new KeyFrame(Duration.seconds(delaySecs), e -> {
                generateRandomOrder();
                scheduleNext();
            })
        );
        generatorTimeline.setCycleCount(1);
        generatorTimeline.play();
    }

    /**
     * Generates a random order. 40% chance it is a combo order with
     * 2-3 items (with quantities 1-3 per item).
     */
    private void generateRandomOrder() {
        MenuItem[] menu = MenuItemFactory.allItems();
        Map<MenuItem, Integer> orderItems = new LinkedHashMap<>();

        // Decide how many distinct items in this order (1, 2, or 3)
        int distinctItems;
        int roll = rng.nextInt(10); // 0-9
        if (roll < 7) {
            distinctItems = 1;      // 70% - single item
        } else if (roll < 9) {
            distinctItems = 2;      // 20% - two-item combo
        } else {
            distinctItems = 3;      // 10% - three-item combo
        }

        // Pick distinct items (no duplicates in one order)
        List<MenuItem> shuffled = new ArrayList<>(Arrays.asList(menu));
        Collections.shuffle(shuffled, rng);
        for (int i = 0; i < Math.min(distinctItems, shuffled.size()); i++) {
            int qty = rng.nextInt(3) + 1; 
            orderItems.put(shuffled.get(i), qty);
        }

        Order order = new Order(orderItems);
        orderQueue.offer(order);
        log("New Order: " + order);
        refresh();
    }

    // ── Order processing ───────────────────────────────────────────────────

    /**
     * Called when "Cook Next Order" is clicked.
     *
     * For each distinct appliance needed by the order, we check if that
     * appliance is free.  If ANY required appliance is busy, we block.
     * If all appliances are free, we start parallel cook timers - one
     * per appliance group - and release each lock independently when done.
     */
    public void handleCookNextOrder() {
        Order order = orderQueue.poll();
        if (order == null) {
            log("No orders in queue.");
            return;
        }

        // ── 1. Check what appliances this order needs ──────────────────
        // Group items by which appliance they use
        Map<ApplianceType, List<Map.Entry<MenuItem, Integer>>> byAppliance = new LinkedHashMap<>();
        for (Map.Entry<MenuItem, Integer> entry : order.getItems().entrySet()) {
            ApplianceType at = entry.getKey().getApplianceType();
            byAppliance.computeIfAbsent(at, k -> new ArrayList<>()).add(entry);
        }

        // ── 2. Check if any required appliance is already busy ─────────
        for (ApplianceType needed : byAppliance.keySet()) {
            if (busyAppliances.contains(needed)) {
                IAppliance ap = findAppliance(needed);
                String apName = ap != null ? ap.getApplianceName() : needed.name();
                log("Cannot start order " + order.getId()
                    + " - " + apName + " is already busy. Try again shortly.");
                // Put the order back at the front of the queue
                ((ArrayDeque<Order>) orderQueue).addFirst(order);
                return;
            }
        }

        // ── 3. Check ingredients (all-or-nothing) ─────────────────────
        Map<Ingredient, Integer> totalNeeded = order.getTotalRequiredIngredients();
        if (!inventory.hasEnoughForMap(totalNeeded)) {
            StringBuilder missing = new StringBuilder();
            for (Map.Entry<Ingredient, Integer> e : totalNeeded.entrySet()) {
                int have = inventory.getQuantity(e.getKey());
                if (have < e.getValue()) {
                    missing.append(e.getKey().name().replace("_", " "))
                           .append(" (need ").append(e.getValue())
                           .append(", have ").append(have).append(")  ");
                }
            }
            log("ERROR: Cannot prepare order " + order.getId()
                + " [" + describeItems(order) + "] - Missing: " + missing.toString().trim());
            refresh();
            return;
        }

        // ── 4. Deduct ingredients immediately (reserved for this order) ─
        try {
            inventory.consumeForMap(totalNeeded);
        } catch (InsufficientIngredientsException ex) {
            log("ERROR: " + ex.getMessage());
            refresh();
            return;
        }

        // ── 5. Lock the needed appliances and start one timer per appliance
        for (ApplianceType at : byAppliance.keySet()) {
            busyAppliances.add(at);
        }
        log("Preparing order " + order.getId() + ": " + describeItems(order));
        refresh();

        // For each appliance group, start its own cook timer
        for (Map.Entry<ApplianceType, List<Map.Entry<MenuItem, Integer>>> groupEntry
                : byAppliance.entrySet()) {

            ApplianceType at          = groupEntry.getKey();
            IAppliance    appliance   = findAppliance(at);
            String        apName      = appliance != null ? appliance.getApplianceName() : at.name();

            // Longest prep time in this appliance group
            int prepSecs = groupEntry.getValue().stream()
                    .mapToInt(e -> e.getKey().getPrepTimeSecs())
                    .max().orElse(2);

            // Label for log
            StringJoiner itemLabel = new StringJoiner(", ");
            double groupValue = 0;
            for (Map.Entry<MenuItem, Integer> e : groupEntry.getValue()) {
                String qtyStr = e.getValue() > 1 ? " x" + e.getValue() : "";
                itemLabel.add(e.getKey().getName() + qtyStr);
                groupValue += e.getKey().getPrice() * e.getValue();
            }
            final double finalGroupValue = groupValue;
            final String finalItemLabel  = itemLabel.toString();

            Timeline cookTimer = new Timeline(
                new KeyFrame(Duration.seconds(prepSecs), e -> {
                    // Appliance process call (polymorphic)
                    if (appliance != null) {
                        for (Map.Entry<MenuItem, Integer> itemEntry : groupEntry.getValue()) {
                            for (int i = 0; i < itemEntry.getValue(); i++) {
                                appliance.process(itemEntry.getKey());
                            }
                        }
                    }
                    inventory.addCash(finalGroupValue);
                    log("Delivered from " + apName + ": " + finalItemLabel
                        + "  (+" + String.format("$%.2f", finalGroupValue)
                        + " | Cash: $" + String.format("%.2f", inventory.getCash()) + ")");

                    busyAppliances.remove(at);
                    refresh();
                })
            );
            cookTimer.setCycleCount(1);
            cookTimer.play();
        }
    }

    /** Restocks a selected ingredient. */
    public void handleRestock(Ingredient ingredient) {
        try {
            inventory.restock(ingredient);
            log("Restocked " + InventoryManager.RESTOCK_AMOUNT + "x "
                + ingredient.name().replace("_", " ")
                + "  (-$" + String.format("%.2f", InventoryManager.RESTOCK_COST) + ")");
        } catch (InsufficientIngredientsException ex) {
            log("ERROR: " + ex.getMessage());
        }
        refresh();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private IAppliance findAppliance(ApplianceType type) {
        for (IAppliance a : appliances) {
            // Use canProcess with a temporary stand-in check via ApplianceType
            // We match by checking a dummy item - cleaner: just match by type directly
            // Since IAppliance.canProcess takes a MenuItem, we use the ApplianceType
            // stored in each appliance class via a helper method we add now.
            if (a instanceof Grill          && type == ApplianceType.GRILL)          return a;
            if (a instanceof AirPot         && type == ApplianceType.AIRPOT)         return a;
            if (a instanceof DrinkDispenser && type == ApplianceType.DRINK_DISPENSER) return a;
            if (a instanceof SauceDispenser && type == ApplianceType.SAUCE_DISPENSER) return a;
        }
        return null;
    }

    private String describeItems(Order order) {
        StringJoiner sj = new StringJoiner(", ");
        for (Map.Entry<MenuItem, Integer> e : order.getItems().entrySet()) {
            sj.add(e.getKey().getName() + (e.getValue() > 1 ? " x" + e.getValue() : ""));
        }
        return sj.toString();
    }

    // ── Accessors ─────────────────────────────────────────────────────────

    public Queue<Order>           getOrderQueue()     { return orderQueue; }
    public InventoryManager       getInventory()      { return inventory; }
    public boolean                isRunning()         { return running; }
    public Set<ApplianceType>     getBusyAppliances() { return Collections.unmodifiableSet(busyAppliances); }

    // ── Internal ──────────────────────────────────────────────────────────

    private void log(String msg) {
        if (logCallback != null) logCallback.accept(msg);
    }

    private void refresh() {
        if (refreshCallback != null) refreshCallback.run();
    }
}
