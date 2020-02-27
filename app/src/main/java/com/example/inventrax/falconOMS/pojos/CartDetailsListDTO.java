package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class CartDetailsListDTO {

    @SerializedName("Quantity")
    private String Quantity;

    @SerializedName("UserID")
    private String UserID;

    @SerializedName("CustomerID")
    private int CustomerID;

    @SerializedName("ExpectedDeliveryDate")
    private String ExpectedDeliveryDate;

    @SerializedName("ActualDeliveryDate")
    private String ActualDeliveryDate;

    @SerializedName("MaterialMasterID")
    private String MaterialMasterID;

    @SerializedName("MCode")
    private String MCode;

    @SerializedName("MDescription")
    private String MDescription;

    @SerializedName("MaterialImgPath")
    private String FileNames;

    @SerializedName("Price")
    private String Price;

    @SerializedName("CartHeaderID")
    private String CartHeaderID;

    @SerializedName("CartDetailsID")
    private String CartDetailsID;

    @SerializedName("IsInActive")
    private Boolean isIsInActive;

    @SerializedName("MaterialPriorityID")
    private int MaterialPriorityID;

    @SerializedName("Results")
    private String Results;

    @SerializedName("TotalPrice")
    private String TotalPrice;

    @SerializedName("OfferValue")
    private String OfferValue;

    @SerializedName("OfferItemCartDetailsID")
    private String OfferItemCartDetailsID;

    @SerializedName("DiscountID")
    private String DiscountID;

    @SerializedName("DiscountText")
    private String DiscountText;

    @SerializedName("GST")
    private String GST;

    @SerializedName("Tax")
    private String Tax;

    @SerializedName("SubTotal")
    private String SubTotal;

    @SerializedName("HSNCode")
    private String HSNCode;

    public CartDetailsListDTO() { }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public String getExpectedDeliveryDate() {
        return ExpectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        ExpectedDeliveryDate = expectedDeliveryDate;
    }

    public String getMaterialMasterID() {
        return MaterialMasterID;
    }

    public void setMaterialMasterID(String materialMasterID) {
        MaterialMasterID = materialMasterID;
    }

    public String getMCode() {
        return MCode;
    }

    public void setMCode(String MCode) {
        this.MCode = MCode;
    }

    public String getMDescription() {
        return MDescription;
    }

    public void setMDescription(String MDescription) {
        this.MDescription = MDescription;
    }

    public String getFileNames() {
        return FileNames;
    }

    public void setFileNames(String fileNames) {
        FileNames = fileNames;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getActualDeliveryDate() {
        return ActualDeliveryDate;
    }

    public void setActualDeliveryDate(String actualDeliveryDate) {
        ActualDeliveryDate = actualDeliveryDate;
    }

    public String getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(String cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }

    public String getCartDetailsID() {
        return CartDetailsID;
    }

    public void setCartDetailsID(String cartDetailsID) {
        CartDetailsID = cartDetailsID;
    }

    public Boolean getIsInActive() {
        return isIsInActive;
    }

    public void setIsInActive(Boolean isInActive) {
        isIsInActive = isInActive;
    }

    public String getResults() {
        return Results;
    }

    public void setResults(String results) {
        Results = results;
    }

    public int getMaterialPriorityID() {
        return MaterialPriorityID;
    }

    public void setMaterialPriorityID(int materialPriorityID) {
        MaterialPriorityID = materialPriorityID;
    }

    public String getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getOfferValue() {
        return OfferValue;
    }

    public void setOfferValue(String offerValue) {
        OfferValue = offerValue;
    }

    public String getOfferItemCartDetailsID() {
        return OfferItemCartDetailsID;
    }

    public void setOfferItemCartDetailsID(String offerItemCartDetailsID) {
        OfferItemCartDetailsID = offerItemCartDetailsID;
    }

    public String getDiscountID() {
        return DiscountID;
    }

    public void setDiscountID(String discountID) {
        DiscountID = discountID;
    }

    public String getDiscountText() {
        return DiscountText;
    }

    public void setDiscountText(String discountText) {
        DiscountText = discountText;
    }

    public String getGST() {
        return GST;
    }

    public void setGST(String GST) {
        this.GST = GST;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(String subTotal) {
        SubTotal = subTotal;
    }

    public String getHSNCode() {
        return HSNCode;
    }

    public void setHSNCode(String HSNCode) {
        this.HSNCode = HSNCode;
    }

}
