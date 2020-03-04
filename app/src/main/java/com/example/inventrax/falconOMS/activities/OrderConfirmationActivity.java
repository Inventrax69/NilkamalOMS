package com.example.inventrax.falconOMS.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.OrderConfirmationHeaderAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.fragments.ProfileFragment;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListResponseDTO;
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
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String classCode = "OMS_Android_OrderConfirmationActivity";
    Toolbar toolbar;
    ActionBar actionBar;
    private RecyclerView rvItemsList;
    LinearLayoutManager linearLayoutManager;
    private TextView txtTotalAmt, txtDeliveryOptions, txtConfirmOrder, txtCancelOrder, txtTotalAmtTax, txtTaxes;
    private FrameLayout frame;
    BottomSheetBehavior behavior;
    private RadioButton rbDefault, rbOptions;
    private SearchableSpinner spinnerDefaultDelivery, optionsSpinnerOne, optionsSpinnerTwo;
    private Button btnViewDefault, btnProceedDefault, btnViewOptionOne, btnViewOptionTwo, btnProceedOptions;
    OrderConfirmationHeaderAdapter mAdapter;
    List<CartHeaderListDTO> cartHeaderList = null;
    AppDatabase db;
    String userId = "";
    Common common;
    private OMSCoreMessage core;
    RestService restService;
    ErrorMessages errorMessages;
    int cartHeaderId, custumerId;
    TextView txtTimer;
    SharedPreferencesUtils sharedPreferencesUtils;
    private static CountDownTimer countDownTimer;
    CoordinatorLayout coordinatorLayout;
    ProgressDialogUtils progressDialogUtils;
    double total, total_tax;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    Dialog approvalDailog;
    List<Integer> headersList;
    Animation anim;
    int c_width, c_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        loadFormControls();
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
                if (mills > 0)
                    deleteCartItemReservation();
            }
        }.start();

    }

    public void stopTimer() {
        countDownTimer.cancel();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void loadFormControls() {

        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // gets the action bar support to enable features
            actionBar = getSupportActionBar();
            // getSupportActionBar().setDisplayShowHomeEnabled(true);

            frame = (FrameLayout) findViewById(R.id.frame);
            db = new RoomAppDatabase(OrderConfirmationActivity.this).getAppDatabase();
            SharedPreferences sp = this.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
            userId = sp.getString(KeyValues.USER_ID, "");

            common = new Common();
            restService = new RestService();
            core = new OMSCoreMessage();
            errorMessages = new ErrorMessages();
            progressDialogUtils = new ProgressDialogUtils(OrderConfirmationActivity.this);

            rvItemsList = (RecyclerView) findViewById(R.id.rvItemsList);
            linearLayoutManager = new LinearLayoutManager(OrderConfirmationActivity.this);
            rvItemsList.setLayoutManager(linearLayoutManager);

            txtTotalAmt = (TextView) findViewById(R.id.txtTotalAmt);
            txtTotalAmtTax = (TextView) findViewById(R.id.txtTotalAmtTax);
            txtTaxes = (TextView) findViewById(R.id.txtTaxes);
            txtDeliveryOptions = (TextView) findViewById(R.id.txtDeliveryOptions);
            txtConfirmOrder = (TextView) findViewById(R.id.txtConfirmOrder);
            txtCancelOrder = (TextView) findViewById(R.id.txtCancelOrder);
            txtTimer = (TextView) findViewById(R.id.txtTimer);
            rbDefault = (RadioButton) findViewById(R.id.rbDefault);
            rbOptions = (RadioButton) findViewById(R.id.rbOptions);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

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
                            v.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });

            rbDefault.setChecked(true);

            btnViewDefault = (Button) findViewById(R.id.btnViewDefault);
            btnProceedDefault = (Button) findViewById(R.id.btnProceedDefault);
            btnViewOptionOne = (Button) findViewById(R.id.btnViewOptionOne);
            btnViewOptionTwo = (Button) findViewById(R.id.btnViewOptionTwo);
            btnProceedOptions = (Button) findViewById(R.id.btnProceedOptions);

            btnViewDefault.setOnClickListener(this);
            btnProceedDefault.setOnClickListener(this);
            btnViewOptionOne.setOnClickListener(this);
            btnViewOptionTwo.setOnClickListener(this);
            btnProceedOptions.setOnClickListener(this);
            txtConfirmOrder.setOnClickListener(this);
            txtCancelOrder.setOnClickListener(this);

            spinnerDefaultDelivery = (SearchableSpinner) findViewById(R.id.spinnerDefaultDelivery);
            optionsSpinnerOne = (SearchableSpinner) findViewById(R.id.optionsSpinnerOne);
            optionsSpinnerTwo = (SearchableSpinner) findViewById(R.id.optionsSpinnerTwo);

            txtDeliveryOptions.setOnClickListener(this);
            sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getApplicationContext());

            // Bottom sheet
            View bottomSheet = findViewById(R.id.bottom_sheet);

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
                    //textview.settextcolor("your color")\
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

            loadData();

        } catch (Exception ex) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_home);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setVisible(true);

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
                Intent intent = new Intent(OrderConfirmationActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            break;

            case R.id.action_profile: {
                FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new ProfileFragment());
            }
            break;

            case R.id.action_cart: {
                Intent intent = new Intent(OrderConfirmationActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
            break;

            case R.id.action_home: {
                db.cartDetailsDAO().resetDeliveryDates();
                FragmentUtils.clearBackStack(OrderConfirmationActivity.this);
                onBackPressed();
            }
            break;

            case android.R.id.home: {
                Intent intent = new Intent(OrderConfirmationActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
            break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txtDeliveryOptions:
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                frame.setVisibility(View.VISIBLE);
                break;

            case R.id.txtConfirmOrder:
                if (NetworkUtils.isInternetAvailable(OrderConfirmationActivity.this)) {
                    headersList = new ArrayList<>();
                    CheckOrderConfirmation();
                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0014, ContextCompat.getColor(OrderConfirmationActivity.this, R.color.dark_red), Snackbar.LENGTH_SHORT);
                }
                break;
            case R.id.btnProceedOptions:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                frame.setVisibility(View.GONE);
                rvItemsList.setAdapter(null);
                break;
            case R.id.txtCancelOrder:
                if (NetworkUtils.isInternetAvailable(OrderConfirmationActivity.this)) {
                    deleteCartItemReservation();
                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0014, ContextCompat.getColor(OrderConfirmationActivity.this, R.color.dark_red), Snackbar.LENGTH_SHORT);
                }
                break;
            case R.id.btnProceedDefault:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                frame.setVisibility(View.GONE);
                rvItemsList.setAdapter(null);
                break;


        }
    }

