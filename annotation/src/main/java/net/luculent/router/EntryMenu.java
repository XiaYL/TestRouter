package net.luculent.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiayanlei on 2018/6/26.
 * menu config
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface EntryMenu {
    Menu value()[];
}
