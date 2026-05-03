package model;

public enum Ingredient {

    BUN             (2.50),
    BEEF_PATTY      (8.00),
    CHICKEN_STRIP   (6.50),
    LETTUCE         (1.50),
    TOMATO          (2.00),
    CHEESE          (3.50),
    PICKLE          (1.50),
    LAVASH          (2.00),

    POTATO          (1.50),
    OIL             (3.00),

    KETCHUP         (1.50),
    MAYO            (1.50),
    BARBECUE        (2.00),

    HEAVY_CREAM     (3.50),
    DARK_CHOCOLATE  (4.50),
    BUTTER          (2.50),
    SUGAR           (1.00),

    COLA_SYRUP      (3.00),
    COFFEE_BEAN     (5.50),
    TEA_LEAF        (2.50),
    WATER           (0.50),
    MILK            (2.00),
    SPRITE_SYRUP    (3.00);

    private final double restockCost;

    Ingredient(double restockCost) {
        this.restockCost = restockCost;
    }

    //cost of ingredients for 10 item
    public double getRestockCost() {
        return restockCost;
    }
}