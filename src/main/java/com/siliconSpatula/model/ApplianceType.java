package com.siliconSpatula.model;

/**
 * Declares which kitchen appliance is required to prepare a MenuItem.
 * Each concrete MenuItem subclass returns one of these values,
 * allowing IAppliance implementations to identify compatible items
 * via canProcess() without instanceof checks.
 */
public enum ApplianceType {
    GRILL,           // Grill – hot food (burgers, chicken strips, wraps)
    AIRPOT,          // AirPot – fries / potato-based sides
    DRINK_DISPENSER, // DrinkDispenser – cold/hot beverages
    SAUCE_DISPENSER  // SauceDispenser – sauce-only items (dipping sauces, extras)
}
