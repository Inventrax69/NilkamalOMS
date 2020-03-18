package com.example.inventrax.falconOMS.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.fragments.ApprovalsDetailsFragment;
import com.example.inventrax.falconOMS.fragments.ApprovalsListFragment;
import com.example.inventrax.falconOMS.fragments.HomeFragmentHints;
import com.example.inventrax.falconOMS.fragments.NotificationFragment;
import com.example.inventrax.falconOMS.fragments.ProductCatalogFragment;
import com.example.inventrax.falconOMS.fragments.ProfileFragment;
import com.example.inventrax.falconOMS.fragments.SADListFragment;
import com.example.inventrax.falconOMS.fragments.SearchFragment;
import com.example.inventrax.falconOMS.fragments.SettingsFragment;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.logout.LogoutUtil;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.network.NetworkSchedulerService;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.CustomerListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListResponse;
import com.example.inventrax.falconOMS.pojos.ModelDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.SyncDataDTO;
import com.example.inventrax.falconOMS.pojos.VariantDTO;
import com.example.inventrax.falconOMS.pojos.productCatalogs;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.room.VariantTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.design.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED;


public class MainActivity extends AppCompatActivity {

    private static final String classCode = "OMS_Android_MainActivity";
    private TextView mTextMessage, txtTimer;
    Toolbar toolbar;
    ActionBar actionBar;
    BottomNavigationView navigation;
    private CoordinatorLayout coordinatorLayout;
    private LogoutUtil logoutUtil;
    SharedPreferencesUtils sharedPreferencesUtils;
    ErrorMessages errorMessages;
    Common common;
    private OMSCoreMessage core;
    private ProgressDialogUtils progressDialogUtils;
    RestService restService;
    private List<ItemListResponse> lstItem;
    private List<CustomerListDTO> customerList;
    List<ItemTable> itemTables;
    List<CustomerTable> customerTables;
    List<CartDetails> cartDetails;
    List<productCatalogs> cartList = null;
    AppDatabase db;
    String userName = "", userId = "", cartHeaderId = "";
    private String  timeStamp = "";
    private static CountDownTimer countDownTimer;
    int c_width, c_height;
    boolean doubleBackToExitPressedOnce = false;
    public boolean isProfileOpened = false, isSearchOpened = false, isSettingOpened = false;
    String itemTimeStamp="", customerTimeStamp ="";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    FragmentManager fm = getSupportFragmentManager();
                    for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                        fm.popBackStack();
                    }
                    loadFragment(new HomeFragmentHints());
                    return true;

                /*case R.id.navigation_schemes:
                    loadFragment(new CustomerListFragment());
                    return true;*/

                case R.id.navigation_cart:
                    if (db.userDivisionCustDAO().getUserDivisionCustCount() > 0) {
                        Intent i = new Intent(MainActivity.this, CartActivity.class);
                        startActivity(i);
                    } else {
                        SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) MainActivity.this).findViewById(R.id.snack_bar_action_layout), "No cart access for this user role", ContextCompat.getColor(MainActivity.this, R.color.dark_red), Snackbar.LENGTH_SHORT);
                    }
                    return true;

                case R.id.navigation_search:
                    if (!isSearchOpened) {
                        loadFragment(new SearchFragment());
                        isProfileOpened = false;
                        isSearchOpened = true;
                        isSettingOpened = false;
                    }
                    return true;

                case R.id.navigation_more:
                    if (!isProfileOpened) {
                        loadFragment(new ProfileFragment());
                        isProfileOpened = true;
                        isSearchOpened = false;
                        isSettingOpened = false;
                    }
                    return true;

            }
            return false;
        }
    };

/*
    public void getCurrentLocation() {
        if (mlocManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
            if (mlocManager != null) {
               Location location = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                }
            }
            // mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener );
        }

    }*/


    Animation anim;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


/*        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

        //for Android 5-7
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);

        // for Android 8 and above
        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

        startActivity(intent);*/

