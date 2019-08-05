package net.luculent.router;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by xiayanlei on 2017/3/9.
 * 所有非应用内url跳转都通过这个页面代理,需要在manifest里注册该页面以及对应的协议
 */
public class RouterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        if (uri != null) {
            Router.with(this).resolveIntent(uri, Router.getGlobalCallback(this), false);
        }
        finish();
    }
}
