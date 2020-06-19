package com.company;

import java.io.IOException;
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
    private static final int robotsCount = 200;

    public static void main( String[] args ) throws IOException, InterruptedException {
        for(int i = 0; i < 100; ++i) {
            initController();
            initRobots();
            giveRobotsStartingProducts();
            initPhaseRequirements();

            startThreads();

            long start = System.nanoTime();
            try {
                controllerThread.join();
                for( Thread t : robotThreads )
                    t.join();
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
            long end = System.nanoTime();
            System.out.println();
            System.out.println( "#" + i + ":  " + TimeUnit.NANOSECONDS.toMillis( end - start ) + "ms" );
            Thread.sleep( 500 );
        }
    }

    private static void giveRobotsStartingProducts() {
        for( int i = 0; i < robotsCount; i++ ) {
            robots.get( i ).addProducts( ProductFactory.create( 5, 1 ) );
            robots.get( i ).addProducts( ProductFactory.create( 6, 2 ) );
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
        robotThreads = new LinkedList<>();
        robots = new LinkedList<>();
        controller = new Controller( robots );
        controllerThread = new Thread( controller );
    }

    private static void startThreads() {
        controllerThread.start();
        for( Thread t : robotThreads )
            t.start();
    }

    private static void initRobots() {
        Robot robot;
        for( int i = 0; i < robotsCount; i++ ) {
            robot = new Robot( i + 1 );
            Phase firstPhase = Phase.getFirst();
            robot.setNextPhase( firstPhase, controller.getRecipe( firstPhase ) );
            robotThreads.add( new Thread( robot ) );
            robots.add( robot );
        }
    }
}
