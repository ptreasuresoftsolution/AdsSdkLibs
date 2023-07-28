package com.sdk.adsconfig;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.sdk.adsconfig.AudienceNetworkFbAds.FbAdsSdk;
import com.sdk.adsconfig.admobads.AdmobAds;
import com.sdk.adsconfig.admobads.TemplateView;

import java.util.Calendar;

//Modify date : 29-May-2023
//version = 1.0
//marge with fb
public class AdsSdkConfig {
    private final String TAG = "AdsSdkConfig Log";
    private SharedPreferences adsPreferences;
    private SharedPreferences.Editor editor;
    Context context;
    public final static String breakTimeMsg = "(ads) Is Break times";

    public enum rewardStatus {REWARD_VIDEO_DONE, REWARD_VIDEO_NOT_DONE}

    public enum NativeUse {USE_FIX_IN_PAGE, USE_IN_LIST, USE_IN_GRID}

    interface AdsInitListener {
        void onInitialized(String st);
    }

    public interface PerformAction {
        void next();
    }

    public interface ActionListener {
        void action(rewardStatus code);
    }

    public interface LoadAdsListener {
        void loaded();

        void fail(String msg);
    }

    public interface GetNativeAds {
        NativeAd getAds(int index);

        void setAds(int index, TemplateView adView);
    }

    public AdsSdkConfig(Context context) {
        this.context = context;
        adsPreferences = context.getSharedPreferences("ads_config_preferences", Context.MODE_PRIVATE);
    }

    public AdsSdkConfig(Context context, boolean isAdsRemove) {
        this.context = context;
        this.isAdsRemove = isAdsRemove;
        adsPreferences = context.getSharedPreferences("ads_config_preferences", Context.MODE_PRIVATE);
    }

    private final long timePeriodInterstitial = 1000 * 45; // in milli second in continuous load gap
    //    private final long timePeriodInterstitial = 1000 * 60 * 2; // in milli second in continuous load gap
    private final long breakTimePeriod = 1000 * 60 * 5; // in milli second (break time of video,interstitial banner an same of all)
    private final long maxCountShowReward = 8; // times to load video continuous then after break, and after break... return to load
    private final long nativeGridGap = 9; // Native ads load in gridview with this item gap
    private final long nativeListGap = 5; // Native ads load in listview with this item gap
    private final String nativeStatus = "A"; // Some is status of native ads (A-> All On / E-> Fix On / D-> All Off) A is (on in list and fix)

    private boolean isAdsRemove = false;

    public boolean isAdsRemove() {
        return isAdsRemove;
    }

    private final static String timePeriodInterstitialApi = "timePeriodInterstitial";
    private final static String breakTimePeriodApi = "breakTimePeriod";
    private final static String maxCountShowRewardApi = "maxCountShowReward";
    private final static String nativeGridGapApi = "native_grid_gap";
    private final static String nativeListGapApi = "native_list_gap";
    private final static String nativeStatusApi = "native_status";

    private final static String adsSwitch = "ads_switch";
    private final static String adsType = "ads_type";
    private final static String loadType = "ads_load_type";
    protected final static String LoadTypeOnLoad = "Onload";
    protected final static String LoadTypePreLoad = "Preload";

    //ad-ids keys
    private final static String appAdsId = "app_ads_id";
    private final static String bannerId = "banner_id";
    private final static String nativeId = "native_id";
    private final static String interstitialId = "interstitial_id";
    private final static String rewardId = "reward_id";
    private final static String openId = "open_id";

    private final String TIME_INTERSTITIAL_KEY = "time_interstitial_key";
    private final String TIME_REWARD_VIDEO_KEY = "time_reward_video_key";
    private final String COUNT_REWARD_VIDEO_KEY = "count_reward_video_key";

    private void setPrefVal(String key, String val) {
        editor = adsPreferences.edit();
        editor.putString(key, val);
        editor.commit();
    }

    private String getPrefVal(String key) {
        return adsPreferences.getString(key, "0");
    }

    public void setAdsConfig(String pTimePeriodInterstitial, String pBreakTimePeriod, String pMaxCountShowReward, String pNativeGridGap, String pNativeListGap, String pNativeStatus) {
        editor = adsPreferences.edit();
        editor.putLong(timePeriodInterstitialApi, Long.parseLong(pTimePeriodInterstitial));
        editor.putLong(breakTimePeriodApi, Long.parseLong(pBreakTimePeriod));
        editor.putLong(maxCountShowRewardApi, Long.parseLong(pMaxCountShowReward));
        editor.putLong(nativeGridGapApi, Long.parseLong(pNativeGridGap));
        editor.putLong(nativeListGapApi, Long.parseLong(pNativeListGap));
        editor.putString(nativeStatusApi, pNativeStatus);
        editor.commit();
    }

