package com.example.inventrax.falconOMS.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.RefDetailsAdapter;
import com.example.inventrax.falconOMS.adapters.RefPopSCMApprovalAdapter;
import com.example.inventrax.falconOMS.adapters.RefSCMDetailsAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.ApprovalListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.MaterialDialogUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApprovalsDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ApprovalsDetailsFragment";
    private View rootView;
    RecyclerView recyclerView;
    Common common;
    private OMSCoreMessage core;
    RestService restService;
    ErrorMessages errorMessages;
    LinearLayoutManager layoutManager;
    TextView txtCRef, txtCCode, txtCName, txtAccept, txtReject;
    String type;
    EditText etRemarks;
    CartHeaderListDTO cartHeaderListDTO;
    List<ApprovalListDTO> approvalListDTOS;
    String userId, userRoleName;
    boolean isChanged = false;
    boolean isCorrectValue = false;
    String cartHeaderId = "";
    Dialog dialog;
    RecyclerView scm_recycler;
    RefPopSCMApprovalAdapter refSCMDetailsAdapter;
    String CartHeaderID, CartRefNo, PartnerCode, PartnerName, WorkFlowTransactionID, WorkFlowTypeID, UserRoleID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.approvals_details_fragment, container, false);
        try {
            loadFormControls();
        } catch (Exception ex) {
            //
        }
        return rootView;
    }

    private void loadFormControls() {

        cartHeaderListDTO = new CartHeaderListDTO();
        int pos = 0;
        if (getArguments().getSerializable("cartHeaderListDTO") != null) {
            cartHeaderListDTO = (CartHeaderListDTO) getArguments().getSerializable("cartHeaderListDTO");
            pos = getArguments().getInt("pos");
        }

        if (getArguments().getString("type") != null) {
            type = getArguments().getString("type");
        }

        if (getArguments().getString("cartHeaderId") != null) {
            cartHeaderId = getArguments().getString("cartHeaderId");
        }


        SharedPreferences sp = getActivity().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");
        userRoleName = sp.getString(KeyValues.USER_ROLE_NAME, "");

        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        txtCRef = (TextView) rootView.findViewById(R.id.txtCRef);
        txtCCode = (TextView) rootView.findViewById(R.id.txtCCode);
        txtCName = (TextView) rootView.findViewById(R.id.txtCName);
        txtAccept = (TextView) rootView.findViewById(R.id.txtAccept);
        txtReject = (TextView) rootView.findViewById(R.id.txtReject);

        etRemarks = (EditText) rootView.findViewById(R.id.etRemarks);

        txtAccept.setOnClickListener(this);
        txtReject.setOnClickListener(this);

        if (cartHeaderId.equals("")) {
            if (type.equals("23")) {
                ApprovalistSCMRF(cartHeaderListDTO.getCartHeaderID() + "|" + type);
            } else {
                ApprovelItemList(cartHeaderListDTO.getCartHeaderID() + "|" + type);
            }

        } else {
            if (type.equals("23")) {
                ApprovalistSCMRF(cartHeaderId + "|" + type);
            } else {
                ApprovelItemList(cartHeaderId + "|" + type);
            }
        }

    }

    AlertDialog alert;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtAccept:

                /*
                if (etRemarks.getText().toString().isEmpty()) {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "Please write remarks", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                    return;
                }
                */

                for (int i = 0; i < approvalListDTOS.size(); i++) {
                    if (approvalListDTOS.get(i).isCorrectValue()) {
                        SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "Please check all values", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                        return;
                    }
                }

                final AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(getActivity());

                // Setting message manually and performing action on button click
                builder.setMessage("Are you sure to Accept")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (approvalListDTOS.size() > 0) {
                                    if (isChanged) {
                                        UpdateApprovalCartList();
                                    } else {
                                        if (type.equals("23")) {
                                            boolean isCheck = true;
                                            for (int i = 0; i < approvalListDTOS.size(); i++) {
                                                if (!approvalListDTOS.get(i).isStatus()) {
                                                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "Please add PO numbers", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                                    isCheck = false;
                                                    return;
                                                }
                                            }
                                            if (isCheck) {
                                                ApproveWorkflow(approvalListDTOS.get(0), "4");
                                            }
                                        } else {
                                            ApproveWorkflow(approvalListDTOS.get(0), "4");
                                        }
                                    }
                                } else {
                                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "No items available", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                }
                            }
                        })
                        .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                            }
                        });

                //Creating dialog box
                alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert");
                alert.show();

                break;

            case R.id.txtReject:

                if (etRemarks.getText().toString().isEmpty()) {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "Please write remarks", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                    return;
                }

                final AlertDialog.Builder builder1;

                builder1 = new AlertDialog.Builder(getActivity());

                //Setting message manually and performing action on button click
                builder1.setMessage("Are you sure to Reject")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (approvalListDTOS.size() > 0) {
                                    ApproveWorkflow(approvalListDTOS.get(0), "2");
                                } else {
                                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "No items available", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                }

                            }
                        })
                        .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                            }
                        });

                //Creating dialog box
                alert = builder1.create();
                //Setting the title manually
                alert.setTitle("Alert");
                alert.show();
                break;

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.toolbar_approvals_details);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

    RefSCMDetailsAdapter refListAdapter;

    public void ApprovalistSCMRF(final String enitity_object) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        message.setEntityObject(enitity_object);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.ApprovalistSCMRF(message);

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

                                approvalListDTOS = new ArrayList<>();

                                if (core.getEntityObject().equals("No Records Found")) {
                                    ProgressDialogUtils.closeProgressDialog();
                                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "No Records Found", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                    return;
                                }

                                txtCRef.setText((String) ((LinkedTreeMap) core.getEntityObject()).get("CartRefNo"));
                                txtCCode.setText((String) ((LinkedTreeMap) core.getEntityObject()).get("PartnerCode"));
                                txtCName.setText((String) ((LinkedTreeMap) core.getEntityObject()).get("PartnerName"));

                                CartHeaderID = (String) ((LinkedTreeMap) core.getEntityObject()).get("CartHeaderID");
                                CartRefNo = (String) ((LinkedTreeMap) core.getEntityObject()).get("CartRefNo");
                                PartnerCode = (String) ((LinkedTreeMap) core.getEntityObject()).get("PartnerCode");
                                PartnerName = (String) ((LinkedTreeMap) core.getEntityObject()).get("PartnerName");
                                WorkFlowTransactionID = (String) ((LinkedTreeMap) core.getEntityObject()).get("WorkFlowTransactionID");
                                WorkFlowTypeID = (String) ((LinkedTreeMap) core.getEntityObject()).get("WorkFlowTypeID");
                                UserRoleID = (String) ((LinkedTreeMap) core.getEntityObject()).get("UserRoleID");

                                JSONArray getApprovalItemList = new JSONArray((ArrayList) ((LinkedTreeMap) core.getEntityObject()).get("AppprovalList"));
                                ApprovalListDTO approvalListDTO = new ApprovalListDTO();
                                for (int i = 0; i < getApprovalItemList.length(); i++) {
                                    approvalListDTO = new Gson().fromJson(getApprovalItemList.getJSONObject(i).toString(), ApprovalListDTO.class);
                                    approvalListDTOS.add(approvalListDTO);
                                }


                                refListAdapter = new RefSCMDetailsAdapter(getActivity(), approvalListDTOS, type, new RefSCMDetailsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(final int pos) {
                                        dialog = new Dialog(getActivity());
                                        dialog.setContentView(R.layout.dialog_scm_recycler);
                                        dialog.setCancelable(false);
                                        Window window = dialog.getWindow();
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                        scm_recycler = dialog.findViewById(R.id.recyclerView);
                                        scm_recycler.setHasFixedSize(true);
                                        scm_recycler.setAdapter(null);

                                        TextView txtCartRefNo = dialog.findViewById(R.id.txtCartRefNo);
                                        TextView txtCutName = dialog.findViewById(R.id.txtCutName);

                                        List<ApprovalListDTO> approvalListDTOS1 = new ArrayList<>();
                                        try {
                                            ApprovalListDTO approvalListDTOS_d = approvalListDTOS.get(pos).clone();
                                            approvalListDTOS1.add(approvalListDTOS_d);
                                        } catch (CloneNotSupportedException e) {
                                            // e.printStackTrace();
                                        }

                                        txtCartRefNo.setText(CartRefNo);
                                        txtCutName.setText(PartnerName);

                                        layoutManager = new LinearLayoutManager(getActivity());
                                        scm_recycler.setLayoutManager(layoutManager);

                                        refSCMDetailsAdapter = new RefPopSCMApprovalAdapter(new ApprovalsDetailsFragment(), pos, getActivity(), getActivity(), approvalListDTOS1, new RefPopSCMApprovalAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int pos1) {
                                                approvalListDTOS.get(pos).setStatus(true);
                                                refListAdapter.notifyDataSetChanged();
                                            }
                                        }, dialog);

                                        dialog.show();

                                        scm_recycler.setAdapter(refSCMDetailsAdapter);
                                    }
                                });

                                ProgressDialogUtils.closeProgressDialog();
                                recyclerView.setAdapter(refListAdapter);

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "Error while getting list of materials", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
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

    public void ApprovelItemList(final String enitity_object) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        message.setEntityObject(enitity_object);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.ApprovelItemList(message);

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

                                approvalListDTOS = new ArrayList<>();

                                if (core.getEntityObject().equals("No Records Found")) {
                                    ProgressDialogUtils.closeProgressDialog();
                                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "No Records Found", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                    return;
                                }

                                Log.v("ANIL", new Gson().toJson(core.getEntityObject()));

                                txtCRef.setText((String) ((LinkedTreeMap) core.getEntityObject()).get("CartRefNo"));
                                txtCCode.setText((String) ((LinkedTreeMap) core.getEntityObject()).get("PartnerCode"));
                                txtCName.setText((String) ((LinkedTreeMap) core.getEntityObject()).get("PartnerName"));

                                JSONArray getApprovalItemList = new JSONArray((ArrayList) ((LinkedTreeMap) core.getEntityObject()).get("AppprovalList"));
                                ApprovalListDTO approvalListDTO = new ApprovalListDTO();
                                for (int i = 0; i < getApprovalItemList.length(); i++) {
                                    approvalListDTO = new Gson().fromJson(getApprovalItemList.getJSONObject(i).toString(), ApprovalListDTO.class);
                                    approvalListDTOS.add(approvalListDTO);
                                }

                                if(type.equals("23")){
                                    CartHeaderID = (String) ((LinkedTreeMap) core.getEntityObject()).get("CartHeaderID");
                                    CartRefNo = (String) ((LinkedTreeMap) core.getEntityObject()).get("CartRefNo");
                                    PartnerCode = (String) ((LinkedTreeMap) core.getEntityObject()).get("PartnerCode");
                                    PartnerName = (String) ((LinkedTreeMap) core.getEntityObject()).get("PartnerName");
                                    WorkFlowTransactionID = (String) ((LinkedTreeMap) core.getEntityObject()).get("WorkFlowTransactionID");
                                    WorkFlowTypeID = (String) ((LinkedTreeMap) core.getEntityObject()).get("WorkFlowTypeID");
                                    UserRoleID = (String) ((LinkedTreeMap) core.getEntityObject()).get("UserRoleID");
                                }else{
                                    CartHeaderID = String.valueOf(approvalListDTOS.get(0).getCartHeaderID());
                                    CartRefNo = String.valueOf(approvalListDTOS.get(0).getCartRefNo());
                                    PartnerCode = "";
                                    PartnerName = String.valueOf(approvalListDTOS.get(0).getCustomer());
                                    WorkFlowTransactionID = String.valueOf(approvalListDTOS.get(0).getWorkFlowtransactionID());
                                    WorkFlowTypeID = String.valueOf(approvalListDTOS.get(0).getWorkFlowTypeId());
                                    UserRoleID = String.valueOf(approvalListDTOS.get(0).getUserRoleId());
                                }

                                RefDetailsAdapter refListAdapter = new RefDetailsAdapter(getActivity(), approvalListDTOS, type, new RefDetailsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int pos) {
                                        //
                                    }
                                }, new RefDetailsAdapter.OnApprovalUpdate() {
                                    @Override
                                    public void onApprovalUpdate(List<ApprovalListDTO> approvalListDTOS1) {
                                        isChanged = true;
                                        isCorrectValue = false;
                                        approvalListDTOS = approvalListDTOS1;
                                    }
                                }, new RefDetailsAdapter.OnCheckValues() {
                                    @Override
                                    public void onCheckValues(int pos) {
                                        isCorrectValue = true;
                                    }
                                });

                                ProgressDialogUtils.closeProgressDialog();
                                recyclerView.setAdapter(refListAdapter);

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "Error while getting list of materials", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
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

    public void UpdateApprovalCartList() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        message.setEntityObject(new Gson().toJson(approvalListDTOS));

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.UpdateApprovalCartList(message);

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

                                if (core.getEntityObject().equals("success")) {
                                    isChanged = false;
                                    ApproveWorkflow(approvalListDTOS.get(0), "4");
                                    RefDetailsAdapter refListAdapter = new RefDetailsAdapter(getActivity(), approvalListDTOS, type, new RefDetailsAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int pos) {
                                            //
                                        }
                                    }, new RefDetailsAdapter.OnApprovalUpdate() {
                                        @Override
                                        public void onApprovalUpdate(List<ApprovalListDTO> approvalListDTOS1) {
                                            isChanged = true;
                                            isCorrectValue = false;
                                            approvalListDTOS = approvalListDTOS1;
                                        }
                                    }, new RefDetailsAdapter.OnCheckValues() {
                                        @Override
                                        public void onCheckValues(int pos) {
                                            isCorrectValue = true;
                                        }
                                    });

                                    recyclerView.setAdapter(refListAdapter);

                                } else {
                                    Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_SHORT).show();
                                }
                                ///
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

    public void ApproveWorkflow(ApprovalListDTO approvalListDTO, final String Status) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        ApprovalListDTO approvalListDTO1 = new ApprovalListDTO();
        approvalListDTO1.setWorkFlowtransactionID(Integer.parseInt(WorkFlowTransactionID));
        approvalListDTO1.setCartHeaderID(Integer.parseInt(CartHeaderID));
        approvalListDTO1.setWorkFlowTypeID(Integer.parseInt(WorkFlowTypeID));
        approvalListDTO1.setUserRoleID(Integer.parseInt(UserRoleID));
        approvalListDTO1.setRFMaterialReqStockID(approvalListDTO.getRFMaterialReqStockID()== null ? "" : approvalListDTO.getRFMaterialReqStockID());
        approvalListDTO1.setStatusID(Status);
        approvalListDTO1.setRemarks(etRemarks.getText().toString());

        message.setEntityObject(approvalListDTO1);

        Log.v("ANIL", new Gson().toJson(message));

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.ApproveWorkflow(message);

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

                                if (core.getEntityObject().equals("Success") || core.getEntityObject().equals("Failure")) {
                                    etRemarks.setText("");
                                    if (Status.equals("2")) {
                                        MaterialDialogUtils.showUploadErrorDialog(getActivity(), "Rejected");
                                    } else {
                                        MaterialDialogUtils.showUploadSuccessDialog(getActivity(), "Approved");
                                    }
                                    ((Activity) getActivity()).onBackPressed();
                                } else {
                                    MaterialDialogUtils.showUploadErrorDialog(getActivity(), "Failed");
                                }
                                /*
                                Bundle bundle=new Bundle();
                                bundle.putString("type",type);
                                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsListFragment(),bundle);
                                */
                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                            }

                            ProgressDialogUtils.closeProgressDialog();

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

}
