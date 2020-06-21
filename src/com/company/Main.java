package com.company;

import com.company.product.Product;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javafx.util.Pair;

public class Main {

    private static List< Thread > robotThreads = new LinkedList<>();
    private static List< Robot > robots = new LinkedList<>();
    private static Controller controller;
    private static Thread controllerThread;
    private static final int robotsCount = 50;

    public static void main( String[] args ) throws InterruptedException {
        // TODO run these concurrently, cancel on fail (timeout for a task)
        for( int i = 0; i < 100; ++i ) {
            long start = System.nanoTime();
            initController();
            initRobots();
            controller.setRobots( robots );

            giveRobotsStartingProducts();
            initPhaseRequirements();

            startThreads();

            awaitThreads();
            long end = System.nanoTime();

            System.out.println();
            System.out.println( "#" + i + ":  " + TimeUnit.NANOSECONDS.toMillis( end - start ) + "ms" );
            Thread.sleep( 800 );
        }
    }

    private static void awaitThreads() {
        try {
            controllerThread.join();
            for( Thread t : robotThreads )
                t.join();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private static void giveRobotsStartingProducts() {
        for( Robot robot: robots ) {
            robot.addProducts( ProductFactory.create( 5, 1 ) );
            robot.addProducts( ProductFactory.create( 6, 2 ) );
        }
    }

    private static void initPhaseRequirements() {
        CopyOnWriteArrayList< Pair< Product.ProductType, Integer > > phaseRequirements = new CopyOnWriteArrayList<>();
        phaseRequirements.add( new Pair<>( Product.ProductType.DIMENSION_BREAKER, 4 ) );
        phaseRequirements.add( new Pair<>( Product.ProductType.TRANSFORMATOR_FUEL, 4 ) );
        phaseRequirements.add( new Pair<>( Product.ProductType.DIMENSION_TRANSFORMATOR, 4 ) );
        Phase.setPhaseRequirements( phaseRequirements );
    }

    public static void initController() {
        controller = new Controller( robotsCount );
        controllerThread = new Thread( controller );
    }

    private static void startThreads() {
        controllerThread.start();
        for( Thread t : robotThreads )
            t.start();
    }

    private static void initRobots() {
        robotThreads = new LinkedList<>();
        robots = new LinkedList<>();
        Robot robot;
        Phase firstPhase = Phase.getFirst();
        var recipe = controller.getRecipe( firstPhase );
        for( int i = 0; i < robotsCount; i++ ) {
            robot = new Robot( i + 1, controller, firstPhase, recipe );
            robotThreads.add( new Thread( robot ) );
            robots.add( robot );
        }
    }
}
