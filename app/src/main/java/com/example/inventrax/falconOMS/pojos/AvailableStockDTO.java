package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvailableStockDTO {

    @SerializedName("SiteID")
    @Expose
    private String siteID;
    @SerializedName("SiteCode")
    @Expose
    private String siteCode;
    @SerializedName("SiteName")
    @Expose
    private String siteName;
    @SerializedName("MaterialMasterID")
    @Expose
    private Integer materialMasterID;
    @SerializedName("AvailableQty")
    @Expose
    private Double availableQty;

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getMaterialMasterID() {
        return materialMasterID;
    }

    public void setMaterialMasterID(Integer materialMasterID) {
        this.materialMasterID = materialMasterID;
    }

    public Double getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Double availableQty) {
        this.availableQty = availableQty;
    }
}
