package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ProductFactory {

    private static Random random = new Random();
    // product creator lambda:
    private static Function< Integer, Product > productCreater = ( productTypeIndex -> {
        int randomInt = random.nextInt( 50 );
        switch( productTypeIndex ) {
            case 0:
            default:
                return new AtomicAccelerator( (byte)randomInt );
            case 1:
                return new DarkMatter( randomInt );
            case 2:
                return new Electricity( randomInt );
            case 3:
                return new FreeRadical( randomInt > 25 );
            case 4:
                return new Hammer( randomInt );
            case 5:
                return new Mirror( (char)randomInt );
        }
    } );

    public static List< Product > create( int productTypeIndex, int productCount ) {
        List< Product > products = new ArrayList<>( productCount );
        for( int i = 0; i < productCount; i++ ) {
            products.add( productCreater.apply( productTypeIndex ) );
        }
        return products;
    }
}
