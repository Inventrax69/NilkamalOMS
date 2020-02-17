package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MaterialListDTO {

    @SerializedName("MaterialMasterID")
    @Expose
    private Integer materialMasterID;
    @SerializedName("Quantity")
    @Expose
    private Integer quantity;
    @SerializedName("ExpectedDeliveryDate")
    @Expose
    private String expectedDeliveryDate;

    public Integer getMaterialMasterID() {
        return materialMasterID;
    }

    public void setMaterialMasterID(Integer materialMasterID) {
        this.materialMasterID = materialMasterID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

}
