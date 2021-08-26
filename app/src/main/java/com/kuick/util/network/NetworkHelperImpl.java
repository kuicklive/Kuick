package com.kuick.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.kuick.R;
import java.net.ConnectException;


public class NetworkHelperImpl implements NetworkHelper {

    /**
     * When internet not available status code = 0 message = Not able to connect, please retry
     * When not socket or http exception status code = -1 message = Something wrong happened, please retry again
     * When socket or http exception status code from error and message from error but display somethinf wne wrong to user
     * */
    
    @Override
    public NetworkError castToNetworkError(Throwable throwable, Context context) {

        if (throwable instanceof ConnectException) {
            return new NetworkError(0, context.getString(R.string.server_connection_error));
        }

        //not an socket error, and not http code error
        if (!(throwable instanceof HttpException)) {
            return new NetworkError(-1, context.getString(R.string.not_a_http_exception_message));
        }

        return new NetworkError(((HttpException) throwable).code(), ((HttpException) throwable).message());

    }


    @Override
    public boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

}
