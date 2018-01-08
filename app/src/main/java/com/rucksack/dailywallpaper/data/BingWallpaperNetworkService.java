package com.rucksack.dailywallpaper.data;

import com.rucksack.dailywallpaper.model.BingWallpaper;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author liaoheng
 * @version 2016-09-19 11:15
 */
public interface BingWallpaperNetworkService {
    @GET Observable<BingWallpaper> getBingWallpaper(@Url String url);
}
