package com.example.xiayanlei.myapplication;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import net.luculent.router.Modules;

import net.luculent.router.Router;
import net.luculent.router.RouterCallback;

/**
 * Created by xiayanlei on 2017/3/9.
 * 主工程注册模块，支持添加拦截器RouterCallback
 */
@Modules({"app_module", "temp_module"})
public class MyApplication extends Application implements RouterCallback {
    @Override
    public void onCreate() {
        super.onCreate();
        Router.setDebug(BuildConfig.DEBUG);
        Router.init();//初始化所有路由配置
    }

    @Override
    public boolean beforeOpen(Context context, Uri uri) {
        return false;
    }

    @Override
    public void notFound(Context context, Uri uri) {
        Toast.makeText(context, "没有对应的页面", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void afterOpen(Context context, Uri uri) {

    }
}
