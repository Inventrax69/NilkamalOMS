package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;
/**
 * Created by karthik.m on 05/31/2018.
 */

public class OMSCoreAuthentication {
    @SerializedName("AuthKey")
    private String authKey;

    @SerializedName("UserID")
    private String userId;

    @SerializedName("AuthValue")
    private String authValue;

    @SerializedName("LoginTimeStamp")
    private String loginTimeStamp;

    @SerializedName("AuthToken")
    private String authToken;

    @SerializedName("RequestNumber")
    private int requestNumber;

    @SerializedName("SSOUSerID")
    private int SSOUSerID;

    @SerializedName("CookieIdentifier")
    private String cookieIdentifier;

    @SerializedName("LoggedInUserID")
    private String loggedInUserID;

    @SerializedName("TransactionUserID")
    private String transactionUserID;








    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthValue() {
        return authValue;
    }

    public void setAuthValue(String authValue) {
        this.authValue = authValue;
    }

    public String getLoginTimeStamp() {
        return loginTimeStamp;
    }

    public void setLoginTimeStamp(String loginTimeStamp) {
        this.loginTimeStamp = loginTimeStamp;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public int getSSOUSerID() {
        return SSOUSerID;
    }

    public void setSSOUSerID(int SSOUSerID) {
        this.SSOUSerID = SSOUSerID;
    }

    public String getCookieIdentifier() {
        return cookieIdentifier;
    }

    public void setCookieIdentifier(String cookieIdentifier) {
        this.cookieIdentifier = cookieIdentifier;
    }

    public String getLoggedInUserID() {
        return loggedInUserID;
    }

    public void setLoggedInUserID(String loggedInUserID) {
        this.loggedInUserID = loggedInUserID;
    }

    public String getTransactionUserID() {
        return transactionUserID;
    }

    public void setTransactionUserID(String transactionUserID) {
        this.transactionUserID = transactionUserID;
    }
}