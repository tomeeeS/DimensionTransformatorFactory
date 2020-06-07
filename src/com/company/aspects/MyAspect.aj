package com.company.aspects;

public aspect PrintAspect {
    pointcut callPrint(): call( void java.lang.System.out.printf(..) );

    before(): callPrint() {
        System.out.print( "asdf" );
    }
}