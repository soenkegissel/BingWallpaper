package com.rucksack.dailywallpaper.helper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.rucksack.dailywallpaper.BuildConfig;
import com.rucksack.dailywallpaper.R;

/**
 * Created by Sönke Gissel on 31.01.2018.
 */

public class RemoteConfigHelper {

    private static final String TAG = RemoteConfigHelper.class.getSimpleName();
    private static FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static boolean getRemoteConfigBoolean(Activity activity, final String key) {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]
        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remoteconfig);
        // [END set_default_values]

        long cacheExpiration = 28800; // 8 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        Log.d(TAG, "Enabled by default for "+ String.valueOf(key) + " = " + mFirebaseRemoteConfig.getBoolean(String.valueOf(key)));
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                            Log.d(TAG, "Fetched remote config for "+String.valueOf(key) + " = " + mFirebaseRemoteConfig.getBoolean(String.valueOf(key)));
                        }
                    }
                });
        return mFirebaseRemoteConfig.getBoolean(String.valueOf(key));
        //getMobileAds(mFirebaseRemoteConfig.getBoolean(BANNER_ADS_ENABLED_KEY));
    }
}
