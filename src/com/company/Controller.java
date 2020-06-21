package com.company;

import com.company.product.Product;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javafx.util.Pair;

/**
 * @author Sajti Tamás
 */
@SuppressWarnings("ALL")
public class Controller implements Runnable {

    public static final int ACTION_QUEUE_CAPACITY = 1000;

    private List< Robot > robots;
    private final CyclicBarrier phaseBarrier;
    // waiting queue for robots to get products
    private final BlockingQueue<Robot> actionQueue = new LinkedBlockingQueue<>( ACTION_QUEUE_CAPACITY );
    private final Runnable phaseAction = () -> {
        try {
            System.out.println("phase done");
            Thread.sleep( 700 );
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    };
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
    private final CountDownLatch doneLatch;

    public Controller( int robotsCount ) {
        this.robotsCount = robotsCount;
        doneLatch = new CountDownLatch( robotsCount );
        phaseBarrier = new CyclicBarrier( robotsCount, phaseAction );
    }

    public void setRobots( List< Robot > robots ) {
        this.robots = robots;
    }

    @Override
    public void run() {
        while( !isDone ) {
            try {
                Robot robot = actionQueue.poll(2, TimeUnit.SECONDS);
                if( robot != null ) {
                    giveRobotResources( robot );
                    synchronized( robot ) {
                        robot.notify();
                    }
                } else {
                    doneLatch.await();
                    isDone = true;
                }
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        System.out.printf( "Controller: I'm done, shutting down %n" );
    }

    public CountDownLatch getDoneLatch() {
        return doneLatch;
    }

    public Function< Product.ProductType, Pair< Integer, Integer > > getRecipe( Phase phase ) {
        return recipes.apply( phase );
    }

    public CyclicBarrier getPhaseBarrier() {
        return phaseBarrier;
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
