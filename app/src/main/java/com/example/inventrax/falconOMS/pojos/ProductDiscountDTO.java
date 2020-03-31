package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductDiscountDTO {

    @SerializedName("MaterialMasterID")
    @Expose
    private String MaterialMasterID;
    @SerializedName("ModelID")
    @Expose
    private String ModelID;
    @SerializedName("CustomerID")
    @Expose
    private String CustomerID;

    public String getMaterialMasterID() {
        return MaterialMasterID;
    }

    public void setMaterialMasterID(String materialMasterID) {
        MaterialMasterID = materialMasterID;
    }

    public String getModelID() {
        return ModelID;
    }

    public void setModelID(String modelID) {
        ModelID = modelID;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }
}
