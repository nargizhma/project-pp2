package com.siliconSpatula.fileio;

import com.siliconSpatula.engine.RestaurantEngine;
import com.siliconSpatula.manager.InventoryManager;
import com.siliconSpatula.model.*;

import java.io.*;
import java.util.*;

/**
 * handles file operations for saving and loading feature.
 *
 * File format is like:
 *   # Silicon Spatula Save File
 *   MONEY,120.00
 *   INGREDIENT,BUN,10
 *   INGREDIENT,BEEF_PATTY,5
 *   ...
 *   ORDER,1,BURGER:2;FRIES:3
 *   ORDER,2,COLA:1;WATER:1
 *
 * The ORDER line contains:  id , token:qty ; token:qty ; ...
 */
public class FileManager {

    private static final String FILE_PATH = "silicon_spatula_save.txt";

    

    public static void saveState(RestaurantEngine engine) throws IOException {
        InventoryManager inv = engine.getInventory();
        // safely write using try-with-resources to ensure file is closed properly
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE_PATH))) {
            w.write("# Silicon Spatula Save File"); // for identification
            w.newLine();

            w.write(String.format("MONEY,%.2f", inv.getCash()));
            w.newLine();

            for (Ingredient ing : Ingredient.values()) {
                w.write("INGREDIENT," + ing.name() + "," + inv.getQuantity(ing)); // adding ingredients and their quantities to the file
                w.newLine();
            }

            // building format: ORDER,id,token:qty;token:qty;...
            for (Order order : engine.getOrderQueue()) { // iterating through the order queue and writing each order to the file
                StringBuilder sb = new StringBuilder("ORDER,");
                sb.append(order.getId()).append(",");
                StringJoiner sj = new StringJoiner(";"); // using stringjoiner for joining multiple items in the order with a semicolon
                for (Map.Entry<MenuItem, Integer> e : order.getItems().entrySet()) {
                    sj.add(e.getKey().getSaveToken() + ":" + e.getValue());
                }
                sb.append(sj); // appending the joined string of items to the order line
                w.write(sb.toString()); //
                w.newLine();
            }
        }
    }

    

    public static void loadState(RestaurantEngine engine) throws IOException {
        InventoryManager inv = engine.getInventory();
        Queue<Order>     q   = engine.getOrderQueue();

        q.clear();
        int maxId = 0; // used for resuming order ID 

        try (BufferedReader r = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",", 3);
                switch (parts[0].toUpperCase()) {

                    case "MONEY":
                        inv.setCash(Double.parseDouble(parts[1]));
                        break;

                    case "INGREDIENT":
                        Ingredient ing = Ingredient.valueOf(parts[1]);
                        inv.setQuantity(ing, Integer.parseInt(parts[2]));
                        break;

                    case "ORDER": {
                        int id = Integer.parseInt(parts[1]);
                        // parts[2] = "BURGER:2;FRIES:3"
                        Map<MenuItem, Integer> items = new LinkedHashMap<>();
                        for (String segment : parts[2].split(";")) {
                            String[] kv = segment.split(":", 2);
                            MenuItem item = MenuItemFactory.fromToken(kv[0].trim());
                            int qty = Integer.parseInt(kv[1].trim());
                            items.put(item, qty); // order items are stored in a linked hash map to preserve the order
                        }
                        q.offer(new Order(id, items));
                        if (id > maxId) maxId = id; //resume order ID counter after loading is max ID seen in the file
                        break;
                    }

                    default:
                        throw new IllegalArgumentException("Unknown save line: " + line); // if the line doesn't match any known type, throw an exception
                }
            }
        }

        Order.resetCounter(maxId + 1); // prevent ID collisions
    }
}
