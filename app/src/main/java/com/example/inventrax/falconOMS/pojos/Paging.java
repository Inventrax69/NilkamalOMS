package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Paging  {

    @SerializedName("PageIndex")
    private int pageIndex;

    @SerializedName("PageSize")
    private int pageSize;

    @SerializedName("PageCount")
    private Double pageCount;

    @SerializedName("SearchString")
    private Object searchString;

    @SerializedName("Filter")
    private String filter;

    @SerializedName("CustomerID")
    private String CustomerID;

    @SerializedName("PageNumber")
    private String PageNumber;

    @SerializedName("Results")
    private List<SOListDTO> results;

    @SerializedName("IsHandheldRequest")
    private Boolean isHandheldRequest;

    public Paging() {

    }

    public Paging(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {

                case "PageCount":
                    if (entry.getValue() != null) {
                        this.setPageCount(Double.parseDouble(entry.getValue().toString()));
                    }
                    break;

                case "PageNumber":
                    if (entry.getValue() != null) {
                        this.setPageNumber(entry.getValue().toString());
                    }
                    break;


                case "Results":
                    if (entry.getValue() != null) {
                        List<LinkedTreeMap<?, ?>> treemapList = (List<LinkedTreeMap<?, ?>>) entry.getValue();
                        List<SOListDTO> lst = new ArrayList<SOListDTO>();
                        for (int i = 0; i < treemapList.size(); i++) {
                            SOListDTO dto = new SOListDTO(treemapList.get(i).entrySet());
                            lst.add(dto);
                        }

                        this.setResults(lst);
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

    public List<SOListDTO> getResults() {
        return results;
    }

    public void setResults(List<SOListDTO> results) {
        this.results = results;
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

    public Object getSearchString() {
        return searchString;
    }

    public void setSearchString(Object searchString) {
        this.searchString = searchString;
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

    public String getPageNumber() {
        return PageNumber;
    }

    public void setPageNumber(String pageNumber) {
        PageNumber = pageNumber;
    }
}
