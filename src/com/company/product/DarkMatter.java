package com.company.product;

import com.company.product.Product;

public class DarkMatter extends Product {
    private final float depth;

    public DarkMatter( String name, float depth ) {
        super( name );
        this.depth = depth;
        productType = ProductType.DARK_MATTER;
    }
}
