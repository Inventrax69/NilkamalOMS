package com.example.inventrax.falconOMS.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.SchemsAndDiscountsAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.pojos.CustomerListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SoundUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchemesAndDiscountsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_SchemesAndDiscountsFragment";
    private View rootView;
    private RecyclerView rvSandD;
    private LinearLayoutManager linearLayoutManager;
    private Gson gson;
    private OMSCoreMessage core;
    private Common common;
    private SoundUtils soundUtils;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private HashMap<String, String> discountHM;
    private List discountStringLst;

    private String customerId = "", divisionId = "";

    private SchemsAndDiscountsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.schemes_discounts_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        if ( getArguments().getString("customerId") != null || !getArguments().getString("customerId").isEmpty() ) {
            customerId = getArguments().getString("customerId");
            divisionId = getArguments().getString("divisionId");
        }

        gson = new Gson();
        core = new OMSCoreMessage();
        common = new Common();
        soundUtils = new SoundUtils();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        errorMessages = new ErrorMessages();

        rvSandD = (RecyclerView) rootView.findViewById(R.id.rvSandD);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvSandD.setLayoutManager(linearLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen._minus3sdp);
        rvSandD.addItemDecoration(itemDecoration);
        rvSandD.setHasFixedSize(true);

        getOffers();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        }

    }

    public void getOffers() {

        if (NetworkUtils.isInternetAvailable(getActivity())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(getActivity(),getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Schemes_Discounts_DTO, getActivity());
        CustomerListDTO customerListDTO = new CustomerListDTO();
        customerListDTO.setDivisionID(divisionId);
        customerListDTO.setCustomerID(customerId);
        message.setEntityObject(customerListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.Offers(message);
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

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }


                            } else {

                                Log.v("SchemesAnd",new Gson().toJson(core.getEntityObject()));

                                try {


                                    ProgressDialogUtils.closeProgressDialog();

                                    ArrayList lst = (ArrayList) core.getEntityObject();
                                    discountHM = new HashMap<>();
                                    discountStringLst = new ArrayList();
                                    for (int i = 0; i < lst.size(); i++) {
                                        discountHM.put(((LinkedTreeMap) lst.get(i)).get("DiscountText").toString(), ((LinkedTreeMap) lst.get(0)).get("DiscountID").toString());
                                    }

                                    for (String list : discountHM.keySet()) {
                                        discountStringLst.add(list);
                                    }

                                    adapter = new SchemsAndDiscountsAdapter(getActivity(), (ArrayList) discountStringLst);
                                    rvSandD.setAdapter(adapter);

                                    ProgressDialogUtils.closeProgressDialog();

                                }
                                catch (Exception ex){
                                    if(ex.getMessage()!=null){

                                    }
                                }
                                ProgressDialogUtils.closeProgressDialog();
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
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getActivity());

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_SandD));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}