/*
    private void CheckOrderConfirmation() {

        if (cartHeaderList.size() > 0) {
            custumerId = cartHeaderList.get(0).getCustomerID();
            cartHeaderId = cartHeaderList.get(0).getCartHeaderID();
            CartHeader cartHeader = db.cartHeaderDAO().getCartHeader(custumerId, cartHeaderId);
            if (cartHeader.isApproved == 0 && cartHeader.isInActive == 1) {
                showInActiveDialog(cartHeaderList.get(0).getCustomerName());
                sendForApproval(cartHeader.cartHeaderID, "6");
            } else if (cartHeader.isApproved == 0 && cartHeader.isCreditLimit == 1) {
                showCreditLimitDialog(cartHeaderList.get(0).getCustomerName());
                sendForApproval(cartHeader.cartHeaderID, "5");
            } else {
                OrderConfirmation(db.cartHeaderDAO().getFullfilmentCompletedHeadersList());
            }
        } else {
            Toast.makeText(OrderConfirmationActivity.this, "No orders list", Toast.LENGTH_SHORT).show();
        }
    }
*/

    private void CheckOrderConfirmation() {

        if (cartHeaderList.size() > 0) {

            custumerId = cartHeaderList.get(0).getCustomerID();
            cartHeaderId = cartHeaderList.get(0).getCartHeaderID();
            if (cartHeaderList.get(0).getIsApproved() == 0 && cartHeaderList.get(0).getIsInActive() == 1) {
                showInActiveDialog(cartHeaderList.get(0).getCustomerName());
                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        sendForApproval(cartHeaderId, "6");
                    }

                }.start();


            } else if (cartHeaderList.get(0).getIsApproved() == 0 && cartHeaderList.get(0).getIsCreditLimit() == 1) {
                showCreditLimitDialog(cartHeaderList.get(0).getCustomerName());
                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        sendForApproval(cartHeaderId, "5");
                    }

                }.start();

            } else {
                headersList.add(cartHeaderId);
                cartHeaderList.remove(0);
                mAdapter.notifyItemRemoved(0);
                if (cartHeaderList.size() > 0) {
                    mAdapter.notifyDataSetChanged();
                    CheckOrderConfirmation();
                } else {
                    doOrderConfirmation();
                }
                // OrderConfirmation(db.cartHeaderDAO().getFullfilmentCompletedHeadersList());

            }
        } else {
            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, errorMessages.EMC_0014, ContextCompat.getColor(OrderConfirmationActivity.this, R.color.dark_red), Snackbar.LENGTH_SHORT);
        }
    }

    int[] headerId;

    public void doOrderConfirmation() {
        if (headersList.size() > 0) {
            headerId = new int[headersList.size()];
            for (int i = 0; i < headersList.size(); i++) {
                headerId[i] = headersList.get(i);
            }
            OrderConfirmation(headerId);
        } else {
            syncCart();
        }
    }

    public void loadData() {
        getcart();
    }

    public void sendForApproval(final int cartHeaderID, String type) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, OrderConfirmationActivity.this);
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
                                common.showAlertType(omsExceptionMessage, OrderConfirmationActivity.this, OrderConfirmationActivity.this);
                            }
                            ProgressDialogUtils.closeProgressDialog();
                            db.cartDetailsDAO().deleteCartDetailsOfCartDetails(cartHeaderID);
                            cartHeaderList.remove(0);
                            mAdapter.notifyItemRemoved(0);
                            if (cartHeaderList.size() > 0) {
                                mAdapter.notifyDataSetChanged();
                                CheckOrderConfirmation();
                            } else {
                                doOrderConfirmation();
                            }
                            approvalDailog.dismiss();


                        } else {
                            ProgressDialogUtils.closeProgressDialog();
                            try {

                                //  String respObject = core.getEntityObject().toString();
                                cartHeaderList.remove(0);
                                mAdapter.notifyItemRemoved(0);
                                if (cartHeaderList.size() > 0) {
                                    mAdapter.notifyDataSetChanged();
                                    CheckOrderConfirmation();
                                } else {
                                    doOrderConfirmation();
                                }
                                approvalDailog.dismiss();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(OrderConfirmationActivity.this)) {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", OrderConfirmationActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0003);
        }
    }

    private void showInActiveDialog(String customerName) {

        approvalDailog = new Dialog(OrderConfirmationActivity.this);
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

        approvalDailog = new Dialog(OrderConfirmationActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.toolbar_orderconfirmation));
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        try {
            Calendar calendar = Calendar.getInstance();
            long mills = calendar.getTimeInMillis();
            long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
            startTime(300000 - (mills - timer));
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void OrderConfirmation(final int[] cartHeaderId) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.OrderFulfilment_DTO, OrderConfirmationActivity.this);
        CartHeaderListResponseDTO oDto = new CartHeaderListResponseDTO();
        oDto.setCartHeaderID(cartHeaderId);
        oDto.setResult("");
        //oDto.setCartHeaderID(Integer.parseInt(cartHeaderId));

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.OrderConfirmation(message);

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
                                common.showAlertType(omsExceptionMessage, OrderConfirmationActivity.this, OrderConfirmationActivity.this);
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                // String respObject = core.getEntityObject().toString();

                                if (core.getEntityObject() != null) {

                                    List lstSONum = new ArrayList();
                                    lstSONum = (ArrayList) core.getEntityObject();
                                    String soNums = "";
                                    if (lstSONum != null && lstSONum.size() > 0) {
                                        for (int i = 0; i < lstSONum.size(); i++) {
                                            soNums = soNums + (String) ((LinkedTreeMap) lstSONum.get(i)).get("SONumber") + ",";
                                        }
                                        soNums = soNums.substring(0, soNums.length() - 1);
                                    } else {
                                        showFailedDialog();
                                        ProgressDialogUtils.closeProgressDialog();
                                        return;
                                    }

                                    sharedPreferencesUtils.savePreference("timer", 0L);
                                    txtTimer.setText("00:00");
                                    txtTimer.setVisibility(View.GONE);
                                    db.cartDetailsDAO().deleteCartDetailsOfCartDetails(cartHeaderId);

                                    showSuccessDialog(soNums);

                                } else {
                                    showFailedDialog();
                                }
                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                Toast.makeText(OrderConfirmationActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if (NetworkUtils.isInternetAvailable(OrderConfirmationActivity.this)) {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", OrderConfirmationActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0003);
        }
    }


    public void deleteCartItemReservation() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, OrderConfirmationActivity.this);
        productCatalogs oDto = new productCatalogs();
        oDto.setUserID(userId);
        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

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
                                common.showAlertType(omsExceptionMessage, OrderConfirmationActivity.this, OrderConfirmationActivity.this);
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                String respObject = core.getEntityObject().toString();

                                if (respObject.equals("Reservation Deleted Successfully")) {
                                    db.cartDetailsDAO().resetDeliveryDates();
                                    Intent i = new Intent(OrderConfirmationActivity.this, CartActivity.class);
                                    startActivity(i);
                                    finish();
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                Toast.makeText(OrderConfirmationActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        ProgressDialogUtils.closeProgressDialog();

                    }
                    ProgressDialogUtils.closeProgressDialog();

                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(OrderConfirmationActivity.this)) {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", OrderConfirmationActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0003);
        }
    }


    public void getcart() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, OrderConfirmationActivity.this);
        productCatalogs productCatalogs = new productCatalogs();
        productCatalogs.setHandheldRequest(true);
        productCatalogs.setUserID(userId);
        productCatalogs.setCustomerID("0");
        productCatalogs.setResults("");
        if(db.cartHeaderDAO().getFullfilmentCompletedHeaders().size()==1)
            productCatalogs.setCartHeaderID(db.cartHeaderDAO().getFullfilmentCompletedHeaders().get(0).cartHeaderID);
        else
            productCatalogs.setCartHeaderID(0);

