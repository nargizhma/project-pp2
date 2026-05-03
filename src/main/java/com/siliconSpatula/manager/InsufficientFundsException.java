package com.siliconSpatula.manager;

public class InsufficientFundsException extends Exception {

    private final double required;
    private final double available;

    public InsufficientFundsException(double required, double available) {
        super(String.format("Not enough cash. Need $%.2f, have $%.2f", required, available));
        this.required  = required;
        this.available = available;
    }

    public double getRequired()  { return required; }
    public double getAvailable() { return available; }
    
}
