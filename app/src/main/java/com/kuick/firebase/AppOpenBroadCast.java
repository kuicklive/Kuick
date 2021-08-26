package com.kuick.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kuick.SplashScreen.SplashScreen;
import com.kuick.util.comman.Utility;

import static com.kuick.base.BaseActivity.isApplicationSentToBackground;

public class AppOpenBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context.getApplicationContext(), SplashScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
        context.unregisterReceiver(this);

        Utility.PrintLog("onStop()"," PowerButton" );
    }


}
