package com.company;

/**
 * @author Sajti Tamás
 */
public class Product {

    private String name;
    protected ProductType productType;

    public enum ProductType {
        DARK_MATTER,
        DIMENSION_BREAKER,
        DIMENSION_TRANSFORMATOR,
        FREE_RADICAL,
        TRANSFORMATOR_FUEL,
        HAMMER,
        MIRROR;
    }
    public Product( String name ) {
        this.name = name;
    }

    public ProductType getProductType() {
        return productType;
    }

    public boolean isOfProductType( ProductType productType ) {
        return this.productType == productType;
    }
}
