package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class FileUploadDTO {

    @SerializedName("base64")
    private String base64;

    @SerializedName("name")
    private String name;

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
