package com.company;

public class Main {

    public static void main(String[] args) {
        Thread controllerThread = new Thread( new Controller( 5, 10 ) );
        controllerThread.start();
        try {
            controllerThread.join();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
