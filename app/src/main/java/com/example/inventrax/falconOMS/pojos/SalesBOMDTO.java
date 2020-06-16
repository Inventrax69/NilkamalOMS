package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SalesBOMDTO {

    @SerializedName("MaterialMasterID")
    private int MaterialMasterID;

    @SerializedName("BOMQuantity")
    private Double BOMQuantity;

    @SerializedName("BOMLineNumber")
    private int BOMLineNumber;


    @SerializedName("Mcode")
    private String Mcode;

    @SerializedName("Mdescription")
    private String Mdescription;


    public SalesBOMDTO() {

    }


    public int getMaterialMasterID() {
        return MaterialMasterID;
    }

    public void setMaterialMasterID(int materialMasterID) {
        MaterialMasterID = materialMasterID;
    }

    public Double getBOMQuantity() {
        return BOMQuantity;
    }

    public void setBOMQuantity(Double BOMQuantity) {
        this.BOMQuantity = BOMQuantity;
    }

    public int getBOMLineNumber() {
        return BOMLineNumber;
    }

    public void setBOMLineNumber(int BOMLineNumber) {
        this.BOMLineNumber = BOMLineNumber;
    }

    public String getMcode() {
        return Mcode;
    }

    public void setMcode(String mcode) {
        Mcode = mcode;
    }

    public String getMdescription() {
        return Mdescription;
    }

    public void setMdescription(String mdescription) {
        Mdescription = mdescription;
    }


}
