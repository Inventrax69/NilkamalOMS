package com.example.inventrax.falconOMS.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.fragments.CartFragment;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.productCatalogs;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartHeaderAdapter extends RecyclerView.Adapter<CartHeaderAdapter.ItemViewHolder> {

    private static final String classCode = "OMS_Android_CartHeaderAdapter";
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<CartHeaderListDTO> itemList;
    private Context context;
    private AppDatabase db;
    private Activity activity;
    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;
    private String userId = "";
    private int itemPosition, headerPosition;
    AlertDialog alert;
    CartFragment cartFragment;

    public CartHeaderAdapter(List<CartHeaderListDTO> itemList, Context context, Activity activity,CartFragment cartFragment) {
        this.itemList = itemList;
        this.context = context;
        this.activity = activity;
        this.cartFragment=cartFragment;
        db = new RoomAppDatabase(this.context).getAppDatabase();
        SharedPreferences sp = context.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");
        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        restService = new RestService();
        core = new OMSCoreMessage();
    }

    public CartHeaderAdapter() { }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_cartheader, viewGroup, false);
        return new ItemViewHolder(view);
    }

    CartDetailsAdapter subItemAdapter;

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, final int i) {
        final CartHeaderListDTO headerListDTO = itemList.get(i);
        final CartHeader cartHeader = db.cartHeaderDAO().getByCustomerIDCartHeaderID(headerListDTO.getCustomerID(), headerListDTO.getCartHeaderID());
        itemViewHolder.customerName.setText(headerListDTO.getCustomerName());
        itemViewHolder.creditLimit.setText(String.valueOf((int) headerListDTO.getCreditLimit()));

        String value = db.customerDAO().getAllCustomerCodeById(cartHeader.shipToPartyId);
        value =  (value == null || value.isEmpty()) ? db.customerDAO().getAllCustomerCode(cartHeader.customerID) : value;
        itemViewHolder.txtShipToPatry.setText(value);

        List customerCodes = new ArrayList();
        List customerIds = new ArrayList();
        customerCodes = db.customerDAO().getAllCustomerCodesName();
        customerIds = db.customerDAO().getAllCustomerIds();
        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.row_text, customerCodes);
        String customerShiptoParty = db.customerDAO().getCustomerCode(String.valueOf(itemList.get(i).getCustomerID()));
        itemViewHolder.shipToPartyAutoComplete.setText(customerShiptoParty);
        itemViewHolder.shipToPartyAutoComplete.setAdapter(adapter);

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                itemViewHolder.rvSubItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        layoutManager.setInitialPrefetchItemCount(headerListDTO.getListCartDetailsList().size());

        // Create sub item view adapter
        subItemAdapter = new CartDetailsAdapter(context, itemList.get(i), itemList.get(i).getListCartDetailsList(), new CartDetailsAdapter.OnItemClickListener() {
            @Override
            public void onDeletClick(int pos) {
                if (NetworkUtils.isInternetAvailable(context)) {
                    deleteCartItem(pos, i);
                } else {
                    deleteItem(pos, i,1);
                }
            }
        });

        itemViewHolder.rvSubItem.setLayoutManager(layoutManager);
        itemViewHolder.rvSubItem.setAdapter(subItemAdapter);
        itemViewHolder.rvSubItem.setRecycledViewPool(viewPool);

        /*if (cartHeader.isPriority == 1)
            itemViewHolder.checkboxPrority.setChecked(true);
        else
            itemViewHolder.checkboxPrority.setChecked(false);*/


       /* if(cartHeader.shipToPartyId==0)
            itemViewHolder.btnAdd.setText("Add");
        else
            itemViewHolder.btnAdd.setText("Remove");*/


        /*itemViewHolder.checkboxPrority.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    db.cartHeaderDAO().updateIsPriority(headerListDTO.getCustomerID(), headerListDTO.getCartHeaderID(), 1);
                else
                    db.cartHeaderDAO().updateIsPriority(headerListDTO.getCustomerID(), headerListDTO.getCartHeaderID(), 0);
            }
        });*/

        itemViewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartHeader.shipToPartyId==0)
                    showShipToPartyDialog(cartHeader.customerID,cartHeader.cartHeaderID,i);
                else {
                    db.cartHeaderDAO().updateShipToPatry(cartHeader.customerID,cartHeader.cartHeaderID,"0");
                    itemList.get(i).setShipToPartyID("0");
                    notifyItemChanged(i);
                    notifyDataSetChanged();
                }
            }
        });
    }

    String sCustomerId;
    protected void showShipToPartyDialog(final int customerID, final int cartHeaderID,final int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.shiptoparty_dialog, null);

        List<String> customerCodes;
        final List<String> customerIds;
        customerCodes = db.customerDAO().getAllCustomerCodesName();
        customerIds = db.customerDAO().getAllCustomerIds();

        ArrayAdapter adapter1 = new ArrayAdapter<String>(context, R.layout.row_text, customerCodes);
        final SearchableSpinner customerListDropDown = (SearchableSpinner) promptView.findViewById(R.id.customerListDropDown);
        customerListDropDown.setAdapter(adapter1);
        customerListDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // customerListDropDown.getSelectedItem().toString();
                sCustomerId = customerIds.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final android.support.v7.app.AlertDialog d = new android.support.v7.app.AlertDialog.Builder(context)
                .setView(promptView)
                .setPositiveButton("OK", null) //Set to null. We override the onclick
                //  .setNegativeButton("CLEAR", null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        db.cartHeaderDAO().updateShipToPatry(customerID,cartHeaderID,sCustomerId);
                        itemList.get(i).setShipToPartyID(sCustomerId);
                        notifyItemChanged(i);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

            }
        });

        d.show();


    }


    public void DeleteCartItem(String cartdetailsid, int customerID, final int pos, final int i) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, context);
        CartDetailsListDTO cartDetailsListDTO=new CartDetailsListDTO();
        cartDetailsListDTO.setCartDetailsID(cartdetailsid);
        cartDetailsListDTO.setUserID(userId);
        cartDetailsListDTO.setCustomerID(customerID);
        cartDetailsListDTO.setResults("");
        message.setEntityObject(cartDetailsListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.DeleteCartItem(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, (Activity)context, context);
                                }
                                ProgressDialogUtils.closeProgressDialog();

                            } else {


                                db.cartDetailsDAO().deleteItem(itemList.get(i).getListCartDetailsList().get(pos).getMaterialMasterID());
                                itemList.get(i).getListCartDetailsList().remove(pos);

                                if (itemList.get(i).getListCartDetailsList().size() == 0) {
                                    itemList.remove(i);
                                    if (itemList.size() == 0) {
                                        // ((CartActivity) activity).changeLayout();
                                    }
                                }

                                notifyItemChanged(i);
                                notifyDataSetChanged();

                                ProgressDialogUtils.closeProgressDialog();
                            }
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
            DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0003);
        }
    }

    private void deleteItem(int pos, int i, int flag) {

       String cartDetailsid = itemList.get(i).getListCartDetailsList().get(pos).getCartDetailsID();
       String cartHeaderid = itemList.get(i).getListCartDetailsList().get(pos).getCartHeaderID();
       int customerID = itemList.get(i).getCustomerID();


            db.cartDetailsDAO().deleteItem(itemList.get(i).getListCartDetailsList().get(pos).getMaterialMasterID());
            if(flag==1){
                db.cartHeaderDAO().updateIsUpdated(customerID,1);
            }
            itemList.get(i).getListCartDetailsList().remove(pos);

            if (itemList.get(i).getListCartDetailsList().size() == 0) {
                itemList.remove(i);
                notifyItemRemoved(i);
                if (itemList.size() == 0) {
                    db.cartHeaderDAO().deleteCartHeader(cartHeaderid);
                    cartFragment.changeLayout();
                }
                if (itemList.size() == 1) {
                    TextView txtOrderFulfilment=activity.findViewById(R.id.txtOrderFulfilment);
                    txtOrderFulfilment.setEnabled(true);
                    txtOrderFulfilment.setVisibility(View.VISIBLE);
                    txtOrderFulfilment.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                }
            }else{
                notifyItemChanged(i);
            }

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
                            deleteItem(itemPosition, headerPosition, 0);
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

        private TextView customerName, creditLimit, txtShipToPatry, btnAdd;
        private AutoCompleteTextView shipToPartyAutoComplete;
        private RecyclerView rvSubItem;
        public CheckBox checkboxPrority;

        ItemViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tv_customerName);
            creditLimit = itemView.findViewById(R.id.tv_creditLimit);
            rvSubItem = itemView.findViewById(R.id.rv_cartDetails);
            shipToPartyAutoComplete = itemView.findViewById(R.id.shipToPartyAutoComplete);
            checkboxPrority = itemView.findViewById(R.id.checkboxPrority);
            txtShipToPatry = itemView.findViewById(R.id.txtShipToPatry);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }

    }


}
