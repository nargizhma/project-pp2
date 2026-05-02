package com.siliconSpatula.model;

/**
 * All ingredients used across the restaurant menu.
 * Used as keys in InventoryManager's HashMap.
 */
public enum Ingredient {
    // Burger / Wrap ingredients
    BUN,
    BEEF_PATTY,
    CHICKEN_STRIP,
    LETTUCE,
    TOMATO,
    CHEESE,
    PICKLE,
    LAVASH,       // for wraps / twister

    // Fries / sides
    POTATO,
    OIL,

    // Sauce
    KETCHUP,
    MAYO,
    BARBECUE,

    // Dessert
    HEAVY_CREAM,
    DARK_CHOCOLATE,
    BUTTER,
    SUGAR,

    // Beverages
    COLA_SYRUP,
    COFFEE_BEAN,
    TEA_LEAF,
    WATER,
    MILK
}
