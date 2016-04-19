package com.johnnyyin.systemhook.hook;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.johnnyyin.systemhook.StubActivity;
import com.johnnyyin.systemhook.utils.Reflect;
import com.johnnyyin.systemhook.utils.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class IPackageManagerHook extends BaseHook {
    private static Singleton<IPackageManagerHook> sInstance = new Singleton<IPackageManagerHook>() {
        @Override
        protected IPackageManagerHook create() {
            return new IPackageManagerHook();
        }
    };

    public static IPackageManagerHook getInstance() {
        return sInstance.get();
    }

    private IPackageManagerHook() {

    }

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            Reflect ActivityThread = Reflect.on("android.app.ActivityThread");
            setOriginObject(ActivityThread.get("sPackageManager"));
            Object proxy = Proxy.newProxyInstance(mOriginObject.getClass().getClassLoader(), mOriginObject.getClass().getInterfaces(), this);
            // 1. 替换掉ActivityThread里面的 sPackageManager 字段
            ActivityThread.set("sPackageManager", proxy);
            // 2. 替换 ApplicationPackageManager里面的 mPM对象
            Reflect.on(context.getPackageManager()).set("mPM", proxy);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d("SS", "IPackageManagerHook.init:" + Log.getStackTraceString(e));
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = super.invoke(proxy, method, args);
        if ("getActivityInfo".equals(method.getName())) {
            if (result == null && args != null && args.length > 0 && args[0] instanceof ComponentName) {
                String clsName = ((ComponentName) args[0]).getClassName();
                if (Utils.hasClass(clsName)) {
                    ActivityInfo activityInfo = mContext.getPackageManager().getActivityInfo(new ComponentName(mContext.getPackageName(), StubActivity.class.getName()), PackageManager.GET_META_DATA);
                    activityInfo.name = clsName;
                    return activityInfo;
                }
            }
        }
        return result;
    }
}
