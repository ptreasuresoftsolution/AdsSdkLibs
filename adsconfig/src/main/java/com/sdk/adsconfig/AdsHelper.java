package com.sdk.adsconfig;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.List;

public class AdsHelper {
    static ProgressDialog progressDialog;
    public static void PleaseWaitShow(Context context) {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                AdsHelper.dismissDialog();
            }
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public static void PleaseWaitShowMessage(String message) {
        try {
            progressDialog.setMessage(message);
        } catch (Exception e) {
            Log.e("Exception", "Update Progress Alert", e);
        }
    }

    public static void dismissDialog() {
        try {
            if (progressDialog != null)
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
        } catch (Exception e) {
        }

    }
    public static void addTestDeviceFb(String deviceIdHash){
        AdSettings.addTestDevice(deviceIdHash);
    }
    public static void setTestDeviceAdMob(List<String> testDeviceIds){
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
    }
}
