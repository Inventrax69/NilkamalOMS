package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.app.DatePickerDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.CartAdapter;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.example.inventrax.falconOMS.util.DateUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CartFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String classCode = "OMS_Android_CartFragment";
    private View rootView;

    private FrameLayout frame;

    private TextView txtstartshopping;
    private LinearLayout empty_cart, llCartProducts;

    private RecyclerView rvCartItemsList;
    LinearLayoutManager linearLayoutManager;
    TextView txtContinueShopping, txtAvailableCL, txtTotal, txtOrderFulfilment, txtConfirmOrder;

    BottomSheetBehavior behavior;
    private CheckBox cbPartialFulfilment, cbSingleDelivery, cbVehicleTypePreference, cbBlancketOrder;
    private Button btnProceed;
    private EditText deliveryDatePicker;
    private SearchableSpinner selectVehicleType;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.cart_fragment, container, false);

        loadFormControls();

        return rootView;
    }


    private void loadFormControls() {

        // To disable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibiltity(false);

        frame = (FrameLayout) rootView.findViewById(R.id.frame);

        //String itemname = getArguments().getString("itemName");

        txtstartshopping = (TextView) rootView.findViewById(R.id.txtstartshopping);
        txtstartshopping.setOnClickListener(this);

        empty_cart = (LinearLayout) rootView.findViewById(R.id.empty_cart);
        llCartProducts = (LinearLayout) rootView.findViewById(R.id.llCartProducts);

        rvCartItemsList = (RecyclerView) rootView.findViewById(R.id.rvCartItemsList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvCartItemsList.setLayoutManager(linearLayoutManager);

        txtContinueShopping = (TextView) rootView.findViewById(R.id.txtContinueShopping);
        txtAvailableCL = (TextView) rootView.findViewById(R.id.txtAvailableCL);
        txtTotal = (TextView) rootView.findViewById(R.id.txtTotal);
        txtOrderFulfilment = (TextView) rootView.findViewById(R.id.txtOrderFulfilment);
        txtConfirmOrder = (TextView) rootView.findViewById(R.id.txtConfirmOrder);

        // bottom sheet view controllers
        selectVehicleType = (SearchableSpinner) rootView.findViewById(R.id.selectVehicleType);
        deliveryDatePicker = (EditText) rootView.findViewById(R.id.deliveryDatePicker);
        cbPartialFulfilment = (CheckBox) rootView.findViewById(R.id.cbPartialFulfilment);
        cbSingleDelivery = (CheckBox) rootView.findViewById(R.id.cbSingleDelivery);
        cbVehicleTypePreference = (CheckBox) rootView.findViewById(R.id.cbVehicleTypePreference);
        cbBlancketOrder = (CheckBox) rootView.findViewById(R.id.cbBlancketOrder);

        btnProceed = (Button) rootView.findViewById(R.id.btnProceed);

        btnProceed.setOnClickListener(this);
        txtOrderFulfilment.setOnClickListener(this);
        txtConfirmOrder.setOnClickListener(this);
        txtContinueShopping.setOnClickListener(this);


        // bottom sheet
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


        loadJSON();


        cbSingleDelivery.setOnCheckedChangeListener(this);
        cbPartialFulfilment.setOnCheckedChangeListener(this);
        cbVehicleTypePreference.setOnCheckedChangeListener(this);
        cbBlancketOrder.setOnCheckedChangeListener(this);


        // Date Picker Dailog integration and validation
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MMM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

                String date = sdf.format(myCalendar.getTime());

                Date today = null;
                Date selectedDate = null;

                try {
                    today = sdf.parse(DateUtils.getDate(myFormat));       // gets the current date
                    selectedDate = sdf.parse(date);                       // gets the date from date picker
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // shows err when selected date before the current date
                if (selectedDate.before(today)) {
                    deliveryDatePicker.setText("");
                    deliveryDatePicker.setError("Please select valid date");
                    Toast.makeText(getContext(), "Plese select valid date", Toast.LENGTH_SHORT).show();
                } else {
                    deliveryDatePicker.setError(null);
                    deliveryDatePicker.setText(sdf.format(myCalendar.getTime()));
                }

            }

        };

        // onClick for edit text to popup the date picker
        deliveryDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // onClick for edit text to popup the date picker
                deliveryDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog =  new DatePickerDialog(getContext(), datePickerListener, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                        String myDate = "2019/08/12";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = null;
                        try {
                            date = sdf.parse(myDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long millis = date.getTime();
                        datePickerDialog.getDatePicker().setMinDate(millis);
                        datePickerDialog.show();
                    }
                });

            }
        });

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (cbSingleDelivery.isChecked()) {
            deliveryDatePicker.setVisibility(View.VISIBLE);
            cbPartialFulfilment.setChecked(false);
        } else {
            deliveryDatePicker.setVisibility(View.GONE);
        }

        if (cbVehicleTypePreference.isChecked()) {
            selectVehicleType.setVisibility(View.VISIBLE);
        } else {
            selectVehicleType.setVisibility(View.GONE);
        }

        if (cbPartialFulfilment.isChecked()) {
            cbSingleDelivery.setChecked(false);
        }

    }


    private void loadJSON() {

        final ArrayList<String> items = new ArrayList<>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");

        CartAdapter mAdapter = new CartAdapter(getContext(), items, new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(getContext(), String.valueOf(items.get(pos) + "" + "Item"), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCartClick(int pos) {

            }

        });

        rvCartItemsList.setAdapter(mAdapter);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtstartshopping:

                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ProductCatalogFragment());

                break;

            case R.id.txtOrderFulfilment:
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                frame.setVisibility(View.VISIBLE);
                break;
            case R.id.btnProceed:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                frame.setVisibility(View.GONE);

                //getFulfilmentOptions();
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderConfirmationFragment());
                break;

            case R.id.txtConfirmOrder:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderConfirmationFragment());
                break;

            case R.id.txtContinueShopping:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ProductCatalogFragment());
                break;


        }
    }


    public void getFulfilmentOptions() {
        int one = 0, two = 0, three = 0, four = 0, five = 0, six = 0, seven = 0,
                eight = 0, nine = 0, ten = 0, eleven = 0, total = 0;

        if (cbPartialFulfilment.isChecked()) {
            one = 1;
            total = one;

        }
        if (cbSingleDelivery.isChecked()) {
            two = 2;
            total = two;
        }
        if (cbVehicleTypePreference.isChecked()) {
            three = 3;
            total = three;
        }
        if (cbBlancketOrder.isChecked()) {
            four = 4;
            total = four;
        }
        if (cbPartialFulfilment.isChecked() && cbSingleDelivery.isChecked()) {
            total = 5;
        }
        if (cbPartialFulfilment.isChecked() && cbVehicleTypePreference.isChecked()) {
            total = 6;
        }
        if (cbPartialFulfilment.isChecked() && cbBlancketOrder.isChecked()) {
            total = 7;
        }
        if (cbSingleDelivery.isChecked() && cbVehicleTypePreference.isChecked()) {
            total = 8;
        }
        if (cbSingleDelivery.isChecked() && cbBlancketOrder.isChecked()) {
            total = 9;
        }
        if (cbVehicleTypePreference.isChecked() && cbBlancketOrder.isChecked()) {
            total = 10;
        }
        if (cbPartialFulfilment.isChecked() && cbSingleDelivery.isChecked()
                && cbVehicleTypePreference.isChecked()) {
            total = 12;
        }
        if (cbSingleDelivery.isChecked() && cbVehicleTypePreference.isChecked()
                && cbBlancketOrder.isChecked()) {
            total = 13;
        }

        if (cbVehicleTypePreference.isChecked() && cbBlancketOrder.isChecked()
                && cbPartialFulfilment.isChecked()) {
            total = 14;
        }
        if (cbPartialFulfilment.isChecked() && cbSingleDelivery.isChecked()
                && cbVehicleTypePreference.isChecked() && cbBlancketOrder.isChecked()) {
            total = 11;
        }
        switch (total) {
            case 1:
                Toast.makeText(getActivity(), "cbPartialFulfilment", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getActivity(), "cbSingleDelivery", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(getActivity(), "cbVehicleTypePreference", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getActivity(), "cbBlancketOrder", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(getActivity(), "cbPartialFulfilment && cbSingleDelivery", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(getActivity(), "cbPartialFulfilment && cbVehicleTypePreference", Toast.LENGTH_SHORT).show();
                break;
            case 7:
                Toast.makeText(getActivity(), "cbPartialFulfilment && cbBlancketOrder", Toast.LENGTH_SHORT).show();
                break;
            case 8:
                Toast.makeText(getActivity(), "cbSingleDelivery && cbVehicleTypePreference", Toast.LENGTH_SHORT).show();
                break;
            case 9:
                Toast.makeText(getActivity(), "cbSingleDelivery && cbBlancketOrder", Toast.LENGTH_SHORT).show();
                break;
            case 10:
                Toast.makeText(getActivity(), "cbVehicleTypePreference && cbBlancketOrder", Toast.LENGTH_SHORT).show();
                break;

            case 11:
                Toast.makeText(getActivity(), "All four", Toast.LENGTH_SHORT).show();
                break;

            case 12:
                Toast.makeText(getActivity(), "cbPartialFulfilment && cbSingleDelivery && cbVehicleTypePreference", Toast.LENGTH_SHORT).show();
                break;
            case 13:
                Toast.makeText(getActivity(), "cbSingleDelivery && cbVehicleTypePreference && cbBlancketOrder", Toast.LENGTH_SHORT).show();
                break;
            case 14:
                Toast.makeText(getActivity(), "cbVehicleTypePreference && cbBlancketOrder && cbPartialFulfilment", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_cart));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

    }


}
