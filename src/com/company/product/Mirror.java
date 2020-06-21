package com.company.product;

public class Mirror extends Product {
    private final char handleSymbol;

    public Mirror( String name, char handleSymbol ) {
        super( name );
        this.handleSymbol = handleSymbol;
        productType = ProductType.MIRROR;
    }
}
