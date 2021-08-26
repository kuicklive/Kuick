package com.kuick.activity;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.StreamerProfileResponse;
import com.kuick.adapter.CommentsAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.common.Common;
import com.kuick.databinding.ActivityLiveBinding;
import com.kuick.fragment.BottomSheetDialog;
import com.kuick.interfaces.Internet;
import com.kuick.interfaces.LiveCountingListener;
import com.kuick.interfaces.LiveEventListener;
import com.kuick.livestreaming.RtcBaseActivity;
import com.kuick.livestreaming.stats.LocalStatsData;
import com.kuick.livestreaming.stats.RemoteStatsData;
import com.kuick.livestreaming.stats.StatsData;
import com.kuick.util.bubble_animation.MyBounceInterpolator;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.shareLive;
import static com.kuick.activity.HomeActivity.imageRefreshListener;
import static com.kuick.activity.HomeActivity.isLiveActivity;
import static com.kuick.activity.HomeActivity.openHome;
import static com.kuick.util.comman.Utility.PrintLog;

public class LiveActivity extends RtcBaseActivity implements LiveEventListener, LiveCountingListener, Internet {

    private static final String TAG = "LiveActivity";
    public static Internet internet;
    public static LiveCountingListener liveCountingListener;
    public static LiveEventListener liveEventListener;
    public static LiveActivity liveActivity;
    public static String eventId = "";
    public static String eventIdForConfetti = "";
    public static boolean isLiveIsRunning = false;
    public static String liveStreamerId = "";
    PictureInPictureParams.Builder pictureInPictureParamsBuilder = null;
    private ActivityLiveBinding binding;
    private CommentsAdapter mLiveAdapter;
    private String streamSlug;
    private List<CommonResponse.ProductData> productData;
    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    private int proviusId = -1;
    private OnSwipeTouchListener onSwipeTouchListener;
    private boolean onStopCalled;
    private List<CommonResponse.Comments> commentsList;
    private BottomSheetDialog bottomSheet;
    private boolean PIPhide = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initt();
        conffitiBubbleAnimation();

    }

    private void conffitiBubbleAnimation() {

        if (Common.socket != null && Common.socket.connected()) {
            Common.socket.on(Common.CONNECT_EVENT_ORDER_GENERATED_ON, onEventOrder);
        }

    }

    private final Emitter.Listener onEventOrder = args -> runOnUiThread(() -> {


        Utility.PrintLog("call", "socket.on onEventOrder = " + args[0].toString());
        if (liveActivity != null && !isInPictureInPictureMode && isLiveIsRunning) {
            bubbleAnimation(args[0].toString());
        }

    });

    private void initt() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
        liveEventListener = this;
        liveCountingListener = this;
        internet = this;
        binding = ActivityLiveBinding.inflate(getLayoutInflater());
        commentsList = new ArrayList<>();
        setContentView(binding.getRoot());
        setLanguageLable();
        liveActivity = this;
        eventId = "";
        eventIdForConfetti = "";
        init();
        initData();
        onClickListener();
        getIntentData();
        isLiveActivity = true;
        onSwipeTouchListener = new OnSwipeTouchListener(this, binding.liveVideoGridLayout);
        isLiveIsRunning = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
        }
    }

    private void bubbleAnimation(String data) {

        try {

            JSONObject jsonObject = new JSONObject(data);
            String eventId = jsonObject.getString("event_id");

            String viewerImage = jsonObject.getString("viewer_image");
            String streamerImage = jsonObject.getString("streamer_image");


            Handler handler = new Handler();
            Runnable runnable;
            Utility.showView(binding.circleProductImage);
            BaseActivity.showGlideImage(this, streamerImage, binding.circleProductImage);
            binding.circleProductImage.startAnimation(Utility.getBounceAnimaion(this));

            Utility.showView(binding.animationView);
            binding.animationView.setSpeed(0.5f);
            binding.animationView.playAnimation();

            runnable = () -> secondClick(viewerImage);
            handler.postDelayed(runnable, 300);

            //Set Buyer Name
            String viewerName = jsonObject.getString("viewer_name");
            String streamerName = jsonObject.getString("streamer_name");

            String buyerText = viewerName + " " + language.getLanguage(KEY.just_made_a_purchase_from) + " " + streamerName;
            Utility.showView(binding.buyerText);
            binding.buyerText.setText(buyerText);

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Bubble Animation Exception");
        }


    }

    private void secondClick(String viewerImage) {
        Handler handler = new Handler();
        Runnable runnable;

        Utility.showView(binding.circleStreamerProfile);
        BaseActivity.showGlideImage(this, viewerImage, binding.circleStreamerProfile);
        binding.circleStreamerProfile.startAnimation(Utility.getBounceAnimaion(this));
        runnable = () -> {
            Utility.hideView(binding.circleProductImage);
            Utility.hideView(binding.circleStreamerProfile);
            binding.buyerText.setText("");
            Utility.hideView(binding.buyerText);
            Utility.hideView(binding.animationView);
        };
        handler.postDelayed(runnable, 5000);
    }

    public void openDefaultBrowser(String url) {
        try {

            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(share, "Share link!"));

        } catch (Exception e) {
            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
        }

    }

    private void setLanguageLable() {

        try {

            if (language != null) {
                binding.txtCommentBox.setHint(language.getLanguage(KEY.type_comment_here));

            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (bottomSheet != null) {
            bottomSheet.dismiss();
        }

        rtcEngine().muteAllRemoteAudioStreams(binding.btnVolume.isSelected());

    }

    @Override
    protected void onResume() {
        super.onResume();
        onStopCalled = false;

        Utility.PrintLog(TAG, "Live Activity onResume()" + binding.btnVolume.isSelected());

        rtcEngine().muteAllRemoteAudioStreams(binding.btnVolume.isSelected());
    }

    private void getIntentData() {
        String watcherCount = getIntent().getStringExtra(Constants.INTENT_KEY_LIVE_WATCHER_COUNTS);
        if (watcherCount != null && !watcherCount.equals("") && !watcherCount.equals("0")) {
            binding.watcher.setText(Utility.setValidWatcherCount(watcherCount));
        }

        callLiveStreamingAPI();
    }

    private void sendComments() {

        if (Common.socket != null && Common.socket.connected()) {
            if (!binding.txtCommentBox.getText().toString().trim().equalsIgnoreCase("")) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Common.EVENT_ID, eventId);
                    jsonObject.put(Common.USER_ID, userPreferences.getUserId());
                    jsonObject.put(Common.USER_TYPE, Constants.USER);

                    jsonObject.put(Common.COMMENT, binding.txtCommentBox.getText().toString().trim());

                    Common.socket.emit(Common.CONNECT_COMMENT, jsonObject);


                    Utility.PrintLog("call", "add comment : " + jsonObject.toString());
                } catch (Exception e) {
                    Utility.PrintLog("call", "Exception = " + e.getMessage());
                }
            } else {
                return;
            }
        }
        binding.txtCommentBox.setText("");


    }

    public void connectSubscribe(String eventId) {
        try {
            if (Common.socket != null && Common.socket.connected()) {


                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put(Common.EVENT_ID, eventId);
                jsonObject1.put(Common.USER_ID, userPreferences.getUserId());
                Common.socket.emit(Common.CONNECT_CONNECT_SUBSCRIBER, jsonObject1);
                Utility.PrintLog("call", "connectSubscribe() jsonObject = " + jsonObject1.toString());
            } else {
                Utility.PrintLog("call", "connectSubscribe() socket null or not connected");
            }
        } catch (Exception e) {
            Utility.PrintLog("call", "connectSubscribe() exception = " + e.getMessage());
        }
    }

    private void init() {

        int role = io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE;

        binding.liveVideoGridLayout.setStatsManager(statsManager());
        rtcEngine().muteAllRemoteAudioStreams(false);
        binding.layValume.setActivated(true);

        rtcEngine().setClientRole(role);

    }

    private void startBroadcast() {
        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(0, true);
        binding.liveVideoGridLayout.addUserVideoSurface(0, surface, true);
        binding.btnVolume.setActivated(true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
        removeRtcVideo(0, true);
        binding.liveVideoGridLayout.removeUserVideo(0, true);
        binding.btnVolume.setActivated(false);
    }

    public void callLiveStreamingAPI() {

        streamSlug = getIntent().getStringExtra(Constants.INTENT_KEY_STREAM_SLUG);
        Utility.PrintLog(TAG, "streamSlug : " + streamSlug);

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<CommonResponse> call = apiService.doLiveStreamingSlugData(userPreferences.getApiKey(), streamSlug, userPreferences.getUserId());
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

                        if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful()) {
                            if (response.body() != null) {
                                setResponseData(response.body());
                            }
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

    @SuppressLint("SetTextI18n")
    private void setResponseData(CommonResponse response) {

        CommonResponse.SlugData slugData = response.getSlugData();
        if (slugData != null) {
            eventId = slugData.getId();
            eventIdForConfetti = slugData.getId();
            Utility.PrintLog("eventId", "eventId :" + eventId);
            connectSubscribe(eventId);
            productData = response.getProductData();
        }

        binding.btnPiP.setVisibility(View.VISIBLE);
        binding.btnHeart.setVisibility(View.VISIBLE);

        if (productData != null && productData.size() > 0) {
            binding.productView.setVisibility(View.VISIBLE);
            binding.btnCart.setVisibility(View.VISIBLE);

            CommonResponse.ProductData data = productData.get(0);
            BaseActivity.showGlideImage(this, data.getImage(), binding.productImg);
            binding.productName.setText(data.getName());
            binding.productPrice.setText(data.getPrice());

        } else {
            binding.productView.setVisibility(View.GONE);
            binding.btnCart.setVisibility(View.GONE);
        }

        StreamerProfileResponse streamerDetail = response.getStreamerProfileResponse();
        if (streamerDetail != null) {
            binding.streamerName.setText(streamerDetail.getFull_name());
            BaseActivity.showGlideImage(this, streamerDetail.getProfile_pic(), binding.userImage);
        }

        // for kuicker user
        if (streamerDetail.getType() != null && streamerDetail.getType().equals("kuicker")) {
            userPreferences.setKuickerId(streamerDetail.getId());

        } else {

            userPreferences.setKuickerId("0");
        }

        setCommentsAdapter(response);


    }


    private void showBottomDialog(List<CommonResponse.ProductData> productData) {
        BottomSheetDialog.instantiate(LiveActivity.this, "com.kuick.fragment.BottomSheetDialog");
        bottomSheet = new BottomSheetDialog(productData, this);
        bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
    }

    private void setCommentsAdapter(CommonResponse response) {

        commentsList = response.getComments();

        Log.e("commentsList", "commentsList => " + commentsList);
        Log.e("commentsList", "commentsList Size => " + commentsList.size());

        if (commentsList != null && commentsList.size() > 0) {
            LinearLayoutManager linerLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.rvComment.setLayoutManager(linerLayout);
            //Collections.reverse(comments);
            mLiveAdapter = new CommentsAdapter(commentsList);
            binding.rvComment.setAdapter(mLiveAdapter);
            binding.rvComment.scrollToPosition(mLiveAdapter.getItemCount() - 1);
            binding.rvComment.setVisibility(View.VISIBLE);

        }

    }

    private void onClickListener() {
        binding.btnClose.setOnClickListener(this);
        binding.btnSendComment.setOnClickListener(this);
        binding.btnVolume.setOnClickListener(this);
        binding.btnCart.setOnClickListener(this);
        binding.btnHeart.setOnClickListener(this);
        binding.btnPiP.setOnClickListener(this);
        binding.productImg.setOnClickListener(this);
        binding.productView.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Utility.PrintLog(TAG, " onBackPressed() " + isInPictureInPictureMode);
        Utility.PrintLog(TAG, " onBackPressed() ");

        if (PIPhide) {
            PIPhide = false;
            isLiveActivity = false;
            Utility.PrintLog("isLiveActivity", "LiveActivity onBackPressed() if" + isLiveActivity);
            userDisconnect();
            finish();
            goToNextScreen(LiveActivity.this, HomeActivity.class, true);
        } else {
            isLiveActivity = false;
            Utility.PrintLog("isLiveActivity", "LiveActivity onBackPressed() else" + isLiveActivity);
            userDisconnect();
            finish();
        }

        isLiveIsRunning = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void finishActivity() {
        isLiveActivity = false;
        isLiveIsRunning = false;
        Utility.PrintLog("isLiveActivity", "LiveActivity finishActivity()" + isLiveActivity);
        userDisconnect();
        finish();

        isForDiscover = true;
        goToNextScreen(this, HomeActivity.class, true);
    }

    public void finishWhenLiveRunningAndClickOnLiveNotification() {
        isLiveActivity = false;
        isLiveIsRunning = false;
        Utility.PrintLog("isLiveActivity", "LiveActivity finishActivity()" + isLiveActivity);
        userDisconnect();
        finish();
    }

    public void whenAppPause() {

        isLiveActivity = false;
        isLiveIsRunning = false;
        Utility.PrintLog("isLiveActivity", "LiveActivity finishActivity()" + isLiveActivity);
        userDisconnect();
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShare:
                openDefaultBrowser(shareLive + streamSlug);
                break;
            case R.id.btnClose:

                Utility.PrintLog(TAG, " onClose() PictureInPicture() " + isInPictureInPictureMode);
                Utility.PrintLog(TAG, " onClose()");

                if (PIPhide) {
                    PIPhide = false;
                    isLiveActivity = false;
                    Utility.PrintLog("isLiveActivity", "LiveActivity btnClose if" + isLiveActivity);
                    userDisconnect();
                    finish();
                    //startActivity(new Intent(this,HomeActivity.class));
                    goToNextScreen(LiveActivity.this, HomeActivity.class, true);
                } else {
                    isLiveActivity = false;
                    Utility.PrintLog("isLiveActivity", "LiveActivity btnClose else" + isLiveActivity);
                    userDisconnect();
                    finish();
                }

                break;
            case R.id.productView:
            case R.id.productImg:
            case R.id.btnCart:
                showBottomDialog(productData);
                break;
            case R.id.btnSendComment:
                Utility.hideKeyboard(this);
                sendComments();
                break;
            case R.id.btnVolume:

                binding.btnVolume.setSelected(!binding.btnVolume.isSelected());
                rtcEngine().muteAllRemoteAudioStreams(v.isActivated());
                v.setActivated(!v.isActivated());
                break;
            case R.id.btnHeart:

                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_animation);
                //  binding.btnHeart.startAnimation(myAnim);
                onClickHeartEmmit();
                binding.heartLayout.addFavor();
                break;
            case R.id.btnPiP:
                liveActivity.startPictureInPictureFeature();
                break;
        }
    }

    private void onClickHeartEmmit() {

        if (Common.socket != null && Common.socket.connected()) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Common.EVENT_ID, eventId);
                jsonObject.put(Common.USER_ID, userPreferences.getUserId());

                Common.socket.emit(Common.CONNECT_HEART_CLICK, jsonObject);


                Utility.PrintLog("call", "onClickHeartEmmit : " + jsonObject.toString());
            } catch (Exception e) {
                Utility.PrintLog("call", "Exception = " + e.getMessage());
            }
        }

    }

    private CommonResponse.Comments getUserComments() {

        CommonResponse.Comments comments = new CommonResponse.Comments();
        comments.setFull_name(userPreferences.getFullName());
        comments.setComment(binding.txtCommentBox.getText().toString());

        return comments;
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        runOnUiThread(() -> {
            Utility.PrintLog(TAG, "on join : " + rtcEngine().getConnectionState());
            removeRemoteUser(proviusId);
            proviusId = uid;
            renderRemoteUser(uid);
        });
    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        binding.liveVideoGridLayout.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {

        if (proviusId == -1) {
            return;
        }

        removeRtcVideo(uid, false);
        binding.liveVideoGridLayout.removeUserVideo(uid, false);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }


    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void onLocalVoiceInteractionStopped() {
        super.onLocalVoiceInteractionStopped();
    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine().switchCamera();
    }

    @Override
    public void finish() {
        statsManager().clearAllData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        } else {
            super.finish();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        onStopCalled = true;
        if (isInPictureInPictureMode) {
            Utility.PrintLog(TAG, " onStop() PictureInPicture()");
            isInPictureInPictureMode = false;
            userDisconnect();
            finish();
        }

        Utility.PrintLog(TAG, " onStop()");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            // do what you want with the power button
            Utility.PrintLog("onStop()", " PowerButton" + isApplicationSentToBackground(this));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initData() {
        mVideoDimension = Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }

    @Override
    public void onClickComment(CommonResponse.Comments comments) {

        binding.txtCommentBox.setText("");

        if (mLiveAdapter == null) {

            LinearLayoutManager linerLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.rvComment.setLayoutManager(linerLayout);

            commentsList.add(comments);
            mLiveAdapter = new CommentsAdapter(commentsList);
            binding.rvComment.setAdapter(mLiveAdapter);
            binding.rvComment.setVisibility(View.VISIBLE);

        } else {
            commentsList.add(comments);
            mLiveAdapter.notifyDataSetChanged();
            binding.rvComment.scrollToPosition(mLiveAdapter.getItemCount() - 1);

            binding.rvComment.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void watcherUpdate(String count) {
        if (count != null && !count.equals("") && !count.equals("0")) {
            binding.watcher.setText(count);
        }

    }

    @Override
    public void startPictureInPicture(String product_id, boolean destination, boolean b) {
        if (bottomSheet != null) {
            bottomSheet.dismiss();
        }
        liveActivity.startPictureInPictureFeature();

        new Handler().postDelayed(() -> imageRefreshListener.goToProductDetailsScreen(product_id, destination, false), 200);

    }

    @Override
    public void refreshCount(String count) {
        if (count != null && !count.equals("") && !count.equals("0")) {
            binding.watcher.setText(count);
        }
    }

    private void openDialogOffline(String message) {

        Utility.PrintLog(TAG, "openDialogOffline");

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, 0);
            builder.setCancelable(false);
            //builder.setTitle(title);
            builder.setMessage(message);
            String positiveText = "Ok";
            builder.setPositiveButton(positiveText, null);

            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.bgSplash)));
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                    if (HomeActivity.openHome != null) {
                        Constants.isFromConnectionLost = true;
                        openHome.home();
                    }
                }
            });
        } catch (Exception e) {
            Utility.PrintLog(TAG, "exception " + e.toString());
        }

    }

    @Override
    public void showDialog() {
        openDialogOffline(language.getLanguage(KEY.streamer_went_offline));
    }

    @Override
    public void onHeartClick(String totalCount, String user_id, String event_id) {


        if (eventId != null && !eventId.equals("") && event_id != null && !event_id.equals("") && eventId.equals(event_id)) {
            if (totalCount != null && !totalCount.equals("")) {
                binding.txtHeartButtonPressCount.setText(totalCount);
            }
            if (!user_id.equals(userPreferences.getUserId())) {
                binding.heartLayout.addFavor();
                Utility.PrintLog(TAG, "onClick Heart");
            }

        }
    }

    @Override
    public void totalCount(String totalCount, String event_id) {

        if (eventId != null && !eventId.equals("") && event_id != null && !event_id.equals("") && eventId.equals(event_id)) {
            if (totalCount != null && !totalCount.equals("")) {
                binding.txtHeartButtonPressCount.setText(totalCount);
            }

        }
    }

    @Override
    public void internetLost() {
        openDialogOffline("Your internet connection has been lost");
    }


    public void startPictureInPictureFeature() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Utility.PrintLog("onStop()", "startPictureInPictureFeature");
            hideViewWhenPiP(true);
            Rational aspectRatio = new Rational(binding.liveVideoGridLayout.getWidth(), binding.liveVideoGridLayout.getHeight());
            pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
            enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
        } else {

            showSnackErrorMessage("PIP Mode not support on this device");
        }
    }

    @Override
    public void onUserLeaveHint() {
        Utility.PrintLog("leaveItPIP", "close");
        setLanguageLable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isInPictureInPictureMode()) {
                Rational aspectRatio = new Rational(binding.liveVideoGridLayout.getWidth(), binding.liveVideoGridLayout.getHeight());
                pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
                enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
            }
        }
    }

    public void hideViewWhenPiP(boolean isHide) {

        if (isHide) {

            binding.parentRelativeView.setVisibility(View.GONE);
            binding.actionView.setVisibility(View.GONE);

        } else {
            binding.parentRelativeView.setVisibility(View.VISIBLE);
            binding.actionView.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {

        Utility.PrintLog("onPictureInPictureModeChanged", "newConfig : " + newConfig);
        if (isInPictureInPictureMode) {
            BaseActivity.isInPictureInPictureMode = true;
            hideViewWhenPiP(true);
            PIPhide = false;

        } else {

            binding.liveVideoGridLayout.setLayoutParams(new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
            new Handler().postDelayed(() -> hideViewWhenPiP(false), 1000);
            BaseActivity.isInPictureInPictureMode = false;
            PIPhide = true;
            refreshProductPrice();
        }
    }

    private void refreshProductPrice() {

        if (productData != null && productData.size() > 0) {
            CommonResponse.ProductData data = productData.get(0);
            binding.productPrice.setText(data.getPrice());
        }

    }

    public void changeFullView() {
        binding.liveVideoGridLayout.setVisibility(View.GONE);
        binding.parentView.setBackgroundResource(R.color.bgSplash);
    }


    public static class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        Context context;
        onSwipeListener onSwipe;

        OnSwipeTouchListener(Context ctx, View mainView) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            mainView.setOnTouchListener(this);
            context = ctx;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        void onSwipeRight() {
            this.onSwipe.swipeRight();
        }

        void onSwipeLeft() {
            this.onSwipe.swipeLeft();
        }

        void onSwipeTop() {
            this.onSwipe.swipeTop();
        }

        void onSwipeBottom() {
            liveActivity.startPictureInPictureFeature();
            this.onSwipe.swipeBottom();
        }

        interface onSwipeListener {
            void swipeRight();

            void swipeTop();

            void swipeBottom();

            void swipeLeft();
        }

        public class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
    }
}