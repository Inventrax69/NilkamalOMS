package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModelDTO {

    @SerializedName("ModelID")
    private String ModelID;

    @SerializedName("ModelCode")
    private String ModelCode;

    @SerializedName("ModelDescription")
    private String ModelDescription;

    @SerializedName("ImgPath")
    private String ImgPath;

    @SerializedName("VarientList")
    private List<VariantDTO> VarientList;


    @SerializedName("CreatedOn")
    private String createdOn;

    @SerializedName("Action")
    private String action;


    public ModelDTO() {

    }


    public ModelDTO(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {


                case "ModelID":
                    if (entry.getValue() != null) {
                        this.setModelID(entry.getValue().toString());
                    }
                    break;
                case "ModelCode":
                    if (entry.getValue() != null) {
                        this.setModelCode(entry.getValue().toString());
                    }
                    break;
                case "ModelDescription":
                    if (entry.getValue() != null) {
                        this.setModelDescription(entry.getValue().toString());
                    }
                    break;
                case "ImgPath":
                    if (entry.getValue() != null) {
                        this.setImgPath(entry.getValue().toString());
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


                case "VarientList":
                    if (entry.getValue() != null) {
                        List<LinkedTreeMap<?, ?>> treemapList = (List<LinkedTreeMap<?, ?>>) entry.getValue();
                        List<VariantDTO> lst = new ArrayList<VariantDTO>();
                        for (int i = 0; i < treemapList.size(); i++) {
                            VariantDTO dto = new VariantDTO(treemapList.get(i).entrySet());
                            lst.add(dto);
                        }

                        this.setVarientList(lst);
                    }
                    break;
            }

        }


    }

    public String getModelID() {
        return ModelID;
    }

    public void setModelID(String modelID) {
        ModelID = modelID;
    }

    public String getModelCode() {
        return ModelCode;
    }

    public void setModelCode(String modelCode) {
        ModelCode = modelCode;
    }

    public String getModelDescription() {
        return ModelDescription;
    }

    public void setModelDescription(String modelDescription) {
        ModelDescription = modelDescription;
    }

    public String getImgPath() {
        return ImgPath;
    }

    public void setImgPath(String imgPath) {
        ImgPath = imgPath;
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

    public List<VariantDTO> getVarientList() {
        return VarientList;
    }

    public void setVarientList(List<VariantDTO> varientList) {
        VarientList = varientList;
    }
}
