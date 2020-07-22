package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.SODetailsAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.SOHeaderDTO;
import com.example.inventrax.falconOMS.pojos.SOListDTO;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ComplaintsFragment";
    private View rootView;

    TextView txtOMSRefNo, txtSAPRefNo, txtCustmerName, txtSourceSite,txtSOValue;
    RecyclerView rv_SODetails;
    LinearLayoutManager layoutManager;
    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;

    private int soHeaderId;
    private String customerName = "";

    MenuItem item;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_history_details_fragment, container, false);

        try {
            loadFormControls();
        } catch (Exception e) {
            Log.d("Exd", e.toString());
        }

        return rootView;

    }

    private void loadFormControls() {

        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        restService = new RestService();
        core = new OMSCoreMessage();

        txtOMSRefNo = (TextView) rootView.findViewById(R.id.txtOMSRefNo);
        txtSAPRefNo = (TextView) rootView.findViewById(R.id.txtSAPRefNo);
        txtCustmerName = (TextView) rootView.findViewById(R.id.txtCustmerName);
        txtSOValue = (TextView) rootView.findViewById(R.id.txtSOValue);
        //txtSourceSite = (TextView) rootView.findViewById(R.id.txtSourceSite);

        rv_SODetails = (RecyclerView) rootView.findViewById(R.id.rv_SODetails);
        rv_SODetails.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        rv_SODetails.setLayoutManager(layoutManager);

        if (getArguments() != null) {
            soHeaderId = getArguments().getInt(KeyValues.SELECTED_SOHEADER);
            customerName = getArguments().getString(KeyValues.SELECTED_CUSTOMER);
        }

        GetSO();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {
            menu.clear();
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void GetSO() {

        if (NetworkUtils.isInternetAvailable(getContext())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.SOHeader_DTO, getContext());

        SOListDTO oDto = new SOListDTO();
        oDto.setSOHeaderID(soHeaderId);
        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.GetSO(message);
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

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }

                        } else {

                            try {

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                SOHeaderDTO soHeaderDTO = new SOHeaderDTO();

                                soHeaderDTO = new SOHeaderDTO(_lstItem.entrySet());

                                txtOMSRefNo.setText(soHeaderDTO.getSONumber());
                                txtSAPRefNo.setText(soHeaderDTO.getSAPSONumber());
                                txtSAPRefNo.setText(soHeaderDTO.getSAPSONumber());
                                txtSOValue.setText(soHeaderDTO.getSOValue());
                                txtCustmerName.setText(customerName);

                                if (soHeaderDTO.getSODetails().size() > 0) {
                                    SODetailsAdapter soDetailsAdapter = new SODetailsAdapter(getActivity(), soHeaderDTO.getSODetails());
                                    rv_SODetails.setAdapter(soDetailsAdapter);
                                }

                            } catch (Exception e) {
                                Log.d("dfa", e.toString());
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
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_order_details_title));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderhistory));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

}
