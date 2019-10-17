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

    @SerializedName("DivisionID")
    private String DivisionID;

    @SerializedName("SegmentID")
    private String SegmentID;

    @SerializedName("Model")
    private String Model;

    @SerializedName("ModelDesc")
    private String ModelDescription;

    @SerializedName("FileNames")
    private String ImgPath;

    @SerializedName("Price")
    private String price;

    @SerializedName("DiscountCount")
    private String discountCount;

    @SerializedName("DiscountId")
    private String discountId;

    @SerializedName("DiscountDesc")
    private String discountDesc;



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
                case "Model":
                    if (entry.getValue() != null) {
                        this.setModel(entry.getValue().toString());
                    }
                    break;
                case "ModelDesc":
                    if (entry.getValue() != null) {
                        this.setModelDescription(entry.getValue().toString());
                    }
                    break;
                case "FileNames":
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

                case "Price":
                    if (entry.getValue() != null) {
                        this.setPrice(entry.getValue().toString());
                    }
                    break;

                case "SegmentID":
                    if (entry.getValue() != null) {
                        this.setSegmentID(entry.getValue().toString());
                    }
                    break;

                case "DivisionID":
                    if (entry.getValue() != null) {
                        this.setDivisionID(entry.getValue().toString());
                    }
                    break;

                case "DiscountCount":
                    if (entry.getValue() != null) {
                        this.setDiscountCount(entry.getValue().toString());
                    }
                    break;

                case "DiscountId":
                    if (entry.getValue() != null) {
                        this.setDiscountId(entry.getValue().toString());
                    }
                    break;

                case "DiscountDesc":
                    if (entry.getValue() != null) {
                        this.setDiscountDesc(entry.getValue().toString());
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

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
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

    public String getSegmentID() {
        return SegmentID;
    }

    public void setSegmentID(String segmentID) {
        SegmentID = segmentID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(String divisionID) {
        DivisionID = divisionID;
    }

    public String getDiscountCount() {
        return discountCount;
    }

    public void setDiscountCount(String discountCount) {
        this.discountCount = discountCount;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getDiscountDesc() {
        return discountDesc;
    }

    public void setDiscountDesc(String discountDesc) {
        this.discountDesc = discountDesc;
    }
}
