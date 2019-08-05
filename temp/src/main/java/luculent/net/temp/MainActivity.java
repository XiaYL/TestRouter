package luculent.net.temp;

import android.app.Activity;
import android.os.Bundle;

import net.luculent.router.EntryMenu;
import net.luculent.router.MActivity;
import net.luculent.router.Menu;
import net.luculent.router.Route;

/**
 * Created by hulifei on 2017/3/6.
 */

@Route("/temp/mainActivity")
@EntryMenu({
        @Menu(menu = "菜单变种1", nodeId = "MBOAG00001", icon = "icon_people"),
        @Menu(menu = "菜单变种2", nodeId = "MBOAG00002", icon = "icon_people")
})
@MActivity
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
