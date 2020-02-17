package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FullfilmentDTO {

    @SerializedName("CartHeaderIDs")
    private List<Integer> CartHeaderIDs;

    @SerializedName("VehicleTypeID")
    private int VehicleTypeID;

    @SerializedName("FulfilmentPreferenceID")
    private int FulfilmentPreferenceID;

    @SerializedName("OrderTypeID")
    private int OrderTypeID;

    @SerializedName("Prefix")
    private String Prefix;

    @SerializedName("CartHeaderID")
    private String CartHeaderID;

    @SerializedName("ShipToPartyCustomerID")
    private String ShipToPartyCustomerID;

    @SerializedName("CartPriorityID")
    private String CartPriorityID;

    @SerializedName("OrderClassificationID")
    private int OrderClassificationID;

    @SerializedName("MaterialList")
    private List<MaterialListDTO> MaterialList;


    public FullfilmentDTO() { }


    public List<Integer> getCartHeaderIDs() {
        return CartHeaderIDs;
    }

    public void setCartHeaderIDs(List<Integer> cartHeaderIDs) {
        CartHeaderIDs = cartHeaderIDs;
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

    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String prefix) {
        Prefix = prefix;
    }

    public String getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(String cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }

    public String getShipToPartyCustomerID() {
        return ShipToPartyCustomerID;
    }

    public void setShipToPartyCustomerID(String shipToPartyCustomerID) {
        ShipToPartyCustomerID = shipToPartyCustomerID;
    }

    public int getVehicleTypeID() {
        return VehicleTypeID;
    }

    public void setVehicleTypeID(int vehicleTypeID) {
        VehicleTypeID = vehicleTypeID;
    }

    public String getCartPriorityID() {
        return CartPriorityID;
    }

    public void setCartPriorityID(String cartPriorityID) {
        CartPriorityID = cartPriorityID;
    }

    public List<MaterialListDTO> getMaterialList() {
        return MaterialList;
    }

    public void setMaterialList(List<MaterialListDTO> materialList) {
        MaterialList = materialList;
    }


    public int getOrderClassificationID() {
        return OrderClassificationID;
    }

    public void setOrderClassificationID(int orderClassificationID) {
        OrderClassificationID = orderClassificationID;
    }

}
