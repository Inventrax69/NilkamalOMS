package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SOHeaderDetailsDTO {

    @SerializedName("OrderTypeID")
    @Expose
    private String orderTypeID;
    @SerializedName("SaleOrganizationID")
    @Expose
    private String saleOrganizationID;
    @SerializedName("DistributionChannelID")
    @Expose
    private String distributionChannelID;
    @SerializedName("DivisionID")
    @Expose
    private String divisionID;
    @SerializedName("SoldToCustomerID")
    @Expose
    private String soldToCustomerID;
    @SerializedName("ShipToCustomerID")
    @Expose
    private String shipToCustomerID;
    @SerializedName("PONumber")
    @Expose
    private String pONumber;
    @SerializedName("PODate")
    @Expose
    private String pODate;
    @SerializedName("Remarks")
    @Expose
    private String remarks;
    @SerializedName("IncoTermsID")
    @Expose
    private String incoTermsID;
    @SerializedName("PaymentsTermID")
    @Expose
    private String paymentsTermID;
    @SerializedName("SOHeaderID")
    @Expose
    private Integer sOHeaderID;
    @SerializedName("SONumber")
    @Expose
    private String sONumber;
    @SerializedName("SODetails")
    @Expose
    private Object sODetails;
    @SerializedName("SaleOrganization")
    @Expose
    private String saleOrganization;
    @SerializedName("DistributionChannel")
    @Expose
    private String distributionChannel;
    @SerializedName("Division")
    @Expose
    private String division;
    @SerializedName("PaymentsTerm")
    @Expose
    private String paymentsTerm;
    @SerializedName("IncoTerms")
    @Expose
    private String incoTerms;
    @SerializedName("SoldToCustomer")
    @Expose
    private String soldToCustomer;
    @SerializedName("ShipToCustomer")
    @Expose
    private String shipToCustomer;
    @SerializedName("OrderType")
    @Expose
    private String orderType;
    @SerializedName("CustomerID")
    @Expose
    private Object customerID;
    @SerializedName("UploadPDF")
    @Expose
    private Object uploadPDF;
    @SerializedName("Address")
    @Expose
    private Object address;
    @SerializedName("FreightPaidbyCustomer")
    @Expose
    private String freightPaidbyCustomer;
    @SerializedName("HasOpenPrice")
    @Expose
    private Object hasOpenPrice;
    @SerializedName("IsInActive")
    @Expose
    private Boolean isInActive;
    @SerializedName("IsOpenPrice")
    @Expose
    private Boolean isOpenPrice;
    @SerializedName("IsCreditLimit")
    @Expose
    private Boolean isCreditLimit;
    @SerializedName("IsApproved")
    @Expose
    private Integer isApproved;
    @SerializedName("CartHeaderId")
    @Expose
    private Integer cartHeaderId;
    @SerializedName("OrderAssitanceID")
    @Expose
    private Integer orderAssitanceID;
    @SerializedName("Type")
    @Expose
    private Object type;
    @SerializedName("SiteID")
    @Expose
    private Integer siteID;
    @SerializedName("NHICustomerName")
    @Expose
    private Object nHICustomerName;
    @SerializedName("NHICustomerAddress1")
    @Expose
    private Object nHICustomerAddress1;
    @SerializedName("NHICustomerAddress2")
    @Expose
    private Object nHICustomerAddress2;
    @SerializedName("NHICustomerEmail")
    @Expose
    private Object nHICustomerEmail;
    @SerializedName("NHICustomerMobile")
    @Expose
    private Object nHICustomerMobile;
    @SerializedName("PaymentModeID")
    @Expose
    private Integer paymentModeID;
    @SerializedName("OrderReasonID")
    @Expose
    private Integer orderReasonID;
    @SerializedName("CustomerGroup")
    @Expose
    private Object customerGroup;
    @SerializedName("OrderClassfication")
    @Expose
    private Integer orderClassfication;
    @SerializedName("IsCancel")
    @Expose
    private Integer isCancel;
    @SerializedName("RoleID")
    @Expose
    private Integer roleID;
    @SerializedName("ZFCCPayment")
    @Expose
    private Object zFCCPayment;
    @SerializedName("DiscountTypeID")
    @Expose
    private Object discountTypeID;
    @SerializedName("FrieghtAmount")
    @Expose
    private Object frieghtAmount;
    @SerializedName("HeaderDiscountPer")
    @Expose
    private Object headerDiscountPer;
    @SerializedName("SalesEMPCode")
    @Expose
    private Object salesEMPCode;
    @SerializedName("CustomerGroupID")
    @Expose
    private Integer customerGroupID;
    @SerializedName("OrderApprovalType")
    @Expose
    private Object orderApprovalType;
    @SerializedName("UploadImages")
    @Expose
    private Object uploadImages;
    @SerializedName("Site")
    @Expose
    private Object site;
    @SerializedName("SOStatus")
    @Expose
    private Object sOStatus;
    @SerializedName("WorkfloWTransactionId")
    @Expose
    private Integer workfloWTransactionId;
    @SerializedName("WorkFlowTypeId")
    @Expose
    private Integer workFlowTypeId;
    @SerializedName("UserRoleId")
    @Expose
    private Integer userRoleId;
    @SerializedName("Result")
    @Expose
    private Object result;
    @SerializedName("CustomizedTypes")
    @Expose
    private Integer customizedTypes;
    @SerializedName("LeadTime")
    @Expose
    private Integer leadTime;
    @SerializedName("CustomerGroup5ID")
    @Expose
    private Integer customerGroup5ID;
    @SerializedName("CustomerGroup5Code")
    @Expose
    private Object customerGroup5Code;
    @SerializedName("DeliveryDate")
    @Expose
    private Object deliveryDate;
    @SerializedName("MaxDetailsDiscount")
    @Expose
    private Object maxDetailsDiscount;
    @SerializedName("DiscountApprovedName")
    @Expose
    private Object discountApprovedName;
    @SerializedName("DiscountApprovedID")
    @Expose
    private Integer discountApprovedID;
    @SerializedName("POCopyPath")
    @Expose
    private Object pOCopyPath;

    public String getOrderTypeID() {
        return orderTypeID;
    }

    public void setOrderTypeID(String orderTypeID) {
        this.orderTypeID = orderTypeID;
    }

    public String getSaleOrganizationID() {
        return saleOrganizationID;
    }

    public void setSaleOrganizationID(String saleOrganizationID) {
        this.saleOrganizationID = saleOrganizationID;
    }

    public String getDistributionChannelID() {
        return distributionChannelID;
    }

    public void setDistributionChannelID(String distributionChannelID) {
        this.distributionChannelID = distributionChannelID;
    }

    public String getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(String divisionID) {
        this.divisionID = divisionID;
    }

    public String getSoldToCustomerID() {
        return soldToCustomerID;
    }

    public void setSoldToCustomerID(String soldToCustomerID) {
        this.soldToCustomerID = soldToCustomerID;
    }

    public String getShipToCustomerID() {
        return shipToCustomerID;
    }

    public void setShipToCustomerID(String shipToCustomerID) {
        this.shipToCustomerID = shipToCustomerID;
    }

    public String getPONumber() {
        return pONumber;
    }

    public void setPONumber(String pONumber) {
        this.pONumber = pONumber;
    }

    public String getPODate() {
        return pODate;
    }

    public void setPODate(String pODate) {
        this.pODate = pODate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getIncoTermsID() {
        return incoTermsID;
    }

    public void setIncoTermsID(String incoTermsID) {
        this.incoTermsID = incoTermsID;
    }

    public String getPaymentsTermID() {
        return paymentsTermID;
    }

    public void setPaymentsTermID(String paymentsTermID) {
        this.paymentsTermID = paymentsTermID;
    }

    public Integer getSOHeaderID() {
        return sOHeaderID;
    }

    public void setSOHeaderID(Integer sOHeaderID) {
        this.sOHeaderID = sOHeaderID;
    }

    public String getSONumber() {
        return sONumber;
    }

    public void setSONumber(String sONumber) {
        this.sONumber = sONumber;
    }

    public Object getSODetails() {
        return sODetails;
    }

    public void setSODetails(Object sODetails) {
        this.sODetails = sODetails;
    }

    public String getSaleOrganization() {
        return saleOrganization;
    }

    public void setSaleOrganization(String saleOrganization) {
        this.saleOrganization = saleOrganization;
    }

    public String getDistributionChannel() {
        return distributionChannel;
    }

    public void setDistributionChannel(String distributionChannel) {
        this.distributionChannel = distributionChannel;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPaymentsTerm() {
        return paymentsTerm;
    }

    public void setPaymentsTerm(String paymentsTerm) {
        this.paymentsTerm = paymentsTerm;
    }

    public String getIncoTerms() {
        return incoTerms;
    }

    public void setIncoTerms(String incoTerms) {
        this.incoTerms = incoTerms;
    }

    public String getSoldToCustomer() {
        return soldToCustomer;
    }

    public void setSoldToCustomer(String soldToCustomer) {
        this.soldToCustomer = soldToCustomer;
    }

    public String getShipToCustomer() {
        return shipToCustomer;
    }

    public void setShipToCustomer(String shipToCustomer) {
        this.shipToCustomer = shipToCustomer;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Object getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Object customerID) {
        this.customerID = customerID;
    }

    public Object getUploadPDF() {
        return uploadPDF;
    }

    public void setUploadPDF(Object uploadPDF) {
        this.uploadPDF = uploadPDF;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public String getFreightPaidbyCustomer() {
        return freightPaidbyCustomer;
    }

    public void setFreightPaidbyCustomer(String freightPaidbyCustomer) {
        this.freightPaidbyCustomer = freightPaidbyCustomer;
    }

    public Object getHasOpenPrice() {
        return hasOpenPrice;
    }

    public void setHasOpenPrice(Object hasOpenPrice) {
        this.hasOpenPrice = hasOpenPrice;
    }

    public Boolean getIsInActive() {
        return isInActive;
    }

    public void setIsInActive(Boolean isInActive) {
        this.isInActive = isInActive;
    }

    public Boolean getIsOpenPrice() {
        return isOpenPrice;
    }

    public void setIsOpenPrice(Boolean isOpenPrice) {
        this.isOpenPrice = isOpenPrice;
    }

    public Boolean getIsCreditLimit() {
        return isCreditLimit;
    }

    public void setIsCreditLimit(Boolean isCreditLimit) {
        this.isCreditLimit = isCreditLimit;
    }

    public Integer getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }

    public Integer getCartHeaderId() {
        return cartHeaderId;
    }

    public void setCartHeaderId(Integer cartHeaderId) {
        this.cartHeaderId = cartHeaderId;
    }

    public Integer getOrderAssitanceID() {
        return orderAssitanceID;
    }

    public void setOrderAssitanceID(Integer orderAssitanceID) {
        this.orderAssitanceID = orderAssitanceID;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Integer getSiteID() {
        return siteID;
    }

    public void setSiteID(Integer siteID) {
        this.siteID = siteID;
    }

    public Object getNHICustomerName() {
        return nHICustomerName;
    }

    public void setNHICustomerName(Object nHICustomerName) {
        this.nHICustomerName = nHICustomerName;
    }

    public Object getNHICustomerAddress1() {
        return nHICustomerAddress1;
    }

    public void setNHICustomerAddress1(Object nHICustomerAddress1) {
        this.nHICustomerAddress1 = nHICustomerAddress1;
    }

    public Object getNHICustomerAddress2() {
        return nHICustomerAddress2;
    }

    public void setNHICustomerAddress2(Object nHICustomerAddress2) {
        this.nHICustomerAddress2 = nHICustomerAddress2;
    }

    public Object getNHICustomerEmail() {
        return nHICustomerEmail;
    }

    public void setNHICustomerEmail(Object nHICustomerEmail) {
        this.nHICustomerEmail = nHICustomerEmail;
    }

    public Object getNHICustomerMobile() {
        return nHICustomerMobile;
    }

    public void setNHICustomerMobile(Object nHICustomerMobile) {
        this.nHICustomerMobile = nHICustomerMobile;
    }

    public Integer getPaymentModeID() {
        return paymentModeID;
    }

    public void setPaymentModeID(Integer paymentModeID) {
        this.paymentModeID = paymentModeID;
    }

    public Integer getOrderReasonID() {
        return orderReasonID;
    }

    public void setOrderReasonID(Integer orderReasonID) {
        this.orderReasonID = orderReasonID;
    }

    public Object getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(Object customerGroup) {
        this.customerGroup = customerGroup;
    }

    public Integer getOrderClassfication() {
        return orderClassfication;
    }

    public void setOrderClassfication(Integer orderClassfication) {
        this.orderClassfication = orderClassfication;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Integer getRoleID() {
        return roleID;
    }

    public void setRoleID(Integer roleID) {
        this.roleID = roleID;
    }

    public Object getZFCCPayment() {
        return zFCCPayment;
    }

    public void setZFCCPayment(Object zFCCPayment) {
        this.zFCCPayment = zFCCPayment;
    }

    public Object getDiscountTypeID() {
        return discountTypeID;
    }

    public void setDiscountTypeID(Object discountTypeID) {
        this.discountTypeID = discountTypeID;
    }

    public Object getFrieghtAmount() {
        return frieghtAmount;
    }

    public void setFrieghtAmount(Object frieghtAmount) {
        this.frieghtAmount = frieghtAmount;
    }

    public Object getHeaderDiscountPer() {
        return headerDiscountPer;
    }

    public void setHeaderDiscountPer(Object headerDiscountPer) {
        this.headerDiscountPer = headerDiscountPer;
    }

    public Object getSalesEMPCode() {
        return salesEMPCode;
    }

    public void setSalesEMPCode(Object salesEMPCode) {
        this.salesEMPCode = salesEMPCode;
    }

    public Integer getCustomerGroupID() {
        return customerGroupID;
    }

    public void setCustomerGroupID(Integer customerGroupID) {
        this.customerGroupID = customerGroupID;
    }

    public Object getOrderApprovalType() {
        return orderApprovalType;
    }

    public void setOrderApprovalType(Object orderApprovalType) {
        this.orderApprovalType = orderApprovalType;
    }

    public Object getUploadImages() {
        return uploadImages;
    }

    public void setUploadImages(Object uploadImages) {
        this.uploadImages = uploadImages;
    }

    public Object getSite() {
        return site;
    }

    public void setSite(Object site) {
        this.site = site;
    }

    public Object getSOStatus() {
        return sOStatus;
    }

    public void setSOStatus(Object sOStatus) {
        this.sOStatus = sOStatus;
    }

    public Integer getWorkfloWTransactionId() {
        return workfloWTransactionId;
    }

    public void setWorkfloWTransactionId(Integer workfloWTransactionId) {
        this.workfloWTransactionId = workfloWTransactionId;
    }

    public Integer getWorkFlowTypeId() {
        return workFlowTypeId;
    }

    public void setWorkFlowTypeId(Integer workFlowTypeId) {
        this.workFlowTypeId = workFlowTypeId;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Integer getCustomizedTypes() {
        return customizedTypes;
    }

    public void setCustomizedTypes(Integer customizedTypes) {
        this.customizedTypes = customizedTypes;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public Integer getCustomerGroup5ID() {
        return customerGroup5ID;
    }

    public void setCustomerGroup5ID(Integer customerGroup5ID) {
        this.customerGroup5ID = customerGroup5ID;
    }

    public Object getCustomerGroup5Code() {
        return customerGroup5Code;
    }

    public void setCustomerGroup5Code(Object customerGroup5Code) {
        this.customerGroup5Code = customerGroup5Code;
    }

    public Object getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Object deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Object getMaxDetailsDiscount() {
        return maxDetailsDiscount;
    }

    public void setMaxDetailsDiscount(Object maxDetailsDiscount) {
        this.maxDetailsDiscount = maxDetailsDiscount;
    }

    public Object getDiscountApprovedName() {
        return discountApprovedName;
    }

    public void setDiscountApprovedName(Object discountApprovedName) {
        this.discountApprovedName = discountApprovedName;
    }

    public Integer getDiscountApprovedID() {
        return discountApprovedID;
    }

    public void setDiscountApprovedID(Integer discountApprovedID) {
        this.discountApprovedID = discountApprovedID;
    }

    public Object getPOCopyPath() {
        return pOCopyPath;
    }

    public void setPOCopyPath(Object pOCopyPath) {
        this.pOCopyPath = pOCopyPath;
    }
}
