package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartListDTO {


    @SerializedName("CartList")
    private List<FullfilmentDTO> CartList;

    public List<FullfilmentDTO> getCartList() {
        return CartList;
    }

    public void setCartList(List<FullfilmentDTO> cartList) {
        CartList = cartList;
    }

}
