package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class PriceDTO {

    @SerializedName("PartnerID")
    private String PartnerID;
    @SerializedName("MaterialMasterID")
    private String MaterialMasterID;

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
}
