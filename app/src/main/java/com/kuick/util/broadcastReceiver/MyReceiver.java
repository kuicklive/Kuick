package com.kuick.util.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.kuick.Remote.Networking;
import com.kuick.SplashScreen.SplashScreen;
import com.kuick.activity.HomeActivity;
import com.kuick.activity.LiveActivity;
import com.kuick.base.BaseActivity;
import com.kuick.util.comman.Utility;

import static com.google.android.datatransport.cct.internal.NetworkConnectionInfo.NetworkType.WIFI;
import static com.kuick.SplashScreen.SplashScreen.isFirstTimeConnected;
import static com.kuick.activity.LiveActivity.liveCountingListener;

public class MyReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {

        Utility.PrintLog("MyReceiver()","first time status "  + Networking.getConnectivityStatusString(context));
        if (isFirstTimeConnected)
        {
            isFirstTimeConnected = false;
            return;
        }

        Utility.PrintLog("MyReceiver()","status "  + Networking.getConnectivityStatusString(context));
        int status = Networking.getConnectivityStatusString(context);

        if (status == 1 && BaseActivity.connectivityListener!=null)
        {
            HomeActivity.connectivityListener.onNetworkConnected();
        }else if (status == 0){

            if (LiveActivity.internet!=null)
            {
                Utility.PrintLog("MyReceiver()","show internet dialog");
                LiveActivity.internet.internetLost();
            }
        }
    }
}