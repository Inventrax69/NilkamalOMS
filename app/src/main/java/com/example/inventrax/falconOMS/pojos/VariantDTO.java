package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Set;

public class VariantDTO {

    @SerializedName("MaterialImgPath")
    private String MaterialImgPath;

    @SerializedName("ModelColor")
    private String ModelColor;

    @SerializedName("MDescriptionLong")
    private String MDescriptionLong;

    @SerializedName("MDescription")
    private String MDescription;

    @SerializedName("Mcode")
    private String Mcode;

    @SerializedName("MaterialID")
    private String MaterialID;


    @SerializedName("CreatedOn")
    private String createdOn;

    @SerializedName("Action")
    private String action;


    public VariantDTO() {

    }


    public VariantDTO(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {


                case "MaterialImgPath":
                    if (entry.getValue() != null) {
                        this.setMaterialImgPath(entry.getValue().toString());
                    }
                    break;
                case "ModelColor":
                    if (entry.getValue() != null) {
                        this.setModelColor(entry.getValue().toString());
                    }
                    break;
                case "MDescriptionLong":
                    if (entry.getValue() != null) {
                        this.setMDescriptionLong(entry.getValue().toString());
                    }
                    break;
                case "MDescription":
                    if (entry.getValue() != null) {
                        this.setMDescription(entry.getValue().toString());
                    }
                    break;

                case "MaterialID":
                    if (entry.getValue() != null) {
                        this.setMaterialID(entry.getValue().toString());
                    }
                    break;
                case "Mcode":
                    if (entry.getValue() != null) {
                        this.setMcode(entry.getValue().toString());
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


            }

        }


    }

    public String getMaterialImgPath() {
        return MaterialImgPath;
    }

    public void setMaterialImgPath(String materialImgPath) {
        MaterialImgPath = materialImgPath;
    }

    public String getModelColor() {
        return ModelColor;
    }

    public void setModelColor(String modelColor) {
        ModelColor = modelColor;
    }

    public String getMDescriptionLong() {
        return MDescriptionLong;
    }

    public void setMDescriptionLong(String MDescriptionLong) {
        this.MDescriptionLong = MDescriptionLong;
    }

    public String getMDescription() {
        return MDescription;
    }

    public void setMDescription(String MDescription) {
        this.MDescription = MDescription;
    }

    public String getMcode() {
        return Mcode;
    }

    public void setMcode(String mcode) {
        Mcode = mcode;
    }

    public String getMaterialID() {
        return MaterialID;
    }

    public void setMaterialID(String materialID) {
        MaterialID = materialID;
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
}
