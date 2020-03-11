package com.example.inventrax.falconOMS.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CustomerListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListDTO;
import com.example.inventrax.falconOMS.pojos.ModelDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.VariantDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.room.UserDivisionCustTable;
import com.example.inventrax.falconOMS.room.VariantTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("StaticFieldLeak")
public class SyncMasterDataActivity extends Activity implements View.OnClickListener {

    private static final String classCode = "OMS_Android_SyncMasterDataActivity";

    private TextView tvItemMasterSyncTime, txtItemMasterSync, tvCustomerMasterSyncTime, txtCustomerMasterSync;
    private CardView cvItemMasterSync, cvCustomerMasterSync;

    private Gson gson;
    private OMSCoreMessage core;
    private Common common;
    private RestService restService;
    private ErrorMessages errorMessages;
    List<CustomerTable> customerTables;
    private List<ModelDTO> lstItem;
    private List<CustomerListDTO> customerList;
    List<ItemTable> itemTables;

    AppDatabase db;
    AlertDialog dialog;

    private SharedPreferencesUtils sharedPreferencesUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_masters);

        try {

            loadFormControls();

        } catch (Exception e) {

        }
    }

    private void loadFormControls() {

        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getApplicationContext());

        tvItemMasterSyncTime = (TextView) findViewById(R.id.tvItemMasterSyncTime);
        txtItemMasterSync = (TextView) findViewById(R.id.txtItemMasterSync);
        tvCustomerMasterSyncTime = (TextView) findViewById(R.id.tvCustomerMasterSyncTime);
        txtCustomerMasterSync = (TextView) findViewById(R.id.txtCustomerMasterSync);

        cvItemMasterSync = (CardView) findViewById(R.id.cvItemMasterSync);
        cvCustomerMasterSync = (CardView) findViewById(R.id.cvCustomerMasterSync);

        txtItemMasterSync.setOnClickListener(this);
        txtCustomerMasterSync.setOnClickListener(this);

        db = new RoomAppDatabase(SyncMasterDataActivity.this).getAppDatabase();

        gson = new Gson();
        core = new OMSCoreMessage();
        common = new Common();
        restService = new RestService();
        errorMessages = new ErrorMessages();

        customerTables = new ArrayList<>();
        lstItem = new ArrayList<>();
        customerList = new ArrayList<CustomerListDTO>();


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.txtItemMasterSync:


                new AsyncTask<Void, Integer, String>() {

                    @Override
                    protected String doInBackground(Void... voids) {
                        synchronized (this) {
                            getProductCatalog();
                        }
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        setProgressDialog();
                        super.onPreExecute();
                    }
                }.execute();
                break;

            case R.id.txtCustomerMasterSync:
                new AsyncTask<Void, Integer, String>() {

                    @Override
                    protected String doInBackground(Void... voids) {
                        synchronized (this) {
                            getCustomerList();
                        }
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        setProgressDialog();
                        super.onPreExecute();
                    }
                }.execute();
                break;

        }

    }


    public void getCustomerList() {

        if (NetworkUtils.isInternetAvailable(SyncMasterDataActivity.this)) {
        } else {
            DialogUtils.showAlertDialog(SyncMasterDataActivity.this, errorMessages.EMC_0007);
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Customer_FPS_DTO, SyncMasterDataActivity.this);
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setPageIndex(0);
        itemListDTO.setPageSize(0);
        itemListDTO.setHandheldRequest(true);
        message.setEntityObject(itemListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.GetCustomerListMobile(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, SyncMasterDataActivity.this, SyncMasterDataActivity.this);
                            }
                            if (dialog.isShowing())
                                dialog.dismiss();

                        } else {

                            try {

                                LinkedTreeMap<String, String> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                CustomerListDTO itemList;
                                final List<CustomerListDTO> lstDto = new ArrayList<CustomerListDTO>();


                                itemList = new CustomerListDTO(_lstItem.entrySet());

                                if (itemList.getResults() != null) {
                                    customerTables = new ArrayList<>();

                                    db.userDivisionCustDAO().deleteAll();

                                    for (CustomerListDTO dd : itemList.getResults()) {

                                        lstDto.add(dd);

                                        customerTables.add(new CustomerTable(dd.getCustomerID(), dd.getCustomerName(), dd.getCustomerCode(),
                                                dd.getCustomerType(), dd.getCustomerTypeID(), dd.getDivision(), dd.getDivisionID().split("[.]")[0], dd.getConnectedDepot(), dd.getMobile(),
                                                dd.getPrimaryID(), dd.getSalesDistrict(), dd.getZone(), dd.getCity()));

                                        db.userDivisionCustDAO().insert(new UserDivisionCustTable(dd.getCustomerID(), dd.getDivisionID().split("[.]")[0]));
                                    }

                                    customerList = lstDto;
                                    if (customerList != null && customerList.size() > 0) {

                                        new AsyncTask<Void, Integer, String>() {

                                            @Override
                                            protected String doInBackground(Void... voids) {
                                                synchronized (this) {
                                                    db.customerDAO().deleteAll();
                                                    db.customerDAO().insertAll(customerTables);
                                                }
                                                return null;
                                            }

                                        }.execute();

                                        if (dialog.isShowing())
                                            dialog.dismiss();



                                    } else {


                                        Intent intent = new Intent(SyncMasterDataActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        if (dialog.isShowing())
                                            dialog.dismiss();
                                    }


                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                }

                                if (dialog.isShowing())
                                    dialog.dismiss();

                                Date date = Calendar.getInstance().getTime();
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String todaysdate = formatter.format(date);
                                tvCustomerMasterSyncTime.setText(""+todaysdate);

                            } catch (Exception ex) {
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        }
                        if (dialog.isShowing())
                            dialog.dismiss();

                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(SyncMasterDataActivity.this)) {
                        DialogUtils.showAlertDialog(SyncMasterDataActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(SyncMasterDataActivity.this, errorMessages.EMC_0014);
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", SyncMasterDataActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing())
                dialog.dismiss();
            DialogUtils.showAlertDialog(SyncMasterDataActivity.this, errorMessages.EMC_0003);
        }
    }

    public void getProductCatalog() {

        if (NetworkUtils.isInternetAvailable(SyncMasterDataActivity.this)) {
        } else {
            DialogUtils.showAlertDialog(SyncMasterDataActivity.this, errorMessages.EMC_0007);
            // soundUtils.alertSuccess(SyncMasterDataActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, SyncMasterDataActivity.this);
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setPageIndex(1);
        itemListDTO.setPageSize(10);
        itemListDTO.setHandheldRequest(true);
        itemListDTO.setSearchString(null);
        itemListDTO.setFilter("0");
        message.setEntityObject(itemListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.ProductCatalog(message);

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
                                common.showAlertType(omsExceptionMessage, SyncMasterDataActivity.this, SyncMasterDataActivity.this);
                            }
                            if (dialog.isShowing())
                                dialog.dismiss();

                        } else {



                            LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                            _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                            itemTables = new ArrayList<>();
                            ItemListDTO itemList;

                            try {

                                itemList = new ItemListDTO(_lstItem.entrySet());
                                lstItem = itemList.getResults();


                                if (lstItem != null && lstItem.size() > 0) {

                                    new AsyncTask<Void, Integer, String>() {

                                        @Override
                                        protected String doInBackground(Void... voids) {
                                            synchronized (this) {


                                                db.itemDAO().deleteAll();
                                                db.variantDAO().deleteAll();


                                                List<ItemTable> itemTableList = new ArrayList<>();
                                                List<VariantTable> variantTableList = new ArrayList<>();


                                                for (ModelDTO md : lstItem) {

                                                    itemTableList.add(new ItemTable(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                            md.getModelDescription(), md.getImgPath(), "0", "0", ""));
/*                                                        db.itemDAO().insert(new ItemTable(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                                md.getModelDescription(), md.getImgPath(), md.getDiscountCount(), md.getDiscountId(), md.getDiscountDesc()));*/


                                                    for (VariantDTO variantDTO : md.getVarientList()) {

                                                        variantTableList.add(new VariantTable(md.getModelID(), md.getDivisionID(),
                                                                variantDTO.getMaterialID(), variantDTO.getMDescription(), variantDTO.getMDescriptionLong(),
                                                                variantDTO.getMcode(), variantDTO.getModelColor(), variantDTO.getMaterialImgPath(),
                                                                "0", "0", "",
                                                                variantDTO.getProductSpecification(), variantDTO.getProductCatalog(), variantDTO.getEBrochure(), variantDTO.getOpenPrice(), (int) Double.parseDouble(variantDTO.getStackSize())));

                                                    }

                                                }

                                                synchronized (this) {
                                                    db.itemDAO().insertAll(itemTableList);
                                                }
                                                db.variantDAO().insertAll(variantTableList);


                                                dialog.dismiss();
                                                Date date = Calendar.getInstance().getTime();
                                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                String todaysdate = formatter.format(date);
                                                tvItemMasterSyncTime.setText(""+todaysdate);


                                            }
                                            return null;
                                        }
                                    }.execute();

                                }

                                if (dialog.isShowing())
                                    dialog.dismiss();


                            } catch (Exception e) {
                                common.showUserDefinedAlertType(errorMessages.EMC_0024, SyncMasterDataActivity.this, SyncMasterDataActivity.this, "Warning");
                                // logException();
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                        if (dialog.isShowing())
                            dialog.dismiss();

                    }
                    if (dialog.isShowing())
                        dialog.dismiss();

                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(SyncMasterDataActivity.this)) {
                        DialogUtils.showAlertDialog(SyncMasterDataActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(SyncMasterDataActivity.this, errorMessages.EMC_0014);
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();

                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", SyncMasterDataActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing())
                dialog.dismiss();
            DialogUtils.showAlertDialog(SyncMasterDataActivity.this, errorMessages.EMC_0003);
        }
    }

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText(getString(R.string.launch_string));
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(18);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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