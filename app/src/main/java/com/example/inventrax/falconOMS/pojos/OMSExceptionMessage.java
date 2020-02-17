package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by karthik.m on 06/19/2018.
 */

public class OMSExceptionMessage {
    @SerializedName("OMSMessage")
    private String OMSMessage;
    @SerializedName("OMSExceptionCode ")
    private String OMSExceptionCode;
    @SerializedName("ShowAsError")
    private boolean ShowAsError;
    @SerializedName("ShowAsWarning")
    private boolean ShowAsWarning;
    @SerializedName("ShowAsSuccess")
    private boolean ShowAsSuccess;
    @SerializedName("ShowAsCriticalError")
    private boolean ShowAsCriticalError;
    @SerializedName("ShowUserConfirmDialogue")
    private boolean ShowUserConfirmDialogue;
    @SerializedName("LogExceptionToDevTeam")
    private boolean logExceptionToDevTeam;
    @SerializedName("Message")
    private  String message;
    @SerializedName("Data")
    private Object data;
    @SerializedName("InnerException")
    private Object innerException;
    @SerializedName("StackTrace")
    private  String stackTrace;
    @SerializedName("HelpLink")
    private  String HelpLink;
    @SerializedName("Source")
    private  String Source;
    @SerializedName("HResult")
    private  int HrResult;

/*    public OMSExceptionMessage(Set<? extends Map.Entry<?, ?>> entries) {
        for(Map.Entry<?, ?> entry : entries) {

            switch (entry.getKey().toString()) {

                case "OMSMessage":
                    if (entry.getValue() != null) {
                        this.setOMSMessage(entry.getValue().toString());
                    }
                    break;
                case "ShowAsError":
                    if (entry.getValue() != null) {
                        this.setShowAsError(Boolean.parseBoolean(entry.getValue().toString()));
                    }
                    break;
                case "ShowAsWarning":
                    if (entry.getValue() != null) {
                        this.setShowAsWarning(Boolean.parseBoolean(entry.getValue().toString()));
                    }
                    break;
                case "ShowAsSuccess":
                    if (entry.getValue() != null) {
                        this.setShowAsSuccess(Boolean.parseBoolean(entry.getValue().toString()));
                    }
                    break;

                case "ShowAsCriticalError":
                    if (entry.getValue() != null) {
                        this.setShowAsCriticalError(Boolean.parseBoolean(entry.getValue().toString()));
                    }
                    break;
                case "ShowUserConfirmDialogue":
                    if (entry.getValue() != null) {
                        this.setShowUserConfirmDialogue(Boolean.parseBoolean(entry.getValue().toString()));
                    }
                    break;
                case "OMSExceptionCode":
                    if (entry.getValue() != null) {
                        this.setOMSExceptionCode(entry.getValue().toString());
                    }
                    break;

            }
        }
    }*/

    public String getOMSMessage() {
        return OMSMessage;
    }

    public void setOMSMessage(String OMSMessage) {
        this.OMSMessage = OMSMessage;
    }

    public String getOMSExceptionCode() {
        return OMSExceptionCode;
    }

    public void setOMSExceptionCode(String OMSExceptionCode) {
        this.OMSExceptionCode = OMSExceptionCode;
    }

    public boolean isShowAsError() {
        return ShowAsError;
    }

    public void setShowAsError(boolean showAsError) {
        ShowAsError = showAsError;
    }

    public boolean isShowAsWarning() {
        return ShowAsWarning;
    }

    public void setShowAsWarning(boolean showAsWarning) {
        ShowAsWarning = showAsWarning;
    }

    public boolean isShowAsSuccess() {
        return ShowAsSuccess;
    }

    public void setShowAsSuccess(boolean showAsSuccess) {
        ShowAsSuccess = showAsSuccess;
    }

    public boolean isShowAsCriticalError() {
        return ShowAsCriticalError;
    }

    public void setShowAsCriticalError(boolean showAsCriticalError) {
        ShowAsCriticalError = showAsCriticalError;
    }

    public boolean isShowUserConfirmDialogue() {
        return ShowUserConfirmDialogue;
    }

    public void setShowUserConfirmDialogue(boolean showUserConfirmDialogue) {
        ShowUserConfirmDialogue = showUserConfirmDialogue;
    }

    public boolean isLogExceptionToDevTeam() {
        return logExceptionToDevTeam;
    }

    public void setLogExceptionToDevTeam(boolean logExceptionToDevTeam) {
        this.logExceptionToDevTeam = logExceptionToDevTeam;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getInnerException() {
        return innerException;
    }

    public void setInnerException(Object innerException) {
        this.innerException = innerException;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getHelpLink() {
        return HelpLink;
    }

    public void setHelpLink(String helpLink) {
        HelpLink = helpLink;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public int getHrResult() {
        return HrResult;
    }

    public void setHrResult(int hrResult) {
        HrResult = hrResult;
    }
}
