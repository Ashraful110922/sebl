package com.opl.one.oplsales;

import android.app.Application;
import android.content.Context;
public class MyApplication extends Application {

    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();


   /*     PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:com.tracker");
        }
        wl.acquire();
        wl.release();*/
    }

    public static Context getAppContext() {
        return appContext;
    }

}