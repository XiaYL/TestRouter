package com.example.xiayanlei.myapplication.design;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.xiayanlei.myapplication.Person;
import com.example.xiayanlei.myapplication.R;

import net.luculent.router.ParamInject;
import net.luculent.router.Route;
import net.luculent.router.Router;
import net.luculent.router.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个中文版Demo App搞定所有Android的Support Library新增所有兼容控件
 * 支持最新2015 Google I/O大会Android Design Support Library
 */
@Route({"designHome"})
public class DesignHomeActivity extends ActionBarActivity {
    //将ToolBar与TabLayout结合放入AppBarLayout
    private Toolbar mToolbar;
    //DrawerLayout中的左侧菜单控件
    private NavigationView mNavigationView;
    //DrawerLayout控件
    private DrawerLayout mDrawerLayout;
    //Tab菜单，主界面上面的tab切换菜单
    private TabLayout mTabLayout;
    //v4中的ViewPager控件
    private ViewPager mViewPager;

    //注册接收参数，value：传递的key，init：默认值，type：转换类型，包含5种基本类型，String和复杂对象bundle
    @ParamInject(value = "date", init = "dsajk", type = Type.STRING)
    String key;

    @ParamInject("date2")
    String key1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_home);
        //初始化控件及布局
        initView();
        Router.inject(this);//注册路由参数处理器，自动解析传递的参数
    }

    private void initView() {
        //MainActivity的布局文件中的主要控件初始化
        mToolbar = (Toolbar) this.findViewById(R.id.tool_bar);
        mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) this.findViewById(R.id.navigation_view);
        mTabLayout = (TabLayout) this.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) this.findViewById(R.id.view_pager);

        //初始化ToolBar
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_dialog_alert);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //对NavigationView添加item的监听事件
        mNavigationView.setNavigationItemSelectedListener(naviListener);
        //开启应用默认打开DrawerLayout
        mDrawerLayout.openDrawer(mNavigationView);

        //初始化TabLayout的title数据集
        List<String> titles = new ArrayList<>();
        titles.add("details");
        titles.add("share");
        titles.add("agenda");
        //初始化TabLayout的title
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));
        //初始化ViewPager的数据集
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new InfoDetailsFragment());
        fragments.add(new ShareFragment());
        fragments.add(new AgendaFragment());
        //创建ViewPager的adapter
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(adapter);
        //千万别忘了，关联TabLayout与ViewPager
        //同时也要覆写PagerAdapter的getPageTitle方法，否则Tab没有title
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
    }

    private NavigationView.OnNavigationItemSelectedListener naviListener = new NavigationView
            .OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            //点击NavigationView中定义的menu item时触发反应
            switch (menuItem.getItemId()) {
                case R.id.menu_info_details:
                    Router.with(DesignHomeActivity.this).commonExtra("userid", "SYS").open
                            ("http://luculent/MBOAG00001?orgNo=2");//传递普通参数，可以通过commonExtra或者url
                    break;
                case R.id.menu_share:
                    Router.with(DesignHomeActivity.this).serialExtra("person", new Person("James", 20)).open
                            ("http://luculent/main");
                    break;
                case R.id.menu_agenda:
//                    List<Person> persons = new ArrayList<>();
//                    persons.add(new Person("Tim", 12));
//                    persons.add(new Person("Lucy", 13));
//                    //传递复杂对象
//                    Router.with(DesignHomeActivity.this).serialExtra("person", new Person("James", 20))
//                            .serialExtra("persons", (Serializable) persons)
//                            .open("luculent://test/temp/mainActivity?userid=SYS");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://luculent/main")));
                    break;
            }
            //关闭DrawerLayout回到主界面选中的tab的fragment页
            mDrawerLayout.closeDrawer(mNavigationView);
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //主界面右上角的menu菜单
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info_details:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.menu_share:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.menu_agenda:
                mViewPager.setCurrentItem(2);
                break;
            case android.R.id.home:
                //主界面左上角的icon点击反应
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        }
    }
}
