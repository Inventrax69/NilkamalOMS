package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SyncDataDTO {

    @SerializedName("ItemMasters")
    private List<ModelDTO> ItemMasters;

    @SerializedName("CustomerMasters")
    private List<CustomerListDTO> CustomerMasters;

    public List<ModelDTO> getItemMasters() {
        return ItemMasters;
    }

    public void setItemMasters(List<ModelDTO> itemMasters) {
        ItemMasters = itemMasters;
    }

    public List<CustomerListDTO> getCustomerMasters() {
        return CustomerMasters;
    }

    public void setCustomerMasters(List<CustomerListDTO> customerMasters) {
        CustomerMasters = customerMasters;
    }


}
