package com.fenghuo.observer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 张少林
 * on 15-3-16 下午10:07
 * Email:zsl@kuba.bz
 * Android Studio
 * Function:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ObserverAction {
    public String[] value();
}
