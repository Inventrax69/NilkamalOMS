package com.example.inventrax.falconOMS.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListResponseDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.productCatalogs;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SCMHeaderAdapter extends RecyclerView.Adapter<SCMHeaderAdapter.ItemViewHolder> {

    private static final String classCode = "OMS_Android_CartHeaderAdapter";
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<CartHeaderListDTO> itemList;
    private Context context;
    private AppDatabase db;
    private Activity activity;
    private Common common;
    private String userId = "";
    private int itemPosition, headerPosition;
    AlertDialog alert;
    private OMSCoreMessage core;
    private ErrorMessages errorMessages;
    SharedPreferencesUtils sharedPreferencesUtils;

    public SCMHeaderAdapter(List<CartHeaderListDTO> itemList, Context context, Activity activity) {
        this.itemList = itemList;
        this.context = context;
        this.activity = activity;
        db = new RoomAppDatabase(this.context).getAppDatabase();
        SharedPreferences sp = context.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");
        common = new Common();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();
    }

    public SCMHeaderAdapter() {}

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_scm_approval_header, viewGroup, false);
        return new ItemViewHolder(view);
    }

    //SCMDetailsAdapter subItemAdapter;

    OrderExpandableListAdapter expandableListAdapter;
    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, final int i) {

        final CartHeaderListDTO headerListDTO = itemList.get(i);
        itemViewHolder.customerName.setText(headerListDTO.getCustomerName());
        itemViewHolder.creditLimit.setText(String.valueOf((int) headerListDTO.getCreditLimit()));

        if (headerListDTO.getIsCreditLimit() > 0) {
            itemViewHolder.customerName.setTextColor(Color.RED);

        } else if (headerListDTO.getIsApproved() > 0) {

            itemViewHolder.customerName.setTextColor(context.getResources().getColor(R.color.safron));

        }else if(headerListDTO.getIsInActive()>0){
            itemViewHolder.customerName.setTextColor(Color.RED);

        } else {
            itemViewHolder.customerName.setTextColor(context.getResources().getColor(R.color.green));

        }

        itemViewHolder.tv_Status.setText(getStatus(headerListDTO));

        Date date = null;
        try {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            date = originalFormat.parse(headerListDTO.getCreatedOn());
            String formattedDate = targetFormat.format(date);
            itemViewHolder.tv_CreatedOn.setText("Created on : "+ formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create layout manager with initial prefetch item count

        // Create sub item view adapter
        // subItemAdapter = new ApprovalDetailsAdapter(context,itemList.get(i).getListCartDetailsList());

        /*subItemAdapter = new SCMDetailsAdapter(context, itemList.get(i).getListCartDetailsList(), new SCMDetailsAdapter.OnItemClickListener() {
            @Override
            public void onDeletClick(int pos) {
                if ((itemList.get(i).getIsInActive()==1 || itemList.get(i).getIsCreditLimit() == 1) &&
                        (itemList.get(i).getIsApproved() == 1 || itemList.get(i).getIsApproved() == 3 || itemList.get(i).getIsApproved() == 7)) {

                    final AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(context);

                    //Setting message manually and performing action on button click
                    builder.setMessage("You cannot delete the items...because this customers has been send to approvals of given cart")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    alert.dismiss();
                                }
                            });
                    //Creating dialog box
                    alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Alert");
                    alert.show();

                } else {
                    if (NetworkUtils.isInternetAvailable(context)) {
                        deleteCartItem(pos, i);
                    } else {
                        deleteItem(pos, i);
                    }
                }
            }
        });*/


        SCMExpandableListAdapter expandableListAdapter = new SCMExpandableListAdapter(context, itemList.get(i));


        itemViewHolder.expandableDeliveryDate.setAdapter(expandableListAdapter);
        if (headerListDTO.getDeliveryDate().size() > 0 && i == 0)
            itemViewHolder.expandableDeliveryDate.expandGroup(0);
        itemViewHolder.expandableDeliveryDate.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    itemViewHolder.expandableDeliveryDate.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        ViewTreeObserver vto = itemViewHolder.expandableDeliveryDate.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                itemViewHolder.expandableDeliveryDate.setIndicatorBounds(itemViewHolder.expandableDeliveryDate.getMeasuredWidth() - 80, itemViewHolder.expandableDeliveryDate.getMeasuredWidth());
            }
        });

        itemViewHolder.tv_Place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemList.get(i).getIsCreditLimit()==1){
                    sendForApproval(itemList.get(i).getCartHeaderID(), "5");
                }else{
                    OrderConfirmation(new int[]{itemList.get(i).getCartHeaderID()});
                }
            }
        });

        itemViewHolder.tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelOrder(itemList.get(i).getCartHeaderID());
            }
        });

    }

    private void deleteItem(int pos, int i) {

        db.cartDetailsDAO().deleteItem(itemList.get(i).getListCartDetailsList().get(pos).getMaterialMasterID());
        itemList.get(i).getListCartDetailsList().remove(pos);

        if(itemList.get(i).getListCartDetailsList().size()==0){
            itemList.remove(i);
        }


        notifyItemChanged(i);
        notifyDataSetChanged();

    }


    public void sendForApproval(final int cartHeaderID, String type) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, context);
        message.setEntityObject(cartHeaderID + "|" + type);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.InitiateWorkflow(message);

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
                                common.showAlertType(omsExceptionMessage, (Activity)context, context);
                            }
                            ProgressDialogUtils.closeProgressDialog();
                            db.cartDetailsDAO().deleteCartDetailsOfCartDetails(cartHeaderID);

                        } else {
                            ProgressDialogUtils.closeProgressDialog();
                            try {
                                ((Activity)context).findViewById(R.id.txtSCMCart).performClick();
                                //  String respObject = core.getEntityObject().toString();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if(NetworkUtils.isInternetAvailable(context)){
                        DialogUtils.showAlertDialog((Activity)context, errorMessages.EMC_0001);
                    }else{
                        DialogUtils.showAlertDialog((Activity)context, errorMessages.EMC_0014);
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

    public void deleteCartItem(int pos, int i) {

        itemPosition = pos;
        headerPosition = i;
        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, context);
        productCatalogs oDto = new productCatalogs();

        oDto.setCartDetailsID(itemList.get(i).getListCartDetailsList().get(pos).getCartDetailsID());
        oDto.setUserID(userId);
        oDto.setCustomerID(String.valueOf(itemList.get(i).getListCartDetailsList().get(pos).getCustomerID()));

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.DeleteCartItem(message);
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
                                common.showAlertType(omsExceptionMessage, activity, context);
                            }

                        } else {

                            deleteItem(itemPosition, headerPosition);

                            ProgressDialogUtils.closeProgressDialog();
                        }
                        ProgressDialogUtils.closeProgressDialog();
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
            DialogUtils.showAlertDialog(activity, errorMessages.EMC_0003);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView customerName, creditLimit, tv_Status,tv_CreatedOn;
        private TextView tv_Cancel,tv_Place;
        ExpandableListView expandableDeliveryDate;
        //private RecyclerView rvSubItem;

        ItemViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tv_customerName);
            creditLimit = itemView.findViewById(R.id.tv_creditLimit);
            tv_Cancel = itemView.findViewById(R.id.tv_Cancel);
            tv_Place = itemView.findViewById(R.id.tv_Place);
            //rvSubItem = itemView.findViewById(R.id.rv_cartDetails);
            tv_Status = itemView.findViewById(R.id.tv_Status);
            tv_CreatedOn = itemView.findViewById(R.id.tv_CreatedOn);
            expandableDeliveryDate = itemView.findViewById(R.id.expandableDeliveryDate);
        }

    }


    public void CancelOrder(int cartHeaderID) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, context);
        productCatalogs oDto = new productCatalogs();
        oDto.setCartHeaderID(cartHeaderID);
        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.CancelOrder(message);
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
                                common.showAlertType(omsExceptionMessage, (Activity)context, context);
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                String respObject = core.getEntityObject().toString();

                                if (respObject.equals("Order Cancelled")) {
                                    ((Activity)context).findViewById(R.id.txtSCMCart).performClick();
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
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
                    if(NetworkUtils.isInternetAvailable(context)){
                        DialogUtils.showAlertDialog((Activity)context, errorMessages.EMC_0001);
                    }else{
                        DialogUtils.showAlertDialog((Activity)context, errorMessages.EMC_0014);
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

    public void OrderConfirmation(final int[] cartHeaderId) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.OrderFulfilment_DTO, context);
        CartHeaderListResponseDTO oDto = new CartHeaderListResponseDTO();
        oDto.setCartHeaderID(cartHeaderId);
        oDto.setResult("");
        //oDto.setCartHeaderID(Integer.parseInt(cartHeaderId));

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.OrderConfirmation(message);

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
                                common.showAlertType(omsExceptionMessage, (Activity) context, context);
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                // String respObject = core.getEntityObject().toString();

                                if (core.getEntityObject() != null) {

                                    List lstSONum = new ArrayList();
                                    lstSONum = (ArrayList) core.getEntityObject();
                                    String soNums = "";
                                    if(lstSONum !=null && lstSONum.size()>0){
                                        for (int i = 0; i < lstSONum.size(); i++) {
                                            soNums = soNums + (String) ((LinkedTreeMap) lstSONum.get(i)).get("SONumber") + ",";
                                        }
                                        soNums = soNums.substring(0, soNums.length() - 1);
                                    }else{
                                        showFailedDialog();
                                        ProgressDialogUtils.closeProgressDialog();
                                        return;
                                    }

                                    showSuccessDialog(soNums);

                                } else {
                                    showFailedDialog();
                                }
                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
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
            DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0003);
        }
    }

    private void showSuccessDialog(String soNum) {

        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.dialog_order_created);

        TextView succesText = (TextView) mDialog.findViewById(R.id.textSONumber);
        Button buttonOk = (Button) mDialog.findViewById(R.id.buttonOk);
        TextView txtOrdersTitle = (TextView) mDialog.findViewById(R.id.txtOrdersTitle);
        txtOrdersTitle.setText(context.getString(R.string.order_created));
        succesText.setText(context.getString(R.string.so_success_text) + soNum);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
                ((Activity)context).findViewById(R.id.txtSCMCart).performClick();
            }
        });

        mDialog.show();

    }

    @SuppressLint("SetTextI18n")
    private void showFailedDialog() {

        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.dialog_order_failed);

        TextView succesText = (TextView) mDialog.findViewById(R.id.textSONumber);
        Button buttonOk = (Button) mDialog.findViewById(R.id.buttonOk);

        succesText.setText(context.getString(R.string.order_failed));

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
            }
        });

        mDialog.show();

    }

    private String getStatus(CartHeaderListDTO headerListDTO){

        if(headerListDTO.getIsApproved()==1 && headerListDTO.getIsInActive()==1 ){
            return "In active Approval (In-Process)";
        }
        else if(headerListDTO.getIsApproved()==1 && headerListDTO.getIsCreditLimit()==1 ){
            return "Credit Limit Approval (In-Process)";
        }
        else if(headerListDTO.getIsApproved()==2 && headerListDTO.getIsInActive()==1  ){
            return "In active Approval (Rejected)";
        }
        else if(headerListDTO.getIsApproved()==2 && headerListDTO.getIsCreditLimit()==1 ){
            return "Credit Limit Approval (Rejected)";
        }
        else if(headerListDTO.getIsApproved()==3 && headerListDTO.getIsInActive()==1  ){
            return "In active Approval (Initiated)";
        }
        else if(headerListDTO.getIsApproved()==3 && headerListDTO.getIsCreditLimit()==1 ){
            return "Credit Limit Approval (Initiated)";
        }
        else if(headerListDTO.getIsApproved()==7 && headerListDTO.getIsInActive()==1  ){
            return "In active Approval (Escalated)";
        }
        else if(headerListDTO.getIsApproved()==7 && headerListDTO.getIsCreditLimit()==1 ){
            return "Credit Limit Approval (Escalated)";
        }
        else if(headerListDTO.getIsApproved()==0 && headerListDTO.getIsInActive()==1 ){
            return "In active Approval Required";
        }
        else if(headerListDTO.getIsApproved()==0 && headerListDTO.getIsCreditLimit()==1 ){
            return "Credit Limit Approval Required";
        }
        else if(headerListDTO.getIsApproved()==0  && headerListDTO.getIsInActive()==0 && headerListDTO.getIsCreditLimit()==0){
            return "Check For Fulfilment Options";
        }
        else if(headerListDTO.getIsApproved()==6){
            return "Auto closed";
        }
        else if(headerListDTO.getIsApproved()==4){
            return "Approved";
        }else{
            return "";
        }
    }
}
