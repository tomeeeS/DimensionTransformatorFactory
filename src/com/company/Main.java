package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static final int CONTROLLER_MIN_CHECK_ON_ROBOTS_TIME_MS = 5;
    public static final int CONTROLLER_MAX_CHECK_ON_ROBOTS_TIME_MS = 10;

    private static List< Thread > robotThreads = new LinkedList<>();
    private static List< Robot > robots = new LinkedList<>();
    private static Controller controller;
    private static Thread controllerThread;
    private static int robotsCount;
    private static Path configPath;

    public static void main( String[] args ) {
        initController();
        readFile( args );

        startThreads();
        try {
            controllerThread.join();
            for( Thread t : robotThreads )
                t.join();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private static void readFile( String[] args ) {
        setConfigFilePath( args );
        try( BufferedReader reader = Files.newBufferedReader( configPath, Charset.forName( "UTF-8" ) ) ) {
            String currentLine = reader.readLine();
            robotsCount = Integer.parseInt( currentLine );
            initRobots();
            readRobotsStartingProducts( reader );
            readPhaseRequirements( reader );
            while( ( currentLine = reader.readLine() ) != null ) {//while there is content on the current line
                System.out.println( currentLine ); // print the current line
            }
        } catch( IOException ex ) {
            ex.printStackTrace(); //handle an exception here
        }
    }

    private static void readRobotsStartingProducts( BufferedReader reader ) throws IOException {
        String currentLine;
        for( int i = 0; i < robotsCount; i++ ) {
            currentLine = reader.readLine(); // in a line here we have pairs of product type ordinals and product counts.
            // for those product types that weren't specified in the line, the robot has 0 of them at the start.
            if( currentLine == null )
                inputFail();
            String[] ints = currentLine.split( " " );
            if( ints.length % 2 == 1 )
                inputFail();
            for( int j = 0; j < ints.length; j+= 2 ) {
                robots.get( i ).addProducts( ProductFactory.create( Integer.parseInt( ints[ j ] ), Integer.parseInt( ints[ j + 1 ] ) ) );
            }
        }
    }

    private static void readPhaseRequirements( BufferedReader reader ) throws IOException {
        String currentLine;
        int phaseCount = Phase.values().length;
        CopyOnWriteArrayList< ConcurrentHashMap< Product.ProductType, Integer > > phaseRequirements = new CopyOnWriteArrayList<>();
        for( int i = 0; i < phaseCount; i++ ) {
            ConcurrentHashMap< Product.ProductType, Integer > phaseRequirement = new ConcurrentHashMap<>();
            currentLine = reader.readLine(); // in a line here we have pairs of product type ordinals and product counts.
            // for those product types that weren't specified in the line, the robot does not need any of them for that phase.
            if( currentLine == null )
                inputFail();
            String[] ints = currentLine.split( " " );
            if( ints.length % 2 == 1 )
                inputFail();
            for( int j = 0; j < ints.length; j+= 2 ) {
                phaseRequirement.put( Product.ProductType.values()[ Integer.parseInt( ints[ j ] ) ], Integer.parseInt( ints[ j + 1 ] ) );
            }
            phaseRequirements.add( phaseRequirement );
        }
        Phase.setPhaseRequirements( phaseRequirements );
    }

    private static void inputFail() {
        System.out.println( "not enough data in file" );
        System.exit( 1 );
    }

    private static void setConfigFilePath( String[] args ) {
        String configPathString;
        if( args.length > 0 )
            configPathString = args[ 0 ];
        else
            configPathString = System.getProperty( "user.dir" ) + "/assets/config.txt";
        configPath = Paths.get( configPathString );
    }

    private static void initController() {
        controller = new Controller( CONTROLLER_MIN_CHECK_ON_ROBOTS_TIME_MS, CONTROLLER_MAX_CHECK_ON_ROBOTS_TIME_MS, robots );
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
            Phase phase = Phase.getFirst();
            robot.setNextPhase( phase, controller.getRecipe( robot, phase ) );
            robotThreads.add( new Thread( robot ) );
            robots.add( robot );
        }
    }
}
