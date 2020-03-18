package com.example.inventrax.falconOMS.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.ApprovalHeaderAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.FullfilmentDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Padmaja on 04/07/2019.
 */

public class CartApproval_DyFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String classCode = "OMS_Android_Activity_Cart";
    private View rootView;
    Toolbar toolbar;
    private FrameLayout frame;
    private TextView txtstartshopping;
    private LinearLayout empty_cart, llCartProducts;
    private RecyclerView rvCartItemsList;
    private SearchableSpinner spinnerSelectCustomer;
    LinearLayoutManager linearLayoutManager;
    TextView txtAvailableCL, txtTotal, txtOrderFulfilment, txtConfirmOrder;
    BottomSheetBehavior behavior;
    private CheckBox cbPartialFulfilment, cbSingleDelivery, cbVehicleTypePreference;
    private Button btnProceed;
    private SearchableSpinner selectVehicleType;
    private CoordinatorLayout coordinatorLayout;
    AppDatabase db;
    List<CartDetails> items = null;
    ApprovalHeaderAdapter mAdapter;
    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;
    private String userId = "", selectedVehicle = "";
    int vehicle;
    private boolean isItemAdded = false;
    List<CartHeaderListDTO> cartHeaderList = null;
    List<CartHeader> cartHeader;
    HashMap<String, Integer> hashMap;
    List<String> vehiclesList;
    String customerId;
    android.support.v7.app.AlertDialog alertDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.approval_fragment, container, false);
        loadFormControls();
        return rootView;
    }

    //Loading all the form controls
    private void loadFormControls() {


        try {

            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.snack_bar_action_layout);
            frame = (FrameLayout) rootView.findViewById(R.id.frame);

            db = new RoomAppDatabase(getActivity()).getAppDatabase();
            db.cartHeaderDAO().deleteHeadersNotThereInCartDetails(); // to clear all duplicate headers

            SharedPreferences sp = getActivity().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
            userId = sp.getString(KeyValues.USER_ID, "");

            mAdapter = new ApprovalHeaderAdapter();

            spinnerSelectCustomer = (SearchableSpinner) rootView.findViewById(R.id.spinnerSelectCustomer);

            txtstartshopping = (TextView) rootView.findViewById(R.id.txtstartshopping);
            txtstartshopping.setOnClickListener(this);

            empty_cart = (LinearLayout) rootView.findViewById(R.id.empty_cart);
            llCartProducts = (LinearLayout) rootView.findViewById(R.id.llCartProducts);

            rvCartItemsList = (RecyclerView) rootView.findViewById(R.id.rvCartItemsList);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            rvCartItemsList.setLayoutManager(linearLayoutManager);

            txtAvailableCL = (TextView) rootView.findViewById(R.id.txtAvailableCL);
            txtTotal = (TextView) rootView.findViewById(R.id.txtTotal);
            txtOrderFulfilment = (TextView) rootView.findViewById(R.id.txtOrderFulfilment);
            txtConfirmOrder = (TextView) rootView.findViewById(R.id.txtConfirmOrder);

            // bottom sheet view controllers
            selectVehicleType = (SearchableSpinner) rootView.findViewById(R.id.selectVehicleType);
            cbPartialFulfilment = (CheckBox) rootView.findViewById(R.id.cbPartialFulfilment);
            cbSingleDelivery = (CheckBox) rootView.findViewById(R.id.cbSingleDelivery);
            cbVehicleTypePreference = (CheckBox) rootView.findViewById(R.id.cbVehicleTypePreference);
            hashMap = new HashMap<>();

            btnProceed = (Button) rootView.findViewById(R.id.btnProceed);

            btnProceed.setOnClickListener(this);
            txtOrderFulfilment.setOnClickListener(this);
            txtConfirmOrder.setOnClickListener(this);

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

                    if (String.valueOf(slideOffset).equalsIgnoreCase("0.0")) {
                        frame.setVisibility(View.GONE);
                    }
                }
            });

            items = new ArrayList<>();

            cbSingleDelivery.setOnCheckedChangeListener(this);
            cbPartialFulfilment.setOnCheckedChangeListener(this);
            cbVehicleTypePreference.setOnCheckedChangeListener(this);

            if (getActivity().getIntent().getExtras() != null) {

                isItemAdded = getActivity().getIntent().getExtras().getBoolean(KeyValues.IS_ITEM_ADDED_TO_CART);

                if (isItemAdded) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        //((MainActivity) CartActivity.getActivity()).startTime();
                    }
                }
            }

            common = new Common();
            errorMessages = new ErrorMessages();
            exceptionLoggerUtils = new ExceptionLoggerUtils();
            restService = new RestService();
            core = new OMSCoreMessage();


            selectVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedVehicle = selectVehicleType.getSelectedItem().toString();
                    if (!selectedVehicle.equalsIgnoreCase("Select")) {
                        vehicle = hashMap.get(selectedVehicle);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            List<CustomerTable> getCustomerNames;
            List<String> customerCodes;
            final List<String> customerIds;
            final List<String> userDivisionId;

            getCustomerNames = db.customerDAO().getCustomerNames();
            userDivisionId = db.customerDAO().getUserDivisionCustTableId();
            customerCodes = new ArrayList();
            customerIds = new ArrayList();
            customerCodes.add("Select Customer");
            customerIds.add("");
            for (int i = 0; i < userDivisionId.size(); i++) {
                customerCodes.add(db.customerDAO().getCustomerCodesByString(userDivisionId.get(i)));
                customerIds.add(db.customerDAO().getCustomerIdByString(userDivisionId.get(i)));
            }

            ArrayAdapter arrayAdapterPrinters = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, customerCodes);
            spinnerSelectCustomer.setAdapter(arrayAdapterPrinters);

            spinnerSelectCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (spinnerSelectCustomer.getSelectedItem().toString().equals("Select Customer")) {
                        customerId = "";
                        txtOrderFulfilment.setEnabled(false);
                    } else {
                        customerId = customerIds.get(i).toString();
                        txtOrderFulfilment.setEnabled(true);
                    }

                    callCart();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            empty_cart.setVisibility(View.INVISIBLE);

        }catch (Exception ex){
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void callCart() {

        setProgressDialog();
        cartHeader=new ArrayList<>();
        if(customerId.equals("")){
            if( db.cartHeaderDAO().getAllWithApprovals().size()>0){
                cartHeader = db.cartHeaderDAO().getAllWithApprovals();
                alertDialog.dismiss();
            }else{
                alertDialog.dismiss();
            }
        }else{
            if( db.cartHeaderDAO().getAllCustomerWithApprovals(customerId).size()>0) {
                cartHeader = db.cartHeaderDAO().getAllCustomerWithApprovals(customerId);
                alertDialog.dismiss();
            }else{
                alertDialog.dismiss();
            }
        }

        if (cartHeader.size() > 0) {
            if (NetworkUtils.isInternetAvailable(getActivity())) {
                loadCart();
            } else {
                loadCart();
                txtOrderFulfilment.setEnabled(false);
                txtConfirmOrder.setEnabled(false);
            }
        }else{
            changeLayout();
        }
    }

    public void changeLayout() {
        //empty_cart.setVisibility(View.VISIBLE);
        llCartProducts.setVisibility(View.GONE);
    }

    private void loadCart() {

        cartHeaderList = new ArrayList<>();

        if (db.cartHeaderDAO().getAll() != null) {

            List<CartHeader> cartHeadersList;
            if(customerId.equals("")){
                cartHeadersList = db.cartHeaderDAO().getAllWithApprovals();
            }else{
                cartHeadersList = db.cartHeaderDAO().getAllCustomerWithApprovals(customerId);
            }

            CartHeaderListDTO cartHeaderListDTO;

            for (CartHeader cartHeader : cartHeadersList) {

                cartHeaderListDTO = new CartHeaderListDTO();
                cartHeaderListDTO.setCartHeaderID(cartHeader.cartHeaderID);
                cartHeaderListDTO.setCustomerName(cartHeader.customerName);
                cartHeaderListDTO.setCreditLimit(cartHeader.creditLimit);
                cartHeaderListDTO.setCustomerID(cartHeader.customerID);
                cartHeaderListDTO.setIsApproved(cartHeader.isApproved);
                cartHeaderListDTO.setIsInActive(cartHeader.isInActive);
                cartHeaderListDTO.setIsCreditLimit(cartHeader.isCreditLimit);
                cartHeaderListDTO.setCreatedOn(cartHeader.timeStamp);

                List<CartDetailsListDTO> cartDetailsListDTOList = new ArrayList<>();

                if (db.cartDetailsDAO().getCartItemsOfCustomer(cartHeader.cartHeaderID,cartHeader.customerID) != null) {

                    List<CartDetails> cartDetailsList = db.cartDetailsDAO().getCartItemsOfCustomer(cartHeader.cartHeaderID,cartHeader.customerID);

                    CartDetailsListDTO cartDetailsListDTO = null;

                    for (CartDetails cartDetails : cartDetailsList) {

                        cartDetailsListDTO = new CartDetailsListDTO();
                        cartDetailsListDTO.setQuantity(cartDetails.quantity);
                        cartDetailsListDTO.setCustomerID(cartDetails.customerId);
                        cartDetailsListDTO.setMaterialMasterID(cartDetails.materialID);
                        cartDetailsListDTO.setMCode(cartDetails.mCode);
                        cartDetailsListDTO.setMDescription(cartDetails.mDescription);
                        cartDetailsListDTO.setFileNames(cartDetails.imgPath);
                        cartDetailsListDTO.setActualDeliveryDate(cartDetails.deliveryDate);
                        cartDetailsListDTO.setPrice(cartDetails.price);
                        cartDetailsListDTO.setCartHeaderID(cartDetails.cartHeaderId);
                        cartDetailsListDTO.setCartDetailsID(cartDetails.cartDetailsId);
                        cartDetailsListDTO.setIsInActive(cartDetails.isInActive);
                        cartDetailsListDTOList.add(cartDetailsListDTO);

                    }

                }

                cartHeaderListDTO.setListCartDetailsList(cartDetailsListDTOList);
                cartHeaderList.add(cartHeaderListDTO);
            }
        }

        if (cartHeaderList.size() > 0) {
            List<CartHeaderListDTO> dummycartHeaderList = new ArrayList<>();
            for (int i = 0; i < cartHeaderList.size(); i++) {
                if (cartHeaderList.get(i).getListCartDetailsList().size() > 0)
                    dummycartHeaderList.add(cartHeaderList.get(i));
            }
            cartHeaderList = dummycartHeaderList;
            
            if (cartHeaderList.size() > 0) {
                mAdapter = new ApprovalHeaderAdapter(cartHeaderList, getActivity(), getActivity());
                empty_cart.setVisibility(View.GONE);
                llCartProducts.setVisibility(View.VISIBLE);
                rvCartItemsList.setAdapter(mAdapter);
            } else {
                empty_cart.setVisibility(View.VISIBLE);
                llCartProducts.setVisibility(View.GONE);
                // (MainActivity).stopTimer();
            }
        } else {
            empty_cart.setVisibility(View.VISIBLE);
            llCartProducts.setVisibility(View.GONE);
            // (MainActivity).stopTimer();
        }

        alertDialog.dismiss();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtstartshopping:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(KeyValues.OPEN_CATALOG, true);
                startActivity(intent);
                break;

            case R.id.txtOrderFulfilment:
                vehiclesList = new ArrayList<>();
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                frame.setVisibility(View.VISIBLE);
                break;

            case R.id.btnProceed:
                break;

            case R.id.txtConfirmOrder:
                break;

        }
    }

    public void VehicleList() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());

        FullfilmentDTO oDto = new FullfilmentDTO();
        oDto.setPrefix(null);

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.VehicleList(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }


                        } else {



                            JSONArray getCartHeader = null;
                            try {
                                getCartHeader = new JSONArray((String) core.getEntityObject());

                                for (int i = 0; i < getCartHeader.length(); i++) {

                                    JSONObject obj = (JSONObject) getCartHeader.get(i);
                                    hashMap.put(obj.getString("Label"), obj.getInt("Value"));

                                }

                                vehiclesList = new ArrayList<>();

                                vehiclesList.add("Select");
                                for (String list : hashMap.keySet()) {
                                    vehiclesList.add(list);
                                }

                                ArrayAdapter arrayAdapterPrinters = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, vehiclesList);
                                selectVehicleType.setAdapter(arrayAdapterPrinters);

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if(NetworkUtils.isInternetAvailable(getActivity())){
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    }else{
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (cbSingleDelivery.isChecked()) {
            cbPartialFulfilment.setChecked(false);
        }

        if (cbVehicleTypePreference.isChecked()) {
            selectVehicleType.setVisibility(View.VISIBLE);
            VehicleList();
        } else {
            selectVehicleType.setVisibility(View.GONE);
        }

        if (cbPartialFulfilment.isChecked()) {
            cbSingleDelivery.setChecked(false);
        }
    }

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getActivity());
        tvText.setText("Please wait");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setView(ll);

        alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 25);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
