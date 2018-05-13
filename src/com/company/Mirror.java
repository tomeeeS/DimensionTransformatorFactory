package com.company;

public class Mirror extends Product {
    private char handleSymbol;

    public Mirror( String name, char handleSymbol ) {
        super( name );
        this.handleSymbol = handleSymbol;
        productType = ProductType.MIRROR;
    }
}
