package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrdersList {

    @SerializedName("SOHeaderID")
    @Expose
    private Double sOHeaderID;
    @SerializedName("POHeaderID")
    @Expose
    private Double pOHeaderID;
    @SerializedName("SONumber")
    @Expose
    private String sONumber;

    public Double getSOHeaderID() {
        return sOHeaderID;
    }

    public void setSOHeaderID(Double sOHeaderID) {
        this.sOHeaderID = sOHeaderID;
    }

    public Double getPOHeaderID() {
        return pOHeaderID;
    }

    public void setPOHeaderID(Double pOHeaderID) {
        this.pOHeaderID = pOHeaderID;
    }

    public String getSONumber() {
        return sONumber;
    }

    public void setSONumber(String sONumber) {
        this.sONumber = sONumber;
    }

}