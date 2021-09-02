package com.kuick.SplashScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kuick.R;
import com.kuick.Remote.EndPoints;
import com.kuick.Response.InitResponse;
import com.kuick.activity.HomeActivity;
import com.kuick.activity.LiveActivity;
import com.kuick.base.BaseActivity;
import com.kuick.common.Common;
import com.kuick.databinding.ActivitySplashScreenBinding;
import com.kuick.util.broadcastReceiver.MyReceiver;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.network.Url;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.BaseUrl;
import static com.kuick.Remote.EndPoints.PATH_DEVICE_TYPE_VALUE;
import static com.kuick.util.comman.Constants.ES;
import static com.kuick.util.comman.Constants.INTENT_KEY_DIRECTION_CODE;
import static com.kuick.util.comman.Constants.order_id;

public class SplashScreen extends BaseActivity {

    public static boolean isFirstTimeConnected = true;
    private static final String TAG = "SplashScreen";
    private ActivitySplashScreenBinding binding;
    private boolean isFromNotification = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Utility.PrintLog("onCreate", "SplashScreen onCreate()");
        Log.e(TAG, "SplashScreen onCreate()");
        //getIntent().getClass().getName()
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new MyReceiver(), intentFilter);

        connectivityListener = this;
        binding.bagIcon.setAnimation(isLeftToRightRoundAnimation());
        binding.kuickTextIcon.setAnimation(inFromRightAnimation());

        if (LiveActivity.liveActivity!=null && LiveActivity.isLiveIsRunning)
        {
            LiveActivity.liveActivity.onStop();
        }


        if (language == null) {
            Utility.PrintLog("blank", "blank");
            setDefaultLanguage();
        } else if (language.getLanguageCode() == null) {
            Utility.PrintLog("blank", "when null language code");
            setDefaultLanguage();
        } else {
            if (language != null && language.getLanguageCode() != null) {
                isFromSpashScreen = true;
                callLanguageAPI(language.getLanguageCode(), true, null);
                Utility.PrintLog("blank", "when is from splash or not null");
            } else {
                isFromSpashScreen = true;
                callLanguageAPI(ES, true, null);
                Utility.PrintLog("blank", "when is from splash or null");
            }
        }

        if (getIntent()!=null && getIntent().getData()!=null)
        {
            if (isLogged())
            {
                Uri uri = getIntent().getData();
                Utility.PrintLog("openFromDeepLinking","uri = " + uri);
                if(uri!=null && !uri.toString().equals(""))
                {
                    openFromDeepLinking(uri);
                }else {

                    callInitAPI();
                }

                Utility.PrintLog("openFromDeepLinking","app open from deep linking" + uri.toString());

            } else {
                goToNextScreen(SplashScreen.this, SecondSplashScreen.class, true);
            }


        }else {
            callInitAPI();
            Utility.PrintLog("openFromDeepLinking","normal flow");
        }
    }

    private void openFromDeepLinking(Uri uri)
    {
        Utility.PrintLog("openFromDeepLinking","URL - " + uri.toString());

         String HOME = BaseUrl + "home";
         String LIVE = BaseUrl + "live-stream";
         String STREAMER_DETAIL = BaseUrl + "@";

        if (uri.toString().contains(LIVE))
        {
            String liveSlug = getEndPoint(uri);
            Constants.isForLiveStreaming = true;
            Constants.liveStreamingSlugFromDeepLinking = liveSlug;
            Utility.PrintLog("openFromDeepLinking","for live screen - " + uri.toString());
            startActivity(new Intent(this, HomeActivity.class));
            this.finish();


        } else if (uri.toString().contains(HOME))
        {
            Utility.PrintLog("openFromDeepLinking","for home screen - " + uri.toString());
            callInitAPI();

        }else if (uri.toString().contains(STREAMER_DETAIL)) {

            String username = getEndPoint(uri);
            username = username.replace("@","");
            Constants.isForStreamerDetail = true;
            Utility.PrintLog("openFromDeepLinking","linking for streamer details screen - " + username);
            startActivity(new Intent(this, HomeActivity.class).putExtra(Constants.INTENT_KEY_USERNAME,username));
            this.finish();

        }else if (Url.getSupportedUrls().contains(uri.toString()))
        {
            callInitAPI();
            Utility.PrintLog("openFromDeepLinking","supported links" + uri.toString());

        }

    }

    private String getEndPoint(Uri uri) {

        List<String> parameters = uri.getPathSegments();
        return parameters.get(parameters.size() - 1);
    }

    public void callInitAPI() {

        try {

            Call<InitResponse> call = apiService.doInitCall(EndPoints.API_KEY, PATH_DEVICE_TYPE_VALUE, appVersion);

            if (checkInternetConnectionWithMessage()) {
                call.enqueue(new Callback<InitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<InitResponse> call, @NotNull Response<InitResponse> response) {
                        //checkSessionCode(response.code());

                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true)) {

                                if (response.body().getUpdate() != null && response.body().getUpdate().equals("false")) {

                                    showInternetDialog(SplashScreen.this, "Kuick", response.body().getMessage_key(), response);

                                } else {
                                    normalFlow(response);
                                }

                            } else {

                                if (response.body().getUpdate() != null && response.body().getUpdate().equals("true"))
                                {

                                    compulsuryUpdate(SplashScreen.this, "Kuick", response.body().getMessage_key(),response.body());

                                } else if (response.body().getMaintenance() != null && response.body().getMaintenance().equals("true")) {

                                    maintanasDialog(SplashScreen.this, "Kuick", response.body().getMessage_key(),response.body());
                                } else {

                                    if (language != null && language.getLanguage(response.body().getMessage()) != null) {
                                        showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                                    }
                                }
                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<InitResponse> call, @NotNull Throwable t) {
                        showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            hideLoader();
            //  showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
        }
    }

    private void normalFlow(Response<InitResponse> response) {

        userPreferences.setTimeZone(response.body().getTime_zone_in_GMT());
        userPreferences.setCurrentTime(response.body().getCurrent_time());
        Utility.PrintLog(TAG,"currentTimeFromServer splash screen init- " + response.body().getCurrent_time());
        userPreferences.setCurrentDateAndTime(response.body().getCurrent_time());
        userPreferences.setStripePublishKey(response.body().getStripe_publishablekey());
        userPreferences.setImageBaseUrl(response.body().getImage_base_url());

        if (response.body().getTax() != null) {
            userPreferences.setTaxStatus(response.body().getTax().getTax_status());
            userPreferences.setTaxType(response.body().getTax().getTax_type());
            userPreferences.setTaxAmount(response.body().getTax().getTax_amount());
        }

        hideLoader();

        getIntentFromNotification();

        if (!isFromNotification) {
            if (isLogged()) {
                goToNextScreen(SplashScreen.this, HomeActivity.class, true);
            } else {
                goToNextScreen(SplashScreen.this, SecondSplashScreen.class, true);
            }
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }
    }


    private void getIntentFromNotification() {

        if (getIntent() != null && getIntent().getStringExtra(INTENT_KEY_DIRECTION_CODE) != null) {
            String point = getIntent().getStringExtra(INTENT_KEY_DIRECTION_CODE);
            isFromNotification = true;
            if (point.equals("1")) {

                if (userPreferences != null && isLogged()) {

                    if (LiveActivity.liveActivity!=null && LiveActivity.isLiveIsRunning)
                    {
                        LiveActivity.liveActivity.finishWhenLiveRunningAndClickOnLiveNotification();
                    }

                    Constants.isHomeActivityIsAlredayOpen = true;
                    String liveSlug = getIntent().getStringExtra(Constants.INTENT_KEY_STREAM_SLUG);
                    Constants.live_slug_from_notification = liveSlug;

                    Intent intent = new Intent(this, HomeActivity.class).putExtra(Constants.INTENT_KEY_STREAM_SLUG, liveSlug);
                    startActivity(intent);
                    finish();
                } else {
                    goToNextScreen(SplashScreen.this, SecondSplashScreen.class, true);
                }
                return;


            } else if (point.equals("2")) {

                if (userPreferences != null && isLogged()) {
                    userPreferences.removeCurrentUser();
                    goToNextScreen(SplashScreen.this, SecondSplashScreen.class, true);
                    return;
                }

            } else if (point.equals("3")) {

                if (userPreferences != null && isLogged()) {

                    if (LiveActivity.liveActivity!=null && LiveActivity.isLiveIsRunning)
                    {
                        LiveActivity.liveActivity.finishWhenLiveRunningAndClickOnLiveNotification();
                    }
                    Constants.isHomeActivityForNotification = true;
                    String orderId = getIntent().getStringExtra(Constants.ORDER_ID);
                    order_id = orderId;
                    startActivity(new Intent(this, HomeActivity.class)
                            .putExtra(INTENT_KEY_DIRECTION_CODE, "3")
                            .putExtra(Constants.ORDER_ID, orderId));
                    finish();
                    return;
                } else {
                    goToNextScreen(SplashScreen.this, SecondSplashScreen.class, true);

                }
                return;
            }else if (point.equals("4")){

                if (userPreferences != null && isLogged()) {

                    Constants.isHomeActivityForNotification = true;
                    startActivity(new Intent(this, HomeActivity.class)
                            .putExtra(INTENT_KEY_DIRECTION_CODE, "4"));
                    finish();
                    return;
                } else {
                    goToNextScreen(SplashScreen.this, SecondSplashScreen.class, true);

                }
            }
            Utility.PrintLog(TAG, "intent code : " + point);
        }
    }


    public void showInternetDialog(Context context, String titletitle, String message, @NotNull Response<InitResponse> body) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, 0);
        builder.setCancelable(false);
        builder.setTitle(titletitle);
        String positiveText = "UPDATE";
        String LATER = "LATER";

        if(message!=null && !message.equals(""))
        {
            if (language!=null && language.getLanguage(message)!=null )
            {
                message = language.getLanguage(message);
                positiveText = language.getLanguage(KEY.update);
                LATER = language.getLanguage(KEY.later);
            }
            else {
                if (body.body() != null) {
                    message = body.body().getMessage();
                }
            }
        }

        builder.setMessage(message);

        builder.setPositiveButton(positiveText, null);
        builder.setNegativeButton(LATER, null);


        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.bgSplash)));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                normalFlow(body);
            }
        });


    }

    public void compulsuryUpdate(Context context, String title, String message, InitResponse body) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context, 0);
        builder.setCancelable(false);
        builder.setTitle(title);
        String positiveText = "UPDATE";

        if(message!=null && !message.equals("")) {
            if (language!=null && language.getLanguage(message)!=null ) {
                message = language.getLanguage(message);
                positiveText = language.getLanguage(KEY.update);
            }
        }

        builder.setMessage(message);
        builder.setPositiveButton(positiveText, null);


        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.bgSplash)));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
            }
        });

    }

    public void maintanasDialog(Context context, String title, String message, InitResponse body) {

        if(message!=null && !message.equals(""))
        {
            if (language!=null && language.getLanguage(message)!=null )
            {
                message = language.getLanguage(message);
            }
            else {

                if (language != null) {
                    message = language.getLanguage(body.getMessage());
                }
            }
        }

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .show();
    }

    public void callLanguageAPI(String languageCode, boolean isCallInit, LinearLayout languageSwitch) {
        languageSwitch(languageSwitch,false);
        try {

            Call<String> call = apiService.doGetLanguage(EndPoints.API_KEY, languageCode);

            if (checkInternetConnectionWithMessage()) {
                if (!isFromSpashScreen){
                    showLoader(true);
                }

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {

                        Utility.PrintLog(TAG, "response code from language API : " + response.code());

                        if (!response.isSuccessful()) {
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            return;
                        }

                        if (response.body() != null) {
                            Utility.PrintLog(TAG, "response json : " + response);

                            try {

                                JSONObject jsonObject = new JSONObject(response.body());
                                Utility.PrintLog(TAG,"json response :"+ jsonObject.toString());
                                language.setLanguageCode(jsonObject.getString("language_code"));

                                if (jsonObject.has("labels")) {
                                    Object languageObject = jsonObject.get("labels");
                                    Utility.PrintLog(TAG,"language :"+ jsonObject.toString());
                                    language.setLanguage(languageObject.toString());
                                    if (isCallInit) {


                                    } else hideLoader();

                                    languageSwitch(languageSwitch,true);
                                    if (!isFromSpashScreen) {
                                        //onClickRefreshActivity();
                                        recreate();
                                    }

                                    isFromSpashScreen = false;

                                }

                            } catch (JSONException e) {
                                languageSwitch(languageSwitch,true);
                                e.printStackTrace();
                                Utility.PrintLog(TAG, "exception : " + e.toString());
                            }
                        }

                        hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        languageSwitch(languageSwitch,true);
                        Utility.PrintLog(TAG, "onFailure language() : " + t.toString());
                        hideLoader();
                    }
                });
            }else languageSwitch(languageSwitch,true);

        } catch (Exception e) {
            languageSwitch(languageSwitch,true);
            Utility.PrintLog(TAG, "exception language() : " + e.toString());
            hideLoader();
        }
    }

    @Override
    public void onNetworkConnected()
    {
        Utility.PrintLog("call", "onNetworkConnected");

    }

    public void connectUser() {

        try {

            if (Common.socket != null && Common.socket.connected())
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Common.USER_ID, userPreferences.getRandomUserId());
                Common.socket.emit(Common.CONNECT_ALL_USER, jsonObject);

                Utility.PrintLog("call", "connectUser() after Network Reconnect = " + jsonObject.toString());

            } else {
                Utility.PrintLog("call", "connectUser() socket null or not connected");
            }

        } catch (Exception e) {
            Utility.PrintLog("call", "connectUser() exception = " + e.getMessage());
        }
    }

    public void connectSubscribe(String eventId) {
        try {
            if (Common.socket != null && Common.socket.connected()) {


                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put(Common.EVENT_ID, eventId);
                jsonObject1.put(Common.USER_ID, userPreferences.getUserId());
                Common.socket.emit(Common.CONNECT_CONNECT_SUBSCRIBER, jsonObject1);
                Log.e("call", "connectSubscribe() after Network Reconnect  = " + jsonObject1.toString());
            } else {
                Log.e("call", "connectSubscribe() socket null or not connected");
            }
        } catch (Exception e) {
            Log.e("call", "connectSubscribe() exception = " + e.getMessage());
        }
    }
}