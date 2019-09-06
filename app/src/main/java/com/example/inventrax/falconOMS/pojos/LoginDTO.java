package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by karthik.m on 05/31/2018.
 */

public class LoginDTO {

    @SerializedName("UserID")
    private String userID;
    @SerializedName("EMail")
    private String eMail;
    @SerializedName("Password")
    private String password;
    @SerializedName("UserIP")
    private String userIP;
    @SerializedName("ClientSessionID")
    private String clientSessionID;
    @SerializedName("UserName")
    private String userName;
    @SerializedName("ClientMAC")
    private String clientMAC;
    @SerializedName("SessionIdentifier")
    private String sessionIdentifier;
    @SerializedName("ClientParams")
    private String clientParams;
    @SerializedName("NewPassword")
    private String newPassword;
    @SerializedName("Result")
    private String Result;



    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public String getClientSessionID() {
        return clientSessionID;
    }

    public void setClientSessionID(String clientSessionID) {
        this.clientSessionID = clientSessionID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClientMAC() {
        return clientMAC;
    }

    public void setClientMAC(String clientMAC) {
        this.clientMAC = clientMAC;
    }

    public String getSessionIdentifier() {
        return sessionIdentifier;
    }

    public void setSessionIdentifier(String sessionIdentifier) {
        this.sessionIdentifier = sessionIdentifier;
    }

    public String getClientParams() {
        return clientParams;
    }

    public void setClientParams(String clientParams) {
        this.clientParams = clientParams;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }
}
