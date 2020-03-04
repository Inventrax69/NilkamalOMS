package com.example.inventrax.falconOMS.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.fragments.CartApprovalFragment;
import com.example.inventrax.falconOMS.fragments.CartFragment;
import com.example.inventrax.falconOMS.fragments.ProfileFragment;
import com.example.inventrax.falconOMS.fragments.SCMCartFragment;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.productCatalogs;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Padmaja on 04/07/2019.
 */

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String classCode = "OMS_Android_Activity_Cart";
    Toolbar toolbar;
    ActionBar actionBar;
    FrameLayout fragmentView;
    private String userId = "";
    private boolean isItemAdded = false;
    TextView txtApprovals, txtOrders, txtTimer, txtSCMCart;
    SharedPreferencesUtils sharedPreferencesUtils;
    private static CountDownTimer countDownTimer;
    SwipeRefreshLayout mySwipeRefreshLayout;
    Common common;
    private OMSCoreMessage core;
    RestService restService;
    ErrorMessages errorMessages;
    AppDatabase db;
    ProgressDialogUtils progressDialogUtils;
    int c_width, c_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        loadFormControls();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
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
                txtTimer.setVisibility(View.VISIBLE);
                txtTimer.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                if (minutes == 0)
                    txtTimer.startAnimation(anim);
            }

            public void onFinish() {
                txtTimer.setText("00:00");
                txtTimer.setVisibility(View.GONE);
                // Called after timer finishes
                if (mills > 0) {

                }
                // deleteCartItemReservation();
            }
        }.start();

    }

    public void stopTimer() {
        // countDownTimer.cancel();
    }

    Animation anim;

    //Loading all the form controls
    @SuppressLint("ClickableViewAccessibility")
    private void loadFormControls() {

        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // gets the action bar support to enable features
            actionBar = getSupportActionBar();

            fragmentView = findViewById(R.id.fragmentView);
            txtApprovals = findViewById(R.id.txtApprovals);
            txtOrders = findViewById(R.id.txtOrders);
            txtSCMCart = findViewById(R.id.txtSCMCart);
            txtTimer = findViewById(R.id.txtTimer);
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

            common = new Common();
            restService = new RestService();
            core = new OMSCoreMessage();
            errorMessages = new ErrorMessages();
            progressDialogUtils = new ProgressDialogUtils(CartActivity.this);

            db = new RoomAppDatabase(CartActivity.this).getAppDatabase();

            mySwipeRefreshLayout = findViewById(R.id.mySwipeRefreshLayout);

            SharedPreferences sp = CartActivity.this.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
            userId = sp.getString(KeyValues.USER_ID, "");

            if (getIntent().getExtras() != null) {

                isItemAdded = getIntent().getExtras().getBoolean(KeyValues.IS_ITEM_ADDED_TO_CART);
                if (isItemAdded) {
                    if (NetworkUtils.isInternetAvailable(CartActivity.this)) {
                        // ((MainActivity) CartActivity.this).startTime();
                    }
                }
            }

            txtOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    txtOrders.setBackgroundColor(Color.parseColor("#008cec"));
                    txtOrders.setTextColor(Color.WHITE);
                    txtOrders.setTypeface(Typeface.DEFAULT_BOLD);
                    txtApprovals.setTypeface(Typeface.DEFAULT);
                    txtApprovals.setBackgroundColor(Color.TRANSPARENT);
                    txtApprovals.setTextColor(Color.GRAY);
                    txtSCMCart.setTypeface(Typeface.DEFAULT);
                    txtSCMCart.setBackgroundColor(Color.TRANSPARENT);
                    txtSCMCart.setTextColor(Color.GRAY);
                    FragmentUtils.replaceFragmentWithBackStack(CartActivity.this, R.id.fragmentView, new CartFragment());
                    txtOrders.setClickable(false);
                    txtApprovals.setClickable(true);
                    txtSCMCart.setClickable(true);

                }
            });

            txtApprovals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    txtApprovals.setTypeface(Typeface.DEFAULT_BOLD);
                    txtApprovals.setBackgroundColor(Color.parseColor("#008cec"));
                    txtApprovals.setTextColor(Color.WHITE);
                    txtOrders.setTypeface(Typeface.DEFAULT);
                    txtOrders.setBackgroundColor(Color.TRANSPARENT);
                    txtOrders.setTextColor(Color.GRAY);
                    txtSCMCart.setTypeface(Typeface.DEFAULT);
                    txtSCMCart.setBackgroundColor(Color.TRANSPARENT);
                    txtSCMCart.setTextColor(Color.GRAY);
                    FragmentUtils.replaceFragmentWithBackStack(CartActivity.this, R.id.fragmentView, new CartApprovalFragment());
                    txtOrders.setClickable(true);
                    txtApprovals.setClickable(false);
                    txtSCMCart.setClickable(true);

                }
            });

            txtSCMCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    txtSCMCart.setTypeface(Typeface.DEFAULT_BOLD);
                    txtSCMCart.setBackgroundColor(Color.parseColor("#008cec"));
                    txtSCMCart.setTextColor(Color.WHITE);
                    txtOrders.setTypeface(Typeface.DEFAULT);
                    txtOrders.setBackgroundColor(Color.TRANSPARENT);
                    txtOrders.setTextColor(Color.GRAY);
                    txtApprovals.setTypeface(Typeface.DEFAULT);
                    txtApprovals.setBackgroundColor(Color.TRANSPARENT);
                    txtApprovals.setTextColor(Color.GRAY);
                    FragmentUtils.replaceFragmentWithBackStack(CartActivity.this, R.id.fragmentView, new SCMCartFragment());
                    txtOrders.setClickable(true);
                    txtApprovals.setClickable(true);
                    txtSCMCart.setClickable(false);

                }
            });

            txtOrders.performClick();

            mySwipeRefreshLayout.setNestedScrollingEnabled(true);
            mySwipeRefreshLayout.setEnabled(false);
            mySwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            // This method performs the actual data-refresh operation.
                            // The method calls setRefreshing(false) when it's finished.
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
            );

            sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getApplicationContext());
            txtTimer.setVisibility(View.GONE);
            /*
            try {
                long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
                Calendar calendar = Calendar.getInstance();
                long mills = calendar.getTimeInMillis();
                startTime(300000 - (mills - timer));
            } catch (Exception e) {
                txtTimer.setText("00:00");
                txtTimer.setVisibility(View.GONE);
            }
            */

            anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // if you want to change color at start of animation
                    txtTimer.setTextColor(Color.RED);
                    if (txtTimer.getText().toString().equals("00:00")) {
                        txtTimer.setVisibility(View.GONE);
                        txtTimer.clearAnimation();
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // if you want to change color at end of animation
                    // textview.settextcolor("your color")\
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

            txtTimer.setOnTouchListener(new View.OnTouchListener() {

                private float xCoOrdinate, yCoOrdinate;

                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    //drag(event, v);
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

        } catch (Exception ex) {
            //
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (menu != null) {

            final MenuItem item = menu.findItem(R.id.addItem);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);

            final MenuItem item2 = menu.findItem(R.id.action_home);
            item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item2.setVisible(true);

        }

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

                Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                /*Intent i1=new Intent(getApplicationContext(),LoginActivity.class);
                i1.setAction(Intent.ACTION_MAIN);
                i1.addCategory(Intent.CATEGORY_HOME);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i1);
                finish();*/

            }
            break;

            case R.id.addItem: {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.putExtra(KeyValues.OPEN_CATALOG, true);
                startActivity(intent);
            }
            break;

            case R.id.action_profile: {
                FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new ProfileFragment());
            }
            break;

            case R.id.action_home: {
                onBackPressed();
                FragmentUtils.clearBackStack(CartActivity.this);
            }
            break;

            case android.R.id.home: {
                onBackPressed();
                FragmentUtils.clearBackStack(CartActivity.this);
            }
            break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.toolbar_cart));
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        /*
        try {
            Calendar calendar = Calendar.getInstance();
            long mills = calendar.getTimeInMillis();
            long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
            startTime(300000 - (mills - timer));
        } catch (Exception e) { }
        */
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

    public void deleteCartItemReservation() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, CartActivity.this);
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
                    ProgressDialogUtils.closeProgressDialog();
                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, CartActivity.this, CartActivity.this);
                            }

                        } else {

                            try {

                                String respObject = core.getEntityObject().toString();

                                if (respObject.equals("Reservation Deleted Successfully")) {
                                    txtTimer.setText("00:00");
                                    txtTimer.setVisibility(View.GONE);
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                Toast.makeText(CartActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(CartActivity.this)) {
                        DialogUtils.showAlertDialog(CartActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(CartActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", CartActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(CartActivity.this, errorMessages.EMC_0003);
        }
    }

}
