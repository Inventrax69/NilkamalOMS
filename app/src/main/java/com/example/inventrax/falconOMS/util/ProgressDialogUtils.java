package com.example.inventrax.falconOMS.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.inventrax.falconOMS.application.AbstractApplication;


public class ProgressDialogUtils {

    private static ProgressDialog progressDialog;

    public ProgressDialogUtils(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    public ProgressDialogUtils() {
        progressDialog = new ProgressDialog(AbstractApplication.get());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    public ProgressDialogUtils(Context context, int dialogStyle) {
        progressDialog = new ProgressDialog(context, dialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    public static void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public static void showProgressDialog(int progressStyle) {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.setProgressStyle(progressStyle);
        progressDialog.show();
    }

    public static void showProgressDialog(int progressStyle,String message) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(progressStyle);
            progressDialog.show();
        }
    }

    public static void showProgressDialog(String message) {
        try{
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        }catch (Exception e){
                //
        }

    }

    public static void closeProgressDialog() {
        try{
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }catch (Exception e){
            //
        }

    }

    public static ProgressDialog getProgressDialog(Context context) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            return progressDialog;
        } else {
            return progressDialog;
        }
    }

}
