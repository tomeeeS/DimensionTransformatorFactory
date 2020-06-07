package com.company;

import java.util.concurrent.CopyOnWriteArrayList;

import javafx.util.Pair;

/**
 * @author Sajti Tamás
 */
public enum Phase {

    ASSEMBLE_DIMENSION_BREAKER( 10, 18 ), // mirror, hammer
    BLEND_FUEL( 7, 9 ), // dark matter, free radicals
    INTEGRATE_DIMENSION_TRANSFORMATOR( 16, 21 ); // dimension breaker, transformator fuel

    private static CopyOnWriteArrayList< Pair< Product.ProductType, Integer > > phaseRequirements;
    private final int minRobotWorkingTimeMs;
    private final int maxRobotWorkingTimeMs;

    Phase( int minRobotWorkingTimeMs, int maxRobotWorkingTimeMs ) {
        this.minRobotWorkingTimeMs = minRobotWorkingTimeMs;
        this.maxRobotWorkingTimeMs = maxRobotWorkingTimeMs;
    }

    public static void setPhaseRequirements( CopyOnWriteArrayList< Pair< Product.ProductType, Integer > > phaseRequirements ) {
        Phase.phaseRequirements = phaseRequirements;
    }

    public Pair< Product.ProductType, Integer > getPhaseRequirement() {
        return phaseRequirements.get( ordinal() );
    }

    public int getMinRobotWorkingTimeMs() {
        return minRobotWorkingTimeMs;
    }

    public int getMaxRobotWorkingTimeMs() {
        return maxRobotWorkingTimeMs;
    }

    public static Phase getFirst() {
        return values()[ 0 ];
    }

    public boolean isLast() {
        return this == INTEGRATE_DIMENSION_TRANSFORMATOR;
    }
}
