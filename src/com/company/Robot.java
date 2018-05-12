package com.company;

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
    private boolean isDoneInThisPhase = false;
    private boolean isDone;

    public Robot( int id, Function< Product.ProductType, Integer > recipe ) {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public void run() {
        try {
            while( !isDone )
                executeAWorkCycle();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public boolean isDoneInThisPhase() {
        isDoneInThisPhase = currentPhase.ordinal() * 2 < cycleCount;
        if( isDoneInThisPhase )
            System.out.printf( "Robot %d: I'm done with phase %s %n", id, currentPhase );
        return isDoneInThisPhase;
    }

    public boolean isDone() {
        isDone = isDoneInThisPhase && currentPhase.isLast();
        if( isDone )
            System.out.printf( "Robot %d: I'm done and I'm shutting down %n", id );
        return isDone;
    }

    public void setNextPhase( Phase robotNextPhase, Function<Product.ProductType, Integer> recipe ) {
        currentPhase = robotNextPhase;
        this.recipe = recipe;
    }

    public int getId() {
        return id;
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
