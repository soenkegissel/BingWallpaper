package com.rucksack.dailywallpaper.helper;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Sönke Gissel / © Rucksack Development on 15.01.2018.
 */

public class FirebaseEventHelper {

    //2 Unterkategorien
    public static void sendEvent(Context context, String category, String parameterMayor, String parameterMayorValue, String parameterMinor, String parameterMinorValue){
        Bundle bundle = new Bundle();
        bundle.putString(parameterMinor, parameterMinorValue);
        bundle.putString(parameterMayor, parameterMayorValue);
        try {
            FirebaseAnalytics.getInstance(context).logEvent(category, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //1 Unterkategorie
    public static void sendEvent(Context context, String category, String parameterMayor, String parameterMayorValue){
        Bundle bundle = new Bundle();
        bundle.putString(parameterMayor, parameterMayorValue);
        try {
            FirebaseAnalytics.getInstance(context).logEvent(category, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //0 Unterkategorie
    public static void sendEvent(Context context, String category){
        Bundle bundle = new Bundle();
        try {
            FirebaseAnalytics.getInstance(context).logEvent(category, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

