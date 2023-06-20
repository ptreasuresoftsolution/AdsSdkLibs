package com.ads.sdklibs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sdk.adsconfig.AdsHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdsHelper adsHelper = new AdsHelper();
    }
}