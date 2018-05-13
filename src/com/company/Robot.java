package com.company;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * @author Sajti Tamás
 */
public class Robot implements Runnable {
    private int id;
    private Phase currentPhase = Phase.getFirst();
    private Function< Product.ProductType, Integer > recipe;
    private Random random = new Random();
    private int cycleCount;
    private boolean[] hasIsDoneInPhaseBeenReported;
    private boolean hasIsDoneBeenReported = false;
    private List<Product> products = new LinkedList<>();

    public Robot( int id, Function< Product.ProductType, Integer > recipe ) {
        this.id = id;
        this.recipe = recipe;
        hasIsDoneInPhaseBeenReported = new boolean[ Phase.values().length ];
        for( int i = 0; i < hasIsDoneInPhaseBeenReported.length; i++ )
            hasIsDoneInPhaseBeenReported[ i ] = false;
    }

    @Override
    public void run() {
        try {
            while( !isDone() )
                executeAWorkCycle();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public boolean isDoneInThisPhase() {
        boolean isDoneInThisPhase = currentPhase.ordinal() * 2 < cycleCount;
        if( isDoneInThisPhase && !hasIsDoneInPhaseBeenReported[ currentPhase.ordinal() ] ) {
            System.out.printf( "Robot %d: I'm done with phase %s %n", id, currentPhase );
            hasIsDoneInPhaseBeenReported[ currentPhase.ordinal() ] = true;
        }
        return isDoneInThisPhase;
    }

    public boolean isDone() {
        boolean isDone = isDoneInThisPhase() && currentPhase.isLast();
        if( isDone && !hasIsDoneBeenReported ) {
            hasIsDoneBeenReported = true;
            System.out.printf( "Robot %d: I'm done, shutting down %n", id );
        }
        return isDone;
    }

    public void setNextPhase( Phase robotNextPhase, Function<Product.ProductType, Integer> recipe ) {
        currentPhase = robotNextPhase;
        this.recipe = recipe;
    }

    public int getId() {
        return id;
    }

    public void addProducts( List<Product> products ) {
        this.products.addAll( products );
    }

    private void executeAWorkCycle() throws InterruptedException {
        int workingTimeMs = getWorkingTimeMs();
        Thread.sleep( workingTimeMs );
        System.out.printf( "Robot %d: working for %d ms %n", id, workingTimeMs );

        cycleCount++; // todo remove
    }

    private int getWorkingTimeMs() {
        return random.nextInt( currentPhase.getMaxRobotWorkingTimeMs() - currentPhase.getMinRobotWorkingTimeMs() + 1 )
                + currentPhase.getMinRobotWorkingTimeMs();
    }
}
