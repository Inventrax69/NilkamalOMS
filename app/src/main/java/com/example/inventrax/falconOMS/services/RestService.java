package com.example.inventrax.falconOMS.services;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Padmaja on 02/07/2019.
 */
public class RestService {


    //public static final String BASE_URL = "http://192.168.1.15/FalconWMSCore_Endpoint/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        try {
            retrofit = null;
            if (retrofit == null) {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100,TimeUnit.SECONDS).build();

                         retrofit = new Retrofit.Builder()
                        //.baseUrl("http://192.168.1.61/OMS_Storefront/API/").client(client)
                        .baseUrl("http://192.168.1.20/OMS_StoreFront/api/").client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        } catch (Exception ex) {
            Log.d("Exceptionerror", ex.toString());
        }
        return retrofit;
    }
}