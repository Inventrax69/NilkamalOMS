package com.example.inventrax.falconOMS.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.ApprovalHeaderAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.CustomerPartnerDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.productCatalogs;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Padmaja on 04/07/2019.
 */

public class CartApprovalFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_Activity_Cart";
    private View rootView;
    Toolbar toolbar;
    private FrameLayout frame;
    private RecyclerView rvCartItemsList;
    private SearchableSpinner spinnerSelectCustomer;
    LinearLayoutManager linearLayoutManager;
    AppDatabase db;
    ApprovalHeaderAdapter mAdapter;
    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;
    private String userId = "", selectedVehicle = "";
    List<CartHeaderListDTO> cartHeaderList = null;
    List<CartHeader> cartHeader;
    String customerId;
    android.support.v7.app.AlertDialog alertDialog;
    CoordinatorLayout coordinatorLayout;
    List<CustomerPartnerDTO> customerPartnerDTOS_list;
    LinearLayout firstLayout, secondLayout;
    TextView check;

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

            firstLayout = rootView.findViewById(R.id.firstLayout);
            secondLayout = rootView.findViewById(R.id.secondLayout);
            check = rootView.findViewById(R.id.check);

            rvCartItemsList = (RecyclerView) rootView.findViewById(R.id.rvCartItemsList);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            rvCartItemsList.setLayoutManager(linearLayoutManager);

            cartHeaderList = new ArrayList<>();

            common = new Common();
            errorMessages = new ErrorMessages();
            exceptionLoggerUtils = new ExceptionLoggerUtils();
            restService = new RestService();
            core = new OMSCoreMessage();

            if (NetworkUtils.isInternetAvailable(getActivity())) {
                PendingCartList();
            } else {
                changeLayout("1");
            }


