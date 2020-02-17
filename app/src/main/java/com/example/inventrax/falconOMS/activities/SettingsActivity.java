package com.example.inventrax.falconOMS.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.common.constants.ServiceURL;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;

/**
 * Created by Padmaja.b on 11/12/2019.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private String classCode = "WMSCore_Android_Activity_002";

    private TextInputLayout inputLayoutServiceUrl;
    private EditText inputService;
    private Button btnSave,btnClose;
    private String url=null;

    AppDatabase db;

    private SharedPreferencesUtils sharedPreferencesUtils;
    ServiceURL serviceUrl = new ServiceURL();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadFormControls();
    }

    public void loadFormControls()
    {
        btnSave=(Button)findViewById(R.id.btnSave);
        btnClose=(Button)findViewById(R.id.btnClose);
        inputLayoutServiceUrl = (TextInputLayout) findViewById(R.id.txtInputLayoutServiceUrl);
        inputService = (EditText)findViewById(R.id.etServiceUrl);

        btnSave.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getApplicationContext());
        inputService.setText(sharedPreferencesUtils.loadPreference(KeyValues.SETTINGS_URL));

        db = new RoomAppDatabase(SettingsActivity.this).getAppDatabase();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:

                if(!inputService.getText().toString().isEmpty()) {
                    serviceUrl.setServiceUrl("");
                    SharedPreferences sp = this.getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
                    sharedPreferencesUtils.removePreferences(KeyValues.SETTINGS_URL);
                    sharedPreferencesUtils.savePreference(KeyValues.SETTINGS_URL, inputService.getText().toString());

                    db.cartHeaderDAO().deleteAll();
                    db.cartDetailsDAO().deleteAll();
                    db.itemDAO().deleteAll();
                    db.customerDAO().deleteAll();
                    db.variantDAO().deleteAll();

                    DialogUtils.showAlertDialog(SettingsActivity.this,new ErrorMessages().EMC_0021);



                }else {
                    DialogUtils.showAlertDialog(SettingsActivity.this,new ErrorMessages().EMC_0022);
                }


                break;

            case R.id.btnClose:
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}