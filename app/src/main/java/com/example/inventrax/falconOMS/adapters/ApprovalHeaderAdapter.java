package com.example.inventrax.falconOMS.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApprovalHeaderAdapter extends RecyclerView.Adapter<ApprovalHeaderAdapter.ItemViewHolder> {

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

    public ApprovalHeaderAdapter(List<CartHeaderListDTO> itemList, Context context, Activity activity) {
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

    public ApprovalHeaderAdapter() {

    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_approval_header, viewGroup, false);
        return new ItemViewHolder(view);
    }

    ApprovalDetailsAdapter subItemAdapter;

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, final int i) {

        final CartHeaderListDTO headerListDTO = itemList.get(i);
        itemViewHolder.customerName.setText(headerListDTO.getCustomerName());
        itemViewHolder.creditLimit.setText(String.valueOf((int) headerListDTO.getCreditLimit()));

        if (headerListDTO.getIsCreditLimit() > 0) {
            itemViewHolder.customerName.setTextColor(Color.RED);
            itemViewHolder.rvSubItem.setClickable(false);
            itemViewHolder.rvSubItem.setEnabled(false);
        } else if (headerListDTO.getIsApproved() > 0) {
            itemViewHolder.rvSubItem.setClickable(false);
            itemViewHolder.customerName.setTextColor(context.getResources().getColor(R.color.safron));
            itemViewHolder.rvSubItem.setEnabled(false);
        } else if (headerListDTO.getIsInActive() > 0) {
            itemViewHolder.customerName.setTextColor(Color.RED);
            itemViewHolder.rvSubItem.setClickable(false);
            itemViewHolder.rvSubItem.setEnabled(false);
        } else {
            itemViewHolder.customerName.setTextColor(context.getResources().getColor(R.color.green));
            itemViewHolder.rvSubItem.setClickable(true);
            itemViewHolder.rvSubItem.setEnabled(true);
        }

        itemViewHolder.tv_Status.setText(getStatus(headerListDTO));

        Date date = null;
        try {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            date = originalFormat.parse(headerListDTO.getCreatedOn());
            String formattedDate = targetFormat.format(date);
            itemViewHolder.tv_CreatedOn.setText("Created on : " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                itemViewHolder.rvSubItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(headerListDTO.getListCartDetailsList().size());

        // Create sub item view adapter
        // subItemAdapter = new ApprovalDetailsAdapter(context,itemList.get(i).getListCartDetailsList());

        subItemAdapter = new ApprovalDetailsAdapter(context, itemList.get(i), new ApprovalDetailsAdapter.OnItemClickListener() {
            @Override
            public void onDeletClick(int pos) {

                if ((itemList.get(i).getIsInActive() == 1 || itemList.get(i).getIsCreditLimit() == 1) &&
                        (itemList.get(i).getIsApproved() == 1 || itemList.get(i).getIsApproved() == 3 || itemList.get(i).getIsApproved() == 7)) {

                    final AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(context);

                    //Setting message manually and performing action on button click
                    builder.setMessage("You cannot delete the items...because this custumers has been send to approvals of given cart")
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
        });


        itemViewHolder.rvSubItem.setLayoutManager(layoutManager);
        itemViewHolder.rvSubItem.setAdapter(subItemAdapter);
        itemViewHolder.rvSubItem.setRecycledViewPool(viewPool);

    }

    private void deleteItem(int pos, int i) {

        db.cartDetailsDAO().deleteItem(itemList.get(i).getListCartDetailsList().get(pos).getMaterialMasterID());
        itemList.get(i).getListCartDetailsList().remove(pos);

        if (itemList.get(i).getListCartDetailsList().size() == 0) {
            itemList.remove(i);
        }

        db.cartHeaderDAO().updateIsUpdated(itemList.get(i).getCustomerID(),1);

        notifyItemChanged(i);
        notifyDataSetChanged();

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
        private TextView customerName, creditLimit, tv_Status, tv_CreatedOn;
        private RecyclerView rvSubItem;

        ItemViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tv_customerName);
            creditLimit = itemView.findViewById(R.id.tv_creditLimit);
            rvSubItem = itemView.findViewById(R.id.rv_cartDetails);
            tv_Status = itemView.findViewById(R.id.tv_Status);
            tv_CreatedOn = itemView.findViewById(R.id.tv_CreatedOn);
        }

    }

    private String getStatus(CartHeaderListDTO headerListDTO) {

        // Is Approved = 1
        if (headerListDTO.getIsApproved() == 1 && headerListDTO.getIsInActive() == 1)
            return "In active Approval (In-Process)";
        else if (headerListDTO.getIsApproved() == 1 && headerListDTO.getIsOpenPrice() == 1)
            return "Open Price Approval (In-Progress)";
        else if (headerListDTO.getIsApproved() == 1 && headerListDTO.getIsStockNotAvailable().equals("1"))
            return "Stock not available Approval (In-Progress)";
        else if (headerListDTO.getIsApproved() == 1 && headerListDTO.getIsCreditLimit() == 1)
            return "Credit Limit Approval (In-Process)";
            // Is Approved = 2
        else if (headerListDTO.getIsApproved() == 2 && headerListDTO.getIsInActive() == 1)
            return "In active Approval (Rejected)";
        else if (headerListDTO.getIsApproved() == 2 && headerListDTO.getIsOpenPrice() == 1)
            return "Open Price Approval (Rejected)";
        else if (headerListDTO.getIsApproved() == 2 && headerListDTO.getIsStockNotAvailable().equals("1"))
            return "Stock not available Approval (Rejected)";
        else if (headerListDTO.getIsApproved() == 2 && headerListDTO.getIsCreditLimit() == 1)
            return "Credit Limit Approval (Rejected)";
            // Is Approved = 3
        else if (headerListDTO.getIsApproved() == 3 && headerListDTO.getIsInActive() == 1)
            return "In active Approval (Initiated)";
        else if (headerListDTO.getIsApproved() == 3 && headerListDTO.getIsOpenPrice() == 1)
            return "Open Price Approval (Initiated)";
        else if (headerListDTO.getIsApproved() == 3 && headerListDTO.getIsStockNotAvailable().equals("1"))
            return "Stock not available Approval (Initiated)";
        else if (headerListDTO.getIsApproved() == 3 && headerListDTO.getIsCreditLimit() == 1)
            return "Credit Limit Approval (Initiated)";
            // Is Approved = 4
        else if (headerListDTO.getIsApproved() == 4 && headerListDTO.getIsInActive() == 1)
            return "In active Approval (Approved)";
        else if (headerListDTO.getIsApproved() == 4 && headerListDTO.getIsOpenPrice() == 1)
            return "Open Price Approval (Approved)";
        else if (headerListDTO.getIsApproved() == 4 && headerListDTO.getIsStockNotAvailable().equals("1"))
            return "Stock not available Approval (Approved)";
        else if (headerListDTO.getIsApproved() == 4 && headerListDTO.getIsCreditLimit() == 1)
            return "Credit Limit Approval (Approved)";
            // Is Approved = 5
        else if (headerListDTO.getIsApproved() == 5 && headerListDTO.getIsInActive() == 1)
            return "In active Approval (Cancelled)";
        else if (headerListDTO.getIsApproved() == 5 && headerListDTO.getIsOpenPrice() == 1)
            return "Open Price Approval (Cancelled)";
        else if (headerListDTO.getIsApproved() == 5 && headerListDTO.getIsStockNotAvailable().equals("1"))
            return "Stock not available Approval (Cancelled)";
        else if (headerListDTO.getIsApproved() == 5 && headerListDTO.getIsCreditLimit() == 1)
            return "Credit Limit Approval (Cancelled)";
            // Is Approved = 6
        else if (headerListDTO.getIsApproved() == 6 && headerListDTO.getIsInActive() == 1)
            return "In active Approval (Auto Closed)";
        else if (headerListDTO.getIsApproved() == 6 && headerListDTO.getIsOpenPrice() == 1)
            return "Open Price Approval (Auto Closed)";
        else if (headerListDTO.getIsApproved() == 6 && headerListDTO.getIsStockNotAvailable().equals("1"))
            return "Stock not available Approval (Auto Closed)";
        else if (headerListDTO.getIsApproved() == 6 && headerListDTO.getIsCreditLimit() == 1)
            return "Credit Limit Approval (Auto Closed)";
            // Is Approved = 7
        else if (headerListDTO.getIsApproved() == 7 && headerListDTO.getIsInActive() == 1)
            return "In active Approval (Escalated)";
        else if (headerListDTO.getIsApproved() == 7 && headerListDTO.getIsOpenPrice() == 1)
            return "Open Price Approval (Escalated)";
        else if (headerListDTO.getIsApproved() == 7 && headerListDTO.getIsStockNotAvailable().equals("1"))
            return "Stock not available Approval (Escalated)";
        else if (headerListDTO.getIsApproved() == 7 && headerListDTO.getIsCreditLimit() == 1)
            return "Credit Limit Approval (Escalated)";
            // Is Approved = 0
        else if (headerListDTO.getIsApproved() == 0 && headerListDTO.getIsInActive() == 1)
            return "In active Approval Required";
        else if (headerListDTO.getIsApproved() == 7 && headerListDTO.getIsOpenPrice() == 1)
            return "Open Price Approval Required";
        else if (headerListDTO.getIsApproved() == 7 && headerListDTO.getIsStockNotAvailable().equals("1"))
            return "Stock not available Approval Required";
        else if (headerListDTO.getIsApproved() == 0 && headerListDTO.getIsCreditLimit() == 1)
            return "Credit Limit Approval Required";
        else if (headerListDTO.getIsApproved() == 0 && headerListDTO.getIsInActive() == 0 && headerListDTO.getIsCreditLimit() == 0)
            return "Check For Fulfilment Options";
        else return "";

    }
}
