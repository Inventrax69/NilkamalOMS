package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.logout.LogoutUtil;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.LoginDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.ProfileDTO;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.Encryption;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ProfileFragment";
    private View rootView;
    private ImageView ivUser;
    private TextView txtUserName, txtWebSite, txtMobile, txtBacktoProfile;
    private LinearLayout llProfile, llChangePassword;
    private TextInputLayout txtInputLayoutNewPwd, txtInputLayoutConfirmPwd;
    private EditText etPwd, etNewPwd, etConfirmPwd;
    private Button btnSubmit,btnChangePassword;
    private CoordinatorLayout coordinatorLayout;

    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;
    private Encryption encryption;
    private LogoutUtil logoutUtil;

    private String encryptedNewPwd = "";
    private String userId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        try {
            loadFormControls();
        } catch (Exception e) {

        }


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getActivity() != null){
            ((MainActivity)getActivity()).isProfileOpened=false;
        }
    }

    private void loadFormControls() {

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);

        llProfile = (LinearLayout) rootView.findViewById(R.id.llProfile);
        llChangePassword = (LinearLayout) rootView.findViewById(R.id.llChangePassword);

        SharedPreferences sp = getActivity().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");

        ivUser = (ImageView) rootView.findViewById(R.id.ivUser);

        txtUserName = (TextView) rootView.findViewById(R.id.txtUserName);
        txtWebSite = (TextView) rootView.findViewById(R.id.txtWebSite);
        txtMobile = (TextView) rootView.findViewById(R.id.txtMobile);

        txtBacktoProfile = (TextView) rootView.findViewById(R.id.txtBacktoProfile);

        etPwd = (EditText) rootView.findViewById(R.id.etPwd);
        etNewPwd = (EditText) rootView.findViewById(R.id.etNewPwd);
        etConfirmPwd = (EditText) rootView.findViewById(R.id.etConfirmPwd);
        txtInputLayoutNewPwd = (TextInputLayout) rootView.findViewById(R.id.txtInputLayoutNewPwd);
        txtInputLayoutConfirmPwd = (TextInputLayout) rootView.findViewById(R.id.txtInputLayoutConfirmPwd);

        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        btnChangePassword = (Button) rootView.findViewById(R.id.btnChangePassword);



        txtBacktoProfile.setOnClickListener(this);

        btnSubmit.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);

        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        restService = new RestService();
        core = new OMSCoreMessage();
        encryption = new Encryption();
        logoutUtil = new LogoutUtil();

        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                etNewPwd.setEnabled(true);
                etConfirmPwd.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        etConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {


                String passwrd = etNewPwd.getText().toString();

                if (!passwrd.equals("")) {
                    if (editable.length() > 0 && passwrd.length() > 0) {
                        if (etConfirmPwd.getText().toString().equals(passwrd)) {
                            // give an error that password and confirm password not match

                            txtInputLayoutConfirmPwd.setErrorEnabled(false);
                            txtInputLayoutConfirmPwd.setHintTextAppearance(R.style.text_appearance);
                            txtInputLayoutConfirmPwd.setPasswordVisibilityToggleTintList(AppCompatResources.getColorStateList(getContext(), R.color.colorAccent));
                            etNewPwd.setError(null);

                        } else {
                            txtInputLayoutConfirmPwd.setHintTextAppearance(R.style.error_appearance);
                            txtInputLayoutConfirmPwd.setPasswordVisibilityToggleTintList(AppCompatResources.getColorStateList(getContext(), R.color.red));
                            txtInputLayoutConfirmPwd.setError("Password not matched");
                        }

                    }
                } else {
                    etNewPwd.setError("Please enter new password");
                    etConfirmPwd.setEnabled(false);
                }
            }

        });

        loadProfile();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnChangePassword:

                llChangePassword.setVisibility(View.VISIBLE);
                llProfile.setVisibility(View.GONE);

                txtInputLayoutConfirmPwd.setErrorEnabled(false);
                txtInputLayoutConfirmPwd.setHintTextAppearance(R.style.text_appearance);

                break;

            case R.id.txtBacktoProfile:

                llChangePassword.setVisibility(View.GONE);
                llProfile.setVisibility(View.VISIBLE);

                etConfirmPwd.setText("");
                etPwd.setText("");
                break;

            case R.id.btnSubmit:

                if (!etPwd.getText().toString().isEmpty()) {
                    if (!etNewPwd.getText().toString().isEmpty()) {
                        if (!etConfirmPwd.getText().toString().isEmpty()) {

                            if (etNewPwd.getText().toString().equals(etConfirmPwd.getText().toString())) {

                                String newPwd = etConfirmPwd.getText().toString();

                                try {
                                    encryptedNewPwd = encryption.encryptData(newPwd);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                changePassword();

                            } else {
                                SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0010,   ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                            }
                        } else {
                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0013,  ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                        }
                    } else {
                        SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0012,  ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                    }
                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0011,   ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                }

                break;

        }
    }

    public void loadProfile() {

        if (NetworkUtils.isInternetAvailable(getContext())) { } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProfileDTO, getContext());
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserID(userId);
        message.setEntityObject(profileDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.LoadProfile(message);
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
                                common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                            }


                        } else {

                            try {


                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<String, String> _lstProfile = new LinkedTreeMap<String, String>();
                                _lstProfile = (LinkedTreeMap<String, String>) core.getEntityObject();

                                ProfileDTO profile;

                                profile = new ProfileDTO(_lstProfile.entrySet());


                                if (!profile.getEmail().equals("")) {
                                    txtWebSite.setText(profile.getEmail());
                                }
                                if (!profile.getFirstName().equals("")) {
                                    txtUserName.setText(profile.getFirstName());
                                }
                                if (!profile.getMobile().equals("")) {
                                    Log.d("mobile", profile.getMobile());
                                    txtMobile.setText(profile.getMobile());
                                }
                                if (!profile.getLastname().equals("")) {
                                    Log.d("lastName", profile.getLastname());
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception e) {

                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if(NetworkUtils.isInternetAvailable(getActivity())){
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    }else{
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                exceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    public void changePassword() {

        if (NetworkUtils.isInternetAvailable(getContext())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.LoginDTO, getContext());
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUserID(userId);
        //loginDTO.setPassword(etPwd.getText().toString());
        try {
            loginDTO.setPassword(encryption.encryptData(etPwd.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loginDTO.setNewPassword(encryptedNewPwd);
        message.setEntityObject(loginDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.ChangePassword(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();
                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {


                            if ((core.getType().toString().equals("Exception"))) {
                                List<OMSExceptionMessage> _lExceptions = new ArrayList<OMSExceptionMessage>();
                                _lExceptions = core.getOMSMessages();

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }


                            } else {

                                try {

                                    ProgressDialogUtils.closeProgressDialog();

                                    LinkedTreeMap<String, String> _lstProfile = new LinkedTreeMap<String, String>();
                                    _lstProfile = (LinkedTreeMap<String, String>) core.getEntityObject();


                                    if (_lstProfile != null) {
                                        for (Map.Entry<String, String> entry : _lstProfile.entrySet()) {

                                            if (entry.getKey().equals("Result")) {

                                                if (entry.getValue().equals("success")) {

                                                    //logoutUtil.doLogout(getActivity());

                                                    etConfirmPwd.setText("");
                                                    etNewPwd.setText("");
                                                    etPwd.setText("");

                                                    common.showUserDefinedAlertType("Password changed successfully, Please logout and login.", getActivity(), getContext(), "Success");
                                                    return;
                                                }else {
                                                    if (entry.getValue().equals("Error")) {
                                                        //logoutUtil.doLogout(getActivity());
                                                        common.showUserDefinedAlertType("Please enter valid previous password. ", getActivity(), getContext(), "Error");
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    ProgressDialogUtils.closeProgressDialog();
                                } catch (Exception e) {

                                }
                            }
                            ProgressDialogUtils.closeProgressDialog();
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if(NetworkUtils.isInternetAvailable(getActivity())){
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    }else{
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                exceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_profile));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }



}
