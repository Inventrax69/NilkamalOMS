package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String classCode = "OMS_Android_HomeFragment";
    private View rootView;

    private LinearLayout llProductCatalog, llCustomer, llInventory, llOrderAssistance, llOrderBooking, llComplaints,
            llHistory, llSchemesAndDiscounts, llOrderTracking;
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
        sharedPreferencesUtils = new SharedPreferencesUtils("LoginActivity", getActivity());
        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);



    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.llProductCatalog:
                if(sharedPreferencesUtils.loadPreferenceAsBoolean("isItemLoaded")){
                    FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ProductCatalogFragment());
                }else{
                    Toast.makeText(getActivity(), "Items Sycning Please wait", Toast.LENGTH_SHORT).show();
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
