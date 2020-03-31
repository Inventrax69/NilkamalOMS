package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplyOffersDTO {

    @SerializedName("PageIndex")
    private int PageIndex;

    @SerializedName("PageSize")
    private int PageSize;

    @SerializedName("PageCount")
    private Double pageCount;

    @SerializedName("SearchString")
    private Object SearchString;

    @SerializedName("Filter")
    private String filter;

    @SerializedName("CustomerID")
    private String CustomerID;

    @SerializedName("Results")
    private List<Object> results;

    @SerializedName("IsHandheldRequest")
    private Boolean IsHandheldRequest;

    @SerializedName("CartHeaderID")
    private String CartHeaderID;

    @SerializedName("UserID")
    private String UserID;








    public ApplyOffersDTO() {

    }


    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int pageIndex) {
        PageIndex = pageIndex;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public Double getPageCount() {
        return pageCount;
    }

    public void setPageCount(Double pageCount) {
        this.pageCount = pageCount;
    }

    public Object getSearchString() {
        return SearchString;
    }

    public void setSearchString(Object searchString) {
        SearchString = searchString;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public List<Object> getResults() {
        return results;
    }

    public void setResults(List<Object> results) {
        this.results = results;
    }

    public Boolean getHandheldRequest() {
        return IsHandheldRequest;
    }

    public void setHandheldRequest(Boolean handheldRequest) {
        IsHandheldRequest = handheldRequest;
    }

    public String getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(String cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
