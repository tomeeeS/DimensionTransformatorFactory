package com.company;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

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

    public Function< Product.ProductType, Integer > getRecipe( Phase phase ) {
        return ( Product.ProductType productType ) -> {
            switch( phase ) {
                case ASSEMBLE_ACCELERATOR:
                default:
                    switch( productType ) {
                        case ATOMIC_ACCELERATOR:
                            return 1;
                        case ELECTRICITY:
                            return 7;
                        default:
                            return 0;
                    }
                case ASSEMBLE_DIMENSION_BREAKER:
                    switch( productType ) {
                        case MIRROR:
                            return 4;
                        case HAMMER:
                            return 2;
                        default:
                            return 0;
                    }
                case BLEND_FUEL:
                    switch( productType ) {
                        case DARK_MATTER:
                            return 10;
                        case FREE_RADICAL:
                            return 7000;
                        default:
                            return 0;
                    }
            }
        };
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
