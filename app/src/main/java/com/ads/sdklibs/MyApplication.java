package com.ads.sdklibs;

import android.app.Application;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.sdk.adsconfig.AdsSdkConfig;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    AdsSdkConfig adsSdkConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        adsSdkConfig = new AdsSdkConfig(this);

        AdSettings.addTestDevice("c3d007fe-512e-4fa6-bebe-be2c10f5c7a5");
        List<String> testDeviceIds = new ArrayList<>();
        testDeviceIds.add("C6E50AD88D7DEFBE0E5FD7A1D201B94A");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
    }
}
