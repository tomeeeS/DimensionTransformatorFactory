package com.company;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private List< Map< Product.ProductType, Integer > > recipeData;

    public Controller( int minCheckOnRobotsTimeMs, int maxCheckOnRobotsTimeMs, List< Robot > robots ) {
        this.minCheckOnRobotsTimeMs = minCheckOnRobotsTimeMs;
        this.maxCheckOnRobotsTimeMs = maxCheckOnRobotsTimeMs;
        this.robots = robots;
    }

    @Override
    public void run() {
        try {
            while( !isDone() ) {
                Thread.sleep( getCheckOnRobotsTimeMs() );
                checkOnRobots();
            }
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public void setRecipeData( List< Map< Product.ProductType, Integer > > recipeData ) {
        this.recipeData = recipeData;
    }

    private boolean isDone() {
        boolean isDone = robots.isEmpty();
        if( isDone )
            System.out.printf( "Controller: I'm done, shutting down %n" );
        return isDone;
    }

    public Function< Product.ProductType, Integer > getRecipe( Phase phase ) {
        return ( Product.ProductType productType ) -> recipeData.get( phase.ordinal() ).getOrDefault( productType, 0 );
    }

    private void checkOnRobots() {
        List< Robot > doneRobots = new LinkedList<>();
        robots.forEach( robot -> {
            if( robot.isDone() )
                doneRobots.add( robot );
            else
                sortOutRobot( robot );
        } );
        robots.removeAll( doneRobots );
    }

    private void sortOutRobot( Robot robot ) {
        if( robot.isDoneInThisPhase() ) // it can't be in the last one
            setNextPhase( robot );
        else
            giveRobotResources( robot );
    }

    private void giveRobotResources( Robot robot ) {
        // we give the robot the same amount from each product
        int productCount = robot.getCurrentPhase().ordinal() + 1;
        Arrays.stream( Product.ProductType.values() )
                .forEach( productType -> robot.addProducts( ProductFactory.create( productType.ordinal(), productCount ) ) );
        System.out.printf( "Controller: giving robot %d resources: %d from everything %n", robot.getId(), productCount );
    }

    private void setNextPhase( Robot robot ) {
        Phase robotNextPhase = Phase.values()[ robot.getCurrentPhase().ordinal() + 1 ];
        System.out.printf( "Controller: setting robot %d's phase to %s %n", robot.getId(), robotNextPhase );
        robot.setNextPhase( robotNextPhase, getRecipe( robotNextPhase ) );
    }

    private int getCheckOnRobotsTimeMs() {
        return random.nextInt( maxCheckOnRobotsTimeMs - minCheckOnRobotsTimeMs + 1 ) + minCheckOnRobotsTimeMs;
    }
}
