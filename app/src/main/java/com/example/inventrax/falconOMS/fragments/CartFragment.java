package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.CartAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.PriceDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DateUtils;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private CheckBox cbPartialFulfilment, cbSingleDelivery, cbVehicleTypePreference;
    private Button btnProceed;
    private EditText deliveryDatePicker;
    private SearchableSpinner selectVehicleType;

    AppDatabase db;
    List<CartDetails> items = null;
    CartAdapter mAdapter;

    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;

    private  String userId = "";

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

        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "room_oms").allowMainThreadQueries().build();

        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");

        mAdapter = new CartAdapter();

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

        items = new ArrayList<>();



        cbSingleDelivery.setOnCheckedChangeListener(this);
        cbPartialFulfilment.setOnCheckedChangeListener(this);
        cbVehicleTypePreference.setOnCheckedChangeListener(this);


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

        if (getArguments()!= null) {

            if(getArguments().getBoolean(KeyValues.IS_ITEM_ADDED_TO_CART)){
                if(NetworkUtils.isInternetAvailable(getContext())){
                  ((MainActivity) getActivity()).startTime();
                }
            }
        }

        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        restService = new RestService();
        core = new OMSCoreMessage();

        CartHeader cartHeader = db.cartHeaderDAO().getCartHeader(userId);

        if(NetworkUtils.isInternetAvailable(getContext())) {

            if (!cartHeader.isCreditLimit && !cartHeader.isInActive && (cartHeader.isApproved == 0 || cartHeader.isApproved == 4)) {
                loadCart();
                showInActiveDailog();
            } else {

                if (cartHeader.isCreditLimit) {
                    showCreditLimitDailog();
                } else if (cartHeader.isInActive) {
                    showInActiveDailog();
                }

            }
        }else {
            loadCart();
            txtOrderFulfilment.setEnabled(false);
            txtConfirmOrder.setEnabled(false);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {

            final MenuItem item = menu.findItem(R.id.addItem);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);

        }

        super.onCreateOptionsMenu(menu, inflater);

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


    private void deleteItem(int pos){

        db.cartDetailsDAO().deleteItem(items.get(pos).materialID,items.get(pos).quantity,items.get(pos).id);
        loadCart();
    }

    private void loadCart() {

        items = db.cartDetailsDAO().getAllCartItems();

        mAdapter = new CartAdapter(getContext(), items, new CartAdapter.OnItemClickListener() {

            @Override
            public void onDeletClick(int pos) {

                deleteItem(pos);

                if (NetworkUtils.isInternetAvailable(getContext())) {
                    deleteCartItem(pos);
                }

            }

        });


        rvCartItemsList.setAdapter(mAdapter);

    }

    private void showCreditLimitDailog() {

        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.approval_dialog);

        TextView content = (TextView) mDialog.findViewById(R.id.dialog_content);
        Button cmfrmBtn = (Button) mDialog.findViewById(R.id.confirm_btn);
        Button cnclBtn = (Button) mDialog.findViewById(R.id.cancel_btn);

        content.setText("Sending for credit limit approval");

        cnclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        cmfrmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });


        mDialog.show();

    }

    private void showInActiveDailog() {

        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.approval_dialog);

        TextView content = (TextView) mDialog.findViewById(R.id.dialog_content);
        Button cmfrmBtn = (Button) mDialog.findViewById(R.id.confirm_btn);
        Button cnclBtn = (Button) mDialog.findViewById(R.id.cancel_btn);

        content.setText("One of your item is in-active it should get approval before proceeding");

        cnclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        cmfrmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });


        mDialog.show();

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

    public void fulfilment() {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getContext());
        PriceDTO oDto = new PriceDTO();

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.OrderFulfilment(message);
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

    public void deleteCartItem(int pos) {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getContext());
        CartDTO oDto = new CartDTO();

        oDto.setCartDetailsID(items.get(pos).cartDetailsId);

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

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }


                            } else {

                                loadCart();

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_cart));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

    }


}
