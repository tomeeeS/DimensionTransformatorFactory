package com.company;

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

    public Function< Phase, Recipe > getRecipes() {
        return ( Phase p ) -> {
            switch ( p ) {
                case ASSEMBLE_ACCELERATOR:
                default:
                    return new Recipe( new RecipeItem( ATOMIC_ACCELERATOR, 1 ), new RecipeItem( ELECTRICITY, 7 ) );
                case ASSEMBLE_DIMENSION_BREAKER:
                    return new Recipe( new RecipeItem( MIRROR, 4 ), new RecipeItem( HAMMER, 2 ) );
                case BLEND_FUEL:
                    return new Recipe( new RecipeItem( DARK_MATTER, 10 ), new RecipeItem( FREE_RADICAL, 7000 ) );
            }
        };
    }
}
