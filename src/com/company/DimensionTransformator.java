package com.company;

import java.util.List;

import javafx.util.Pair;

public class DimensionTransformator extends Product {

    private Pair< Integer, Integer > fuelAndDimensionBreaker;
    private List< DimensionBreaker > dimensionBreakers;

    public DimensionTransformator( String productName, int randomInt ) {
        super( productName );
        this.fuelAndDimensionBreaker = new Pair<>( randomInt, randomInt );
        productType = ProductType.DIMENSION_TRANSFORMATOR;
    }
}
