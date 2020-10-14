package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceOrderResponce {

    @SerializedName("CartHeaderID")
    @Expose
    private Double cartHeaderID;
    @SerializedName("CartRefNo")
    @Expose
    private String cartRefNo;

    @SerializedName("ExceptionError")
    @Expose
    private String ExceptionError;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("IsCreditLimit")
    @Expose
    private Double isCreditLimit;
    @SerializedName("OrdersList")
    @Expose
    private List<OrdersList> ordersList = null;

    public Double getCartHeaderID() {
        return cartHeaderID;
    }

    public void setCartHeaderID(Double cartHeaderID) {
        this.cartHeaderID = cartHeaderID;
    }

    public String getCartRefNo() {
        return cartRefNo;
    }

    public void setCartRefNo(String cartRefNo) {
        this.cartRefNo = cartRefNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getIsCreditLimit() {
        return isCreditLimit;
    }

    public void setIsCreditLimit(Double isCreditLimit) {
        this.isCreditLimit = isCreditLimit;
    }

    public List<OrdersList> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<OrdersList> ordersList) {
        this.ordersList = ordersList;
    }

    public String getExceptionError() {
        return ExceptionError;
    }

    public void setExceptionError(String exceptionError) {
        ExceptionError = exceptionError;
    }

}
