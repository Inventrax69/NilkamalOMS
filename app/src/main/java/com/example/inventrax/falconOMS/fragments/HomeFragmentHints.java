package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CustomerListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListResponse;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import static com.example.inventrax.falconOMS.util.DateUtils.DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH;

public class HomeFragmentHints extends Fragment implements View.OnClickListener {
    private static final String classCode = "OMS_Android_HomeFragment";
    private View rootView;

    private LinearLayout llProductCatalog, llCustomer, llInventory, llOrderAssistance, llOrderBooking, llComplaints,
            llHistory, llSchemesAndDiscounts, llOrderTracking;

    private CoordinatorLayout coordLayout;

    private GuideView mGuideView;
    private GuideView.Builder builder;

    SharedPreferencesUtils sharedPreferencesUtils;

    ErrorMessages errorMessages;
    Common common;
    private OMSCoreMessage core;
    RestService restService;
    private List<ItemListResponse> lstItem;
    private List<CustomerListDTO> customerList;
    List<ItemTable> itemTables;
    List<CustomerTable> customerTables;

    AppDatabase db;
    private String itemTimeStamp = "", customerTimeStamp = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.home_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        // To enable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibiltity(true);

        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "room_oms").allowMainThreadQueries().build();

        coordLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordLayout);
        llProductCatalog = (LinearLayout) rootView.findViewById(R.id.llProductCatalog);
        llCustomer = (LinearLayout) rootView.findViewById(R.id.llCustomer);
        llInventory = (LinearLayout) rootView.findViewById(R.id.llInventory);
        llOrderAssistance = (LinearLayout) rootView.findViewById(R.id.llOrderAssistance);
        llOrderBooking = (LinearLayout) rootView.findViewById(R.id.llOrderBooking);
        llComplaints = (LinearLayout) rootView.findViewById(R.id.llComplaints);
        llHistory = (LinearLayout) rootView.findViewById(R.id.llHistory);
        llSchemesAndDiscounts = (LinearLayout) rootView.findViewById(R.id.llSchemesAndDiscounts);
        llOrderTracking = (LinearLayout) rootView.findViewById(R.id.llOrderTracking);

        llProductCatalog.setOnClickListener(this);
        llCustomer.setOnClickListener(this);
        llInventory.setOnClickListener(this);
        llOrderAssistance.setOnClickListener(this);
        llOrderBooking.setOnClickListener(this);
        llComplaints.setOnClickListener(this);
        llHistory.setOnClickListener(this);
        llSchemesAndDiscounts.setOnClickListener(this);
        llOrderTracking.setOnClickListener(this);

        errorMessages = new ErrorMessages();
        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        lstItem = new ArrayList<ItemListResponse>();
        customerList = new ArrayList<CustomerListDTO>();

        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());

        if(sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_HINTS))
        showHints();

        if(sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_ITEM_LOADED)){

            String itemLastTime = new SimpleDateFormat(DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH).format(new Date(db.itemDAO().getLastRecord().timestamp));
            String custLastTime = new SimpleDateFormat(DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH).format(new Date(db.customerDAO().getLastRecord().timestamp));
            itemTimeStamp = itemLastTime;
        }



        //itemTimeStamp = "2019-08-27 19:08:18.630";

        customerTimeStamp = "2019-08-27 19:08:18.630";

        syncItemData();

       // syncCustomerData();


    }


    private void updatingForDynamicLocationViews() {
        llOrderTracking.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mGuideView.updateGuideViewLocation();
            }
        });
    }

    public void showHints(){
        builder = new GuideView.Builder(getContext())
                .setTitle("Product Catalog")
                .setContentText("Product List")
                .setGravity(Gravity.center)
                .setDismissType(DismissType.outside)
                .setTargetView(llProductCatalog)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.llProductCatalog:
                                builder.setTargetView(llCustomer)
                                        . setTitle("CustomerTable")
                                        .setContentText("This is CustomerTable")
                                        .build();
                                break;
                            case R.id.llCustomer:
                                builder.setTargetView(llInventory)
                                        . setTitle("Inventory")
                                        .setContentText("This is Inventory")
                                        .build();
                                break;
                            case R.id.llInventory:
                                builder.setTargetView(llOrderAssistance)
                                        . setTitle("Order Assistance")
                                        .setContentText("This is Order Assistance")
                                        .build();
                                break;
                            case R.id.llOrderAssistance:
                                builder.setTargetView(llOrderBooking)
                                        . setTitle("Order Booking")
                                        .setContentText("This is Order Booking")
                                        .build();
                                break;
                            case R.id.llOrderBooking:
                                builder.setTargetView(llComplaints)
                                        . setTitle("Complaints")
                                        .setContentText("This is Complaints")
                                        .build();
                                break;
                            case R.id.llComplaints:
                                builder.setTargetView(llHistory)
                                        . setTitle("History")
                                        .setContentText("This is History")
                                        .build();
                                break;
                            case R.id.llHistory:
                                builder.setTargetView(llSchemesAndDiscounts)
                                        . setTitle("Schemes and Discounts")
                                        .setContentText("This is Schemes and Discounts")
                                        .build();
                                break;
                            case R.id.llSchemesAndDiscounts:
                                builder.setTargetView(llOrderTracking)
                                        . setTitle("Order Tracking")
                                        .setContentText("This is Order Tracking")
                                        .build();

                                sharedPreferencesUtils.savePreference(KeyValues.IS_HINTS,false);

                                break;
                            case R.id.llOrderTracking:
                                return;
                        }
                        mGuideView = builder.build();
                        mGuideView.show();
                    }
                });

        mGuideView = builder.build();
        mGuideView.show();

        updatingForDynamicLocationViews();
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.llProductCatalog:
                if(sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_ITEM_LOADED)){
                    FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ProductCatalogFragment());
                }else{
                    SnackbarUtils.showSnackbar(coordLayout,"Items Sycning Please wait..!", Snackbar.LENGTH_SHORT);
                }
                break;
            case R.id.llCustomer:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container, new CustomerListFragment());
                break;
            case R.id.llInventory:

                break;
            case R.id.llOrderAssistance:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container,new OrderAssistanceFragment());
                break;
            case R.id.llOrderBooking:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container,new OrderBookingFragment());
                break;
            case R.id.llComplaints:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container,new ComplaintsFragment());
                break;
            case R.id.llHistory:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container,new OrderHistoryFragment());
                break;
            case R.id.llSchemesAndDiscounts:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container,new SchemesAndDiscountsFragment());
                break;
            case R.id.llOrderTracking:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container,new DeliveryTrackingFragment());
                break;


        }
    }


    public void syncItemData() {

        if (NetworkUtils.isInternetAvailable(getContext())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ItemMaster_FPS_DTO, getContext());
        ItemListResponse itemListDTO = new ItemListResponse();
        if(!itemTimeStamp.equals("")) {
            itemListDTO.setCreatedOn(itemTimeStamp);
        }else {
            return;
        }
        message.setEntityObject(itemListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.SyncItemData(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();
                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                itemTables =new ArrayList<>();
                                ItemListDTO itemList;

                                try {

                                    itemList = new ItemListDTO(_lstItem.entrySet());
                                    lstItem = itemList.getResults();



                                } catch (Exception e) {
                                    common.showUserDefinedAlertType("No items found", getActivity(), getContext(), "Warning");
                                    // logException();
                                }
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
                    Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    public void syncCustomerData() {

        if (NetworkUtils.isInternetAvailable(getContext())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Customer_FPS_DTO, getContext());
        CustomerListDTO customerListDTO = new CustomerListDTO();
        if(!customerTimeStamp.equals("")) {
            customerListDTO.setCreatedOn(customerTimeStamp);
        }else {
            return;
        }
        message.setEntityObject(customerListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.SyncCustomerData(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();
                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<String, String> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();


                                CustomerListDTO itemList;

                                itemList = new CustomerListDTO(_lstItem.entrySet());

                                customerTables =new ArrayList<>();

                                for (CustomerListDTO dd : itemList.getResults()) {

                                    SimpleDateFormat sdf = new SimpleDateFormat(DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH);
                                    Date date = null;
                                    try {
                                        date = sdf.parse(dd.getCreatedOn());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    long startDate = date.getTime();

                                    customerTables.add(new CustomerTable(dd.getCustomerID(),dd.getCustomerName(),dd.getCustomerCode(),
                                            dd.getCustomerType(),dd.getDivision(),dd.getConnectedDepot(),dd.getMobile(),
                                            dd.getPrimaryID(),dd.getSalesDistrict(),dd.getZone(),startDate));

                                    if(dd.getAction().equalsIgnoreCase("A")){

                                        Toast.makeText(getContext(), "A", Toast.LENGTH_SHORT).show();

                                    }else if(dd.getAction().equalsIgnoreCase("M")){
                                        Toast.makeText(getContext(), "M", Toast.LENGTH_SHORT).show();
                                    }else if(dd.getAction().equalsIgnoreCase("D")){
                                        Toast.makeText(getContext(), "D", Toast.LENGTH_SHORT).show();
                                    }

                                }

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
                    Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.nilkamal);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Jackson");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
