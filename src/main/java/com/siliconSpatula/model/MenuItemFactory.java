package com.siliconSpatula.model;
import com.siliconSpatula.model.menu.Beverage;
import com.siliconSpatula.model.menu.Dessert;
import com.siliconSpatula.model.menu.HotFood;
import com.siliconSpatula.model.menu.Sauce;

public class MenuItemFactory {

    //gets the menu item from the token (from file, when loading)
    public static MenuItem fromToken(String token) {
        switch (token.toUpperCase()) {
            case "BURGER":         return new HotFood.Burger();
            case "CHICKEN_STRIPS": return new HotFood.ChickenStrips();
            case "TWISTER":        return new HotFood.Twister();
            case "FRIES":          return new HotFood.Fries();
            case "MUFFIN_CAKE":    return new Dessert.MuffinCake();
            case "CHEESECAKE":     return new Dessert.Cheesecake();
            case "LATTE":          return new Beverage.HotDrink.Latte();
            case "AMERICANO":      return new Beverage.HotDrink.Americano();
            case "TEA":            return new Beverage.HotDrink.Tea();
            case "COLA":           return new Beverage.ColdDrink.Cola();
            case "WATER":          return new Beverage.ColdDrink.Water();
            case "KETCHUP_SAUCE":  return new Sauce.KetchupSauce();
            case "MAYO_SAUCE":     return new Sauce.MayoSauce();
            case "BARBECUE_SAUCE": return new Sauce.BarbecueSauce();
            case "SPRITE":          return new Beverage.ColdDrink.Sprite();
            case "COLAZERO":        return new Beverage.ColdDrink.ColaZero();
            default:
                throw new IllegalArgumentException("Unknown menu item token: " + token);
        }
    }

    //all menu items as a list (random generation for orders)
    public static MenuItem[] allItems() {
        return new MenuItem[]{
            new HotFood.Burger(),
            new HotFood.ChickenStrips(),
            new HotFood.Twister(),
            new HotFood.Fries(),
            new Dessert.MuffinCake(),
            new Dessert.Cheesecake(),
            new Beverage.HotDrink.Latte(),
            new Beverage.HotDrink.Americano(),
            new Beverage.HotDrink.Tea(),
            new Beverage.ColdDrink.Cola(),
            new Beverage.ColdDrink.Water(),
            new Sauce.KetchupSauce(),
            new Sauce.MayoSauce(),
            new Sauce.BarbecueSauce(),
            new Beverage.ColdDrink.Sprite(),
            new Beverage.ColdDrink.ColaZero()
        };
    }
}
