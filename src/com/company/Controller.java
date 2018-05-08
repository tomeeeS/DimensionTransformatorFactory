package com.company;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static com.company.Product.ProductType.*;

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

    public Function< Phase, HashMap< Product.ProductType, Integer > > getRecipes() {
        return ( Phase p ) -> {
            HashMap< Product.ProductType, Integer > map = new HashMap<>();
            switch ( p ) {
                case ASSEMBLE_ACCELERATOR:
                default:
                    map.put( ATOMIC_ACCELERATOR, 1 );
                    map.put( ELECTRICITY, 7 );
                    break;
                case ASSEMBLE_DIMENSION_BREAKER:
                    map.put( MIRROR, 4 );
                    map.put( HAMMER, 2 );
                    break;
                case BLEND_FUEL:
                    map.put( DARK_MATTER, 10 );
                    map.put( FREE_RADICAL, 7000 );
                    break;
            }
            return map;
        };
    }
}
