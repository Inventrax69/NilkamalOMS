package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @SerializedName("SiteCode")
    private String SiteCode;

    @SerializedName("FullName")
    private String FullName;

    @SerializedName("Employeecode")
    private String Employeecode;

    @SerializedName("UserId")
    private String UserId;

    @SerializedName("Customers")
    private List<String> Customers;

    @SerializedName("CartHeaderID")
    private String CartHeaderID;


    public LoginDTO() {

    }

    public LoginDTO(Set<? extends Map.Entry<?, ?>> entries) {

        for (Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {

                case "UserID":
                    if (entry.getValue() != null) {
                        this.setUserID(entry.getValue().toString());
                    }
                    break;
                case "EMail":
                    if (entry.getValue() != null) {
                        this.seteMail(entry.getValue().toString());
                    }
                    break;
                case "Password":
                    if (entry.getValue() != null) {
                        this.setPassword(entry.getValue().toString());
                    }
                    break;
                case "UserIP":
                    if (entry.getValue() != null) {
                        this.setUserIP(entry.getValue().toString());
                    }
                    break;
                case "ClientSessionID":
                    if (entry.getValue() != null) {
                        this.setClientSessionID(entry.getValue().toString());
                    }
                    break;

                case "UserName":
                    if (entry.getValue() != null) {
                        this.setUserName(entry.getValue().toString());
                    }
                    break;

                case "ClientMAC":
                    if (entry.getValue() != null) {
                        this.setClientMAC(entry.getValue().toString());
                    }
                    break;

                case "SessionIdentifier":
                    if (entry.getValue() != null) {
                        this.setSessionIdentifier(entry.getValue().toString());
                    }
                    break;

                case "ClientParams":
                    if (entry.getValue() != null) {
                        this.setClientParams(entry.getValue().toString());
                    }
                    break;

                case "NewPassword":
                    if (entry.getValue() != null) {
                        this.setNewPassword(entry.getValue().toString());
                    }
                    break;

                case "Result":
                    if (entry.getValue() != null) {
                        this.setResult(entry.getValue().toString());
                    }
                    break;

                case "SiteCode":
                    if (entry.getValue() != null) {
                        this.setSiteCode(entry.getValue().toString());
                    }
                    break;

                case "FullName":
                    if (entry.getValue() != null) {
                        this.setFullName(entry.getValue().toString());
                    }
                    break;

                case "Employeecode":
                    if (entry.getValue() != null) {
                        this.setEmployeecode(entry.getValue().toString());
                    }
                    break;

                case "UserId":
                    if (entry.getValue() != null) {
                        this.setUserId(entry.getValue().toString());
                    }
                    break;

                case "CartHeaderID":
                    if (entry.getValue() != null) {
                        this.setCartHeaderID (entry.getValue().toString());
                    }
                    break;


                case "Customers":
                    if (entry.getValue() != null) {
                        this.setCustomers((List) entry.getValue());
                    }
                    break;


            }

        }


    }

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

    public String getSiteCode() {
        return SiteCode;
    }

    public void setSiteCode(String siteCode) {
        SiteCode = siteCode;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmployeecode() {
        return Employeecode;
    }

    public void setEmployeecode(String employeecode) {
        Employeecode = employeecode;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public List getCustomers() {
        return Customers;
    }

    public void setCustomers(List customers) {
        Customers = customers;
    }

    public String getCartHeaderID() {
        return CartHeaderID;
    }

    public void setCartHeaderID(String cartHeaderID) {
        CartHeaderID = cartHeaderID;
    }
}
