package com.sdk.adsconfig.AudienceNetworkFbAds;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.sdk.adsconfig.AdsHelper;
import com.sdk.adsconfig.AdsSdkConfig;
import com.sdk.adsconfig.UnlockButton;
import com.sdk.adsconfig.admobads.AppOpenManager;

public class FbAdsSdk extends AdsSdkConfig {
    private final String TAG = "FbAdsSdk Log";
    Context context;
    AudienceNetworkAds.InitListener fbInitListener;

    private boolean isAdsRemove = false;
    private boolean initAdsFlg = false;

    public FbAdsSdk(Context context) {
        super(context);
        this.context = context;
        setPreLoadInterstitial();
        setPreLoadRewardVideo();
    }

    public FbAdsSdk(Context context, boolean isAdsRemove) {
        super(context);
        this.context = context;
        this.isAdsRemove = isAdsRemove;
        setPreLoadInterstitial();
        setPreLoadRewardVideo();
    }

    public FbAdsSdk(Context context, AudienceNetworkAds.InitListener fbInitListener) {
        super(context);
        this.context = context;
        this.fbInitListener = fbInitListener;
        setPreLoadInterstitial();
        setPreLoadRewardVideo();
    }

    public FbAdsSdk(Context context, AudienceNetworkAds.InitListener fbInitListener, boolean isAdsRemove) {
        super(context);
        this.context = context;
        this.fbInitListener = fbInitListener;
        this.isAdsRemove = isAdsRemove;
        setPreLoadInterstitial();
        setPreLoadRewardVideo();
    }

    private void mobileFbAdsInit() {
        if (getAdsType().equalsIgnoreCase("Facebook"))
            if (!initAdsFlg) {
                if (fbInitListener != null) {
                    if (!AudienceNetworkAds.isInitialized(context)) {
                    /*if (DEBUG) {
                        AdSettings.turnOnSDKDebugger(context);
                    }*/
                        AudienceNetworkAds
                                .buildInitSettings(context)
                                .withInitListener(fbInitListener)
                                .initialize();
                    }
                } else if (!AudienceNetworkAds.isInitialized(context)) {
                    AudienceNetworkAds.initialize(context);
                }
                initAdsFlg = true;
            }
    }

