package com.example.inventrax.falconOMS.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.SADListAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.pojos.DiscountDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SADListFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ApprovalsListFragment";
    private View rootView;
    RecyclerView recyclerView;
    Common common;
    private OMSCoreMessage core;
    RestService restService;
    ErrorMessages errorMessages;
    LinearLayoutManager layoutManager;
    RelativeLayout noitemRelative,mainRelative;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.sad_list_fragment, container, false);
        try{
            loadFormControls();
        }catch (Exception ex){ }

        return rootView;
    }

    private void loadFormControls() {

        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();

        recyclerView=rootView.findViewById(R.id.recyclerView);
        noitemRelative =rootView.findViewById(R.id.noitemRelative);
        mainRelative =rootView.findViewById(R.id.mainRelative);
        recyclerView.setHasFixedSize(true);

        mainRelative.setVisibility(View.VISIBLE);
        noitemRelative.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ApprovalDiscount();

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_sad_list));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


    public void ApprovalDiscount() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        message.setEntityObject(null);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.ApprovalDiscount(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }
                            ProgressDialogUtils.closeProgressDialog();

                        } else {
                            ProgressDialogUtils.closeProgressDialog();

                            try {

                                JSONArray getApprovalList = new JSONArray((ArrayList) core.getEntityObject());
                                DiscountDTO discountDTO=new DiscountDTO();
                                final List<DiscountDTO> discountDTOS=new ArrayList<>();
                                for(int i=0;i<getApprovalList.length();i++){
                                    discountDTO =new Gson().fromJson(getApprovalList.getJSONObject(i).toString(), DiscountDTO.class);
                                    discountDTOS.add(discountDTO);
                                }

                                if(discountDTOS.size()>0){
                                    SADListAdapter refListAdapter=new SADListAdapter(getActivity(), discountDTOS, new SADListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int pos) {
/*                                            Bundle bundle=new Bundle();
                                        bundle.putSerializable("discountDTO",discountDTOS.get(pos));
                                        bundle.putInt("pos",pos);
                                        FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsDetailsFragment(),bundle);*/
                                        }
                                    });

                                    recyclerView.setAdapter(refListAdapter);

                                }else{
                                    mainRelative.setVisibility(View.GONE);
                                    noitemRelative.setVisibility(View.VISIBLE);
                                }



                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "Error while approvals", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                            }
                        }
                    }
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
}
