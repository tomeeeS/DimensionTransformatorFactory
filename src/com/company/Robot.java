package com.company;

import com.company.product.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.util.Pair;

/**
 * @author Sajti Tamás
 */
public class Robot implements Runnable {

    private final ReadWriteLock phaseLock = new ReentrantReadWriteLock();
    private final ReadWriteLock recipeLock = new ReentrantReadWriteLock();
    private final int id;
    private final Controller controller;
    private Phase currentPhase;
    private Function< Product.ProductType, Pair< Integer, Integer > > recipe; // gyártó lambda
    private final List< Product > products = new LinkedList<>();
    private final BiFunction< Phase, List< Product >, List< Product > > produce = ( phase, ingredients ) -> {  // a recipe szerint gyárt
        synchronized( products ) {
            products.removeAll( ingredients );
            List< Product > newProducts = new ArrayList<>( 1 );
            synchronized( recipeLock.writeLock() ) {
                Arrays.stream( Product.ProductType.values() )
                    .forEach( productType ->
                        newProducts.addAll(
                            ProductFactory.create(
                                productType.ordinal(),
                                recipe.apply( productType ).getValue()
                            )
                        )
                    );
            }
            products.addAll( newProducts );
            newProducts.forEach( product -> System.out.printf( "Robot %d: I produced a %s%n", getId(), product.getProductType() ) );
            return products;
        }
    };
    private volatile boolean isDone = false;

    public Robot( int id, Controller controller, Phase firstPhase, Function< Product.ProductType, Pair< Integer, Integer>> recipe  ) {
        this.id = id;
        this.controller = controller;
        setPhase( firstPhase, recipe );
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
        synchronized( phaseLock.readLock() ) {
            return currentPhase;
        }
    }

    private void setPhase( Phase robotNextPhase, Function< Product.ProductType, Pair< Integer, Integer > > recipe ) {
        synchronized( phaseLock.writeLock() ) {
            // robot advances to next phase
            currentPhase = robotNextPhase;
        }
        synchronized( recipeLock.writeLock() ) {
            this.recipe = recipe; // a robot új gyártó lambdát kap
        }
    }

    public int getId() {
        return id;
    }

    public void addProducts( List< Product > products ) {
        synchronized( this.products ) {
            this.products.addAll( products );
        }
    }

    private void produce() {
        // robot executed production
        // it loses required products in random order
        synchronized( products ) {
            Collections.shuffle( products );
            List< Product > ingredients = new LinkedList<>();
            synchronized( recipeLock.writeLock() ) {
                Arrays.stream( Product.ProductType.values() )
                    .forEach( productType ->
                        ingredients.addAll(
                            products.stream()
                                .filter( product -> product.isOfProductType( productType ) )
                                .limit( recipe.apply( productType ).getKey() )
                                .collect( Collectors.toList() )
                        ) );
            }
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
            .allMatch( productType -> doWeHaveThisMuchOfProduct( productCountNeeded.apply( productType ), productType ) );
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
        else
            if( isDoneInPhase() ) {
                donePhaseActions();
            } else
                synchronized( this ) {
                    controller.askProducts( this );
                    wait();
                }
    }

    private void donePhaseActions() {
        try {
            controller.getPhaseBarrier().await();
        } catch( InterruptedException | BrokenBarrierException e ) {
            e.printStackTrace();
        }
        if( getCurrentPhase().isLast() ) {
            isDone = true;
            controller.getDoneLatch().countDown();
        } else
            setNextPhase();
    }

    private void setNextPhase() {
        Phase robotNextPhase = Phase.values()[ getCurrentPhase().ordinal() + 1 ];
        setPhase( robotNextPhase, controller.getRecipe( robotNextPhase ) );
    }

    public boolean isDoneInPhase() {
        return isPhaseRequirementSatisfied();
    }

}
