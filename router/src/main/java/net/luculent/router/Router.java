package net.luculent.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayanlei on 2017/3/5.
 * 页面跳转处理器
 */
public class Router {

    private static final String TAG = "Router";

    private static boolean debug = false;


    /**
     * activity关联路径，一个activity可以关联多个路径
     */
    static final Map<String, Class> ACTIVITY_INJECTORS = new LinkedHashMap<>();

    /**
     * activity和参数关联
     */
    static final Map<Class, List<FieldMapping>> FIELD_INJECTORS = new LinkedHashMap<>();

    static final Map<Class, Constructor<? extends FieldInjector>> BINDINGS = new LinkedHashMap<>();

    static final Map<String, MenuMapping> MENU_INJECTORS = new HashMap<>();

    private Router() {
        throw new AssertionError("No instance");
    }

    static void activityInject(String route, Class clazz) {
        ACTIVITY_INJECTORS.put(route, clazz);
    }

    static void fieldInject(Class clazz, FieldMapping mapping) {
        List<FieldMapping> mappings = FIELD_INJECTORS.get(clazz);
        if (mappings == null) {
            mappings = new ArrayList<>();
        }
        mappings.add(mapping);
        FIELD_INJECTORS.put(clazz, mappings);
    }

    static void menuInject(String nodeId, MenuMapping menuMapping) {
        MENU_INJECTORS.put(nodeId, menuMapping);
        activityInject(nodeId, menuMapping.getMenuActivity());
    }

    /**
     * app启动的时候将activity和对应的参数注册
     */
    public static void init() {
        if (ACTIVITY_INJECTORS.isEmpty() || FIELD_INJECTORS.isEmpty()) {
            RouteInit.init();
            if (debug) {
                for (Map.Entry<String, Class> map : ACTIVITY_INJECTORS.entrySet()) {
                    Log.d(TAG, "Injected activity is " + map.getKey() + ": " + map.getValue());
                }
            }
        }
    }

    public static void setDebug(boolean debug) {
        Router.debug = debug;
    }

    /**
     * @param activity
     */
    public static FieldInjector inject(Activity activity) {
        Constructor<? extends FieldInjector> constructor = findBindingConstructorForClass(activity.getClass());
        List<FieldMapping> mappings = FIELD_INJECTORS.get(activity.getClass());
        try {
            return constructor.newInstance(activity, mappings);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance", cause);
        }
    }

    public static MenuMapping getMenuInfo(String nodeId) {
        return MENU_INJECTORS.get(nodeId);
    }

    public static Collection<MenuMapping> getMenus() {
        return MENU_INJECTORS.values();
    }

    public static ActivityRouter with(Context context) {
        return new ActivityRouter(context);
    }

    public static class ActivityRouter {
        Context context;
        int flg = -1;
        int inAnimate = -1;//进入动画
        int outAnimate = -1;//销毁动画
        int requestCode = -1;
        Bundle bundle;

        public ActivityRouter(Context context) {
            this.context = context;
            bundle = new Bundle();
        }

        public ActivityRouter addFlags(int flags) {
            this.flg |= flags;
            return this;
        }

        /**
         * 设置页面进入和退出的效果
         *
         * @param enter
         * @param exit
         * @return
         */
        public ActivityRouter doAnimate(int enter, int exit) {
            this.inAnimate = enter;
            this.outAnimate = exit;
            return this;
        }

        public ActivityRouter commonExtra(String key, String value) {
            bundle.putString(key, value);
            return this;
        }

        public ActivityRouter serialExtra(String key, Serializable serializable) {
            bundle.putSerializable(key, serializable);
            return this;
        }

        public ActivityRouter parcelExtra(String key, Parcelable parcelable) {
            bundle.putParcelable(key, parcelable);
            return this;
        }

        public ActivityRouter parcelArrayExtra(String key, ArrayList<? extends Parcelable> value) {
            bundle.putParcelableArrayList(key, value);
            return this;
        }

        public void openForResult(String url, int requestCode) {
            this.requestCode = requestCode;
            open(url);
        }

        public void openForResult(String url, int requestCode, RouterCallback callback) {
            this.requestCode = requestCode;
            open(url, callback);
        }

        public boolean open(String url) {
            return open(url, getGlobalCallback(context));
        }

        public boolean open(String url, RouterCallback callback) {
            return resolveIntent(Uri.parse(url), callback, true);
        }

        boolean resolveIntent(Uri uri, RouterCallback callback, boolean isInner) {
            if (debug)
                Log.d(TAG, "Redirect uri is " + uri.toString());
            if (callback != null) {
                if (callback.beforeOpen(context, uri)) {//拦截打开
                    return false;
                }
            }
            boolean success = redirectInApp(context, uri, isInner);
            if (callback != null) {
                if (success) {
                    callback.afterOpen(context, uri);
                } else {
                    callback.notFound(context, uri);
                }
            }
            return success;
        }

        private boolean redirectInApp(Context context, Uri uri, boolean isInner) {
            RouteRule rule = new RouteRule(uri);
            Class clazz = findBindingActivity(rule);
            if (clazz != null) {
                Intent intent = new Intent(context, clazz);
                intent.putExtras(rule.generateBundle(new Bundle()));
                if (isInner) {
                    if (flg != -1) {
                        intent.addFlags(flg);
                    }
                    if (requestCode != -1 && context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, requestCode);
                    } else {
                        context.startActivity(intent);
                    }
                    startAnimate(context);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                return true;
            }
            if (debug)
                Log.d(TAG, "No activity found for rule : " + rule.toString());
            return false;
        }

        void startAnimate(Context context) {
            if (context instanceof Activity && inAnimate != -1 && outAnimate != -1) {
                ((Activity) context).overridePendingTransition(inAnimate, outAnimate);
            }
        }
    }

    private static Class findBindingActivity(RouteRule rule) {
        if (rule == null) {
            return null;
        }
        Class clazz = ACTIVITY_INJECTORS.get(rule.component);
        if (clazz != null) {
            if (debug)
                Log.d(TAG, "Activity found with component " + rule.component);
            return clazz;
        } else {
            clazz = findBindingActivity(rule.next());
        }
        return clazz;
    }

    static RouterCallback getGlobalCallback(Context context) {
        if (context.getApplicationContext() instanceof IRouter){
            IRouter iRouter = (IRouter) context.getApplicationContext();
            if (iRouter != null){
                return iRouter.globalCallback();
            }
        }
        return null;
    }

    private static Constructor<? extends FieldInjector> findBindingConstructorForClass(Class<?> clazz) {
        Constructor<? extends FieldInjector> constructor = BINDINGS.get(clazz);
        if (constructor != null) {
            if (debug)
                Log.d(TAG, "Injector cached in bindings");
            return constructor;
        }
        String clzName = clazz.getName();
        if (clzName.startsWith("android.") || clzName.startsWith("java.")) {
            if (debug)
                Log.d(TAG, "Miss:Reached framework class.Abandoning search");
            return null;
        }
        try {
            Class<?> bindClass = Class.forName(clzName + "_ParamInject");
            constructor = (Constructor<? extends FieldInjector>) bindClass.getConstructor(clazz, List.class);
            if (debug)
                Log.d(TAG, "Loaded binding class and constructor");
        } catch (ClassNotFoundException e) {
            if (debug)
                Log.d(TAG, "Not found.Trying superclass " + clazz.getSuperclass().getName());
            constructor = findBindingConstructorForClass(clazz.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clzName, e);
        }
        BINDINGS.put(clazz, constructor);
        return constructor;
    }
}
