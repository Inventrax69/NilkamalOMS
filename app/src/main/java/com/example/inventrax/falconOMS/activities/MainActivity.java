package com.example.inventrax.falconOMS.activities;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.fragments.CartFragment;
import com.example.inventrax.falconOMS.fragments.HomeFragmentHints;
import com.example.inventrax.falconOMS.fragments.ProfileFragment;
import com.example.inventrax.falconOMS.fragments.SearchFragment;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.logout.LogoutUtil;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDTO;
import com.example.inventrax.falconOMS.pojos.CartResponseListDTO;
import com.example.inventrax.falconOMS.pojos.CustomerListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListResponse;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.AndroidUtils;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.inventrax.falconOMS.util.DateUtils.DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH;


public class MainActivity extends AppCompatActivity {

    private static final String classCode = "OMS_Android_MainActivity";

    private TextView mTextMessage, txtTimer;
    Toolbar toolbar;
    BottomNavigationView navigation;
    private CoordinatorLayout coordinatorLayout;
    private LogoutUtil logoutUtil;


    SharedPreferencesUtils sharedPreferencesUtils;

    ErrorMessages errorMessages;
    Common common;
    private OMSCoreMessage core;
    RestService restService;


    private List<ItemListResponse> lstItem;
    private List<CustomerListDTO> customerList;
    List<ItemTable> itemTables;
    List<CustomerTable> customerTables;
    List<CartDetails> cartDetails;
    List<CartDTO> cartList = null;

    AppDatabase db;
    String userName = "", userId = "",cartHeaderId = "";
    private String itemTimeStamp = "", customerTimeStamp = "";


    ActionBar actionBar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    loadFragment(new HomeFragmentHints());
                    return true;

                case R.id.navigation_bag:

                    return true;

                case R.id.navigation_cart:
                    loadFragment(new CartFragment());
                    return true;

                case R.id.navigation_search:
                    loadFragment(new SearchFragment());
                    return true;

