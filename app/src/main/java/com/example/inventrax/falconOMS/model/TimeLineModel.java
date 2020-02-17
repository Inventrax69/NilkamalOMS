package com.example.inventrax.falconOMS.model;


import com.google.gson.annotations.SerializedName;

public class TimeLineModel {

    @SerializedName("message")
    public String message;

    @SerializedName("date")
    public String date;

    @SerializedName("status")
    public OrderStatus status;

    public TimeLineModel(String item_successfully_delivered, String s, OrderStatus inactive) {

        setMessage(item_successfully_delivered);
        setDate(s);
        setStatus(inactive);

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
