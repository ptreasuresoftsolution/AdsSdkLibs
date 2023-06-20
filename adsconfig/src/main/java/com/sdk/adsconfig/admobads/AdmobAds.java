package com.sdk.adsconfig.admobads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.sdk.adsconfig.AdsHelper;
import com.sdk.adsconfig.AdsSdkConfig;
import com.sdk.adsconfig.UnlockButton;


//Modify date : 29-May-2023
//version = 4.0
//marge with fb
/**
 * in banner ads ----
 * <application android:hardwareAccelerated="true">
 * <!-- For activities that use ads, hardwareAcceleration should be true. -->
 * <activity android:hardwareAccelerated="true" />
 * <!-- For activities that don't use ads, hardwareAcceleration can be false. -->
 * <activity android:hardwareAccelerated="false" />
 * </application>
 * ----- End banner ads
 **/

/****
 required manifest code for ads ------
 <meta-data
 android:name="com.google.android.gms.ads.APPLICATION_ID"
 android:value="@string/APP_id" />
 ------ end required code
 **/

public class AdmobAds extends AdsSdkConfig {
    private final String TAG = "AdMobAds Log";
    Context context;
    private AppOpenManager appOpenManager;
    OnInitializationCompleteListener onInitializationCompleteListener;
    private AdRequest adRequestInterstitialAd, adRequestRewardAd;
    private InterstitialAd mInterstitialAd;
    private boolean isAdsRemove = false;
    private boolean initAdsFlg = false;

    //open ads
    public void activeOpenAds(Application application) {
        if (!isOnAdsSwitch())
            return;
        if (isAdsRemove)
            return;
        appOpenManager = new AppOpenManager(application, this);
    }

    //banner ads
    public void adBannerLoad(ViewGroup adContainer) {
        if (!isOnAdsSwitch()) {
            ((ViewGroup) adContainer.getParent()).removeView(adContainer);
            return;
        }
        if (isAdsRemove) {
            ((ViewGroup) adContainer.getParent()).removeView(adContainer);
            return;
        }
        mobileAdsInit();
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getBannerId());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.i(TAG, "Banner onAdClicked ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "Banner onError : " + loadAdError.getMessage());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.i(TAG, "Banner onAdImpression ");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "Banner onAdLoaded ");
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adContainer.addView(adView);
        adView.loadAd(adRequest);
    }

    //native ads
    /*
    <com.infotech.simba.mahakalapp.util.TemplateView
        android:id="@+id/ad_view"
        android:layout_width="match_parent"
        android:visibility="gone"
        android:layout_height="wrap_content"
        app:gnt_template_type="@layout/gnt_small_template_view" />
    */
    public TemplateView getTemplateView(Context context,int layoutRes) {
        TemplateView templateView = new TemplateView(context);
        templateView.setTemplateType(context,layoutRes);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        templateView.setLayoutParams(layoutParams);
        templateView.onFinishInflate();
        return templateView;
    }

    public void nativeAdsLoadSingle(TemplateView adView) {
        nativeAdsLoadSingle(adView, null, NativeUse.USE_FIX_IN_PAGE);
    }

    public void nativeAdsLoadSingle(TemplateView adView, NativeUse use) {
        nativeAdsLoadSingle(adView, null, use);
    }

