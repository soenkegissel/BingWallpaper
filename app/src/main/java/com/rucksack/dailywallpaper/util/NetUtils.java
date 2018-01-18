package com.rucksack.dailywallpaper.util;

import android.util.Log;

import com.github.liaoheng.common.util.FileUtils;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.SystemException;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.rucksack.dailywallpaper.data.BingWallpaperNetworkService;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author liaoheng
 * @version 2016-11-15 10:48
 */
public class NetUtils {

    private NetUtils() {
    }

    private static NetUtils retrofitUtils;

    public static NetUtils get() {
        if (retrofitUtils == null) {
            retrofitUtils = new NetUtils();
        }
        return retrofitUtils;
    }

    private Retrofit mRetrofit;

    public void init() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (L.isPrint()) {
                    Log.d("NetUtils", message);
                }
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(logging);
//        try {
//            File cacheFile = FileUtils.createCacheSDAndroidDirectory(Constants.HTTP_CACHE_DIR);
//            builder.cache(new Cache(cacheFile, Constants.HTTP_DISK_CACHE_SIZE));
//        } catch (SystemException ignored) {
//        }
        mRetrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).client(builder.build())
                .build();
    }

    private BingWallpaperNetworkService mBingWallpaperNetworkService;

    public BingWallpaperNetworkService getBingWallpaperNetworkService() {
        if (mBingWallpaperNetworkService == null) {
            mBingWallpaperNetworkService = getRetrofit().create(BingWallpaperNetworkService.class);
        }
        return mBingWallpaperNetworkService;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

}