/*        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        getCurrentLocation();*/

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snack_bar_action_layout);

        setSupportActionBar(toolbar);

        // scheduleJob();

        // gets the tool bar icon and handles click event
        toolbar.setLogo(ContextCompat.getDrawable(MainActivity.this, R.drawable.nilkamal));
        /*View logoView = AndroidUtils.getToolbarLogoIcon(toolbar);
        if (logoView != null)
            logoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(new ProfileFragment());
                }
            });*/

        // gets the action bar support to enable features
        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // load the store fragment by default
        loadFragment(new HomeFragmentHints());

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);       // removes bottom navigation bar animation
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        logoutUtil = new LogoutUtil();

        db = new RoomAppDatabase(MainActivity.this).getAppDatabase();

        SharedPreferences sp = this.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userName = sp.getString(KeyValues.USER_NAME, "");
        userId = sp.getString(KeyValues.USER_ID, "");
        cartHeaderId = sp.getString(KeyValues.CART_HEADERID, "");

        errorMessages = new ErrorMessages();
        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        progressDialogUtils = new ProgressDialogUtils(MainActivity.this);
        progressDialogUtils = new ProgressDialogUtils(this);

        lstItem = new ArrayList<ItemListResponse>();
        customerList = new ArrayList<CustomerListDTO>();
        cartList = new ArrayList<>();

        txtTimer = (TextView) findViewById(R.id.txtTimer);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        txtTimer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                txtTimer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                c_width = width - txtTimer.getWidth();
                c_height = height - txtTimer.getHeight();
            }
        });


        txtTimer.setOnTouchListener(new View.OnTouchListener() {

            private float xCoOrdinate, yCoOrdinate;

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // drag(event, v);
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        xCoOrdinate = v.getX() - event.getRawX();
                        yCoOrdinate = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (((event.getRawX() + xCoOrdinate) > 0.5 && (event.getRawX() + xCoOrdinate) < c_width))
                            if ((event.getRawY() + yCoOrdinate) > 0.5 && (event.getRawY() + yCoOrdinate) < c_height)
                                v.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                        break;
                    default:
                        return false;
                }
                return true;
            }

        });

        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getApplicationContext());
        //sharedPreferencesUtils.savePreference(KeyValues.USER_ROLE_NAME, "DTD");
        //sharedPreferencesUtils.savePreference("timer", mills);
