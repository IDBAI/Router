package com.idba.annotation.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-16 10:58
 * describe:
 *
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)

public @interface IntentExtrasParam {
    String value();
}
