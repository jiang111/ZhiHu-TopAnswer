package com.jiang.android.architecture.utils;

import com.apkfuns.logutils.LogUtils;
import com.jiang.android.architecture.BuildConfig;

/**
 * Created by jiang on 2016/11/23.
 */

public class L {

    private static boolean isDEBUG = true;

    static {
        isDEBUG = BuildConfig.DEBUG;
        LogUtils.configAllowLog = isDEBUG;
    }

    public static void i(String value) {
        if (isDEBUG) {
            LogUtils.i(value);
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
