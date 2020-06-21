package com.company.product;

import com.company.product.Product;

public class Hammer extends Product {
    private final int size;

    public Hammer( String name, int size ) {
        super( name );
        this.size = size;
        productType = ProductType.HAMMER;
    }
}
