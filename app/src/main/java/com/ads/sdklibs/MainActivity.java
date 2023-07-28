package com.ads.sdklibs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.sdk.adsconfig.AdsSdkConfig;

public class MainActivity extends AppCompatActivity {
    AdsSdkConfig adsSdkConfig;
    LinearLayout ad_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adsSdkConfig = new AdsSdkConfig(this);
        adsSdkConfig.setAdsType("Facebook");
        adsSdkConfig.setAdsSwitch("1");//Ads On for debug
        adsSdkConfig.setUnitIds("IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID",
                "YOUR_PLACEMENT_ID",
                "YOUR_PLACEMENT_ID",
                "YOUR_PLACEMENT_ID",
                "");

        adsSdkConfig.initAds();
        ad_view = findViewById(R.id.ad_view);
        adsSdkConfig.loadNativeBanner(this, ad_view);

    }
}