package com.example.xiayanlei.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.luculent.router.FieldInjector;
import net.luculent.router.ParamInject;
import net.luculent.router.Route;
import net.luculent.router.Router;
import net.luculent.router.Type;

import java.util.List;

@Route({"luculent", "/main"})
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    @ParamInject(value = "date", init = "dsajk")
    String key;

    @ParamInject(value = "person", type = Type.BUNDLE)
    Person person;

    @ParamInject(value = "persons", type = Type.BUNDLE)
    List<Person> persons;

    FieldInjector injector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        injector = Router.inject(this);
        Log.d(TAG, "key = " + key);
        if (person != null) {
            Log.d(TAG, "receive person = " + person.toString());
        }
        if (persons != null) {
            for (Person person : persons) {
                Log.d(TAG, "person in list = " + person.toString());
            }
        }
    }

    public void onlineView(View view) {
        Log.i(TAG, "onlineView: " + (view instanceof TextView));
        if (view instanceof TextView) {
            WebviewActivity.gotoWeb(this, ((TextView) view).getText().toString());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        injector.reset();
    }
}
