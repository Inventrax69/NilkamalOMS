package com.example.inventrax.falconOMS.adapters;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.pojos.ApprovalListDTO;
import com.example.inventrax.falconOMS.pojos.CreditLimitCommitmentDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.MaterialDialogUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SoundUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by padmaja on 22/07/19.
 **/

public class RefPopCreditListAdapter extends RecyclerView.Adapter<RefPopCreditListAdapter.ViewHolder> {

    private static final String classCode = "OMS_Android_RefPopCreditListAdapter";
    private List<ApprovalListDTO> approvalListDTOS;
    private Context context;
    private OnItemClickListener listener;
    private Calendar myCalendar;
    private int pos = 0;
    private Button btnADD,btnSEND,btnCLEAR;
    private ImageView btnClOSE;
    private double total_amount;
    private Dialog dialog;
    private ApprovalListDTO approvalListDTOInit;
    private DecimalFormat df1 = new DecimalFormat("#.##");
    private FragmentActivity fragmentActivity;
    private Common common;
    private RestService restService;
    private OMSCoreMessage core;
    private ErrorMessages errorMessages;

    public RefPopCreditListAdapter(FragmentActivity fragmentActivity, Context applicationContext, List<ApprovalListDTO> approvalListDTOS, OnItemClickListener mlistener, Dialog dialog) {

        this.context = applicationContext;
        this.fragmentActivity = fragmentActivity;
        this.approvalListDTOS = approvalListDTOS;
        this.listener = mlistener;
        this.dialog = dialog;

        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();

        this.btnADD = dialog.findViewById(R.id.btnADD);
        this.btnSEND = dialog.findViewById(R.id.btnSEND);
        this.btnCLEAR = dialog.findViewById(R.id.btnCLEAR);
        this.btnClOSE = dialog.findViewById(R.id.btnClOSE);

        this.approvalListDTOInit=new ApprovalListDTO();

        if (approvalListDTOS.size() > 0){

            //  this.approvalListDTOInit = (ApprovalListDTO)approvalListDTOS.get(approvalListDTOS.size() - 1);
            this.approvalListDTOInit.setCartRefNo(approvalListDTOS.get(approvalListDTOS.size() - 1).getCartRefNo());
            this.approvalListDTOInit.setCustomer(approvalListDTOS.get(approvalListDTOS.size() - 1).getCustomer());
            this.approvalListDTOInit.setWorkFlowtransactionID(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowtransactionID());
            this.approvalListDTOInit.setWorkFlowTypeId(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowTypeId());
            this.approvalListDTOInit.setWorkFlowStatus(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatus());
            this.approvalListDTOInit.setCartHeaderID(approvalListDTOS.get(approvalListDTOS.size() - 1).getCartHeaderID());
            this.approvalListDTOInit.setWorkFlowStatus(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatus());
            this.approvalListDTOInit.setWorkFlowStatusID(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatusID());
            this.approvalListDTOInit.setUserRoleID(approvalListDTOS.get(approvalListDTOS.size() - 1).getUserRoleID());
            this.approvalListDTOInit.setQuantity(approvalListDTOS.get(approvalListDTOS.size() - 1).getQuantity());
            this.approvalListDTOInit.setAmount(approvalListDTOS.get(approvalListDTOS.size() - 1).getAmount());
            this.approvalListDTOInit.setCartDate(approvalListDTOS.get(approvalListDTOS.size() - 1).getCartDate());
            this.approvalListDTOInit.setAvailableCreditLimit(approvalListDTOS.get(approvalListDTOS.size() - 1).getAvailableCreditLimit());
            this.approvalListDTOInit.setTotalCreditLimit(approvalListDTOS.get(approvalListDTOS.size() - 1).getTotalCreditLimit());
            this.approvalListDTOInit.setRequiredCreditLimit(approvalListDTOS.get(approvalListDTOS.size() - 1).getRequiredCreditLimit());
            this.total_amount = Double.valueOf(df1.format(Double.parseDouble(approvalListDTOS.get(approvalListDTOS.size()-1).getAmount())));

        }

        myCalendar = Calendar.getInstance();

    }