    public void setUnitIds(String banner_id, String native_id, String interstitial_id, String reward_id, String open_id) {
        editor = adsPreferences.edit();
        editor.putString(bannerId, banner_id);
        editor.putString(nativeId, native_id);
        editor.putString(interstitialId, interstitial_id);
        editor.putString(rewardId, reward_id);
        editor.putString(openId, open_id);
        editor.commit();
    }

    public long getTimePeriodInterstitialApi() {
        return adsPreferences.getLong(timePeriodInterstitialApi, timePeriodInterstitial);
    }

    public long getBreakTimePeriodApi() {
        return adsPreferences.getLong(breakTimePeriodApi, breakTimePeriod);
    }

    public long getMaxCountShowRewardApi() {
        return adsPreferences.getLong(maxCountShowRewardApi, maxCountShowReward);
    }

    public long geNativeGridGapApi() {
        return adsPreferences.getLong(nativeGridGapApi, nativeGridGap);
    }

    public int getNativeListGapApi() {
        return (int) adsPreferences.getLong(nativeListGapApi, nativeListGap);
    }

    public String getNativeStatusApi() {
        return adsPreferences.getString(nativeStatusApi, nativeStatus);
    }

    public boolean isOnAdsSwitch() {
        return adsPreferences.getString(adsSwitch, "0").equals("1");
    }

    public void setAdsSwitch(String ads_switch) {
        if (editor == null)
            editor = adsPreferences.edit();
        editor.putString(adsSwitch, ads_switch);
        editor.commit();
    }

    public void setAdsType(String ads_type) {
        if (editor == null)
            editor = adsPreferences.edit();
        editor.putString(adsType, ads_type);
        editor.commit();
    }

    public String getAdsType() {
        return adsPreferences.getString(adsType, "Admob");
    }

    public void setAdsLoadType(String load_type) {
        if (editor == null)
            editor = adsPreferences.edit();
        editor.putString(loadType, load_type);
        editor.commit();
    }

    public String getAdsLoadType() {
        return adsPreferences.getString(loadType, "Onload");
    }

