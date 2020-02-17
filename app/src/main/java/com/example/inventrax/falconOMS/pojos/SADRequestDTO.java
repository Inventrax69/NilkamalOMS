package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SADRequestDTO {

    @SerializedName("StatusID")
    private String StatusID;

    @SerializedName("Remarks")
    private String Remarks;

    @SerializedName("Rows")
    private List<DiscountDTO> Rows;

    public String getStatusID() {
        return StatusID;
    }

    public void setStatusID(String statusID) {
        StatusID = statusID;
    }

    public List<DiscountDTO> getRows() {
        return Rows;
    }

    public void setRows(List<DiscountDTO> rows) {
        Rows = rows;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }


}
