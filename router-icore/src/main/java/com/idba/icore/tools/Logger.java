package com.idba.icore.tools;

import android.util.Log;
/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/
public class Logger {
    private static boolean isDebug = true;//BuildConfig.DEBUG;


    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isDebug)
            Log.w(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.v(tag, msg);
    }
}
