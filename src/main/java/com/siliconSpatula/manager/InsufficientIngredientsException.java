package com.siliconSpatula.manager;

/**
 * Thrown when an order cannot be cooked due to insufficient ingredients.
 * Caught by RestaurantEngine and logged to the System Log.
 */
public class InsufficientIngredientsException extends Exception {

    public InsufficientIngredientsException(String message) {
        super(message);
    }
}
