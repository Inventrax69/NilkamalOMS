package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class SODetails {

    @SerializedName("LineNumber")
    private int LineNumber;

    @SerializedName("Material")
    private String Material;

    @SerializedName("Quantity")
    private int Quantity;

    @SerializedName("ConditionTypes")
    private String ConditionTypes;

    @SerializedName("UnitPrice")
    private String UnitPrice;

    @SerializedName("MaterialMasterID")
    private int MaterialMasterID;

    @SerializedName("mDesc")
    private String mDesc;


    public SODetails(){

    }


    public int getLineNumber() {
        return LineNumber;
    }

    public void setLineNumber(int lineNumber) {
        LineNumber = lineNumber;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getConditionTypes() {
        return ConditionTypes;
    }

    public void setConditionTypes(String conditionTypes) {
        ConditionTypes = conditionTypes;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public int getMaterialMasterID() {
        return MaterialMasterID;
    }

    public void setMaterialMasterID(int materialMasterID) {
        MaterialMasterID = materialMasterID;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }
}