/*            List<CustomerTable> getCustomerNames;
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
                    } else {
                        customerId = customerIds.get(i).toString();
                    }

                    callCart();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });*/

        } catch (Exception ex) {
            //Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void changeLayout(String s) {
        //empty_cart.setVisibility(View.VISIBLE);
        // llCartProducts.setVisibility(View.GONE);

        firstLayout.setVisibility(View.GONE);
        secondLayout.setVisibility(View.VISIBLE);


        if (s.equals("1"))
            check.setText("No Internet Connection");
        else
            check.setText("No Pending Cart");


    }

    private void loadCart() {

        cartHeaderList = new ArrayList<>();

        if (db.cartHeaderDAO().getAll() != null) {

            List<CartHeader> cartHeadersList;
            if (customerId.equals("")) {
                cartHeadersList = db.cartHeaderDAO().getAllWithApprovals();
            } else {
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
                rvCartItemsList.setAdapter(mAdapter);
            } else {

            }
        } else {
            // (MainActivity).stopTimer();
        }

        alertDialog.dismiss();
    }


    public void PendingCartList() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());
        productCatalogs productCatalogs = new productCatalogs();
        productCatalogs.setHandheldRequest(true);
        productCatalogs.setUserID(userId);
        message.setEntityObject(productCatalogs);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.PendingCartList(message);


        Log.v("ABCDE", new Gson().toJson(message));

        ProgressDialogUtils.showProgressDialog("Please wait..");

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
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                if (core.getEntityObject() == null) {
                                    changeLayout("2");
                                    ProgressDialogUtils.closeProgressDialog();
                                    return;
                                }

                                JSONArray getCustomerList = new JSONArray((String) core.getEntityObject());

                                CustomerPartnerDTO customerPartnerDTO = new CustomerPartnerDTO();
                                List<CustomerPartnerDTO> customerPartnerDTOS = new ArrayList<>();
                                customerPartnerDTOS_list = new ArrayList<>();

                                for (int i = 0; i < getCustomerList.length(); i++) {
                                    customerPartnerDTO = new Gson().fromJson(getCustomerList.getJSONObject(i).toString(), CustomerPartnerDTO.class);
                                    customerPartnerDTOS.add(customerPartnerDTO);
                                }

                                cartHeaderList = new ArrayList<>();

                                if (customerPartnerDTOS.size() > 0) {

                                    for (int i = 0; i < customerPartnerDTOS.size(); i++) {
                                        if (customerPartnerDTOS.get(i).getCartHeader().size() > 0) {
                                            customerPartnerDTOS_list.add(customerPartnerDTOS.get(i));
                                            cartHeaderList.addAll(customerPartnerDTOS.get(i).getCartHeader());
                                        }
                                    }


                                    List<String> customerCodes;
                                    final List<String> customerIds;

                                    customerCodes = new ArrayList<>();
                                    customerIds = new ArrayList<>();

                                    customerCodes.add("Select Customer");
                                    customerIds.add("");

                                    for (int i = 0; i < customerPartnerDTOS_list.size(); i++) {
                                        customerCodes.add(customerPartnerDTOS_list.get(i).getCustomerName());
                                        customerIds.add(String.valueOf(customerPartnerDTOS_list.get(i).getCustomerID()));
                                    }

                                    ArrayAdapter<String> arrayAdapterPrinters = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, customerCodes);
                                    spinnerSelectCustomer.setAdapter(arrayAdapterPrinters);

                                    spinnerSelectCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            if (spinnerSelectCustomer.getSelectedItem().toString().equals("Select Customer")) {
                                                customerId = "";
                                                if (cartHeaderList.size() > 0) {

                                                    List<CartHeaderListDTO> cloneCartHeaderList = new GetCartHeaderList(cartHeaderList).getCartHeaderList_Class();
                                                    List<CartHeaderListDTO> cartHeaderList1 = new GetCartHeaderList(cartHeaderList).getCartHeaderList_Class();

                                                    // List<CartHeaderListDTO> cloneCartHeaderList = ((List<CartHeaderListDTO>) ((ArrayList<CartHeaderListDTO>) cartHeaderList).clone());
                                                    // List<CartHeaderListDTO> cartHeaderList1 = ((List<CartHeaderListDTO>) ((ArrayList<CartHeaderListDTO>) cartHeaderList).clone());

                                                    // List<CartHeaderListDTO> cloneCartHeaderList =new ArrayList<>();
                                                    // List<CartHeaderListDTO> cartHeaderList1=new ArrayList<>();

                                                    // cloneCartHeaderList = Collections.unmodifiableList(cartHeaderList);
                                                    // cartHeaderList1 = Collections.unmodifiableList(cartHeaderList);

                                                    // List<CartHeaderListDTO> mutableList = cartHeaderList.getImmutableList().stream().collect(Collectors.toList());

                                                    // Log.v("ABCDE",new Gson().toJson(cartHeaderList));


                                                    for (int p = 0; p < cloneCartHeaderList.size(); p++) {

                                                        HashMap<String, List<CartDetailsListDTO>> offerCartDetailsDTOList = new HashMap<>();
                                                        boolean check = false;

                                                        for (int q = 0; q < cloneCartHeaderList.get(p).getListCartDetailsList().size(); q++) {

                                                            if (cloneCartHeaderList.get(p).getListCartDetailsList().get(q).getOfferItemCartDetailsID() != null) {
                                                                if (!cloneCartHeaderList.get(p).getListCartDetailsList().get(q).getOfferItemCartDetailsID().equals("-1")) {
                                                                    if (cloneCartHeaderList.get(p).getOfferCartDetailsDTOList() == null) {
                                                                        List<CartDetailsListDTO> cartDetailsListDTOS = offerCartDetailsDTOList.get(cloneCartHeaderList.get(p).getListCartDetailsList().get(q).getOfferItemCartDetailsID());
                                                                        if (cartDetailsListDTOS == null) {
                                                                            cartDetailsListDTOS = new ArrayList<>();
                                                                            cartDetailsListDTOS.add(cloneCartHeaderList.get(p).getListCartDetailsList().get(q));
                                                                            offerCartDetailsDTOList.put(cloneCartHeaderList.get(p).getListCartDetailsList().get(q).getOfferItemCartDetailsID(), cartDetailsListDTOS);
                                                                            cartHeaderList1.get(p).getListCartDetailsList().remove(q);
                                                                            check=true;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        if (check)
                                                            cartHeaderList1.get(p).setOfferCartDetailsDTOList(offerCartDetailsDTOList);
                                                    }

                                                    if (cartHeaderList1 != null) {
                                                        mAdapter = new ApprovalHeaderAdapter(cartHeaderList1, getActivity(), getActivity());
                                                        rvCartItemsList.setAdapter(mAdapter);
                                                    }
                                                }
                                            } else {
                                                customerId = customerIds.get(i).toString();
                                                if (customerPartnerDTOS_list.get(i - 1).getCartHeader().size() > 0) {

                                                    List<CartHeaderListDTO> cartHeaderList1 = new ArrayList<>(customerPartnerDTOS_list.get(i - 1).getCartHeader());

                                                    for (int p = 0; p < customerPartnerDTOS_list.get(i - 1).getCartHeader().size(); p++) {

                                                        HashMap<String, List<CartDetailsListDTO>> offerCartDetailsDTOList = new HashMap<>();

                                                        boolean check = false;

                                                        for (int q = 0; q < customerPartnerDTOS_list.get(i - 1).getCartHeader().get(p).getListCartDetailsList().size(); q++) {
                                                            if (customerPartnerDTOS_list.get(i - 1).getCartHeader().get(p).getListCartDetailsList().get(q).getOfferItemCartDetailsID() != null) {
                                                                if (!customerPartnerDTOS_list.get(i - 1).getCartHeader().get(p).getListCartDetailsList().get(q).getOfferItemCartDetailsID().equals("-1")) {
                                                                    if (customerPartnerDTOS_list.get(i - 1).getCartHeader().get(p).getOfferCartDetailsDTOList() == null) {
                                                                        List<CartDetailsListDTO> cartDetailsListDTOS = offerCartDetailsDTOList.get(customerPartnerDTOS_list.get(i - 1).getCartHeader().get(p).getListCartDetailsList().get(q).getOfferItemCartDetailsID());
                                                                        if (cartDetailsListDTOS == null) {
                                                                            cartDetailsListDTOS = new ArrayList<>();
                                                                            cartDetailsListDTOS.add(customerPartnerDTOS_list.get(i - 1).getCartHeader().get(p).getListCartDetailsList().get(q));
                                                                            offerCartDetailsDTOList.put(customerPartnerDTOS_list.get(i - 1).getCartHeader().get(p).getListCartDetailsList().get(q).getOfferItemCartDetailsID(), cartDetailsListDTOS);
                                                                            cartHeaderList1.get(p).getListCartDetailsList().remove(q);
                                                                            check = true;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (check)
                                                            cartHeaderList1.get(p).setOfferCartDetailsDTOList(offerCartDetailsDTOList);
                                                    }

                                                    if (cartHeaderList1 != null) {
                                                        mAdapter = new ApprovalHeaderAdapter(cartHeaderList1, getActivity(), getActivity());
                                                        rvCartItemsList.setAdapter(mAdapter);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                } else {
                                    changeLayout("2");
                                }

                                ProgressDialogUtils.closeProgressDialog();

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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


    public class GetCartHeaderList {

        List<CartHeaderListDTO> cartHeaderList_Class;

        GetCartHeaderList(List<CartHeaderListDTO> cartHeaderList_Class) {
            this.cartHeaderList_Class = cartHeaderList_Class;
        }

        public List<CartHeaderListDTO> getCartHeaderList_Class() {
            return cartHeaderList_Class;
        }

        public void setCartHeaderList_Class(List<CartHeaderListDTO> cartHeaderList_Class) {
            this.cartHeaderList_Class = cartHeaderList_Class;
        }

    }
}
