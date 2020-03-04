package com.example.inventrax.falconOMS.activities;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.application.AbstractApplication;
import com.example.inventrax.falconOMS.common.constants.ServiceURL;
import com.example.inventrax.falconOMS.locale.LocaleHelper;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.services.appupdate.ConfirmationStrategy;
import com.example.inventrax.falconOMS.services.appupdate.DownloadStrategy;
import com.example.inventrax.falconOMS.services.appupdate.InternalHttpDownloadStrategy;
import com.example.inventrax.falconOMS.services.appupdate.NotificationConfirmationStrategy;
import com.example.inventrax.falconOMS.services.appupdate.SimpleHttpDownloadStrategy;
import com.example.inventrax.falconOMS.services.appupdate.SimpleHttpVersionCheckStrategy;
import com.example.inventrax.falconOMS.services.appupdate.UpdateRequest;
import com.example.inventrax.falconOMS.services.appupdate.UpdateServiceUtils;
import com.example.inventrax.falconOMS.services.appupdate.VersionCheckStrategy;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.NotificationUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by padmaja rani.B on 02/08/2019
 */

public class SplashActivity extends Activity {

    Handler handler;
    SharedPreferencesUtils sharedPreferencesUtils;
    UpdateServiceUtils updateServiceUtils;
    private Context context;
    private UpdateRequest.Builder builder;
    private ServiceURL serviceURL;
    private static final String JSON_VERSION_CODE = "versionCode";
    private static final String JSON_UPDATE_URL = "updateURL";
    private static final String JSON_UPDATE_TYPE = "updateType";
    private static final String JSON_ERROR_CHECK = "error";
    protected String url = null;
    protected String updateURL = null;
    protected String updateType = null;
    AlertDialog.Builder alertDialog;
    int status;
    boolean isError = false;
    long downloadID;
    String updateJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashfile);

        // navigation transition
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, SplashActivity.this);
        sharedPreferencesUtils.savePreference(KeyValues.IS_CUSTOMER_MASTER_UPDATE, false);
        // sharedPreferencesUtils.savePreference(KeyValues.USER_ROLE_NAME,"DT");
        // changes to Login after 0.5sec

        try {
            if (!sharedPreferencesUtils.loadPreference(KeyValues.SELECTED_LANG).isEmpty() || (!sharedPreferencesUtils.loadPreference(KeyValues.SELECTED_LANG).equalsIgnoreCase("")))
                LocaleHelper.setLocale(SplashActivity.this, sharedPreferencesUtils.loadPreference(KeyValues.SELECTED_LANG));
            else
                LocaleHelper.setLocale(SplashActivity.this, "en");
        } catch (Exception e) {
            LocaleHelper.setLocale(SplashActivity.this, "en");
        }

        updateServiceUtils = new UpdateServiceUtils();
        this.context = AbstractApplication.get();
        builder = new UpdateRequest.Builder(context);
        serviceURL = new ServiceURL();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

