package com.example.inventrax.falconOMS.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
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

/**
 * Created by padmaja on 22/07/19.
 */
public class RefCreditListAdapter extends RecyclerView.Adapter<RefCreditListAdapter.ViewHolder> {

    OnItemClickListener listener;
    AlertDialog alert;
    private List<ApprovalListDTO> approvalListDTOS1;
    private Context context;
    private List<Boolean> checkBokItem;
    private TextView txtAccept, txtReject;
    private CheckBox checkBoxSelectAll;
    private EditText etRemarks;
    private Common common;
    private RestService restService;
    private OMSCoreMessage core;
    private ErrorMessages errorMessages;

    public RefCreditListAdapter(Context context, List<ApprovalListDTO> approvalListDTOS1, OnItemClickListener mlistener) {
        this.context = context;
        this.approvalListDTOS1 = approvalListDTOS1;
        listener = mlistener;
        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();
        txtAccept = (TextView) ((Activity) context).findViewById(R.id.txtAccept);
        txtReject = (TextView) ((Activity) context).findViewById(R.id.txtReject);
        checkBoxSelectAll = (CheckBox) ((Activity) context).findViewById(R.id.checkBoxSelectAll);
        etRemarks = (EditText) ((Activity) context).findViewById(R.id.etRemarks);
        checkBokItem = new ArrayList<>();
        for (int i = 0; i < this.approvalListDTOS1.size(); i++)
            checkBokItem.add(false);

        mainFragmentsActions();

    }

    public RefCreditListAdapter() { }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approvals_credit_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        ApprovalListDTO approvalListDTO = approvalListDTOS1.get(i);
        viewHolder.txtCRef.setText(approvalListDTO.getCartRefNo());
        viewHolder.txtCName.setText(approvalListDTO.getCustomer());
        viewHolder.txtQty.setText("Qty: " + approvalListDTO.getQuantity());
        viewHolder.txtAmount.setText("Amount: " + approvalListDTO.getAmount());
        viewHolder.txtDate.setText("Created on: " + approvalListDTO.getCartDate());
        viewHolder.checkBoxSelect.setChecked(checkBokItem.get(i));
        viewHolder.checkBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (int j = 0; j < approvalListDTOS1.size(); j++){
                    checkBokItem.set(j,false);
                    notifyItemChanged(j);
                }
                checkBokItem.set(i, b);


/*                if(!b){
                    checkBoxSelectAll.setChecked(false);
                }*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return approvalListDTOS1.size();
    }


    private void mainFragmentsActions() {

        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                for (int i = 0; i < approvalListDTOS1.size(); i++)
                    checkBokItem.set(i, b);

                notifyDataSetChanged();

            }
        });

        txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etRemarks.getText().toString().isEmpty()) {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), "Please write remarks", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                    return;
                }

                final AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(context);

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure to Accept")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (approvalListDTOS1.size() > 0) {
                                    List<ApprovalListDTO> approvalListDTOSList = new ArrayList<>();
                                    ApprovalListDTO approvalListDTO;
                                    for (int i = 0; i < approvalListDTOS1.size(); i++) {
                                        approvalListDTO = new ApprovalListDTO();
                                        if (checkBokItem.get(i)) {
                                            approvalListDTO.setWorkFlowtransactionID(approvalListDTOS1.get(i).getWorkFlowtransactionID());
                                            approvalListDTO.setWorkFlowTypeId(approvalListDTOS1.get(i).getWorkFlowTypeId());
                                            approvalListDTO.setCartHeaderID(approvalListDTOS1.get(i).getCartHeaderID());
                                            approvalListDTO.setUserRoleID(approvalListDTOS1.get(i).getUserRoleID());
                                            approvalListDTOSList.add(approvalListDTO);
                                        }
                                    }

                                    if (approvalListDTOSList.size() > 0) {
                                        BulkApproveWorkflow("4", approvalListDTOSList, etRemarks.getText().toString());
                                    } else {
                                        SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), "Please check atleast one item", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                                    }
                                } else {
                                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), "No discounts available", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
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
        message = common.SetAuthentication(EndpointConstants.Scalar, context);
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
                                common.showAlertType(omsExceptionMessage, (Activity) context, context);
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {
                                if(core.getEntityObject()!=null && !core.getEntityObject().toString().isEmpty()){


                                if (core.getEntityObject().equals("Success") || core.getEntityObject().equals("Failure")) {

                                    List<ApprovalListDTO> approvalListDTOSList = new ArrayList<>();
                                    for (int i = 0; i < approvalListDTOS1.size(); i++) {
                                        if (!checkBokItem.get(i)) {
                                            approvalListDTOSList.add(approvalListDTOS1.get(i));
                                        }
                                    }

                                    approvalListDTOS1 = approvalListDTOSList;
                                    checkBokItem = new ArrayList<>();
                                    for (int i = 0; i < approvalListDTOS1.size(); i++)
                                        checkBokItem.add(false);

                                    checkBoxSelectAll.setChecked(false);
                                    etRemarks.setText("");
                                    if (type.equals("2")) {
                                        MaterialDialogUtils.showUploadErrorDialog(context, "Rejected");
                                    } else {
                                        MaterialDialogUtils.showUploadSuccessDialog(context, "Approved");
                                    }

                                    notifyDataSetChanged();
                                    ((Activity) context).onBackPressed();

                                } else {
                                    MaterialDialogUtils.showUploadSuccessDialog(context, "Failed");
                                }

                                }else{
                                    MaterialDialogUtils.showUploadSuccessDialog(context, "Failed");
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), "Error while approvals", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if(NetworkUtils.isInternetAvailable(context)){
                        DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0001);
                    }else{
                        DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), "SADListAdapter", "001", context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0003);
        }
    }

    // Item click listener interface
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtCRef, txtCName, txtQty, txtAmount, txtDate;
        CheckBox checkBoxSelect;
        ImageView btnADD;

        public ViewHolder(View view) {
            super(view);
            txtCRef = (TextView) view.findViewById(R.id.txtCRef);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtCName = (TextView) view.findViewById(R.id.txtCName);
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            btnADD = (ImageView) view.findViewById(R.id.btnADD);
            checkBoxSelect = (CheckBox) view.findViewById(R.id.checkBoxSelect);

            //on item click
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
