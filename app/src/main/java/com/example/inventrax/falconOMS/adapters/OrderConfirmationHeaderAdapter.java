package com.example.inventrax.falconOMS.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.CartActivity;
import com.example.inventrax.falconOMS.activities.OrderConfirmationActivity;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
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
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmationHeaderAdapter extends RecyclerView.Adapter<OrderConfirmationHeaderAdapter.ItemViewHolder> {

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
    private double total, total_tax;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    Animation anim = new AlphaAnimation(0.0f, 1.0f);
    boolean setBlink = false;

    public OrderConfirmationHeaderAdapter(List<CartHeaderListDTO> itemList, Context context, Activity activity) {
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

    public OrderConfirmationHeaderAdapter() {
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_orderconfirm_header, viewGroup, false);
        return new ItemViewHolder(view);
    }

    OrderConfirmationAdapter subItemAdapter;

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, final int i) {

        final CartHeaderListDTO headerListDTO = itemList.get(i);
        itemViewHolder.customerName.setText(headerListDTO.getCustomerName());
        itemViewHolder.creditLimit.setText(String.valueOf((int) headerListDTO.getCreditLimit()));
        df2.setRoundingMode(RoundingMode.DOWN);
        itemViewHolder.tv_total.setText("Sub total : " + "Rs. " + df2.format(Double.parseDouble(headerListDTO.getTotalPrice())));
        itemViewHolder.tv_total_tax.setText("Total with tax : " + "Rs. " + df2.format(Double.parseDouble(headerListDTO.getTotalPriceWithTax())));

        if (headerListDTO.getIsCreditLimit() > 0) {
            itemViewHolder.customerName.setTextColor(Color.RED);
        } else if (headerListDTO.getIsApproved() > 0) {
            itemViewHolder.customerName.setTextColor(context.getResources().getColor(R.color.safron));
        } else if (headerListDTO.getIsInActive() > 0) {
            itemViewHolder.customerName.setTextColor(Color.RED);
        } else {
            itemViewHolder.customerName.setTextColor(context.getResources().getColor(R.color.green));
        }

        itemViewHolder.tv_Status.setText(getStatus(headerListDTO));

        if (setBlink) {
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            itemViewHolder.tv_Status.startAnimation(anim);
            itemViewHolder.tv_Status.setTextColor(context.getResources().getColor(R.color.red));
        }

        /*
        double sub_total=0.0;
        if(this.itemList.get(i).getIsApproved()==0){
            for (int j = 0; j < this.itemList.get(i).getDeliveryDate().size(); j++) {
                for (int k = 0; k < this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().size(); k++) {
                    sub_total+=
                            ((this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice() != null ||
                                    !this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice().isEmpty() ||
                                    !this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice().equals("")) ?
                                    Double.parseDouble(this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice())  : 0.0)
                                    *
                                    ((this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity() != null ||
                                            !this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity().isEmpty() ||
                                            !this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity().equals("")) ?
                                            Double.parseDouble(this.itemList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity()) : 0.0);
                }
            }
        }
        */

        OrderExpandableListAdapter expandableListAdapter = new OrderExpandableListAdapter(context, itemList.get(i), new OrderExpandableListAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClickListener(int group, int child) {

                if (NetworkUtils.isInternetAvailable(context)) {
                    Log.v("ABCDE", "onDeleteClickListener");
                    DeleteItemFromCart(itemList.get(i).getCartHeaderID(), itemList.get(i).getDeliveryDate().get(group).getListCartDetailsList().get(child).getCartDetailsID(), i, group, child);
                } else {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "Please enable internet", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                }

            }
        });

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
    }


    private void DeleteItemFromCart(final int cartHeaderID, final String cartDetailsID, final int pos, final int group, final int child) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, context);
        productCatalogs productCatalogs = new productCatalogs();
        productCatalogs.setHandheldRequest(true);
        productCatalogs.setUserID(userId);
        productCatalogs.setCustomerID("0");
        productCatalogs.setResults("");
        productCatalogs.setCartHeaderID(cartHeaderID);
        productCatalogs.setCartDetailsID(cartDetailsID);
        message.setEntityObject(productCatalogs);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.DeleteItemFromCart(message);

        Log.v("ABCDE_M", new Gson().toJson(message));

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


                        } else {

                            ProgressDialogUtils.closeProgressDialog();

                            Log.v("ABCDE_O", new Gson().toJson(core.getEntityObject()));

                            try {


                                if (core.getEntityObject().toString().equals("2")) {
                                    db.cartHeaderDAO().deleteCartHeader(String.valueOf(cartHeaderID));
                                    db.cartDetailsDAO().deleteItemByCartDetailsId(cartDetailsID);
                                    ProgressDialogUtils.closeProgressDialog();
                                    Intent intent = new Intent(((Activity) context), CartActivity.class);
                                    ((Activity) context).startActivity(intent);
                                    ((Activity) context).finish();
                                    return;
                                }

                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());
                                List<CartHeaderListDTO> cartHeaderList = new ArrayList<>();
                                List<CartHeaderListDTO> cartHeader = new ArrayList<>();
                                for (int i = 0; i < getCartHeader.length(); i++) {
                                    for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {
                                        CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);
                                        if (cartHeaderListDTO.getCartHeaderID() == cartHeaderID) {
                                            cartHeaderList.add(cartHeaderListDTO);
                                        }
                                        cartHeader.add(cartHeaderListDTO);
                                    }
                                }

                                if (cartHeader.size() > 0) {
                                    total = 0.0;
                                    total_tax = 0.0;
                                    for (int i = 0; i < cartHeader.size(); i++) {
                                        if (cartHeader.get(i).getIsApproved() == 0) {

                                            total += Double.parseDouble(cartHeader.get(i).getTotalPrice() == null ? "0" : cartHeader.get(i).getTotalPrice());
                                            total_tax += Double.parseDouble(cartHeader.get(i).getTotalPriceWithTax() == null ? "0" : cartHeader.get(i).getTotalPriceWithTax());

                                            /* for (int j = 0; j < cartHeader.get(i).getDeliveryDate().size(); j++) {
                                                for (int k = 0; k < cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().size(); k++) {
                                                    total+=
                                                            ((cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice() != null ||
                                                                    !cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice().isEmpty() ||
                                                                    !cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice().equals("")) ?
                                                                    Double.parseDouble(cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice())  : 0.0)
                                                                    *
                                                                    ((cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity() != null ||
                                                                            !cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity().isEmpty() ||
                                                                            !cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity().equals("")) ?
                                                                            Double.parseDouble(cartHeader.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity()) : 0.0);
                                                }
                                            }*/

                                        }
                                    }
                                }

                                if (cartHeaderList.size() > 0) {
                                    itemList.get(pos).getDeliveryDate().get(group).getListCartDetailsList().remove(child);
                                    db.cartDetailsDAO().deleteItemByCartDetailsId(cartDetailsID);
                                    if (itemList.get(pos).getDeliveryDate().get(group).getListCartDetailsList().size() > 0) {
                                        notifyItemChanged(pos);
                                    } else {
                                        itemList.get(pos).getDeliveryDate().remove(group);
                                        if (itemList.get(pos).getDeliveryDate().size() > 0) {
                                            notifyItemChanged(pos);
                                        } else {
                                            itemList.remove(pos);
                                            db.cartHeaderDAO().deleteCartHeader(String.valueOf(cartHeaderID));
                                            notifyItemRemoved(pos);
                                        }
                                    }

                                    // df2.setRoundingMode(RoundingMode.DOWN);
/*                                  ((TextView)((Activity)context).findViewById(R.id.txtTotalAmt)).setText(""+df2.format(total));
                                    ((TextView)((Activity)context).findViewById(R.id.txtTotalAmtTax)).setText(""+df2.format(total_tax));
                                    ((TextView)((Activity)context).findViewById(R.id.txtTaxes)).setText(""+df2.format(total_tax-total)); */
                                    ((TextView) ((Activity) context).findViewById(R.id.txtTotalAmt)).setText("Rs." + String.format("%.2f", total) + "/-");
                                    ((TextView) ((Activity) context).findViewById(R.id.txtTotalAmtTax)).setText("Rs." + String.format("%.2f", total_tax) + "/-");
                                    ((TextView) ((Activity) context).findViewById(R.id.txtTaxes)).setText("Rs." + String.format("%.2f", total_tax - total) + "/-");

                                    notifyDataSetChanged();

                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception e) {
                                ProgressDialogUtils.closeProgressDialog();
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
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0003);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView customerName, creditLimit, tv_Status, tv_total, tv_total_tax;
        //private RecyclerView rvSubItem;
        ExpandableListView expandableDeliveryDate;

        ItemViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tv_customerName);
            creditLimit = itemView.findViewById(R.id.tv_creditLimit);
            //rvSubItem = itemView.findViewById(R.id.rv_cartDetails);
            tv_Status = itemView.findViewById(R.id.tv_Status);
            tv_total = itemView.findViewById(R.id.tv_total);
            tv_total_tax = itemView.findViewById(R.id.tv_total_tax);
            expandableDeliveryDate = itemView.findViewById(R.id.expandableDeliveryDate);
        }
    }

    private String getStatus(CartHeaderListDTO headerListDTO) {

        if (headerListDTO.getIsApproved() == 1 && headerListDTO.getIsInActive() == 1) {
            return "In active Approval (In-Process)";
        } else if (headerListDTO.getIsApproved() == 1 && headerListDTO.getIsCreditLimit() == 1) {
            return "Credit Limit Approval (In-Process)";
        } else if (headerListDTO.getIsApproved() == 2 && headerListDTO.getIsInActive() == 1) {
            return "In active Approval (Rejected)";
        } else if (headerListDTO.getIsApproved() == 2 && headerListDTO.getIsCreditLimit() == 1) {
            return "Credit Limit Approval (Rejected)";
        } else if (headerListDTO.getIsApproved() == 3 && headerListDTO.getIsInActive() == 1) {
            return "In active Approval (Initiated)";
        } else if (headerListDTO.getIsApproved() == 3 && headerListDTO.getIsCreditLimit() == 1) {
            return "Credit Limit Approval (Initiated)";
        } else if (headerListDTO.getIsApproved() == 7 && headerListDTO.getIsInActive() == 1) {
            return "In active Approval (Escalated)";
        } else if (headerListDTO.getIsApproved() == 7 && headerListDTO.getIsCreditLimit() == 1) {
            return "Credit Limit Approval (Escalated)";
        } else if (headerListDTO.getIsApproved() == 0 && headerListDTO.getIsInActive() == 1) {
            setBlink = true;
            return "In active Approval Required";
        } else if (headerListDTO.getIsApproved() == 0 && headerListDTO.getIsCreditLimit() == 1) {
            setBlink = true;
            return "Credit Limit Approval Required";
        } else if (headerListDTO.getIsApproved() == 0 && headerListDTO.getIsInActive() == 0 && headerListDTO.getIsCreditLimit() == 0) {
            return "No Approvals Required";
        } else if (headerListDTO.getIsApproved() == 6) {
            return "Auto closed";
        } else if (headerListDTO.getIsApproved() == 4) {
            return "Approved";
        } else {
            return "";
        }
    }
}