    public RefPopCreditListAdapter() { }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.credit_list_pop, viewGroup, false);
        return new ViewHolder(view, new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final ApprovalListDTO approvalListDTO = approvalListDTOS.get(i);

        viewHolder.SNO.setText(String.valueOf(i + 1));
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        if(approvalListDTO.getExpectedDeliveryDate() == null){
            approvalListDTOS.get(i).setExpectedDeliveryDate(formattedDate);
        }
        viewHolder.etDatePicker.setText( approvalListDTO.getExpectedDeliveryDate());
        viewHolder.myCustomEditTextListener.updatePosition(viewHolder.getAdapterPosition());
        double d = Double.parseDouble(String.valueOf(approvalListDTO.getAmount()));
        viewHolder.etPrice.setText(String.format("%.2f", d));

        if (i == approvalListDTOS.size() - 1) {
            viewHolder.etPrice.setEnabled(true);
            viewHolder.etPrice.setClickable(true);
        } else {
            viewHolder.etPrice.setEnabled(false);
            viewHolder.etPrice.setClickable(false);
        }

        viewHolder.etPrice.setFocusable(false);
        viewHolder.etPrice.setFocusableInTouchMode(true);

        pos=-1;
        viewHolder.etDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                pos = i;
            }
        });

    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
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
        //String myFormat = "dd-MM-yyyy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        approvalListDTOS.get(pos).setExpectedDeliveryDate(sdf.format(myCalendar.getTime()));
        notifyItemChanged(pos);
    }

    @Override
    public int getItemCount() {
        return approvalListDTOS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView etDatePicker, SNO;
        EditText etPrice;
        MyCustomEditTextListener myCustomEditTextListener;
        private OMSCoreMessage core;
        private Common common;
        private SoundUtils soundUtils;
        private ExceptionLoggerUtils exceptionLoggerUtils;
        private ErrorMessages errorMessages;
        private Gson gson;

        public ViewHolder(View view, MyCustomEditTextListener myCustomEditTextListener) {
            super(view);
            etDatePicker = (TextView) view.findViewById(R.id.etDatePicker);
            SNO = (TextView) view.findViewById(R.id.SNO);
            etPrice = (EditText) view.findViewById(R.id.etPrice);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.etPrice.addTextChangedListener(myCustomEditTextListener);
            gson = new Gson();
            core = new OMSCoreMessage();
            common = new Common();
            soundUtils = new SoundUtils();
            exceptionLoggerUtils = new ExceptionLoggerUtils();
            errorMessages = new ErrorMessages();
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

            btnADD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ApprovalListDTO approvalListDTO1;
                    double amount = 0;
                    for (int p = 0; p < approvalListDTOS.size(); p++) {
                        try{
                            amount = Double.valueOf(df1.format(amount)) + Double.valueOf(df1.format(Double.parseDouble(approvalListDTOS.get(p).getAmount())));
                        }catch(Exception e){
                            Toast.makeText(context, "Enter valid number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (approvalListDTOS.size() > 0) {
                        if (total_amount > amount) {
                            // approvalListDTO1 = approvalListDTOS.get(approvalListDTOS.size() - 1);
                            approvalListDTO1 = new ApprovalListDTO();
                            approvalListDTO1.setCartRefNo(approvalListDTOS.get(approvalListDTOS.size() - 1).getCartRefNo());
                            approvalListDTO1.setCustomer(approvalListDTOS.get(approvalListDTOS.size() - 1).getCustomer());
                            approvalListDTO1.setWorkFlowtransactionID(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowtransactionID());
                            approvalListDTO1.setWorkFlowTypeId(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowTypeId());
                            approvalListDTO1.setWorkFlowStatus(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatus());
                            approvalListDTO1.setCartHeaderID(approvalListDTOS.get(approvalListDTOS.size() - 1).getCartHeaderID());
                            approvalListDTO1.setWorkFlowStatus(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatus());
                            approvalListDTO1.setWorkFlowStatusID(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatusID());
                            approvalListDTO1.setUserRoleID(approvalListDTOS.get(approvalListDTOS.size() - 1).getUserRoleID());
                            approvalListDTO1.setQuantity(approvalListDTOS.get(approvalListDTOS.size() - 1).getQuantity());
                            approvalListDTO1.setAmount("" + Double.valueOf(df1.format((total_amount - amount))));
                            approvalListDTOS.add(approvalListDTO1);
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(etPrice.getWindowToken(), 0);
                            notifyItemInserted(approvalListDTOS.size() - 1);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Amount Exceed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Amount Exceed", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            btnSEND.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ApprovalListDTO approvalListDTO1;
                    double amount = 0;
                    for (int p = 0; p < approvalListDTOS.size(); p++) {
                        try{
                            amount = amount + Double.parseDouble(approvalListDTOS.get(p).getAmount());
                        }catch(Exception e){
                            Toast.makeText(context, "Enter valid number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (approvalListDTOS.size() > 0) {
                        if (total_amount > amount) {
                            // approvalListDTO1 = approvalListDTOS.get(approvalListDTOS.size() - 1);
                            approvalListDTO1 = new ApprovalListDTO();
                            approvalListDTO1.setCartRefNo(approvalListDTOS.get(approvalListDTOS.size() - 1).getCartRefNo());
                            approvalListDTO1.setCustomer(approvalListDTOS.get(approvalListDTOS.size() - 1).getCustomer());
                            approvalListDTO1.setWorkFlowtransactionID(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowtransactionID());
                            approvalListDTO1.setWorkFlowTypeId(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowTypeId());
                            approvalListDTO1.setWorkFlowStatus(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatus());
                            approvalListDTO1.setCartHeaderID(approvalListDTOS.get(approvalListDTOS.size() - 1).getCartHeaderID());
                            approvalListDTO1.setWorkFlowStatus(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatus());
                            approvalListDTO1.setWorkFlowStatusID(approvalListDTOS.get(approvalListDTOS.size() - 1).getWorkFlowStatusID());
                            approvalListDTO1.setUserRoleID(approvalListDTOS.get(approvalListDTOS.size() - 1).getUserRoleID());
                            approvalListDTO1.setQuantity(approvalListDTOS.get(approvalListDTOS.size() - 1).getQuantity());
                            approvalListDTO1.setAmount("" + (total_amount - amount));
                            approvalListDTOS.add(approvalListDTO1);
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(etPrice.getWindowToken(), 0);
                            notifyItemInserted(approvalListDTOS.size() - 1);
                            notifyDataSetChanged();
                        } else {
                           InsertCreditLimitCommitments();
                        }
                    } else {
                        Toast.makeText(context, "Amount Exceed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnCLEAR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    approvalListDTOS=new ArrayList<>();
                    approvalListDTOS.add(approvalListDTOInit);
                    notifyDataSetChanged();

                }
            });

            btnClOSE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    /*
                    Bundle bundle2=new Bundle();
                    bundle2.putString("type","5");
                    FragmentUtils.replaceFragmentWithBundle(fragmentActivity, R.id.container, new ApprovalsListFragment(),bundle2);
                    */
                    approvalListDTOS=new ArrayList<>();
                    approvalListDTOS.add(approvalListDTOInit);
                    notifyDataSetChanged();
                    dialog.dismiss();

                }
            });

        }


        public void InsertCreditLimitCommitments() {

            if (NetworkUtils.isInternetAvailable(context)) { } else {
                DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0007);
                return;
            }

            OMSCoreMessage message = new OMSCoreMessage();
            message = common.SetAuthentication(EndpointConstants.CreditLimitCommitment_DTO, context);
            CreditLimitCommitmentDTO creditLimitCommitmentDTO = new CreditLimitCommitmentDTO();
            creditLimitCommitmentDTO.setCreditLimitCommitments(approvalListDTOS);
            message.setEntityObject(creditLimitCommitmentDTO);

            Call<OMSCoreMessage> call = null;
            ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

            call = apiService.InsertCreditLimitCommitments(message);
            ProgressDialogUtils.showProgressDialog("Please Wait");

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
                                    common.showAlertType(omsExceptionMessage,(Activity) context, context);

                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } else {
                                try {

                                    if(core.getEntityObject()!=null && !core.getEntityObject().toString().isEmpty()){


                                        if (core.getEntityObject().equals("Success")) {

                                            MaterialDialogUtils.showUploadSuccessDialog(context, "Approved");

                                            dialog.dismiss();
                                            ((Activity) context).onBackPressed();

                                        } else {
                                            MaterialDialogUtils.showUploadErrorDialog(context, "Failed");
                                        }

                                    }else{
                                        MaterialDialogUtils.showUploadErrorDialog(context, "Failed");
                                    }

                                    ProgressDialogUtils.closeProgressDialog();

/*
                                    if(core.getEntityObject().equals("Success")){
                                        approvalListDTOS=new ArrayList<>();
                                        approvalListDTOS.add(approvalListDTOInit);
                                        notifyDataSetChanged();
                                        BulkApproveWorkflow("4", approvalListDTOS, "");
                                       // BulkApproveWorkflow("4", approvalListDTOS, "");
                                        dialog.dismiss();
                                    }else{
                                        MaterialDialogUtils.showUploadErrorDialog(context,"Failed");
                                    }*/

                                }catch (Exception ex){

                                }
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
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
                    ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", context);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ProgressDialogUtils.closeProgressDialog();
                DialogUtils.showAlertDialog((Activity)context, errorMessages.EMC_0003);
            }
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {

        private int position;

        void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if(position==approvalListDTOS.size()-1)
                approvalListDTOS.get(position).setAmount(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) { }

    }

    // Item click listener interface
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

}