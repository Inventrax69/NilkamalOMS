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
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context context;
    SharedPreferences sharedpreferences;
    SharedPreferencesUtils sharedPreferencesUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNewToken(String token) {
        Log.d("ABCDE", "Refreshed token:" + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        String NOTIFICATION_CHANNEL_ID = "OMS";

        long pattern[] = {0, 1000, 500, 1000};

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "OMS Notifications",
                    NotificationManager.IMPORTANCE_HIGH);

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

        Intent appActivityIntent = new Intent(this, MainActivity.class);

        appActivityIntent.putExtra("whattodo", "msg");


        PendingIntent contentAppActivityIntent =
                PendingIntent.getActivity(
                        this,  // calling from Activity
                        0,
                        appActivityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);



        Log.d("NOTIFICATION", remoteMessage.getNotification().getBody());

        notificationBuilder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(remoteMessage.getNotification().getBody())
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.n_launcher)
                .setContentIntent(contentAppActivityIntent)
                .setAutoCancel(true);



        mNotificationManager.notify("OMS", 1000, notificationBuilder.build());


    }

}
