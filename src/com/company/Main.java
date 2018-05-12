package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List< Thread > robotThreads;
    private static List< Robot > robots;
    private static Controller controller;

    public static void main( String[] args ) {
//        readFile();
        int robotCount = 1;
        robotThreads = new ArrayList<>( robotCount );
        robots = new ArrayList<>( robotCount );
        controller = new Controller( 5, 10, robots );
        Thread controllerThread = new Thread( controller );
        initRobots();
        startThreads( controllerThread );
        try {
            controllerThread.join();
            for( Thread t : robotThreads )
                t.join();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private static void startThreads( Thread controllerThread ) {
        controllerThread.start();
        for( Thread t : robotThreads )
            t.start();
    }

    private static void initRobots() {
        Robot robot = new Robot( controller.getRecipe( Phase.getFirst() ) );
        robotThreads.add( new Thread( robot ) );
        robots.add( robot );
    }
}
