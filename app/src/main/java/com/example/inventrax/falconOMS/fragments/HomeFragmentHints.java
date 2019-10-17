package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.content.Context;
import android.content.SharedPreferences;
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
    String userName = "", userId = "";

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

        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userName = sp.getString(KeyValues.USER_NAME, "");
        userId = sp.getString(KeyValues.USER_ID, "");

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

        if (sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_HINTS))
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

    public void showHints() {
        builder = new GuideView.Builder(getContext())
                .setTitle(getString(R.string.product_catalog))
                .setContentText(getString(R.string.hint_product_catalog))
                .setGravity(Gravity.center)
                .setDismissType(DismissType.outside)
                .setTargetView(llProductCatalog)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.llProductCatalog:
                                builder.setTargetView(llCustomer)
                                        .setTitle(getString(R.string.customer_list))
                                        .setContentText(getString(R.string.hint_customer_list))
                                        .build();
                                break;
                            case R.id.llCustomer:
                                builder.setTargetView(llInventory)
                                        .setTitle(getString(R.string.inventory))
                                        .setContentText(getString(R.string.hint_inventory))
                                        .build();
                                break;
                            case R.id.llInventory:
                                builder.setTargetView(llOrderAssistance)
                                        .setTitle(getString(R.string.order_assistance))
                                        .setContentText(getString(R.string.hint_order_assistance))
                                        .build();
                                break;
                            case R.id.llOrderAssistance:
                                builder.setTargetView(llOrderBooking)
                                        .setTitle(getString(R.string.order_booking))
                                        .setContentText(getString(R.string.hint_order_booking))
                                        .build();
                                break;
                            case R.id.llOrderBooking:
                                builder.setTargetView(llComplaints)
                                        .setTitle(getString(R.string.complaints))
                                        .setContentText(getString(R.string.hint_compliants))
                                        .build();
                                break;
                            case R.id.llComplaints:
                                builder.setTargetView(llHistory)
                                        .setTitle(getString(R.string.history))
                                        .setContentText(getString(R.string.hint_order_history))
                                        .build();
                                break;
                            case R.id.llHistory:
                                builder.setTargetView(llSchemesAndDiscounts)
                                        .setTitle(getString(R.string.schemes_amp_discounts))
                                        .setContentText(getString(R.string.hint_sd))
                                        .build();
                                break;
                            case R.id.llSchemesAndDiscounts:
                                builder.setTargetView(llOrderTracking)
                                        .setTitle(getString(R.string.order_tracking))
                                        .setContentText(getString(R.string.hint_order_tracking))
                                        .build();

                                sharedPreferencesUtils.savePreference(KeyValues.IS_HINTS, false);

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
                if (sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_ITEM_LOADED)) {
                    FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ProductCatalogFragment());
                } else {
                    SnackbarUtils.showSnackbar(coordLayout, getString(R.string.syncingItems), Snackbar.LENGTH_SHORT);
                }
                break;
            case R.id.llCustomer:
                if (sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_Customer_LOADED)) {
                    FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new CustomerListFragment());
                } else {
                    SnackbarUtils.showSnackbar(coordLayout, getString(R.string.syncingCustomers), Snackbar.LENGTH_SHORT);
                }
                break;
            case R.id.llInventory:

                break;
            case R.id.llOrderAssistance:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderAssistanceFragment());
                break;
            case R.id.llOrderBooking:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderBookingFragment());
                break;
            case R.id.llComplaints:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ComplaintsFragment());
                break;
            case R.id.llHistory:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderHistoryFragment());
                break;
            case R.id.llSchemesAndDiscounts:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new SchemesAndDiscountsFragment());
                break;
            case R.id.llOrderTracking:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new DeliveryTrackingFragment());
                break;


        }
    }





    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.nilkamal);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(userName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
