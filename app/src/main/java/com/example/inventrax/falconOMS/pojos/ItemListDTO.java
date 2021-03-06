package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemListDTO  {

    @SerializedName("PageIndex")
    private int pageIndex;

    @SerializedName("PageSize")
    private int pageSize;

    @SerializedName("PageCount")
    private Double pageCount;

    @SerializedName("Results")
    private List<ModelDTO> results;

    @SerializedName("IsHandheldRequest")
    private Boolean isHandheldRequest;

    public ItemListDTO() {

    }

    public ItemListDTO(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {

                case "PageCount":
                    if (entry.getValue() != null) {
                        this.setPageCount(Double.parseDouble(entry.getValue().toString()));
                    }
                    break;


                case "Results":
                    if (entry.getValue() != null) {
                        List<LinkedTreeMap<?, ?>> treemapList = (List<LinkedTreeMap<?, ?>>) entry.getValue();
                        List<ModelDTO> lst = new ArrayList<ModelDTO>();
                        for (int i = 0; i < treemapList.size(); i++) {
                            ModelDTO dto = new ModelDTO(treemapList.get(i).entrySet());
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

    public List<ModelDTO> getResults() {
        return results;
    }

    public void setResults(List<ModelDTO> results) {
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
}
