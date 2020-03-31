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

    /* @GET("Inventory/GetData")
    Call<String> Get(); */

    @POST("UserLogOut")
    Call<OMSCoreMessage> LoginUser(@Body OMSCoreMessage oRequest);

    @POST("ForgotPassword")
    Call<OMSCoreMessage> ForgotPassword(@Body OMSCoreMessage oRequest);

    @POST("Profile")
    Call<OMSCoreMessage> LoadProfile(@Body OMSCoreMessage oRequest);

    @POST("Changepassword")
    Call<OMSCoreMessage> ChangePassword(@Body OMSCoreMessage oRequest);

    @POST("GetNotifications")
    Call<OMSCoreMessage> GetNotifications(@Body OMSCoreMessage oRequest);

    @POST("Master/ProductCatalog")
    Call<OMSCoreMessage> ProductCatalog(@Body OMSCoreMessage oRequest);

    @POST("Master/ProductCatalog2")
    Call<OMSCoreMessage> ProductCatalog2(@Body OMSCoreMessage oRequest);

    @POST("Master/GetCustomerList")
    Call<OMSCoreMessage> GetCustomerList(@Body OMSCoreMessage oRequest);

    @POST("Master/GetCustomerListMobile")
    Call<OMSCoreMessage> GetCustomerListMobile(@Body OMSCoreMessage oRequest);

    @POST("Master/SyncItemData")
    Call<OMSCoreMessage> SyncItemData(@Body OMSCoreMessage oRequest);

    @POST("Master/SyncCustomerData")
    Call<OMSCoreMessage> SyncCustomerData(@Body OMSCoreMessage oRequest);

    @POST("Master/SyncData")
    Call<OMSCoreMessage> SyncData(@Body OMSCoreMessage oRequest);

    @POST("OrderAssistanceUpload")
    Call<OMSCoreMessage> OrderAssistanceUpload(@Body OMSCoreMessage oRequest);

/*    @POST("Orders/cartlist")
    Call<OMSCoreMessage> cartlist(@Body OMSCoreMessage oRequest);*/

    @POST("Orders/ActiveCartList")
    Call<OMSCoreMessage> cartlist(@Body OMSCoreMessage oRequest);

    @POST("Orders/GetPrice")
    Call<OMSCoreMessage> GetPrice(@Body OMSCoreMessage oRequest);

    @POST("Master/VehicleList")
    Call<OMSCoreMessage> VehicleList(@Body OMSCoreMessage oRequest);

    @POST("Orders/HHTCartDetails")
    Call<OMSCoreMessage> HHTCartDetails(@Body OMSCoreMessage oRequest);

    @POST("Orders/PendingCartList")
    Call<OMSCoreMessage> PendingCartList(@Body OMSCoreMessage oRequest);

/*    @POST("Orders/OrderFulfilment1")
    Call<OMSCoreMessage> OrderFulfilment(@Body OMSCoreMessage oRequest); */

