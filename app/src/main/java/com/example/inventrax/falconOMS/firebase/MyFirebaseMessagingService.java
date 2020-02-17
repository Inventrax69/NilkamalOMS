package com.example.inventrax.falconOMS.firebase;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.LoginActivity;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context context;
    SharedPreferencesUtils sharedPreferencesUtils;
    private String nType = "", operation = "", type = "", ID = "",Link="";
    AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNewToken(String token) {

        Log.d("NOTIFICATION2", "Refreshed token:" + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        db = new RoomAppDatabase(getApplicationContext()).getAppDatabase();
        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getApplicationContext());

        Log.d("ABCDE", new Gson().toJson(remoteMessage.getData()));

        JSONObject object = null;
        try {
            object = new JSONObject(remoteMessage.getData());

            nType = object.getString("NType");
            if (nType.equalsIgnoreCase("4")) {
                operation = object.getString("Ope");
                type = object.getString("Type");
                ID = object.getString("ID");
                Link = object.getString("Link");
                if (type.equalsIgnoreCase("Customer")) {
                    sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_MASTER_UPDATE, true);
                } else {
                    sharedPreferencesUtils.savePreference(KeyValues.IS_ITEM_MASTER_UPDATE, true);
                }
            } else {
                type = object.getString("Type");
                ID = object.getString("ID");
                Link = object.getString("Link");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nType.equalsIgnoreCase("3")) {
            showNotification(type,ID,Link);
        }

    }

    private void showNotification(String notificationText,String ID,String Link) {

        String NOTIFICATION_CHANNEL_ID = "OMS";

        long pattern[] = {0, 1000, 500, 1000};

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "OMS Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            channel.canBypassDnd();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean value= prefs.getBoolean(KeyValues.IS_CUSTOMER_LOADED,false);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KeyValues.IS_NOTIFICATION_AVAILABLE, true);
        editor.commit();

        Intent appActivityIntent;
        if (value) {
            appActivityIntent = new Intent(this, MainActivity.class);
            //appActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            appActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appActivityIntent.putExtra("NOTIFICATION1", "msg");
            appActivityIntent.putExtra("NOTIFY_ID", ID);
            appActivityIntent.putExtra("Link", Link);
            appActivityIntent.putExtra("Type", notificationText);
        } else {
            appActivityIntent = new Intent(this, LoginActivity.class);
            //appActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            appActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appActivityIntent.putExtra("NOTIFICATION1", "msg");
            appActivityIntent.putExtra("NOTIFY_ID", ID);
            appActivityIntent.putExtra("Type", notificationText);
        }

        PendingIntent contentAppActivityIntent =
                PendingIntent.getActivity(
                        this,  // calling from Activity
                        0,
                        appActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Log.d("NOTIFICATION", notificationText);

        notificationBuilder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(notificationText)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.n_launcher)
                .setStyle((new NotificationCompat.BigTextStyle().bigText(notificationText)))
                .setContentIntent(contentAppActivityIntent);

        mNotificationManager.notify("OMS", Integer.parseInt(ID), notificationBuilder.build());

    }

}
