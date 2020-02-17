package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApprovalListDTO implements Serializable,Cloneable {

    @SerializedName("Materialcode")
    @Expose
    private String materialcode;

    @SerializedName("WorkFlowtransactionID")
    @Expose
    private Integer workFlowtransactionID;

    @SerializedName("Quantity")
    @Expose
    private Double quantity;

    @SerializedName("WorkFlowTypeId")
    @Expose
    private Integer workFlowTypeId;

    @SerializedName("WorkFlowTypeID")
    @Expose
    private Integer workFlowTypeID;

    @SerializedName("WorkFlowStatus")
    @Expose
    private String workFlowStatus;

    @SerializedName("CartDetailsID")
    @Expose
    private Integer cartDetailsID;

    @SerializedName("CartHeaderID")
    @Expose
    private Integer cartHeaderID;

    @SerializedName("Price")
    @Expose
    private String price;

    @SerializedName("Description")
    @Expose
    private String description;

    @SerializedName("UserRoleId")
    @Expose
    private Integer userRoleId;

    @SerializedName("UserRoleID")
    @Expose
    private Integer userRoleID;

    @SerializedName("WorkFlowType")
    @Expose
    private String WorkFlowType;

    @SerializedName("WorkFlowStatusID")
    @Expose
    private String WorkFlowStatusID;

    @SerializedName("StatusID")
    @Expose
    private String StatusID;

    @SerializedName("Remarks")
    @Expose
    private String Remarks;

    @SerializedName("Customer")
    @Expose
    private String Customer;

    @SerializedName("CartRefNo")
    @Expose
    private String CartRefNo;

    @SerializedName("Amount")
    @Expose
    private String Amount;

    @SerializedName("BasePrice")
    @Expose
    private String BasePrice;

    @SerializedName("TotalCreditLimit")
    @Expose
    private String TotalCreditLimit;

    @SerializedName("RequiredCreditLimit")
    @Expose
    private String RequiredCreditLimit;

    @SerializedName("AvailableCreditLimit")
    @Expose
    private String AvailableCreditLimit;

    @SerializedName("CartDate")
    @Expose
    private String CartDate;

    @SerializedName("ExpectedDeliveryDate")
    @Expose
    private String ExpectedDeliveryDate;

    @SerializedName("PONumber")
    @Expose
    private String PONumber="";

    @SerializedName("RFMaterialReqStockID")
    @Expose
    private String RFMaterialReqStockID;

    @SerializedName("isCorrectValue")
    @Expose
    private boolean isCorrectValue  = false;

    @SerializedName("Status")
    @Expose
    private boolean Status  = false;

    public String getMaterialcode() {
        return materialcode;
    }

    public void setMaterialcode(String materialcode) {
        this.materialcode = materialcode;
    }

    public Integer getWorkFlowtransactionID() {
        return workFlowtransactionID;
    }

    public void setWorkFlowtransactionID(Integer workFlowtransactionID) {
        this.workFlowtransactionID = workFlowtransactionID;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getWorkFlowTypeId() {
        return workFlowTypeId;
    }

    public void setWorkFlowTypeId(Integer workFlowTypeId) {
        this.workFlowTypeId = workFlowTypeId;
    }

    public String getWorkFlowStatus() {
        return workFlowStatus;
    }

    public void setWorkFlowStatus(String workFlowStatus) {
        this.workFlowStatus = workFlowStatus;
    }

    public Integer getCartDetailsID() {
        return cartDetailsID;
    }

    public void setCartDetailsID(Integer cartDetailsID) {
        this.cartDetailsID = cartDetailsID;
    }

    public Integer getCartHeaderID() {
        return cartHeaderID;
    }

    public void setCartHeaderID(Integer cartHeaderID) {
        this.cartHeaderID = cartHeaderID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getStatusID() {
        return StatusID;
    }

    public void setStatusID(String statusID) {
        StatusID = statusID;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public Integer getWorkFlowTypeID() {
        return workFlowTypeID;
    }

    public void setWorkFlowTypeID(Integer workFlowTypeID) {
        this.workFlowTypeID = workFlowTypeID;
    }

    public Integer getUserRoleID() {
        return userRoleID;
    }

    public void setUserRoleID(Integer userRoleID) {
        this.userRoleID = userRoleID;
    }


    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getCartRefNo() {
        return CartRefNo;
    }

    public void setCartRefNo(String cartRefNo) {
        CartRefNo = cartRefNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getExpectedDeliveryDate() {
        return ExpectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        ExpectedDeliveryDate = expectedDeliveryDate;
    }

    public String getBasePrice() {
        return BasePrice;
    }

    public void setBasePrice(String basePrice) {
        BasePrice = basePrice;
    }

    public String getWorkFlowType() {
        return WorkFlowType;
    }

    public void setWorkFlowType(String workFlowType) {
        WorkFlowType = workFlowType;
    }

    public String getWorkFlowStatusID() {
        return WorkFlowStatusID;
    }

    public void setWorkFlowStatusID(String workFlowStatusID) {
        WorkFlowStatusID = workFlowStatusID;
    }

    public String getTotalCreditLimit() {
        return TotalCreditLimit;
    }

    public void setTotalCreditLimit(String totalCreditLimit) {
        TotalCreditLimit = totalCreditLimit;
    }

    public String getRequiredCreditLimit() {
        return RequiredCreditLimit;
    }

    public void setRequiredCreditLimit(String requiredCreditLimit) {
        RequiredCreditLimit = requiredCreditLimit;
    }

    public String getAvailableCreditLimit() {
        return AvailableCreditLimit;
    }

    public void setAvailableCreditLimit(String availableCreditLimit) {
        AvailableCreditLimit = availableCreditLimit;
    }

    public String getCartDate() {
        return CartDate;
    }

    public void setCartDate(String cartDate) {
        CartDate = cartDate;
    }


    @Override
    public ApprovalListDTO clone() throws CloneNotSupportedException {
        return (ApprovalListDTO) super.clone();
    }

    public boolean isCorrectValue() {
        return isCorrectValue;
    }

    public void setCorrectValue(boolean correctValue) {
        isCorrectValue = correctValue;
    }

    public String getPONumber() {
        return PONumber;
    }

    public void setPONumber(String PONumber) {
        this.PONumber = PONumber;
    }

    public String getRFMaterialReqStockID() {
        return RFMaterialReqStockID;
    }

    public void setRFMaterialReqStockID(String RFMaterialReqStockID) {
        this.RFMaterialReqStockID = RFMaterialReqStockID;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }


}
