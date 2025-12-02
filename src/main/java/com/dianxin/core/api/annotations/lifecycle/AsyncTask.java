package com.dianxin.core.api.annotations.lifecycle;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AsyncTask {

    /**
     * Optional description (for log/debug)
     */
    String value() default "";
}
