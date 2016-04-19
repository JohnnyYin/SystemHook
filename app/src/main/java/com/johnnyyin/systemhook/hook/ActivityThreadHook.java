package com.johnnyyin.systemhook.hook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.johnnyyin.systemhook.plugin.PluginActivityInfo;
import com.johnnyyin.systemhook.utils.Constants;
import com.johnnyyin.systemhook.utils.Reflect;

public class ActivityThreadHook extends BaseHook {

    private static class HCompat {
        public static final int LAUNCH_ACTIVITY = 100;
    }

    private static Singleton<ActivityThreadHook> sInstance = new Singleton<ActivityThreadHook>() {
        @Override
        protected ActivityThreadHook create() {
            return new ActivityThreadHook();
        }
    };

    public static ActivityThreadHook getInstance() {
        return sInstance.get();
    }

    private ActivityThreadHook() {

    }

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            Object activityThreadVal = Reflect.on("android.app.ActivityThread").get("sCurrentActivityThread");
            Object mHVal = Reflect.on(activityThreadVal).get("mH");
            Reflect.on(mHVal).set("mCallback", new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    // LAUNCH_ACTIVITY
                    if (msg.what == HCompat.LAUNCH_ACTIVITY) {
                        try {
                            Intent intent = Reflect.on(msg.obj).get("intent");
                            if (intent.getExtras() != null) {
                                ClassLoader oldClassLoader = Reflect.on(intent.getExtras()).get("mClassLoader");
                                intent.setExtrasClassLoader(PluginActivityInfo.class.getClassLoader());
                                Parcelable p = intent.getParcelableExtra(Constants.PLUGIN_ACTIVITY_INFO);
                                intent.setExtrasClassLoader(oldClassLoader);
                                if (p instanceof PluginActivityInfo) {
                                    PluginActivityInfo info = (PluginActivityInfo) p;
                                    if (!TextUtils.isEmpty(info.targetActivityName)) {
                                        intent.setComponent(new ComponentName(intent.getComponent().getPackageName(), info.targetActivityName));
                                        ActivityInfo activityInfo = Reflect.on(msg.obj).get("activityInfo");
                                        activityInfo.name = info.targetActivityName;
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Log.d("SS", "ActivityThreadHook.init:" + Log.getStackTraceString(e));
                        }
                    }
                    return false;
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d("SS", "ActivityThreadHook.init:" + Log.getStackTraceString(e));
        }
    }
}
