package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomerListDTO implements Serializable {

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

    @SerializedName("Results")
    private List<CustomerListDTO> results;

    @SerializedName("CreatedOn")
    private String createdOn;

    @SerializedName("Action")
    private String action;

    @SerializedName("Prefix")
    private String Prefix;

    @SerializedName("Type")
    private String Type;


    public CustomerListDTO(){

    }


    public CustomerListDTO(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {

                case "CustomerName":
                    if (entry.getValue() != null) {
                        this.setCustomerName(entry.getValue().toString());
                    }
                    break;
                case "CustomerID":
                    if (entry.getValue() != null) {
                        this.setCustomerID(entry.getValue().toString());
                    }
                    break;

                case "CustomerType":
                    if (entry.getValue() != null) {
                        this.setCustomerType(entry.getValue().toString());
                    }
                    break;

                case "CustomerTypeID":
                    if (entry.getValue() != null) {
                        this.setCustomerTypeID(entry.getValue().toString());
                    }
                    break;


                case "CustomerCode":
                    if (entry.getValue() != null) {
                        this.setCustomerCode(entry.getValue().toString());
                    }
                    break;
                case "PrimaryID":
                    if (entry.getValue() != null) {
                        this.setPrimaryID(entry.getValue().toString());
                    }
                    break;
                case "Zone":
                    if (entry.getValue() != null) {
                        this.setZone(entry.getValue().toString());
                    }
                    break;
                case "SalesDistrict":
                    if (entry.getValue() != null) {
                        this.setSalesDistrict(entry.getValue().toString());
                    }
                    break;
                case "Division":
                    if (entry.getValue() != null) {
                        this.setDivision(entry.getValue().toString());
                    }
                    break;

                case "DivisionID":
                    if (entry.getValue() != null) {
                        this.setDivisionID(entry.getValue().toString());
                    }
                    break;
                case "ConnectedDepot":
                    if (entry.getValue() != null) {
                        this.setConnectedDepot(entry.getValue().toString());
                    }
                    break;
                case "Mobile":
                    if (entry.getValue() != null) {
                        this.setMobile(entry.getValue().toString());
                    }
                    break;

                case   "Results":
                    if(entry.getValue()!=null) {
                        List<LinkedTreeMap<?, ?>> treemapList = (List<LinkedTreeMap<?, ?>>) entry.getValue();
                        List<CustomerListDTO> lst = new ArrayList<CustomerListDTO>();
                        for (int i = 0; i < treemapList.size(); i++) {
                            CustomerListDTO dto = new CustomerListDTO(treemapList.get(i).entrySet());
                            lst.add(dto);
                        }

                        this.setResults(lst);
                    }
                    break;

                case "CreatedOn":
                    if (entry.getValue() != null) {
                        this.setCreatedOn(entry.getValue().toString());
                    }
                    break;

                 case "Action":
                    if (entry.getValue() != null) {
                        this.setAction(entry.getValue().toString());
                    }
                    break;



            }

        }


    }


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

    public List<CustomerListDTO> getResults() {
        return results;
    }

    public void setResults(List<CustomerListDTO> results) {
        this.results = results;
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

    public String getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(String divisionID) {
        DivisionID = divisionID;
    }

    public String getCustomerTypeID() {
        return CustomerTypeID;
    }

    public void setCustomerTypeID(String customerTypeID) {
        CustomerTypeID = customerTypeID;
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
}
