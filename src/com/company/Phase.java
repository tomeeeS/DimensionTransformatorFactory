package com.company;

import java.util.concurrent.CopyOnWriteArrayList;

import javafx.util.Pair;

/**
 * @author Sajti Tamás
 */
public enum Phase {

    ASSEMBLE_DIMENSION_BREAKER(), // mirror, hammer
    BLEND_FUEL( ), // dark matter, free radicals
    INTEGRATE_DIMENSION_TRANSFORMATOR(); // dimension breaker, transformator fuel

    private static CopyOnWriteArrayList< Pair< Product.ProductType, Integer > > phaseRequirements;

    public static void setPhaseRequirements( CopyOnWriteArrayList< Pair< Product.ProductType, Integer > > phaseRequirements ) {
        Phase.phaseRequirements = phaseRequirements;
    }

    public Pair< Product.ProductType, Integer > getPhaseRequirement() {
        return phaseRequirements.get( ordinal() );
    }

    public int getMinRobotWorkingTimeMs() {
        return 15;
    }

    public int getMaxRobotWorkingTimeMs() {
        return 30;
    }

    public static Phase getFirst() {
        return values()[ 0 ];
    }

    public boolean isLast() {
        return this == INTEGRATE_DIMENSION_TRANSFORMATOR;
    }
}
