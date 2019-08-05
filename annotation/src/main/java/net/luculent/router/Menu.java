package net.luculent.router;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by xiayanlei on 2018/7/27.
 */
@Retention(RetentionPolicy.CLASS)
public @interface Menu {
    String menu();//name of menu

    String nodeId();//id of menu

    String icon();//icon of menu
}
