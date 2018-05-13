package com.company;

public class Electricity extends Product {
    private double voltage;

    public Electricity( String name, double voltage ) {
        super( name );
        this.voltage = voltage;
    }
}