/*        if( db.cartHeaderDAO().getFullfilmentCompletedHeaders().size() == 0){

        }else{
            productCatalogs.setCartHeaderID(db.cartHeaderDAO().getFullfilmentCompletedHeaders().);
        }*/

        message.setEntityObject(productCatalogs);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.getcart(message);

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
                                common.showAlertType(omsExceptionMessage, OrderConfirmationActivity.this, OrderConfirmationActivity.this);
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());
                                cartHeaderList = new ArrayList<>();
                                for (int i = 0; i < getCartHeader.length(); i++) {
                                    String CustomerID =  getCartHeader.getJSONObject(i).getString("CustomerID");
                                    String CustomerName =  getCartHeader.getJSONObject(i).getString("CustomerName");
                                    String CustomerCode =  getCartHeader.getJSONObject(i).getString("CustomerCode");
                                    Double CreditLimit =  getCartHeader.getJSONObject(i).getDouble("CreditLimit");
                                    for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {
                                        CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);
                                        cartHeaderListDTO.setCustomerName(CustomerName);
                                        cartHeaderListDTO.setCreditLimit(CreditLimit);
                                        if (db.cartHeaderDAO().getFullfilmentCompletedHeaders() != null) {
                                            List<CartHeader> cartHeaders = db.cartHeaderDAO().getFullfilmentCompletedHeaders();
                                            for (CartHeader cartHeader : cartHeaders) {
                                                if (cartHeaderListDTO.getCartHeaderID() == cartHeader.cartHeaderID) {
                                                    cartHeaderList.add(cartHeaderListDTO);
                                                }
                                            }
                                        }
                                    }
                                }

                                if (cartHeaderList.size() > 0) {
                                    total = 0.0;
                                    total_tax = 0.0;
                                    for (int i = 0; i < cartHeaderList.size(); i++) {
                                        if (cartHeaderList.get(i).getIsApproved() == 0) {
                                            total += Double.parseDouble(cartHeaderList.get(i).getTotalPrice());
                                            total_tax += Double.parseDouble(cartHeaderList.get(i).getTotalPriceWithTax());
                                            /*  for (int j = 0; j < cartHeaderList.get(i).getDeliveryDate().size(); j++) {
                                                for (int k = 0; k < cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().size(); k++) {
                                                    total +=
                                                            ((cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice() != null ||
                                                                    !cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice().isEmpty() ||
                                                                    !cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice().equals("")) ?
                                                                    Double.parseDouble(cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getPrice()) : 0.0)
                                                                    *
                                                                    ((cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity() != null ||
                                                                            !cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity().isEmpty() ||
                                                                            !cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity().equals("")) ?
                                                                            Double.parseDouble(cartHeaderList.get(i).getDeliveryDate().get(j).getListCartDetailsList().get(k).getQuantity()) : 0.0);
                                                }
                                            }*/
                                        }
                                    }

                                    df2.setRoundingMode(RoundingMode.DOWN);
/*                                    txtTotalAmt.setText("Rs." + df2.format(total) + "/-");
                                    txtTotalAmtTax.setText("Rs." + df2.format(total_tax) + "/-");
                                    txtTaxes.setText("Rs." + df2.format(total_tax - total) + "/-");*/
                                    txtTotalAmt.setText("Rs." + String.format("%.2f", total) + "/-");
                                    txtTotalAmtTax.setText("Rs." + String.format("%.2f", total_tax) + "/-");
                                    txtTaxes.setText("Rs." + String.format("%.2f", total_tax - total) + "/-");

                                    List<CartHeaderListDTO> cartHeaderList1 = new ArrayList<>(cartHeaderList);

                                    for (int p = 0; p < cartHeaderList.size(); p++) {
                                        HashMap<String, List<CartDetailsListDTO>> offerCartDetailsDTOList = new HashMap<>();
                                        for (int q = 0; q < cartHeaderList.get(p).getDeliveryDate().size(); q++) {
                                            for (int s = 0; s < cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().size(); s++) {
                                                if (cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().get(s).getOfferItemCartDetailsID() != null) {
                                                    if (!cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().get(s).getOfferItemCartDetailsID().equals("-1")) {
                                                        List<CartDetailsListDTO> cartDetailsListDTOS = offerCartDetailsDTOList.get(cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().get(s).getOfferItemCartDetailsID());
                                                        if (cartDetailsListDTOS == null) {
                                                            cartDetailsListDTOS = new ArrayList<>();
                                                        }
                                                        cartDetailsListDTOS.add(cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().get(s));
                                                        offerCartDetailsDTOList.put(cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().get(s).getOfferItemCartDetailsID(), cartDetailsListDTOS);
                                                        cartHeaderList1.get(p).getDeliveryDate().get(q).getListCartDetailsList().remove(s);
                                                    }
                                                }
                                            }
                                        }
                                        cartHeaderList1.get(p).setOfferCartDetailsDTOList(offerCartDetailsDTOList);
                                    }


/*                                    List<CartHeaderListDTO> cartHeaderList1=new ArrayList<>(cartHeaderList);

                                    for(int p=0;p<cartHeaderList.size();p++){
                                        for(int q=0;q<cartHeaderList.get(p).getDeliveryDate().size();q++){
                                            for(int s=0;s<cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().size();s++){
                                                if(cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().get(s).getOfferItemCartDetailsID() !=null &&
                                                        !cartHeaderList.get(p).getDeliveryDate().get(q).getListCartDetailsList().get(s).getOfferItemCartDetailsID().equals("-1")){
                                                    cartHeaderList1.get(p).getDeliveryDate().get(q).getListCartDetailsList().remove(s);
                                                }
                                            }
                                        }
                                    }*/

                                    mAdapter = new OrderConfirmationHeaderAdapter(cartHeaderList1, OrderConfirmationActivity.this, OrderConfirmationActivity.this);
                                    rvItemsList.setAdapter(mAdapter);

                                }

                                Calendar calendar = Calendar.getInstance();
                                long mills = calendar.getTimeInMillis();
                                sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, OrderConfirmationActivity.this);
                                sharedPreferencesUtils.savePreference(KeyValues.TIMER, mills);
                                long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
                                (OrderConfirmationActivity.this).startTime(300000 - (mills - timer));


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
                    if (NetworkUtils.isInternetAvailable(OrderConfirmationActivity.this)) {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", OrderConfirmationActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0003);
        }
    }


    public void syncCart() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, OrderConfirmationActivity.this);
        productCatalogs productCatalogs = new productCatalogs();
        productCatalogs.setHandheldRequest(true);
        productCatalogs.setUserID(userId);
        message.setEntityObject(productCatalogs);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.cartlist(message);

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
                                common.showAlertType(omsExceptionMessage, OrderConfirmationActivity.this, OrderConfirmationActivity.this);
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                if(core.getEntityObject() !=null) {

                                    JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                    db.cartDetailsDAO().deleteAll();
                                    db.cartHeaderDAO().deleteAll();

                                    for (int i = 0; i < getCartHeader.length(); i++) {

                                        TypeToken<CartHeaderListDTO> header = new TypeToken<CartHeaderListDTO>() {};

                                        for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                            CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                            db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                    cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                    cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));

                                            for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {

                                                CartDetailsListDTO cart = cartHeaderListDTO.getListCartDetailsList().get(k);

                                                db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                        cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                        cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                        cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                        cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                        cart.getDiscountID(),cart.getDiscountText(),cart.getGST(),cart.getTax(),cart.getSubTotal(),cart.getHSNCode()));
                                            }

                                        }

                                    }

                                }else{
                                    db.cartDetailsDAO().deleteAll();
                                    db.cartHeaderDAO().deleteAll();
                                }

                                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, OrderConfirmationActivity.this);
                                sharedPreferencesUtils.savePreference(KeyValues.TIMER, 0L);
                                long timer = sharedPreferencesUtils.loadPreferenceAsLong(KeyValues.TIMER);
                                (OrderConfirmationActivity.this).findViewById(R.id.txtTimer).setVisibility(View.GONE);
                                (OrderConfirmationActivity.this).stopTimer();

                                Intent i = new Intent(OrderConfirmationActivity.this, CartActivity.class);
                                startActivity(i);
                                finish();

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
                    if (NetworkUtils.isInternetAvailable(OrderConfirmationActivity.this)) {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", OrderConfirmationActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(OrderConfirmationActivity.this, errorMessages.EMC_0003);
        }
    }


    private void showSuccessDialog(String soNum) {

        final Dialog mDialog = new Dialog(OrderConfirmationActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.dialog_order_created);

        TextView succesText = (TextView) mDialog.findViewById(R.id.textSONumber);
        Button buttonOk = (Button) mDialog.findViewById(R.id.buttonOk);
        TextView txtOrdersTitle = (TextView) mDialog.findViewById(R.id.txtOrdersTitle);
        txtOrdersTitle.setText(this.getString(R.string.order_created));
        succesText.setText(this.getString(R.string.so_success_text) + soNum);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
                syncCart();
            }
        });

        mDialog.show();

    }

    @SuppressLint("SetTextI18n")
    private void showFailedDialog() {

        final Dialog mDialog = new Dialog(OrderConfirmationActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.dialog_order_failed);

        TextView succesText = (TextView) mDialog.findViewById(R.id.textSONumber);
        Button buttonOk = (Button) mDialog.findViewById(R.id.buttonOk);

        succesText.setText(this.getString(R.string.order_failed));

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
                syncCart();
            }
        });

        mDialog.show();

    }

}
