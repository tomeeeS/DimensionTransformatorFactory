package com.company;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ProductFactory {

    private static Random random = new Random();
    // product creator lambda:
    private static Function< Integer, Product > productCreater = ( productTypeIndex -> {
        int randomInt = random.nextInt( 50 );
        String productName = getRandomProductName();
        switch( productTypeIndex ) {
            case 0:
            default:
                return new DarkMatter( productName, randomInt );
            case 1:
                return new DimensionBreaker( productName, (byte)randomInt );
            case 2:
                return new DimensionTransformator( productName, randomInt );
            case 3:
                return new FreeRadical( productName, randomInt > 25 );
            case 4:
                return new TransformatorFuel( productName, randomInt );
            case 5:
                return new Hammer( productName, randomInt );
            case 6:
                return new Mirror( productName, (char)randomInt );
        }
    } );

    private static String getRandomProductName() {
        byte[] array = new byte[ 7 ];
        new Random().nextBytes( array );
        return new String( array, Charset.forName( "UTF-8" ) );
    }

    public static List< Product > create( int productTypeIndex, int productCount ) {
        List< Product > products = new ArrayList<>( productCount );
        for( int i = 0; i < productCount; i++ ) {
            products.add( productCreater.apply( productTypeIndex ) );
        }
        return products;
    }
}
