package com.company;

/**
 * Created by satsaat on 2018. 04. 17..
 */
public enum Phase {

    ASSEMBLE_TRANSFORMATOR( 1, 1 ),
    ASSEMBLE_DIMENSION_BREAKER( 1, 1 ),
    BLEND_FUEL( 1, 1 );

    private final int minRobotWorkingTimeMs;
    private final int maxRobotWorkingTimeMs;

    Phase(int minRobotWorkingTimeMs, int maxRobotWorkingTimeMs) {
        this.minRobotWorkingTimeMs = minRobotWorkingTimeMs;
        this.maxRobotWorkingTimeMs = maxRobotWorkingTimeMs;
    }

    public int getMinRobotWorkingTimeMs() {
        return minRobotWorkingTimeMs;
    }

    public int getMaxRobotWorkingTimeMs() {
        return maxRobotWorkingTimeMs;
    }

}
