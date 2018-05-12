package com.company;

/**
 * @author Sajti Tamás
 */
public enum Phase {

    ASSEMBLE_ACCELERATOR( 10, 18 ), // electricity, atomic accelerator
    ASSEMBLE_DIMENSION_BREAKER( 8, 11 ), // mirror, hammer
    BLEND_FUEL( 7, 9 ); // dark matter, free radicals

    private final int minRobotWorkingTimeMs;
    private final int maxRobotWorkingTimeMs;

    Phase( int minRobotWorkingTimeMs, int maxRobotWorkingTimeMs ) {
        this.minRobotWorkingTimeMs = minRobotWorkingTimeMs;
        this.maxRobotWorkingTimeMs = maxRobotWorkingTimeMs;
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
}
