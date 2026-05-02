package com.siliconSpatula.model;

import com.siliconSpatula.model.menu.*;

/**
 * Factory that maps save-file tokens → concrete MenuItem instances.
 * Used during file load to reconstruct the correct subclass.
 * Add a new case here whenever a new MenuItem subclass is added.
 */
public class MenuItemFactory {

    /**
     * Creates a fresh MenuItem from its save token.
     * @throws IllegalArgumentException if the token is unknown.
     */
    public static MenuItem fromToken(String token) {
        switch (token.toUpperCase()) {
            case "BURGER":         return new HotFood.Burger();
            case "CHICKEN_STRIPS": return new HotFood.ChickenStrips();
            case "TWISTER":        return new HotFood.Twister();
            case "FRIES":          return new HotFood.Fries();
            case "MUFFIN_CAKE":    return new Dessert.MuffinCake();
            case "CHEESECAKE":     return new Dessert.Cheesecake();
            case "COFFEE":         return new Beverage.Coffee();
            case "TEA":            return new Beverage.Tea();
            case "COLA":           return new Beverage.Cola();
            case "WATER":          return new Beverage.Water();
            case "KETCHUP_SAUCE":  return new Sauce.KetchupSauce();
            case "MAYO_SAUCE":     return new Sauce.MayoSauce();
            case "BARBECUE_SAUCE": return new Sauce.BarbecueSauce();
            default:
                throw new IllegalArgumentException("Unknown menu item token: " + token);
        }
    }

    /** Returns all possible orderable items (used for random order generation). */
    public static MenuItem[] allItems() {
        return new MenuItem[]{
            new HotFood.Burger(),
            new HotFood.ChickenStrips(),
            new HotFood.Twister(),
            new HotFood.Fries(),
            new Dessert.MuffinCake(),
            new Dessert.Cheesecake(),
            new Beverage.Coffee(),
            new Beverage.Tea(),
            new Beverage.Cola(),
            new Beverage.Water(),
            new Sauce.KetchupSauce(),
            new Sauce.MayoSauce(),
            new Sauce.BarbecueSauce()
        };
    }
}