    //banner Ads
    //banner size ( BANNER_50 / BANNER_90 / RECTANGLE_HEIGHT_250 )
    public void fbBannerLoad(ViewGroup adContainer) {
        if (!isOnAdsSwitch()) {
            ((ViewGroup) adContainer.getParent()).removeView(adContainer);
            return;
        }
        if (isAdsRemove) {
            ((ViewGroup) adContainer.getParent()).removeView(adContainer);
            return;
        }
        mobileFbAdsInit();
        AdView adView = new AdView(context, getBannerId(), AdSize.BANNER_HEIGHT_50); //for testing : IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID
        adContainer.addView(adView);
        AdView.AdViewLoadConfig config = adView.buildLoadAdConfig().withAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "Banner onError : " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "Banner onAdLoaded ");
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.i(TAG, "Banner onAdClicked ");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.i(TAG, "Banner onLoggingImpression ");
            }
        }).build();
        adView.loadAd(config);
    }

    //interstitial ads
    private InterstitialAd interstitialAd;

    public void fbInterstitialBanner(Activity activity) {
        fbInterstitialBanner(activity, null, null);
    }

    public void fbInterstitialBanner(Activity activity, LoadAdsListener loadAdsListener) {
        fbInterstitialBanner(activity, loadAdsListener, null);
    }

    public void fbInterstitialBanner(Activity activity, LoadAdsListener loadAdsListener, InterstitialAdListener interstitialAdListener) {
        if (!isOnAdsSwitch()) {
            if (loadAdsListener != null)
                loadAdsListener.fail("Is Off ads");
            return;
        }
        if (isAdsRemove) {
            if (loadAdsListener != null)
                loadAdsListener.fail("Is Remove ads");
            return;
        }
        if (getAdsLoadType().equals(LoadTypeOnLoad)) {
            if (!isValidLoadInterstitial()) {
                if (loadAdsListener != null)
                    loadAdsListener.fail(breakTimeMsg);
                return;
            }
            mobileFbAdsInit();
            interstitialAd = new InterstitialAd(context, getInterstitialId());
            interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                    Log.e(TAG, "Interstitial ad displayed.");
                    if (interstitialAdListener != null)
                        interstitialAdListener.onInterstitialDisplayed(ad);
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    Log.e(TAG, "Interstitial ad dismissed.");
                    if (interstitialAdListener != null)
                        interstitialAdListener.onInterstitialDismissed(ad);
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                    if (loadAdsListener != null)
                        loadAdsListener.fail(adError.getErrorMessage());
                    if (interstitialAdListener != null)
                        interstitialAdListener.onError(ad, adError);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // an ad is loaded.
                    if (loadAdsListener != null)
                        loadAdsListener.loaded();
                    if (interstitialAdListener != null)
                        interstitialAdListener.onAdLoaded(ad);
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                    // Show the ad
                    interstitialAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    Log.d(TAG, "Interstitial ad clicked!");
                    if (interstitialAdListener != null)
                        interstitialAdListener.onAdClicked(ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d(TAG, "Interstitial ad impression logged!");
                    if (interstitialAdListener != null)
                        interstitialAdListener.onLoggingImpression(ad);
                }
            }).build());
        } else {
            if (!isValidLoadInterstitial()) {
                if (loadAdsListener != null)
                    loadAdsListener.fail(breakTimeMsg);
                return;
            }
            if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                if (loadAdsListener != null)
                    loadAdsListener.loaded();

                interstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        // Interstitial ad displayed callback
                        Log.e(TAG, "Interstitial ad displayed.");
                        AppOpenManager.isShowingAd = true;
                        if (interstitialAdListener != null)
                            interstitialAdListener.onInterstitialDisplayed(ad);
                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        // Interstitial dismissed callback
                        Log.e(TAG, "Interstitial ad dismissed.");
                        if (interstitialAdListener != null)
                            interstitialAdListener.onInterstitialDismissed(ad);
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        // Ad error callback
                        Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                        if (loadAdsListener != null)
                            loadAdsListener.fail(adError.getErrorMessage());
                        if (interstitialAdListener != null)
                            interstitialAdListener.onError(ad, adError);
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                        // an ad is loaded.
                        if (loadAdsListener != null) {
                            loadAdsListener.loaded();
                        }
                        if (interstitialAdListener != null) {
                            interstitialAdListener.onAdLoaded(ad);
                        }
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Ad clicked callback
                        Log.d(TAG, "Interstitial ad clicked!");
                        if (interstitialAdListener != null)
                            interstitialAdListener.onAdClicked(ad);
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Ad impression logged callback
                        Log.d(TAG, "Interstitial ad impression logged!");
                        if (interstitialAdListener != null)
                            interstitialAdListener.onLoggingImpression(ad);
                    }
                });
                interstitialAd.show();
            } else {
                if (loadAdsListener != null)
                    loadAdsListener.fail("Interstitial Ads is not pre loaded!");
                setPreLoadInterstitial();
            }
        }
    }

    public void showFbInterstitialAdsWithNext(Context context, Activity activity, PerformAction performAction) {
        AdsHelper.PleaseWaitShow(context);
        this.fbInterstitialBanner(activity, new LoadAdsListener() {
            @Override
            public void loaded() {
                AdsHelper.dismissDialog();
            }

            @Override
            public void fail(String msg) {
                AdsHelper.dismissDialog();
                Log.e(TAG, "AdLoad " + activity.getLocalClassName() + " Fail : " + msg);
                performAction.next();
            }
        }, new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                performAction.next();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                performAction.next();
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
    }

    private void setPreLoadInterstitial() {
        if (!isOnAdsSwitch()) {
            return;
        }
        if (isAdsRemove) {
            return;
        }
        if (getAdsType().equalsIgnoreCase("Facebook"))
            if (getAdsLoadType().equals(LoadTypePreLoad)) {
                mobileFbAdsInit();

                interstitialAd = new InterstitialAd(context, getInterstitialId());
                interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().build());
            }
    }

    //reward ads
    private RewardedVideoAd rewardedVideoAd;
    private boolean earnReward = false;

    public void fbRewardVideoAds(LoadAdsListener loadAdsListener, ActionListener actionListener) {
        fbRewardVideoAds(loadAdsListener, actionListener, null);
    }

    public void fbRewardVideoAds(LoadAdsListener loadAdsListener, ActionListener actionListener, RewardedVideoAdListener rewardedVideoAdListener) {
        earnReward = false;
        if (!isOnAdsSwitch()) {
            loadAdsListener.fail("Is Ads Off");
            return;
        }
        if (isAdsRemove) {
            loadAdsListener.fail("Is AdsRemove");
            return;
        }
        if (getAdsLoadType().equals(LoadTypeOnLoad)) {
            if (!isValidLoadRewardVideo()) {
                loadAdsListener.fail(breakTimeMsg);
                return;
            }
            mobileFbAdsInit();
            rewardedVideoAd = new RewardedVideoAd(context, getRewardId());
            Log.e(TAG, "reward id : " + getRewardId());
            rewardedVideoAd.loadAd(rewardedVideoAd.buildLoadAdConfig().withAdListener(new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, AdError error) {
                    // Rewarded video ad failed to load
                    Log.e(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
                    if (loadAdsListener != null)
                        loadAdsListener.fail(error.getErrorMessage());
                    if (rewardedVideoAdListener != null)
                        rewardedVideoAdListener.onError(ad, error);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Rewarded video ad is loaded and ready to be displayed
                    Log.d(TAG, "Rewarded video ad is loaded and ready to be displayed!");
                    if (loadAdsListener != null)
                        loadAdsListener.loaded();
                    if (rewardedVideoAdListener != null)
                        rewardedVideoAdListener.onAdLoaded(ad);
                    rewardedVideoAd.show();
                    AppOpenManager.isShowingAd = true;
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Rewarded video ad clicked
                    Log.d(TAG, "Rewarded video ad clicked!");
                    if (rewardedVideoAdListener != null)
                        rewardedVideoAdListener.onAdClicked(ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Rewarded Video ad impression - the event will fire when the
                    // video starts playing
                    Log.d(TAG, "Rewarded video ad impression logged!");
                    if (rewardedVideoAdListener != null)
                        rewardedVideoAdListener.onLoggingImpression(ad);
                    AppOpenManager.isShowingAd = true;
                }

                @Override
                public void onRewardedVideoCompleted() {
                    // Rewarded Video View Complete - the video has been played to the end.
                    // You can use this event to initialize your reward
                    Log.d(TAG, "Rewarded video completed!");
                    if (rewardedVideoAdListener != null)
                        rewardedVideoAdListener.onRewardedVideoCompleted();
                    AppOpenManager.isShowingAd = false;
                    earnReward = true;
                    // Call method to give reward
                    // giveReward();
                }

                @Override
                public void onRewardedVideoClosed() {
                    // The Rewarded Video ad was closed - this can occur during the video
                    // by closing the app, or closing the end card.
                    Log.d(TAG, "Rewarded video ad closed!");
                    if (rewardedVideoAdListener != null)
                        rewardedVideoAdListener.onRewardedVideoClosed();
                    AppOpenManager.isShowingAd = false;
                    if (earnReward)
                        actionListener.action(rewardStatus.REWARD_VIDEO_DONE);
                    else
                        actionListener.action(rewardStatus.REWARD_VIDEO_NOT_DONE);

                    setPreLoadRewardVideo();
                }
            }).build());

        } else {
            if (!isValidLoadRewardVideo()) {
                loadAdsListener.fail(breakTimeMsg);
                return;
            }
            if (rewardedVideoAd != null && rewardedVideoAd.isAdLoaded()) {
                if (loadAdsListener != null)
                    loadAdsListener.loaded();

                rewardedVideoAd.buildLoadAdConfig().withAdListener(new RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {
                        // Rewarded Video View Complete - the video has been played to the end.
                        // You can use this event to initialize your reward
                        Log.d(TAG, "Rewarded video completed!");
                        if (rewardedVideoAdListener != null)
                            rewardedVideoAdListener.onRewardedVideoCompleted();
                        AppOpenManager.isShowingAd = false;
                        earnReward = true;
                        // Call method to give reward
                        // giveReward();
                    }

                    @Override
                    public void onRewardedVideoClosed() {
                        // The Rewarded Video ad was closed - this can occur during the video
                        // by closing the app, or closing the end card.
                        Log.d(TAG, "Rewarded video ad closed!");
                        if (rewardedVideoAdListener != null)
                            rewardedVideoAdListener.onRewardedVideoClosed();
                        AppOpenManager.isShowingAd = false;
                        if (earnReward)
                            actionListener.action(rewardStatus.REWARD_VIDEO_DONE);
                        else
                            actionListener.action(rewardStatus.REWARD_VIDEO_NOT_DONE);

                        setPreLoadRewardVideo();
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        // Rewarded video ad failed to load
                        Log.e(TAG, "Rewarded video ad failed to load: " + adError.getErrorMessage());
                        if (loadAdsListener != null)
                            loadAdsListener.fail(adError.getErrorMessage());
                        if (rewardedVideoAdListener != null)
                            rewardedVideoAdListener.onError(ad, adError);
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        // Rewarded video ad is loaded and ready to be displayed
                        Log.d(TAG, "Rewarded video ad is loaded and ready to be displayed!");
                        if (loadAdsListener != null)
                            loadAdsListener.loaded();
                        if (rewardedVideoAdListener != null)
                            rewardedVideoAdListener.onAdLoaded(ad);
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Rewarded video ad clicked
                        Log.d(TAG, "Rewarded video ad clicked!");
                        if (rewardedVideoAdListener != null)
                            rewardedVideoAdListener.onAdClicked(ad);
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Rewarded Video ad impression - the event will fire when the
                        // video starts playing
                        Log.d(TAG, "Rewarded video ad impression logged!");
                        if (rewardedVideoAdListener != null)
                            rewardedVideoAdListener.onLoggingImpression(ad);
                        AppOpenManager.isShowingAd = true;
                    }
                });
                rewardedVideoAd.show();
            } else {
                if (loadAdsListener != null)
                    loadAdsListener.fail("Reward Ads is not pre loaded!");
                setPreLoadRewardVideo();
            }
        }
    }

    public void fbRewardVideoAdsWithNext(Context context, PerformAction performAction) {
        if (this.checkRewardVideoAvailable()) {
            UnlockButton.unlockSet(context, new UnlockButton.UnlockButtonListener() {
                @Override
                public void unlock() {
                    AdsHelper.PleaseWaitShow(context);
                    fbRewardVideoAds(new LoadAdsListener() {
                        @Override
                        public void loaded() {
                            AdsHelper.dismissDialog();
                        }

                        @Override
                        public void fail(String msg) {
                            AdsHelper.dismissDialog();
                            performAction.next();

                        }
                    }, new ActionListener() {
                        @Override
                        public void action(rewardStatus code) {
                            if (code.equals(rewardStatus.REWARD_VIDEO_DONE)) {
                                performAction.next();
                            }
                        }
                    });
                }
            });
        } else {
            performAction.next();
        }
    }


    private void setPreLoadRewardVideo() {
        if (!isOnAdsSwitch()) {
            return;
        }
        if (isAdsRemove) {
            return;
        }
        if (getAdsType().equalsIgnoreCase("Facebook"))
            if (getAdsLoadType().equals(LoadTypePreLoad)) {
                mobileFbAdsInit();

                rewardedVideoAd = new RewardedVideoAd(context, getRewardId());
                rewardedVideoAd.loadAd(rewardedVideoAd.buildLoadAdConfig().build());
            }
    }

    //native
    public NativeAdLayout getFbNativeAdLayout(Context context) {
        NativeAdLayout nativeAdLayout = new NativeAdLayout(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nativeAdLayout.setLayoutParams(layoutParams);
        return nativeAdLayout;
    }

    //    native banner
    public void fbNativeBannerAdsLoadSingle(NativeAdLayout nativeAdLayout) {
        fbNativeBannerAdsLoadSingle(nativeAdLayout, null, NativeUse.USE_FIX_IN_PAGE);
    }

    public void fbNativeBannerAdsLoadSingle(NativeAdLayout nativeAdLayout, NativeUse use) {
        fbNativeBannerAdsLoadSingle(nativeAdLayout, null, use);
    }

    public void fbNativeBannerAdsLoadSingle(NativeAdLayout nativeAdLayout, LoadAdsListener loadAdsListener, NativeUse use) {
        if (getNativeStatusApi().equals("D") || (getNativeStatusApi().equals("E") && !use.equals(NativeUse.USE_FIX_IN_PAGE))) {
            ((ViewGroup) nativeAdLayout.getParent()).removeView(nativeAdLayout);
            return;
        }
        if (!isOnAdsSwitch()) {
            ((ViewGroup) nativeAdLayout.getParent()).removeView(nativeAdLayout);
            return;
        }
        if (isAdsRemove) {
            ((ViewGroup) nativeAdLayout.getParent()).removeView(nativeAdLayout);
            return;
        }
        mobileFbAdsInit();
        NativeBannerAd nativeBannerAd = new NativeBannerAd(context, getNativeId());
        nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (loadAdsListener != null)
                    loadAdsListener.fail(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (loadAdsListener != null)
                    loadAdsListener.loaded();
                new FacebookNativeInflated(context).inflateAd(nativeBannerAd, nativeAdLayout);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        }).build());
    }

    //    native size
    public void fbNativeSizeAdsLoadSingle(NativeAdLayout nativeAdLayout) {
        fbNativeSizeAdsLoadSingle(nativeAdLayout, null, NativeUse.USE_FIX_IN_PAGE, -1);
    }

    public void fbNativeSizeAdsLoadSingle(NativeAdLayout nativeAdLayout, NativeUse use) {
        fbNativeSizeAdsLoadSingle(nativeAdLayout, null, use, -1);
    }

    public void fbNativeSizeAdsLoadSingle(NativeAdLayout nativeAdLayout, LoadAdsListener loadAdsListener, NativeUse use) {
        fbNativeSizeAdsLoadSingle(nativeAdLayout, loadAdsListener, use, -1);
    }

    public void fbNativeSizeAdsLoadSingle(NativeAdLayout nativeAdLayout, LoadAdsListener loadAdsListener, NativeUse use, int cusRes) {
        if (getNativeStatusApi().equals("D") || (getNativeStatusApi().equals("E") && !use.equals(NativeUse.USE_FIX_IN_PAGE))) {
            ((ViewGroup) nativeAdLayout.getParent()).removeView(nativeAdLayout);
            return;
        }
        if (!isOnAdsSwitch()) {
            ((ViewGroup) nativeAdLayout.getParent()).removeView(nativeAdLayout);
            return;
        }
        if (isAdsRemove) {
            ((ViewGroup) nativeAdLayout.getParent()).removeView(nativeAdLayout);
            return;
        }
        mobileFbAdsInit();
        NativeAd nativeAd = new NativeAd(context, getNativeId());
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withMediaCacheFlag(NativeAdBase.MediaCacheFlag.NONE).withAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (loadAdsListener != null)
                    loadAdsListener.fail(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (loadAdsListener != null)
                    loadAdsListener.loaded();
                if (cusRes == -1)
                    new FacebookNativeInflated(context).inflateAd(nativeAd, nativeAdLayout);
                else
                    new FacebookNativeInflated(context).inflateAd(nativeAd, nativeAdLayout, cusRes);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        }).build());
    }

    //    https://developers.google.com/admob/android/native/start#loading_ads
    private final int maxNativeRequestSize = 5; // The loadAds() method sends a request for multiple ads (up to 5):

    public boolean isAdsRemove() {
        return isAdsRemove;
    }

}
