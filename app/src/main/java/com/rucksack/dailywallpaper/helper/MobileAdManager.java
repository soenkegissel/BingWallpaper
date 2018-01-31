package com.rucksack.dailywallpaper.helper;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.amazon.device.ads.AdLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.rucksack.dailywallpaper.BuildConfig;
import com.rucksack.dailywallpaper.R;
import com.rucksack.dailywallpaper.ui.MainActivity;

/**
 * Created by Sönke Gissel on 31.01.2018.
 */

public class MobileAdManager {
    private String TAG = MobileAdManager.class.getSimpleName();
    private Activity activity;
    private AdView mAdView;
    private AdLayout amazonAdView;

    public MobileAdManager(Activity activity) {
        this.activity = activity;
    }

    public void get(boolean isBannerAdEnabled) {
        mAdView = new AdView(activity);
        amazonAdView = new AdLayout(activity);
        final String flavor;
        Log.d(TAG, "Ads trace: isBannerAdEnabled? "+isBannerAdEnabled);
        //Gefüllt durch ProductFlavors
        if (BuildConfig.GOOGLE) {
            flavor = "GOOGLE";
            Log.d(TAG, "Go Google!");
            MobileAds.initialize(activity,
                    "ca-app-pub-7250949786873174~9815294391");
            View adContainer = activity.findViewById(R.id.adMobView);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            if (BuildConfig.DEBUG) {
                //DebugAdUnitId
                mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                ((RelativeLayout)adContainer).addView(mAdView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            } else {
                //ReleaseAdUnitId
                mAdView.setAdUnitId("ca-app-pub-7250949786873174/1622600294");
                if(isBannerAdEnabled) {
                    ((RelativeLayout) adContainer).addView(mAdView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                }
            }
        } else {
            //AMAZON
            flavor = "AMAZON";
            Log.d(TAG, "Go Amazon!");
            MobileAds.initialize(activity,
                    "ca-app-pub-7250949786873174~6993144760");
            View adContainer = activity.findViewById(R.id.adMobView);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            if (BuildConfig.DEBUG) {
                //DebugAdUnitId
                mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                ((RelativeLayout)adContainer).addView(mAdView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            } else {
                //ReleaseAdUnitId
                mAdView.setAdUnitId("ca-app-pub-7250949786873174/9694850797");
                if(isBannerAdEnabled) {
                    ((RelativeLayout) adContainer).addView(mAdView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                }
            }

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    FirebaseEventHelper.sendEvent(activity, "remoteconfig", "adListenerResult", "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    FirebaseEventHelper.sendEvent(activity, "remoteconfig", "adListenerResult", "onadFailedtoLoad "+errorCode);
                }
            });

                /*AdRegistration.setAppKey("ef20ac0833c44488b5e98c5f6c54db5d");
                amazonAdView = findViewById(R.id.amazonAdview);
                AdTargetingOptions adOptions = new AdTargetingOptions();
                if (BuildConfig.DEBUG) {
                    AdRegistration.enableLogging(true);
                    //AdRegistration.enableTesting(true);
                    Toast.makeText(this, "You receive an Amazon Ad for Debug", Toast.LENGTH_LONG).show();
                    this.amazonAdView.loadAd(adOptions); // Retrieves an ad on background thread
                }
                else{
                    if(isBannerAdEnabled)
                        Toast.makeText(this, "You receive an Amazon Ad for Release", Toast.LENGTH_LONG).show();
                        this.amazonAdView.loadAd(adOptions); // Retrieves an ad on background thread
                }*/
        }
    }
}
