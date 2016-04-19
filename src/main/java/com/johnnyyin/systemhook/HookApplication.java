package com.johnnyyin.systemhook;

import android.app.Application;

import com.johnnyyin.systemhook.hook.ActivityThreadHook;
import com.johnnyyin.systemhook.hook.IActivityManagerHook;
import com.johnnyyin.systemhook.hook.IPackageManagerHook;

public class HookApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        IActivityManagerHook.getInstance().init(this);
        IPackageManagerHook.getInstance().init(this);
        ActivityThreadHook.getInstance().init(this);
    }

}
