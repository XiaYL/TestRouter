package net.luculent.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiayanlei on 2018/8/17.
 * annotation to inject activity into manifest
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MActivity {

    String launchMode() default "standard";

    String orientation() default "portrait";
}
