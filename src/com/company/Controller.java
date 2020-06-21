package com.company;

import com.company.product.Product;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javafx.util.Pair;

/**
 * @author Sajti Tamás
 */
@SuppressWarnings("ALL")
public class Controller implements Runnable {

    public static final int ACTION_QUEUE_CAPACITY = 1000;
    public static final int FINAL_PHASE_NUMBER = 3;

    private int phaseNumber;
    private List< Robot > robots;
    // waiting queue for robots to get products
    private final BlockingQueue<Robot> actionQueue = new LinkedBlockingQueue<>( ACTION_QUEUE_CAPACITY );
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
                                    return new Pair<>( 2, 0 );
                            }
                            break;
                        case BLEND_FUEL:
                            switch( productType ) {
                                case TRANSFORMATOR_FUEL:
                                    return new Pair<>( 0, 1 );
                                case FREE_RADICAL:
                                    return new Pair<>( 5, 0 );
                                case DARK_MATTER:
                                    return new Pair<>( 4, 0 );
                            }
                            break;
                        case INTEGRATE_DIMENSION_TRANSFORMATOR:
                            switch( productType ) {
                                case DIMENSION_TRANSFORMATOR:
                                    return new Pair<>( 0, 1 );
                                case TRANSFORMATOR_FUEL:
                                    return new Pair<>( 4, 0 );
                                case DIMENSION_BREAKER:
                                    return new Pair<>( 4, 0 );
                            }
                            break;
                    }
                    return new Pair<>( 0, 0 );
                }
            };
    private volatile boolean isDone = false;
    private final int robotsCount;
    private final Phaser phaser;

    public Controller( int robotsCount ) {
        this.robotsCount = robotsCount;
        phaser = new Phaser( 1 + robotsCount ) {
            @Override
            protected boolean onAdvance( int phase, int registeredParties ) {
                phaseNumber = phase;
                try {
                    System.out.println();
                    System.out.printf("phase %d done %n %n", 1 + phaseNumber );
                    Thread.sleep( 1000 );
                } catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                return super.onAdvance( phase, registeredParties );
            }
        };
    }

    public void setRobots( List< Robot > robots ) {
        this.robots = robots;
    }

    @Override
    public void run() {
        while( !isDone ) {
            try {
                Robot robot = actionQueue.poll(200, TimeUnit.MILLISECONDS);
                if( robot != null ) {
                    giveRobotResources( robot );
                    synchronized( robot ) {
                        robot.notify();
                    }
                } else {
                    phaser.arriveAndAwaitAdvance();
                    if( phaser.getPhase() == FINAL_PHASE_NUMBER )
                        isDone = true;
                }
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        System.out.printf( "Controller: I'm done, shutting down %n" );
    }

    public Phaser getPhaser() {
        return phaser;
    }

    public Function< Product.ProductType, Pair< Integer, Integer > > getRecipe( Phase phase ) {
        return recipes.apply( phase );
    }

    public void askProducts( Robot robot ) {
        try {
            actionQueue.put( robot );
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private boolean isDone() {
        return robots.isEmpty();
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

}
