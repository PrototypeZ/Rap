package io.github.jason1114.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason on 2017/1/28/0028.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Expires {
    long value();
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    boolean crossTimeUnit() default false;
}
