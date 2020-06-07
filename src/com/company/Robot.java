package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.util.Pair;

/**
 * @author Sajti Tamás
 */
public class Robot implements Runnable {

    private final Object productsLock = new Object();
    private final Object phaseLock = new Object();
    private final Object idLock = new Object();
    private final Object doneLock = new Object();
    private final int id;
    private Phase currentPhase;
    private Function< Product.ProductType, Pair< Integer, Integer > > recipe; // gyártó lambda
    private final Random random = new Random();
    private final List< Product > products = new LinkedList<>();
    private final BiFunction< Phase, List< Product >, List< Product > > produce = ( phase, ingredients ) ->   // a recipe szerint gyárt
    {
        synchronized( productsLock ) {
            products.removeAll( ingredients );
            List< Product > newProducts = new ArrayList<>( 1 );
            Arrays.stream( Product.ProductType.values() ).forEach( productType -> newProducts.addAll( ProductFactory.create( productType.ordinal(), recipe.apply( productType ).getValue() ) ) );
            products.addAll( newProducts );
            newProducts.forEach( product -> System.out.printf( "Robot %d: I produced a %s%n", getId(), product.getProductType() ) );
            return products;
        }
    };
    private boolean isDone = false;

    public Robot( int id ) {
        synchronized( idLock ) {
            this.id = id;
        }
    }

    @Override
    public void run() {
        try {
            while( !getIsDone() )
                executeAWorkCycle();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public void setDone( boolean isDone ) {
        synchronized( doneLock ) {
            this.isDone = isDone;
            if( this.isDone )
                System.out.printf( "Robot %d: I'm done, shutting down %n", getId() );
        }
    }

    public Phase getCurrentPhase() {
        synchronized( phaseLock ) {
            return currentPhase;
        }
    }

    public void setNextPhase( Phase robotNextPhase, Function< Product.ProductType, Pair< Integer, Integer > > recipe ) {
        synchronized( phaseLock ) {
            // robot advances to next phase
            currentPhase = robotNextPhase;
        }
        this.recipe = recipe; // a robot új gyártó lambdát kap
    }

    public int getId() {
        synchronized( idLock ) {
            return id;
        }
    }

    public void addProducts( List< Product > products ) {
        synchronized( productsLock ) {
            this.products.addAll( products );
        }
    }

    public boolean isPhaseRequirementSatisfied() {
        ConcurrentHashMap< Product.ProductType, Integer > phaseRequirement = getCurrentPhase().getPhaseRequirement();
        return Arrays.stream( Product.ProductType.values() )
                .map( productType -> doWeHaveThisMuchOfProduct( phaseRequirement.getOrDefault( productType, 0 ), productType ) )
                .reduce( true, ( Boolean x, Boolean y ) -> x && y );
    }

    public boolean getIsDone() {
        synchronized( doneLock ) {
            return isDone;
        }
    }

    private void produce() {
        // robot executed production
        // it loses required products in random order
        synchronized( productsLock ) {
            Collections.shuffle( products );
            List< Product > ingredients = new LinkedList<>();
            Arrays.stream( Product.ProductType.values() )
                    .forEach( productType ->
                            ingredients.addAll( products.stream()
                                    .filter( product -> product.isOfProductType( productType ) )
                                    .limit( recipe.apply( productType ).getKey() )
                                    .collect( Collectors.toList() ) ) );
            produce.apply( getCurrentPhase(), ingredients );
        }
    }

    private boolean isRecipeRequirementSatisfied() {
        return Arrays.stream( Product.ProductType.values() )
                .map( productType -> doWeHaveThisMuchOfProduct( recipe.apply( productType ).getKey(), productType ) )
                .reduce( true, ( Boolean x, Boolean y ) -> x && y );
    }
    // does the robot's product list satisfy the phase's requirement

    private boolean doWeHaveThisMuchOfProduct( Integer productCountNeeded, Product.ProductType productType ) {
        long productCount;
        synchronized( productsLock ) {
            productCount = products.stream().filter( product -> product.isOfProductType( productType ) ).count();
        }
        boolean doWeHaveThisMuchOfProduct = productCount >= productCountNeeded;
        if( !doWeHaveThisMuchOfProduct )
            System.out.printf( "Robot %d: I don't have %d of %s, I have %d %n", getId(), productCountNeeded, productType, productCount );
        return doWeHaveThisMuchOfProduct;
    }

    private void executeAWorkCycle() throws InterruptedException {
        if( isRecipeRequirementSatisfied() )
            produce();
        int workingTimeMs = getWorkingTimeMs();
        Thread.sleep( workingTimeMs );
    }

    private int getWorkingTimeMs() {
        return random.nextInt( getCurrentPhase().getMaxRobotWorkingTimeMs() - getCurrentPhase().getMinRobotWorkingTimeMs() + 1 )
                + getCurrentPhase().getMinRobotWorkingTimeMs();
    }
}
