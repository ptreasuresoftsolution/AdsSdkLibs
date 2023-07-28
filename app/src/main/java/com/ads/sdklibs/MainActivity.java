package com.ads.sdklibs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.sdk.adsconfig.AdsHelper;
import com.sdk.adsconfig.AdsSdkConfig;
import com.sdk.adsconfig.AudienceNetworkFbAds.FacebookNativeInflated;

public class MainActivity extends AppCompatActivity {
    AdsSdkConfig adsSdkConfig;
    LinearLayout ad_view;

    private NativeBannerAd nativeBannerAd;

    private NativeAdLayout nativeBannerAdLayout;

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