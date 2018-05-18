package com.company;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// the method annotated with this does not throw IOException
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GoodIo {

}
