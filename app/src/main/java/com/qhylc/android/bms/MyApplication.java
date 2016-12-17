package com.qhylc.android.bms;

import android.app.Application;
import android.content.Context;

/**
 * Created by qhylc on {2016/11/26.}
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}