/*              if (NetworkUtils.isInternetAvailable(SplashActivity.this))
                    checkUpdate();
                else
                    navigateToMainScreen();*/

                navigateToMainScreen();

            }
        }, 500);

    }

    public void checkUpdate() {

        try {
            builder.setVersionCheckStrategy(buildVersionCheckStrategy())
                    .setPreDownloadConfirmationStrategy(buildPreDownloadConfirmationStrategy())
                    .setDownloadStrategy(buildDownloadStrategy())
                    .setPreInstallConfirmationStrategy(buildPreInstallConfirmationStrategy())
                    .execute();

        } catch (Exception ex) {
            Log.d("test123", ex.toString());
        }
    }

    private VersionCheckStrategy buildVersionCheckStrategy() {
        return (new SimpleHttpVersionCheckStrategy("http://192.168.1.65/oms_storefront/api/" + "updateJson"));
    }

    private ConfirmationStrategy buildPreDownloadConfirmationStrategy() {

        MyTsk myTsk = new MyTsk(SplashActivity.this);
        myTsk.execute();
        Notification n = NotificationUtils.getUpdateNotification(SplashActivity.this, "Update Available", "Click to download the update!");
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        return (new NotificationConfirmationStrategy(n));

    }

    private DownloadStrategy buildDownloadStrategy() {
        if (Build.VERSION.SDK_INT >= 11) {
            return (new InternalHttpDownloadStrategy());
        }
        return (new SimpleHttpDownloadStrategy());
    }

    private ConfirmationStrategy buildPreInstallConfirmationStrategy() {
        Notification n = NotificationUtils.getUpdateNotification(SplashActivity.this, "Update Ready to Install", "Click to install the update!");
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        return (new NotificationConfirmationStrategy(n));
    }

    ProgressDialog mProgressDialog;
    public static int DOWNLOAD_REQUEST = 99;

    public class DownloadFile extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create progress dialog
            mProgressDialog = new ProgressDialog(SplashActivity.this);
            // Set your progress dialog Title
            mProgressDialog.setTitle("Downloading");
            // Set your progress dialog Message
            mProgressDialog.setMessage("Downloading the apk, please wait...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // Show progress dialog
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // Detect the file lenghth
                int fileLength = connection.getContentLength();
                // Locate storage location
                String filepath = Environment.getExternalStorageDirectory()
                        .getPath();
                // Download the file
                InputStream input = new BufferedInputStream(url.openStream());
                // Save the downloaded file
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Nilkamal_OMS/Nilkamal_OMS.apk");
                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // Publish the progress
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                // Error Log
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final PackageManager pm = SplashActivity.this.getPackageManager();
            String apkName = "Nilkamal_OMS.apk";
            String fullPath = Environment.getExternalStorageDirectory().getPath() + "/Nilkamal_OMS/" + apkName;
            PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);

            try {
                int versionNumber = info.versionCode;
                String versionName = info.versionName;
            } catch (Exception e) {
                //DialogUtils.showAlertDialog(SplashActivity.this,"Failed to update the app");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                Uri contentUri = FileProvider.getUriForFile(SplashActivity.this, "com.example.inventrax.falconOMS.provider",
                        new File(Environment.getExternalStorageDirectory().getPath() + "/Nilkamal_OMS/Nilkamal_OMS.apk"));
                Intent openFileIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                openFileIntent.setData(contentUri);
                SplashActivity.this.startActivityForResult(openFileIntent, DOWNLOAD_REQUEST);

            } else {

                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/Nilkamal_OMS/Nilkamal_OMS.apk")),
                        "application/vnd.android.package-archive");
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                SplashActivity.this.startActivityForResult(intent1, DOWNLOAD_REQUEST);

            }
            mProgressDialog.dismiss();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DOWNLOAD_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                if (updateType.equals("major")) {
                    finish();
                } else {
                    navigateToMainScreen();
                }
            }
        }
    }

    public class MyTsk extends AsyncTask {

        Context context;

        public MyTsk(Context context) {
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL("http://192.168.1.65/oms_storefront/api/" + "updateJson").openConnection();
                int result = -1;
                conn.connect();

                status = conn.getResponseCode();

                if (status == 200) {

                    InputStream is = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    StringBuilder buf = new StringBuilder();
                    String str;

                    while ((str = in.readLine()) != null) {
                        buf.append(str);
                        buf.append('\n');
                    }

                    in.close();

                    JSONObject json = new JSONObject(buf.toString());

                    if (json.getString(JSON_ERROR_CHECK).equals("YES")) {
                        return objects;
                    }

                    isError = true;
                    result = json.getInt(JSON_VERSION_CODE);
                    updateURL = json.getString(JSON_UPDATE_URL);
                    updateType = json.getString(JSON_UPDATE_TYPE);

                    try {
                        PackageManager manager = context.getPackageManager();
                        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                        int version = info.versionCode;
                        if (version < result) {

                            SplashActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (updateType.equalsIgnoreCase("major")) {

                                        showMajorUpdateDialog(SplashActivity.this, "Update available", "Please hit update button to update app.", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                // updateTheApplication(context, updateURL);
                                                new DownloadFile().execute(updateURL);

                                            }
                                        });

                                    } else {

                                        showMinorUpdateDialog(SplashActivity.this, "Update available", "Please hit update button to update app.", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                switch (i) {

                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        // updateTheApplication(context, updateURL);
                                                        new DownloadFile().execute(updateURL);
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        navigateToMainScreen();
                                                        break;

                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        } else {

                            navigateToMainScreen();

                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    // throw new RuntimeException(String.format("Received %d from server", status));
                }
            } catch (Exception e) {
                // e.printStackTrace();
            } finally {
                conn.disconnect();
            }

            return objects;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (isError) {
                if (status != 200) {
                    navigateToMainScreen();
                }
            } else {
                navigateToMainScreen();
            }


        }

        IntentFilter intentFilter;
        DownloadManager dm = null;

        private void updateTheApplication(Context ctx, String apkPath) {

            BroadcastReceiver receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    String action = intent.getAction();
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (downloadID == id) {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downloadID);
                        Cursor cursor = dm.query(query);
                        if (cursor.moveToFirst()) {

                            switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                                case DownloadManager.STATUS_SUCCESSFUL: {
                                    //common.showUserDefinedAlertType("SUCCESS",getActivity(),getContext(),"Success");
                                    break;
                                }
                                case DownloadManager.STATUS_FAILED: {
                                    // common.showUserDefinedAlertType("Download failed..!", getActivity(), getContext(), "Error");
                                    break;
                                }
                                default:
                                    break;
                            }
                        }

                    }
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                        try {
                            final PackageManager pm = context.getPackageManager();
                            String apkName = "Nilkamal_OMS.apk";
                            String fullPath = Environment.getExternalStorageDirectory().getPath() + "/Nilkamal_OMS/" + apkName;
                            PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
                            int versionNumber = info.versionCode;
                            String versionName = info.versionName;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                Uri contentUri = FileProvider.getUriForFile(context, "com.example.inventrax.falconOMS.provider",
                                        new File(Environment.getExternalStorageDirectory().getPath() + "/Nilkamal_OMS/Nilkamal_OMS.apk"));
                                Intent openFileIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                openFileIntent.setData(contentUri);
                                context.startActivity(openFileIntent);

                            } else {
                                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                intent1.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/Nilkamal_OMS/Nilkamal_OMS.apk")),
                                        "application/vnd.android.package-archive");
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                                context.startActivity(intent1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            if (intentFilter == null) {
                intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                ctx.registerReceiver(receiver, intentFilter);
            }
            if (dm == null) {
                dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            }

            String apkName = "Nilkamal_OMS.apk";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkPath));
            request.setTitle(ctx.getString(R.string.app_name));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //request.setDescription(ctx.getString(R.string.dont_cancel));
            request.setDescription("don't cancel");
            request.setDestinationInExternalPublicDir("/Nilkamal_OMS", apkName);
            dm.enqueue(request);

        }

    }

    public void showMajorUpdateDialog(Activity activity, String dialogTitle, String message, DialogInterface.OnClickListener onClickListener) {

        alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(dialogTitle);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Update", onClickListener);
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    public void showMinorUpdateDialog(Activity activity, String dialogTitle, String message, DialogInterface.OnClickListener onClickListener) {

        alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(dialogTitle);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Update", onClickListener);
        alertDialog.setNegativeButton("Cancel", onClickListener);
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    public void navigateToMainScreen() {
        try {

            if (sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_CUSTOMER_LOADED)) {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }

        } catch (Exception e) {

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }
    }

}