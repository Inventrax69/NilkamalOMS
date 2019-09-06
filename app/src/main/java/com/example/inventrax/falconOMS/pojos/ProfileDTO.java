package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Set;


/**
 * Created by karthik.m on 05/31/2018.
 */

public class ProfileDTO {

    @SerializedName("Email")
    private String email    ;

    @SerializedName("DOB")
    private String DOB;

    @SerializedName("Mobile")
    private String mobile;

    @SerializedName("UserID")
    private String userID;

    @SerializedName("FirstName")
    private String firstName;

    @SerializedName("MiddleName")
    private String middleName;

    @SerializedName("Lastname")
    private String lastname;

    public ProfileDTO(){

    }


    public ProfileDTO(Set<? extends Map.Entry<?, ?>> entries)
    {

        for(Map.Entry<?, ?> entry : entries)
        {

            switch(entry.getKey().toString())
            {

                case   "Email":
                    if(entry.getValue()!=null) {
                        this.setEmail(entry.getValue().toString());
                    }
                    break;
                case   "DOB":
                    if(entry.getValue()!=null) {
                        this.setDOB(entry.getValue().toString());
                    }
                    break;
                case   "Mobile":
                    if(entry.getValue()!=null) {
                        this.setMobile(entry.getValue().toString());
                    }
                    break;
                case   "UserID":
                    if(entry.getValue()!=null) {
                        this.setUserID(entry.getValue().toString());
                    }
                    break;
                case   "FirstName":
                    if(entry.getValue()!=null) {
                        this.setFirstName(entry.getValue().toString());
                    }
                    break;

                case   "MiddleName":
                    if(entry.getValue()!=null) {
                        this.setMiddleName(entry.getValue().toString());
                    }
                    break;

                case   "Lastname":
                    if(entry.getValue()!=null) {
                        this.setLastname(entry.getValue().toString());
                    }
                    break;




            }

        }


    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
