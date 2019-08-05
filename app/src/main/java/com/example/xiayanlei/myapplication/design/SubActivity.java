package com.example.xiayanlei.myapplication.design;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.xiayanlei.myapplication.R;

import net.luculent.router.ParamInject;
import net.luculent.router.Route;

/**
 * Author       : yanbo
 * Date         : 2015-06-02
 * Time         : 10:15
 * Description  :
 */
@Route({"luculent/MBOAG00001"/*, "/MBOAG00001"*/})
public class SubActivity extends AppCompatActivity {

    @ParamInject("value")
    String value1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(android.R.drawable.ic_input_delete);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    }
}
