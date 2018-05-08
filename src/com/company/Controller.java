package com.company;

import java.util.List;
import java.util.Random;

/**
 * @author Sajti Tamás
 */
public class Controller implements Runnable {

    private final int minCheckOnRobotsTimeMs;
    private final int maxCheckOnRobotsTimeMs;
    private final List< Robot > robots;
    private Random random = new Random();

    public Controller( int minCheckOnRobotsTimeMs, int maxCheckOnRobotsTimeMs, List< Robot > robots ) {
        this.minCheckOnRobotsTimeMs = minCheckOnRobotsTimeMs;
        this.maxCheckOnRobotsTimeMs = maxCheckOnRobotsTimeMs;
        this.robots = robots;
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
        for( int i = 0; i < robots.size(); i++ ) {
            robots.get( i ).whatsup();
        }
    }

    private int getRandomSleepTime() {
        return random.nextInt( maxCheckOnRobotsTimeMs - minCheckOnRobotsTimeMs + 1 ) + minCheckOnRobotsTimeMs;
    }
}
