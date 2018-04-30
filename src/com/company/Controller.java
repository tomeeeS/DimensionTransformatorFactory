package com.company;

import java.util.Random;

/**
 * @author Sajti Tamás
 */
public class Controller implements Runnable {
    
    private final int minCheckOnRobotsTimeMs;
    private final int maxCheckOnRobotsTimeMs;
    private Random random = new Random();

    public Controller( int minCheckOnRobotsTimeMs, int maxCheckOnRobotsTimeMs ) {
        this.minCheckOnRobotsTimeMs = minCheckOnRobotsTimeMs;
        this.maxCheckOnRobotsTimeMs = maxCheckOnRobotsTimeMs;
    }

    @Override
    public void run() {
        try {
            Thread.sleep( getRandomSleepTime() );
            checkOnRobots();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private void checkOnRobots() {

    }

    private int getRandomSleepTime() {
        return random.nextInt( maxCheckOnRobotsTimeMs - minCheckOnRobotsTimeMs + 1 ) + minCheckOnRobotsTimeMs;
    }
}
