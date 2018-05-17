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

    private final int minCheckOnRobotsTimeMs;
    private final int maxCheckOnRobotsTimeMs;
    private final List< Robot > robots;
    private final Object recipesLock = new Object();
    // the returned pair's first element is the required count of products of that type, the second is the produced count
    private final Function< Integer, Function< Phase, Function< Product.ProductType, Pair< Integer, Integer > > > > recipes =
            robotId -> phase -> productType -> {
                synchronized( recipesLock ) {
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
                                    return new Pair<>( 6, 0 );
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
                                    return new Pair<>( 11, 0 );
                            }
                            break;
                    }
                    return new Pair<>( 0, 0 );
                }
            };
    private Random random = new Random();

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

    public Function< Product.ProductType, Pair< Integer, Integer > > getRecipe( Robot robot, Phase phase ) {
        return recipes.apply( robot.getId() ).apply( phase );
    }

    public boolean isDoneInPhase( Robot robot ) {
        return robot.isPhaseRequirementSatisfied();
    }

    public boolean isDone( Robot robot ) {
        return isDoneInPhase( robot ) && robot.getCurrentPhase().isLast();
    }

    private boolean isDone() {
        boolean isDone = robots.isEmpty();
        if( isDone )
            System.out.printf( "Controller: I'm done, shutting down %n" );
        return isDone;
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
        int productCount = robot.getCurrentPhase().ordinal() + 1;
        Arrays.stream( Product.ProductType.values() )
                .forEach( productType -> {
                    int productCountGiven = recipes.apply( robot.getId() ).apply( robot.getCurrentPhase() ).apply( productType ).getKey() > 0 ? productCount : 0;
                    robot.addProducts( ProductFactory.create( productType.ordinal(), productCountGiven ) );
                    if( productCountGiven > 0 )
                        System.out.printf( "Controller: giving robot %d resources: %d %s%n", robot.getId(), productCount, productType );
                } );
    }

    private void setNextPhase( Robot robot ) {
        Phase robotNextPhase = Phase.values()[ robot.getCurrentPhase().ordinal() + 1 ];
        System.out.printf( "Controller: setting robot %d's phase to %s %n", robot.getId(), robotNextPhase );
        robot.setNextPhase( robotNextPhase, getRecipe( robot, robotNextPhase ) );
    }

    private int getCheckOnRobotsTimeMs() {
        return random.nextInt( maxCheckOnRobotsTimeMs - minCheckOnRobotsTimeMs + 1 ) + minCheckOnRobotsTimeMs;
    }
}
