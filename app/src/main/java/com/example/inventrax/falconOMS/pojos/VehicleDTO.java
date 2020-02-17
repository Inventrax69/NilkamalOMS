package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class VehicleDTO {

    @SerializedName("CartHeaderID")
    private int CartHeaderID;

    @SerializedName("VehickeTypeID")
    private int VehickeTypeID;

    @SerializedName("FulfilmentPreferenceID")
    private int FulfilmentPreferenceID;

    @SerializedName("OrderTypeID")
    private int OrderTypeID;

    public VehicleDTO() {

    }


    public int getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(int cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }

    public int getVehickeTypeID() {
        return VehickeTypeID;
    }

    public void setVehickeTypeID(int vehickeTypeID) {
        VehickeTypeID = vehickeTypeID;
    }

    public int getFulfilmentPreferenceID() {
        return FulfilmentPreferenceID;
    }

    public void setFulfilmentPreferenceID(int fulfilmentPreferenceID) {
        FulfilmentPreferenceID = fulfilmentPreferenceID;
    }

    public int getOrderTypeID() {
        return OrderTypeID;
    }

    public void setOrderTypeID(int orderTypeID) {
        OrderTypeID = orderTypeID;
    }
}
