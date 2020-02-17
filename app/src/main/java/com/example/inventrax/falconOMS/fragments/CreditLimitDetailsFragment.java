package com.example.inventrax.falconOMS.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.RefPopCreditListAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.pojos.ApprovalListDTO;
import com.example.inventrax.falconOMS.pojos.CreditRequestDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.MaterialDialogUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditLimitDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ApprovalsFragment";
    private View rootView;
    CardView cvOpenPrice, cvInActive, cvCreditLimit, cvSchemesAndDiscounts;
    ApprovalListDTO approvalListDTO;
    TextView type, txtCartRefNo, txtDate, txtCustomer, txtQuantity,
            txtAmount, txtTotalCL, txtAvaCL, txtReqCL,
            txtAccept, txtReject, etRemarks;
    AlertDialog alert;
    private Common common;
    private RestService restService;
    private OMSCoreMessage core;
    private ErrorMessages errorMessages;
    LinearLayoutManager layoutManager;
    RefPopCreditListAdapter refPopCreditListAdapter;
    RecyclerView credit_recycler;
    List<ApprovalListDTO> approvalListDTOS;
    Dialog dialog;
    SetGetApprovalList setGetApprovalList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.credit_limt_details_fragment, container, false);
        try {
            loadFormControls();
        } catch (Exception e) {
            //
        }
        return rootView;
    }

    private void loadFormControls() {


        if (getArguments().getSerializable("approvalListDTO") != null) {
            approvalListDTO = (ApprovalListDTO) getArguments().getSerializable("approvalListDTO");
        }

        type = rootView.findViewById(R.id.type);
        txtCartRefNo = rootView.findViewById(R.id.txtCartRefNo);
        txtDate = rootView.findViewById(R.id.txtDate);
        txtCustomer = rootView.findViewById(R.id.txtCustomer);
        txtQuantity = rootView.findViewById(R.id.txtQuantity);
        txtAmount = rootView.findViewById(R.id.txtAmount);
        txtTotalCL = rootView.findViewById(R.id.txtTotalCL);
        txtAvaCL = rootView.findViewById(R.id.txtAvaCL);
        txtReqCL = rootView.findViewById(R.id.txtReqCL);

        txtReject = rootView.findViewById(R.id.txtReject);
        txtAccept = rootView.findViewById(R.id.txtAccept);
        etRemarks = rootView.findViewById(R.id.etRemarks);

        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();

        type.setText(approvalListDTO.getWorkFlowType());
        txtCartRefNo.setText(approvalListDTO.getCartRefNo());
        txtDate.setText(approvalListDTO.getCartDate());
        txtCustomer.setText(approvalListDTO.getCustomer());
        txtQuantity.setText(String.valueOf(approvalListDTO.getQuantity()));
        txtAmount.setText(approvalListDTO.getAmount());
        txtTotalCL.setText(approvalListDTO.getTotalCreditLimit());
        txtAvaCL.setText(approvalListDTO.getAvailableCreditLimit());
        txtReqCL.setText(approvalListDTO.getRequiredCreditLimit());

        approvalListDTOS = new ArrayList<>();
        approvalListDTOS.add(approvalListDTO);

        txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.credit_recycler);
                dialog.setCancelable(false);
                Window window = dialog.getWindow();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                credit_recycler = dialog.findViewById(R.id.recyclerView);
                credit_recycler.setHasFixedSize(true);
                credit_recycler.setAdapter(null);

                TextView txtCartRefNo = dialog.findViewById(R.id.txtCartRefNo);
                TextView txtCutName = dialog.findViewById(R.id.txtCutName);

                List<ApprovalListDTO> approvalListDTOS1 = new ArrayList<>();
                try {
                    ApprovalListDTO approvalListDTOS_d = approvalListDTO.clone();
                    approvalListDTOS1.add(approvalListDTOS_d);
                } catch (CloneNotSupportedException e) {
                    // e.printStackTrace();
                }

                txtCartRefNo.setText(approvalListDTO.getCartRefNo());
                txtCutName.setText(approvalListDTO.getCustomer());

                layoutManager = new LinearLayoutManager(getActivity());
                credit_recycler.setLayoutManager(layoutManager);

                refPopCreditListAdapter = new RefPopCreditListAdapter(getActivity(), getActivity(), approvalListDTOS1, new RefPopCreditListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int pos1) {

                    }
                }, dialog);

                credit_recycler.setAdapter(refPopCreditListAdapter);

                dialog.show();
            }
        });


        txtReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etRemarks.getText().toString().isEmpty()) {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) getActivity()).findViewById(R.id.snack_bar_action_layout), "Please write remarks", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                    return;
                }

                final AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(getActivity());

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure to Reject")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (approvalListDTOS.size() > 0) {
                                        BulkApproveWorkflow("2", approvalListDTOS, etRemarks.getText().toString());
                                } else {
                                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) (getActivity()).findViewById(R.id.snack_bar_action_layout), "No discounts available", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
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

            }
        });


    }


    public void BulkApproveWorkflow(final String type, List<ApprovalListDTO> approvalListDTOSList, final String remarks) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setStatusID(type);
        creditRequestDTO.setRows(approvalListDTOSList);
        creditRequestDTO.setRemarks(remarks);
        message.setEntityObject(creditRequestDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.BulkApproveWorkflowOrders(message);

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
                                common.showAlertType(omsExceptionMessage, (Activity) getActivity(), getActivity());
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {
                                if (core.getEntityObject() != null && !core.getEntityObject().toString().isEmpty()) {


                                    if (core.getEntityObject().equals("Success") || core.getEntityObject().equals("Failure")) {

                                        etRemarks.setText("");
                                        if (type.equals("2")) {
                                            MaterialDialogUtils.showUploadErrorDialog(getActivity(), "Rejected");
                                        } else {
                                            MaterialDialogUtils.showUploadSuccessDialog(getActivity(), "Approved");
                                        }

                                        ((Activity) getActivity()).onBackPressed();

                                    } else {
                                        MaterialDialogUtils.showUploadSuccessDialog(getActivity(), "Failed");
                                    }

                                } else {
                                    MaterialDialogUtils.showUploadSuccessDialog(getActivity(), "Failed");
                                }

                                ProgressDialogUtils.closeProgressDialog();

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
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog((Activity) getActivity(), errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog((Activity) getActivity(), errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), "SADListAdapter", "001", getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog((Activity) getActivity(), errorMessages.EMC_0003);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        }
    }


    public class SetGetApprovalList {

        List<ApprovalListDTO> listDTOS;

        SetGetApprovalList(List<ApprovalListDTO> listDTOS) {
            this.listDTOS = listDTOS;
        }

        public List<ApprovalListDTO> getListDTOS() {
            return listDTOS;
        }

        public void setListDTOS(List<ApprovalListDTO> listDTOS) {
            this.listDTOS = listDTOS;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_credit_limit));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

}
