package com.example.mank;

import android.app.Application;
import android.content.Context;

public class MainActivityClassForContext extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MainActivityClassForContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MainActivityClassForContext.context;
    }
}
