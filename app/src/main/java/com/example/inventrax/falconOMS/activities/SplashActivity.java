package com.example.inventrax.falconOMS.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.inventrax.falconOMS.R;

/**
 * Created by padmaja rani.B on 02/08/2019
 */

public class SplashActivity extends Activity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashfile);
        //navigation transition
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        //changes to Login after 0.5sec
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);

    }
}