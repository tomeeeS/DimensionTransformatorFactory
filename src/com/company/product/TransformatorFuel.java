package com.company.product;

public class TransformatorFuel extends Product {

    private final double volume;

    public TransformatorFuel( String name, double volume ) {
        super( name );
        this.volume = volume;
        productType = ProductType.TRANSFORMATOR_FUEL;
    }
}
