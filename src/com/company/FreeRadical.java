package com.company;

public class FreeRadical extends Product {
    private final boolean isFast;

    public FreeRadical( String name, boolean isFast ) {
        super( name );
        this.isFast = isFast;
        productType = ProductType.FREE_RADICAL;
    }
}
