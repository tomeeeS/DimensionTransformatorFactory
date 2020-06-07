package com.company;

public class DimensionBreaker extends Product {

    private final byte code;

    public DimensionBreaker( String name, byte code ) {
        super( name );
        this.code = code;
        productType = ProductType.DIMENSION_BREAKER;
    }
}
