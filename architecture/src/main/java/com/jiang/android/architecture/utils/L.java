package com.jiang.android.architecture.utils;

import android.util.Log;

import com.apkfuns.logutils.LogUtils;

/**
 * Created by jiang on 2016/11/23.
 */

public class L {

    private static boolean isDEBUG = true;


    public static void debug(boolean debug) {
        isDEBUG = debug;
    }

    public static void i(String value) {
        if (isDEBUG) {
            LogUtils.i(value);
        }
    }

    public static void i(String key, String value) {
        if (isDEBUG) {
            Log.i(key, value);
        }
    }

    public static void d(String value) {
        if (isDEBUG) {
            LogUtils.d(value);
        }
    }


    public static void e(String value) {
        if (isDEBUG) {
            LogUtils.e(value);
        }
    }

    public static void v(String value) {
        if (isDEBUG) {
            LogUtils.v(value);
        }
    }


    /**
     * 打印json
     *
     * @param value
     */
    public static void json(String value) {
        if (isDEBUG) {
            LogUtils.json(value);
        }
    }
}
