package com.example.inventrax.falconOMS.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.pojos.LoginDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreAuthentication;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.AndroidUtils;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.ValidationUtils;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivty extends AppCompatActivity {

    private static final String classCode = "OMS_Android_Activity_Forgot";
    EditText etEmail;
    RelativeLayout main_relative, login_tool_relative;
    private OMSCoreMessage core;
    private ErrorMessages errorMessages = new ErrorMessages();
    private Common common = new Common();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        main_relative = (RelativeLayout) findViewById(R.id.main_relative);
        login_tool_relative = (RelativeLayout) findViewById(R.id.login_tool_relative);
        etEmail = (EditText) findViewById(R.id.etEmail);
        core = new OMSCoreMessage();

        findViewById(R.id.btnSentToMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidationUtils.isValidEmail(etEmail.getText().toString())) {
                    etEmail.setError(null);
                    etEmail.clearFocus();
                    forgotPassword();
                } else {
                    etEmail.setError(errorMessages.EMC_0008);
                }
            }
        });

        findViewById(R.id.tvBacktoLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivty.this, LoginActivity.class));
                finish();
            }
        });

        setKeyboardListener();


    }

    private int heightDiff;
    private boolean wasOpened;
    private final int DefaultKeyboardDP = 100;
    // Lollipop includes button bar in the root. Add height of button bar (48dp) to maxDiff
    private final int EstimatedKeyboardDP = DefaultKeyboardDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);

    public final void setKeyboardListener() {

        final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private final Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                // Convert the dp to pixels.
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, activityRootView.getResources().getDisplayMetrics());

                // Conclude whether the keyboard is shown or not.
                activityRootView.getWindowVisibleDisplayFrame(r);
                heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == wasOpened) {

                    return;
                }

                wasOpened = isShown;
                if (isShown) {
                    main_relative.setVisibility(View.GONE);
                    login_tool_relative.setVisibility(View.VISIBLE);
                    findViewById(R.id.lblForget).setVisibility(View.INVISIBLE);
                } else {
                    login_tool_relative.setVisibility(View.GONE);
                    main_relative.setVisibility(View.VISIBLE);
                    findViewById(R.id.lblForget).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // Method to generate email to retrive password
    public void forgotPassword() {

        if (NetworkUtils.isInternetAvailable(this)) {
        } else {
            DialogUtils.showAlertDialog(this, errorMessages.EMC_0007);
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        OMSCoreAuthentication token = new OMSCoreAuthentication();
        LoginDTO loginDTO = new LoginDTO();
        token.setAuthKey("asdasd");
        token.setUserId("0");
        token.setAuthValue("asdasd");
        token.setLoginTimeStamp("string");
        token.setAuthToken("asdasdasd");
        token.setRequestNumber(0);
        token.setCookieIdentifier("adada");
        token.setSSOUSerID(0);
        token.setLoggedInUserID("asd");
        token.setTransactionUserID("asd");
        message.setType(EndpointConstants.LoginDTO);
        message.setAuthToken(token);
        loginDTO.setUserID("suresh");
        loginDTO.seteMail(etEmail.getText().toString());
        loginDTO.setUserIP(AndroidUtils.getIPAddress(true));
        loginDTO.setClientSessionID("dflgkjdfsg");
        loginDTO.setClientMAC("asdasd");
        loginDTO.setSessionIdentifier("asdasdsad");
        loginDTO.setClientParams("");
        loginDTO.setUserName("Suresh");
        message.setEntityObject(loginDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.ForgotPassword(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();

                    if (response.body() != null) {

                        core = response.body();


                        if ((core.getType().toString().equals("Exception"))) {
                            List<OMSExceptionMessage> _lExceptions = new ArrayList<OMSExceptionMessage>();
                            _lExceptions = core.getOMSMessages();

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, ForgotPasswordActivty.this, getApplicationContext());
                            }


                        } else {

                            ProgressDialogUtils.closeProgressDialog();

                            try {

                                LinkedTreeMap<String, String> _lstLogindetails = new LinkedTreeMap<String, String>();
                                _lstLogindetails = (LinkedTreeMap<String, String>) core.getEntityObject();
                                if (_lstLogindetails != null) {
                                    for (Map.Entry<String, String> entry : _lstLogindetails.entrySet()) {

                                        if (entry.getKey().equals("Result")) {
                                            common.setIsPopupActive(true);
                                            DialogUtils.showAlertDialog(ForgotPasswordActivty.this, "Success", entry.getValue(), R.drawable.success, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            common.setIsPopupActive(false);
                                                            startActivity(new Intent(ForgotPasswordActivty.this, LoginActivity.class));
                                                            break;
                                                    }
                                                }
                                            });

                                            return;

                                        }
                                    }
                                }
                            }catch (Exception ex){
                                ProgressDialogUtils.closeProgressDialog();
                                Toast.makeText(ForgotPasswordActivty.this,  ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if(NetworkUtils.isInternetAvailable(ForgotPasswordActivty.this)){
                        DialogUtils.showAlertDialog(ForgotPasswordActivty.this, errorMessages.EMC_0001);
                    }else{
                        DialogUtils.showAlertDialog(ForgotPasswordActivty.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getApplicationContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(ForgotPasswordActivty.this, errorMessages.EMC_0003);
        }
    }
}
