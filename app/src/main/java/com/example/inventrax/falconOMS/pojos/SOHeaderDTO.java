package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SOHeaderDTO {

    @SerializedName("OrderTypeID")
    private int OrderTypeID;

    @SerializedName("SaleOrganizationID")
    private int SaleOrganizationID;

    @SerializedName("DistributionChannelID")
    private int DistributionChannelID;

    @SerializedName("DivisionID")
    private int DivisionID;

    @SerializedName("SoldToCustomerID")
    private int SoldToCustomerID;

    @SerializedName("ShipToCustomerID")
    private int ShipToCustomerID;

    @SerializedName("PONumber")
    private String PONumber;

    @SerializedName("PODate")
    private String PODate;

    @SerializedName("Remarks")
    private String Remarks;

    @SerializedName("IncoTermsID")
    private int IncoTermsID;

    @SerializedName("PaymentsTermID")
    private int PaymentsTermID;

    @SerializedName("SOHeaderID")
    private int SOHeaderID;

    @SerializedName("SONumber")
    private String SONumber;

    @SerializedName("SODetails")
    private List<SODetails> SODetails;

    @SerializedName("SaleOrganization")
    private String SaleOrganization;

    @SerializedName("DistributionChannel")
    private String DistributionChannel;

    @SerializedName("Division")
    private String Division;

    @SerializedName("PaymentsTerm")
    private String PaymentsTerm;

    @SerializedName("IncoTerms")
    private String IncoTerms;

    @SerializedName("SoldToCustomer")
    private String SoldToCustomer;

    @SerializedName("ShipToCustomer")
    private String ShipToCustomer;

    @SerializedName("OrderType")
    private String OrderType;

    @SerializedName("SAPSONumber")
    private String SAPSONumber;



    @SerializedName("SOValue")
    private String SOValue;




    public SOHeaderDTO() {

    }

    public SOHeaderDTO(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {

                case "SAPSONumber":
                    if (entry.getValue() != null) {
                        this.setSAPSONumber(entry.getValue().toString());
                    }
                    break;

                case "SONumber":
                    if (entry.getValue() != null) {
                        this.setSONumber(entry.getValue().toString());
                    }
                    break;

               case "SOValue":
                    if (entry.getValue() != null) {
                        this.setSOValue(entry.getValue().toString());
                    }
                    break;



                case "SODetails":
                    if (entry.getValue() != null) {
                        List<LinkedTreeMap<?, ?>> treemapList = (List<LinkedTreeMap<?, ?>>) entry.getValue();
                        List<SODetails> lst = new ArrayList<SODetails>();
                        for (int i = 0; i < treemapList.size(); i++) {
                            SODetails dto = new SODetails(treemapList.get(i).entrySet());
                            lst.add(dto);
                        }

                        this.setSODetails(lst);
                    }
                    break;


            }

        }


    }

    public int getOrderTypeID() {
        return OrderTypeID;
    }

    public void setOrderTypeID(int orderTypeID) {
        OrderTypeID = orderTypeID;
    }

    public int getSaleOrganizationID() {
        return SaleOrganizationID;
    }

    public void setSaleOrganizationID(int saleOrganizationID) {
        SaleOrganizationID = saleOrganizationID;
    }

    public int getDistributionChannelID() {
        return DistributionChannelID;
    }

    public void setDistributionChannelID(int distributionChannelID) {
        DistributionChannelID = distributionChannelID;
    }

    public int getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(int divisionID) {
        DivisionID = divisionID;
    }

    public int getSoldToCustomerID() {
        return SoldToCustomerID;
    }

    public void setSoldToCustomerID(int soldToCustomerID) {
        SoldToCustomerID = soldToCustomerID;
    }

    public int getShipToCustomerID() {
        return ShipToCustomerID;
    }

    public void setShipToCustomerID(int shipToCustomerID) {
        ShipToCustomerID = shipToCustomerID;
    }

    public String getPONumber() {
        return PONumber;
    }

    public void setPONumber(String PONumber) {
        this.PONumber = PONumber;
    }

    public String getPODate() {
        return PODate;
    }

    public void setPODate(String PODate) {
        this.PODate = PODate;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public int getIncoTermsID() {
        return IncoTermsID;
    }

    public void setIncoTermsID(int incoTermsID) {
        IncoTermsID = incoTermsID;
    }

    public int getPaymentsTermID() {
        return PaymentsTermID;
    }

    public void setPaymentsTermID(int paymentsTermID) {
        PaymentsTermID = paymentsTermID;
    }

    public int getSOHeaderID() {
        return SOHeaderID;
    }

    public void setSOHeaderID(int SOHeaderID) {
        this.SOHeaderID = SOHeaderID;
    }

    public String getSONumber() {
        return SONumber;
    }

    public void setSONumber(String SONumber) {
        this.SONumber = SONumber;
    }

    public List<SODetails> getSODetails() {
        return SODetails;
    }

    public void setSODetails(List<SODetails> SODetails) {
        this.SODetails = SODetails;
    }

    public String getSaleOrganization() {
        return SaleOrganization;
    }

    public void setSaleOrganization(String saleOrganization) {
        SaleOrganization = saleOrganization;
    }

    public String getDistributionChannel() {
        return DistributionChannel;
    }

    public void setDistributionChannel(String distributionChannel) {
        DistributionChannel = distributionChannel;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String division) {
        Division = division;
    }

    public String getPaymentsTerm() {
        return PaymentsTerm;
    }

    public void setPaymentsTerm(String paymentsTerm) {
        PaymentsTerm = paymentsTerm;
    }

    public String getIncoTerms() {
        return IncoTerms;
    }

    public void setIncoTerms(String incoTerms) {
        IncoTerms = incoTerms;
    }

    public String getSoldToCustomer() {
        return SoldToCustomer;
    }

    public void setSoldToCustomer(String soldToCustomer) {
        SoldToCustomer = soldToCustomer;
    }

    public String getShipToCustomer() {
        return ShipToCustomer;
    }

    public void setShipToCustomer(String shipToCustomer) {
        ShipToCustomer = shipToCustomer;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    public String getSAPSONumber() {
        return SAPSONumber;
    }

    public void setSAPSONumber(String SAPSONumber) {
        this.SAPSONumber = SAPSONumber;
    }

    public String getSOValue() {
        return SOValue;
    }

    public void setSOValue(String SOValue) {
        this.SOValue = SOValue;
    }
}
