package com.rucksack.dailywallpaper.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.crashlytics.android.Crashlytics;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.liaoheng.common.util.BitmapUtils;
import com.github.liaoheng.common.util.DisplayUtils;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.UIUtils;
import com.github.liaoheng.common.util.ValidateUtils;

import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.pixplicity.generate.OnFeedbackListener;
import com.pixplicity.generate.Rate;
import com.rucksack.dailywallpaper.BuildConfig;
import com.rucksack.dailywallpaper.R;
import com.rucksack.dailywallpaper.data.BingWallpaperNetworkClient;
import com.rucksack.dailywallpaper.helper.FirebaseEventHelper;
import com.rucksack.dailywallpaper.helper.MobileAdManager;
import com.rucksack.dailywallpaper.helper.RemoteConfigHelper;
import com.rucksack.dailywallpaper.model.BingWallpaperImage;
import com.rucksack.dailywallpaper.model.BingWallpaperState;
import com.rucksack.dailywallpaper.service.BingWallpaperIntentService;
import com.rucksack.dailywallpaper.util.BingWallpaperUtils;
import com.rucksack.dailywallpaper.util.LogDebugFileUtils;
import com.rucksack.dailywallpaper.util.TasksUtils;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 壁纸主界面
 *
 * @author liaoheng
 * @version 2017-2-15
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.bing_wallpaper_view)
    ImageView wallpaperView;
    @BindView(R.id.bing_wallpaper_error)
    TextView mErrorTextView;
    @BindView(R.id.bing_wallpaper_bottom)
    View mBottomView;

    @BindView(R.id.bing_wallpaper_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_drawer)
    NavigationView mNavigationView;
    @BindView(R.id.bing_wallpaper_set_menu)
    FloatingActionMenu mSetWallpaperActionMenu;

    private WallpaperBroadcastReceiver mWallpaperBroadcastReceiver;
    private BingWallpaperImage mCurBingWallpaperImage;
    private boolean isRun;
    private Rate mRateBar;

    //Ads
    private static final String BANNER_ADS_ENABLED_KEY = "dailywallpaper_banner_ads_enabled";

    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*       getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);
        if (TasksUtils.isOne()) {
            TasksUtils.markOne();
        }
        Toolbar toolbar = UIUtils.findViewById(this, R.id.toolbar);
        /*int statusBarHeight = DisplayUtils.getStatusBarHeight(this);
        ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
        lp.height += statusBarHeight;
        toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getPaddingTop() + statusBarHeight,
                toolbar.getPaddingRight(), toolbar.getPaddingBottom());*/
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_home);

        /*if (BingWallpaperUtils.isNavigationBar(this)) {
            int navigationBarHeight = BingWallpaperUtils.getNavigationBarHeight(this);
            if (navigationBarHeight > 0) {
                UIUtils.viewVisible(mBottomView);
                ViewGroup.LayoutParams layoutParams = mBottomView.getLayoutParams();
                layoutParams.height = navigationBarHeight;
                mBottomView.setLayoutParams(layoutParams);
            }
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mSetWallpaperActionMenu.getLayoutParams();
            layoutParams.bottomMargin = layoutParams.bottomMargin + navigationBarHeight;
            mSetWallpaperActionMenu.setLayoutParams(layoutParams);
        }*/

        mNavigationView.setNavigationItemSelectedListener(this);

        mWallpaperBroadcastReceiver = new WallpaperBroadcastReceiver();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRun) {
                    UIUtils.showToast(getActivity(),getString(R.string.set_wallpaper_running));
                } else {
                    getBingWallpaper();
                }
            }
        });
        getBingWallpaper();
        MobileAdManager mobileAds = new MobileAdManager(this);
        mobileAds.get(RemoteConfigHelper.getRemoteConfigBoolean(this, BANNER_ADS_ENABLED_KEY));

        if(BuildConfig.GOOGLE) {
            initRate();
            Log.d(TAG, "Rate. getRemainingCount: "+mRateBar.getRemainingCount());
            mRateBar.count();
        }
    }

    private void initRate() {
        ViewGroup viewGroup = findViewById(R.id.coordinatorLayout);
        mRateBar = new Rate.Builder(this)
                .setTriggerCount(3)
                .setMinimumInstallTime(0)
                .setFeedbackAction(new OnFeedbackListener() {
                    @Override
                    public void onFeedbackTapped() {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"rucksack.development@gmail.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "DailyWallpaper user feedback");
                        intent.putExtra(Intent.EXTRA_TEXT, "I want to get in contact with the developers about: ");
                        try {
                            startActivity(Intent.createChooser(intent, "How to send mail?"));
                        } catch (android.content.ActivityNotFoundException ex) {
                            //do something else
                            Toast.makeText(MainActivity.this, "Sorry, no activity found to handle Emails.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onRateTapped() {
                        Uri uri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT  |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try
                        {
                            MainActivity.this.startActivity(goToMarket);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            MainActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
                        }
                        finally
                        {
                            uri = null;
                            goToMarket = null;
                        }
                    }

                    @Override
                    public void onRequestDismissed(boolean dontAskAgain) {
                        if (dontAskAgain) {
                            Toast.makeText(MainActivity.this, "Ok then, I won't bother you again.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Ok, I'll ask again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setSnackBarParent(viewGroup)
                .build();
    }

    private void getBingWallpaper() {
        if (!BingWallpaperUtils.isConnectedOrConnecting(getApplicationContext())) {
            mErrorTextView.setText(getString(R.string.network_unavailable));
            return;
        }
        showSwipeRefreshLayout();
        BingWallpaperNetworkClient.getBingWallpaper()
                .compose(this.<BingWallpaperImage>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BingWallpaperImage>() {
                    @Override
                    public void call(BingWallpaperImage bingWallpaperImage) {
                        mCurBingWallpaperImage = bingWallpaperImage;
                        setImage(bingWallpaperImage);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getBingWallpaperError(throwable);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void getBingWallpaperError(Throwable throwable) {
        dismissSwipeRefreshLayout();
        String error = getString(R.string.network_request_error);
        if (throwable instanceof SocketTimeoutException) {
            error = getString(R.string.connection_timed_out);
        }
        mErrorTextView.setText(getString(R.string.pull_refresh) + error);
        if (throwable == null) {
            L.Log.e(TAG, error);
        } else {
            L.Log.e(TAG, throwable);
            if (!BuildConfig.DEBUG) {
                Crashlytics.logException(throwable);
            }
        }
        if (BingWallpaperUtils.isEnableLog(getApplicationContext())) {
            LogDebugFileUtils.get().e(TAG, throwable, error);
        }
    }

    /**
     * @param type 0. both , 1. home , 2. lock
     */
    private void setWallpaper(int type) {
        if (isRun) {
            UIUtils.showToast(this, getString(R.string.set_wallpaper_running));
            return;
        }
        if (!BingWallpaperUtils.isConnectedOrConnecting(this)) {
            UIUtils.showToast(this, getString(R.string.network_unavailable));
            return;
        }
        showProgressDialog();
        BingWallpaperIntentService.start(this, type, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId_ = item.getItemId();
        if (itemId_ == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dismissSwipeRefreshLayout() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void dismissProgressDialog() {
        isRun = false;
        mSetWallpaperActionMenu.showMenu(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showSwipeRefreshLayout() {
        mErrorTextView.setText("");
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void showProgressDialog() {
        isRun = true;
        mSetWallpaperActionMenu.hideMenu(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_drawer_settings) {
            UIUtils.startActivity(this, SettingsActivity.class);
        } else if (item.getItemId() == R.id.menu_main_drawer_wallpaper_info) {
            if (mCurBingWallpaperImage != null) {
                try {
                    String url = mCurBingWallpaperImage.getCopyrightlink();
                    if (!ValidateUtils.isWebUrl(url)) {
                        url = "https://www.bing.com";
                    }
                    new CustomTabsIntent.Builder()
                            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary)).build()
                            .launchUrl(this, Uri.parse(url));
                } catch (AndroidRuntimeException e) {
                    UIUtils.showToast(getActivity(), getString(R.string.unable_open_url));
                }
            }

        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    class WallpaperBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BingWallpaperIntentService.ACTION_GET_WALLPAPER_STATE.equals(intent.getAction())) {
                int extra = intent
                        .getIntExtra(BingWallpaperIntentService.EXTRA_GET_WALLPAPER_STATE, -1);
                if (extra < 0) {
                    return;
                }
                BingWallpaperState state = BingWallpaperState.find(extra);

                if (BingWallpaperState.BEGIN.equals(state)) {
                    //                    showProgressDialog();
                } else if (BingWallpaperState.SUCCESS.equals(state)) {
                    dismissProgressDialog();
                    UIUtils.showToast(getApplicationContext(), getString(R.string.set_wallpaper_success));
                    showRating();
                } else if (BingWallpaperState.FAIL.equals(state)) {
                    dismissProgressDialog();
                    UIUtils.showToast(getApplicationContext(), getString(R.string.set_wallpaper_failure));
                }

            }
        }
    }

    private void setImage(BingWallpaperImage bingWallpaperImage) {
        setTitle(bingWallpaperImage.getCopyright());

        String url = BingWallpaperUtils.getUrl(getApplicationContext(), bingWallpaperImage);

        Glide.with(getActivity()).load(url)
                .centerCrop().crossFade().into(new ImageViewTarget<GlideDrawable>(wallpaperView) {
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                getBingWallpaperError(e);
            }

            @Override
            protected void setResource(GlideDrawable resource) {
                wallpaperView.setImageDrawable(resource);

                Palette.from(BitmapUtils.drawableToBitmap(resource))
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {

                                int lightMutedSwatch = palette.getMutedColor(ContextCompat
                                        .getColor(getActivity(), R.color.colorPrimaryDark));

                                int lightVibrantSwatch = palette.getVibrantColor(ContextCompat
                                        .getColor(getActivity(), R.color.colorAccent));

                                mSetWallpaperActionMenu.removeAllMenuButtons();

                                mSetWallpaperActionMenu.setMenuButtonColorNormal(lightMutedSwatch);
                                mSetWallpaperActionMenu.setMenuButtonColorPressed(lightMutedSwatch);
                                mSetWallpaperActionMenu.setMenuButtonColorRipple(lightVibrantSwatch);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    addActionButton(lightMutedSwatch, lightVibrantSwatch,
                                            getString(R.string.set_wallpaper_auto_mode_home),
                                            R.drawable.ic_home_white_24dp, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    setWallpaper(1);
                                                    mSetWallpaperActionMenu.close(true);
                                                }
                                            });

                                    addActionButton(lightMutedSwatch, lightVibrantSwatch,
                                            getString(R.string.set_wallpaper_auto_mode_lock),
                                            R.drawable.ic_lock_white_24dp, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    setWallpaper(2);
                                                    mSetWallpaperActionMenu.close(true);
                                                }
                                            });

                                    addActionButton(lightMutedSwatch, lightVibrantSwatch,
                                            getString(R.string.set_wallpaper_auto_mode_both),
                                            R.drawable.ic_smartphone_white_24dp, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    setWallpaper(0);
                                                    mSetWallpaperActionMenu.close(true);
                                                }
                                            });
                                } else {
                                    mSetWallpaperActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setWallpaper(0);
                                        }
                                    });
                                }

                                mSetWallpaperActionMenu.showMenu(true);
                            }
                        });

                isRun = false;
                dismissSwipeRefreshLayout();
            }
        });

    }

    private void showRating() {
        if(BuildConfig.GOOGLE) {
            FirebaseEventHelper.sendEvent(MainActivity.this, "ratingDialogShown");
            Log.d(TAG, "Rate. Show rating?! "+ mRateBar.showRequest());
            mRateBar.showRequest();
        }
    }

    private void addActionButton(@ColorInt int lightMutedSwatch, @ColorInt int lightVibrantSwatch, String text,
            @DrawableRes int resId, View.OnClickListener listener) {
        FloatingActionButton actionButton = new FloatingActionButton(getActivity());
        actionButton.setLabelText(text);
        actionButton.setColorNormal(lightMutedSwatch);
        actionButton.setColorPressed(lightMutedSwatch);
        actionButton.setColorRipple(lightVibrantSwatch);
        actionButton.setImageResource(resId);
        actionButton.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
        mSetWallpaperActionMenu.addMenuButton(actionButton);
        actionButton.setLabelColors(lightMutedSwatch, lightMutedSwatch, lightVibrantSwatch);
        actionButton.setOnClickListener(listener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mWallpaperBroadcastReceiver,
                new IntentFilter(BingWallpaperIntentService.ACTION_GET_WALLPAPER_STATE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mWallpaperBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        UIUtils.cancelToast();
        super.onDestroy();
    }
}
