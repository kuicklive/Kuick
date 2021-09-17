package com.kuick.base;

import static com.kuick.activity.CartPageActivity.cartPageActivity;
import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.activity.HomeActivity.imageRefreshListener;
import static com.kuick.fragment.HomeFragment.homeFragment;
import static com.kuick.util.comman.Constants.ALLOW;
import static com.kuick.util.comman.Constants.AppConstance.FORWARD_SCREEN_CHANGE_TIME;
import static com.kuick.util.comman.Constants.CANCEL;
import static com.kuick.util.comman.Constants.OK;
import static com.kuick.util.comman.Constants.SESSION_EXPIRE;
import static com.kuick.util.comman.Utility.PrintLog;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.kuick.BuildConfig;
import com.kuick.R;
import com.kuick.Remote.ApiInterface;
import com.kuick.Remote.EndPoints;
import com.kuick.Remote.NetworkConnectivityListener;
import com.kuick.Remote.Networking;
import com.kuick.Response.AllStreamerData;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.Response.StreamerCountryList;
import com.kuick.SplashScreen.SecondSplashScreen;
import com.kuick.activity.CartPageActivity;
import com.kuick.activity.HomeActivity;
import com.kuick.activity.LiveActivity;
import com.kuick.common.Common;
import com.kuick.databinding.ActivityLoginBinding;
import com.kuick.databinding.ActivitySecondSplashScreenBinding;
import com.kuick.databinding.ActivitySignupBinding;
import com.kuick.fragment.DiscoverFragment;
import com.kuick.fragment.HomeFragment;
import com.kuick.fragment.NotificationFragment;
import com.kuick.fragment.OrderHistoryFragment;
import com.kuick.fragment.ProfileFragment;
import com.kuick.fragment.VideoClipsFragment;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.livestreaming.rtc.EngineConfig;
import com.kuick.livestreaming.rtc.EventHandler;
import com.kuick.livestreaming.stats.StatsManager;
import com.kuick.model.FeatureStreamers;
import com.kuick.model.NewStreamers;
import com.kuick.model.TrendingStreamers;
import com.kuick.pref.Language;
import com.kuick.pref.UserPreferences;
import com.kuick.util.App;
import com.kuick.util.comman.Analytic;
import com.kuick.util.comman.CommanDialogs;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.DefaultLanguage;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.countrypicker.CountryPicker;
import com.kuick.util.dialog.BlurDialogFragment;
import com.kuick.util.loader.LoaderDialogFragment;
import com.kuick.util.network.NetworkHelper;
import com.kuick.util.network.NetworkHelperImpl;
import com.kuick.util.service.MyService;
import com.ms_square.etsyblur.BlurConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.emitters.StreamEmitter;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity implements
        BaseView,
        View.OnClickListener,
        ClickEventListener,
        EventHandler,
        GoogleApiClient.OnConnectionFailedListener,
        NetworkConnectivityListener {

    public static final int RC_SIGN_IN = 1;
    public static final int PERMISSION_REQUEST_CODE = 1;
    public static final int MY_REQUEST_CODE_CAMERA = 101;
    public static final int MY_REQUEST_CODE_STORAGE = 100;
    public static ArrayList<String> allCountriesList = new ArrayList<>();
    public static List<StreamerCountryList> streamerCountryLists = new ArrayList<>();
    public static ArrayList<String> allRegionsList = new ArrayList<>();
    public static NetworkConnectivityListener connectivityListener;
    public static boolean permissionGranted = false;
    public static boolean isInPictureInPictureMode = false;
    public static boolean isFromSpashScreen = false;
    public static ClickEventListener clickEventListener;
    public static int GALLERY = 1, CAMERA = 0, REQUEST_CAMERA_SUMSUNG = 5;
    public static BaseActivity baseActivity;
    public static boolean isForDiscover = false;
    static boolean runningActivity = false;
    private static int[] colorsList;
    private final String TAG = "BaseActivity";
    public String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
    };
    public CountryPicker picker;
    public List<String> permissionNeeds = Arrays.asList("email", "public_profile");
    public ApiInterface apiService;
    public String appVersion;
    public UserPreferences userPreferences;
    public Language language = null;
    public Fragment fragment;
    protected DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private Fragment activeFragment;
    private NetworkHelper networkHelper;
    private LoaderDialogFragment loaderDialogFragment;
    private LoaderDialogFragment loader;

    public static ArrayList<String> getAllRegions() {
        return allRegionsList;
    }

    public static ArrayList<String> getAllCountries() {
        return allCountriesList;
    }

    public static List<StreamerCountryList> getAllStreamerCountries() {
        return streamerCountryLists;
    }

    public static void showGlideImage(Context mContext, String imgUrl, ImageView imageView) {

        try {
            Glide.with(mContext)
                    .load(imgUrl)
                    //.centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .into(imageView);
        } catch (Exception e) {

        }
    }

    public static void showGlideImageWithError(Context mContext, String imgUrl, ImageView imageView, Drawable drawable) {

        try {
            Glide.with(mContext)
                    .load(imgUrl)
                    .error(drawable)
                    //.centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .into(imageView);
        } catch (Exception e) {

        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(10);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            return !topActivity.getPackageName().equals(context.getPackageName());
        }
        return false;
    }

    public void openCountryPicker() {

        try {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            });

        } catch (Exception e) {

        }

    }

    public void callCountryList(boolean isShowLoader) {

        allCountriesList = new ArrayList<>();

        try {


            if (checkInternetConnectionWithMessage()) {
                if (!isShowLoader) {
                    showLoader(true);
                }


                Call<String> call = apiService.doCountryList(EndPoints.API_KEY);


                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());


                        if (response.body() != null) {

                            if (response.isSuccessful()) {
                                try {

                                    JSONObject json = new JSONObject(response.body());
                                    String jsonString = json.getString("country_list");
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    //allCountriesList = new ArrayList<>();

                                    Iterator<String> iter = jsonObject.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        try {
                                            String country = jsonObject.getString(key);
                                            allCountriesList.add(country);
                                            Utility.PrintLog(TAG, "CountryList : " + country);
                                        } catch (JSONException e) {
                                            Utility.PrintLog(TAG, "success exception : " + e.toString());
                                            // Something went wrong!
                                        }
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Utility.PrintLog(TAG, "json exception : " + e.toString());
                                }
                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                        Utility.PrintLog(TAG, t.toString());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }
    }

    public void PermissionDeny(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton(ALLOW, (dialog, id) -> {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            permissionGranted = false;
        });

        builder.setNegativeButton(CANCEL, (dialog, id) -> {

        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void userDisconnect() {

        if (Common.socket != null && Common.socket.connected()) {


            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Common.USER_ID, userPreferences.getUserId());
                Common.socket.emit(Common.DISCONNECT_USER, jsonObject);

                Log.e("call", "onClick Disconnected : " + jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("call", "userDisconnect() Exception = " + e.getMessage());
            }

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            startService(new Intent(this, MyService.class));
        } catch (Exception e) {

        }

        baseActivity = this;
        apiService = Networking.getClient().create(ApiInterface.class);
        appVersion = getAppVersion();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setStatusBar();
        userPreferences = UserPreferences.newInstance(this);
        language = Language.newInstance(this);
        networkHelper = new NetworkHelperImpl();
        loader = new LoaderDialogFragment();
        setGlobalLayoutListener();
        getDisplayMetrics();


        colorsList = new int[]{
                getResources().getColor(R.color.orange),
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.purple),
                getResources().getColor(R.color.darkblue),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.yellow)
        };

    }

    private void getDisplayMetrics() {
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    private void setGlobalLayoutListener() {
        final View layout = findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver observer = layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onGlobalLayoutCompleted();
            }
        });
    }

    protected void onGlobalLayoutCompleted() {

    }

    public void goToNextScreen(Activity activity, Class activityClass, boolean isFinish) {
        startActivity(new Intent(activity, activityClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        if (isFinish) {
            finish();
        }
    }

    public void goToNextScreenWithParam(Activity activity, Class activityClass, boolean isFinish, String key, String value) {
        startActivity(new Intent(activity, activityClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(key, value));
        if (isFinish) {
            finish();
        }
    }

    public void setStatusBar() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }

            }
        } catch (Exception e) {
            Utility.PrintLog(TAG, "setStatusBar() Exception = " + e.toString());
        }
    }

    protected Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(1000);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation isLeftToRightRoundAnimation() {
        return AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right_round_animation);
    }

    public void showProfileFragment() {

        if (activeFragment instanceof ProfileFragment) return;

        fragment = getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragment = ProfileFragment.newInstance();
        transaction.add(R.id.container, fragment, ProfileFragment.TAG);

        if (activeFragment != null) transaction.hide(activeFragment);

        transaction.commit();
        activeFragment = fragment;
    }

    public void showHomeFragment() {

        if (activeFragment instanceof HomeFragment) {
            if (homeFragment != null && Constants.isForRefreshHomeApi) {
                Constants.isForRefreshHomeApi = false;
                homeFragment.refreshHomeFragment();
            }
            return;
        }


        fragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragment = HomeFragment.newInstance();
        transaction.add(R.id.container, fragment, HomeFragment.TAG);

        if (activeFragment != null) transaction.hide(activeFragment);

        transaction.commit();
        activeFragment = fragment;
    }

    public void showNotificationFragment() {

        if (activeFragment instanceof NotificationFragment) return;

        fragment = getSupportFragmentManager().findFragmentByTag(NotificationFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragment = NotificationFragment.newInstance();
        transaction.add(R.id.container, fragment, NotificationFragment.TAG);

        if (activeFragment != null) transaction.hide(activeFragment);

        transaction.commit();
        activeFragment = fragment;
    }

    public void showVideoClipFragment() {

        if (activeFragment instanceof VideoClipsFragment) return;

        fragment = getSupportFragmentManager().findFragmentByTag(VideoClipsFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragment = VideoClipsFragment.newInstance();
        transaction.add(R.id.container, fragment, VideoClipsFragment.TAG);

        if (activeFragment != null) transaction.hide(activeFragment);

        transaction.commit();
        activeFragment = fragment;
    }

    public void showOrderHistoryFragment() {

        if (activeFragment instanceof OrderHistoryFragment) return;

        fragment = getSupportFragmentManager().findFragmentByTag(OrderHistoryFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragment = OrderHistoryFragment.newInstance();
        transaction.add(R.id.container, fragment, OrderHistoryFragment.TAG);

        if (activeFragment != null) transaction.hide(activeFragment);

        transaction.commit();
        activeFragment = fragment;
    }

    public void showDiscoverFragment() {

        if (activeFragment instanceof DiscoverFragment) return;

        fragment = getSupportFragmentManager().findFragmentByTag(DiscoverFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragment = DiscoverFragment.newInstance();
        transaction.add(R.id.container, fragment, DiscoverFragment.TAG);

        if (activeFragment != null) transaction.hide(activeFragment);

        transaction.commit();
        activeFragment = fragment;
    }

    public void switchSetup(ActivityLoginBinding binding, boolean isTrue) {

        if (!isTrue) {
            binding.btnEN.setBackgroundResource(R.drawable.switch_selected);
            binding.btnEN.setTextColor(Color.WHITE);
            binding.btnSPA.setTextColor(getResources().getColor(R.color.unselected_color));
            binding.btnSwitch.setSelected(isTrue);
            binding.btnSPA.setBackground(null);
        } else {
            binding.btnSPA.setBackgroundResource(R.drawable.switch_selected);
            binding.btnSPA.setTextColor(Color.WHITE);
            binding.btnEN.setTextColor(getResources().getColor(R.color.unselected_color));
            binding.btnSwitch.setSelected(isTrue);
            binding.btnEN.setBackground(null);
        }
    }

    public void switchLanguageSetup(ActivitySignupBinding binding, boolean isTrue) {

        if (!isTrue) {
            binding.btnEN.setBackgroundResource(R.drawable.switch_selected);
            binding.btnEN.setTextColor(Color.WHITE);
            binding.btnSPA.setTextColor(getResources().getColor(R.color.unselected_color));
            binding.btnSwitch.setSelected(isTrue);
            binding.btnSPA.setBackground(null);
        } else {
            binding.btnSPA.setBackgroundResource(R.drawable.switch_selected);
            binding.btnSPA.setTextColor(Color.WHITE);
            binding.btnEN.setTextColor(getResources().getColor(R.color.unselected_color));
            binding.btnSwitch.setSelected(isTrue);
            binding.btnEN.setBackground(null);
        }
    }

    public void switchLanguage(ActivitySecondSplashScreenBinding binding, boolean isTrue) {

        if (!isTrue) {
            binding.btnEN.setBackgroundResource(R.drawable.switch_selected);
            binding.btnEN.setTextColor(Color.WHITE);
            binding.btnSPA.setTextColor(getResources().getColor(R.color.unselected_color));
            binding.btnSwitch.setSelected(isTrue);
            binding.btnSPA.setBackground(null);
        } else {
            binding.btnSPA.setBackgroundResource(R.drawable.switch_selected);
            binding.btnSPA.setTextColor(Color.WHITE);
            binding.btnEN.setTextColor(getResources().getColor(R.color.unselected_color));
            binding.btnSwitch.setSelected(isTrue);
            binding.btnEN.setBackground(null);
        }
    }

    public boolean checkInternetConnectionWithMessage() {
        if (networkHelper.isNetworkConnected(this)) {
            return true;
        } else {
            showErrorDialogInternet(R.string.oops, language.getLanguage(KEY.internet_connection_lost_please_retry_again)/*R.string.network_connection_error*/);
            return false;
        }
    }

    protected void showErrorDialog(int title, int message) {
        CommanDialogs.showOneButtonDialog(
                getViewActivity(),
                getString(title),
                getString(message),
                language.getLanguage(KEY.ok),
                (dialog, which) -> dialog.dismiss(),
                true
        );
    }

    protected void showErrorDialogInternet(int title, String message) {
        CommanDialogs.showOneButtonDialog(
                getViewActivity(),
                getString(title),
                message,
                language.getLanguage(KEY.ok),
                (dialog, which) -> dialog.dismiss(),
                true
        );
    }

    @Override
    public void showLoader(boolean isClose) {

        try {
            if (!loader.isAdded()) {
                loader.show(getSupportFragmentManager(), LoaderDialogFragment.TAG);
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "BaseActivity showLoader() Exception() ");
        }
    }

    @Override
    public void hideLoader() {

        try {
            if (loader != null) {
                loader.dismiss();
            }
        } catch (Exception e) {
            Utility.PrintLog(TAG, "Loader Exception");
        }
    }

    @Override
    public void showMessageAndFinish(String message) {
        if (!TextUtils.isEmpty(message)) {
            Utility.showInfoSnackBar(message, findViewById(android.R.id.content), true);
            finishActivityAfterMessage(getViewActivity());
        }
    }

    @Override
    public void showSnackResponse(String message) {
        if (!TextUtils.isEmpty(message)) {
            Utility.showInfoSnackBar(message, findViewById(android.R.id.content), true);
        }
    }

    public <T extends BaseResponse> boolean checkResponseStatusWithMessage(T response, boolean showMessage) {

        boolean isResponseValid;

        if (response == null) {
            isResponseValid = false;
        } else {

            final String messageFromServer = response.getMessage();

            if (response.getStatus()) {
                isResponseValid = true;

                if (showMessage) {
                    // showSnackResponse(messageFromServer);
                }

            } else {
                isResponseValid = false;
                if (showMessage) {
                    //  showSnackErrorMessage(messageFromServer);
                }
            }

        }

        return isResponseValid;
    }

    @Override
    public Activity getViewActivity() {
        return this;
    }

    @Override
    public void showSnackErrorMessage(String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        TextView snackTextView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackTextView.setTypeface(ResourcesCompat.getFont(getViewActivity(), R.font.quicksand_regular));
        snackTextView.setMaxLines(4);

        if (true) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(getViewActivity(), R.color.colorErrorRed));
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(getViewActivity(), R.color.successGreen));
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            snackbar.show();
        }
    }

    @Override
    public void onCancelCall() {

    }

    protected void finishActivityAfterMessage(Activity context) {
        new Handler().postDelayed(context::finish, FORWARD_SCREEN_CHANGE_TIME);
    }

    @Override
    public void onClick(View v) {

    }

    public void showPaymentSuccessDilaog(String orderId, CartPageActivity mcontext) {
        addFirebaseLogEvent(Analytic.eventPurchase, Analytic.AddToCartScreen, Analytic.btnPurchase);
        callForConffiti(orderId);
        CreateDialogDialogFragment blurDialog = new CreateDialogDialogFragment(orderId, mcontext);
        blurDialog.show(getSupportFragmentManager(), "blurDialog");
    }

    private void callForConffiti(String orderId) {

        if (LiveActivity.liveActivity != null && LiveActivity.eventIdForConfetti != null && !LiveActivity.eventIdForConfetti.equals("")) {

            if (checkInternetConnectionWithMessage()) {

                Call<CommonResponse> call = apiService.doOrderEvent(userPreferences.getApiKey(), LiveActivity.eventIdForConfetti, orderId);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        LiveActivity.eventIdForConfetti = "";
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                    }
                });

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        runningActivity = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        runningActivity = true;
    }

    protected App application() {
        return (App) getApplication();
    }

    protected RtcEngine rtcEngine() {
        return application().rtcEngine();
    }

    public EngineConfig config() {
        return application().engineConfig();
    }

    protected StatsManager statsManager() {
        return application().statsManager();
    }

    protected void registerRtcEventHandler(EventHandler handler) {
        application().registerEventHandler(handler);
    }

    protected void removeRtcEventHandler(EventHandler handler) {
        application().removeEventHandler(handler);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {

    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {

    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {

    }

    @Override
    public void onClickPosition(int position) {

    }

    @Override
    public void RefreshAllAdapter() {

    }

    @Override
    public void RefreshTrendingFullScreenAdapter(List<TrendingStreamers> trendingStreamersListFullScreen) {

    }

    @Override
    public void RefreshFeaturedFullScreenAdapter(List<FeatureStreamers> featureStreamersListFullScreen) {

    }

    @Override
    public void RefreshNewStreamerFullScreenAdapter(List<NewStreamers> newStreamersListFullScreen) {

    }

    @Override
    public void MostPopularStreamer(List<MostPopularLivesResponse> mostPopularLivesResponses) {

    }

    @Override
    public void StreamerDetailScreenLive(List<AllStreamerData> allStreamerData) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void slideUp(View view, boolean isFalse) {
        long duration = 500;
        int height = view.getHeight();
        if (!isFalse) {
            height = 30;
            duration = 700;
        }

        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, height, 0);
        animate.setDuration(duration);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isFalse)
                    slideDown(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                view.getHeight());
        animate.setDuration(500);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public boolean isLogged() {
        return userPreferences != null && userPreferences.getCurrentUser();
    }

    public void checkErrorCode(int errorcode) {
        Utility.PrintLog(TAG, "error code : " + errorcode);

        if (errorcode == Networking.ErrorCode) {
            if (userPreferences != null && userPreferences.getUserId() != null) {

                if (homeActivity != null) {
                    homeActivity.disconnectSocket();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(SESSION_EXPIRE);
                //.setTitle("Warning");
                builder.setCancelable(false);
                builder.setPositiveButton(OK, (dialog, id) -> {
                    userPreferences.removeCurrentUser();

                    goToNextScreen(BaseActivity.this, SecondSplashScreen.class, true);
                    return;
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void checkSessionCode(int errorcode) {
        Utility.PrintLog(TAG, "error code : " + errorcode);

        if (errorcode == Networking.ErrorCode) {
            if (userPreferences != null && userPreferences.getUserId() != null) {

                if (homeActivity != null) {
                    homeActivity.disconnectSocket();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(SESSION_EXPIRE);
                //.setTitle("Warning");
                builder.setCancelable(false);
                builder.setPositiveButton(OK, (dialog, id) -> {
                    userPreferences.removeCurrentUser();
                    goToNextScreen(BaseActivity.this, SecondSplashScreen.class, true);
                    return;
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
            return;
        }
    }

    public void saveData(CommonResponse response) {

        userPreferences.saveCurrentUser(response);
        goToNextScreen(BaseActivity.this, HomeActivity.class, true);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public void languageSwitch(LinearLayout languageSwitch, boolean isEnableOrNot) {
        if (languageSwitch != null) {
            languageSwitch.setEnabled(isEnableOrNot);
        }
    }

    public void callLanguageAPI(String languageCode, boolean isCallInit, LinearLayout languageSwitch) {
        languageSwitch(languageSwitch, false);
        try {

            Call<String> call = apiService.doGetLanguage(EndPoints.API_KEY, languageCode);

            if (checkInternetConnectionWithMessage()) {
                if (!isFromSpashScreen) {
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
                                Utility.PrintLog(TAG, "json response :" + jsonObject.toString());
                                language.setLanguageCode(jsonObject.getString("language_code"));

                                if (jsonObject.has("labels")) {
                                    Object languageObject = jsonObject.get("labels");
                                    Utility.PrintLog(TAG, "language :" + jsonObject.toString());
                                    language.setLanguage(languageObject.toString());
                                    if (isCallInit) {


                                    } else hideLoader();

                                    languageSwitch(languageSwitch, true);

                                    if (!isFromSpashScreen) {
                                        //onClickRefreshActivity();
                                        recreate();
                                    }

                                    isFromSpashScreen = false;

                                    if (Constants.isFromProfileScreen) {
                                        Constants.isFromProfileScreen = false;
                                        callLanguagUpdateApi();
                                    }

                                }

                            } catch (JSONException e) {
                                languageSwitch(languageSwitch, true);
                                e.printStackTrace();
                                Utility.PrintLog(TAG, "exception : " + e.toString());
                            }
                        }

                        hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        languageSwitch(languageSwitch, true);
                        Utility.PrintLog(TAG, "onFailure language() : " + t.toString());
                        hideLoader();
                    }
                });
            } else languageSwitch(languageSwitch, true);

        } catch (Exception e) {
            languageSwitch(languageSwitch, true);
            Utility.PrintLog(TAG, "exception language() : " + e.toString());
            hideLoader();
        }
    }

    public void callLanguagUpdateApi() {

        try {

            Call<String> call = apiService.doUpdateLanguage(userPreferences.getApiKey(), userPreferences.getUserId(), language.getLanguageCode());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    Utility.PrintLog(TAG, response.toString());

                    checkErrorCode(response.code());


                    if (response.body() != null) {

                        if (response.isSuccessful()) {


                        } else {

                        }
                    }

                    hideLoader();
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                    Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                    hideLoader();
                }
            });
        } catch (Exception e) {

        }
    }

    public void setDefaultLanguage() {
        try {
            JSONObject jsonObject = new JSONObject(DefaultLanguage.SPENISH);
            language.setLanguageCode(jsonObject.getString("language_code"));


            if (jsonObject.has("labels")) {
                Object languageObject = jsonObject.get("labels");
                Utility.PrintLog(TAG, "default language :" + jsonObject.toString());
                language.setLanguage(languageObject.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utility.PrintLog(TAG, "Default language() : " + e.toString());
        }
    }

    @Override
    public void onNetworkConnected() {

    }

    @Override
    public void onNetworkHomeSocketConnect() {

    }

    public void showCartCount(TextView btnCartCount) {
        if (userPreferences != null && userPreferences.getTotalCartSize() != null && !userPreferences.getTotalCartSize().equals("") && !userPreferences.getTotalCartSize().equals("0")) {

            btnCartCount.setVisibility(View.VISIBLE);
            btnCartCount.setText(userPreferences.getTotalCartSize());
        } else btnCartCount.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Utility.PrintLog("checkActivityStatus" ,"onDestroy() = " + isApplicationSentToBackground(this));
    }


    public void callLiveStreamingAPI(String liveStreamingSlug, String count, Context mContext) {


        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);


                Call<CommonResponse> call = apiService.doLiveStreamingSlugData(userPreferences.getApiKey(), liveStreamingSlug, homeActivity.userPreferences.getUserId());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        PrintLog(TAG, "response : " + response);
                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (checkResponseStatusWithMessage(response.body(), true)) {
                                if (response.body() != null) {
                                    CommonResponse lievResponse = response.body();

                                    config().setChannelName(lievResponse.getSlugData().getLive_streaming_slug());

                                    try {

                                        startActivity(new Intent(mContext, LiveActivity.class)
                                                .putExtra(Constants.INTENT_KEY_STREAM_SLUG, liveStreamingSlug)
                                                .putExtra(Constants.INTENT_KEY_LIVE_WATCHER_COUNTS, count));

                                    } catch (Exception e) {
                                        Utility.PrintLog(TAG, "exception " + e.toString());
                                    }
                                }
                            } else
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                        }
                        hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            hideLoader();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean isAppInstalled(String package_name) {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo("" + package_name, PackageManager.GET_META_DATA);
            return true;

        } catch (PackageManager.NameNotFoundException e) {

            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addFirebaseLogEvent(String eventName, String screenName, String buttonName) {
        userPreferences = UserPreferences.newInstance(this);
        Bundle parameters = new Bundle();
        parameters.putString(Analytic.ANALYTICS_PARAM_SCREEN_NAME, screenName + "_android");
        parameters.putString(Analytic.ANALYTICS_PARAM_BUTTON_NAME, buttonName + "_android");
        // parameters.putString(Constants.ANALYTICS_PARAM_METHOD, methodName);
        parameters.putString(Analytic.ANALYTICS_PARAM_USER_ID, this.userPreferences.getUserId());
        parameters.putString(Analytic.ANALYTICS_PARAM_USER_NAME, this.userPreferences.getFullName());
        App.mFirebaseAnalytics.logEvent(eventName+"_android", parameters);
        Utility.PrintLog("addFirebaseLogEvent","events added");

    }

    public static class CreateDialogDialogFragment extends BlurDialogFragment {

        public static Dialog builder;
        private final boolean isForOrderDetails = true;
        String orderId;
        CartPageActivity mcontext = null;

        public CreateDialogDialogFragment(String orderId, CartPageActivity mcontext) {
            this.orderId = orderId;
            this.mcontext = mcontext;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            builder = new Dialog(getContext());
            builder.setContentView(R.layout.dialog_fragment_single_action);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            TextView payment_success = builder.findViewById(R.id.txt_message);
            TextView order_placed = builder.findViewById(R.id.order_placed);
            TextView btnOk = builder.findViewById(R.id.btnOk);

            KonfettiView conffitiView = builder.findViewById(R.id.viewKonfetti);
            DisplayMetrics display = new DisplayMetrics();
            //getWindowManager().getDefaultDisplay().getMetrics(display);
            btnOk.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    btnOk.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    conffitiView.build()
                            .addColors(colorsList)
                            .setDirection(0.0, 250)
                            .setSpeed(1f, 1f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(1000L)
                            .addShapes(Shape.Square.INSTANCE, Shape.Square.RECT, Shape.Circle.INSTANCE)
                            .addSizes(new Size(6, 1f))
                            //.setPosition(centreX,centreY)
                            .setPosition(-50f, conffitiView.getWidth() + 50f, -50f, -50f)
                            .streamFor(200, StreamEmitter.INDEFINITE);//);
                }
            });

            try {

                if (mcontext.language != null) {
                    payment_success.setText(mcontext.language.getLanguage(KEY.payment_successful));
                    order_placed.setText(mcontext.language.getLanguage(KEY.your_order_has_been_placed));
                    btnOk.setText(mcontext.language.getLanguage(KEY.ok));
                }

            } catch (Exception e) {

            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                builder.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            builder.setCancelable(false);
            builder.setCanceledOnTouchOutside(false);
            builder.findViewById(R.id.btnOk).setOnClickListener(v -> {
                conffitiView.stopGracefully();

                builder.dismiss();
                cartPageActivity.onBackPressed();
                /*new Handler().postDelayed(() -> */
                imageRefreshListener.goToProductDetailsScreen(orderId, false, true);/*, 100);*/

            });

            builder.show();

            /*conffitiView.build()
                    .addColors(colorsList)
                    .setDirection(0.0, 250)
                    .setSpeed(1f, 1f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(1000L)
                    .addShapes(Shape.Square.INSTANCE, Shape.Square.RECT, Shape.CIRCLE.INSTANCE)
                    .addSizes(new Size(6, 1f))
                    //.setPosition(centreX,centreY)
                    .setPosition(-50f, *//*conffitiView.getWidth()*//*display.widthPixels +  50f, -50f, -50f)
                    .streamFor(200, StreamEmitter.INDEFINITE*//*5000L*//*);//);*/


            builder.setOnKeyListener((dialog, keyCode, event) -> {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
                return false;
            });

            return builder;
        }

        protected BlurConfig blurConfig() {
            return new BlurConfig.Builder()
                    //.asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                    .debug(true)
                    .build();
        }
    }
}
