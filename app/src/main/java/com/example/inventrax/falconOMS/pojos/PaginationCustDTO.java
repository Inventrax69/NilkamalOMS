package com.example.inventrax.falconOMS.pojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaginationCustDTO {


    /*@SerializedName("PageNumber")
    @Expose
    private Integer pageNumber;
    @SerializedName("PageSize")
    @Expose
    private Integer pageSize;
    @SerializedName("PageCount")
    @Expose
    private Integer pageCount;*/
    @SerializedName("Results")
    @Expose
    private List<CustResult> results = null;


    public List<CustResult> getResults() {
        return results;
    }

    public void setResults(List<CustResult> results) {
        this.results = results;
    }
}