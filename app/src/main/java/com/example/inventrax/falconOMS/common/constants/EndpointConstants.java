package com.example.inventrax.falconOMS.common.constants;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Padmaja.B on 05/31/2018.
 */

/*public enum EndpointConstants {
    None, LoginDTO, LoginUserDTO, ProfileDTO, Inventory, Exception, ItemMaster_FPS_DTO,
    Customer_FPS_DTO, ProductCatalog_FPS_DTO, OrderAssistance_DTO,HHTCartDTO,OrderFulfilment_DTO,
    Schemes_Discounts_DTO, Scalar, SOHeader_DTO,MiscDTO,CreditLimitCommitment_DTO,SO_FPS_DTO,SOPGI_FPS_DTO;
}*/

public enum EndpointConstants {
    @SerializedName("none")
    None,
    @SerializedName("LoginDTO")
    LoginDTO,
    @SerializedName("LoginUserDTO")
    LoginUserDTO,
    @SerializedName("ProfileDTO")
    ProfileDTO,
    @SerializedName("Inventory")
    Inventory,
    @SerializedName("Exception")
    Exception,
    @SerializedName("ItemMaster_FPS_DTO")
    ItemMaster_FPS_DTO,
    @SerializedName("Customer_FPS_DTO")
    Customer_FPS_DTO,
    @SerializedName("ProductCatalog_FPS_DTO")
    ProductCatalog_FPS_DTO,
    @SerializedName("OrderAssistance_DTO")
    OrderAssistance_DTO,
    @SerializedName("HHTCartDTO")
    HHTCartDTO,
    @SerializedName("OrderFulfilment_DTO")
    OrderFulfilment_DTO,
    @SerializedName("Schemes_Discounts_DTO")
    Schemes_Discounts_DTO,
    @SerializedName("Scalar")
    Scalar,
    @SerializedName("SOHeader_DTO")
    SOHeader_DTO,
    @SerializedName("MiscDTO")
    MiscDTO,
    @SerializedName("CreditLimitCommitment_DTO")
    CreditLimitCommitment_DTO,
    @SerializedName("SO_FPS_DTO")
    SO_FPS_DTO,
    @SerializedName("SOPGI_FPS_DTO")
    SOPGI_FPS_DTO
}