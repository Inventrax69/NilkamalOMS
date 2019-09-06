package com.example.inventrax.falconOMS.logout;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.example.inventrax.falconOMS.activities.LoginActivity;
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


    public void doLogout(Activity activity){

        try
        {
            Intent loginIntent = new Intent(activity, LoginActivity.class);
            activity.startActivity(loginIntent);

            //Toast.makeText(activity, "You have successfully logged out", Toast.LENGTH_LONG).show();
            activity.finish();

        }catch (Exception ex){

        }

    }


}
