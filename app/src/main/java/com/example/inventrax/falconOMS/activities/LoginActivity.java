package com.example.inventrax.falconOMS.activities;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.application.AbstractApplication;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.common.constants.ServiceURL;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.locale.LocaleHelper;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.model.Selectedlanguage;
import com.example.inventrax.falconOMS.pojos.CustomerListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListDTO;
import com.example.inventrax.falconOMS.pojos.LoginDTO;
import com.example.inventrax.falconOMS.pojos.ModelDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreAuthentication;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.VariantDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.room.UserDivisionCustTable;
import com.example.inventrax.falconOMS.room.VariantTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.AndroidUtils;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.Encryption;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.NotificationUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SoundUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Padmaja on 04/07/2019.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String classCode = "OMS_Android_Activity_Login";
    private EditText inputUserId, inputPassword, etEmail, etOTP;
    private Button btnLogin;
    private CheckBox chkRememberPassword;
    private TextView tvForgotPwd, timer, tvMobileNumber;
    private Spinner languagespinner;
    private LinearLayout llLogin, llForgotPwd, llOTPCapture;
    private ProgressDialogUtils progressDialogUtils;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private Gson gson;
    private OMSCoreMessage core;
    private Common common;
    private SoundUtils soundUtils;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private NotificationUtils notificationUtils;
    private Encryption encryption;
    private List<ModelDTO> lstItem;
    private List<CustomerListDTO> customerList;
    RestService restService;
    private String encryptedPass = "", customerIDs = "", userId = "", serviceUrlString = "";
    private Resources resources;
    ServiceURL serviceURL;
    RelativeLayout main_relative, login_tool_relative;
    List<CustomerTable> customerTables;
    List<ItemTable> itemTables;
    ImageView ivNilkamal;
    AppDatabase db;
    AlertDialog dialog;
    ImageView settings;
    String firebase_token;
    int pageIndex = 1;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            loadFormControls();
            FirebaseMessaging.getInstance().subscribeToTopic("all");
        } catch (Exception ex) {
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0016);
            return;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_login);
        loadFormControls();
    }

    //Loading all the form controls
    private void loadFormControls() {

        try {

            common = new Common();
            notificationUtils = new NotificationUtils();
            encryption = new Encryption();
            errorMessages = new ErrorMessages();
            serviceURL = new ServiceURL();
            exceptionLoggerUtils = new ExceptionLoggerUtils();
            restService = new RestService();
            core = new OMSCoreMessage();
            resources = getResources();

            firebase_token = FirebaseInstanceId.getInstance().getToken();

            sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getApplicationContext());
            languagespinner = (Spinner) findViewById(R.id.languagespinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.language_array, android.R.layout.simple_spinner_dropdown_item);
            languagespinner.setAdapter(adapter);

            if (sharedPreferencesUtils.loadPreference(KeyValues.SELECTED_LANG) != null) {
                if (sharedPreferencesUtils.loadPreference(KeyValues.SELECTED_LANG).equals("hi")) {
                    languagespinner.setSelection(2, false);
                } else if (sharedPreferencesUtils.loadPreference(KeyValues.SELECTED_LANG).equals("te")) {
                    languagespinner.setSelection(1, false);
                } else {
                    languagespinner.setSelection(0, false);
                }
            } else {
                languagespinner.setSelection(0, false);
            }

            languagespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            LocaleHelper.setLocale(LoginActivity.this, "en");
                            // DialogUtils.showAlertDialog(LoginActivity.this, resources.getString(R.string.msg));
                            sharedPreferencesUtils.savePreference(KeyValues.SELECTED_LANG, "en");
                            Selectedlanguage.setLanguage(1);
                            break;
                        case 1:
                            LocaleHelper.setLocale(LoginActivity.this, "te");
                            // DialogUtils.showAlertDialog(LoginActivity.this, resources.getString(R.string.msg));
                            sharedPreferencesUtils.savePreference(KeyValues.SELECTED_LANG, "te");
                            Selectedlanguage.setLanguage(2);
                            break;
                        case 2:
                            LocaleHelper.setLocale(LoginActivity.this, "hi");
                            // DialogUtils.showAlertDialog(LoginActivity.this, resources.getString(R.string.msg));
                            sharedPreferencesUtils.savePreference(KeyValues.SELECTED_LANG, "hi");
                            Selectedlanguage.setLanguage(3);
                            break;

                    }
                    Configuration newConfig = new Configuration();
                    newConfig.locale = getResources().getConfiguration().locale;
                    onConfigurationChanged(newConfig);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            });

            db = new RoomAppDatabase(LoginActivity.this).getAppDatabase();

            // db.cartHeaderDAO().deleteAll();
            // db.cartDetailsDAO().deleteAll();

            lstItem = new ArrayList<ModelDTO>();
            customerList = new ArrayList<CustomerListDTO>();

            inputUserId = (EditText) findViewById(R.id.etUsername);
            inputPassword = (EditText) findViewById(R.id.etPass);
            etEmail = (EditText) findViewById(R.id.etEmail);
            etOTP = (EditText) findViewById(R.id.etOTP);
            ivNilkamal = (ImageView) findViewById(R.id.ivNilkamal);

            chkRememberPassword = (CheckBox) findViewById(R.id.cbRememberMe);

            llForgotPwd = (LinearLayout) findViewById(R.id.llForgotPwd);
            llOTPCapture = (LinearLayout) findViewById(R.id.llOTPCapture);
            main_relative = (RelativeLayout) findViewById(R.id.main_relative);
            login_tool_relative = (RelativeLayout) findViewById(R.id.login_tool_relative);

            soundUtils = new SoundUtils();
            inputUserId.addTextChangedListener(new LoginViewTextWatcher(inputUserId));
            inputPassword.addTextChangedListener(new LoginViewTextWatcher(inputPassword));
            gson = new GsonBuilder().create();

            btnLogin = (Button) findViewById(R.id.btnLogin);

            tvForgotPwd = (TextView) findViewById(R.id.tvForgotPwd);
            tvMobileNumber = (TextView) findViewById(R.id.tvMobileNumber);
            timer = (TextView) findViewById(R.id.timer);

            settings = (ImageView) findViewById(R.id.ivSettings);

            try {
                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                });
            } catch (Exception ex) {

            }

            btnLogin.setOnClickListener(this);

            tvForgotPwd.setOnClickListener(this);

            progressDialogUtils = new ProgressDialogUtils(this);

            ServiceURL.setServiceUrl(serviceUrlString);

            if (sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_REMEMBER_PASSWORD_CHECKED, false)) {
                inputUserId.setText(sharedPreferencesUtils.loadPreference(KeyValues.EMAIL, ""));
                inputPassword.setText(sharedPreferencesUtils.loadPreference(KeyValues.PASSWORD, ""));
                chkRememberPassword.setChecked(true);
            } else {
                inputUserId.setText(sharedPreferencesUtils.loadPreference(KeyValues.EMAIL, ""));
                inputPassword.setText(sharedPreferencesUtils.loadPreference(KeyValues.PASSWORD, ""));
                sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_REMEMBER_PASSWORD_CHECKED, true);
            }

            AbstractApplication.CONTEXT = getApplicationContext();

            setKeyboardListener();

        } catch (Exception ex) {
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0016);
            return;
        }
    }

    private int heightDiff;
    private boolean wasOpened;
    private final int DefaultKeyboardDP = 100;
    // Lollipop includes button bar in the root. Add height of button bar (48dp) to maxDiff
    private final int EstimatedKeyboardDP = DefaultKeyboardDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);

    public final void setKeyboardListener() {

        final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private final Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                // Convert the dp to pixels.
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, activityRootView.getResources().getDisplayMetrics());

                // Conclude whether the keyboard is shown or not.
                activityRootView.getWindowVisibleDisplayFrame(r);
                heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == wasOpened) {
                    return;
                }

                wasOpened = isShown;
                if (isShown) {
                    main_relative.setVisibility(View.GONE);
                    login_tool_relative.setVisibility(View.VISIBLE);
                } else {
                    login_tool_relative.setVisibility(View.GONE);
                    main_relative.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvForgotPwd:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivty.class));
                break;

            case R.id.btnLogin:

                InputMethodManager imm = (InputMethodManager) LoginActivity.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputPassword.getWindowToken(), 0);

                SharedPreferences sp = this.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
                serviceUrlString = sp.getString(KeyValues.SETTINGS_URL, "");

                if (serviceUrlString != null && !serviceUrlString.equals("")) {

                    if (!inputUserId.getText().toString().isEmpty() && !inputPassword.getText().toString().isEmpty()) {

                        String pass = inputPassword.getText().toString();

                        try {
                            // Encrypting password with the key
                            encryptedPass = Encryption.encryptData(pass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(NetworkUtils.isInternetAvailable(LoginActivity.this)) {

                            new AsyncTask<Void, Integer, String>() {

                                @Override
                                protected String doInBackground(Void... voids) {
                                    synchronized (this) {
                                        validateUserSession();
                                    }
                                    return null;
                                }
                            }.execute();
                        }else {
                            DialogUtils.showAlertDialog(LoginActivity.this,errorMessages.EMC_0007);
                        }


                    /*getProductCatalog();
                    getCustomerList();*/

/*                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);*/

                        //If User Clicks on remember me username,Password is stored in Shared preferences
                        if (chkRememberPassword.isChecked()) {
                            sharedPreferencesUtils.savePreference(KeyValues.EMAIL, inputUserId.getText().toString().trim());
                            sharedPreferencesUtils.savePreference(KeyValues.PASSWORD, inputPassword.getText().toString().trim());
                            sharedPreferencesUtils.savePreference(KeyValues.IS_REMEMBER_PASSWORD_CHECKED, true);
                        }
                    } else {

                    }
                } else {
                    DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0017);
                }
                break;
        }

    }

    public void getProductCatalog() {

        if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {

        } else {
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0007);
            // soundUtils.alertSuccess(getActivity(),getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, LoginActivity.this);
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setSearchString(null);
        itemListDTO.setFilter("0");
        itemListDTO.setPageIndex(1);
        itemListDTO.setPageSize(10);
        itemListDTO.setHandheldRequest(true);
        message.setEntityObject(itemListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");
        call = apiService.ProductCatalog(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, LoginActivity.this, LoginActivity.this);

                            }

                            dialog.dismiss();

                        } else {

                            try {

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                itemTables = new ArrayList<>();
                                ItemListDTO itemList;

                                try {

                                    itemList = new ItemListDTO(_lstItem.entrySet());
                                    lstItem = itemList.getResults();


                                    if (lstItem != null && lstItem.size() > 0) {

                                        new AsyncTask<Void, Integer, String>() {

                                            @Override
                                            protected String doInBackground(Void... voids) {
                                                synchronized (this) {

                                                    // deleting model(item table) table and variant tables
                                                    db.itemDAO().deleteAll();
                                                    db.variantDAO().deleteAll();

                                                    List<ItemTable> itemTableList = new ArrayList<>();
                                                    List<VariantTable> variantTableList = new ArrayList<>();


                                                    for (ModelDTO md : lstItem) {

                                                        itemTableList.add(new ItemTable(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                                md.getModelDescription(), md.getImgPath(), "0", "0", ""));
                                                        /*  db.itemDAO().insert(new ItemTable(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                                md.getModelDescription(), md.getImgPath(), md.getDiscountCount(), md.getDiscountId(), md.getDiscountDesc()));*/


                                                        for (VariantDTO variantDTO : md.getVarientList()) {

                                                            variantTableList.add(new VariantTable(md.getModelID(), md.getDivisionID(),
                                                                    variantDTO.getMaterialID(), variantDTO.getMDescription(), variantDTO.getMDescriptionLong(),
                                                                    variantDTO.getMcode(), variantDTO.getModelColor(), variantDTO.getMaterialImgPath(),
                                                                    "0", "0", "",
                                                                    variantDTO.getProductSpecification(), variantDTO.getProductCatalog(), variantDTO.getEBrochure(), variantDTO.getOpenPrice(), (int) Double.parseDouble(variantDTO.getStackSize())));

                                                        }

                                                    }

                                                    synchronized (this) {
                                                        // inserting all the items list (Models list)
                                                        db.itemDAO().insertAll(itemTableList);
                                                    }
                                                    // inserting all the variants in variant table
                                                    db.variantDAO().insertAll(variantTableList);


                                                    dialog.dismiss();

                                                    sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, true);
                                                    sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_LOADED, true);
                                                    Date date = Calendar.getInstance().getTime();
                                                    //
                                                    // Display a date in day, month, year format
                                                    //
                                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                    String todaysdate = formatter.format(date);

                                                    sharedPreferencesUtils.savePreference(KeyValues.ITEM_MASTER_SYNC_TIME, todaysdate);
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                                return null;
                                            }
                                        }.execute();

                                    } else {

                                        sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, true);
                                        sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_LOADED, true);
                                        Date date = Calendar.getInstance().getTime();
                                        //
                                        // Display a date in day, month, year format
                                        //
                                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String todaysdate = formatter.format(date);
                                        sharedPreferencesUtils.savePreference(KeyValues.ITEM_MASTER_SYNC_TIME, todaysdate);

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                    ProgressDialogUtils.closeProgressDialog();

                                } catch (Exception e) {
                                    dialog.dismiss();
                                    common.showUserDefinedAlertType(errorMessages.EMC_0018, LoginActivity.this, LoginActivity.this, "Warning");
                                }

                            } catch (Exception ex) {
                                dialog.dismiss();
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
                    if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {
                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                    dialog.dismiss();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", LoginActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0003);
        }
    }

    public void getCustomerList() {

        if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {

        } else {
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0007);
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Customer_FPS_DTO, LoginActivity.this);
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setPageIndex(0);
        itemListDTO.setPageSize(0);
        itemListDTO.setHandheldRequest(true);
        message.setEntityObject(itemListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.GetCustomerListMobile(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, LoginActivity.this, LoginActivity.this);
                            }

                            dialog.dismiss();

                        } else {

                            try {

                                LinkedTreeMap<String, String> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                CustomerListDTO itemList;
                                final List<CustomerListDTO> lstDto = new ArrayList<CustomerListDTO>();


                                itemList = new CustomerListDTO(_lstItem.entrySet());

                                if (itemList.getResults() != null) {
                                    customerTables = new ArrayList<>();

                                    SharedPreferences sp = LoginActivity.this.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
                                    customerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");
                                    userId = sp.getString(KeyValues.USER_ID, "");

                                    // deleting existed user table
                                    db.userDivisionCustDAO().deleteAll();

                                    for (CustomerListDTO dd : itemList.getResults()) {

                                        lstDto.add(dd);

                                        customerTables.add(new CustomerTable(dd.getCustomerID(), dd.getCustomerName(), dd.getCustomerCode(),
                                                dd.getCustomerType(), dd.getCustomerTypeID(), dd.getDivision(), dd.getDivisionID().split("[.]")[0], dd.getConnectedDepot(), dd.getMobile(),
                                                dd.getPrimaryID(), dd.getSalesDistrict(), dd.getZone(), dd.getCity()));

                                        db.userDivisionCustDAO().insert(new UserDivisionCustTable(dd.getCustomerID(), dd.getDivisionID().split("[.]")[0]));

                                    }

                                    customerList = lstDto;
                                    if (customerList != null && customerList.size() > 0) {

                                        new AsyncTask<Void, Integer, String>() {

                                            @Override
                                            protected String doInBackground(Void... voids) {
                                                synchronized (this) {
                                                    db.customerDAO().deleteAll();
                                                    // inserting customers into local database
                                                    db.customerDAO().insertAll(customerTables);

                                                    Date date = Calendar.getInstance().getTime();
                                                    //
                                                    // Display a date in day, month, year format
                                                    //
                                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                    String todaysdate = formatter.format(date);

                                                    sharedPreferencesUtils.savePreference(KeyValues.CUSTOMER_MASTER_SYNC_TIME, todaysdate);
                                                    // service call to get list of Models and Variants under the model
                                                    getProductCatalog();

                                                }
                                                return null;
                                            }
                                        }.execute();


                                    } else {

                                        dialog.dismiss();

                                        sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, true);
                                        sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_LOADED, true);
                                        Date date = Calendar.getInstance().getTime();
                                        //
                                        // Display a date in day, month, year format
                                        //
                                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String todaysdate = formatter.format(date);

                                        sharedPreferencesUtils.savePreference(KeyValues.CUSTOMER_MASTER_SYNC_TIME, todaysdate);

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                    ProgressDialogUtils.closeProgressDialog();
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                dialog.dismiss();
                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {
                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", LoginActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0003);
        }
    }

    //Validating the User credentials and Calling the API method
    public void validateUserSession() {

        if (NetworkUtils.isInternetAvailable(this)) {

        } else {
            DialogUtils.showAlertDialog(this, errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        OMSCoreAuthentication token = new OMSCoreAuthentication();
        LoginDTO loginDTO = new LoginDTO();
        token.setAuthKey("asdasd");
        token.setUserId("0");
        token.setAuthValue("asdasd");
        token.setLoginTimeStamp("string");
        token.setAuthToken("asdasdasd");
        token.setRequestNumber(0);
        token.setCookieIdentifier("adada");
        token.setSSOUSerID(0);
        token.setLoggedInUserID("asd");
        token.setTransactionUserID("asd");
        message.setType(EndpointConstants.LoginDTO);
        message.setAuthToken(token);
        loginDTO.setUserID("suresh");
        loginDTO.seteMail(inputUserId.getText().toString());
        loginDTO.setPassword(encryptedPass);
        loginDTO.setUserIP(AndroidUtils.getIPAddress(true));
        loginDTO.setClientSessionID("125484745844");
        loginDTO.setClientMAC("asdasd");
        loginDTO.setSessionIdentifier("asdasdsad");
        loginDTO.setClientParams("");
        loginDTO.setFCMToken(firebase_token);
        loginDTO.setWeb(false);
        loginDTO.setUserName("Suresh");
        message.setEntityObject(loginDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService;
        try {
            apiService = RestService.getClient().create(ApiInterface.class);
        } catch (Exception e) {
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0015);
            return;
        }


        call = apiService.LoginUser(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();
                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {
                            List<OMSExceptionMessage> _lExceptions = new ArrayList<OMSExceptionMessage>();
                            _lExceptions = core.getOMSMessages();

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, LoginActivity.this, LoginActivity.this);

                            }

                        } else {

                            try {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<String, String> _lstLogindetails = new LinkedTreeMap<String, String>();
                                _lstLogindetails = (LinkedTreeMap<String, String>) core.getEntityObject();
                                if (_lstLogindetails != null) {

                                    LoginDTO login;

                                    login = new LoginDTO((((LinkedTreeMap<String, String>) core.getEntityObject()).entrySet()));

                                    // Storing user information into shared preference
                                    if (login.getUserId() != null && login.getUserName() != null && login.getCustomers() != null) {
                                        sharedPreferencesUtils.savePreference(KeyValues.USER_ID, login.getUserId().split("[.]")[0]);
                                        sharedPreferencesUtils.savePreference(KeyValues.USER_NAME, login.getUserName());
                                        sharedPreferencesUtils.savePreference(KeyValues.CART_HEADERID, login.getCartHeaderID());
                                        sharedPreferencesUtils.savePreference(KeyValues.CUSTOMER_IDS, gson.toJson(login.getCustomers()));
                                        sharedPreferencesUtils.savePreference(KeyValues.USER_ROLE_NAME, login.getUserRoleName());
                                        // sharedPreferencesUtils.savePreference(KeyValues.CUSTOMER_IDS, gson.toJson(login.getCustomers()));
                                    } else {
                                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0002);
                                        return;
                                    }

                                    sharedPreferencesUtils.savePreference(KeyValues.TIMER, 0L);
                                    sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, false);
                                    sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_LOADED, false);

                                    db.cartDetailsDAO().deleteAll();
                                    db.cartHeaderDAO().deleteAll();

                                    setProgressDialog();


                                    /**
                                     * Waits if necessary for the computation to complete, and then
                                     * retrieves its result.
                                     *
                                     * @return The computed result.
                                     *
                                     * @throws CancellationException If the computation was cancelled.
                                     * @throws ExecutionException If the computation threw an exception.
                                     * @throws InterruptedException If the current thread was interrupted
                                     *         while waiting.
                                     */
                                    new AsyncTask<Void, Integer, String>() {

                                        @Override
                                        protected String doInBackground(Void... voids) {
                                            synchronized (this) {
                                                // service call to get the customer under the user
                                                getCustomerList();
                                            }
                                            return null;
                                        }
                                    }.execute();


/*                                        }else{
                                        SharedPreferences sp = LoginActivity.this.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
                                        customerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");
                                        userId = sp.getString(KeyValues.USER_ID, "");

                                        JSONArray jsonArray = null;
                                        try {
                                            jsonArray = new JSONArray(customerIDs);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        db.userDivisionCustDAO().deleteAll();
                                        db.cartHeaderDAO().deleteAll();
                                        db.cartDetailsDAO().deleteAll();

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            try {
                                                CustomerTable custTable;
                                                custTable = db.customerDAO().getCustomer(jsonArray.getString(i));

                                                if (jsonArray.getString(i) != null) {
                                                    db.userDivisionCustDAO().insert(new UserDivisionCustTable(custTable.customerId, custTable.divisionId));
                                                }

                                            } catch (Exception e) {
                                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, true);
                                        sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_LOADED, true);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }*/
                                } else {
                                    DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0002);
                                }
                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {
                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", LoginActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0003);
        }
    }

    private class LoginViewTextWatcher implements TextWatcher {

        private View view;

        private LoginViewTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etUsername:
                    // validateUserId();
                    break;
                case R.id.etPass:
                    // validatePassword();
                    break;
            }
        }
    }

    public void testProductCatalog() {

        if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {

        } else {
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0007);
            // soundUtils.alertSuccess(getActivity(),getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, LoginActivity.this);
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setSearchString(null);
        itemListDTO.setFilter("0");
        itemListDTO.setPageIndex(pageIndex);
        itemListDTO.setPageSize(500);
        itemListDTO.setHandheldRequest(true);
        message.setEntityObject(itemListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");
        call = apiService.ProductCatalog(message);

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
                                common.showAlertType(omsExceptionMessage, LoginActivity.this, LoginActivity.this);

                            }

                            dialog.dismiss();

                        } else {

                            try {

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                itemTables = new ArrayList<>();
                                ItemListDTO itemList;

                                try {

                                    itemList = new ItemListDTO(_lstItem.entrySet());
                                    lstItem = itemList.getResults();


                                    if (lstItem != null && lstItem.size() > 0) {


                                        new AsyncTask<Void, Integer, String>() {

                                            @Override
                                            protected String doInBackground(Void... voids) {
                                                synchronized (this) {

                                                    db.itemDAO().deleteAll();
                                                    db.variantDAO().deleteAll();

                                                    for (ModelDTO md : lstItem) {

                                                        db.itemDAO().insert(new ItemTable(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                                md.getModelDescription(), md.getImgPath(), md.getDiscountCount(), md.getDiscountId(), md.getDiscountDesc()));

                                                        for (VariantDTO variantDTO : md.getVarientList()) {

                                                            db.variantDAO().insert(new VariantTable(md.getModelID(), md.getDivisionID(),
                                                                    variantDTO.getMaterialID(), variantDTO.getMDescription(), variantDTO.getMDescriptionLong(),
                                                                    variantDTO.getMcode(), variantDTO.getModelColor(), variantDTO.getMaterialImgPath(),
                                                                    variantDTO.getDiscountCount(), variantDTO.getDiscountId(), variantDTO.getDiscountDesc(),
                                                                    variantDTO.getProductSpecification(), variantDTO.getProductCatalog(), variantDTO.getEBrochure(), variantDTO.getOpenPrice(), (int) Double.parseDouble(variantDTO.getStackSize())));

                                                        }

                                                    }

                                                    dialog.dismiss();

                                                    sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, true);
                                                    sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_LOADED, true);

                                                    pageIndex++;
                                                    testProductCatalog();


                                                }
                                                return null;
                                            }
                                        }.execute();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {

                                        sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, true);
                                        sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_LOADED, true);

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                    ProgressDialogUtils.closeProgressDialog();

                                } catch (Exception e) {
                                    dialog.dismiss();
                                    common.showUserDefinedAlertType(errorMessages.EMC_0018, LoginActivity.this, LoginActivity.this, "Warning");
                                }

                            } catch (Exception ex) {
                                dialog.dismiss();
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
                    if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {
                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                    dialog.dismiss();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", LoginActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0003);
        }
    }

/*
    private void versioncontrol() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Do something for lollipop and above versions
        } else {
            Toast.makeText(LoginActivity.this, "You are running on lower versions of Android version, Some features may not work on your device", Toast.LENGTH_LONG).show();
            //finish();
        }
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        //android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 25);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText(getString(R.string.launch_string));
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(18);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

}