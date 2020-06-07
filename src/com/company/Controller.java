package com.company;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import javafx.util.Pair;

/**
 * @author Sajti Tamás
 */
public class Controller implements Runnable {

    public static final int CONTROLLER_MIN_CHECK_ON_ROBOTS_TIME_MS = 1;
    public static final int CONTROLLER_MAX_CHECK_ON_ROBOTS_TIME_MS = 2;

    private final List< Robot > robots;
    // the returned pair's first element is the required count of products of that type, the second is the produced count
    private final Function< Phase, Function< Product.ProductType, Pair< Integer, Integer > > > recipes =
            phase -> productType -> {
                synchronized( this ) {
                    switch( phase ) {
                        case ASSEMBLE_DIMENSION_BREAKER:
                        default:
                            switch( productType ) {
                                case DIMENSION_BREAKER:
                                    return new Pair<>( 0, 1 );
                                case HAMMER:
                                    return new Pair<>( 3, 0 );
                                case MIRROR:
                                    return new Pair<>( 3, 0 );
                            }
                            break;
                        case BLEND_FUEL:
                            switch( productType ) {
                                case TRANSFORMATOR_FUEL:
                                    return new Pair<>( 0, 1 );
                                case FREE_RADICAL:
                                    return new Pair<>( 5, 0 );
                                case DARK_MATTER:
                                    return new Pair<>( 6, 0 );
                            }
                            break;
                        case INTEGRATE_DIMENSION_TRANSFORMATOR:
                            switch( productType ) {
                                case DIMENSION_TRANSFORMATOR:
                                    return new Pair<>( 0, 1 );
                                case TRANSFORMATOR_FUEL:
                                    return new Pair<>( 11, 0 );
                                case DIMENSION_BREAKER:
                                    return new Pair<>( 10, 0 );
                            }
                            break;
                    }
                    return new Pair<>( 0, 0 );
                }
            };
    private final Random random = new Random();

    public Controller( List< Robot > robots ) {
        this.robots = robots;
    }

    @Override
    public void run() {
        try {
            while( !isDone() ) {
                Thread.sleep( getCheckOnRobotsTimeMs() );
                checkOnRobots();
            }
            System.out.printf( "Controller: I'm done, shutting down %n" );
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public Function< Product.ProductType, Pair< Integer, Integer > > getRecipe( Phase phase ) {
        return recipes.apply( phase );
    }

    public boolean isDoneInPhase( Robot robot ) {
        return robot.isPhaseRequirementSatisfied();
    }

    public boolean isDone( Robot robot ) {
        return isDoneInPhase( robot ) && robot.getCurrentPhase().isLast();
    }

    private boolean isDone() {
        return robots.isEmpty();
    }

    private void checkOnRobots() {
        List< Robot > doneRobots = new LinkedList<>();
        robots.forEach( robot -> {
            if( isDone( robot ) ) {
                doneRobots.add( robot );
                robot.setDone( true );
            } else
                sortOutRobot( robot );
        } );
        robots.removeAll( doneRobots );
    }

    private void sortOutRobot( Robot robot ) {
        if( isDoneInPhase( robot ) ) // it can't be in the last one
            setNextPhase( robot );
        else
            giveRobotResources( robot );
    }

    private void giveRobotResources( Robot robot ) {
        // we give the robot the same amount from each product
        int productAmount = robot.getCurrentPhase().ordinal() + 1;
        Arrays.stream( Product.ProductType.values() )
            .forEach( productType -> {
                int productAmountGiven = recipes.apply( robot.getCurrentPhase() ).apply( productType ).getKey() > 0 ? productAmount : 0;
                robot.addProducts( ProductFactory.create( productType.ordinal(), productAmountGiven ) );
                if( productAmountGiven > 0 )
                    System.out.printf( "Controller: %d %s -> robot %d%n", productAmount, productType, robot.getId() );
            } );
    }

    private void setNextPhase( Robot robot ) {
        Phase robotNextPhase = Phase.values()[ robot.getCurrentPhase().ordinal() + 1 ];
        robot.setNextPhase( robotNextPhase, getRecipe( robotNextPhase ) );
    }

    private int getCheckOnRobotsTimeMs() {
        return random.nextInt( CONTROLLER_MAX_CHECK_ON_ROBOTS_TIME_MS - CONTROLLER_MIN_CHECK_ON_ROBOTS_TIME_MS + 1 )
            + CONTROLLER_MIN_CHECK_ON_ROBOTS_TIME_MS;
    }
}
