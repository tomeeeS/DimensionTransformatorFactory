package com.company;

import java.util.function.Function;

/**
 * @author Sajti Tamás
 */
public class Robot implements Runnable {
    private String id;
    private Phase currentPhase = Phase.getFirst();
    private Function< Product.ProductType, Integer > recipe;

    public Robot( Function<  Product.ProductType, Integer > recipe ) {
        this.recipe = recipe;
    }

    @Override
    public void run() {

    }

    public void whatsup() {

    }
}
