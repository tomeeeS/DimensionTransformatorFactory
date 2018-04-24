package com.company;

/**
 * Created by satsaat on 2018. 04. 17..
 */
public class Controller {
    private final int minCheckOnRobotsTimeMs;
    private final int maxCheckOnRobotsTimeMs;

    public Controller(int minCheckOnRobotsTimeMs, int maxCheckOnRobotsTimeMs) {
        this.minCheckOnRobotsTimeMs = minCheckOnRobotsTimeMs;
        this.maxCheckOnRobotsTimeMs = maxCheckOnRobotsTimeMs;
    }
}
