package com.example.inventrax.falconOMS.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


import com.example.inventrax.falconOMS.R;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Map;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    public static String DEVICE_GCM_REGISTER_ID;
    private static AppController mInstance;
    private  Context appContext;
    public static Map<String,String> mapUserRoutes;




    public static synchronized AppController getInstance() {
        return mInstance;
    }


    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
        //Initializing Acra
        mInstance = this;
        MultiDex.install(this);

        AbstractApplication.CONTEXT = getApplicationContext();
        appContext= getApplicationContext();
        //LocaleHelper.onCreate(this, "en");


        //LocaleHelper.onCreate(this, "en");

    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();

        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

    }


}