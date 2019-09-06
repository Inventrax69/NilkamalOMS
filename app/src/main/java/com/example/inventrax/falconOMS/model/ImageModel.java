package com.example.inventrax.falconOMS.model;

import com.google.gson.annotations.SerializedName;

public class ImageModel {



    @SerializedName("image")
    public String image;

    public ImageModel(String image) {
        setImage(image);

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
