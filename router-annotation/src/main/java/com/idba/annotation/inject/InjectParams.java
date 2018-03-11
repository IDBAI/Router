package com.idba.annotation.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-17 10:53
 * describe:
 **/
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface InjectParams {
    String value() default "";
}
