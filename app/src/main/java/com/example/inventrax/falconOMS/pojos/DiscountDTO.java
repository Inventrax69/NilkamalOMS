package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DiscountDTO implements Serializable {

    @SerializedName("WorkFlowtransactionID")
    @Expose
    private Integer workFlowtransactionID;
    @SerializedName("WorkFlowTypeId")
    @Expose
    private Integer workFlowTypeId;
    @SerializedName("WorkFlowStatus")
    @Expose
    private String workFlowStatus;
    @SerializedName("DiscountID")
    @Expose
    private Integer discountID;
    @SerializedName("WorkFlowType")
    @Expose
    private String workFlowType;
    @SerializedName("DiscountCode")
    @Expose
    private String discountCode;
    @SerializedName("WorkFlowStatusID")
    @Expose
    private String workFlowStatusID;
    @SerializedName("UserRoleID")
    @Expose
    private String userRoleID;
    @SerializedName("Remarks")
    @Expose
    private String remarks;
    @SerializedName("CartHeaderID")
    @Expose
    private String CartHeaderID;

    public Integer getWorkFlowtransactionID() {
        return workFlowtransactionID;
    }

    public void setWorkFlowtransactionID(Integer workFlowtransactionID) {
        this.workFlowtransactionID = workFlowtransactionID;
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

    public Integer getDiscountID() {
        return discountID;
    }

    public void setDiscountID(Integer discountID) {
        this.discountID = discountID;
    }

    public String getWorkFlowType() {
        return workFlowType;
    }

    public void setWorkFlowType(String workFlowType) {
        this.workFlowType = workFlowType;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getWorkFlowStatusID() {
        return workFlowStatusID;
    }

    public void setWorkFlowStatusID(String workFlowStatusID) {
        this.workFlowStatusID = workFlowStatusID;
    }

    public String getUserRoleID() {
        return userRoleID;
    }

    public void setUserRoleID(String userRoleID) {
        this.userRoleID = userRoleID;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(String cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }

}
