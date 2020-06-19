package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.util.Pair;

/**
 * @author Sajti Tamás
 */
public class Robot implements Runnable {

    private final Object phaseLock = new Object();
    private final int id;
    private Phase currentPhase;
    private Function< Product.ProductType, Pair< Integer, Integer > > recipe; // gyártó lambda
    private final Random random = new Random();
    private final List< Product > products = new LinkedList<>();
    private final BiFunction< Phase, List< Product >, List< Product > > produce = ( phase, ingredients ) -> {  // a recipe szerint gyárt
        synchronized( products ) {
            products.removeAll( ingredients );
            List< Product > newProducts = new ArrayList<>( 1 );
            Arrays.stream( Product.ProductType.values() ).forEach( productType -> newProducts.addAll( ProductFactory.create( productType.ordinal(), recipe.apply( productType ).getValue() ) ) );
            products.addAll( newProducts );
            newProducts.forEach( product -> System.out.printf( "Robot %d: I produced a %s%n", getId(), product.getProductType() ) );
            return products;
        }
    };
    private volatile boolean isDone = false;

    public Robot( int id ) {
        this.id = id;
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

    public void setDone( boolean isDone ) {
        this.isDone = isDone;
        if( this.isDone )
            System.out.printf( "Robot %d: I'm done, shutting down %n", getId() );
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
        return id;
    }

    public void addProducts( List< Product > products ) {
        synchronized( this.products ) {
            this.products.addAll( products );
        }
    }

    public boolean isDone() {
        return isDone;
    }

    private void produce() {
        // robot executed production
        // it loses required products in random order
        synchronized( products ) {
            Collections.shuffle( products );
            List< Product > ingredients = new LinkedList<>();
            Arrays.stream( Product.ProductType.values() )
                .forEach( productType ->
                    ingredients.addAll(
                        products.stream()
                        .filter( product -> product.isOfProductType( productType ) )
                        .limit( recipe.apply( productType ).getKey() )
                        .collect( Collectors.toList() )
                    ) );
            produce.apply( getCurrentPhase(), ingredients );
        }
    }

    public boolean isPhaseRequirementSatisfied() {
        Pair< Product.ProductType, Integer > phaseRequirement = getCurrentPhase().getPhaseRequirement();
        return isRequirementSatisfied(
            productType -> {
                if( phaseRequirement.getKey() == productType)
                    return phaseRequirement.getValue();
                else
                    return 0;
            }
        );
    }

    private boolean isRecipeRequirementSatisfied() {
        return isRequirementSatisfied(
            productType -> recipe.apply( productType ).getKey());
    }

    private boolean isRequirementSatisfied( Function< Product.ProductType,Integer > productCountNeeded ) {
        return Arrays.stream( Product.ProductType.values() )
                .map( productType -> doWeHaveThisMuchOfProduct( productCountNeeded.apply( productType ), productType ) )
                .reduce( true, ( Boolean x, Boolean y ) -> x && y );
    }

    // does the robot's product list satisfy the phase's requirement
    private boolean doWeHaveThisMuchOfProduct( Integer productCountNeeded, Product.ProductType productType ) {
        long productCount;
        synchronized( products ) {
            productCount = products.stream().filter( product -> product.isOfProductType( productType ) ).count();
        }
        boolean doWeHaveThisMuchOfProduct = productCount >= productCountNeeded;
        if( !doWeHaveThisMuchOfProduct )
            System.out.printf( "Robot %d: %s: %d/%d %n", getId(), productType, productCount, productCountNeeded );
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
