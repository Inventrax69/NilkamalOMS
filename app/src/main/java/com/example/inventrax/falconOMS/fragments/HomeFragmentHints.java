package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

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

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class HomeFragmentHints extends Fragment implements View.OnClickListener {
    private static final String classCode = "OMS_Android_HomeFragment";
    private View rootView;

    private LinearLayout llProductCatalog, llCustomer, llInventory, llOrderAssistance, llOrderBooking, llComplaints,
            llHistory, llSchemesAndDiscounts, llOrderTracking;

    private CoordinatorLayout coordLayout;

    private GuideView mGuideView;
    private GuideView.Builder builder;

    SharedPreferencesUtils sharedPreferencesUtils;

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

        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());

        if(sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_HINTS))
        showHints();

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
                    //Toast.makeText(getActivity(), "Items Sycning Please wait", Toast.LENGTH_SHORT).show();
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