    public void nativeAdsLoadSingle(TemplateView adView, LoadAdsListener loadAdsListener, NativeUse use) {
        if (getNativeStatusApi().equals("D") || (getNativeStatusApi().equals("E") && !use.equals(NativeUse.USE_FIX_IN_PAGE))) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            return;
        }
        if (!isOnAdsSwitch()) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            return;
        }
        if (isAdsRemove) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            return;
        }
        mobileAdsInit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            AdLoader adLoader = new AdLoader.Builder(context, getNativeId())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            setNativeAdvance(adView, nativeAd);
                            if (loadAdsListener != null)
                                loadAdsListener.loaded();
                        }
                    })
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    public void nativeAdsLoadAdvanceMultipleQueueAds(int adRequireRequestSize, LoadAdsListener loadAdsListener, TemplateView adView) {
        nativeAdsLoadAdvanceMultipleQueueAds(adRequireRequestSize, loadAdsListener, adView, NativeUse.USE_FIX_IN_PAGE);
    }

    public void nativeAdsLoadAdvanceMultipleQueueAds(int adRequireRequestSize, LoadAdsListener loadAdsListener, TemplateView adView, NativeUse use) {
        if (getNativeStatusApi().equals("D") || (getNativeStatusApi().equals("E") && !use.equals(NativeUse.USE_FIX_IN_PAGE))) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            return;
        }
        if (!isOnAdsSwitch()) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            return;
        }
        if (isAdsRemove) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            return;
        }
        if (adRequireRequestSize > maxNativeRequestSize)
            adRequireRequestSize = maxNativeRequestSize;
        final int adRequestSize = adRequireRequestSize;

        mobileAdsInit();
        AdLoader adLoader;
        AdLoader.Builder builder = new AdLoader.Builder(context, getNativeId())
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        setNativeAdvance(adView, nativeAd);
                        if (loadAdsListener != null)
                            loadAdsListener.loaded();
                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        loadAdsListener.fail(loadAdError.getMessage());
                    }
                });
        adLoader = builder.build();
        adLoader.loadAds(new AdRequest.Builder().build(), adRequestSize);
    }

    private void setNativeAdvance(TemplateView adView, NativeAd nativeAd) {
        NativeTemplateStyle styles = new
                NativeTemplateStyle.Builder().build();
        adView.setStyles(styles);
        adView.setNativeAd(nativeAd);
    }

    //interstitial ads
    public void showInterstitialBanner(Activity activity) {
        showInterstitialBanner(activity, null, null);
    }

    public void showInterstitialBanner(Activity activity, LoadAdsListener loadAdsListener) {
        showInterstitialBanner(activity, loadAdsListener, null);
    }

    public void showInterstitialBanner(Activity activity, LoadAdsListener loadAdsListener, FullScreenContentCallback fullScreenContentCallback) {
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
            mobileAdsInit();
            adRequestInterstitialAd = new AdRequest.Builder().build();
            InterstitialAd.load(context, getInterstitialId(), adRequestInterstitialAd, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    if (loadAdsListener != null)
                        loadAdsListener.loaded();
                    mInterstitialAd = interstitialAd;

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                            if (fullScreenContentCallback != null)
                                fullScreenContentCallback.onAdImpression();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            AppOpenManager.isShowingAd = false;
                            if (fullScreenContentCallback != null)
                                fullScreenContentCallback.onAdDismissedFullScreenContent();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            if (fullScreenContentCallback != null)
                                fullScreenContentCallback.onAdFailedToShowFullScreenContent(adError);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            AppOpenManager.isShowingAd = true;
                            if (fullScreenContentCallback != null)
                                fullScreenContentCallback.onAdShowedFullScreenContent();
                        }
                    });

                    mInterstitialAd.show(activity);
                    Log.i(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.getMessage());
                    if (loadAdsListener != null)
                        loadAdsListener.fail(loadAdError.getMessage());
                    mInterstitialAd = null;
                }
            });
        } else {
            if (!isValidLoadInterstitial()) {
                if (loadAdsListener != null)
                    loadAdsListener.fail(breakTimeMsg);
                return;
            }
            if (mInterstitialAd != null) {
                if (loadAdsListener != null)
                    loadAdsListener.loaded();
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        if (fullScreenContentCallback != null)
                            fullScreenContentCallback.onAdImpression();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        AppOpenManager.isShowingAd = false;
                        if (fullScreenContentCallback != null)
                            fullScreenContentCallback.onAdDismissedFullScreenContent();
                        setPreLoadInterstitial();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        if (fullScreenContentCallback != null)
                            fullScreenContentCallback.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        AppOpenManager.isShowingAd = true;
                        if (fullScreenContentCallback != null)
                            fullScreenContentCallback.onAdShowedFullScreenContent();
                    }
                });
                mInterstitialAd.show(activity);
            } else {
                if (loadAdsListener != null)
                    loadAdsListener.fail("Interstitial Ads is not pre loaded!");
                setPreLoadInterstitial();
            }
        }
    }

    /*
    admobAds.showInterstitialAdsWithNext(MainActivity.this, MainActivity.this, new AdmobAds.PerformAction() {
        @Override
        public void next() {
            Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
            intent.putExtra("which", "SPLIT");
            intent.putExtra("fromWhich", "main");
            startActivity(intent);
        }
    });
    */
    public void showInterstitialAdsWithNext(Context context, Activity activity, PerformAction performAction) {
        AdsHelper.PleaseWaitShow(context);

        Log.e(TAG, "inst id : " + getInterstitialId());
        this.showInterstitialBanner(activity, new LoadAdsListener() {
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
        }, new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                performAction.next();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                performAction.next();
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
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            if (getAdsLoadType().equals(LoadTypePreLoad)) {
                mobileAdsInit();
                adRequestInterstitialAd = new AdRequest.Builder().build();
                InterstitialAd.load(context, getInterstitialId(), adRequestInterstitialAd, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "Preload Interstitial onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
            }
    }


    //reward ads
    private RewardedAd mRewardedAd;
    private boolean earnReward = false;

    public void loadRewardVideoAds(LoadAdsListener loadAdsListener, ActionListener actionListener, Activity activity) {
        loadRewardVideoAds(loadAdsListener, actionListener, activity, null);
    }

    public void loadRewardVideoAds(LoadAdsListener loadAdsListener, ActionListener actionListener, Activity activity, FullScreenContentCallback fullScreenContentCallback) {
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
            adRequestRewardAd = new AdRequest.Builder().build();
            RewardedAd.load(context, getRewardId(), adRequestRewardAd, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error.
                    Log.e(TAG, loadAdError.getMessage());
                    mRewardedAd = null;
                    if (loadAdsListener != null)
                        loadAdsListener.fail(loadAdError.getMessage());
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mRewardedAd = rewardedAd;
                    if (loadAdsListener != null)
                        loadAdsListener.loaded();
                    _LoadRewardVideo(actionListener, fullScreenContentCallback, activity);
                    Log.e(TAG, "Ad was loaded.");
                }
            });
        } else {
            if (!isValidLoadRewardVideo()) {
                loadAdsListener.fail(breakTimeMsg);
                return;
            }
            if (mRewardedAd != null) {
                if (loadAdsListener != null)
                    loadAdsListener.loaded();
                _LoadRewardVideo(actionListener, fullScreenContentCallback, activity);
            } else {
                if (loadAdsListener != null)
                    loadAdsListener.fail("Reward Ads is not pre loaded!");
                setPreLoadRewardVideo();
            }
        }
    }

    private void _LoadRewardVideo(ActionListener actionListener, FullScreenContentCallback fullScreenContentCallback, Activity activity) {
        earnReward = false;
        if (mRewardedAd != null) {
            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    if (fullScreenContentCallback != null)
                        fullScreenContentCallback.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.e(TAG, "Ad was shown.");
                    AppOpenManager.isShowingAd = true;
                    if (fullScreenContentCallback != null)
                        fullScreenContentCallback.onAdShowedFullScreenContent();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show.");
                    if (fullScreenContentCallback != null)
                        fullScreenContentCallback.onAdFailedToShowFullScreenContent(adError);
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.e(TAG, "Ad was dismissed. " + mRewardedAd.getRewardItem().getAmount());
                    AppOpenManager.isShowingAd = false;
                    if (fullScreenContentCallback != null)
                        fullScreenContentCallback.onAdDismissedFullScreenContent();
                    mRewardedAd = null;
                    if (earnReward)
                        actionListener.action(rewardStatus.REWARD_VIDEO_DONE);
                    else
                        actionListener.action(rewardStatus.REWARD_VIDEO_NOT_DONE);

                    setPreLoadRewardVideo();
                }
            });
            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.e(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    earnReward = true;
                }
            });
        } else {
            Log.e(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    /*
    admobAds.showRewardVideoAdsWithNext(MainActivity.this, MainActivity.this, new AdmobAds.PerformAction() {
        @Override
        public void next() {
            Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
            intent.putExtra("which", "VIDEOMERGE");
            intent.putExtra("fromWhich", "main");
            startActivity(intent);
        }
     });
     */

    public void showRewardVideoAdsWithNext(Context context, Activity activity, PerformAction performAction) {
        if (this.checkRewardVideoAvailable()) {
            UnlockButton.unlockSet(context, new UnlockButton.UnlockButtonListener() {
                @Override
                public void unlock() {
                    AdsHelper.PleaseWaitShow(context);
                    AdmobAds.this.loadRewardVideoAds(new LoadAdsListener() {
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
                    }, activity);
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
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            if (getAdsLoadType().equals(LoadTypePreLoad)) {
                adRequestRewardAd = new AdRequest.Builder().build();
                RewardedAd.load(context, getRewardId(), adRequestRewardAd, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.e(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.e(TAG, "Preload RewardVideo onAdLoaded");
                    }
                });
            }
    }


    public AdmobAds(Context context) {
        super(context);
        this.context = context;
        setPreLoadInterstitial();
        setPreLoadRewardVideo();
    }

    public AdmobAds(Context context, boolean isAdsRemove) {
        super(context);
        this.context = context;
        this.isAdsRemove = isAdsRemove;
        setPreLoadInterstitial();
        setPreLoadRewardVideo();
    }

    public AdmobAds(Context context, OnInitializationCompleteListener onInitializationCompleteListener, boolean isAdsRemove) {
        super(context);
        this.context = context;
        this.onInitializationCompleteListener = onInitializationCompleteListener;
        this.isAdsRemove = isAdsRemove;
        setPreLoadInterstitial();
        setPreLoadRewardVideo();
    }

    public AdmobAds(Context context, OnInitializationCompleteListener onInitializationCompleteListener) {
        super(context);
        this.context = context;
        this.onInitializationCompleteListener = onInitializationCompleteListener;
        setPreLoadInterstitial();
        setPreLoadRewardVideo();
    }

    protected void mobileAdsInit() {
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            if (!initAdsFlg) {
                if (onInitializationCompleteListener != null)
                    MobileAds.initialize(context, onInitializationCompleteListener);
                else
                    MobileAds.initialize(context);
                initAdsFlg = true;
            }
        }
    }

    //    https://developers.google.com/admob/android/native/start#loading_ads
    private final int maxNativeRequestSize = 5; // The loadAds() method sends a request for multiple ads (up to 5):

    public boolean isAdsRemove() {
        return isAdsRemove;
    }


}
