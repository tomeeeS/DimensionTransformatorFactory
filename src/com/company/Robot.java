package com.company;

import java.util.function.Function;

/**
 * @author Sajti Tamás
 */
public class Robot implements Runnable {
    private String id;
    private Phase currentPhase = Phase.values()[ 0 ];
    private Function<Phase, Recipe> recipes;

    public Robot( Function<Phase, Recipe> recipes ) {
        this.recipes = recipes;
    }

    @Override
    public void run() {

    }

    public void whatsup() {

    }
}
