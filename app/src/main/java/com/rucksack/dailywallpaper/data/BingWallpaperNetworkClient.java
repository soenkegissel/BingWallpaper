package com.rucksack.dailywallpaper.data;

import com.github.liaoheng.common.util.SystemDataException;
import com.github.liaoheng.common.util.SystemRuntimeException;

import com.rucksack.dailywallpaper.model.BingWallpaper;
import com.rucksack.dailywallpaper.model.BingWallpaperImage;
import com.rucksack.dailywallpaper.util.BingWallpaperUtils;
import com.rucksack.dailywallpaper.util.NetUtils;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author liaoheng
 * @version 2016-09-20 11:27
 */
public class BingWallpaperNetworkClient {

    public static Observable<BingWallpaperImage> getBingWallpaper() {
        String url = BingWallpaperUtils.getUrl();
        return NetUtils.get().getBingWallpaperNetworkService()
                .getBingWallpaper(url).subscribeOn(Schedulers.io())
                .map(new Func1<BingWallpaper, BingWallpaperImage>() {
                    @Override
                    public BingWallpaperImage call(BingWallpaper bingWallpaper) {
                        if (bingWallpaper == null || bingWallpaper.getImages() == null
                                || bingWallpaper.getImages().isEmpty()) {
                            throw new SystemRuntimeException(new SystemDataException("bing wallpaper is not data"));
                        }
                        return bingWallpaper.getImages().get(0);
                    }
                });
    }

}
