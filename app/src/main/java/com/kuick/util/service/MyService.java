package com.kuick.util.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import static com.kuick.activity.LiveActivity.liveActivity;
import static com.kuick.base.BaseActivity.isInPictureInPictureMode;
import static com.kuick.util.comman.Utility.PrintLog;

public class MyService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        PrintLog("checkActivityStatus", "onTaskRemoved() ");
        if (liveActivity != null && isInPictureInPictureMode) {
            isInPictureInPictureMode = false;
            liveActivity.finishAffinity();
            PrintLog("checkActivityStatus", "FinishLiveActivity Live IS ON onDestroy()");
        }
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
}
