package com.example.inventrax.falconOMS.pojos;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by karthik.m on 05/31/2018.
 */

public class OMSCoreMessage {
    @SerializedName("AuthToken")
    @Expose
    private OMSCoreAuthentication authToken;

    @SerializedName("EntityObject")
    @Expose
    private Object entityObject;

    @SerializedName("Type")
    @Expose
    private EndpointConstants type;


    @SerializedName("OMSMessages")
    @Expose
    private List<OMSExceptionMessage> OMSMessages;






    public List<OMSExceptionMessage> getOMSMessages() {
        return OMSMessages;
    }

    public void setOMSMessages(List<OMSExceptionMessage> OMSMessages) {
        this.OMSMessages = OMSMessages;
    }

    public EndpointConstants getType() {
        return type;
    }

    public void setType(EndpointConstants type) {
        this.type = type;
    }

    public OMSCoreAuthentication getAuthToken() {
        return authToken;
    }

    public void setAuthToken(OMSCoreAuthentication authToken) {
        this.authToken = authToken;
    }

    public Object getEntityObject() {
        return entityObject;
    }

    public void setEntityObject(Object entityObject) {
        this.entityObject = entityObject;
    }
}
