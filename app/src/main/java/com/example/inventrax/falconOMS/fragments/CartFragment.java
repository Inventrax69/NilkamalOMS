package com.example.inventrax.falconOMS.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.CartActivity;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.activities.OrderConfirmationActivity;
import com.example.inventrax.falconOMS.adapters.CartHeaderAdapter;
import com.example.inventrax.falconOMS.adapters.CartMaterialHeaderAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.ApplyOffersDTO;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListResponseDTO;
import com.example.inventrax.falconOMS.pojos.CartListDTO;
import com.example.inventrax.falconOMS.pojos.CartProcessDTO;
import com.example.inventrax.falconOMS.pojos.CustomerPartnerDTO;
import com.example.inventrax.falconOMS.pojos.FullfilmentDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.ProductDiscountDTO;
import com.example.inventrax.falconOMS.pojos.VariantDTO;
import com.example.inventrax.falconOMS.pojos.productCatalogs;
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
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Padmaja on 04/07/2019.
 */

public class CartFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String classCode = "OMS_Android_Activity_Cart";
    private View rootView;
    Toolbar toolbar;
    private FrameLayout frame;
    private TextView txtstartshopping;
    private LinearLayout empty_cart, llCartProducts;
    private RecyclerView rvCartItemsList;
    private SearchableSpinner spinnerSelectCustomer;
    private ImageView ivStartShop;
    LinearLayoutManager linearLayoutManager;
    TextView txtAvailableCL, txtTotal, txtOrderFulfilment, txtConfirmOrder, txtApplyOffers;
    BottomSheetBehavior behavior;
    private CheckBox cbPartialFulfilment, cbSingleDelivery, cbVehicleTypePreference;
    private Button btnProceed;
    private SearchableSpinner selectVehicleType;
    private CoordinatorLayout coordinatorLayout;
    AppDatabase db;
    List<CartDetails> items = null;
    CartHeaderAdapter mAdapter;
    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;
    private String userId = "", selectedVehicle = "", userRoleName = "";
    int vehicle = 0;
    private boolean isItemAdded = false;
    List<CartHeaderListDTO> cartHeaderList = null;
    List<CartHeader> cartHeader;
    HashMap<String, Integer> hashMap;
    List<String> vehiclesList;
    String customerId = "";
    SwipeRefreshLayout mySwipeRefreshLayout;
    android.support.v7.app.AlertDialog alertDialog;
    List<productCatalogs> cartList = null;
    LinearLayout selCustomerlinear;
    private TextView txtTotalAmt, txtTotalAmtTax, txtTaxes;
    Dialog dialog;
    int cartHeaderId, custumerId;
    Dialog approvalDailog;
    List<Integer> headersList;

    private boolean isOfferApplied=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cart_fragment, container, false);
        try {
            loadFormControls();
        } catch (Exception e) {

        }
        return rootView;
    }

    //Loading all the form controls
    private void loadFormControls() {

        try {

            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
            frame = (FrameLayout) rootView.findViewById(R.id.frame);

            db = new RoomAppDatabase(getActivity()).getAppDatabase();
            db.cartHeaderDAO().deleteHeadersNotThereInCartDetails(); // to clear all duplicate headers

            SharedPreferences sp = getActivity().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
            userId = sp.getString(KeyValues.USER_ID, "");
            userRoleName = sp.getString(KeyValues.USER_ROLE_NAME, "");

            mAdapter = new CartHeaderAdapter();

            spinnerSelectCustomer = (SearchableSpinner) rootView.findViewById(R.id.spinnerSelectCustomer);

            txtstartshopping = (TextView) rootView.findViewById(R.id.txtstartshopping);
            ivStartShop = (ImageView) rootView.findViewById(R.id.ivStartShop);
            txtstartshopping.setOnClickListener(this);
            ivStartShop.setOnClickListener(this);

            empty_cart = (LinearLayout) rootView.findViewById(R.id.empty_cart);
            llCartProducts = (LinearLayout) rootView.findViewById(R.id.llCartProducts);

            rvCartItemsList = (RecyclerView) rootView.findViewById(R.id.rvCartItemsList);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            rvCartItemsList.setLayoutManager(linearLayoutManager);

            txtAvailableCL = (TextView) rootView.findViewById(R.id.txtAvailableCL);
            txtTotal = (TextView) rootView.findViewById(R.id.txtTotal);
            txtOrderFulfilment = (TextView) rootView.findViewById(R.id.txtOrderFulfilment);
            txtConfirmOrder = (TextView) rootView.findViewById(R.id.txtConfirmOrder);
            txtApplyOffers = (TextView) rootView.findViewById(R.id.txtApplyOffers);

            txtTotalAmt = (TextView) rootView.findViewById(R.id.txtTotalAmt);
            txtTaxes = (TextView) rootView.findViewById(R.id.txtTaxes);
            txtTotalAmtTax = (TextView) rootView.findViewById(R.id.txtTotalAmtTax);

            txtConfirmOrder = (TextView) rootView.findViewById(R.id.txtConfirmOrder);
            selCustomerlinear = (LinearLayout) rootView.findViewById(R.id.selCustomerlinear);

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
            txtApplyOffers.setOnClickListener(this);


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
            customerCodes = new ArrayList<String>();
            customerIds = new ArrayList<String>();
            headersList = new ArrayList<>();

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
                        txtOrderFulfilment.setEnabled(true);
                        txtOrderFulfilment.setVisibility(View.VISIBLE);
                        txtOrderFulfilment.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.light_grey)));
                    } else {
                        customerId = customerIds.get(i).toString();
                        txtOrderFulfilment.setEnabled(true);
                        txtOrderFulfilment.setVisibility(View.VISIBLE);
                        txtOrderFulfilment.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorAccent)));
                    }
                    callCart();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            empty_cart.setVisibility(View.INVISIBLE);

            if (userRoleName.equals("DTD")) {
                selCustomerlinear.setVisibility(View.GONE);
                txtOrderFulfilment.setEnabled(true);
                txtOrderFulfilment.setVisibility(View.VISIBLE);
                txtOrderFulfilment.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorAccent)));
                callCart();
            }

        } catch (Exception ex) {

        }

    }

    public void callCart() {

        setProgressDialog();
        cartHeader = new ArrayList<>();
        if (customerId.equals("")) {
            if (db.cartHeaderDAO().getAllWithOutApprovals().size() > 0) {
                cartHeader = db.cartHeaderDAO().getAllWithOutApprovals();
                alertDialog.dismiss();
            } else {
                alertDialog.dismiss();
            }
        } else {
            if (db.cartHeaderDAO().getAllCustomerWithOutApprovals(customerId).size() > 0) {
                cartHeader = db.cartHeaderDAO().getAllCustomerWithOutApprovals(customerId);
                alertDialog.dismiss();
            } else {
                alertDialog.dismiss();
            }
        }

        if (cartHeader.size() > 0) {
            if (cartHeader.size() == 1) {
                txtOrderFulfilment.setEnabled(true);
                txtOrderFulfilment.setVisibility(View.VISIBLE);
                txtOrderFulfilment.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorAccent)));
            }

            loadCart();

            /*
            if (NetworkUtils.isInternetAvailable(getActivity())) {

            } else {
                loadCart();
                txtOrderFulfilment.setEnabled(false);
                txtConfirmOrder.setEnabled(false);
            }
            */

        } else {
            changeLayout();
        }

    }

    public void changeLayout() {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
        sharedPreferencesUtils.savePreference(KeyValues.TIMER, 0L);
        long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
        ((CartActivity) getActivity()).findViewById(R.id.txtTimer).setVisibility(View.GONE);
        ((CartActivity) getActivity()).stopTimer();
        empty_cart.setVisibility(View.VISIBLE);
        llCartProducts.setVisibility(View.GONE);
    }

    private void loadCart() {

        cartHeaderList = new ArrayList<>();

        if (db.cartHeaderDAO().getAll() != null) {

            List<CartHeader> cartHeadersList;

            if (customerId.equals("")) {
                cartHeadersList = db.cartHeaderDAO().getAllWithOutApprovals();
            } else {
                cartHeadersList = db.cartHeaderDAO().getAllCustomerWithOutApprovals(customerId);
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
                cartHeaderListDTO.setShipToPartyID(String.valueOf(cartHeader.shipToPartyId));
                cartHeaderListDTO.setIsPriority(String.valueOf(cartHeader.isPriority));

                List<CartDetailsListDTO> cartDetailsListDTOList = new ArrayList<>();

                if (db.cartDetailsDAO().getCartItemsOfCustomer(cartHeader.cartHeaderID, cartHeader.customerID) != null) {

                    List<CartDetails> cartDetailsList = db.cartDetailsDAO().getCartItemsOfCustomer(cartHeader.cartHeaderID, cartHeader.customerID);

                    CartDetailsListDTO cartDetailsListDTO = null;

                    for (CartDetails cartDetails : cartDetailsList) {

                        cartDetailsListDTO = new CartDetailsListDTO();
                        cartDetailsListDTO.setQuantity(cartDetails.quantity);
                        cartDetailsListDTO.setCustomerID(cartDetails.customerId);
                        cartDetailsListDTO.setMaterialMasterID(cartDetails.materialID);
                        cartDetailsListDTO.setMCode(cartDetails.mCode);
                        cartDetailsListDTO.setMDescription(cartDetails.mDescription);
                        if (cartDetails.imgPath != null) {
                            if (cartDetails.imgPath.split("[|]").length <= 0) {
                                cartDetailsListDTO.setFileNames(cartDetails.imgPath);
                            } else {
                                cartDetailsListDTO.setFileNames(cartDetails.imgPath.split("[|]")[0]);
                            }
                        } else {
                            cartDetailsListDTO.setFileNames("");
                        }
                        cartDetailsListDTO.setPrice(cartDetails.price);
                        cartDetailsListDTO.setCartHeaderID(cartDetails.cartHeaderId);
                        cartDetailsListDTO.setCartDetailsID(cartDetails.cartDetailsId);
                        cartDetailsListDTO.setIsInActive(cartDetails.isInActive);
                        cartDetailsListDTO.setMaterialPriorityID(cartDetails.isPriority);
                        cartDetailsListDTO.setOfferValue(cartDetails.offerValue);
                        cartDetailsListDTO.setTotalPrice(cartDetails.totalPrice);
                        cartDetailsListDTO.setOfferItemCartDetailsID(cartDetails.offerItemCartDetailsID);
                        cartDetailsListDTO.setDiscountID(cartDetails.discountID);
                        cartDetailsListDTO.setDiscountText(cartDetails.discountText);
                        cartDetailsListDTO.setGST(cartDetails.gst);
                        cartDetailsListDTO.setTax(cartDetails.tax);
                        cartDetailsListDTO.setSubTotal(cartDetails.subtotal);
                        cartDetailsListDTO.setHSNCode(cartDetails.HSNCode);
                        cartDetailsListDTO.setBOMHeaderID(cartDetails.bomHeaderId);
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
                mAdapter = new CartHeaderAdapter(cartHeaderList, getActivity(), getActivity(), CartFragment.this);
                empty_cart.setVisibility(View.GONE);
                llCartProducts.setVisibility(View.VISIBLE);
                rvCartItemsList.setAdapter(mAdapter);
            } else {
                empty_cart.setVisibility(View.VISIBLE);
                llCartProducts.setVisibility(View.GONE);
            }

        } else {
            empty_cart.setVisibility(View.VISIBLE);
            llCartProducts.setVisibility(View.GONE);
        }

        alertDialog.dismiss();
    }

    AlertDialog alert;

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtstartshopping:
                goToProductCatalog();
                break;

            case R.id.ivStartShop:
                goToProductCatalog();
                break;

            case R.id.txtOrderFulfilment:

                if (NetworkUtils.isInternetAvailable(getActivity())) {

                        if (cartHeaderList.size() > 1 && customerId.isEmpty() && !userRoleName.equals("DTD")) {
                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0023, ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                            return;
                        }

                        if (db.cartHeaderDetailsDao().getUpdateCount()) {

                            if(isOfferApplied){


                            vehiclesList = new ArrayList<>();
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            frame.setVisibility(View.VISIBLE);

                            if (userRoleName.equals("DTD")) {

                                if (db.cartHeaderDAO().getTaxesList().size() == 1) {
                                    txtTotalAmt.setText(String.format("%.2f", Double.parseDouble(db.cartHeaderDAO().getTaxesList().get(0).getTotalPrice())));
                                    txtTaxes.setText(String.format("%.2f", Double.parseDouble(db.cartHeaderDAO().getTaxesList().get(0).getTaxes())));
                                    txtTotalAmtTax.setText(String.format("%.2f", Double.parseDouble(db.cartHeaderDAO().getTaxesList().get(0).getTotalPriceWithTax())));
                                }

                            } else {

                                String customerId1 = customerId;

                                if (customerId.isEmpty()) {
                                    customerId1 = String.valueOf(cartHeaderList.get(0).getCustomerID());
                                }

                                if (db.cartHeaderDAO().getTaxesListByCustomer(customerId1).size() == 1) {
                                    txtTotalAmt.setText(String.format("%.2f", Double.parseDouble(db.cartHeaderDAO().getTaxesListByCustomer(customerId1).get(0).getTotalPrice())));
                                    txtTaxes.setText(String.format("%.2f", Double.parseDouble(db.cartHeaderDAO().getTaxesListByCustomer(customerId1).get(0).getTaxes())));
                                    txtTotalAmtTax.setText(String.format("%.2f", Double.parseDouble(db.cartHeaderDAO().getTaxesListByCustomer(customerId1).get(0).getTotalPriceWithTax())));
                                }

                            }
                        }else {
                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0026, ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                        }

                        } else {

                            cartList = new ArrayList<>();

                            if (db.cartDetailsDAO().getCartItemsWithOutApprovals() != null) {

                                List<CartDetails> cartDetailsList = new ArrayList<>();
                                cartDetailsList = db.cartDetailsDAO().getCartItemsWithOutApprovals();
                                productCatalogs cDto;
                                for (int i = 0; i < cartDetailsList.size(); i++) {
                                    cDto = new productCatalogs();
                                    cDto.setMaterialMasterID(cartDetailsList.get(i).materialID);
                                    cDto.setMCode(cartDetailsList.get(i).mCode);
                                    cDto.setQuantity(cartDetailsList.get(i).quantity);
                                    cDto.setCustomerID(String.valueOf(cartDetailsList.get(i).customerId));
                                    cDto.setImagePath(cartDetailsList.get(i).imgPath);
                                    cDto.setPrice(cartDetailsList.get(i).price);
                                    cDto.setShipToPartyCustomerID(String.valueOf(cartDetailsList.get(i).customerId));
                                    cDto.setCartDetailsID("0");
                                    cDto.setMaterialPriorityID(String.valueOf(cartDetailsList.get(i).isPriority));
                                    cDto.setDeliveryDate(cartDetailsList.get(i).deliveryDate);
                                    cDto.setCartHeaderID(Integer.parseInt(cartDetailsList.get(i).cartHeaderId));
                                    cartList.add(cDto);
                                }

                                addToCart();

                            }
                            // Toast.makeText(getActivity(), "Cart has changed please sync add to cart", Toast.LENGTH_SHORT).show();
                        }

                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0007, ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                }

                break;

            case R.id.txtApplyOffers:

                if (NetworkUtils.isInternetAvailable(getActivity())) {

                    //Toast.makeText(getActivity(), "offer", Toast.LENGTH_SHORT).show();
                    if (!customerId.equals("")) {
                        CartHeader cartHeader = db.cartHeaderDAO().getCartHeaderByCustomerID(Integer.parseInt(customerId));
                        applyOffer(cartHeader.cartHeaderID);
                    } else {

                        if (userRoleName.equals("DTD")) {
                            applyOffer(0);
                        }else {
                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0023, ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                        }

                    }

                } else {

                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0007, ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                }


                break;


            case R.id.btnProceed:

                final AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(getActivity());

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure to proceed")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                frame.setVisibility(View.GONE);

                                List<CartHeader> fullfilHeadersids = db.cartHeaderDAO().getHeadersWithCustmor();
                                FullfilmentDTO oDto;
                                List<FullfilmentDTO> fullfilmentDTOS = new ArrayList<>();
                                if (customerId.equals("") || customerId.isEmpty()) {
                                    for (int i = 0; i < fullfilHeadersids.size(); i++) {
                                        oDto = new FullfilmentDTO();
                                        oDto.setCartHeaderID(String.valueOf(fullfilHeadersids.get(i).cartHeaderID));

                                        int fullfilmentType;
                                        if (cbPartialFulfilment.isChecked()) {
                                            fullfilmentType = 2;
                                        } else if (cbSingleDelivery.isChecked()) {
                                            fullfilmentType = 1;
                                        } else {
                                            fullfilmentType = 2;
                                        }
                                        oDto.setFulfilmentPreferenceID(fullfilmentType);
                                        oDto.setOrderTypeID(1);
                                        oDto.setOrderClassificationID(1);
                                        /*CartHeader cartHeader = db.cartHeaderDAO().getByCustomerIDCartHeaderID(fullfilHeadersids.get(i).customerID,fullfilHeadersids.get(i).cartHeaderID);
                                        int shipToPartyId = cartHeader.shipToPartyId;
                                        if(shipToPartyId==0)
                                            oDto.setShipToPartyCustomerID(String.valueOf(cartHeader.customerID));
                                        else
                                            oDto.setShipToPartyCustomerID(String.valueOf(shipToPartyId));
                                        int isPriority = cartHeader.isPriority;
                                        oDto.setCartPriorityID(String.valueOf("0"));*/
                                        oDto.setMaterialList(null);
                                        fullfilmentDTOS.add(oDto);
                                    }

                                    fulfilment(fullfilmentDTOS);

                                } else {

                                    oDto = new FullfilmentDTO();
                                    Integer fullfilHeadersid = db.cartHeaderDAO().getHeadersWithCustmor(customerId);
                                    oDto.setCartHeaderID(String.valueOf(fullfilHeadersid));

                                    int fullfilmentType;
                                    if (cbPartialFulfilment.isChecked()) {
                                        fullfilmentType = 2;
                                    } else if (cbSingleDelivery.isChecked()) {
                                        fullfilmentType = 1;
                                    } else {
                                        fullfilmentType = 2;
                                    }
                                    oDto.setFulfilmentPreferenceID(fullfilmentType);
                                    oDto.setOrderTypeID(1);
                                    oDto.setOrderClassificationID(1);
                                    /* CartHeader cartHeader=db.cartHeaderDAO().getByCustomerIDCartHeaderID(Integer.parseInt(customerId),fullfilHeadersid);
                                    int shipToPartyId= cartHeader.shipToPartyId;
                                    if(shipToPartyId==0)
                                        oDto.setShipToPartyCustomerID(customerId);
                                    else
                                        oDto.setShipToPartyCustomerID(String.valueOf(shipToPartyId));
                                    int isPriority = cartHeader.isPriority;
                                    oDto.setCartPriorityID(String.valueOf("0")); */
                                    oDto.setMaterialList(null);
                                    fullfilmentDTOS.add(oDto);
                                    fulfilment(fullfilmentDTOS);

                                }

                            }
                        })
                        .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                            }
                        });

                //Creating dialog box
                alert = builder.create();

                //Setting the title manually
                alert.setTitle(getString(R.string.confirm));
                alert.show();

                break;


        }
    }

    public void applyOffer(final int cartHeaderID) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());

        ApplyOffersDTO applyOffersDTO = new ApplyOffersDTO();
        applyOffersDTO.setCartHeaderID(String.valueOf(cartHeaderID));
        if (!userRoleName.equals("DTD")) {
            if (!customerId.equals("")) {

                applyOffersDTO.setCustomerID(customerId);

            } else {
                Toast.makeText(getActivity(), "Please select customer", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            applyOffersDTO.setCustomerID("0");
        }
        applyOffersDTO.setHandheldRequest(true);
        applyOffersDTO.setSearchString(null);
        applyOffersDTO.setPageIndex(1);
        applyOffersDTO.setPageSize(5);
        applyOffersDTO.setUserID(userId);

        message.setEntityObject(applyOffersDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.ActiveCartListWithOffers(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }

                            isOfferApplied = false;
                            ProgressDialogUtils.closeProgressDialog();


                        } else {

                            db.cartDetailsDAO().deleteAll();
                            db.cartHeaderDAO().deleteAll();

                            try {

                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                CartHeaderListDTO cartHeaderListDTO;
                                CartDetailsListDTO cart;

                                for (int i = 0; i < getCartHeader.length(); i++) {

                                    for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                        cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);
                                        if (cartHeaderListDTO.getListCartDetailsList().size() > 0) {
                                            db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                    cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                    cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));
                                        }

                                        for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {
                                            cart = cartHeaderListDTO.getListCartDetailsList().get(k);
                                            db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                    cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                    cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                    cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                    cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                    cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode(),cart.getDiscountedPrice(),cart.getBOMHeaderID()));
                                        }
                                    }
                                }

                                isOfferApplied = true;

                                loadFormControls();

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception e) {
                                ProgressDialogUtils.closeProgressDialog();

                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
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


    public void goToProductCatalog() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(KeyValues.OPEN_CATALOG, true);
        startActivity(intent);
    }

    public void fulfilment(final List<FullfilmentDTO> fullfilmentDTOS) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.OrderFulfilment_DTO, getActivity());
        CartListDTO cartListDTO = new CartListDTO();
        cartListDTO.setCartList(fullfilmentDTOS);
        message.setEntityObject(cartListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

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

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }

                        } else {

                            try {

                                JSONArray getApprovalListDTO = new JSONArray((String) core.getEntityObject());
                                CustomerPartnerDTO customerPartnerDTO = new CustomerPartnerDTO();
                                final List<CustomerPartnerDTO> customerPartnerDTOS = new ArrayList<>();
                                for (int i = 0; i < getApprovalListDTO.length(); i++) {
                                    customerPartnerDTO = new Gson().fromJson(getApprovalListDTO.getJSONObject(i).toString(), CustomerPartnerDTO.class);
                                    customerPartnerDTOS.add(customerPartnerDTO);
                                }

                                final List<CustomerPartnerDTO> isRFMcustomerPartnerDTOS = new ArrayList<>();
                                for (int i = 0; i < customerPartnerDTOS.size(); i++) {
                                    if (customerPartnerDTOS.get(i).getrFM()) {
                                        isRFMcustomerPartnerDTOS.add(customerPartnerDTOS.get(i));
                                    }
                                }

                                if (isRFMcustomerPartnerDTOS.size() > 0) {

                                    dialog = new Dialog(getActivity());
                                    dialog.setContentView(R.layout.cart_material_popup);
                                    dialog.setCancelable(false);
                                    Window window = dialog.getWindow();
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
                                    //recyclerView.setText(new Gson().toJson(isRFMcustomerPartnerDTOS));
                                    linearLayoutManager = new LinearLayoutManager(getActivity());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    CartMaterialHeaderAdapter cartMaterialHeaderAdapter = new CartMaterialHeaderAdapter(isRFMcustomerPartnerDTOS, getActivity(), getActivity(), CartFragment.this, new CartMaterialHeaderAdapter.OnItemClickListener() {

                                        @Override
                                        public void onItemClick(int pos) {

                                        }

                                        @Override
                                        public void onChangeIsProcessID(int pos, String IsProcessID) {
                                            isRFMcustomerPartnerDTOS.get(pos).setIsProcessID(IsProcessID);
                                        }
                                    });
                                    recyclerView.setAdapter(cartMaterialHeaderAdapter);

                                    Button btnPAD = dialog.findViewById(R.id.btnPAD);
                                    Button btnPOD = dialog.findViewById(R.id.btnPOD);
                                    ImageView btnClOSE = dialog.findViewById(R.id.btnClOSE);

                                    dialog.show();

                                    btnClOSE.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });

                                    btnPAD.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if (isRFMcustomerPartnerDTOS.size() > 0) {
                                                int[] headerIds = new int[isRFMcustomerPartnerDTOS.size()];
                                                for (int i = 0; i < isRFMcustomerPartnerDTOS.size(); i++) {
                                                    headerIds[i] = isRFMcustomerPartnerDTOS.get(i).getCartHeaderID();
                                                }
                                                ProceedWithAvbQty(headerIds, fullfilmentDTOS);
                                            }

                                        }
                                    });

                                    btnPOD.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if (isRFMcustomerPartnerDTOS.size() > 0) {

                                                boolean check = false;
                                                int[] headerIds = new int[isRFMcustomerPartnerDTOS.size()];
                                                List<CartProcessDTO> cartProcessDTOS = new ArrayList<>();

                                                for (int i = 0; i < fullfilmentDTOS.size(); i++) {

                                                    CartProcessDTO cartProcessDTO = new CartProcessDTO();
                                                    cartProcessDTO.setCartHeaderID(Integer.parseInt(fullfilmentDTOS.get(i).getCartHeaderID()));
                                                    cartProcessDTO.setISProceessID(Integer.parseInt("0"));
                                                    cartProcessDTOS.add(cartProcessDTO);

                                                    for (int j = 0; j < isRFMcustomerPartnerDTOS.size(); j++) {
                                                        if (isRFMcustomerPartnerDTOS.get(j).getCartHeaderID() == Integer.parseInt(fullfilmentDTOS.get(i).getCartHeaderID())) {
                                                            headerIds[j] = isRFMcustomerPartnerDTOS.get(j).getCartHeaderID();
                                                            CartProcessDTO cartProcessDTO1 = new CartProcessDTO();
                                                            cartProcessDTO1.setCartHeaderID(isRFMcustomerPartnerDTOS.get(j).getCartHeaderID());
                                                            if (Integer.parseInt(isRFMcustomerPartnerDTOS.get(j).getIsProcessID()) == 0) {
                                                                check = true;
                                                            }
                                                            cartProcessDTO1.setISProceessID(Integer.parseInt(isRFMcustomerPartnerDTOS.get(j).getIsProcessID()));
                                                            cartProcessDTOS.set(i, cartProcessDTO1);
                                                        }
                                                    }

                                                }

                                                if (check) {
                                                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0025, ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                                } else {
                                                    ProcessCart(headerIds, fullfilmentDTOS, cartProcessDTOS);
                                                }

                                                // ProceedWithOrderQty(headerIds, fullfilmentDTOS);

                                            }

                                        }
                                    });
                                }


                                if (isRFMcustomerPartnerDTOS.size() == 0) {

                                    List<CartHeader> cartHeadersList = db.cartHeaderDAO().getCartHeadersForSTP();

                                    JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                    db.cartDetailsDAO().deleteAll();
                                    db.cartHeaderDAO().deleteAll();

                                    int value = 0;


                                    for (int i = 0; i < getCartHeader.length(); i++) {

                                        TypeToken<CartHeaderListDTO> header = new TypeToken<CartHeaderListDTO>() {
                                        };

                                        String CustomerID = getCartHeader.getJSONObject(i).getString("CustomerID");
                                        String CustomerName = getCartHeader.getJSONObject(i).getString("CustomerName");
                                        String CustomerCode = getCartHeader.getJSONObject(i).getString("CustomerCode");
                                        Double CreditLimit = getCartHeader.getJSONObject(i).getDouble("CreditLimit");


                                        for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                            CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                            db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), CustomerName, CreditLimit, cartHeaderListDTO.getCartHeaderID(),
                                                    cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                    cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));

                                            if (cartHeaderListDTO.getIsInActive() == 1) {
                                                if (getActivity() != null) {
                                                    synchronized (getActivity()) {
                                                        Toast.makeText(getActivity(), CustomerName + " sent for Inactive approval", Toast.LENGTH_LONG).show();
                                                        sendForApproval(cartHeaderListDTO.getCartHeaderID(), "6");
                                                    }
                                                }
                                            } else if (cartHeaderListDTO.getIsStockNotAvailable().equals("1")) {
                                                if (getActivity() != null) {
                                                    synchronized (getActivity()) {
                                                        Toast.makeText(getActivity(), CustomerName + " sent for SCM approval", Toast.LENGTH_LONG).show();
                                                        sendForApproval(cartHeaderListDTO.getCartHeaderID(), "23");
                                                    }
                                                }
                                            } else {
                                                value += 1;
                                                if (customerId.equals("") || customerId.isEmpty())
                                                    db.cartHeaderDAO().updateisFulfillmentCompleted(cartHeaderListDTO.getCartHeaderID(), String.valueOf(cartHeaderListDTO.getCustomerID()));
                                                else if (fullfilmentDTOS.size() > 0)
                                                    db.cartHeaderDAO().updateisFulfillmentCompleted(Integer.parseInt(fullfilmentDTOS.get(0).getCartHeaderID()), String.valueOf(customerId));
                                            }


                                            for (int p = 0; p < cartHeaderListDTO.getDeliveryDate().size(); p++) {

                                                for (int k = 0; k < cartHeaderListDTO.getDeliveryDate().get(p).getListCartDetailsList().size(); k++) {

                                                    CartDetailsListDTO cart = cartHeaderListDTO.getDeliveryDate().get(p).getListCartDetailsList().get(k);

                                                    db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                            cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                            cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                            cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                            cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                            cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode(),cart.getDiscountedPrice(),cart.getBOMHeaderID()));
                                                }
                                            }
                                        }
                                    }

                                    for (int i = 0; i < cartHeadersList.size(); i++) {
                                        db.cartHeaderDAO().updateShipToPatryAndIsPriority(cartHeadersList.get(i).customerID, cartHeadersList.get(i).shipToPartyId, cartHeadersList.get(i).isPriority);
                                    }

                                    if (value > 0) {

                                        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                        Calendar calendar = Calendar.getInstance();
                                        long mills = calendar.getTimeInMillis();
                                        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                        sharedPreferencesUtils.savePreference(KeyValues.TIMER, mills);
                                        long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);

                                        /*
                                        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                        sharedPreferencesUtils.savePreference(KeyValues.TIMER, 0L);
                                        long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
                                        ((CartActivity) getActivity()).findViewById(R.id.txtTimer).setVisibility(View.GONE);
                                        ((CartActivity) getActivity()).stopTimer();
                                        */


                                        Intent i = new Intent(getActivity(), OrderConfirmationActivity.class);
                                        startActivity(i);
                                        getActivity().finish();

                                    } else {
                                        ((CartActivity) getActivity()).findViewById(R.id.txtOrders).performClick();
                                    }

                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception e) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
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

    public void sendForApproval(final int cartHeaderID, String type) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, getActivity());
        message.setEntityObject(cartHeaderID + "|" + type);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.InitiateWorkflow(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }

                            ProgressDialogUtils.closeProgressDialog();

                            if (approvalDailog != null && approvalDailog.isShowing())
                                approvalDailog.dismiss();

                           /* getFragmentManager()
                                    .beginTransaction()
                                    .detach(CartFragment.this)
                                    .attach(CartFragment.this)
                                    .commit();*/
                            loadFormControls();

                        } else {
                            ProgressDialogUtils.closeProgressDialog();
                            try {

                                //  String respObject = core.getEntityObject().toString();

                                db.cartHeaderDAO().deleteCartHeader(String.valueOf(cartHeaderID));
                                db.cartDetailsDAO().deleteCartDetailsOfCartDetails(cartHeaderID);

                                if (approvalDailog != null && approvalDailog.isShowing())
                                    approvalDailog.dismiss();

                               /* getFragmentManager()
                                        .beginTransaction()
                                        .detach(CartFragment.this)
                                        .attach(CartFragment.this)
                                        .commit();*/

                                loadFormControls();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
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

    private void showInActiveDialog(String customerName) {

        approvalDailog = new Dialog(getActivity());
        approvalDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        approvalDailog.setCancelable(true);
        approvalDailog.setContentView(R.layout.approval_dialog);

        TextView content = (TextView) approvalDailog.findViewById(R.id.dialog_content);
        Button confirm_btn = (Button) approvalDailog.findViewById(R.id.confirm_btn);
        content.setText(customerName + " sent for in-active approvals");
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approvalDailog.dismiss();
            }
        });
        confirm_btn.setVisibility(View.GONE);
        approvalDailog.show();

    }

    private void showCreditLimitDialog(String customerName) {

        approvalDailog = new Dialog(getActivity());
        approvalDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        approvalDailog.setCancelable(true);
        approvalDailog.setContentView(R.layout.approval_dialog);
        TextView content = (TextView) approvalDailog.findViewById(R.id.dialog_content);
        Button confirm_btn = (Button) approvalDailog.findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approvalDailog.dismiss();
            }
        });
        content.setText(customerName + " sent for credit limit approval");
        approvalDailog.show();

    }

    public void ProcessCart(final int[] cartHeaderId, final List<FullfilmentDTO> fullfilmentDTOS, List<CartProcessDTO> cartProcessDTOS) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.OrderFulfilment_DTO, getActivity());
        CartHeaderListResponseDTO cartHeaderListResponseDTO = new CartHeaderListResponseDTO();
        cartHeaderListResponseDTO.setCartProcessDTOS(cartProcessDTOS);
        message.setEntityObject(cartHeaderListResponseDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.ProcessCart(message);

        ProgressDialogUtils.showProgressDialog("Please Wait");

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                String respObject = core.getEntityObject().toString();

                                if (core.getEntityObject().toString().equals("1")) {
                                    ProgressDialogUtils.closeProgressDialog();
                                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "sent for SCM approval", ContextCompat.getColor(getActivity(), R.color.safron), Snackbar.LENGTH_SHORT);
                                    dialog.dismiss();
                                    db.cartHeaderDAO().deleteCartHeaderbyList(cartHeaderId);
                                    ((CartActivity) getActivity()).findViewById(R.id.txtApprovals).performClick();
                                    return;
                                }

                                if (core.getEntityObject().toString().equals("2")) {
                                    ProgressDialogUtils.closeProgressDialog();
                                    dialog.dismiss();
                                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Can't proceed as available quantity is zero", ContextCompat.getColor(getActivity(), R.color.safron), Snackbar.LENGTH_SHORT);
                                    db.cartHeaderDAO().deleteCartHeaderbyList(cartHeaderId);
                                    ((CartActivity) getActivity()).findViewById(R.id.txtOrders).performClick();
                                    return;
                                }

                                List<CartHeader> cartHeadersList = db.cartHeaderDAO().getCartHeadersForSTP();

                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                db.cartDetailsDAO().deleteAll();
                                db.cartHeaderDAO().deleteAll();

                                int value = 0;

                                for (int i = 0; i < getCartHeader.length(); i++) {

                                    TypeToken<CartHeaderListDTO> header = new TypeToken<CartHeaderListDTO>() {
                                    };

                                    String CustomerID = getCartHeader.getJSONObject(i).getString("CustomerID");
                                    String CustomerName = getCartHeader.getJSONObject(i).getString("CustomerName");
                                    String CustomerCode = getCartHeader.getJSONObject(i).getString("CustomerCode");
                                    Double CreditLimit = getCartHeader.getJSONObject(i).getDouble("CreditLimit");

                                    for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                        CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                        db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), CustomerName, CreditLimit, cartHeaderListDTO.getCartHeaderID(),
                                                cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));

                                        if (cartHeaderListDTO.getIsInActive() == 1) {
                                            if (getActivity() != null) {
                                                synchronized (getActivity()) {
                                                    Toast.makeText(getActivity(), CustomerName + " sent for Inactive approval", Toast.LENGTH_LONG).show();
                                                    sendForApproval(cartHeaderListDTO.getCartHeaderID(), "6");
                                                }
                                            }
                                        } else if (cartHeaderListDTO.getIsStockNotAvailable().equals("1")) {
                                            if (getActivity() != null) {
                                                synchronized (getActivity()) {
                                                    Toast.makeText(getActivity(), CustomerName + " sent for SCM approval", Toast.LENGTH_LONG).show();
                                                    sendForApproval(cartHeaderListDTO.getCartHeaderID(), "23");
                                                }
                                            }
                                        } else {
                                            value += 1;
                                            if (customerId.equals("") || customerId.isEmpty())
                                                db.cartHeaderDAO().updateisFulfillmentCompleted(cartHeaderListDTO.getCartHeaderID(), String.valueOf(cartHeaderListDTO.getCustomerID()));
                                            else if (fullfilmentDTOS.size() > 0)
                                                db.cartHeaderDAO().updateisFulfillmentCompleted(Integer.parseInt(fullfilmentDTOS.get(0).getCartHeaderID()), String.valueOf(customerId));
                                        }

                                        for (int p = 0; p < cartHeaderListDTO.getDeliveryDate().size(); p++) {
                                            for (int k = 0; k < cartHeaderListDTO.getDeliveryDate().get(p).getListCartDetailsList().size(); k++) {

                                                CartDetailsListDTO cart = cartHeaderListDTO.getDeliveryDate().get(p).getListCartDetailsList().get(k);

/*                                                if (customerId.equals("") || customerId.isEmpty())
                                                    db.cartHeaderDAO().updateisFulfillmentCompleted(cartHeaderListDTO.getCartHeaderID(), String.valueOf(cartHeaderListDTO.getCustomerID()));
                                                else if (fullfilmentDTOS.size() > 0)
                                                    db.cartHeaderDAO().updateisFulfillmentCompleted(Integer.parseInt(fullfilmentDTOS.get(0).getCartHeaderID()), String.valueOf(customerId));*/

                                                db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                        cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                        cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                        cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                        cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                        cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode(),cart.getDiscountedPrice(),cart.getBOMHeaderID()));
                                            }
                                        }
                                    }
                                }

                                for (int i = 0; i < cartHeadersList.size(); i++) {
                                    db.cartHeaderDAO().updateShipToPatryAndIsPriority(cartHeadersList.get(i).customerID, cartHeadersList.get(i).shipToPartyId, cartHeadersList.get(i).isPriority);
                                }

                                if (value > 0) {

                                    SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                    Calendar calendar = Calendar.getInstance();
                                    long mills = calendar.getTimeInMillis();
                                    sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                    sharedPreferencesUtils.savePreference(KeyValues.TIMER, mills);
                                    long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);

                                    /*
                                    SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                    sharedPreferencesUtils.savePreference(KeyValues.TIMER, 0L);
                                    long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
                                    ((CartActivity) getActivity()).findViewById(R.id.txtTimer).setVisibility(View.GONE);
                                    ((CartActivity) getActivity()).stopTimer();
                                    */


                                    Intent i = new Intent(getActivity(), OrderConfirmationActivity.class);
                                    startActivity(i);
                                    getActivity().finish();

                                } else {
                                    Intent i = new Intent(getActivity(), CartActivity.class);
                                    startActivity(i);
                                    getActivity().finish();
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
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

    public void ProceedWithOrderQty(final int[] cartHeaderId, final List<FullfilmentDTO> fullfilmentDTOS) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.OrderFulfilment_DTO, getActivity());
        CartHeaderListResponseDTO oDto = new CartHeaderListResponseDTO();
        oDto.setCartHeaderID(cartHeaderId);
        oDto.setResult("");
        //oDto.setCartHeaderID(Integer.parseInt(cartHeaderId));

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.ProceedWithOrderQty(message);

        ProgressDialogUtils.showProgressDialog("Please Wait");

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                String respObject = core.getEntityObject().toString();

                                List<CartHeader> cartHeadersList = db.cartHeaderDAO().getCartHeadersForSTP();

                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                db.cartDetailsDAO().deleteAll();
                                db.cartHeaderDAO().deleteAll();

                                for (int i = 0; i < getCartHeader.length(); i++) {

                                    TypeToken<CartHeaderListDTO> header = new TypeToken<CartHeaderListDTO>() {
                                    };

                                    for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                        CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                        db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));

                                        for (int p = 0; p < cartHeaderListDTO.getDeliveryDate().size(); p++) {
                                            for (int k = 0; k < cartHeaderListDTO.getDeliveryDate().get(p).getListCartDetailsList().size(); k++) {

                                                CartDetailsListDTO cart = cartHeaderListDTO.getDeliveryDate().get(p).getListCartDetailsList().get(k);

                                                if (customerId.equals("") || customerId.isEmpty())
                                                    db.cartHeaderDAO().updateisFulfillmentCompleted(cartHeaderListDTO.getCartHeaderID(), String.valueOf(cartHeaderListDTO.getCustomerID()));
                                                else if (fullfilmentDTOS.size() > 0)
                                                    db.cartHeaderDAO().updateisFulfillmentCompleted(Integer.parseInt(fullfilmentDTOS.get(0).getCartHeaderID()), String.valueOf(customerId));

                                                db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                        cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                        cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                        cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                        cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                        cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode(),cart.getDiscountedPrice(),cart.getBOMHeaderID()));
                                            }
                                        }
                                    }
                                }

                                for (int i = 0; i < cartHeadersList.size(); i++) {
                                    db.cartHeaderDAO().updateShipToPatryAndIsPriority(cartHeadersList.get(i).customerID, cartHeadersList.get(i).shipToPartyId, cartHeadersList.get(i).isPriority);
                                }

                                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                sharedPreferencesUtils.savePreference("timer", 0L);
                                long timer = sharedPreferencesUtils.loadPreferenceAsLong("timer");
                                ((CartActivity) getActivity()).findViewById(R.id.txtTimer).setVisibility(View.GONE);
                                ((CartActivity) getActivity()).stopTimer();

                                Intent i = new Intent(getActivity(), OrderConfirmationActivity.class);
                                startActivity(i);
                                getActivity().finish();

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
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

    public void ProceedWithAvbQty(final int[] cartHeaderId, final List<FullfilmentDTO> fullfilmentDTOS) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.OrderFulfilment_DTO, getActivity());
        CartHeaderListResponseDTO oDto = new CartHeaderListResponseDTO();
        oDto.setCartHeaderID(cartHeaderId);
        oDto.setResult("");
        //oDto.setCartHeaderID(Integer.parseInt(cartHeaderId));

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.ProceedWithAvbQty(message);

        ProgressDialogUtils.showProgressDialog("Please Wait");

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                String respObject = core.getEntityObject().toString();

                                List<CartHeader> cartHeadersList = db.cartHeaderDAO().getCartHeadersForSTP();

                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                db.cartDetailsDAO().deleteAll();
                                db.cartHeaderDAO().deleteAll();

                                for (int i = 0; i < getCartHeader.length(); i++) {

                                    TypeToken<CartHeaderListDTO> header = new TypeToken<CartHeaderListDTO>() {
                                    };

                                    for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                        CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                        db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));

                                        for (int p = 0; p < cartHeaderListDTO.getDeliveryDate().size(); p++) {
                                            for (int k = 0; k < cartHeaderListDTO.getDeliveryDate().get(p).getListCartDetailsList().size(); k++) {

                                                CartDetailsListDTO cart = cartHeaderListDTO.getDeliveryDate().get(p).getListCartDetailsList().get(k);

                                                if (customerId.equals("") || customerId.isEmpty())
                                                    db.cartHeaderDAO().updateisFulfillmentCompleted(cartHeaderListDTO.getCartHeaderID(), String.valueOf(cartHeaderListDTO.getCustomerID()));
                                                else if (fullfilmentDTOS.size() > 0)
                                                    db.cartHeaderDAO().updateisFulfillmentCompleted(Integer.parseInt(fullfilmentDTOS.get(0).getCartHeaderID()), String.valueOf(customerId));

                                                db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                        cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                        cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                        cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                        cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                        cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode(),cart.getDiscountedPrice(),cart.getBOMHeaderID()));
                                            }
                                        }
                                    }
                                }

                                for (int i = 0; i < cartHeadersList.size(); i++) {
                                    db.cartHeaderDAO().updateShipToPatryAndIsPriority(cartHeadersList.get(i).customerID, cartHeadersList.get(i).shipToPartyId, cartHeadersList.get(i).isPriority);
                                }

                                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                sharedPreferencesUtils.savePreference(KeyValues.TIMER, 0L);
                                long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
                                ((CartActivity) getActivity()).findViewById(R.id.txtTimer).setVisibility(View.GONE);
                                ((CartActivity) getActivity()).stopTimer();

                                Intent i = new Intent(getActivity(), OrderConfirmationActivity.class);
                                startActivity(i);
                                getActivity().finish();

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
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
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
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


        /*
        if (cbVehicleTypePreference.isChecked()) {
            selectVehicleType.setVisibility(View.VISIBLE);
            VehicleList();
        } else {
            selectVehicleType.setVisibility(View.GONE);
        }
        */

        /*
       if (cbPartialFulfilment.isChecked()) {
            cbSingleDelivery.setChecked(false);
        }else{
            cbPartialFulfilment.setChecked(true);
            cbSingleDelivery.setChecked(false);
        }

        if (cbSingleDelivery.isChecked()) {
            cbPartialFulfilment.setChecked(false);
        }
        */

        if (compoundButton.getId() == R.id.cbPartialFulfilment) {
            if (b) {
                cbSingleDelivery.setChecked(false);
            }
        } else if (compoundButton.getId() == R.id.cbSingleDelivery) {
            if (b) {
                cbPartialFulfilment.setChecked(false);
            }
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

    public void addToCart() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, getActivity());
        productCatalogs oDto = new productCatalogs();
        oDto.setUserID(userId);
        oDto.setProductCatalogs(cartList);
        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please Wait...Cart is Syncing");

        call = apiService.HHTCartDetails(message);


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());

                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            db.cartDetailsDAO().deleteAll();
                            db.cartHeaderDAO().deleteAll();

                            try {

                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                CartHeaderListDTO cartHeaderListDTO;
                                CartDetailsListDTO cart;

                                for (int i = 0; i < getCartHeader.length(); i++) {

                                    for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                        cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);
                                        if (cartHeaderListDTO.getListCartDetailsList().size() > 0) {
                                            db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                    cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                    cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));
                                        }

                                        for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {
                                            cart = cartHeaderListDTO.getListCartDetailsList().get(k);
                                            db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                    cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                    cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                    cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                    cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                    cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode(),cart.getDiscountedPrice(),cart.getBOMHeaderID()));
                                        }
                                    }
                                }

                                /*
                                for(int i=0;i<cartHeadersList.size();i++){
                                    db.cartHeaderDAO().updateShipToPatryAndIsPriority(cartHeadersList.get(i).customerID,cartHeadersList.get(i).shipToPartyId,cartHeadersList.get(i).isPriority);
                                }
                                */

                                Intent i = new Intent(getActivity(), CartActivity.class);
                                startActivity(i);
                                getActivity().finish();

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception e) {
                                ProgressDialogUtils.closeProgressDialog();

                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
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

}
