package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class PriceDTO {

    @SerializedName("PartnerID")
    private String PartnerID;
    @SerializedName("MaterialMasterID")
    private String MaterialMasterID;
    @SerializedName("CustomerID")
    private String CustomerID;
    @SerializedName("ModelID")
    private String ModelID;
    @SerializedName("Results")
    private String Results;


    public String getPartnerID() {
        return PartnerID;
    }

    public void setPartnerID(String partnerID) {
        PartnerID = partnerID;
    }


    public String getMaterialMasterID() {
        return MaterialMasterID;
    }

    public void setMaterialMasterID(String materialMasterID) {
        MaterialMasterID = materialMasterID;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getModelID() {
        return ModelID;
    }

    public void setModelID(String modelID) {
        ModelID = modelID;
    }

    public String getResults() {
        return Results;
    }

    public void setResults(String results) {
        Results = results;
    }
}
