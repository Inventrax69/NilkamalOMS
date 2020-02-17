package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

public class NotificationDTO {

    @SerializedName("NotifRequestID")
    private int NotifRequestID;

    @SerializedName("NotifTriggerName")
    private String NotifTriggerName;

    @SerializedName("NotifTriggerDescription")
    private String NotifTriggerDescription;

    @SerializedName("CreatedOn")
    private String CreatedOn;

    @SerializedName("Reference")
    private String Reference;

    @SerializedName("Link")
    private String Link;

    public NotificationDTO(){

    }


    public int getNotifRequestID() {
        return NotifRequestID;
    }

    public void setNotifRequestID(int notifRequestID) {
        NotifRequestID = notifRequestID;
    }

    public String getNotifTriggerName() {
        return NotifTriggerName;
    }

    public void setNotifTriggerName(String notifTriggerName) {
        NotifTriggerName = notifTriggerName;
    }

    public String getNotifTriggerDescription() {
        return NotifTriggerDescription;
    }

    public void setNotifTriggerDescription(String notifTriggerDescription) {
        NotifTriggerDescription = notifTriggerDescription;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String reference) {
        Reference = reference;
    }


}
