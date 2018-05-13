package com.company;

public class AtomicAccelerator extends Product {
    private byte code;

    public AtomicAccelerator( String name, byte code ) {
        super( name );
        this.code = code;
        productType = ProductType.ATOMIC_ACCELERATOR;
    }
}
