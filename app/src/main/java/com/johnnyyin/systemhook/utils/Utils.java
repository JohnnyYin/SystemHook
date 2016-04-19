package com.johnnyyin.systemhook.utils;

import android.text.TextUtils;

public class Utils {

    public static boolean hasClass(String name) {
        try {
            return !TextUtils.isEmpty(name) && Class.forName(name) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
