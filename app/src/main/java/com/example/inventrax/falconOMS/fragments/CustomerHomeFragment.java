package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

public class CustomerHomeFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_CustomerHomeFragment";
    private View rootView;
    private CardView llOrderBooking, llComplaints,
            llDashboard, llSchemesAndDiscounts;
    SharedPreferencesUtils sharedPreferencesUtils;
    AppDatabase db;
    private String customerId = "",customerName = "",divisionId = "",divisionType = "", mobileNo = "",customerCode= "";
    private TextView txtCustomerName,txtCustomerCode,txtCustomerDivision,txtCustomerMobileNum;
    private ImageView CustImg;
    private CustomerTable customerTable;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.customer_home, container, false);

        if(getArguments().getSerializable(KeyValues.SELECTED_CUSTOMER) != null){
            customerTable = (CustomerTable) getArguments().getSerializable(KeyValues.SELECTED_CUSTOMER);
            if( customerTable != null) {
                customerId = customerTable.customerId;
                customerCode = customerTable.customerCode;
                customerName = customerTable.customerName;
                divisionId = customerTable.divisionId;
                divisionType = customerTable.division;
                mobileNo = customerTable.mobile;
            }
        }

       /* if(!getArguments().getString(KeyValues.SELECTED_CUSTOMER_ID).isEmpty() || getArguments().getString(KeyValues.SELECTED_CUSTOMER_ID)!=null) {



            customerId = getArguments().getString(KeyValues.SELECTED_CUSTOMER_ID);
            customerName = getArguments().getString(KeyValues.SELECTED_CUSTOMER_NAME);
            divisionId = getArguments().getString(KeyValues.CUSTOMER_DIVISION_ID);
        }*/
        loadFormControls();

        return rootView;
    }


    private void loadFormControls() {


        try {

            // To enable Bottom navigation bar
            ((MainActivity) getActivity()).SetNavigationVisibility(true);



            llOrderBooking = (CardView) rootView.findViewById(R.id.llOrderBooking);
            llComplaints = (CardView) rootView.findViewById(R.id.llComplaints);
            llDashboard = (CardView) rootView.findViewById(R.id.llDashboard);
            llSchemesAndDiscounts = (CardView) rootView.findViewById(R.id.llSchemesAndDiscounts);

            llOrderBooking.setBackground(getActivity().getResources().getDrawable(R.drawable.card_view_rb));
            llSchemesAndDiscounts.setBackground(getActivity().getResources().getDrawable(R.drawable.card_view_lb));
            llComplaints.setBackground(getActivity().getResources().getDrawable(R.drawable.card_view_rt));
            llDashboard.setBackground(getActivity().getResources().getDrawable(R.drawable.card_view_lt));


            txtCustomerName = (TextView) rootView.findViewById(R.id.txtCustomerName);
            txtCustomerCode = (TextView) rootView.findViewById(R.id.txtCustomerCode);
            txtCustomerDivision = (TextView) rootView.findViewById(R.id.txtCustomerDivision);
            txtCustomerMobileNum = (TextView) rootView.findViewById(R.id.txtCustomerMobileNum);

            CustImg = (ImageView) rootView.findViewById(R.id.CustImg);

            txtCustomerCode.setText(customerCode);
            txtCustomerName.setText(customerName);
            txtCustomerDivision.setText(divisionType);
            txtCustomerMobileNum.setText(mobileNo);

            Picasso.with(getActivity())
                    .load(R.drawable.hello_logo)
                    .placeholder(R.drawable.load)
                    .into(CustImg);

            llOrderBooking.setOnClickListener(this);
            llComplaints.setOnClickListener(this);
            llDashboard.setOnClickListener(this);
            llSchemesAndDiscounts.setOnClickListener(this);



            db = new RoomAppDatabase(getActivity()).getAppDatabase();

            sharedPreferencesUtils = new SharedPreferencesUtils("LoginActivity", getActivity());
            SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);

        }catch (Exception ex){

        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.llOrderBooking:
                Bundle bundle=new Bundle();
                bundle.putString("customerId",customerId);
                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ProductCatalogFragment(),bundle);
                break;
            case R.id.llComplaints:
                String url = KeyValues.COMPLAINTS_URL_EXTERNAL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.llDashboard:
                //FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderHistoryFragment());
                break;
            case R.id.llSchemesAndDiscounts:
                bundle=new Bundle();
                bundle.putString("customerId",customerId);
                bundle.putString("divisionId",divisionId);
                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new SchemesAndDiscountsFragment(),bundle);
                break;
            case R.id.llOrderTracking:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new DeliveryTrackingFragment());
                break;


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(customerName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
