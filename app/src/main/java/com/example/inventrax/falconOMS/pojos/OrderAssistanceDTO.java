package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderAssistanceDTO {

    @SerializedName("PartnerID")
    private String PartnerID;
    @SerializedName("FileUpload")
    private List<FileUploadDTO> FileUpload;
    @SerializedName("PdfUpload")
    private List<FileUploadDTO> PdfUpload;



    public String getPartnerID() {
        return PartnerID;
    }

    public void setPartnerID(String partnerID) {
        PartnerID = partnerID;
    }

    public List<FileUploadDTO> getFileUpload() {
        return FileUpload;
    }

    public void setFileUpload(List<FileUploadDTO> fileUpload) {
        FileUpload = fileUpload;
    }

    public List<FileUploadDTO> getPdfUpload() {
        return PdfUpload;
    }

    public void setPdfUpload(List<FileUploadDTO> pdfUpload) {
        PdfUpload = pdfUpload;
    }
}
