package com.example.inventrax.falconOMS.activities;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.application.AbstractApplication;
import com.example.inventrax.falconOMS.application.AppController;
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
import com.example.inventrax.falconOMS.pojos.ItemListResponse;
import com.example.inventrax.falconOMS.pojos.LoginDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreAuthentication;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.ItemTable;
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
import java.util.ArrayList;
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
    private List<ItemListResponse> lstItem;
    private List<CustomerListDTO> customerList;
    RestService restService;

    private String encryptedPass = "";
    private Resources resources;
    ServiceURL serviceURL;
    RelativeLayout  main_relative, login_tool_relative;
    List<CustomerTable> customerTables;
    List<ItemTable> itemTables;

    ImageView ivNilkamal;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        loadFormControls();
        Log.d("ABCDE", "Refreshed token:" + FirebaseInstanceId.getInstance().getToken());

        FirebaseMessaging.getInstance().subscribeToTopic("all");



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

            languagespinner = (Spinner) findViewById(R.id.languagespinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.language_array, android.R.layout.simple_spinner_dropdown_item);
            languagespinner.setAdapter(adapter);

            languagespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 1:
                            LocaleHelper.setLocale(AppController.getInstance().getApplicationContext(), "en");
                            Selectedlanguage.setLanguage(2);
                            break;
                        case 2:
                            LocaleHelper.setLocale(AppController.getInstance().getApplicationContext(), "te");
                            DialogUtils.showAlertDialog(LoginActivity.this, resources.getString(R.string.msg));
                            Selectedlanguage.setLanguage(1);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            });

            db = Room.databaseBuilder(LoginActivity.this,
                    AppDatabase.class, KeyValues.ROOM_DATA_BASE_NAME).allowMainThreadQueries().build();

            lstItem = new ArrayList<ItemListResponse>();
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

            btnLogin.setOnClickListener(this);


            tvForgotPwd.setOnClickListener(this);


            progressDialogUtils = new ProgressDialogUtils(this);

            sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getApplicationContext());

            if (sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_REMEMBER_PASSWORD_CHECKED, false)) {
                inputUserId.setText(sharedPreferencesUtils.loadPreference(KeyValues.USER_NAME, ""));
                inputPassword.setText(sharedPreferencesUtils.loadPreference(KeyValues.PASSWORD, ""));
                chkRememberPassword.setChecked(true);
            } else {
                inputUserId.setText(sharedPreferencesUtils.loadPreference(KeyValues.USER_NAME, ""));
                inputPassword.setText(sharedPreferencesUtils.loadPreference(KeyValues.PASSWORD, ""));
                sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_REMEMBER_PASSWORD_CHECKED, true);
            }

            AbstractApplication.CONTEXT = getApplicationContext();

        } catch (Exception ex) {

            Log.v("ABCD", "" + ex);

            DialogUtils.showAlertDialog(this, "Error while initializing controls");
            return;

        }

        setKeyboardListener();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvForgotPwd:
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivty.class));
                break;

            case R.id.btnLogin:

                if (!inputUserId.getText().toString().isEmpty() && !inputPassword.getText().toString().isEmpty()) {

                    String pass = inputPassword.getText().toString();

                    try {
                        // Encrypting password with the key
                        encryptedPass = Encryption.encryptData(pass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    validateUserSession();

                    /*llLogin.setVisibility(View.GONE);
                    llForgotPwd.setVisibility(View.GONE);
                    llOTPCapture.setVisibility(View.GONE);*/

                        /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
*/


                    //If User Clicks on remember me username,Password is stored in Shared preferences
                    if (chkRememberPassword.isChecked()) {
                        sharedPreferencesUtils.savePreference(KeyValues.USER_NAME, inputUserId.getText().toString().trim());
                        sharedPreferencesUtils.savePreference(KeyValues.PASSWORD, inputPassword.getText().toString().trim());
                        sharedPreferencesUtils.savePreference(KeyValues.IS_REMEMBER_PASSWORD_CHECKED, true);
                    }
                } else {

                }

                break;


        }

    }

    public void startTime() {

        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                // shows time and changes for every second
                timer.setText("00:" + String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                // Called after timer finishes
                timer.setText("00:00");
            }
        }.start();
    }

    public void getItemList() {

        if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {
        } else {
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ItemMaster_FPS_DTO, LoginActivity.this);
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setPageIndex(0);
        itemListDTO.setPageSize(0);
        message.setEntityObject(itemListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.GetItemList(message);
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
                                    common.showAlertType(omsExceptionMessage, LoginActivity.this, getApplicationContext());
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                itemTables =new ArrayList<>();
                                ItemListDTO itemList;

                                try {

                                    itemList = new ItemListDTO(_lstItem.entrySet());
                                    lstItem = itemList.getResults();



                                    executeItemAsyncTask();


                                } catch (Exception e) {
                                    common.showUserDefinedAlertType("No items found", LoginActivity.this, getApplicationContext(), "Warning");
                                    // logException();
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
                    Toast.makeText(LoginActivity.this, throwable.toString(), Toast.LENGTH_LONG).show();
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

    public void getCustomerList() {

        if (NetworkUtils.isInternetAvailable(LoginActivity.this)) {
        } else {
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Customer_FPS_DTO, LoginActivity.this);
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setPageIndex(1);
        itemListDTO.setPageSize(20);
        message.setEntityObject(itemListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.GetCustomerList(message);
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
                                    common.showAlertType(omsExceptionMessage, LoginActivity.this, LoginActivity.this);
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<String, String> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();


                                CustomerListDTO itemList;
                                final List<CustomerListDTO> lstDto = new ArrayList<CustomerListDTO>();


                                itemList = new CustomerListDTO(_lstItem.entrySet());

                                customerTables =new ArrayList<>();

                                for (CustomerListDTO dd : itemList.getResults()) {

                                    lstDto.add(dd);

                                    customerTables.add(new CustomerTable(dd.getCustomerID(),dd.getCustomerName(),dd.getCustomerCode(),
                                            dd.getCustomerType(),dd.getDivision(),dd.getConnectedDepot(),dd.getMobile(),
                                            dd.getPrimaryID(),dd.getSalesDistrict(),dd.getZone()));

                                }

                                customerList = lstDto;

                                executeCustomerAsyncTask();

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
                    Toast.makeText(LoginActivity.this, throwable.toString(), Toast.LENGTH_LONG).show();
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

    public void saveItemListTolocalDB(List<ItemListResponse> itemList) {

        if (itemList != null && itemList.size() > 0) {

            for (ItemListResponse dd : itemList) {

                itemTables.add(new ItemTable(dd.getMaterialID(),dd.getMaterialCode(),dd.getMaterialDescription(),
                        dd.getMaterialPath(),dd.getMaterialType()));
            }

            db.itemDAO().delete();

            db.itemDAO().insertAll(itemTables);

        }

    }

    public void saveCustomerListTolocalDB(List<CustomerListDTO> custList) {

        if (custList != null && custList.size() > 0) {

            db.customerDAO().delete();
            db.customerDAO().insertAll(customerTables);

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
        loginDTO.setUserName("Suresh");
        message.setEntityObject(loginDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.LoginUser(message);
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
                                List<OMSExceptionMessage> _lExceptions = new ArrayList<OMSExceptionMessage>();
                                _lExceptions = core.getOMSMessages();

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, LoginActivity.this, getApplicationContext());
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<String, String> _lstLogindetails = new LinkedTreeMap<String, String>();
                                _lstLogindetails = (LinkedTreeMap<String, String>) core.getEntityObject();
                                if (_lstLogindetails != null) {
                                   /* for (Map.Entry<String, String> entry : _lstLogindetails.entrySet()) {
                                        if (entry.getKey().equals("UserId")) {

                                            sharedPreferencesUtils.savePreference("RefUserId", entry.getValue().toString());
                                        }
                                        if (entry.getKey().equals("UserName")) {

                                            sharedPreferencesUtils.savePreference("UserName", entry.getValue().toString());
                                        }
                                        if (entry.getKey().equals("UserRole")) {

                                            sharedPreferencesUtils.savePreference("RefUserRollId", entry.getValue().toString());
                                        }

                                    }*/
                                    sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, false);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);

                                    getItemList();

                                    getCustomerList();

                                }
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    Toast.makeText(LoginActivity.this, throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getApplicationContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(LoginActivity.this, errorMessages.EMC_0003);
        }
    }



    public void executeItemAsyncTask()
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                saveItemListTolocalDB(lstItem);
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
                sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_LOADED, true);
                Log.v("opi","onPost");
            }
        };

        if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    public void executeCustomerAsyncTask()
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {

                String msg = "";

                // stores the data into local data base
               saveCustomerListTolocalDB(customerList);

                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {

            }
        };

        if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
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
            switch (view.getId()){
                case R.id.etUsername:
                    //validateUserId();
                    break;
                case R.id.etPass:
                    //validatePassword();
                    break;
            }
        }
    }

    private void versioncontrol() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Do something for lollipop and above versions
        } else {
            //Toast.makeText(getApplicationContext(), "You are running on lower versions of Android version, Some features may not work on your device", Toast.LENGTH_LONG).show();
            //finish();

            Toast.makeText(getApplicationContext(), "You are running on lower versions of Android version, Some features may not work on your device", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        /*try {

            EnterpriseDeviceManager enterpriseDeviceManager = (EnterpriseDeviceManager)getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);

            RestrictionPolicy restrictionPolicy = enterpriseDeviceManager.getRestrictionPolicy();

            restrictionPolicy.allowSettingsChanges(false);

        }catch (Exception ex){

        }*/

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
}
