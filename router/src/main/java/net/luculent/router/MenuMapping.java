package net.luculent.router;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by xiayanlei on 2018/7/27.
 * 菜单映射
 */

public class MenuMapping {
    Class<? extends Activity> menuActivity;
    String menu;
    String nodeId;
    String icon;

    public MenuMapping(Class<? extends Activity> menuActivity, String menu, String nodeId, String icon) {
        this.menuActivity = menuActivity;
        this.menu = menu;
        this.nodeId = nodeId;
        this.icon = icon;
    }

    public Class<?> getMenuActivity() {
        return menuActivity;
    }

    public String getNodeId() {
        return nodeId == null ? "" : nodeId;
    }

    public String getMenu(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            int resId = context.getResources().getIdentifier(menu, "string", info.packageName);
            return context.getString(resId);
        } catch (Exception e) {
            Log.e("MenuMapping", e.getMessage());
        }
        return menu == null ? "" : menu;
    }

    public int getDrawableId(Context context) {
        int resId = 0;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            resId = context.getResources().getIdentifier(icon, "drawable", info.packageName);
        } catch (Exception e) {
            Log.e("MenuMapping", e.getMessage());
        }
        return resId;
    }
}
