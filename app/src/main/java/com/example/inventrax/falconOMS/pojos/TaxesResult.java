package com.example.inventrax.falconOMS.pojos;

import java.io.Serializable;

public class TaxesResult implements Serializable {

    public String totalPrice;
    public String totalPriceWithTax;
    public String taxes;

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalPriceWithTax() {
        return totalPriceWithTax;
    }

    public void setTotalPriceWithTax(String totalPriceWithTax) {
        this.totalPriceWithTax = totalPriceWithTax;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }


}
