package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Set;

public class SODetails {

    @SerializedName("LineNumber")
    private int LineNumber;

    @SerializedName("Material")
    private String Material;

    @SerializedName("Quantity")
    private int Quantity;

    @SerializedName("ConditionTypes")
    private String ConditionTypes;

    @SerializedName("UnitPrice")
    private String UnitPrice;

    @SerializedName("MaterialMasterID")
    private int MaterialMasterID;

    @SerializedName("mDesc")
    private String mDesc;

    @SerializedName("ItemDescription")
    private String ItemDescription;


    public SODetails() {

    }


    public SODetails(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {

                case "Material":
                    if (entry.getValue() != null) {
                        this.setMaterial(entry.getValue().toString());
                    }
                    break;

                case "Quantity":
                    if (entry.getValue() != null) {
                        this.setQuantity((int) Double.parseDouble(entry.getValue().toString()));
                    }
                    break;

                case "UnitPrice":
                    if (entry.getValue() != null) {
                        this.setUnitPrice(entry.getValue().toString());
                    }
                    break;

                case "mDesc":
                    if (entry.getValue() != null) {
                        this.setmDesc(entry.getValue().toString());
                    }
                    break;

                case "ItemDescription":
                    if (entry.getValue() != null) {
                        this.setItemDescription(entry.getValue().toString());
                    }
                    break;


            }
        }
    }


    public int getLineNumber() {
        return LineNumber;
    }

    public void setLineNumber(int lineNumber) {
        LineNumber = lineNumber;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getConditionTypes() {
        return ConditionTypes;
    }

    public void setConditionTypes(String conditionTypes) {
        ConditionTypes = conditionTypes;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public int getMaterialMasterID() {
        return MaterialMasterID;
    }

    public void setMaterialMasterID(int materialMasterID) {
        MaterialMasterID = materialMasterID;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }
}
