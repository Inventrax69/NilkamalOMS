package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CustomerPartnerDTO implements Serializable,Cloneable {

    @SerializedName("RFM")
    @Expose
    private Boolean rFM;
    @SerializedName("PartnerID")
    @Expose
    private Integer partnerID;
    @SerializedName("PartnerName")
    @Expose
    private String partnerName;
    @SerializedName("CartHeaderID")
    @Expose
    private Integer cartHeaderID;

    @SerializedName("IsProcessID")
    @Expose
    private String IsProcessID="0";
    @SerializedName("CustomerID")
    @Expose
    private Integer customerID;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("CustomerCode")
    @Expose
    private String customerCode;
    @SerializedName("CreditLimit")
    @Expose
    private Double creditLimit;
    @SerializedName("CartHeader")
    @Expose
    private List<CartHeaderListDTO> cartHeader = null;
    @SerializedName("Materials")
    @Expose
    private List<CardMaterialDTO> materials = null;


    public Boolean getrFM() {
        return rFM;
    }

    public void setrFM(Boolean rFM) {
        this.rFM = rFM;
    }

    public Integer getPartnerID() {
        return partnerID;
    }

    public void setPartnerID(Integer partnerID) {
        this.partnerID = partnerID;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Integer getCartHeaderID() {
        return cartHeaderID;
    }

    public void setCartHeaderID(Integer cartHeaderID) {
        this.cartHeaderID = cartHeaderID;
    }

    public List<CardMaterialDTO> getMaterials() {
        return materials;
    }

    public void setMaterials(List<CardMaterialDTO> materials) {
        this.materials = materials;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public List<CartHeaderListDTO> getCartHeader() {
        return cartHeader;
    }

    public void setCartHeader(List<CartHeaderListDTO> cartHeader) {
        this.cartHeader = cartHeader;
    }

    public String getIsProcessID() {
        return IsProcessID;
    }

    public void setIsProcessID(String isProcessID) {
        IsProcessID = isProcessID;
    }

    @Override
    public List<CartHeaderListDTO> clone() throws CloneNotSupportedException {
        return (List<CartHeaderListDTO>) super.clone();
    }

}
