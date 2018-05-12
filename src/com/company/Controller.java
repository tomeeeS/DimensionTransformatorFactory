package com.company;

import java.util.LinkedList;
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
            while( !isDone() ) {
                Thread.sleep( getCheckOnRobotsTimeMs() );
                checkOnRobots();
            }
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private boolean isDone() {
        boolean isDone = robots.isEmpty();
        if( isDone )
            System.out.printf( "Controller: I'm done and I'm shutting down %n" );
        return isDone;
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
                            return 3;
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
                            return 12;
                        default:
                            return 0;
                    }
            }
        };
    }

    private void checkOnRobots() {
        List< Robot > doneRobots = new LinkedList<>();
        for( Robot robot : robots )
            if( robot.isDone() )
                doneRobots.add( robot );
            else
                sortOutRobot( robot );
        robots.removeAll( doneRobots );
    }

    private void sortOutRobot( Robot robot ) {
        if( robot.isDoneInThisPhase() ) // it can't be in the last one
            setNextPhase( robot );
        else
            giveRobotResources( robot );
    }

    private void giveRobotResources( Robot robot ) {
        System.out.printf( "Controller: giving robot %d resources %n", robot.getId() );
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
