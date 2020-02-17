package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CardMaterialDTO {

    @SerializedName("MCode")
    @Expose
    private String mCode;
    @SerializedName("MDescription")
    @Expose
    private String mDescription;
    @SerializedName("OrderQuantity")
    @Expose
    private Double orderQuantity;
    @SerializedName("AvailableQuantity")
    @Expose
    private Double availableQuantity;

    public String getMCode() {
        return mCode;
    }

    public void setMCode(String mCode) {
        this.mCode = mCode;
    }

    public String getMDescription() {
        return mDescription;
    }

    public void setMDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Double getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Double orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