/*    @POST("Orders/OrderFulfilment2")
    Call<OMSCoreMessage> OrderFulfilment(@Body OMSCoreMessage oRequest);*/

    @POST("Orders/OrderFulfilmentProcess")
    Call<OMSCoreMessage> OrderFulfilment(@Body OMSCoreMessage oRequest);

    @POST("Orders/DeleteCartItem")
    Call<OMSCoreMessage> DeleteCartItem(@Body OMSCoreMessage oRequest);

    @POST("Orders/DeleteCartItemReservation")
    Call<OMSCoreMessage> DeleteCartItemReservation(@Body OMSCoreMessage oRequest);

    @POST("Orders/DeleteReservation")
    Call<OMSCoreMessage> DeleteReservation(@Body OMSCoreMessage oRequest);

    @POST("Orders/CancelOrder")
    Call<OMSCoreMessage> CancelOrder(@Body OMSCoreMessage oRequest);

    @POST("Orders/InitiateWorkflow")
    Call<OMSCoreMessage> InitiateWorkflow(@Body OMSCoreMessage oRequest);

    @POST("Orders/MMIntelliSearch")
    Call<OMSCoreMessage> MMIntelliSearch(@Body OMSCoreMessage oRequest);

    @POST("Orders/InsertCreditLimitCommitments")
    Call<OMSCoreMessage> InsertCreditLimitCommitments(@Body OMSCoreMessage oRequest);

    @POST("Orders/OrderConfirmation")
    Call<OMSCoreMessage> OrderConfirmation(@Body OMSCoreMessage oRequest);

    @POST("Orders/ProceedWithOrderQty")
    Call<OMSCoreMessage> ProceedWithOrderQty(@Body OMSCoreMessage oRequest);

    @POST("Orders/ProcessCart")
    Call<OMSCoreMessage> ProcessCart(@Body OMSCoreMessage oRequest);

    @POST("Orders/SCMApprovedCartList")
    Call<OMSCoreMessage> SCMApprovedCartList(@Body OMSCoreMessage oRequest);

    @POST("Orders/ProceedWithAvbQty")
    Call<OMSCoreMessage> ProceedWithAvbQty(@Body OMSCoreMessage oRequest);

    @POST("Orders/MaterialIntelliSearch")
    Call<OMSCoreMessage> MaterialIntelliSearch(@Body OMSCoreMessage oRequest);

    @POST("Orders/getcart")
    Call<OMSCoreMessage> getcart(@Body OMSCoreMessage oRequest);

    @POST("Orders/ApprovalList")
    Call<OMSCoreMessage> ApprovalList(@Body OMSCoreMessage oRequest);

    @POST("Orders/ApprovalCreditLimitList")
    Call<OMSCoreMessage> ApprovalCreditLimitList(@Body OMSCoreMessage oRequest);

    @POST("Orders/ApprovalDiscount")
    Call<OMSCoreMessage> ApprovalDiscount(@Body OMSCoreMessage oRequest);

    @POST("Orders/ApprovelItemList")
    Call<OMSCoreMessage> ApprovelItemList(@Body OMSCoreMessage oRequest);

    @POST("Orders/ApprovalistSCMRF")
    Call<OMSCoreMessage> ApprovalistSCMRF(@Body OMSCoreMessage oRequest);

    @POST("Orders/UpsertSCMRFData")
    Call<OMSCoreMessage> UpsertSCMRFData(@Body OMSCoreMessage oRequest);

    @POST("Orders/ApproveWorkflow")
    Call<OMSCoreMessage> ApproveWorkflow(@Body OMSCoreMessage oRequest);

    @POST("Orders/UpdateApprovalCartList")
    Call<OMSCoreMessage> UpdateApprovalCartList(@Body OMSCoreMessage oRequest);

    @POST("Master/Offers")
    Call<OMSCoreMessage> Offers(@Body OMSCoreMessage oRequest);

    @POST("Orders/BulkApproveWorkflow")
    Call<OMSCoreMessage> BulkApproveWorkflowOrders(@Body OMSCoreMessage oRequest);

    @POST("Master/BulkApproveWorkflow")
    Call<OMSCoreMessage> BulkApproveWorkflowMaster(@Body OMSCoreMessage oRequest);


    /*
    @GET("Master/Image")
    Call<String> Image();
    */

    @POST("Orders/SODependencies")
    Call<OMSCoreMessage> SODependencies(@Body OMSCoreMessage oRequest);

    @POST("Orders/MaterialUnderDivision")
    Call<OMSCoreMessage> MaterialUnderDivision(@Body OMSCoreMessage oRequest);

    @POST("Orders/NHIOrderCreation")
    Call<OMSCoreMessage> NHIOrderCreation(@Body OMSCoreMessage oRequest);

    @POST("Master/Incoterms")
    Call<OMSCoreMessage> Incoterms(@Body OMSCoreMessage oRequest);

    @POST("Master/TermPayment")
    Call<OMSCoreMessage> TermPayment(@Body OMSCoreMessage oRequest);

    @POST("Master/ShiptoPartyCustomer")
    Call<OMSCoreMessage> ShiptoPartyCustomer(@Body OMSCoreMessage oRequest);

    @POST("CustomersunderUser")
    Call<OMSCoreMessage> CustomersunderUser(@Body OMSCoreMessage oRequest);

    @POST("Orders/DeleteItemFromCart")
    Call<OMSCoreMessage> DeleteItemFromCart(@Body OMSCoreMessage oRequest);

    @POST("Orders/LogException")
    Call<String> LogException(@Body OMSCoreMessage oRequest);

    @POST("Orders/GetSO")
    Call<OMSCoreMessage> GetSO(@Body OMSCoreMessage oRequest);

    @POST("Orders/GetStock")
    Call<OMSCoreMessage> GetStock(@Body OMSCoreMessage oRequest);

    @GET
    Call<ResponseBody> fetchUrl(@Url String url);

    @POST("Master/Varient")
    Call<OMSCoreMessage> Varient(@Body OMSCoreMessage oRequest);

    @POST("Orders/SOListMOB")
    Call<OMSCoreMessage> SOListMOB(@Body OMSCoreMessage oRequest);


    @POST("Orders/ActiveCartListWithOffers")
    Call<OMSCoreMessage> ActiveCartListWithOffers(@Body OMSCoreMessage oRequest);

    @POST("Orders/ProductDiscount")
    Call<OMSCoreMessage> ProductDiscount(@Body OMSCoreMessage oRequest);

}