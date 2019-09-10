package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.OrderCheckoutAdapter;
import com.example.inventrax.falconOMS.adapters.OrderConfirmationAdapter;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;

import java.util.ArrayList;

public class OrderConfirmationFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_OrderConfirmationFragment";
    private View rootView;

    private RecyclerView rvItemsList;
    LinearLayoutManager linearLayoutManager;
    private TextView availableCL, txtTotalAmt, txtDeliveryOptions, txtConfirmOrder;

    private FrameLayout frame;

    BottomSheetBehavior behavior;
    private RadioButton rbDefault, rbOptions;
    private SearchableSpinner spinnerDefaultDelivery, optionsSpinnerOne, optionsSpinnerTwo;
    private Button btnViewDefault, btnProceedDefault, btnViewOptionOne, btnViewOptionTwo, btnProceedOptions;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_confirmation_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        // To disable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibiltity(false);

        frame = (FrameLayout) rootView.findViewById(R.id.frame);

        rvItemsList = (RecyclerView) rootView.findViewById(R.id.rvItemsList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvItemsList.setLayoutManager(linearLayoutManager);

        availableCL = (TextView) rootView.findViewById(R.id.availableCL);
        txtTotalAmt = (TextView) rootView.findViewById(R.id.txtTotalAmt);
        txtDeliveryOptions = (TextView) rootView.findViewById(R.id.txtDeliveryOptions);
        txtConfirmOrder = (TextView) rootView.findViewById(R.id.txtConfirmOrder);

        rbDefault = (RadioButton) rootView.findViewById(R.id.rbDefault);
        rbOptions = (RadioButton) rootView.findViewById(R.id.rbOptions);

        rbDefault.setChecked(true);

        btnViewDefault = (Button) rootView.findViewById(R.id.btnViewDefault);
        btnProceedDefault = (Button) rootView.findViewById(R.id.btnProceedDefault);
        btnViewOptionOne = (Button) rootView.findViewById(R.id.btnViewOptionOne);
        btnViewOptionTwo = (Button) rootView.findViewById(R.id.btnViewOptionTwo);
        btnProceedOptions = (Button) rootView.findViewById(R.id.btnProceedOptions);

        btnViewDefault.setOnClickListener(this);
        btnProceedDefault.setOnClickListener(this);
        btnViewOptionOne.setOnClickListener(this);
        btnViewOptionTwo.setOnClickListener(this);
        btnProceedOptions.setOnClickListener(this);

        spinnerDefaultDelivery = (SearchableSpinner) rootView.findViewById(R.id.spinnerDefaultDelivery);
        optionsSpinnerOne = (SearchableSpinner) rootView.findViewById(R.id.optionsSpinnerOne);
        optionsSpinnerTwo = (SearchableSpinner) rootView.findViewById(R.id.optionsSpinnerTwo);

        txtDeliveryOptions.setOnClickListener(this);

        // Bottom sheet
        View bottomSheet = rootView.findViewById(R.id.bottom_sheet);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events

                if(String.valueOf(slideOffset).equalsIgnoreCase("0.0")){
                    frame.setVisibility(View.GONE);
                }
            }
        });

        loadJSONSample();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtDeliveryOptions:
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                frame.setVisibility(View.VISIBLE);
                break;

            case R.id.btnProceedDefault:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                frame.setVisibility(View.GONE);
                rvItemsList.setAdapter(null);

                loadJSONAfterFullfilment();
                break;
            case R.id.btnProceedOptions:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                frame.setVisibility(View.GONE);

                rvItemsList.setAdapter(null);

                loadJSONAfterFullfilment();

                break;
        }
    }

    private void loadJSONSample() {

        final ArrayList<String> items = new ArrayList<>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");

        OrderConfirmationAdapter mAdapter = new OrderConfirmationAdapter(getContext(), items, new OrderConfirmationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                //Toast.makeText(getContext(), String.valueOf(items.get(pos) + "" + "Item"), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCartClick(int pos) {

            }

        });

        rvItemsList.setAdapter(mAdapter);

    }

    private void loadJSONAfterFullfilment() {

        final ArrayList<String> items = new ArrayList<>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");

        OrderCheckoutAdapter mAdapter = new OrderCheckoutAdapter(getContext(), items, new OrderCheckoutAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                //Toast.makeText(getContext(), String.valueOf(items.get(pos) + "" + "Item"), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCartClick(int pos) {

            }

        });

        rvItemsList.setAdapter(mAdapter);

    }



    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderconfirmation));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

    }
}
