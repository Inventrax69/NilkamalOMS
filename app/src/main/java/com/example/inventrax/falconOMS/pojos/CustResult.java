package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustResult {

    @SerializedName("CustomerName")
    private String customerName;

    @SerializedName("CustomerID")
    private String customerID;

    @SerializedName("CustomerType")
    private String customerType;

    @SerializedName("CustomerTypeID")
    private String CustomerTypeID;

    @SerializedName("CustomerCode")
    private String customerCode;

    @SerializedName("PrimaryID")
    private String primaryID;

    @SerializedName("Zone")
    private String zone;

    @SerializedName("SalesDistrict")
    private String salesDistrict;

    @SerializedName("Division")
    private String division;

    @SerializedName("DivisionID")
    private String DivisionID;

    @SerializedName("ConnectedDepot")
    private String connectedDepot;

    @SerializedName("Mobile")
    private String mobile;

    @SerializedName("CreatedOn")
    private String createdOn;

    @SerializedName("Action")
    private String action;

    @SerializedName("Prefix")
    private String Prefix;

    @SerializedName("Type")
    private String Type;

    @SerializedName("City")
    private String City;


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerTypeID() {
        return CustomerTypeID;
    }

    public void setCustomerTypeID(String customerTypeID) {
        CustomerTypeID = customerTypeID;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getPrimaryID() {
        return primaryID;
    }

    public void setPrimaryID(String primaryID) {
        this.primaryID = primaryID;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getSalesDistrict() {
        return salesDistrict;
    }

    public void setSalesDistrict(String salesDistrict) {
        this.salesDistrict = salesDistrict;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(String divisionID) {
        DivisionID = divisionID;
    }

    public String getConnectedDepot() {
        return connectedDepot;
    }

    public void setConnectedDepot(String connectedDepot) {
        this.connectedDepot = connectedDepot;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String prefix) {
        Prefix = prefix;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }
}