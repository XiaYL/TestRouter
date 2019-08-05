# TestRouter

简单的android路由框架，支持参数传递和自动解析

step1：在AndroidManifest.xml中注册RouterActivity

        <activity android:name="net.luculent.router.RouterActivity">
            <intent-filter>
                <action android:name="android.intent.action.VIEW"/>

                <category android:name="android.intent.category.DEFAULT"/>
                <category android:name="android.intent.category.BROWSABLE"/>

                <data
                    android:host="luculent"
                    android:scheme="http"/>
            </intent-filter>
        </activity>

step2：在Application中调用初始化方法Router.init()

step3：activity中使用@Route注解路径

step4：打开页面，支持简单对象和复杂对象的传递，简单参数也可以在url中设置

    Router.with(DesignHomeActivity.this)
          .commonExtra("userid", "SYS")//普通参数
          .serialExtra("person", new Person("James", 20))//传递复杂对象
          .open("http://luculent/MBOAG00001?orgNo=2")//参数 orgNo
          ;
    
说明：如果需要自动解析传递的参数，需要在目标页面添加注解

    //注册接收参数，value：传递的key，init：默认值，type：转换类型，包含5种基本类型，String和复杂对象bundle
    @ParamInject(value = "date", init = "dsajk", type = Type.STRING)
    String key;
    
    Router.inject(this);//注册路由参数处理器，自动解析传递的参数

多模块配置：
1）在每个module的build.gradle中添加模块名称

        javaCompileOptions {
            annotationProcessorOptions {//配置module名称，每个模块的标识
                arguments = [moduleName: 'temp_module']
            }
        }
2）在application模块里通过 @Modules 配置所有的模块，需要和各build.gradle文件的moduleName一致，一般在Application中设置

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