                case R.id.navigation_more:
                    loadFragment(new ProfileFragment());
                    return true;

            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snack_bar_action_layout);

        setSupportActionBar(toolbar);

        // gets the tool bar icon and handles click event
        toolbar.setLogo(ContextCompat.getDrawable(MainActivity.this, R.drawable.nilkamal));
        View logoView = AndroidUtils.getToolbarLogoIcon(toolbar);
        if (logoView != null)
            logoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(new ProfileFragment());
                }
            });

        // gets the action bar support to enable features
        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // load the store fragment by default
        loadFragment(new HomeFragmentHints());


        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);       // removes bottom navigation bar animation
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        logoutUtil = new LogoutUtil();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.i("ddere", "Extra:" + extras.getString("whattodo"));
        }

        db = Room.databaseBuilder(MainActivity.this,
                AppDatabase.class, "room_oms").allowMainThreadQueries().build();

        SharedPreferences sp = this.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userName = sp.getString(KeyValues.USER_NAME, "");
        userId = sp.getString(KeyValues.USER_ID, "");
        cartHeaderId = sp.getString(KeyValues.CART_HEADERID, "");

        errorMessages = new ErrorMessages();
        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        lstItem = new ArrayList<ItemListResponse>();
        customerList = new ArrayList<CustomerListDTO>();
        cartList = new ArrayList<>();

        txtTimer = (TextView) findViewById(R.id.txtTimer);

         /* if(sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_ITEM_LOADED)){

            String itemLastTime = new SimpleDateFormat(DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH).format(new Date(db.itemDAO().getLastRecord().timestamp));
            String custLastTime = new SimpleDateFormat(DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH).format(new Date(db.customerDAO().getLastRecord().timestamp));
            itemTimeStamp = itemLastTime;
        }*/


        //itemTimeStamp = "2019-08-27 19:08:18.630";

        customerTimeStamp = "2019-08-27 19:08:18.630";


        if (NetworkUtils.isInternetAvailable(MainActivity.this)) {

            //syncItemData();

            // syncCustomerData();

            //cartSyncAsync();

        }

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startTime() {

        txtTimer.setText("");
        CountDownTimer countDownTimer ;
        countDownTimer = new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {

                // shows time and changes for every second
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                txtTimer.setVisibility(View.VISIBLE);
                txtTimer.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                // Called after timer finishes
                txtTimer.setText("00:00");
                txtTimer.setVisibility(View.GONE);
                //deleteCartItemReservation();
            }
        }.start();

        //countDownTimer.cancel();

    }

    // to show or hide bottom navigation bar from different fragments
    public void SetNavigationVisibiltity(boolean b) {
        if (b) {
            navigation.setVisibility(View.VISIBLE);
        } else {
            navigation.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout: {

                logoutUtil.doLogout(MainActivity.this);
            }
            break;

            /*case R.id.action_about: {

                //FragmentUtils.replaceFragmentWithBackStack(this, R.id.container_body, new AboutFragment());
            }
            break;*/
            case R.id.action_profile: {

                FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new ProfileFragment());
            }
            break;

            case R.id.action_notification: {

                Toast.makeText(MainActivity.this, "Notifications pressed", Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.action_cart: {

                FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new CartFragment());
            }
            break;

            case android.R.id.home: {

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                onBackPressed();

            }
            break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void syncItemData() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ItemMaster_FPS_DTO, MainActivity.this);
        ItemListResponse itemListDTO = new ItemListResponse();
        if (!itemTimeStamp.equals("")) {
            itemListDTO.setCreatedOn(itemTimeStamp);
        } else {
            return;
        }
        message.setEntityObject(itemListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.SyncItemData(message);


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
                                    common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                itemTables = new ArrayList<>();
                                ItemListDTO itemList;

                            }

                        }

                    }

                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", MainActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0003);
        }
    }

    public void syncCustomerData() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Customer_FPS_DTO, MainActivity.this);
        CustomerListDTO customerListDTO = new CustomerListDTO();
        if (!customerTimeStamp.equals("")) {
            customerListDTO.setCreatedOn(customerTimeStamp);
        } else {
            return;
        }
        message.setEntityObject(customerListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.SyncCustomerData(message);


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
                                    common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<String, String> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();


                                CustomerListDTO itemList;

                                itemList = new CustomerListDTO(_lstItem.entrySet());

                                customerTables = new ArrayList<>();

                                for (CustomerListDTO dd : itemList.getResults()) {

                                    SimpleDateFormat sdf = new SimpleDateFormat(DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH);
                                    Date date = null;
                                    try {
                                        date = sdf.parse(dd.getCreatedOn());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    long startDate = date.getTime();

                                    customerTables.add(new CustomerTable(dd.getCustomerID(), dd.getCustomerName(), dd.getCustomerCode(),
                                            dd.getCustomerType(), dd.getDivision(), dd.getConnectedDepot(), dd.getMobile(),
                                            dd.getPrimaryID(), dd.getSalesDistrict(), dd.getZone(), startDate));

                                    if (dd.getAction().equalsIgnoreCase("A")) {

                                        Toast.makeText(MainActivity.this, "A", Toast.LENGTH_SHORT).show();

                                    } else if (dd.getAction().equalsIgnoreCase("M")) {
                                        Toast.makeText(MainActivity.this, "M", Toast.LENGTH_SHORT).show();
                                    } else if (dd.getAction().equalsIgnoreCase("D")) {
                                        Toast.makeText(MainActivity.this, "D", Toast.LENGTH_SHORT).show();
                                    }

                                }


                            }

                        }

                    }

                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", MainActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0003);
        }
    }

    public void syncCart() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, MainActivity.this);
        CartDTO cartDTO = new CartDTO();
        cartDTO.setHandheldRequest(true);
        cartDTO.setCartHeaderID(0);
        cartDTO.setUserID(userId);
        cartDTO.setCustomerID("0");
        cartDTO.setSearchString(null);
        cartDTO.setPageIndex(1);
        cartDTO.setPageSize(5);
        message.setEntityObject(cartDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.cartlist(message);


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
                                    common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();


                                CartResponseListDTO cart = new CartResponseListDTO();
                                // get JSONObject from JSON file
                                JSONObject obj = null;
                                JSONArray cartListResp = null;

                                try {
                                    obj = new JSONObject((String) core.getEntityObject());


                                    JSONArray getCartHeader = new JSONArray(obj.getString("Table"));

                                    cartListResp = new JSONArray(obj.getString("Table1"));

                                    if (!getCartHeader.get(0).toString().contains("null")) {
                                        TypeToken<CartResponseListDTO> header = new TypeToken<CartResponseListDTO>() {
                                        };

                                        CartResponseListDTO headerInsert = new CartResponseListDTO();

                                        headerInsert = new Gson().fromJson(getCartHeader.get(0).toString(), header.getType());
                                        db.cartHeaderDAO().deleteAll();
                                        db.cartHeaderDAO().insert(new CartHeader(userId,Integer.parseInt(headerInsert.getCartHeaderID()),
                                                headerInsert.getInActive(),headerInsert.getCreditLimit(),headerInsert.getIsApproved()));

                                        TypeToken<CartResponseListDTO> token = new TypeToken<CartResponseListDTO>() {
                                        };

                                        for (int i = 0; i < cartListResp.length(); i++) {
                                            cart = new Gson().fromJson(cartListResp.get(0).toString(), token.getType());
                                            db.cartDetailsDAO().insert(new CartDetails(cart.getMaterialMasterID(), cart.getMCode(), cart.getMDescription(),
                                                    cart.getExpectedDeliveryDate(), cart.getActualDeliveryDate(),
                                                    cart.getQuantity(), cart.getFileNames(), cart.getPrice(),
                                                    cart.getCartHeaderID(),cart.getCartDetailsID(),cart.getCustomerID()));
                                        }

                                        addToCart();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        }

                    }

                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", MainActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0003);
        }
    }

    public void cartSyncAsync() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {

                String msg = "";

                syncCart();

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        };

        if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    public void addToCart() {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, MainActivity.this);
        CartDTO oDto = new CartDTO();
        oDto.setUserID(userId);

        if(db.cartDetailsDAO().getAllCartItems() != null){
            List<CartDetails> cartDetailsList = new ArrayList<>();
            cartDetailsList = db.cartDetailsDAO().getAllCartItems();

            CartDTO cDto = new CartDTO();

            for(int i = 0; i< cartDetailsList.size(); i++){


                cDto.setMaterialMasterID(cartDetailsList.get(i).materialID);
                cDto.setMCode(cartDetailsList.get(i).mCode);
                cDto.setQuantity(cartDetailsList.get(i).quantity);
                cDto.setCustomerID(cartDetailsList.get(i).customerId);
                cDto.setImagePath(cartDetailsList.get(i).imgPath);
                cDto.setPrice(cartDetailsList.get(i).price);

                cartList.add(cDto);
            }
        }

        oDto.setCartHHT(cartList);
        message.setEntityObject(oDto);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.HHTCartDetails(message);


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
                                    common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);
                                }
                                ProgressDialogUtils.closeProgressDialog();

                            } else {

                                syncCart();

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
                    Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", MainActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0003);
        }
    }


    public void deleteCartItemReservation() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, MainActivity.this);
        CartDTO oDto = new CartDTO();

        oDto.setCartHeaderID(Integer.parseInt(cartHeaderId));

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.DeleteCartItemReservation(message);
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
                                    common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);
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
                    Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", MainActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0003);
        }
    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        /*if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }*/

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
    }


}

class BottomNavigationViewHelper {

    @SuppressLint("RestrictedApi")
    static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }
}



