package engine;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

import appliance.AirPot;
import appliance.DrinkDispenser;
import appliance.Grill;
import appliance.IAppliance;
import appliance.Oven;
import appliance.SauceDispenser;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import manager.InsufficientFundsException;
import manager.InsufficientIngredientsException;
import manager.InventoryManager;
import model.ApplianceType;
import model.Ingredient;
import model.MenuItem;
import model.MenuItemFactory;
import model.Order;

public class RestaurantEngine {

    private final Queue<Order> orderQueue = new ArrayDeque<>();
    private final InventoryManager inventory = new InventoryManager();
    private final List<IAppliance> appliances = new ArrayList<>();
    private final Random rng = new Random();

    //timeline
    private Timeline generatorTimeline;
    private boolean  running = false;

    // showing which appliances are currently busy (locked) by an active order
    private final Set<ApplianceType> busyAppliances = new HashSet<>();

    //calls ui
    private Consumer<String> logCallback;
    private Runnable refreshCallback;

    public RestaurantEngine() {
        appliances.add(new Grill());
        appliances.add(new AirPot());
        appliances.add(new Oven());
        appliances.add(new DrinkDispenser());
        appliances.add(new SauceDispenser());
    }


    public void setLogCallback(Consumer<String> cb){ 
        this.logCallback     = cb; 
    }

    public void setRefreshCallback(Runnable cb){ 
        this.refreshCallback = cb; 
    }


    public void startSimulation() {
        if (running) return;
        running = true;
        scheduleNext();
    }

    public void stopSimulation() {
        running = false;
        if (generatorTimeline != null) generatorTimeline.stop();
        log("Simulation stopped. Save game and close the application.");
    }

    //random generator
    private void scheduleNext() {
        if (!running) return;
        int delaySecs = 2 + rng.nextInt(4); // 2-5 seconds 
        generatorTimeline = new Timeline(
            new KeyFrame(Duration.seconds(delaySecs), e -> {
                generateRandomOrder();
                scheduleNext();
            })
        );
        generatorTimeline.setCycleCount(1);
        generatorTimeline.play();
    }


    private void generateRandomOrder() {
        MenuItem[] menu = MenuItemFactory.allItems();
        Map<MenuItem, Integer> orderItems = new LinkedHashMap<>();

        //number of distinct items in the order
        int distinctItems;
        int roll = rng.nextInt(10); // 0-9
        if (roll < 6) {
            //60%
            distinctItems = 1; 
        } else if (roll < 9) {
            //30%
            distinctItems = 2;   
        } else {
            //10%
            distinctItems = 3;   
        }

        List<MenuItem> shuffled = new ArrayList<>(Arrays.asList(menu));
        Collections.shuffle(shuffled, rng);
        for (int i = 0; i < Math.min(distinctItems, shuffled.size()); i++) {
            int qty =  (rng.nextInt(3) + 1) ; //1-3 for each item
            orderItems.put(shuffled.get(i), qty);
        }

        Order order = new Order(orderItems);
        orderQueue.offer(order);
        log("New Order: " + order);
        refresh();
    }


