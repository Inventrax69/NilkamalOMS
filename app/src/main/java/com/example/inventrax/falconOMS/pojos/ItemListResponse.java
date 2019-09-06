package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Set;

public class ItemListResponse {


    @SerializedName("MaterialCode")
    private String materialCode;

    @SerializedName("MaterialDescription")
    private String materialDescription;

    @SerializedName("MaterialType")
    private String materialType;

    @SerializedName("MaterialGroup")
    private String materialGroup;

    @SerializedName("MaterialID")
    private String materialID;

    @SerializedName("MaterialPath")
    private String materialPath;


    public ItemListResponse() {

    }


    public ItemListResponse(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {


                case "MaterialCode":
                    if (entry.getValue() != null) {
                        this.setMaterialCode(entry.getValue().toString());
                    }
                    break;
                case "MaterialDescription":
                    if (entry.getValue() != null) {
                        this.setMaterialDescription(entry.getValue().toString());
                    }
                    break;
                case "MaterialType":
                    if (entry.getValue() != null) {
                        this.setMaterialType(entry.getValue().toString());
                    }
                    break;
                case "MaterialGroup":
                    if (entry.getValue() != null) {
                        this.setMaterialGroup(entry.getValue().toString());
                    }
                    break;
                case "MaterialID":
                    if (entry.getValue() != null) {
                        this.setMaterialGroup(entry.getValue().toString());
                    }
                    break;

                case "MaterialPath":
                    if (entry.getValue() != null) {
                        this.setMaterialPath(entry.getValue().toString());
                    }
                    break;


            }

        }


    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialGroup() {
        return materialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        this.materialGroup = materialGroup;
    }

    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

    public String getMaterialPath() {
        return materialPath;
    }

    public void setMaterialPath(String materialPath) {
        this.materialPath = materialPath;
    }
}
