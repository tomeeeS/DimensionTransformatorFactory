package com.company.product;

import com.company.product.Product;

public class DimensionBreaker extends Product {

    private final byte code;

    public DimensionBreaker( String name, byte code ) {
        super( name );
        this.code = code;
        productType = ProductType.DIMENSION_BREAKER;
    }
}
