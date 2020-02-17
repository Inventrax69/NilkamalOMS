package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class CartHeaderListDTO implements Serializable,Cloneable  {

    @SerializedName("CustomerName")
    private String CustomerName;

/*  @SerializedName("CartHeaderID")
    private int CartHeaderID;*/

    @SerializedName("CartHeaderID")
    private int CartHeaderID;

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



    @SerializedName("IsOpenPrice")
    private int IsOpenPrice;

    @SerializedName("CustomerID")
    private int CustomerID;

    @SerializedName("Result")
    private String Result;

    @SerializedName("CreatedOn")
    private String CreatedOn;

    @SerializedName("isPriority")
    private String isPriority;

    @SerializedName("PartnerCode")
    private String PartnerCode;

    @SerializedName("PartnerName")
    private String PartnerName;

    @SerializedName("CartRef")
    private String CartRef;

    @SerializedName("TotalPrice")
    private String TotalPrice;

    @SerializedName("TotalPriceWithTax")
    private String TotalPriceWithTax;

    @SerializedName("IsStockNotAvailable")
    private String IsStockNotAvailable;

    private String ShipToPartyID;

    @SerializedName("DeliveryDate")
    private List<DeliveryDateDTO> DeliveryDate;


    HashMap<String,List<CartDetailsListDTO>> offerCartDetailsDTOList;

    public CartHeaderListDTO() {
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public int getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(int cartHeaderID) {
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

    public List<DeliveryDateDTO> getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(List<DeliveryDateDTO> deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public String getPartnerCode() {
        return PartnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        PartnerCode = partnerCode;
    }

    public String getPartnerName() {
        return PartnerName;
    }

    public void setPartnerName(String partnerName) {
        PartnerName = partnerName;
    }

    public String getCartRef() {
        return CartRef;
    }

    public void setCartRef(String cartRef) {
        CartRef = cartRef;
    }

    public String getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getTotalPriceWithTax() {
        return TotalPriceWithTax;
    }

    public void setTotalPriceWithTax(String totalPriceWithTax) {
        TotalPriceWithTax = totalPriceWithTax;
    }

    public String getIsStockNotAvailable() {
        return IsStockNotAvailable;
    }

    public void setIsStockNotAvailable(String isStockNotAvailable) {
        IsStockNotAvailable = isStockNotAvailable;
    }

    public int getIsOpenPrice() {
        return IsOpenPrice;
    }

    public void setIsOpenPrice(int isOpenPrice) {
        IsOpenPrice = isOpenPrice;
    }


    public HashMap<String, List<CartDetailsListDTO>> getOfferCartDetailsDTOList() {
        return offerCartDetailsDTOList;
    }

    public void setOfferCartDetailsDTOList(HashMap<String, List<CartDetailsListDTO>> offerCartDetailsDTOList) {
        this.offerCartDetailsDTOList = offerCartDetailsDTOList;
    }




}
