package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class ApprovalDTO {

    @SerializedName("Type")
    private String Type;
    @SerializedName("DivisionID")
    private String DivisionID;
    @SerializedName("MGroupID")
    private String MGroupID;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(String divisionID) {
        DivisionID = divisionID;
    }

    public String getMGroupID() {
        return MGroupID;
    }

    public void setMGroupID(String MGroupID) {
        this.MGroupID = MGroupID;
    }
}
