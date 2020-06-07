package com.company;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ProductFactory {

    private static final Random random = new Random();
    // product creator lambda:
    private static final Function< Integer, Product > productCreator = ( productTypeIndex -> {
        int randomInt = random.nextInt( 50 );
        String productName = getRandomProductName();
        return switch( productTypeIndex ) {
            default -> new DarkMatter( productName, randomInt );
            case 1 -> new DimensionBreaker( productName, (byte)randomInt );
            case 2 -> new DimensionTransformator( productName, randomInt );
            case 3 -> new FreeRadical( productName, randomInt > 25 );
            case 4 -> new TransformatorFuel( productName, randomInt );
            case 5 -> new Hammer( productName, randomInt );
            case 6 -> new Mirror( productName, (char)randomInt );
        };
    } );

    private static String getRandomProductName() {
        byte[] array = new byte[ 7 ];
        new Random().nextBytes( array );
        return new String( array, StandardCharsets.UTF_8 );
    }

    public static List< Product > create( int productTypeIndex, int productCount ) {
        List< Product > products = new ArrayList<>( productCount );
        for( int i = 0; i < productCount; i++ ) {
            products.add( productCreator.apply( productTypeIndex ) );
        }
        return products;
    }
}
