package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class CartProcessDTO {

    @SerializedName("CartHeaderID")
    private int CartHeaderID;

    @SerializedName("IsProcessID")
    private int ISProceessID;


    public CartProcessDTO() {

    }


    public int getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(int cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }


    public int getISProceessID() {
        return ISProceessID;
    }

    public void setISProceessID(int ISProceessID) {
        this.ISProceessID = ISProceessID;
    }
}
