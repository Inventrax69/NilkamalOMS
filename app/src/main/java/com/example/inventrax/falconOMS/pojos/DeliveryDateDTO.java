package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeliveryDateDTO {

    @SerializedName("ActualDeliveryDate")
    private String ActualDeliveryDate;

    @SerializedName("FromDate")
    private String FromDate;

    @SerializedName("ToDate")
    private String ToDate;

    @SerializedName("CartDetails")
    private List<CartDetailsListDTO> listCartDetailsList;

    public String getActualDeliveryDate() {
        return ActualDeliveryDate;
    }

    public void setActualDeliveryDate(String actualDeliveryDate) {
        ActualDeliveryDate = actualDeliveryDate;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getToDate() {
        return ToDate;
    }

    public void setToDate(String toDate) {
        ToDate = toDate;
    }

    public List<CartDetailsListDTO> getListCartDetailsList() {
        return listCartDetailsList;
    }

    public void setListCartDetailsList(List<CartDetailsListDTO> listCartDetailsList) {
        this.listCartDetailsList = listCartDetailsList;
    }


}
