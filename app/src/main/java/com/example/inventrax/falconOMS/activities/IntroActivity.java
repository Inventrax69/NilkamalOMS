package com.example.inventrax.falconOMS.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.IntroViewPagerAdapter;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.model.ScreenItem;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Padmaja on 02/08/2019.
 */

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    Button btnGetStarted;
    Animation btnAnim ;
    TextView tvSkip;

    public static final int MULTIPLE_PERMISSIONS = 15;
    // if the android mobile version is greater than 6.0 we are giving the following permissions
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.GET_ACCOUNTS, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET, Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make the activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        // when this activity is about to be launch we need to check if its openened before or not

        if (restorePrefData()) {

            Intent mainActivity = new Intent(getApplicationContext(),SplashActivity.class );
            startActivity(mainActivity);
            finish();


        }

        setContentView(R.layout.activity_intro);
        requestforpermissions(permissions);
        /*// hide the action bar

        getSupportActionBar().hide();*/
        loadFormControls();

    }

    public void loadFormControls(){

        // ini views
        btnNext = (Button) findViewById(R.id.btn_next);
        btnGetStarted = (Button) findViewById(R.id.btn_get_started);
        tabIndicator = (TabLayout) findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = (TextView) findViewById(R.id.tv_skip);

        // fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Order Booking","We are getting India's favourite furniture brand closer to you with just a click away!",R.drawable.order_delivery));
        mList.add(new ScreenItem("Experss Delivery","One stop-shop for quality, trendy and budget-friendly furniture range",R.drawable.express_delivery));
        mList.add(new ScreenItem("Order Status","We are getting India's favourite furniture brand closer to you with just a click away!",R.drawable.delivery_status));

        // setup viewpager
        screenPager = (ViewPager) findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {

                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size()-1) { // when we rech to the last screen
                    // TODO : show the GETSTARTED Button and hide the indicator and the next button

                    loaddLastScreen();

                }



            }
        });

        // tablayout add change listener


        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //navigation transition
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                //open splash activity
                Intent login = new Intent(getApplicationContext(),SplashActivity.class);
                startActivity(login);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                savePrefsData1();
                finish();


            }
        });

        // skip button click listener

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });
    }

    private boolean restorePrefData() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences(KeyValues.MY_PREFS,MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean(KeyValues.IS_INTRO_OPENED,false);
        return  isIntroActivityOpnendBefore;

    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(KeyValues.MY_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KeyValues.IS_INTRO_OPENED,true);
        editor.commit();


    }

    private void savePrefsData1() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(KeyValues.MY_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KeyValues.IS_HINTS,true);
        editor.commit();


    }


    public void requestforpermissions(String[] permissions) {
        if (checkPermissions()) {
        }
        //  permissions  granted.
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(IntroActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFormControls();
                    // permissions granted.
                } else {
                    String permission = "";
                    for (String per : permissions) {
                        permission += "\n" + per;

                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }


    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);



    }
}