    public String getBannerId() {
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            return adsPreferences.getString(bannerId, "ca-app-pub-3940256099942544/6300978111");
        else if (getAdsType().equalsIgnoreCase("Facebook"))
            return adsPreferences.getString(bannerId, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
        else
            return adsPreferences.getString(bannerId, "TEST_OTHER_ID");
    }

    public String getNativeId() {
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            return adsPreferences.getString(nativeId, "ca-app-pub-3940256099942544/2247696110");
        else if (getAdsType().equalsIgnoreCase("Facebook"))
            return adsPreferences.getString(nativeId, "YOUR_PLACEMENT_ID|YOUR_PLACEMENT_ID");
        else
            return adsPreferences.getString(nativeId, "TEST_OTHER_ID");
    }

    public String getInterstitialId() {
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            return adsPreferences.getString(interstitialId, "ca-app-pub-3940256099942544/1033173712");
        else if (getAdsType().equalsIgnoreCase("Facebook"))
            return adsPreferences.getString(interstitialId, "YOUR_PLACEMENT_ID");
        else
            return adsPreferences.getString(interstitialId, "TEST_OTHER_ID");
    }

    public String getRewardId() {
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            return adsPreferences.getString(rewardId, "ca-app-pub-3940256099942544/5224354917");
        else if (getAdsType().equalsIgnoreCase("Facebook"))
            return adsPreferences.getString(rewardId, "YOUR_PLACEMENT_ID");
        else
            return adsPreferences.getString(rewardId, "TEST_OTHER_ID");
    }

    public String getOpenId() {
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            return adsPreferences.getString(openId, "ca-app-pub-3940256099942544/3419835294");
        else if (getAdsType().equalsIgnoreCase("Facebook"))
            return adsPreferences.getString(openId, "YOUR_PLACEMENT_ID");
        else
            return adsPreferences.getString(openId, "TEST_OTHER_ID");
    }

    public String getAppAdsId() {
        return adsPreferences.getString(appAdsId, "NO_REQUIRED");
    }

    protected boolean isValidLoadInterstitial() {
        long lastLoadTime = Long.parseLong(getPrefVal(TIME_INTERSTITIAL_KEY));
        long currentTime = Calendar.getInstance().getTime().getTime();
        if (lastLoadTime == 0) {
            setPrefVal(TIME_INTERSTITIAL_KEY, String.valueOf(currentTime));
            return true;
        }
        long nextLoadTime = lastLoadTime + getTimePeriodInterstitialApi();
        if (currentTime >= nextLoadTime) {
            setPrefVal(TIME_INTERSTITIAL_KEY, String.valueOf(currentTime));
            return true;
        }
        return false;
    }

    protected boolean isValidLoadRewardVideo() {
        int lastCount = Integer.parseInt(getPrefVal(COUNT_REWARD_VIDEO_KEY));
        long currentTime = Calendar.getInstance().getTime().getTime();
        if (lastCount <= getMaxCountShowRewardApi()) {
            lastCount++;
            setPrefVal(COUNT_REWARD_VIDEO_KEY, String.valueOf(lastCount));
            setPrefVal(TIME_REWARD_VIDEO_KEY, String.valueOf(currentTime));
            return true;
        }
        long lastLoadTime = Long.parseLong(getPrefVal(TIME_REWARD_VIDEO_KEY));
        long nextLoadTime = lastLoadTime + getBreakTimePeriodApi();
        if (currentTime >= nextLoadTime) {
            lastCount = 0;
            setPrefVal(COUNT_REWARD_VIDEO_KEY, String.valueOf(lastCount));
            setPrefVal(TIME_REWARD_VIDEO_KEY, String.valueOf(currentTime));
            return true;
        }
        return false;
    }

    public boolean checkRewardVideoAvailable() {
        if (!isOnAdsSwitch()) {
            return false;
        }
        if (isAdsRemove) {
            return false;
        }
        int lastCount = Integer.parseInt(getPrefVal(COUNT_REWARD_VIDEO_KEY));
        long currentTime = Calendar.getInstance().getTime().getTime();
        if (lastCount <= getMaxCountShowRewardApi()) {
            return true;
        }
        long lastLoadTime = Long.parseLong(getPrefVal(TIME_REWARD_VIDEO_KEY));
        long nextLoadTime = lastLoadTime + getBreakTimePeriodApi();
        if (currentTime >= nextLoadTime) {
            return true;
        }
        return false;
    }

    AdmobAds admobAds;
    FbAdsSdk fbAdsSdk;

    //init all sdk
    public void initAds() {
        Log.e(TAG, "ads type : " + getAdsType());
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            admobAds = new AdmobAds(context, isAdsRemove);
        else if (getAdsType().equalsIgnoreCase("Facebook"))
            fbAdsSdk = new FbAdsSdk(context, isAdsRemove);
    }

    public AdmobAds getAdmobAds() {
        return admobAds;
    }

    public FbAdsSdk getFbAdsSdk() {
        return fbAdsSdk;
    }

    public void initAds(boolean isAdsRemove) {
        this.isAdsRemove = isAdsRemove;
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            admobAds = new AdmobAds(context, isAdsRemove);
        else if (getAdsType().equalsIgnoreCase("Facebook"))
            fbAdsSdk = new FbAdsSdk(context, isAdsRemove);
    }

    public void initAds(boolean isAdsRemove, AdsInitListener initListener) {
        this.isAdsRemove = isAdsRemove;
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx"))
            admobAds = new AdmobAds(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                    initListener.onInitialized("null");
                }
            }, isAdsRemove);
        else if (getAdsType().equalsIgnoreCase("Facebook"))
            fbAdsSdk = new FbAdsSdk(context, new AudienceNetworkAds.InitListener() {
                @Override
                public void onInitialized(AudienceNetworkAds.InitResult initResult) {
                    initListener.onInitialized(initResult.getMessage());
                }
            }, isAdsRemove);
    }

    //ads unit function definition
    //banner unit
    public void loadBannerAds(ViewGroup adContainer) {
        adContainer.removeAllViews();
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            admobAds.adBannerLoad(adContainer);
        } else if (getAdsType().equalsIgnoreCase("Facebook")) {
            fbAdsSdk.fbBannerLoad(adContainer);
        }
    }

    //interstitial unit
    public void loadInterstitialAds(Context context, Activity activity, PerformAction performAction) {
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            admobAds.showInterstitialAdsWithNext(context, activity, performAction);
        } else if (getAdsType().equalsIgnoreCase("Facebook")) {
            fbAdsSdk.showFbInterstitialAdsWithNext(context, activity, performAction);
        }
    }

    //reward unit
    public void loadRewardAds(Context context, Activity activity, PerformAction performAction) {
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            admobAds.showRewardVideoAdsWithNext(context, activity, performAction);
        } else if (getAdsType().equalsIgnoreCase("Facebook")) {
            fbAdsSdk.fbRewardVideoAdsWithNext(context, performAction);
        }
    }

    //native banner
    public void loadNativeBanner(Context context, ViewGroup ntContainer) {
        ntContainer.removeAllViews();
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            TemplateView templateView = admobAds.getTemplateView(context, R.layout.gnt_small_template_view);
            ntContainer.addView(templateView);
            admobAds.nativeAdsLoadSingle(templateView);
        } else if (getAdsType().equalsIgnoreCase("Facebook")) {
            NativeAdLayout nativeAdLayout = fbAdsSdk.getFbNativeAdLayout(context);
            ntContainer.addView(nativeAdLayout);
            fbAdsSdk.fbNativeBannerAdsLoadSingle(nativeAdLayout);
        }
    }

    //native size
    public void loadNativeSizeAds(Context context, ViewGroup ntContainer) {
        ntContainer.removeAllViews();
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            TemplateView templateView = admobAds.getTemplateView(context, R.layout.gnt_medium_template_view);
            ntContainer.addView(templateView);
            admobAds.nativeAdsLoadSingle(templateView);
        } else if (getAdsType().equalsIgnoreCase("Facebook")) {
            NativeAdLayout nativeAdLayout = fbAdsSdk.getFbNativeAdLayout(context);
            ntContainer.addView(nativeAdLayout);
            fbAdsSdk.fbNativeSizeAdsLoadSingle(nativeAdLayout);
        }
    }

    public void loadNativeListAds(Context context, ViewGroup ntContainer, int adsRequest) {
        ntContainer.removeAllViews();
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            TemplateView templateView = admobAds.getTemplateView(context, R.layout.gnt_small_template_view);
            ntContainer.addView(templateView);
            admobAds.nativeAdsLoadAdvanceMultipleQueueAds(adsRequest, null, templateView, NativeUse.USE_IN_LIST);
        } else if (getAdsType().equalsIgnoreCase("Facebook")) {
            NativeAdLayout nativeAdLayout = fbAdsSdk.getFbNativeAdLayout(context);
            ntContainer.addView(nativeAdLayout);
            fbAdsSdk.fbNativeSizeAdsLoadSingle(nativeAdLayout, NativeUse.USE_IN_LIST);
        }
    }

    public void loadNativeGridAds(Context context, ViewGroup ntContainer, int adsRequest) {
        ntContainer.removeAllViews();
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            TemplateView templateView = admobAds.getTemplateView(context, R.layout.gnt_native_template_view);
            ntContainer.addView(templateView);
            admobAds.nativeAdsLoadAdvanceMultipleQueueAds(adsRequest, null, templateView, NativeUse.USE_IN_GRID);
        } else if (getAdsType().equalsIgnoreCase("Facebook")) {
            NativeAdLayout nativeAdLayout = fbAdsSdk.getFbNativeAdLayout(context);
            ntContainer.addView(nativeAdLayout);
            fbAdsSdk.fbNativeSizeAdsLoadSingle(nativeAdLayout, NativeUse.USE_IN_GRID);
        }
    }

    public void loadNativeListGridAds(Context context, ViewGroup ntContainer, int adsRequest, int cusRes) {
        ntContainer.removeAllViews();
        if (getAdsType().equalsIgnoreCase("Admob") || getAdsType().equalsIgnoreCase("Adx")) {
            TemplateView templateView = admobAds.getTemplateView(context, cusRes);
            ntContainer.addView(templateView);
            admobAds.nativeAdsLoadAdvanceMultipleQueueAds(adsRequest, null, templateView, NativeUse.USE_IN_GRID);
        } else if (getAdsType().equalsIgnoreCase("Facebook")) {
            NativeAdLayout nativeAdLayout = fbAdsSdk.getFbNativeAdLayout(context);
            ntContainer.addView(nativeAdLayout);
            fbAdsSdk.fbNativeSizeAdsLoadSingle(nativeAdLayout, null, NativeUse.USE_IN_GRID, cusRes);
        }
    }
}
