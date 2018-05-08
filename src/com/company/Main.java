package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List< Thread > robotThreads = new ArrayList<>( 1 );
        List< Robot > robots = new ArrayList<>( 1 );
        Robot robot = new Robot();
        robotThreads.add( new Thread( robot ) );
        robots.add( robot );
        Thread controllerThread = new Thread( new Controller( 5, 10, robots ) );
        controllerThread.start();
        try {
            controllerThread.join();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
