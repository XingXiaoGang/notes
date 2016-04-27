package com.fenghuo.observer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 张少林
 * on 15-3-31 下午4:48
 * Email:zsl@kuba.bz
 * Android Studio
 * Function:
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    public String value();
}
