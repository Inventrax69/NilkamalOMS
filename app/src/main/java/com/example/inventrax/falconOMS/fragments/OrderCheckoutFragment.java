package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.util.FragmentUtils;

public class OrderCheckoutFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_OrderCheckout";
    private View rootView;

    private RecyclerView rvItemsList;
    LinearLayoutManager linearLayoutManager;
    private TextView availableCL, txtTotalAmt, txtBack, txtContinue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_checkout_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        // To disable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibiltity(false);

        //String itemname = getArguments().getString("itemName");

        rvItemsList = (RecyclerView) rootView.findViewById(R.id.rvItemsList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvItemsList.setLayoutManager(linearLayoutManager);

        availableCL = (TextView) rootView.findViewById(R.id.availableCL);
        txtTotalAmt = (TextView) rootView.findViewById(R.id.txtTotalAmt);
        txtBack = (TextView) rootView.findViewById(R.id.txtBack);
        txtContinue = (TextView) rootView.findViewById(R.id.txtContinue);


        txtBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtBack:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container,new CartFragment());
                break;

            case R.id.txtContinue:

                break;


        }
    }





    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_ordercheckout));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

    }
}
