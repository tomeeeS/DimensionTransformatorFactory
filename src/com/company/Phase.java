package com.company;

/**
 * Created by satsaat on 2018. 04. 17..
 */
public enum Phase {

    ASSEMBLE_TRANSFORMATOR( 1, 1, 1, 1 ),
    ASSEMBLE_DIMENSION_BREAKER( 1, 1, 1, 1 ),
    BLEND_FUEL( 1, 1, 1, 1 );

    private final int minRobotWorkingTimeMs;
    private final int maxRobotWorkingTimeMs;
    private final int minControllerCheckTimeMs;
    private final int maxControllerCheckTimeMs;

    Phase(int minRobotWorkingTimeMs, int maxRobotWorkingTimeMs, int minControllerCheckTimeMs, int maxControllerCheckTimeMs) {
        this.minRobotWorkingTimeMs = minRobotWorkingTimeMs;
        this.maxRobotWorkingTimeMs = maxRobotWorkingTimeMs;
        this.minControllerCheckTimeMs = minControllerCheckTimeMs;
        this.maxControllerCheckTimeMs = maxControllerCheckTimeMs;
    }

    public int getMinRobotWorkingTimeMs() {
        return minRobotWorkingTimeMs;
    }

    public int getMaxRobotWorkingTimeMs() {
        return maxRobotWorkingTimeMs;
    }

    public int getMinControllerCheckTimeMs() {
        return minControllerCheckTimeMs;
    }

    public int getMaxControllerCheckTimeMs() {
        return maxControllerCheckTimeMs;
    }
}
