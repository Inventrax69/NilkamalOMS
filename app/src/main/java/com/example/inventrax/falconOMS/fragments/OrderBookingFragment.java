package com.example.inventrax.falconOMS.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.SOAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.pojos.CustomerListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.PriceDTO;
import com.example.inventrax.falconOMS.pojos.SODetails;
import com.example.inventrax.falconOMS.pojos.SOHeaderDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DateUtils;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.MaterialDialogUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderBookingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String classCode = "OMS_Android_OrderBookingFragment";
    private View rootView;
    private LinearLayout llSOForm, llSOCreation;
    private CardView cvSOHeader, cvSODetails;
    private AutoCompleteTextView auto_shipToParty, auto_paymentTerms, auto_incoTerms;
    private EditText etPONumber, etPODate;
    private SearchableSpinner spinnerSelectCustomer;
    private AppCompatButton btnAddItem, btnBack, btnNext, btnCreateSO;
    private RecyclerView rvAddedItems;
    private TextView txtSoldToParty, txtSONumber, txtSAPSONumber, txtSalesOrg, txtDistChannel, txtDivision, txtEdit;
    private CoordinatorLayout coordinateLayout;
    private View overlay;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    private OMSCoreMessage core;
    private HashMap<String, Integer> custMap;
    private HashMap<Integer, String> hashMapItemDesc;
    private HashMap<String, Integer> hashMapItemHints, hashMapPayment, hashMapInco, hashMapShipToParty;
    private List sugg;
    ArrayAdapter<String> itemAdapter, paymentAdapter, incoAdapter, shipToPartyAdapter;

    private String customerId = "", customerName = "", divisionId = "", mDesc = "";
    private String selectedItem = "", incoTerm = "", paymentTerms = "", shipToParty = "";
    int salesOrgId, distChannelId, mmId, incoTermId, paymentId, shipToPartyId, soldToPartyId;
    private String price = "", conditionType = "";
    private AutoCompleteTextView auto_itemSelection;
    private EditText etQty;
    private TextView txtPrice;
    String myFormat = "dd/MMM/yyyy";
    boolean editMode = false;
    SOAdapter mAdapter;

    private List<SODetails> soDetailsList;

    AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_booking_fragment, container, false);
        try{
            loadFormControls();
        }catch (Exception e){

        }

        return rootView;
    }

    private void loadFormControls() {

        llSOForm = (LinearLayout) rootView.findViewById(R.id.llSOForm);
        llSOCreation = (LinearLayout) rootView.findViewById(R.id.llSOCreation);

        overlay = (View) rootView.findViewById(R.id.overlay);

        cvSOHeader = (CardView) rootView.findViewById(R.id.cvSOHeader);
        cvSODetails = (CardView) rootView.findViewById(R.id.cvSODetails);

        spinnerSelectCustomer = (SearchableSpinner) rootView.findViewById(R.id.spinnerSelectCustomer);
        auto_shipToParty = (AutoCompleteTextView) rootView.findViewById(R.id.auto_shipToParty);
        auto_paymentTerms = (AutoCompleteTextView) rootView.findViewById(R.id.auto_paymentTerms);
        auto_incoTerms = (AutoCompleteTextView) rootView.findViewById(R.id.auto_incoTerms);

        //auto_shipToParty.setThreshold(4);
        auto_paymentTerms.setThreshold(2);
        auto_incoTerms.setThreshold(2);

        etPONumber = (EditText) rootView.findViewById(R.id.etPONumber);
        etPODate = (EditText) rootView.findViewById(R.id.etPODate);

        btnAddItem = (AppCompatButton) rootView.findViewById(R.id.btnAddItem);
        btnNext = (AppCompatButton) rootView.findViewById(R.id.btnNext);
        btnCreateSO = (AppCompatButton) rootView.findViewById(R.id.btnCreateSO);
        btnBack = (AppCompatButton) rootView.findViewById(R.id.btnBack);

        txtSoldToParty = (TextView) rootView.findViewById(R.id.txtSoldToParty);
        txtSONumber = (TextView) rootView.findViewById(R.id.txtSONumber);
        txtSAPSONumber = (TextView) rootView.findViewById(R.id.txtSAPSONumber);
        txtSalesOrg = (TextView) rootView.findViewById(R.id.txtSalesOrg);
        txtDistChannel = (TextView) rootView.findViewById(R.id.txtDistChannel);
        txtDivision = (TextView) rootView.findViewById(R.id.txtDivision);
        txtEdit = (TextView) rootView.findViewById(R.id.txtEdit);

        db = new RoomAppDatabase(getActivity()).getAppDatabase();

        coordinateLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinateLayout);
        rvAddedItems = (RecyclerView) rootView.findViewById(R.id.rvAddedItems);

        btnAddItem.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnCreateSO.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        txtEdit.setOnClickListener(this);

        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        core = new OMSCoreMessage();

        auto_incoTerms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (editMode)
                    incoterms(auto_incoTerms.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        auto_paymentTerms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                /*if(editMode)
                    termPayment();*/

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (editMode)
                    termPayment(auto_paymentTerms.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        auto_shipToParty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (editMode)
                    shiptoPartyCustomer(auto_shipToParty.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) { }

        });

        spinnerSelectCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                customerName = (String) adapterView.getItemAtPosition(i);

                if (!customerName.equalsIgnoreCase("Select Customer")) {

                    customerId = String.valueOf(custMap.get(customerName));

                    if (!customerId.equalsIgnoreCase("")) {
                        cvSODetails.setVisibility(View.VISIBLE);
                        soDependencies();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        auto_paymentTerms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                paymentTerms = (String) adapterView.getItemAtPosition(i);
                paymentId = hashMapPayment.get(paymentTerms);
            }
        });

        auto_incoTerms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                incoTerm = (String) adapterView.getItemAtPosition(i);
                incoTermId = hashMapInco.get(incoTerm);
            }
        });

        auto_shipToParty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                shipToParty = (String) adapterView.getItemAtPosition(i);
                shipToPartyId = hashMapShipToParty.get(shipToParty);
            }
        });

        // Date Picker Dailog integration and validation
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //In which you need put here
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
                    etPODate.setText("");
                    etPODate.setError("Please select valid date");
                    Toast.makeText(getContext(), "Date specified [" + selectedDate + "] is before today [" + today + "]", Toast.LENGTH_SHORT).show();
                } else {
                    etPODate.setError(null);
                    etPODate.setText(sdf.format(myCalendar.getTime()));
                }

            }

        };

        // onClick for edit text to popup the date picker
        etPODate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                String myDate = "2019/08/12";
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                Date date = null;
                try {
                    //date = sdf.parse(myDate);   // If a it requires selected date is mentioned
                    date = sdf.parse(DateUtils.getDate(myFormat));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long millis = date.getTime();
                datePickerDialog.getDatePicker().setMinDate(millis);
                datePickerDialog.show();

            }
        });


        customersunderUser();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAddItem:
                showItemSelectionDialog();
                break;

            case R.id.btnNext:

                llSOForm.setVisibility(View.GONE);
                llSOCreation.setVisibility(View.VISIBLE);

                break;

            case R.id.btnBack:

                llSOForm.setVisibility(View.VISIBLE);
                llSOCreation.setVisibility(View.GONE);

                break;


            case R.id.btnCreateSO:

                if (soDetailsList.size() > 0) {
                    createSO();
                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinateLayout, getString(R.string.add_items_to_proceed), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                }

                break;

            case R.id.txtEdit:

                editMode = true;

                auto_incoTerms.setEnabled(true);
                auto_paymentTerms.setEnabled(true);
                auto_shipToParty.setEnabled(true);

                break;


        }

    }

    private void loadList(final List<SODetails> soDetailsList) {


        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                rvAddedItems.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        layoutManager.setInitialPrefetchItemCount(soDetailsList.size());

        mAdapter = new SOAdapter(getContext(), soDetailsList, new SOAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                soDetailsList.remove(pos);
                mAdapter.notifyDataSetChanged();
            }
        });

        rvAddedItems.setNestedScrollingEnabled(false);
        rvAddedItems.setLayoutManager(layoutManager);
        rvAddedItems.setAdapter(mAdapter);
        rvAddedItems.setRecycledViewPool(viewPool);
    }

    public void customersunderUser() {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());

        message.setEntityObject("");

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.CustomersunderUser(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }

                            } else {

                                try{


                                JSONArray names = null;
                                custMap = new HashMap<>();
                                List<String> customerNames = new ArrayList<>();
                                try {

                                    names = new JSONArray((String) core.getEntityObject());

                                    customerNames.add("Select Customer");

                                    for (int i = 0; i < names.length(); i++) {
                                        custMap.put(names.getJSONObject(i).getString("Label"), names.getJSONObject(i).getInt("Value"));
                                        customerNames.add(names.getJSONObject(i).getString("Label"));
                                    }

                                    if (customerNames.size() > 0) {

                                        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.row_text, customerNames);
                                        spinnerSelectCustomer.setAdapter(adapter);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                }catch (Exception e){

                                }

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

    public void soDependencies() {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());

        CustomerListDTO customerListDTO = new CustomerListDTO();
        customerListDTO.setCustomerID(customerId);

        message.setEntityObject(customerListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.SODependencies(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }

                            } else {

                                JSONObject custDetails;

                                try {
                                    custDetails = new JSONObject(new Gson().toJson(core.getEntityObject()));

                                    txtSoldToParty.setText(custDetails.getString("SoldToCustomer"));
                                    txtSalesOrg.setText(custDetails.getString("SaleOrganization"));
                                    if (!custDetails.getString("SaleOrganizationID").equalsIgnoreCase(""))
                                        salesOrgId = Integer.parseInt(custDetails.getString("SaleOrganizationID"));
                                    txtDistChannel.setText(custDetails.getString("DistributionChannel"));
                                    if (!custDetails.getString("DistributionChannelID").equalsIgnoreCase(""))
                                        distChannelId = Integer.parseInt(custDetails.getString("DistributionChannelID"));
                                    txtDivision.setText(custDetails.getString("Division"));
                                    if (!custDetails.getString("DivisionID").equalsIgnoreCase(""))
                                        divisionId = custDetails.getString("DivisionID");
                                    incoTerm = custDetails.getString("IncoTerms");
                                    paymentTerms = custDetails.getString("PaymentsTerm");
                                    if (!custDetails.getString("IncoTermsID").equalsIgnoreCase(""))
                                        incoTermId = Integer.parseInt(custDetails.getString("IncoTermsID"));
                                    if (!custDetails.getString("PaymentsTermID").equalsIgnoreCase(""))
                                        paymentId = Integer.parseInt(custDetails.getString("PaymentsTermID"));
                                    auto_incoTerms.setText(custDetails.getString("IncoTerms"));
                                    auto_paymentTerms.setText(custDetails.getString("PaymentsTerm"));
                                    auto_shipToParty.setText(custDetails.getString("ShipToCustomer"));
                                    shipToParty = custDetails.getString("ShipToCustomer");
                                    if (!custDetails.getString("ShipToCustomerID").equalsIgnoreCase(""))
                                        shipToPartyId = Integer.parseInt(custDetails.getString("ShipToCustomerID"));
                                    if (!custDetails.getString("SoldToCustomerID").equalsIgnoreCase(""))
                                        soldToPartyId = Integer.parseInt(custDetails.getString("SoldToCustomerID"));

                                    cvSODetails.setVisibility(View.VISIBLE);

                                    soDetailsList = new ArrayList<>();

                                    ProgressDialogUtils.closeProgressDialog();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

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

    public void materialBasedOnDivision(String searchResult) {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());

        CustomerListDTO customerListDTO = new CustomerListDTO();
        customerListDTO.setDivisionID(divisionId);
        customerListDTO.setPrefix(searchResult);

        message.setEntityObject(customerListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.MaterialUnderDivision(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }

                            } else {
                                hashMapItemHints = new HashMap<String, Integer>();
                                hashMapItemDesc = new HashMap<Integer, String>();

                                JSONArray getCartHeader = null;
                                try {
                                    getCartHeader = new JSONArray((String) core.getEntityObject());

                                    for (int i = 0; i < getCartHeader.length(); i++) {

                                        hashMapItemHints.put(getCartHeader.getJSONObject(i).getString("Label"), getCartHeader.getJSONObject(i).getInt("Value"));
                                        hashMapItemDesc.put(getCartHeader.getJSONObject(i).getInt("Value"), getCartHeader.getJSONObject(i).getString("Description"));
                                    }
                                    sugg = new ArrayList();
                                    for (String s : hashMapItemHints.keySet()) {
                                        sugg.add(s);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    public void incoterms(String searchString) {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());

        CustomerListDTO customerListDTO = new CustomerListDTO();
        customerListDTO.setPrefix(searchString);

        message.setEntityObject(customerListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.Incoterms(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }

                            } else {
                                hashMapInco = new HashMap<String, Integer>();

                                JSONArray getCartHeader = null;
                                try {
                                    getCartHeader = new JSONArray((String) core.getEntityObject());

                                    for (int i = 0; i < getCartHeader.length(); i++) {

                                        hashMapInco.put(getCartHeader.getJSONObject(i).getString("Label"), getCartHeader.getJSONObject(i).getInt("Value"));

                                    }
                                    sugg = new ArrayList();
                                    for (String s : hashMapInco.keySet()) {
                                        sugg.add(s);
                                    }


                                    if (getActivity() != null) {
                                        incoAdapter = new ArrayAdapter<String>(getActivity(),
                                                android.R.layout.simple_list_item_1, sugg);
                                        auto_incoTerms.setAdapter(incoAdapter);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    public void termPayment(String searchString) {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());

        CustomerListDTO customerListDTO = new CustomerListDTO();
        customerListDTO.setPrefix(searchString);

        message.setEntityObject(customerListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.TermPayment(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }

                            } else {
                                hashMapPayment = new HashMap<String, Integer>();

                                JSONArray getCartHeader = null;
                                try {
                                    getCartHeader = new JSONArray((String) core.getEntityObject());

                                    for (int i = 0; i < getCartHeader.length(); i++) {

                                        hashMapPayment.put(getCartHeader.getJSONObject(i).getString("Label"), getCartHeader.getJSONObject(i).getInt("Value"));

                                    }
                                    sugg = new ArrayList();
                                    for (String s : hashMapPayment.keySet()) {
                                        sugg.add(s);
                                    }

                                    if (getActivity() != null) {
                                        paymentAdapter = new ArrayAdapter<String>(getActivity(),
                                                android.R.layout.simple_list_item_1, sugg);
                                        auto_paymentTerms.setAdapter(paymentAdapter);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    public void shiptoPartyCustomer(String searchString) {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());

        CustomerListDTO customerListDTO = new CustomerListDTO();
        customerListDTO.setPrefix(searchString);
        customerListDTO.setType(customerId);

        message.setEntityObject(customerListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.ShiptoPartyCustomer(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }

                            } else {
                                hashMapShipToParty = new HashMap<String, Integer>();

                                JSONArray getCartHeader = null;
                                try {
                                    getCartHeader = new JSONArray((String) core.getEntityObject());

                                    for (int i = 0; i < getCartHeader.length(); i++) {

                                        hashMapShipToParty.put(getCartHeader.getJSONObject(i).getString("Label"), getCartHeader.getJSONObject(i).getInt("Value"));

                                    }
                                    sugg = new ArrayList();
                                    for (String s : hashMapShipToParty.keySet()) {
                                        sugg.add(s);
                                    }

                                    if (getActivity() != null) {
                                        shipToPartyAdapter = new ArrayAdapter<String>(getActivity(),
                                                android.R.layout.simple_list_item_1, sugg);
                                        auto_shipToParty.setAdapter(shipToPartyAdapter);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    protected void showItemSelectionDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.add_item_dialog, null);

        auto_itemSelection = promptView.findViewById(R.id.auto_itemSelection);
        etQty = promptView.findViewById(R.id.etQty);
        txtPrice = promptView.findViewById(R.id.txtPrice);
        sugg = new ArrayList();
        itemAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_item, sugg);

        auto_itemSelection.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
        auto_itemSelection.setThreshold(2);

        auto_itemSelection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                materialBasedOnDivision(auto_itemSelection.getText().toString());
                if (getActivity() != null) {
                    itemAdapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, sugg);
                    auto_itemSelection.setAdapter(itemAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        auto_itemSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedItem = (String) adapterView.getItemAtPosition(position);
                mmId = hashMapItemHints.get(selectedItem);
                mDesc = hashMapItemDesc.get(mmId);
                getPrice(customerId, mmId);
            }
        });

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setView(promptView)
                .setPositiveButton("OK", null) //Set to null. We override the onclick

                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (!price.equalsIgnoreCase("")) {

                            if (!etQty.getText().toString().equalsIgnoreCase("")) {

                                if (!etQty.getText().toString().equalsIgnoreCase("0")) {

                                    SODetails soDetails = new SODetails();

                                    if (soDetailsList.size() == 0) {
                                        spinnerSelectCustomer.setEnabled(false);
                                    }

                                    int lineNum = 1;

                                    if (soDetailsList.size() > 0) {
                                        lineNum = soDetailsList.size() + 1;
                                    }

                                    soDetails.setLineNumber(lineNum);
                                    soDetails.setMaterial(selectedItem);
                                    soDetails.setMaterialMasterID(mmId);
                                    soDetails.setQuantity(Integer.parseInt(etQty.getText().toString()));
                                    soDetails.setUnitPrice(price);
                                    soDetails.setConditionTypes(conditionType);
                                    soDetails.setmDesc(hashMapItemDesc.get(mmId));

/*                                    if (db.variantDAO().getMDesc(mmId) != null && !db.variantDAO().getMDesc(mmId).equals("")) {
                                        soDetails.setmDesc(db.variantDAO().getMDesc(mmId));
                                    }*/

                                    soDetailsList.add(soDetails);
                                    loadList(soDetailsList);

                                } else {
                                    SnackbarUtils.showSnackbarLengthShort(coordinateLayout, getString(R.string.enterQty), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                                }
                            } else {
                                SnackbarUtils.showSnackbarLengthShort(coordinateLayout, getString(R.string.enterQty), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                            }

                            dialog.dismiss();
                        } else {
                            SnackbarUtils.showSnackbarLengthShort(coordinateLayout, getString(R.string.PriceNotAvailable), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                        }
                    }
                });

            }
        });

        d.show();


    }

    protected void getPrice(String partnerId, int mmId) {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());
        PriceDTO oDto = new PriceDTO();
        oDto.setMaterialMasterID(String.valueOf(mmId));
        oDto.setPartnerID(partnerId);
        message.setEntityObject(oDto);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.GetPrice(message);
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
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }

                            } else {

                                JSONObject jObject;
                                try {
                                    jObject = new JSONObject(new Gson().toJson(core.getEntityObject()));

                                    price = jObject.getString("Amount");
                                    conditionType = jObject.getString("Conditiontype");
                                    txtPrice.setText("Price:" + "" + jObject.getString("Amount"));


                                } catch (Exception e) {

                                }


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

    protected void createSO() {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.SOHeader_DTO, getActivity());
        SOHeaderDTO oDto = new SOHeaderDTO();
        oDto.setOrderTypeID(1);
        oDto.setDistributionChannel(txtDistChannel.getText().toString());
        oDto.setDistributionChannelID(distChannelId);
        oDto.setDivision(txtDivision.getText().toString());
        oDto.setSoldToCustomer(txtSoldToParty.getText().toString());
        oDto.setSoldToCustomerID(soldToPartyId);
        oDto.setShipToCustomer(shipToParty);
        oDto.setShipToCustomerID(shipToPartyId);
        oDto.setDivisionID(Integer.parseInt(divisionId));
        oDto.setIncoTerms(incoTerm);
        oDto.setIncoTermsID(incoTermId);
        oDto.setPaymentsTerm(paymentTerms);
        oDto.setPaymentsTermID(paymentId);
        oDto.setShipToCustomer(shipToParty);
        oDto.setShipToCustomerID(shipToPartyId);
        oDto.setPODate(etPODate.getText().toString());
        oDto.setPONumber(etPONumber.getText().toString());
        oDto.setSaleOrganization(txtSalesOrg.getText().toString());
        oDto.setSaleOrganizationID(salesOrgId);
        oDto.setSODetails(soDetailsList);
        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.NHIOrderCreation(message);
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
                                    common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                                }


                            } else {

                                try{
                                    if (!((String) response.body().getEntityObject()).equalsIgnoreCase("")) {
                                        txtSONumber.setText((String) response.body().getEntityObject());
                                        MaterialDialogUtils.showUploadSuccessDialog(getActivity(), "Done");
                                        btnCreateSO.setVisibility(View.GONE);
                                        txtEdit.setVisibility(View.GONE);
                                        btnAddItem.setVisibility(View.GONE);
                                        overlay.setVisibility(View.VISIBLE);
                                    }
                                }catch (Exception e){

                                }

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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderbooking));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

}
