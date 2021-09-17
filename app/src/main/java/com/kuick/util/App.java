package com.kuick.util;

import android.app.Application;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kuick.R;
import com.kuick.livestreaming.rtc.AgoraEventHandler;
import com.kuick.livestreaming.rtc.EngineConfig;
import com.kuick.livestreaming.rtc.EventHandler;
import com.kuick.livestreaming.stats.StatsManager;
import com.kuick.pref.Language;
import com.kuick.util.utils.FileUtil;
import com.stripe.jetbrains.annotations.NotNull;
import com.stripe.stripeterminal.TerminalLifecycleObserver;

import io.agora.rtc.RtcEngine;

import static com.kuick.activity.LiveActivity.liveActivity;
import static com.kuick.base.BaseActivity.isInPictureInPictureMode;
import static com.kuick.util.comman.Utility.PrintLog;


public class App extends Application implements LifecycleObserver {

    @NotNull
    private TerminalLifecycleObserver observer;

    private RtcEngine mRtcEngine;
    private final EngineConfig mGlobalConfig = new EngineConfig();
    private final AgoraEventHandler mHandler = new AgoraEventHandler();
    private final StatsManager mStatsManager = new StatsManager();
    private Language language;
    private long exoPlayerCacheSize = 90 * 1024 * 1024;
    private LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor;
    private ExoDatabaseProvider exoDatabaseProvider;
    public static SimpleCache simpleCache;
    public static FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        observer = TerminalLifecycleObserver.getInstance();
        registerActivityLifecycleCallbacks(observer);
        language = Language.newInstance(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.private_app_id), mHandler);
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            void onDestroy() {
                PrintLog("checkActivityStatus", "OnLifecycleEvent onDestroy() ");
            }
        });


        leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        exoDatabaseProvider = new ExoDatabaseProvider(this);
        simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        observer.onTrimMemory(level, this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(EventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(EventHandler handler) {
        mHandler.removeHandler(handler);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        PrintLog("checkActivityStatus", "LiveCycle onResume()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onAny() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onAppDestroyed() {
        PrintLog("checkActivityStatus", "LiveCycle onAppDestroyed()");
    }

}
