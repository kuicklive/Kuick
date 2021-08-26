package com.kuick.Remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kuick.BuildConfig;
import com.kuick.base.BaseActivity;
import com.stripe.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.kuick.Remote.EndPoints.API_KEY;
import static com.kuick.Remote.EndPoints.HEADER_KEY;
import static com.kuick.Remote.EndPoints.HEADER_X_API_KEY;
import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.base.BaseActivity.baseActivity;

public class Networking {

    private static Retrofit retrofit = null;
    private static ApiInterface networkService = null;
    public static int ErrorCode = 403;

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;


    public static Retrofit getClient()
    {

        final Interceptor networkInterceptor = chain -> {
            Request.Builder requestBuilder = chain.request().newBuilder();
            if (homeActivity!=null && homeActivity.userPreferences!=null && homeActivity.userPreferences.getApiKey() != null) {
                requestBuilder.header(HEADER_X_API_KEY, homeActivity.userPreferences.getApiKey());
                requestBuilder.header(HEADER_KEY, API_KEY);
            }
            return chain.proceed(requestBuilder.build());
        };

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.readTimeout(1, TimeUnit.MINUTES);
            httpClient.readTimeout(1, TimeUnit.MINUTES);
            httpClient.addNetworkInterceptor(networkInterceptor);
            httpClient.addInterceptor(logging);
            httpClient.addNetworkInterceptor(networkInterceptor);

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BuildConfig.API_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build();
            }


            return retrofit;
        }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = Networking.getConnectivityStatus(context);
        int status = 0;
        if (conn == Networking.TYPE_WIFI) {
            status = NETWORK_STATUS_WIFI;
        } else if (conn == Networking.TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == Networking.TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }
}