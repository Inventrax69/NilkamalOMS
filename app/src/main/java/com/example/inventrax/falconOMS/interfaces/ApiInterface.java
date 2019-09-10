package com.example.inventrax.falconOMS.interfaces;

/**
 * Author   : Padmaja Rani B.
 * Date		: 04/07/2019
 * Purpose	: Web Service URL's and Web Methods
 */

import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface ApiInterface {


    /*@GET("Inventory/GetData")
    Call<String> Get();*/

    @POST("UserLogOut")
    Call<OMSCoreMessage> LoginUser(@Body OMSCoreMessage oRequest);

    @POST("ForgotPasswordActivty")
    Call<OMSCoreMessage> ForgotPassword(@Body OMSCoreMessage oRequest);

    @POST("Profile")
    Call<OMSCoreMessage> LoadProfile(@Body OMSCoreMessage oRequest);

    @POST("Changepassword")
    Call<OMSCoreMessage> ChangePassword(@Body OMSCoreMessage oRequest);

    @POST("Inventory/GetActiveStock")
    Call<String> GetActiveStock(@Body OMSCoreMessage oRequest);

    @POST("Master/GetItemList")
    Call<OMSCoreMessage> GetItemList(@Body OMSCoreMessage oRequest);

    @POST("Master/GetProductCatalog")
    Call<OMSCoreMessage> GetProductCatalog(@Body OMSCoreMessage oRequest);

    @POST("Master/GetCustomerList")
    Call<OMSCoreMessage> GetCustomerList(@Body OMSCoreMessage oRequest);

    @POST("Master/SyncItemData")
    Call<OMSCoreMessage> SyncItemData(@Body OMSCoreMessage oRequest);

    @POST("Master/SyncCustomerData")
    Call<OMSCoreMessage> SyncCustomerData(@Body OMSCoreMessage oRequest);


    /*@GET("Master/Image")
    Call<String> Image();*/

    @GET
    Call<ResponseBody> fetchUrl(@Url String url);
}