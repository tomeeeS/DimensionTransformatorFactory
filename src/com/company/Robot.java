package com.company;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Sajti Tamás
 */
public class Robot implements Runnable {

    private int id;
    private Phase currentPhase = Phase.getFirst();
    private Function< Product.ProductType, Integer > recipe;
    private Random random = new Random();
    private boolean[] hasIsDoneInPhaseBeenReported;
    private boolean hasIsDoneBeenReported = false;
    private List< Product > products = new LinkedList<>();
    private BiFunction< Phase, List< Product > ,List< Product >  > produce = ( phase, removableList ) ->
        {
            products.removeAll( removableList );
            switch( phase ) {
                case ASSEMBLE_ACCELERATOR:
                    products.addAll( ProductFactory.create( 2, 2 ) );
                    break;
                case ASSEMBLE_DIMENSION_BREAKER:
                    products.addAll( ProductFactory.create( 3, 1 ) );
                    break;
                case BLEND_FUEL:
                    products.addAll( ProductFactory.create( 4, 2 ) );
                    break;
            }
            return products;
        };

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
        boolean isDoneInThisPhase = isRecipeSatisfied();
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

    public void setNextPhase( Phase robotNextPhase, Function< Product.ProductType, Integer > recipe ) {
        // robot executed production
        // loss of required products randomly
        Collections.shuffle( products );
        List< Product > removables = new LinkedList<>();
        Arrays.stream( Product.ProductType.values() )
                .forEach( productType -> removables.addAll( products.stream()
                        .filter( product -> product.isOfProductType( productType ) )
                        .limit( this.recipe.apply( productType ) )
                        .collect( Collectors.toList() ) ) );
        produce.apply( currentPhase, removables );

        // robot advances to next phase
        currentPhase = robotNextPhase;
        this.recipe = recipe;
    }

    public int getId() {
        return id;
    }

    public void addProducts( List< Product > products ) {
        this.products.addAll( products );
    }

    private boolean isRecipeSatisfied() {
        return Arrays.stream( Product.ProductType.values() )
                .map( productType -> doWeHaveThisMuchOfProduct( recipe.apply( productType ), productType ) )
                .reduce( true, ( Boolean x, Boolean y ) -> x && y );
    }

    private boolean doWeHaveThisMuchOfProduct( Integer productCountNeeded, Product.ProductType productType ) {
        long productCount = products.stream().filter( product -> product.isOfProductType( productType ) ).count();
        boolean doWeHaveThisMuchOfProduct = productCount >= productCountNeeded;
        if( !doWeHaveThisMuchOfProduct )
            System.out.printf( "Robot %d: I don't have %d of %s, I have %d %n", id, productCountNeeded, productType, productCount );
        return doWeHaveThisMuchOfProduct;
    }

    private void executeAWorkCycle() throws InterruptedException {
        int workingTimeMs = getWorkingTimeMs();
        Thread.sleep( workingTimeMs );
    }

    private int getWorkingTimeMs() {
        return random.nextInt( currentPhase.getMaxRobotWorkingTimeMs() - currentPhase.getMinRobotWorkingTimeMs() + 1 )
                + currentPhase.getMinRobotWorkingTimeMs();
    }
}
