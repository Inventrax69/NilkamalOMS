package com.example.inventrax.falconOMS.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.inventrax.falconOMS.application.AbstractApplication;
import com.example.inventrax.falconOMS.model.KeyValues;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Padmaja on 02/07/2019.
 */
public class RestService {

    // public static final String BASE_URL = "http://192.168.1.15/FalconWMSCore_Endpoint/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {

        try {

            Context context = AbstractApplication.CONTEXT;
            retrofit = null;
            SharedPreferences sp = context.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(200, TimeUnit.SECONDS)
                        .readTimeout(200, TimeUnit.SECONDS)
                        .build();

                retrofit = new Retrofit.Builder()
                         //.baseUrl("http://192.168.1.62/OMS_Masters/").client(client)
                         //.baseUrl("http://192.168.1.20/OMS_StoreFront/api/").client(client)
                           .baseUrl(sp.getString(KeyValues.SETTINGS_URL,"")).client(client)
                         //.baseUrl("http://192.168.1.145/oms_storefront/api/").client(client)
                         //.baseUrl("http://103.252.184.181:90/OMS_StoreFront/api/").client(client)
                         //.baseUrl("http://192.168.1.62/OMS_StoreFront/api/").client(client)
                         //.baseUrl("http://192.168.1.241/OMS_StoreFront/api/").client(client)
                         //.baseUrl("http://192.168.1.241/OMS_Masters/api/").client(client)
                         //.baseUrl("http://192.168.1.62/OMS_Orders/").client(client)
                         //.baseUrl("http://192.168.1.9/OMS_Orders/").client(client)
                         //.baseUrl("http://192.168.1.62/OMS_Orders/").client(client)
                         //.baseUrl("http://192.168.1.65/OMS_Orders/").client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

            return retrofit;

        } catch (Exception ex) {
            Log.d("Exceptionerror", ex.toString());
        }

        return retrofit;
    }
}