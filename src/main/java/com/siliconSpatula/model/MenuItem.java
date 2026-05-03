package com.siliconSpatula.model;
import java.util.Map;
public abstract class MenuItem {

    protected String name;
    protected double price; 
    protected int prepTimeSecs; 
    protected ApplianceType applianceType; 

    public MenuItem(String name, double price, int prepTimeSecs, ApplianceType applianceType) {
        this.name = name;
        this.price = price;
        this.prepTimeSecs = prepTimeSecs;
        this.applianceType = applianceType;
    }

    public abstract Map<Ingredient, Integer> getRequiredIngredients();

    //unique naming for save/load files
    public abstract String getSaveToken();

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
