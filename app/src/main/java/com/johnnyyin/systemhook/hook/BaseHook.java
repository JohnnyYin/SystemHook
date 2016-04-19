package com.johnnyyin.systemhook.hook;

import android.content.Context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class BaseHook implements InvocationHandler {
    protected Object mOriginObject;
    protected Context mContext;

    protected void setOriginObject(Object obj) {
        mOriginObject = obj;
    }

    public void init(Context context) {
        mContext = context;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(mOriginObject, args);
    }
}
