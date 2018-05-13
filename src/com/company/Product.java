package com.company;

/**
 * @author Sajti Tamás
 */
public class Product {
    private String name;
    protected ProductType productType;

    public enum ProductType {
        ATOMIC_ACCELERATOR,
        DARK_MATTER,
        ELECTRICITY,
        FREE_RADICAL,
        HAMMER,
        MIRROR
    }

    public Product( String name ) {
        this.name = name;
    }

    public boolean isOfProductType( ProductType productType ) {
        return this.productType == productType;
    }
}
