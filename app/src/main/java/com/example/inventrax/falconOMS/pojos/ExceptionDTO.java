package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class ExceptionDTO {

    @SerializedName("exceptionMessage")
    private String exceptionMessage;

    @SerializedName("Source")
    private String Source;

    @SerializedName("Page")
    private String Page;

    @SerializedName("Action")
    private String Action;

    @SerializedName("UserID")
    private String UserID;

    public ExceptionDTO(){

    }


    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getPage() {
        return Page;
    }

    public void setPage(String page) {
        Page = page;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
