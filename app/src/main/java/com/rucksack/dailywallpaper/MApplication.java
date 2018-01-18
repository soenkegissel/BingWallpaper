package com.rucksack.dailywallpaper;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.github.liaoheng.common.Common;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;

import com.rucksack.dailywallpaper.util.BingWallpaperUtils;
import com.rucksack.dailywallpaper.util.BingWallpaperAlarmManager;
import com.rucksack.dailywallpaper.util.Constants;
import com.rucksack.dailywallpaper.util.BingWallpaperJobManager;
import com.rucksack.dailywallpaper.util.LogDebugFileUtils;
import com.rucksack.dailywallpaper.util.NetUtils;
import com.rucksack.dailywallpaper.util.TasksUtils;

/**
 * @author liaoheng
 * @version 2016-09-19 11:34
 */
public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Common.init(this, Constants.PROJECT_NAME, BuildConfig.DEBUG);
        TasksUtils.init(this);
        if (BingWallpaperUtils.isEnableLog(this)) {
            LogDebugFileUtils.get().init();
            LogDebugFileUtils.get().open();
        }
        NetUtils.get().init();

        //debug firebase not work
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG);

        if (TasksUtils.isOne()) {
            BingWallpaperAlarmManager.clear(this);
            BingWallpaperJobManager.disabled(this);
        }
    }
}
