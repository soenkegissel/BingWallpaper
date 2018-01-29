package com.rucksack.dailywallpaper.ui;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.systembar.SystemBarHelper;
import com.github.liaoheng.common.util.AppUtils;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;
import com.github.liaoheng.common.util.UIUtils;

import org.joda.time.LocalTime;

import com.rucksack.dailywallpaper.R;
import com.rucksack.dailywallpaper.service.AutoSetWallpaperBroadcastReceiver;
import com.rucksack.dailywallpaper.util.BingWallpaperUtils;
import com.rucksack.dailywallpaper.util.BingWallpaperAlarmManager;
import com.rucksack.dailywallpaper.util.BingWallpaperJobManager;
import com.rucksack.dailywallpaper.util.LogDebugFileUtils;
import com.rucksack.dailywallpaper.util.SettingTrayPreferences;
import com.rucksack.dailywallpaper.view.TimePreference;
import com.rucksack.dailywallpaper.view.VersionPreference;

/**
 * @author liaoheng
 * @version 2016-09-20 13:59
 */
public class SettingsActivity extends com.fnp.materialpreferences.PreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBarHelper
                .tintStatusBar(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), 0);
        setPreferenceFragment(new MyPreferenceFragment());
    }

    public static final String PREF_SET_WALLPAPER_RESOLUTION = "pref_set_wallpaper_resolution";
    public static final String PREF_SET_WALLPAPER_AUTO_MODE = "pref_set_wallpaper_auto_mode";
    public static final String PREF_SET_WALLPAPER_DAY_FULLY_AUTOMATIC_UPDATE = "pref_set_wallpaper_day_fully_automatic_update";
    public static final String PREF_SET_WALLPAPER_DAY_AUTO_UPDATE = "pref_set_wallpaper_day_auto_update";
    public static final String PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_TIME = "pref_set_wallpaper_day_auto_update_time";
    public static final String PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_ONLY_WIFI = "pref_set_wallpaper_day_auto_update_only_wifi";
    public static final String PREF_SET_WALLPAPER_LOG = "pref_set_wallpaper_debug_log";

    public static class MyPreferenceFragment extends com.fnp.materialpreferences.PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public int addPreferencesFromResource() {
            return R.xml.preferences;
        }

        CheckBoxPreference mOnlyWifiPreference;
        ListPreference mResolutionListPreference;
        ListPreference mModeTypeListPreference;
        TimePreference mTimePreference;
        CheckBoxPreference mDayUpdatePreference;
        CheckBoxPreference mAutoUpdatePreference;
        Preference mLogPreference;
        //        private int openLogCount;
        private SettingTrayPreferences mPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPreferences = new SettingTrayPreferences(getActivity());
            Preference version = findPreference("pref_version");
            mLogPreference = findPreference(PREF_SET_WALLPAPER_LOG);
            try {
                String versionName = AppUtils.getVersionInfo(getActivity()).versionName;
                version.setSummary(versionName);
            } catch (SystemException e) {
                L.Log.w(TAG, e);
            }
            mOnlyWifiPreference = (CheckBoxPreference) findPreference(
                    PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_ONLY_WIFI);

            //            version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            //                @Override
            //                public boolean onPreferenceClick(Preference preference) {
            //                    openLogCount++;
            //                    if (openLogCount >= 5) {
            //                        openLogCount = 0;
            //                        ((PreferenceCategory) findPreference("pref_other_group")).addPreference(mLogPreference);
            //                    }
            //                    return true;
            //                }
            //            });
            findPreference("pref_license").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    UIUtils.startActivity(getActivity(), LicenseActivity.class);
                    return false;
                }
            });
            //            ((PreferenceCategory) findPreference("pref_other_group")).removePreference(mLogPreference);

            mResolutionListPreference = (ListPreference) findPreference(
                    PREF_SET_WALLPAPER_RESOLUTION);
            mModeTypeListPreference = (ListPreference) findPreference(
                    PREF_SET_WALLPAPER_AUTO_MODE);
            mDayUpdatePreference = (CheckBoxPreference) findPreference(
                    PREF_SET_WALLPAPER_DAY_AUTO_UPDATE);
            mTimePreference = (TimePreference) findPreference(
                    PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_TIME);
            mAutoUpdatePreference = (CheckBoxPreference) findPreference(PREF_SET_WALLPAPER_DAY_FULLY_AUTOMATIC_UPDATE);

            String[] nameStrings = getResources().getStringArray(R.array.pref_set_wallpaper_auto_mode_name);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                nameStrings = new String[] { getString(R.string.set_wallpaper_auto_mode_both) };
            }
            mModeTypeListPreference.setEntries(nameStrings);

            mResolutionListPreference.setSummary(BingWallpaperUtils.getResolution(getActivity()));
            mModeTypeListPreference.setSummary(BingWallpaperUtils.getAutoMode(getActivity()));

            LocalTime localTime = BingWallpaperUtils.getDayUpdateTime(getActivity());

            if (localTime != null) {
                mTimePreference.setSummary(localTime.toString("HH:mm"));
            } else {
                mTimePreference.setSummary(R.string.pref_not_set_time);
            }
            if (mAutoUpdatePreference.isChecked()) {
                mDayUpdatePreference.setChecked(false);
            }
            mTimePreference.setEnabled(mDayUpdatePreference.isChecked());
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            switch (key) {
                case PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_ONLY_WIFI:
                    mPreferences.put(PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_ONLY_WIFI, mOnlyWifiPreference.isChecked());
                    break;
                case PREF_SET_WALLPAPER_RESOLUTION:
                    mResolutionListPreference.setSummary(mResolutionListPreference.getEntry());
                    mPreferences.put(PREF_SET_WALLPAPER_RESOLUTION, mResolutionListPreference.getValue());
                    break;
                case PREF_SET_WALLPAPER_AUTO_MODE:
                    mModeTypeListPreference.setSummary(mModeTypeListPreference.getEntry());
                    mPreferences.put(PREF_SET_WALLPAPER_AUTO_MODE, mModeTypeListPreference.getValue());
                    break;
                case PREF_SET_WALLPAPER_DAY_FULLY_AUTOMATIC_UPDATE:
                    if (mAutoUpdatePreference.isChecked()) {
                        mTimePreference.setSummary(R.string.pref_not_set_time);
                        mDayUpdatePreference.setChecked(false);
                        BingWallpaperUtils.clearDayUpdateTime(getActivity());
                        BingWallpaperAlarmManager.clear(getActivity());
                        BingWallpaperJobManager.enabled(getActivity());
                    } else {
                        BingWallpaperJobManager.disabled(getActivity());
                    }
                    mPreferences.put(PREF_SET_WALLPAPER_DAY_FULLY_AUTOMATIC_UPDATE, mAutoUpdatePreference.isChecked());
                    break;
                case PREF_SET_WALLPAPER_DAY_AUTO_UPDATE:
                    if (mDayUpdatePreference.isChecked()) {
                        mAutoUpdatePreference.setChecked(false);
                        BingWallpaperUtils.enabledReceiver(getActivity(),
                                AutoSetWallpaperBroadcastReceiver.class.getName());
                    } else {
                        BingWallpaperUtils.disabledReceiver(getActivity(),
                                AutoSetWallpaperBroadcastReceiver.class.getName());
                    }
                    mTimePreference.setEnabled(mDayUpdatePreference.isChecked());
                    mPreferences.put(PREF_SET_WALLPAPER_DAY_AUTO_UPDATE, mDayUpdatePreference.isChecked());
                    break;
                case PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_TIME:
                    if (mTimePreference.isEnabled()) {
                        BingWallpaperAlarmManager
                                .add(getActivity(), mTimePreference.getLocalTime());
                        mPreferences.put(PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_TIME,
                                mTimePreference.getLocalTime().toString());
                    }
                    break;
                case PREF_SET_WALLPAPER_LOG:
                    CheckBoxPreference logPreference = (CheckBoxPreference) findPreference(
                            PREF_SET_WALLPAPER_LOG);

                    mPreferences.put(PREF_SET_WALLPAPER_LOG, logPreference.isChecked());

                    if (logPreference.isChecked()) {
                        LogDebugFileUtils.get().init();
                        LogDebugFileUtils.get().open();
                    } else {
                        LogDebugFileUtils.get().clearFile();
                    }
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