    //cook next order in queue (from ui button)
public void handleCookNextOrder() {
    Order order = orderQueue.poll();
    if (order == null) {
        log("No orders in queue.");
        return;
    }

    // group items by appliance type
    Map<ApplianceType, List<Map.Entry<MenuItem, Integer>>> byAppliance = new LinkedHashMap<>();
    for (Map.Entry<MenuItem, Integer> entry : order.getItems().entrySet()) {
        ApplianceType at = entry.getKey().getApplianceType();
        byAppliance.computeIfAbsent(at, k -> new ArrayList<>()).add(entry);
    }

    // check if any required appliance is already busy
    for (ApplianceType needed : byAppliance.keySet()) {
        if (busyAppliances.contains(needed)) {
            IAppliance ap = findAppliance(needed);
            String apName = ap != null ? ap.getApplianceName() : needed.name();
            log("Cannot start order " + order.getId()
                + " - " + apName + " is already busy. Try again shortly.");
            ((ArrayDeque<Order>) orderQueue).addFirst(order);
            return;
        }
    }

    // check ingredients
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

    // deduct ingredients
    try {
        inventory.consumeForMap(totalNeeded);
    } catch (InsufficientIngredientsException ex) {
        log("ERROR: " + ex.getMessage());
        refresh();
        return;
    }

    // lock appliances
    for (ApplianceType at : byAppliance.keySet()) {
        busyAppliances.add(at);
    }
    log("Preparing order " + order.getId() + ": " + describeItems(order));
    refresh();


    //based on how many different kind of appliances are needed it sets and loops on a counter to prepare the whole order at once
    int[] remaining = { byAppliance.size() };
    double totalPrice = order.getTotalPrice();

    for (Map.Entry<ApplianceType, List<Map.Entry<MenuItem, Integer>>> groupEntry
            : byAppliance.entrySet()) {

        ApplianceType at = groupEntry.getKey();
        IAppliance appliance = findAppliance(at);
        String apName = appliance != null ? appliance.getApplianceName() : at.name();

        // longest prep time in this appliance group
        int prepSecs = groupEntry.getValue().stream()
                .mapToInt(e -> e.getKey().getPrepTimeSecs())
                .max().orElse(1);

        // label for what this appliance is making
        StringJoiner itemLabel = new StringJoiner(", ");
        for (Map.Entry<MenuItem, Integer> e : groupEntry.getValue()) {
            itemLabel.add(e.getKey().getName() + (e.getValue() > 1 ? " x" + e.getValue() : ""));
        }
        final String finalItemLabel = itemLabel.toString();

        Timeline cookTimer = new Timeline(
            new KeyFrame(Duration.seconds(prepSecs), e -> {
                //appliance process item
                if (appliance != null) {
                    for (Map.Entry<MenuItem, Integer> itemEntry : groupEntry.getValue()) {
                        for (int i = 0; i < itemEntry.getValue(); i++) {
                            appliance.process(itemEntry.getKey());
                        }
                    }
                }

                log(apName + " finished: " + finalItemLabel);
                busyAppliances.remove(at);

                // when all appliance timers done complete the order as a whole
                remaining[0]--;
                if (remaining[0] == 0) {
                    inventory.addCash(totalPrice);
                    log("Order #" + order.getId() + " complete! ["
                        + describeItems(order) + "]  +"
                        + String.format("$%.2f", totalPrice)
                        + " | Cash: $" + String.format("%.2f", inventory.getCash()));
                }

                refresh();
            })
        );
        cookTimer.setCycleCount(1);
        cookTimer.play();
    }
}

    //restocks selected ingredient (from ui button)
    public void handleRestock(Ingredient ingredient) {
        try {
            inventory.restock(ingredient);
            log("Restocked " + InventoryManager.RESTOCK_AMOUNT + "x "
                + ingredient.name().replace("_", " ")
                + "  (-$" + String.format("%.2f", ingredient.getRestockCost() * 10) + ")");
        } catch (InsufficientFundsException ex) {
            log("ERROR: Cannot restock - " + ex.getMessage());
        } catch (InsufficientIngredientsException ex) {
            log("ERROR: " + ex.getMessage());
        }
        refresh();
    }

    //helper methods
    private IAppliance findAppliance(ApplianceType type) {
        for (IAppliance a : appliances) {
            //instanceof checks if the appliance matches the type we are looking for
            if (a instanceof Grill && type == ApplianceType.GRILL) return a;
            if (a instanceof AirPot && type == ApplianceType.AIRPOT) return a;
            if (a instanceof Oven && type == ApplianceType.OVEN) return a;
            if (a instanceof DrinkDispenser && type == ApplianceType.DRINK_DISPENSER) return a;
            if (a instanceof SauceDispenser && type == ApplianceType.SAUCE_DISPENSER) return a;
        }
        return null;
    }

    //description of the order's items for logging
    private String describeItems(Order order) {
        StringJoiner sj = new StringJoiner(", ");
        for (Map.Entry<MenuItem, Integer> e : order.getItems().entrySet()) {
            sj.add(e.getKey().getName() + (e.getValue() > 1 ? " x" + e.getValue() : ""));
        }
        return sj.toString();
    }


    public Queue<Order> getOrderQueue() {
        return orderQueue; 
    
    }
    public InventoryManager getInventory() { 
        return inventory; 
    }

    public boolean isRunning() { 
        return running; 
    }

    public Set<ApplianceType> getBusyAppliances() { 
        return Collections.unmodifiableSet(busyAppliances); 
    }

    //forwards text to ui log area
    private void log(String msg) {
        if (logCallback != null) logCallback.accept(msg);
    }

    // tells ui to refresh all data at any state change
    private void refresh() {
        if (refreshCallback != null) refreshCallback.run();
    }
}