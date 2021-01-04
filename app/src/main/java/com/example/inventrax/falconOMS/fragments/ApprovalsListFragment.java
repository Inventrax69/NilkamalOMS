package com.example.inventrax.falconOMS.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.RefCreditListAdapter;
import com.example.inventrax.falconOMS.adapters.RefListAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.ApprovalDTO;
import com.example.inventrax.falconOMS.pojos.ApprovalListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ApprovalsListFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ApprovalsListFragment";
    private View rootView;
    RecyclerView recyclerView;
    Common common;
    private OMSCoreMessage core;
    RestService restService;
    ErrorMessages errorMessages;
    LinearLayoutManager layoutManager;
    String type;
    LinearLayout linear_check;
    CheckBox checkBoxSelectAll;
    RelativeLayout noitemRelative, mainRelative;
    AlertDialog dialog;

    SharedPreferences sp;
    String userRoleName = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.approvals_list_fragment, container, false);
            loadFormControls();
        } catch (Exception ex) {

        }

        return rootView;
    }

    private void loadFormControls() {

        if (getArguments().getString("type") != null) {
            type = getArguments().getString("type");
        }

        sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, MODE_PRIVATE);
        userRoleName = sp.getString(KeyValues.USER_ROLE_NAME, "");

        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        linear_check = rootView.findViewById(R.id.linear_check);
        noitemRelative = rootView.findViewById(R.id.noitemRelative);
        mainRelative = rootView.findViewById(R.id.mainRelative);
        checkBoxSelectAll = rootView.findViewById(R.id.checkBoxSelectAll);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        checkBoxSelectAll.setVisibility(View.GONE);
        linear_check.setVisibility(View.GONE);

        mainRelative.setVisibility(View.VISIBLE);
        noitemRelative.setVisibility(View.GONE);

        if (type.equals("4") || type.equals("6") || type.equals("23")) {

            /*if(userRoleName.equalsIgnoreCase(KeyValues.USER_ROLE_NAME_SUPPLY_CHAIN_MANAGER))
                ApprovalList(type,"","");
            else*/
            ApprovalList(type,"0","0");
        }
        if (type.equals("5")) {
            ApprovalCreditLimitList(type);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) { }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_approvals_list));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

    public void ApprovalList(final String type, final String divisionId, String mGroupId) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        ApprovalDTO approvalDTO = new ApprovalDTO();
        approvalDTO.setType(type);
        approvalDTO.setDivisionID(divisionId);
        approvalDTO.setMGroupID(mGroupId);
        message.setEntityObject(approvalDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        setProgressDialog();
        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.ApprovalList(message);

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
                            if(dialog!=null)
                                dialog.cancel();
                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                JSONArray getApprovalList = new JSONArray((ArrayList) core.getEntityObject());
                                CartHeaderListDTO cartHeaderListDTO = new CartHeaderListDTO();
                                final List<CartHeaderListDTO> cartHeaderListDTOS = new ArrayList<>();
                                for (int i = 0; i < getApprovalList.length(); i++) {
                                    cartHeaderListDTO = new Gson().fromJson(getApprovalList.getJSONObject(i).toString(), CartHeaderListDTO.class);
                                    cartHeaderListDTOS.add(cartHeaderListDTO);
                                }

                                if (cartHeaderListDTOS.size() > 0) {


                                    RefListAdapter refListAdapter = new RefListAdapter(getActivity(), cartHeaderListDTOS, new RefListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int pos) {
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("cartHeaderListDTO", cartHeaderListDTOS.get(pos));
                                            bundle.putInt("pos", pos);
                                            bundle.putString("type", type);
                                            FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsDetailsFragment(), bundle);
                                        }
                                    });

                                    recyclerView.setAdapter(refListAdapter);
                                    if(dialog!=null)
                                        dialog.cancel();

                                } else {
                                    mainRelative.setVisibility(View.GONE);
                                    noitemRelative.setVisibility(View.VISIBLE);
                                    if(dialog!=null)
                                        dialog.cancel();
                                }
                                if(dialog!=null)
                                    dialog.cancel();
                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                if(dialog!=null)
                                    dialog.cancel();
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                    }
                    if(dialog!=null)
                        dialog.cancel();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(dialog!=null)
                dialog.cancel();
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    /*
        private int year, month, day;
        TextView etDatePicker;
        Calendar myCalendar = Calendar.getInstance();
    */

    List<ApprovalListDTO> approvalListDTOS;

    public void ApprovalCreditLimitList(final String type) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        message.setEntityObject(type);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.ApprovalCreditLimitList(message);

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

                            try {

                                JSONArray getApprovalList = new JSONArray((ArrayList) core.getEntityObject());
                                ApprovalListDTO approvalListDTO = new ApprovalListDTO();
                                final List<ApprovalListDTO> approvalListDTOS = new ArrayList<>();
                                for (int i = 0; i < getApprovalList.length(); i++) {
                                    approvalListDTO = new Gson().fromJson(getApprovalList.getJSONObject(i).toString(), ApprovalListDTO.class);
                                    approvalListDTOS.add(approvalListDTO);
                                }

                                if (approvalListDTOS.size() > 0) {

                                    final List<ApprovalListDTO> approvalListDTOS_dy = new ArrayList<>(approvalListDTOS);

                                    RefCreditListAdapter refListAdapter = new RefCreditListAdapter(getActivity(), approvalListDTOS, new RefCreditListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(final int pos) {

                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("approvalListDTO", approvalListDTOS.get(pos));
                                            FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new CreditLimitDetailsFragment(), bundle);

                                        /*
                                        final Dialog dialog = new Dialog(getActivity());
                                        dialog.setContentView(R.layout.credit_recycler);
                                        dialog.setCancelable(false);
                                        Window window = dialog.getWindow();
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        final RecyclerView credit_recycler;
                                        credit_recycler = dialog.findViewById(R.id.recyclerView);
                                        credit_recycler.setHasFixedSize(true);
                                        credit_recycler.setAdapter(null);

                                        TextView txtCartRefNo = dialog.findViewById(R.id.txtCartRefNo);
                                        TextView txtCutName = dialog.findViewById(R.id.txtCutName);

                                        txtCartRefNo.setText(approvalListDTOS_dy.get(pos).getCartRefNo());
                                        txtCutName.setText(approvalListDTOS_dy.get(pos).getCustomer());

                                        layoutManager = new LinearLayoutManager(getActivity());
                                        credit_recycler.setLayoutManager(layoutManager);

                                        List<ApprovalListDTO> approvalListDTOS1;
                                        approvalListDTOS1 = new ArrayList<>();
                                        approvalListDTOS1.add(approvalListDTOS_dy.get(pos));

                                        *//*
                                        final HashMap<String, List<ApprovalListDTO>> hashMap = new HashMap();
                                        approvalListDTOS1 = hashMap.get(approvalListDTOS.get(pos).getCartRefNo());

                                        if (approvalListDTOS1 != null) {
                                            if (!(approvalListDTOS1.size() > 0)) {
                                                approvalListDTOS1 = new ArrayList<>();
                                                approvalListDTOS1.add(approvalListDTOS.get(pos));
                                            }
                                        } else {
                                            approvalListDTOS1 = new ArrayList<>();
                                            approvalListDTOS1.add(approvalListDTOS.get(pos));
                                        }

                                        hashMap.put(approvalListDTOS.get(pos).getCartRefNo(), approvalListDTOS1);
                                        *//*

                                        final RefPopCreditListAdapter refPopCreditListAdapter = new RefPopCreditListAdapter(getActivity(),getActivity(), approvalListDTOS1, new RefPopCreditListAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int pos1) {

                                            }
                                        },dialog);

                                        credit_recycler.setAdapter(refPopCreditListAdapter);

                                        *//*
                                        btnClOSE.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        });
                                        *//*

                                             *//*
                                        etDatePicker = (TextView) dialog.findViewById(R.id.etDatePicker);
                                        etDatePicker.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // TODO Auto-generated method stub
                                                DatePickerDialog datePickerDialog=  new DatePickerDialog(getActivity(), date,
                                                        myCalendar.get(Calendar.YEAR),
                                                        myCalendar.get(Calendar.MONTH),
                                                        myCalendar.get(Calendar.DAY_OF_MONTH));
                                                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                                datePickerDialog.show();
                                            }
                                        });
                                        *//*

                                        dialog.show();*/

                                        }
                                    });

                                    recyclerView.setAdapter(refListAdapter);

                                } else {
                                    mainRelative.setVisibility(View.GONE);
                                    noitemRelative.setVisibility(View.VISIBLE);
                                }
                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();

                            }
                        }
                    }
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


/*    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };


    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDatePicker.setText(sdf.format(myCalendar.getTime()));
    }*/


    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getActivity());
        tvText.setText(getString(R.string.wait_string));
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(18);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

}
