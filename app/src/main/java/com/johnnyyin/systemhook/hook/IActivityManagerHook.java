package com.johnnyyin.systemhook.hook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.johnnyyin.systemhook.StubActivity;
import com.johnnyyin.systemhook.plugin.PluginActivityInfo;
import com.johnnyyin.systemhook.utils.Constants;
import com.johnnyyin.systemhook.utils.Reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class IActivityManagerHook extends BaseHook {

    private static class ActivityManagerCompat {
        public static final int START_SUCCESS = 0;
        public static final int START_CLASS_NOT_FOUND = -2;
        public static final int START_INTENT_NOT_RESOLVED = -1;
    }

    private static Singleton<IActivityManagerHook> sInstance = new Singleton<IActivityManagerHook>() {
        @Override
        protected IActivityManagerHook create() {
            return new IActivityManagerHook();
        }
    };

    public static IActivityManagerHook getInstance() {
        return sInstance.get();
    }

    private IActivityManagerHook() {

    }

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            Object gDefaultVal = Reflect.on("android.app.ActivityManagerNative").get("gDefault");
            Reflect Singleton = Reflect.on(gDefaultVal);
            setOriginObject(Singleton.get("mInstance"));
            Singleton.set("mInstance", Proxy.newProxyInstance(mOriginObject.getClass().getClassLoader(), mOriginObject.getClass().getInterfaces(), this));
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d("SS", "ActivityManagerHook.invoke:" + Log.getStackTraceString(e));
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d("SS", "ActivityManagerHook.invoke: name = " + method.getName() + ", args = " + Arrays.toString(args));
        Object result = super.invoke(proxy, method, args);
        if ("startActivity".equals(method.getName())) {
            if (result instanceof Integer && isActivityNotFount((Integer) result) && args != null && args.length >= 3) {
                // android.app.ActivityManager#START_SUCCESS
                Intent intent = (Intent) args[2];
                PluginActivityInfo info = new PluginActivityInfo();
                info.targetActivityName = intent.getComponent().getClassName();
                intent.putExtra(Constants.PLUGIN_ACTIVITY_INFO, info);
                intent.setComponent(new ComponentName(mContext.getPackageName(), StubActivity.class.getName()));
                long start = System.currentTimeMillis();
                result = super.invoke(proxy, method, args);
                Log.d("SS", "ActivityManagerHook startActivity, time = " + (System.currentTimeMillis() - start));
                return result;
            }
        }
        return result;
    }

    public static boolean isActivityNotFount(int res) {
        if (res >= ActivityManagerCompat.START_SUCCESS) {
            return false;
        }

        switch (res) {
            case ActivityManagerCompat.START_INTENT_NOT_RESOLVED:
            case ActivityManagerCompat.START_CLASS_NOT_FOUND:
                return true;
        }
        return false;
    }
}