/*        try {
            Calendar calendar = Calendar.getInstance();
            long mills = calendar.getTimeInMillis();
            long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
            startTime(300000 - (mills - timer));
        } catch (Exception e) {
            txtTimer.setText("00:00");
            txtTimer.setVisibility(View.GONE);
        }*/

        txtTimer.setVisibility(View.GONE);

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // if you want to change color at start of animation
                //textview.settextcolor("your color")
                txtTimer.setTextColor(Color.RED);
                if (txtTimer.getText().toString().equals("00:00")) {
                    txtTimer.setVisibility(View.GONE);
                    txtTimer.clearAnimation();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // if you want to change color at end of animation
                //textview.settextcolor("your color")
                txtTimer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (txtTimer.getText().toString().equals("00:00")) {
                    txtTimer.setVisibility(View.GONE);
                    txtTimer.clearAnimation();
                }
            }
        });



         /*
         if(sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_ITEM_LOADED)){
            String itemLastTime = new SimpleDateFormat(DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH).format(new Date(db.itemDAO().getLastRecord().timestamp));
            String custLastTime = new SimpleDateFormat(DDMMMYYYYHHMMSS_DATE_FORMAT_SLASH).format(new Date(db.customerDAO().getLastRecord().timestamp));
            itemTimeStamp = itemLastTime;
        }
        */


        // itemTimeStamp = "2019-08-27 19:08:18.630";

        timeStamp = "2019-08-27 19:08:18.630";

        Date date = Calendar.getInstance().getTime();
        //
        // Display a date in day, month, year format
        //
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String todaysdate = formatter.format(date);

        Log.v("ABCDE", "" + todaysdate);

        if (NetworkUtils.isInternetAvailable(MainActivity.this)) {
            // syncItemData();
            // syncCustomerData();
            cartSyncAsync();
            itemTimeStamp = sp.getString(KeyValues.ITEM_MASTER_SYNC_TIME, "");
            customerTimeStamp = sp.getString(KeyValues.CUSTOMER_MASTER_SYNC_TIME, "");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                Date date1 = simpleDateFormat.parse(itemTimeStamp);
                Date date2 = simpleDateFormat.parse(customerTimeStamp);

                long diffInMs = date1.getTime() - date2.getTime();

                long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

                if(diffInSec >= 0){
                    timeStamp= customerTimeStamp;
                }else{
                    timeStamp= itemTimeStamp;
                }

                //SyncData();

            } catch (ParseException e) {
               // e.printStackTrace();
            }
            //
        }


        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {

                if (getIntent().getExtras().get("NOTIFICATION1").equals("msg")) {

                    SharedPreferences prefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(KeyValues.IS_NOTIFICATION_AVAILABLE, false);
                    editor.commit();

                    Bundle bundle = new Bundle();
                    bundle.putString("ID", "" + getIntent().getExtras().get("NOTIFY_ID"));
                    String Type = "" + getIntent().getExtras().get("Type");
                    String Link = "" + getIntent().getExtras().get("Link");
                    if (Type.equals("OpenPrice Approval")) {
                        if (Link.split("[/]").length == 4) {
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("type", Link.split("[/]")[2]);
                            bundle1.putString("cartHeaderId", Link.split("[/]")[3]);
                            FragmentUtils.replaceFragmentWithBackStackWithBundle(MainActivity.this, R.id.container, new ApprovalsDetailsFragment(), bundle1);
                        } else {
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("type", "4");
                            FragmentUtils.replaceFragmentWithBackStackWithBundle(MainActivity.this, R.id.container, new ApprovalsListFragment(), bundle1);
                        }
                    } else if (Type.equals("Credit Limit Approval")) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("type", "5");
                        FragmentUtils.replaceFragmentWithBackStackWithBundle(MainActivity.this, R.id.container, new ApprovalsListFragment(), bundle2);
                    } else if (Type.equals("InActive Approval")) {
                        if (Link.split("[/]").length == 4) {
                            Bundle bundle3 = new Bundle();
                            bundle3.putString("type", Link.split("[/]")[2]);
                            bundle3.putString("cartHeaderId", Link.split("[/]")[3]);
                            FragmentUtils.replaceFragmentWithBackStackWithBundle(MainActivity.this, R.id.container, new ApprovalsDetailsFragment(), bundle3);
                        } else {
                            Bundle bundle3 = new Bundle();
                            bundle3.putString("type", "6");
                            FragmentUtils.replaceFragmentWithBackStackWithBundle(MainActivity.this, R.id.container, new ApprovalsListFragment(), bundle3);
                        }
                    } else if (Type.equals("Discount Approval")) {
                        FragmentUtils.replaceFragmentWithBackStack(MainActivity.this, R.id.container, new SADListFragment());
                    } else {
                        FragmentUtils.replaceFragmentWithBackStackWithBundle(MainActivity.this, R.id.container, new NotificationFragment(), bundle);
                    }

                }
            }
        } catch (Exception ex) {
        }


        if (getIntent().getExtras() != null) {
            boolean openCatalog = getIntent().getExtras().getBoolean(KeyValues.OPEN_CATALOG);
            if (openCatalog) {
                loadFragment(new ProductCatalogFragment());
            }
        }

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void startTime(final long mills) {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(mills, 1000) {

            public void onTick(long millisUntilFinished) {
                // shows time and changes for every second
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                txtTimer.setVisibility(View.GONE);
                txtTimer.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                if (minutes == 0)
                    txtTimer.startAnimation(anim);
            }

            public void onFinish() {
                // Called after timer finishes
                txtTimer.setText("00:00");
                txtTimer.setVisibility(View.GONE);
                if (mills > 0) {

                }
                // deleteCartItemReservation();
                // deleteCartItemReservation();
            }
        }.start();

    }

    // to show or hide bottom navigation bar from different fragments
    public void SetNavigationVisibility(boolean b) {
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_logout: {
                logoutUtil.doLogout(MainActivity.this, MainActivity.this);
            }
            break;

            case R.id.action_setting: {
                if (!isSettingOpened) {
                    FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new SettingsFragment());
                    isSettingOpened = true;
                    isProfileOpened = false;
                    isSearchOpened = false;
                }
            }
            break;

            /*
            case R.id.action_about: {
                //FragmentUtils.replaceFragmentWithBackStack(this, R.id.container_body, new AboutFragment());
            }
            break;
            */

            case R.id.action_profile: {
                if (!isProfileOpened) {
                    FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new ProfileFragment());
                    isProfileOpened = true;
                    isSearchOpened = false;
                    isSettingOpened = false;
                }
            }
            break;

            case R.id.action_notification: {
                FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new NotificationFragment());
            }
            break;

            case R.id.action_cart: {
                if (db.userDivisionCustDAO().getUserDivisionCustCount() > 0) {
                    Intent i = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(i);
                } else {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) MainActivity.this).findViewById(R.id.snack_bar_action_layout), errorMessages.EMC_0019, ContextCompat.getColor(MainActivity.this, R.color.dark_red), Snackbar.LENGTH_SHORT);
                }
            }
            break;

            case android.R.id.home: {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                onBackPressed();
            }
            break;

            case R.id.action_home: {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                FragmentManager fm = getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                loadFragment(new HomeFragmentHints());
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void SyncData() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, MainActivity.this);
        productCatalogs oDto = new productCatalogs();
        oDto.setUserID(userId);
        oDto.setCreatedOn(timeStamp);
        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.SyncData(message);

        //ProgressDialogUtils.showProgressDialog("Please Wait");

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
                                common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);

                            }
                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {
                                if (core.getEntityObject() != null || !core.getEntityObject().toString().isEmpty()) {

                                    SyncDataDTO syncDataDTO = new Gson().fromJson(new Gson().toJson(core.getEntityObject()), SyncDataDTO.class);

                                    if (syncDataDTO.getCustomerMasters().size() > 0) {
                                        for (int i = 0; i < syncDataDTO.getCustomerMasters().size(); i++) {
                                            CustomerListDTO dd = syncDataDTO.getCustomerMasters().get(i);
                                            if (syncDataDTO.getCustomerMasters().get(i).getAction().equals("A")) {

                                                CustomerTable customerTable = new CustomerTable(dd.getCustomerID(), dd.getCustomerName(), dd.getCustomerCode(),
                                                        dd.getCustomerType(), dd.getCustomerTypeID(), dd.getDivision(), dd.getDivisionID().split("[.]")[0], dd.getConnectedDepot(), dd.getMobile(),
                                                        dd.getPrimaryID(), dd.getSalesDistrict(), dd.getZone(), dd.getCity());

                                                db.customerDAO().insert(customerTable);

                                            } else if (syncDataDTO.getCustomerMasters().get(i).getAction().equals("M")) {

                                                db.customerDAO().updateByCustomerId(dd.getCustomerID(), dd.getCustomerName(), dd.getCustomerCode(),
                                                        dd.getCustomerType(), dd.getCustomerTypeID(), dd.getDivision(), dd.getDivisionID().split("[.]")[0], dd.getConnectedDepot(), dd.getMobile(),
                                                        dd.getPrimaryID(), dd.getSalesDistrict(), dd.getZone(), dd.getCity(), "");

                                            } else if (syncDataDTO.getCustomerMasters().get(i).getAction().equals("D")) {
                                                db.customerDAO().deleteByCustomerId(dd.getCustomerID());
                                            }
                                        }
                                    }

                                    if (syncDataDTO.getItemMasters().size() > 0) {
                                        for (int i = 0; i < syncDataDTO.getItemMasters().size(); i++) {

                                            ModelDTO md = syncDataDTO.getItemMasters().get(i);

                                            /*  if(syncDataDTO.getItemMasters().get(i).getAction().equals("A") || syncDataDTO.getItemMasters().get(i).getAction().equals("M")){*/

                                            if (db.itemDAO().getCountByModelID(md.getModelID()) == 0) {
                                                db.itemDAO().insert(new ItemTable(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                        md.getModelDescription(), md.getImgPath(), md.getDiscountCount(), md.getDiscountId(), md.getDiscountDesc()));
                                            } else {
                                                db.itemDAO().updateByModelID(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                        md.getModelDescription(), md.getImgPath(), "", md.getDiscountCount(), md.getDiscountId(), md.getDiscountDesc(), "");
                                            }

                                            for (VariantDTO variantDTO : md.getVarientList()) {

                                                if (variantDTO.getAction().equals("D")) {
                                                    db.variantDAO().deleteByMaterialID(variantDTO.getMaterialID());
                                                } else {
                                                    if (db.variantDAO().getCountByMaterialID(variantDTO.getMaterialID()) == 0) {
                                                        db.variantDAO().insert(new VariantTable(md.getModelID(), md.getDivisionID(),
                                                                variantDTO.getMaterialID(), variantDTO.getMDescription(), variantDTO.getMDescriptionLong(),
                                                                variantDTO.getMcode(), variantDTO.getModelColor(), variantDTO.getMaterialImgPath(),
                                                                variantDTO.getDiscountCount(), variantDTO.getDiscountId(), variantDTO.getDiscountDesc(),
                                                                variantDTO.getProductSpecification(), variantDTO.getProductCatalog(), variantDTO.getEBrochure(), variantDTO.getOpenPrice(), (int) Double.parseDouble(variantDTO.getStackSize())));
                                                    } else {
                                                        db.variantDAO().updateByMaterialID(md.getModelID(), md.getDivisionID(),
                                                                variantDTO.getMaterialID(), variantDTO.getMDescription(), variantDTO.getMDescriptionLong(),
                                                                variantDTO.getMcode(), variantDTO.getModelColor(), "",
                                                                variantDTO.getDiscountCount(), variantDTO.getDiscountId(), variantDTO.getDiscountDesc(), variantDTO.getMaterialImgPath(),
                                                                variantDTO.getProductSpecification(), variantDTO.getProductCatalog(), variantDTO.getEBrochure(),
                                                                String.valueOf(variantDTO.getOpenPrice()), String.valueOf((int) Double.parseDouble(variantDTO.getStackSize())), "");
                                                    }
                                                }
                                            }
                                            /*}else if(syncDataDTO.getItemMasters().get(i).getAction().equals("D")){
                                                db.itemDAO().deleteByModelID(md.getModelID());
                                            }*/
                                        }
                                    }
                                }


                            } catch (Exception e) {

                            }
                        }

                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(MainActivity.this)) {
                        DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0014);
                    }
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


    public void syncCart(final String value) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, MainActivity.this);
        productCatalogs productCatalogs = new productCatalogs();
        productCatalogs.setHandheldRequest(true);
        productCatalogs.setUserID(userId);
        message.setEntityObject(productCatalogs);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);
        ProgressDialogUtils.showProgressDialog("Please Wait");

        call = apiService.cartlist(message);

        final String sMessage = message.getEntityObject().toString();

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
                                common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);
                            }


                        } else {

                            ProgressDialogUtils.closeProgressDialog();

                            try {





                                    /*
                                    db.cartHeaderDAO().deleteAllIsUpdated();
                                    db.cartDetailsDAO().deleteAllIsUpdated();
                                    db.cartHeaderDAO().deleteHeadersNotThereInCartDetails();
                                    */

                                if (value.equals("1")) {
                                    if (core.getEntityObject() != null) {

                                        JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());
                                        db.cartHeaderDAO().deleteAll();
                                        db.cartDetailsDAO().deleteAll();

                                        for (int i = 0; i < getCartHeader.length(); i++) {


                                            for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                                CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                                db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                        cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                        cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));

                                                for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {

                                                    CartDetailsListDTO cart = cartHeaderListDTO.getListCartDetailsList().get(k);

                                                    // String discountID,String discountText,String gst,String tax,String subtotal,String HSNCode

                                                    db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                            cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                            cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                            cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0,
                                                            cart.getMaterialPriorityID(), cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                            cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode()));
                                                }

                                            }

                                        }

                                    } else {
                                        db.cartHeaderDAO().deleteAll();
                                        db.cartDetailsDAO().deleteAll();
                                    }

                                    BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
                                    BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(1);

                                    View notificationBadge = LayoutInflater.from(MainActivity.this).inflate(R.layout.notification_badge, menuView, false);
                                    TextView textView = notificationBadge.findViewById(R.id.counter_badge);
                                    textView.setText(String.valueOf(db.cartDetailsDAO().getCartDetailsCountIsApproved()));
                                    itemView.addView(notificationBadge);


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
                                    }

                                    if (core.getEntityObject() != null) {
                                        JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                        for (int i = 0; i < getCartHeader.length(); i++) {


                                            for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                                CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                                for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {

                                                    CartDetailsListDTO cart = cartHeaderListDTO.getListCartDetailsList().get(k);

                                                    db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                            cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                            cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                            cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0,
                                                            cart.getMaterialPriorityID(), cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                            cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode()));

                                                    productCatalogs cDto = new productCatalogs();
                                                    cDto.setMaterialMasterID(cart.getMaterialMasterID());
                                                    cDto.setMCode(cart.getMCode());
                                                    cDto.setQuantity(cart.getQuantity());
                                                    cDto.setCustomerID(String.valueOf(cartHeaderListDTO.getCustomerID()));
                                                    cDto.setImagePath(cart.getFileNames());
                                                    cDto.setPrice(cart.getPrice());
                                                    cDto.setShipToPartyCustomerID(String.valueOf(cartHeaderListDTO.getCustomerID()));
                                                    cDto.setCartDetailsID("0");
                                                    cDto.setMaterialPriorityID(String.valueOf(cart.getMaterialPriorityID()));
                                                    cDto.setDeliveryDate(cart.getExpectedDeliveryDate());
                                                    cDto.setCartHeaderID(Integer.parseInt(cart.getCartHeaderID()));
                                                    cartList.add(cDto);

                                                }
                                            }
                                        }
                                    }

                                    addToCart();
                                }

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
                    if (NetworkUtils.isInternetAvailable(MainActivity.this)) {
                        DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0014);
                    }
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

    public void addToCart() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, MainActivity.this);
        productCatalogs oDto = new productCatalogs();
        oDto.setUserID(userId);
        oDto.setProductCatalogs(cartList);
        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.HHTCartDetails(message);

        //ProgressDialogUtils.showProgressDialog("Please Wait");

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
                                common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);

                            }
                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {


                                List<CartHeader> cartHeadersList = db.cartHeaderDAO().getCartHeadersForSTP();

                                ProgressDialogUtils.closeProgressDialog();

                                try {

                                    if (core.getEntityObject() != null) {

                                        JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());
                                        db.cartDetailsDAO().deleteAll();
                                        db.cartHeaderDAO().deleteAll();

                                        CartHeaderListDTO cartHeaderListDTO;
                                        CartDetailsListDTO cart;

                                        for (int i = 0; i < getCartHeader.length(); i++) {

                                            for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {
                                                cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                                if (cartHeaderListDTO.getListCartDetailsList().size() > 0) {
                                                    db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                            cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                            cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));
                                                    db.cartHeaderDAO().updateIsUpdated(cartHeaderListDTO.getCustomerID(), 0);
                                                }


                                                for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {
                                                    cart = cartHeaderListDTO.getListCartDetailsList().get(k);
                                                    db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                            cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                            cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                            cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                            cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                            cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode()));
                                                }
                                            }

                                        }

                                        for (int i = 0; i < cartHeadersList.size(); i++) {
                                            db.cartHeaderDAO().updateShipToPatryAndIsPriority(cartHeadersList.get(i).customerID, cartHeadersList.get(i).shipToPartyId, cartHeadersList.get(i).isPriority);
                                        }


                                    } else {
                                        db.cartDetailsDAO().deleteAll();
                                        db.cartHeaderDAO().deleteAll();
                                    }

                                } catch (JSONException e) {

                                }


                                // SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Item added to cart", ContextCompat.getColor(MainActivity.this, R.color.dark_green), Snackbar.LENGTH_SHORT);

                                BottomNavigationMenuView menuView = (BottomNavigationMenuView) ((BottomNavigationView) MainActivity.this.findViewById(R.id.navigation)).getChildAt(0);
                                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(1);

                                View notificationBadge = LayoutInflater.from(MainActivity.this).inflate(R.layout.notification_badge, menuView, false);
                                TextView textView = notificationBadge.findViewById(R.id.counter_badge);
                                textView.setText(String.valueOf(db.cartDetailsDAO().getCartDetailsCountIsApproved()));
                                itemView.addView(notificationBadge);


                            } catch (Exception e) {

                            }
                        }

                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(MainActivity.this)) {
                        DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0014);
                    }
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

    @SuppressLint("ObsoleteSdkInt")
    public void cartSyncAsync() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                if (db.cartHeaderDetailsDao().getUpdateCount()) {
                    syncCart("1");
                } else {
                    syncCart("2");
                }

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

    public void deleteCartItemReservation() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, MainActivity.this);
        productCatalogs oDto = new productCatalogs();
        oDto.setUserID(userId);

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.DeleteReservation(message);
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
                                common.showAlertType(omsExceptionMessage, MainActivity.this, MainActivity.this);
                            }

                        } else {

                            try {

                                String respObject = core.getEntityObject().toString();
                                txtTimer.setText("00:00");
                                txtTimer.setVisibility(View.GONE);
                                if (respObject.equals("Reservation Deleted Successfully")) {
                                    db.cartDetailsDAO().resetDeliveryDates();
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                //Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(MainActivity.this)) {
                        DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(MainActivity.this, errorMessages.EMC_0014);
                    }
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

/*        try {
            if (txtTimer.getText().toString().equals("00:00")) {
                txtTimer.setText("00:00");
                txtTimer.setVisibility(View.GONE);
            } else {
                Calendar calendar = Calendar.getInstance();
                long mills = calendar.getTimeInMillis();
                long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
                startTime(300000 - (mills - timer));
            }
        } catch (Exception e) {
            //
        }*/

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(1);

        View notificationBadge = LayoutInflater.from(MainActivity.this).inflate(R.layout.notification_badge, menuView, false);
        TextView textView = notificationBadge.findViewById(R.id.counter_badge);
        textView.setText(String.valueOf(db.cartDetailsDAO().getCartDetailsCountIsApproved()));
        itemView.addView(notificationBadge);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {

            if (doubleBackToExitPressedOnce) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            final Toast toast = Toast.makeText(this, "Please click Back again to exit", Toast.LENGTH_SHORT);
            toast.show();
/*            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 1000);*/

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 3000);

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
                item.setLabelVisibilityMode(LABEL_VISIBILITY_LABELED);
                // item.setShiftingMode(false);
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



