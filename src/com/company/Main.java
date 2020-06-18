package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
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
    private static Path configPath;

    public static void main( String[] args ) throws IOException, InterruptedException {
        for(int i = 0; i < 100; ++i) {
            initController();
            readFile( args );

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

    @GoodIo
    public static void readFile( String[] args ) throws IOException {
//        setConfigFilePath( args );
//        try( BufferedReader reader = Files.newBufferedReader( configPath, StandardCharsets.UTF_8 ) ) {
//            String currentLine = reader.readLine();
            //Integer.parseInt( currentLine );
            initRobots();
            readRobotsStartingProducts( null );
            readPhaseRequirements( null );
//            while( ( currentLine = reader.readLine() ) != null ) {//while there is content on the current line
//                System.out.println( currentLine ); // print the current line
//            }
//        } catch( IOException ex ) {
////            inputFail();
//            throw( ex );
//        }
    }

    private static void readRobotsStartingProducts( BufferedReader reader ) throws IOException {
//        String currentLine;
        for( int i = 0; i < robotsCount; i++ ) {
//            currentLine = reader.readLine(); // in a line here we have pairs of product type ordinals and product amounts.
            // for those product types that weren't specified in the line, the robot has 0 of them at the start.
//            if( currentLine == null )
//                inputFail();
//            String[] ints = currentLine.split( " " );
//            if( ints.length % 2 == 1 )
//                inputFail();
//            for( int j = 0; j < ints.length; j+= 2 ) {
//                robots.get( i ).addProducts( ProductFactory.create( Integer.parseInt( ints[ j ] ), Integer.parseInt( ints[ j + 1 ] ) ) );
                robots.get( i ).addProducts( ProductFactory.create( 5, 1 ) );
                robots.get( i ).addProducts( ProductFactory.create( 6, 2 ) );
//            }
        }
    }

    private static void readPhaseRequirements( BufferedReader reader ) throws IOException {
//        String currentLine;
//        int phaseCount = Phase.values().length;
        CopyOnWriteArrayList< Pair< Product.ProductType, Integer > > phaseRequirements = new CopyOnWriteArrayList<>();
//        for( int i = 0; i < phaseCount; i++ ) {
//            ConcurrentHashMap< Product.ProductType, Integer > phaseRequirement = new ConcurrentHashMap<>();
//            currentLine = reader.readLine(); // in a line here we have pairs of product type ordinals and product amounts.
//            // for those product types that weren't specified in the line, the robot does not need any of them for that phase.
//            if( currentLine == null )
//                inputFail();
//            String[] ints = currentLine.split( " " );
//            if( ints.length % 2 == 1 )
//                inputFail();
//            for( int j = 0; j < ints.length; j+= 2 ) {
//                phaseRequirement.put( Product.ProductType.values()[ Integer.parseInt( ints[ j ] ) ], Integer.parseInt( ints[ j + 1 ] ) );
//            }
            phaseRequirements.add( new Pair<>( Product.ProductType.DIMENSION_BREAKER, 4 ) );
            phaseRequirements.add( new Pair<>( Product.ProductType.TRANSFORMATOR_FUEL, 4 ) );
            phaseRequirements.add( new Pair<>( Product.ProductType.DIMENSION_TRANSFORMATOR, 4 ) );
//        }
        Phase.setPhaseRequirements( phaseRequirements );
    }

//    private static void inputFail() {
//        System.out.println( "not enough data in file" );
//        System.exit( 1 );
//    }
//
//    private static void setConfigFilePath( String[] args ) {
//        String configPathString;
//        if( args.length > 0 )
//            configPathString = args[ 0 ];
//        else
//            configPathString = System.getProperty( "user.dir" ) + "/assets/config.txt";
//        configPath = Paths.get( configPathString );
//    }

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
