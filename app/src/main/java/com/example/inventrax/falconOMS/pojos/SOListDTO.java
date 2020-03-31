package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class SOListDTO implements Serializable {

    @SerializedName("SOHeaderID")
    private int SOHeaderID;

    @SerializedName("Customer")
    private String Customer;

    @SerializedName("OrderDate")
    private String OrderDate;

    @SerializedName("OMSSONumber")
    private String OMSSONumber;

    @SerializedName("SOValue")
    private String SOValue;

    @SerializedName("Status")
    private String Status;


    public SOListDTO() {

    }

    public SOListDTO(Set<? extends Map.Entry<?, ?>> entrySet) {

        for (Map.Entry<?, ?> entry : entrySet) {

            switch (entry.getKey().toString()) {

                case "SOHeaderID":
                    if (entry.getValue() != null) {
                        this.setSOHeaderID((int) Double.parseDouble(entry.getValue().toString()));
                    }
                    break;
                case "Customer":
                    if (entry.getValue() != null) {
                        this.setCustomer(entry.getValue().toString());
                    }
                    break;
                case "OrderDate":
                    if (entry.getValue() != null) {
                        this.setOrderDate(entry.getValue().toString());
                    }
                    break;
                case "OMSSONumber":
                    if (entry.getValue() != null) {
                        this.setOMSSONumber(entry.getValue().toString());
                    }
                    break;

                case "SOValue":
                    if (entry.getValue() != null) {
                        this.setSOValue(entry.getValue().toString());
                    }
                    break;

                case "Status":
                    if (entry.getValue() != null) {
                        this.setStatus(entry.getValue().toString());
                    }
                    break;


            }
        }
    }


    public int getSOHeaderID() {
        return SOHeaderID;
    }

    public void setSOHeaderID(int SOHeaderID) {
        this.SOHeaderID = SOHeaderID;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getOMSSONumber() {
        return OMSSONumber;
    }

    public void setOMSSONumber(String OMSSONumber) {
        this.OMSSONumber = OMSSONumber;
    }

    public String getSOValue() {
        return SOValue;
    }

    public void setSOValue(String SOValue) {
        this.SOValue = SOValue;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
