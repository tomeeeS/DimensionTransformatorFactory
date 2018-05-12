package com.company;

import java.util.LinkedList;
import java.util.List;

public class Main {

    private static List< Thread > robotThreads;
    private static List< Robot > robots;
    private static Controller controller;
    private static Thread controllerThread;
    private static int robotsCount;

    public static void main( String[] args ) {
//        readFile();
        robotsCount = 2;
        robotThreads = new LinkedList<>();
        robots = new LinkedList<>();

        initController();
        initRobots();
        startThreads();
        try {
            controllerThread.join();
            for( Thread t : robotThreads )
                t.join();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private static void initController() {
        controller = new Controller( 5, 10, robots );
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
            robot = new Robot( i + 1, controller.getRecipe( Phase.getFirst() ) );
            robotThreads.add( new Thread( robot ) );
            robots.add( robot );
        }
    }
}
