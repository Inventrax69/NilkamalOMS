package com.example.inventrax.falconOMS.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.ExceptionDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreAuthentication;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.AndroidUtils;
import com.example.inventrax.falconOMS.util.DateUtils;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SoundUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Prasanna.ch on 06/14/2018.
 */

public class Common {
    private OMSCoreMessage core;
    private Gson gson;
    String userId = null;

    private SoundUtils soundUtils;
    public static boolean isPopupActive() {
        return isPopupActive;
    }

    public static void setIsPopupActive(boolean isPopupActive) {
        Common.isPopupActive = isPopupActive;
    }

    public static boolean isPopupActive;

    // commom Authentication Object
    public OMSCoreMessage SetAuthentication(EndpointConstants Constant, Context context) {

        SharedPreferences sp = context.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID,"");
        OMSCoreMessage message = new OMSCoreMessage();
        OMSCoreAuthentication token = new OMSCoreAuthentication();
        token.setAuthKey(AndroidUtils.getDeviceSerialNumber().toString());
        token.setUserId(userId);
        token.setAuthValue("");
        token.setLoginTimeStamp(DateUtils.getTimeStamp().toString());
        token.setAuthToken("");
        token.setRequestNumber(1);
        token.setCookieIdentifier("adada");
        token.setSSOUSerID(0);
        token.setLoggedInUserID(userId);
        token.setTransactionUserID(userId);
        message.setType(Constant);
        message.setAuthToken(token);

        return message;

    }




    public void showAlertType(OMSExceptionMessage omsExceptionMessage, Activity activity, Context context) {

        soundUtils = new SoundUtils();
        if (omsExceptionMessage.isShowAsCriticalError()) {
            setIsPopupActive(true);
            soundUtils.alertCriticalError(activity, context);
            DialogUtils.showAlertDialog(activity, "Critical Error", omsExceptionMessage.getOMSMessage().toString(), R.drawable.link_break, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            setIsPopupActive(false);
                            break;
                    }
                }
            });

            return;
        }
        if (omsExceptionMessage.isShowAsError()) {
            setIsPopupActive(true);
            soundUtils.alertError(activity, context);
            DialogUtils.showAlertDialog(activity, "Error", omsExceptionMessage.getOMSMessage().toString(), R.drawable.cross_circle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            setIsPopupActive(false);
                            break;
                    }
                }
            });

            return;
        }
        if (omsExceptionMessage.isShowAsWarning()) {
            setIsPopupActive(true);
            soundUtils.alertWarning(activity, context);
            DialogUtils.showAlertDialog(activity, "Warning", omsExceptionMessage.getOMSMessage().toString(), R.drawable.warning_img,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            setIsPopupActive(false);
                            break;
                    }
                }
            });

            return;
        }
        if (omsExceptionMessage.isShowAsSuccess()) {
            setIsPopupActive(true);
            soundUtils.alertSuccess(activity, context);
            DialogUtils.showAlertDialog(activity, "Success", omsExceptionMessage.getOMSMessage().toString(), R.drawable.success,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            setIsPopupActive(false);
                            break;
                    }
                }
            });
            return;
        }

    }




    public void showUserDefinedAlertType(String errorCode, Activity activity, Context context,String alerttype) {

        soundUtils = new SoundUtils();
        if (alerttype.equalsIgnoreCase("Critical Error")) {
            setIsPopupActive(true);
            soundUtils.alertCriticalError(activity, context);
            DialogUtils.showAlertDialog(activity, "Critical Error", errorCode, R.drawable.link_break, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            setIsPopupActive(false);
                            break;
                    }
                }
            });

            return;
        }
        if (alerttype.equalsIgnoreCase("Error")) {
            setIsPopupActive(true);
            soundUtils.alertError(activity, context);
            DialogUtils.showAlertDialog(activity, "Error", errorCode, R.drawable.cross_circle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            setIsPopupActive(false);
                            break;
                    }
                }
            });

            return;
        }
        if (alerttype.equalsIgnoreCase("Warning")) {
            setIsPopupActive(true);
            soundUtils.alertWarning(activity, context);
            DialogUtils.showAlertDialog(activity, "Warning", errorCode, R.drawable.warning_img,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            setIsPopupActive(false);
                            break;
                    }
                }
            });

            return;
        }
        if (alerttype.equalsIgnoreCase("Success")) {
            setIsPopupActive(true);
            soundUtils.alertSuccess(activity, context);
            DialogUtils.showAlertDialog(activity, "Success", errorCode, R.drawable.success,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            setIsPopupActive(false);
                            break;
                    }
                }
            });
            return;
        }

    }


    // sending exception to the database
    public void logException(String activityOrFragment,String methodName,String exception,Activity activity) {
        try {

            String exceptionString = activityOrFragment + methodName + exception;

            OMSCoreMessage message = new OMSCoreMessage();
            message = this.SetAuthentication(EndpointConstants.Exception, activity);
            ExceptionDTO wmsExceptionMessage = new ExceptionDTO();
            wmsExceptionMessage.setSource("Mobile");
            wmsExceptionMessage.setPage(activityOrFragment);
            wmsExceptionMessage.setExceptionMessage(exceptionString);
            wmsExceptionMessage.setAction(methodName);
            message.setEntityObject(wmsExceptionMessage);

            Call<String> call = null;
            ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

            try {
                //Checking for Internet Connectivity
                // if (NetworkUtils.isInternetAvailable()) {
                // Calling the Interface method
                call = apiService.LogException(message);
                // } else {
                // DialogUtils.showAlertDialog(getActivity(), "Please enable internet");
                // return;
                // }

            } catch (Exception ex) {
                ProgressDialogUtils.closeProgressDialog();
            }
            try {
                //Getting response from the method
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        try {

                            core = gson.fromJson(response.body().toString(), OMSCoreMessage.class);

                            // if any Exception throws
                            if ((core.getType().toString().equals("Exception"))) {

                                Log.v("EXCEPTION","Exception Occurred");

                            } else {
                                LinkedTreeMap<String, String> _lResultvalue = new LinkedTreeMap<String, String>();
                                _lResultvalue = (LinkedTreeMap<String, String>) core.getEntityObject();
                                for (Map.Entry<String, String> entry : _lResultvalue.entrySet()) {
                                    if (entry.getKey().equals("Result")) {
                                        String Result = entry.getValue();
                                        if (Result.equals("0")) {
                                            return;
                                        } else {
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {

                            ProgressDialogUtils.closeProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {
                        ProgressDialogUtils.closeProgressDialog();

                    }
                });
            } catch (Exception ex) {
                ProgressDialogUtils.closeProgressDialog();
            }
        } catch (Exception ex) {
            ProgressDialogUtils.closeProgressDialog();
        }
    }
}