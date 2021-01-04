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
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.pojos.DiscountDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.SADRequestDTO;
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
public class SADListAdapter extends RecyclerView.Adapter<SADListAdapter.ViewHolder> {

    private List<DiscountDTO> discountDTOS;
    private List<Boolean> checkBokItem;
    private Context context;
    OnItemClickListener listener;
    private Common common;
    private RestService restService;
    private EditText etRemarks;
    private OMSCoreMessage core;
    private ErrorMessages errorMessages;
    private TextView txtAccept, txtReject;
    private CheckBox checkBoxSelectAll;

    public SADListAdapter(Context applicationContext, List<DiscountDTO> discountDTOS, OnItemClickListener mlistener) {

        this.context = applicationContext;
        this.discountDTOS = discountDTOS;
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
        for (int i = 0; i < this.discountDTOS.size(); i++)
            checkBokItem.add(false);

        mainFragmentsActions();

    }

    public SADListAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sad_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        DiscountDTO discountDTO = discountDTOS.get(i);

        viewHolder.txtWorkFlowType.setText(discountDTO.getWorkFlowType());
        viewHolder.txtRemarks.setText(discountDTO.getRemarks());
        viewHolder.txtDiscountCode.setText(discountDTO.getDiscountCode());
        viewHolder.txtWorkFlowStatus.setText(discountDTO.getWorkFlowStatus());
        viewHolder.checkBoxSelect.setChecked(checkBokItem.get(i));
        viewHolder.checkBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkBokItem.set(i, b);
            }
        });

    }

    @Override
    public int getItemCount() {
        return discountDTOS.size();
    }

    public void mainFragmentsActions() {

        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                for (int i = 0; i < discountDTOS.size(); i++)
                    checkBokItem.set(i, b);

                notifyDataSetChanged();

            }
        });

        txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etRemarks.getText().toString().trim().isEmpty()) {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), errorMessages.EMC_0027, ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                    return;
                }

                final AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(context);

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure to Accept")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (discountDTOS.size() > 0) {
                                    List<DiscountDTO> discountDTOList = new ArrayList<>();
                                    DiscountDTO discountDTO;
                                    for (int i = 0; i < discountDTOS.size(); i++) {
                                        discountDTO = new DiscountDTO();
                                        if (checkBokItem.get(i)) {
                                            discountDTO.setWorkFlowtransactionID(discountDTOS.get(i).getWorkFlowtransactionID());
                                            discountDTO.setWorkFlowTypeId(discountDTOS.get(i).getWorkFlowTypeId());
                                            discountDTO.setCartHeaderID(String.valueOf(discountDTOS.get(i).getDiscountID()));
                                            discountDTO.setUserRoleID(discountDTOS.get(i).getUserRoleID());
                                            discountDTOList.add(discountDTO);
                                        }
                                    }

                                    if (discountDTOList.size() > 0) {
                                        BulkApproveWorkflow("4", discountDTOList, etRemarks.getText().toString());
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

        txtReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (discountDTOS.size() > 0) {
                    List<DiscountDTO> discountDTOList = new ArrayList<>();
                    DiscountDTO discountDTO;
                    for (int i = 0; i < discountDTOS.size(); i++) {
                        discountDTO = new DiscountDTO();
                        if (checkBokItem.get(i)) {
                            discountDTO.setWorkFlowtransactionID(discountDTOS.get(i).getWorkFlowtransactionID());
                            discountDTO.setWorkFlowTypeId(discountDTOS.get(i).getWorkFlowTypeId());
                            discountDTO.setCartHeaderID(String.valueOf(discountDTOS.get(i).getDiscountID()));
                            discountDTO.setUserRoleID(discountDTOS.get(i).getUserRoleID());
                            discountDTOList.add(discountDTO);
                        }
                    }

                    if (discountDTOList.size() == 0) {
                        SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), "Please check atleast one item", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                        return;
                    }
                }

                if (etRemarks.getText().toString().trim().isEmpty()) {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), "Please write remarks", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                    return;
                }

                final AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(context);

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure to Reject")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (discountDTOS.size() > 0) {
                                    List<DiscountDTO> discountDTOList = new ArrayList<>();
                                    DiscountDTO discountDTO;
                                    for (int i = 0; i < discountDTOS.size(); i++) {
                                        discountDTO = new DiscountDTO();
                                        if (checkBokItem.get(i)) {
                                            discountDTO.setWorkFlowtransactionID(discountDTOS.get(i).getWorkFlowtransactionID());
                                            discountDTO.setWorkFlowTypeId(discountDTOS.get(i).getWorkFlowTypeId());
                                            discountDTO.setCartHeaderID(String.valueOf(discountDTOS.get(i).getDiscountID()));
                                            discountDTO.setUserRoleID(discountDTOS.get(i).getUserRoleID());
                                            discountDTOList.add(discountDTO);
                                        }
                                    }

                                    if (discountDTOList.size() > 0) {
                                        BulkApproveWorkflow("2", discountDTOList, etRemarks.getText().toString());
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

    AlertDialog alert;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtWorkFlowType, txtRemarks, txtDiscountCode, txtWorkFlowStatus, txtAccept, txtReject;
        CheckBox checkBoxSelect;

        public ViewHolder(View view) {
            super(view);
            txtWorkFlowType = (TextView) view.findViewById(R.id.txtWorkFlowType);
            txtRemarks = (TextView) view.findViewById(R.id.txtRemarks);
            txtDiscountCode = (TextView) view.findViewById(R.id.txtDiscountCode);
            txtWorkFlowStatus = (TextView) view.findViewById(R.id.txtWorkFlowStatus);
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

    // Item click listener interface
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public void BulkApproveWorkflow(final String type, List<DiscountDTO> discountDTOSList, final String remarks) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.MiscDTO, context);
        SADRequestDTO sadRequestDTO = new SADRequestDTO();
        sadRequestDTO.setStatusID(type);
        sadRequestDTO.setRows(discountDTOSList);
        sadRequestDTO.setRemarks(remarks);
        message.setEntityObject(sadRequestDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.BulkApproveWorkflowMaster(message);

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

                                if (core.getEntityObject().equals("Success") || core.getEntityObject().equals("Failure")) {


/*                                    List<DiscountDTO> discountDTOList = new ArrayList<>();
                                    for (int i = 0; i < discountDTOS.size(); i++) {
                                        if (!checkBokItem.get(i)) {
                                            discountDTOList.add(discountDTOS.get(i));
                                        }
                                    }

                                    discountDTOS = discountDTOList;
                                    checkBokItem = new ArrayList<>();
                                    for (int i = 0; i < discountDTOS.size(); i++)
                                        checkBokItem.add(false);

                                    checkBoxSelectAll.setChecked(false);*/

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
                    if (NetworkUtils.isInternetAvailable(context)) {
                        DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0001);
                    } else {
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

}
