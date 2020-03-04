package com.example.inventrax.falconOMS.logout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.example.inventrax.falconOMS.activities.LoginActivity;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;


public class LogoutUtil {

    private FragmentActivity fragmentActivity;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private Activity activity;

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public void setSharedPreferencesUtils(SharedPreferencesUtils sharedPreferencesUtils) {
        this.sharedPreferencesUtils = sharedPreferencesUtils;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        new ProgressDialogUtils(activity);
    }


    public void doLogout(Activity activity, Context context){

        try
        {
            Intent loginIntent = new Intent(activity, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(loginIntent);
            new SharedPreferencesUtils(KeyValues.MY_PREFS, context).savePreference(KeyValues.IS_CUSTOMER_LOADED, false);
            activity.finish();

        }catch (Exception ex){

        }

    }


}
