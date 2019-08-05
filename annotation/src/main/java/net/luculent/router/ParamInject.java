package net.luculent.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiayanlei on 2017/3/5.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface ParamInject {

    String value();

    Type type() default Type.STRING;//

    String init() default "";//
}
