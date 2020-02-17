package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class productCatalogs {

    @SerializedName("SearchString")
    private String SearchString;

    @SerializedName("CartHeaderID")
    private int CartHeaderID;

    @SerializedName("CartDetailsID")
    private String CartDetailsID;

    @SerializedName("UserID")
    private String UserID;

    @SerializedName("MaterialMasterID")
    private String MaterialMasterID;

    @SerializedName("MCode")
    private String MCode;

    @SerializedName("Quantity")
    private String Quantity;

    @SerializedName("ImagePath")
    private String ImagePath;

    @SerializedName("Price")
    private String Price;

    @SerializedName("DeliveryDate")
    private String DeliveryDate;


    @SerializedName("CustomerID")
    private String CustomerID;


    @SerializedName("PageIndex")
    private int pageIndex;

    @SerializedName("PageSize")
    private int pageSize;

    @SerializedName("PageCount")
    private Double pageCount;

    @SerializedName("IsHandheldRequest")
    private Boolean isHandheldRequest;

    @SerializedName("productCatalogs")
    private List productCatalogs;

    @SerializedName("ShipToPartyCustomerID")
    private String ShipToPartyCustomerID;


    @SerializedName("MaterialPriorityID")
    private String MaterialPriorityID;

    @SerializedName("Results")
    private String Results;


    public productCatalogs() {

    }

    public productCatalogs(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {

                case "PageCount":
                    if (entry.getValue() != null) {
                        this.setPageCount(Double.parseDouble(entry.getValue().toString()));
                    }
                    break;
            }
        }
    }


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Double getPageCount() {
        return pageCount;
    }

    public void setPageCount(Double pageCount) {
        this.pageCount = pageCount;
    }

    public Boolean getHandheldRequest() {
        return isHandheldRequest;
    }

    public void setHandheldRequest(Boolean handheldRequest) {
        isHandheldRequest = handheldRequest;
    }

    public String getSearchString() {
        return SearchString;
    }

    public void setSearchString(String searchString) {
        SearchString = searchString;
    }

    public int getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(int cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
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

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }


    public String getCartDetailsID() {
        return CartDetailsID;
    }

    public void setCartDetailsID(String cartDetailsID) {
        CartDetailsID = cartDetailsID;
    }

    public String getShipToPartyCustomerID() {
        return ShipToPartyCustomerID;
    }

    public void setShipToPartyCustomerID(String shipToPartyCustomerID) {
        ShipToPartyCustomerID = shipToPartyCustomerID;
    }

    public List getProductCatalogs() {
        return productCatalogs;
    }

    public void setProductCatalogs(List productCatalogs) {
        this.productCatalogs = productCatalogs;
    }


    public String getMaterialPriorityID() {
        return MaterialPriorityID;
    }

    public void setMaterialPriorityID(String materialPriorityID) {
        MaterialPriorityID = materialPriorityID;
    }

    public String getResults() {
        return Results;
    }

    public void setResults(String results) {
        Results = results;
    }
}
