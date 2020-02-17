package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartHeaderListResponseDTO {

    @SerializedName("CustomerName")
    private String CustomerName;

/*    @SerializedName("CartHeaderID")
    private int CartHeaderID;*/

    @SerializedName("CartHeaderID")
    private int[] CartHeaderID;


    @SerializedName("CreditLimit")
    private double CreditLimit;

    @SerializedName("CartDetailsID")
    private String CartDetailsID;

    @SerializedName("CartDetails")
    private List<CartDetailsListDTO> listCartDetailsList;

    @SerializedName("IsInActive")
    private int IsInActive;

    @SerializedName("IsCreditLimit")
    private int IsCreditLimit;

    @SerializedName("IsApproved")
    private int IsApproved;

    @SerializedName("CustomerID")
    private int CustomerID;

    @SerializedName("Result")
    private String Result;

    @SerializedName("CreatedOn")
    private String CreatedOn;

    @SerializedName("isPriority")
    private String isPriority;

    private String ShipToPartyID;


    @SerializedName("CartProcess")
    private List<CartProcessDTO> cartProcessDTOS;

    public CartHeaderListResponseDTO() {

    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public int[] getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(int[] cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }

    public String getCartDetailsID() {
        return CartDetailsID;
    }

    public void setCartDetailsID(String cartDetailsID) {
        CartDetailsID = cartDetailsID;
    }

    public int getIsInActive() {
        return IsInActive;
    }

    public void setIsInActive(int isInActive) {
        IsInActive = isInActive;
    }

    public int getIsCreditLimit() {
        return IsCreditLimit;
    }

    public void setIsCreditLimit(int isCreditLimit) {
        IsCreditLimit = isCreditLimit;
    }

    public int getIsApproved() {
        return IsApproved;
    }

    public void setIsApproved(int isApproved) {
        IsApproved = isApproved;
    }

    public List<CartDetailsListDTO> getListCartDetailsList() {
        return listCartDetailsList;
    }

    public void setListCartDetailsList(List<CartDetailsListDTO> listCartDetailsList) {
        this.listCartDetailsList = listCartDetailsList;
    }


    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public double getCreditLimit() {
        return CreditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        CreditLimit = creditLimit;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }


    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getIsPriority() {
        return isPriority;
    }

    public void setIsPriority(String isPriority) {
        this.isPriority = isPriority;
    }

    public String getShipToPartyID() {
        return ShipToPartyID;
    }

    public void setShipToPartyID(String shipToPartyID) {
        ShipToPartyID = shipToPartyID;
    }


    public List<CartProcessDTO> getCartProcessDTOS() {
        return cartProcessDTOS;
    }

    public void setCartProcessDTOS(List<CartProcessDTO> cartProcessDTOS) {
        this.cartProcessDTOS = cartProcessDTOS;
    }

}
