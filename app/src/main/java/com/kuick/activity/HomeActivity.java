package com.kuick.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuick.R;
import com.kuick.Response.AllStreamerData;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.SplashScreen.SecondSplashScreen;
import com.kuick.base.BaseActivity;
import com.kuick.common.Common;
import com.kuick.common.SocketSingleObject;
import com.kuick.databinding.ActivityHomeBinding;
import com.kuick.fragment.DiscoverFragment;
import com.kuick.fragment.VideoClipsFragment;
import com.kuick.interfaces.ImageRefreshListener;
import com.kuick.interfaces.OpenHome;
import com.kuick.model.BannerDetails;
import com.kuick.model.FeatureStreamers;
import com.kuick.model.NewStreamers;
import com.kuick.model.RecommendedStreamers;
import com.kuick.model.TrendingStreamers;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.API_KEY;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_ID;
import static com.kuick.activity.CartPageActivity.cartPageActivity;
import static com.kuick.activity.FeaturedStreamersFullScreen.onClickFeturedFullScreen;
import static com.kuick.activity.LiveActivity.eventId;
import static com.kuick.activity.LiveActivity.liveActivity;
import static com.kuick.activity.LiveActivity.liveCountingListener;
import static com.kuick.activity.LiveActivity.liveEventListener;
import static com.kuick.activity.NewStreamersFullScreen.onClickNewStreamerFullScreen;
import static com.kuick.activity.ProductDetailsActivity.productDetailsActivity;
import static com.kuick.activity.StreamerDetailsActivity.streamerDetailLive;
import static com.kuick.activity.StreamerDetailsActivity.streamerDetailLiveList;
import static com.kuick.activity.StreamerDetailsActivity.streamerDetailsActivity;
import static com.kuick.activity.TopCategoriesLivesActivity.isTopCategoriesActivity;
import static com.kuick.activity.TopCategoriesLivesActivity.topCategoriesListener;
import static com.kuick.activity.TopCategoriesLivesActivity.topCategoryLiveList;
import static com.kuick.activity.TrendingStreamersFullScreen.onClickTrendingFullScreen;
import static com.kuick.base.BaseActivity.CreateDialogDialogFragment.builder;
import static com.kuick.fragment.DiscoverFragment.mostPopularListener;
import static com.kuick.fragment.DiscoverFragment.mostPopularLives;
import static com.kuick.fragment.HomeFragment.bannerDetails;
import static com.kuick.fragment.HomeFragment.featureStreamers;
import static com.kuick.fragment.HomeFragment.homeFragmentClickEventListener;
import static com.kuick.fragment.HomeFragment.newStreamers;
import static com.kuick.fragment.HomeFragment.recommendedStreamers;
import static com.kuick.fragment.HomeFragment.trendingStreamersList;
import static com.kuick.util.comman.Constants.ADMIN_END;
import static com.kuick.util.comman.Constants.INTENT_KEY_DIRECTION_CODE;
import static com.kuick.util.comman.Constants.SCREEN;
import static com.kuick.util.comman.Constants.USER_END;
import static com.kuick.util.comman.Constants.WEBVIEW_SCREEN;
import static com.kuick.util.comman.Constants.isHomeActivityForNotification;
import static com.kuick.util.comman.Utility.PrintLog;

public class HomeActivity extends BaseActivity implements ImageRefreshListener, OpenHome {

    private static final String TAG = "HomeActivity";
    public static OpenHome openHome;
    public static ImageRefreshListener imageRefreshListener;
    public static HomeActivity homeActivity;
    public static boolean isLiveActivity = false;
    public static ArrayList<String> liveStreamingLiveIdDashboard = new ArrayList<>();
    private final Map<String, String> liveWatcherCountDashboard = new HashMap<>();
    private final Emitter.Listener onDisconnect = args -> runOnUiThread(() -> {

        if (Common.socket == null) {
            Utility.PrintLog("call", "socket.on onDisconnect");
        }
    });
    private final Emitter.Listener onHeartClick = args -> {
        runOnUiThread(() -> {

            try {
                JSONObject jsonObject = new JSONObject(args[0].toString());
                Utility.PrintLog("call", "onHeartClick() On" + jsonObject.toString());

                if (jsonObject != null) {
                    String count = jsonObject.getString("count");
                    String userId = jsonObject.getString("user_id");
                    String event_id = jsonObject.getString("event_id");

                    Utility.PrintLog(TAG, "User id - " + userId);
                    Utility.PrintLog(TAG, "Event id - " + eventId);

                    if (liveCountingListener != null) {
                        liveCountingListener.onHeartClick(count, userId, event_id);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();

                Utility.PrintLog("call", "onHeartClick() Exception" + e.toString());
            }

        });
    };
    private final Emitter.Listener onStreamerUserEvent = args -> runOnUiThread(() ->
    {

        if (args[0] != null && args[0].toString().length() > 0) {
            Utility.PrintLog("call", "socket.on onStreamerUserEvent = " + args[0].toString());

            try {

                JSONObject jsonObject = new JSONObject(args[0].toString());

                if (jsonObject != null) {
                    String id = jsonObject.getString("user_id");
                    Utility.PrintLog("mostPopularLives", "new live user id : " + id);
                    String live_streaming_slug = jsonObject.getString("live_streaming_slug");
                    String count = jsonObject.getString("count");

                    //for most popular live streamer-----------------------------
                    if (mostPopularLives != null && mostPopularLives.size() > 0) {
                        Utility.PrintLog("mostPopularLives", "mostPopularLives array size : " + mostPopularLives.size());

                        for (int k = 0; k < mostPopularLives.size(); k++) {
                            MostPopularLivesResponse list = mostPopularLives.get(k);
                            Utility.PrintLog("mostPopularLives", "mostPopularLives array id : " + list.getId());

                            if (list.getId() != null && list.getId().equals(id)) {
                                mostPopularLives.get(k).setIs_live("1");
                                mostPopularLives.get(k).setLive_streaming_slug(live_streaming_slug);
                                mostPopularLives.get(k).setCount(count);
                                break;
                            }
                        }
                    }


                    //for top categories live streamer-----------------------------
                    if (topCategoryLiveList != null && topCategoryLiveList.size() > 0) {
                        for (int k = 0; k < topCategoryLiveList.size(); k++) {
                            MostPopularLivesResponse list = topCategoryLiveList.get(k);

                            if (list.getId() != null && list.getId().equals(id)) {
                                topCategoryLiveList.get(k).setIs_live("1");
                                topCategoryLiveList.get(k).setLive_streaming_slug(live_streaming_slug);
                                topCategoryLiveList.get(k).setCount(count);
                                break;
                            }
                        }
                    }

                    // for streamer details upcoming lives---------------------------
                    if (streamerDetailLiveList != null && streamerDetailLiveList.size() > 0) {
                        for (int k = 0; k < streamerDetailLiveList.size(); k++) {
                            AllStreamerData list = streamerDetailLiveList.get(k);

                            if (list.getId() != null && list.getId().equals(id)) {
                                streamerDetailLiveList.get(k).setIsLive("1");
                                streamerDetailLiveList.get(k).setLive_streaming_slug(live_streaming_slug);
                                streamerDetailLiveList.get(k).setCount(count);
                                break;
                            }
                        }
                    }

                    if (mostPopularListener != null) {
                        mostPopularListener.MostPopularStreamer(mostPopularLives);
                    }

                    if (topCategoriesListener != null) {
                        topCategoriesListener.MostPopularStreamer(topCategoryLiveList);
                    }

                    if (streamerDetailLive != null) {
                        streamerDetailLive.StreamerDetailScreenLive(streamerDetailLiveList);
                    }

                }
            } catch (Exception e) {

            }

        }
    });
    private final Emitter.Listener onConnectSubscriber = args -> runOnUiThread(() ->
    {

        if (args[0] != null && args[0].toString().length() > 0) {
            Utility.PrintLog("call", "socket.on onConnectSubscriber = " + args[0].toString());

            try {
                JSONObject jsonObject = new JSONObject(args[0].toString());

                if (jsonObject != null) {

                    if (jsonObject.has("is_live")) {
                        Constants.isLiveStatus = jsonObject.getString("is_live");
                        Utility.PrintLog("isLiveStatus", "status : " + jsonObject.getString("is_live"));

                        if (!jsonObject.getString("is_live").equals("") && jsonObject.getString("is_live").equals("false")) {
                            if (liveCountingListener != null) {
                                liveCountingListener.showDialog();
                            }
                        }
                    }


                }

            } catch (JSONException e) {
                Utility.PrintLog(TAG, "exception()" + e.toString());
                e.printStackTrace();
            }
        }
    });
    private final Emitter.Listener onComment = args -> runOnUiThread(() ->
    {

        try {

            if (args[0] != null && args[0].toString().length() > 0) {
                Utility.PrintLog("call", "socket.on onComment = " + args[0].toString());
                JSONObject jsonObject = new JSONObject(args[0].toString());

                if (jsonObject != null) {
                    CommonResponse.Comments comments = new CommonResponse.Comments();

                    String comment = "", first_name = "", last_name = "", profile_image = "", userType = "";

                    if (jsonObject.has("comment")) {
                        comment = jsonObject.getString("comment");
                        userType = jsonObject.getString("user_type");
                    }

                    if (jsonObject.has("user_type")) {
                        userType = jsonObject.getString("user_type");
                    }

                    if (jsonObject.has("event_id")) {
                        String event_id = jsonObject.getString("event_id");

                        if (eventId != null && !eventId.equals("")) {
                            if (!event_id.equals(eventId)) {
                                return;
                            }
                        }
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("user");

                    if (jsonArray != null && jsonArray.length() > 0) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        if (jsonObject1 != null) {
                            if (jsonObject1.has("first_name")) {
                                first_name = jsonObject1.getString("first_name");
                            }

                            if (jsonObject1.has("last_name")) {
                                last_name = jsonObject1.getString("last_name");
                            }

                            if (jsonObject1.has("profile_image")) {
                                profile_image = jsonObject1.getString("profile_image");
                            }

                            String fullName = "";

                            if (jsonObject1.has("first_name") || jsonObject1.has("last_name")) {

                                if (first_name != null && !first_name.equalsIgnoreCase("")) {
                                    if (last_name != null && !last_name.equalsIgnoreCase("")) {
                                        fullName = first_name + " " + last_name;

                                    } else {
                                        fullName = first_name;
                                    }

                                } else {

                                    if (last_name != null && !last_name.equalsIgnoreCase("")) {
                                        fullName = last_name;

                                    } else {
                                        fullName = "";
                                    }
                                }

                            } else {

                                if (jsonObject1.has("full_name")) {
                                    fullName = jsonObject1.getString("full_name");
                                }
                            }

                            if (profile_image != null && !profile_image.equals("") && !profile_image.equals("null")) {
                                profile_image = userPreferences.getImageUrl() + profile_image;

                            } else {

                                if (userType.equals(Constants.USER)) {
                                    profile_image = userPreferences.getImageUrl() + USER_END;

                                } else {

                                    profile_image = userPreferences.getImageUrl() + ADMIN_END;
                                }
                            }

                            //String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(comment);

                            comments.setComment(comment);
                            comments.setFull_name(fullName);
                            comments.setProfile_image(profile_image);

                            Utility.PrintLog(TAG, "Profile image : " + profile_image);
                            Utility.PrintLog(TAG, "user profile : " + userPreferences.getProfileImage());
                            liveEventListener.onClickComment(comments);

                        }
                    }
                }
            }

        } catch (Exception e) {
            Utility.PrintLog("call", "exception onComment = " + e.getMessage());
        }
    });
    private final Emitter.Listener onStartLiveStreaming = args -> runOnUiThread(new Runnable() {
        @Override
        public void run() {

            if (args[0] != null && args[0].toString().length() > 0) {
                Utility.PrintLog("call", "socket.on onStartLiveStreaming = " + args[0].toString());

                try {

                    JSONObject jsonObject = new JSONObject(args[0].toString());

                    if (jsonObject != null) {
                        String id = jsonObject.getString("user_id");
                        Utility.PrintLog("mostPopularLives", "new live user id : " + id);

                        String live_streaming_slug = jsonObject.getString("live_streaming_slug");
                        String count = jsonObject.getString("count");

                        //for recommended streamer----------------------------------------
                      /*  if (recommendedStreamers != null && recommendedStreamers.size() > 0) {
                            for (int k = 0; k < recommendedStreamers.size(); k++) {
                                RecommendedStreamers list = recommendedStreamers.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    recommendedStreamers.get(k).setIs_live("1");
                                    recommendedStreamers.get(k).setLive_streaming_slug(live_streaming_slug);
                                    recommendedStreamers.get(k).setCount(count);
                                    break;
                                }
                            }
                        }


                        //for swipe banner--------------------------------------------
                        if (bannerDetails != null && bannerDetails.size() > 0) {
                            for (int k = 0; k < bannerDetails.size(); k++) {
                                BannerDetails list = bannerDetails.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    bannerDetails.get(k).setLive_streaming_slug(live_streaming_slug);
                                    bannerDetails.get(k).setCount(count);
                                    break;
                                }
                            }
                        }


                        //for newStreamer----------------------------------------------------
                        if (newStreamers != null && newStreamers.size() > 0) {
                            for (int k = 0; k < newStreamers.size(); k++) {
                                NewStreamers list = newStreamers.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    newStreamers.get(k).setIs_live("1");
                                    newStreamers.get(k).setLive_streaming_slug(live_streaming_slug);
                                    newStreamers.get(k).setCount(count);
                                    break;
                                }
                            }
                        }


                        //for trending streamers ----------------------------------------------------
                        if (trendingStreamersList != null && trendingStreamersList.size() > 0) {
                            for (int k = 0; k < trendingStreamersList.size(); k++) {
                                TrendingStreamers list = trendingStreamersList.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    trendingStreamersList.get(k).setIs_live("1");
                                    trendingStreamersList.get(k).setLive_streaming_slug(live_streaming_slug);
                                    trendingStreamersList.get(k).setCount(count);
                                    break;
                                }
                            }
                        }


                        //for featured streamers ----------------------------------------------------
                        if (featureStreamers != null && featureStreamers.size() > 0) {
                            for (int k = 0; k < featureStreamers.size(); k++) {
                                FeatureStreamers list = featureStreamers.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    featureStreamers.get(k).setIs_live("1");
                                    featureStreamers.get(k).setLive_streaming_slug(live_streaming_slug);
                                    featureStreamers.get(k).setCount(count);
                                    break;
                                }
                            }
                        }*/


                        if (fragment instanceof DiscoverFragment) {
                            ((DiscoverFragment) fragment).addSingleNewLiveShort(jsonObject);
                        }

                        if (isTopCategoriesActivity != null) {
                            isTopCategoriesActivity.addSingleNewLiveShort(jsonObject);
                        }
                        if (streamerDetailsActivity != null) {
                            streamerDetailsActivity.addSingleNewLiveShort(jsonObject);
                        }

                        refreshAllAdapter();
                    }

                } catch (Exception e) {
                    Utility.PrintLog("call", "onStartStreaming() Exception :  = " + args[0].toString());
                }

            }
        }
    });
    private final Emitter.Listener onNormalStopLiveStreaming = args -> runOnUiThread(new Runnable() {
        @Override
        public void run() {

            if (args[0] != null && args[0].toString().length() > 0) {
                Utility.PrintLog("call", "socket.on onNormalStopLiveStreaming = " + args[0].toString());

                try {

                    JSONObject jsonObject = new JSONObject(args[0].toString());

                    if (jsonObject != null) {
                        String id = jsonObject.getString("user_id");
                        String event_id = jsonObject.getString("event_id");

                        //when app is in background

                        Utility.PrintLog("isApplicationSentToBackground", "is When Home Activity " + isApplicationSentToBackground(HomeActivity.this));


                        // for recommended streamer------------------------------------
                        if (recommendedStreamers != null && recommendedStreamers.size() > 0) {
                            for (int k = 0; k < recommendedStreamers.size(); k++) {
                                RecommendedStreamers list = recommendedStreamers.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    recommendedStreamers.get(k).setIs_live("0");
                                    recommendedStreamers.get(k).setLive_streaming_slug(null);
                                    recommendedStreamers.get(k).setCount(null);
                                    break;
                                }
                            }
                        }


                        //for swipe banner--------------------------------------------
                        if (bannerDetails != null && bannerDetails.size() > 0) {
                            for (int k = 0; k < bannerDetails.size(); k++) {
                                BannerDetails list = bannerDetails.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    bannerDetails.get(k).setLive_streaming_slug(null);
                                    bannerDetails.get(k).setCount(null);
                                    break;
                                }
                            }
                        }

                        //for new streamer-------------------------------------------
                        if (newStreamers != null && newStreamers.size() > 0) {
                            for (int k = 0; k < newStreamers.size(); k++) {
                                NewStreamers list = newStreamers.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    newStreamers.get(k).setIs_live("0");
                                    newStreamers.get(k).setLive_streaming_slug(null);
                                    newStreamers.get(k).setCount(null);
                                    break;
                                }
                            }
                        }


                        //for trending streamer -------------------------------------
                        if (trendingStreamersList != null && trendingStreamersList.size() > 0) {
                            for (int k = 0; k < trendingStreamersList.size(); k++) {
                                TrendingStreamers list = trendingStreamersList.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    trendingStreamersList.get(k).setIs_live("0");
                                    trendingStreamersList.get(k).setLive_streaming_slug(null);
                                    trendingStreamersList.get(k).setCount(null);
                                    break;
                                }
                            }
                        }


                        //for featured streamer -------------------------------------
                        if (featureStreamers != null && featureStreamers.size() > 0) {
                            for (int k = 0; k < featureStreamers.size(); k++) {
                                FeatureStreamers list = featureStreamers.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    featureStreamers.get(k).setIs_live("0");
                                    featureStreamers.get(k).setLive_streaming_slug(null);
                                    featureStreamers.get(k).setCount(null);
                                    break;
                                }
                            }
                        }


                        //for most popular live streamer-----------------------------
                        if (mostPopularLives != null && mostPopularLives.size() > 0) {
                            for (int k = 0; k < mostPopularLives.size(); k++) {
                                MostPopularLivesResponse list = mostPopularLives.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    mostPopularLives.get(k).setIs_live("0");
                                    mostPopularLives.get(k).setLive_streaming_slug(null);
                                    mostPopularLives.get(k).setCount(null);
                                    break;
                                }
                            }
                        }


                        //for top categories live streamer-----------------------------
                        if (topCategoryLiveList != null && topCategoryLiveList.size() > 0) {
                            for (int k = 0; k < topCategoryLiveList.size(); k++) {
                                MostPopularLivesResponse list = topCategoryLiveList.get(k);

                                if (list.getId() != null && list.getId().equals(id)) {
                                    topCategoryLiveList.get(k).setIs_live("0");
                                    topCategoryLiveList.get(k).setLive_streaming_slug(null);
                                    topCategoryLiveList.get(k).setCount(null);
                                    break;
                                }
                            }
                        }


                        if (isLiveActivity) //if user whatch live streaming and stream close live than it happen
                        {
                            isLiveActivity = false;
                            Utility.PrintLog("isLiveActivity", "HomeActivity onNormalStopLive" + isLiveActivity);

                            if (liveActivity != null) {
                                Utility.PrintLog("liveActivity", "eventId " + eventId);
                                Utility.PrintLog("liveActivity", "id " + event_id);
                                if (eventId != null && !eventId.equals("")) {
                                    if (event_id.equals(eventId)) {
                                        if (isApplicationSentToBackground(HomeActivity.this)) {
                                            liveActivity.whenAppPause();

                                            return;
                                        } else {

                                            liveActivity.finishActivity();
                                        }
                                    }
                                }

                            }
                        }

                        //refresh all adapter
                        refreshAllAdapter();

                    }

                } catch (Exception e) {
                    Utility.PrintLog("call", "stopLive() Exception : " + e.toString());
                }

            }
        }
    });
    private final Emitter.Listener onCurrentLiveEvent = args -> runOnUiThread(() ->
    {

        if (args[0] != null && args[0].toString().length() > 0) {
            Utility.PrintLog("call", "connectUser() jsonObject = socket.on onCurrentLiveEvent() = " + args[0].toString());

            try {

                String jsonString = args[0].toString();
                JSONObject jsonResult = null;

                try {

                    jsonResult = new JSONObject(jsonString);

                    if (fragment instanceof DiscoverFragment) {
                        ((DiscoverFragment) fragment).addLiveShort(jsonResult);
                    }

                    StreamerDetailsActivity.addLiveShort(jsonResult);
                    TopCategoriesLivesActivity.addLiveShort(jsonResult);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Utility.PrintLog(TAG, "Exception socket.on onCurrentLiveEvent() : " + e.toString());

            }
        }
    });
    private final Emitter.Listener onCountEventSubscribe = args -> runOnUiThread(() -> {

        try {

            if (args[0] != null && args[0].toString().length() > 0) {
                Utility.PrintLog("call", "socket.on onCountEventSubscribe = " + args[0].toString());

                JSONObject jsonObject = new JSONObject(args[0].toString());
                JSONObject data = jsonObject.getJSONObject("data");

                for (int k = 0; k < data.length(); k++) {

                    String count = data.getString("count");
                    String event_id = data.getString("event_id");
                    Utility.PrintLog(TAG, "onCountEventSubscribe count" + count);

                    //for most popular live streamer---------------------------
                    if (mostPopularLives != null && mostPopularLives.size() > 0) {

                        for (int i = 0; i < mostPopularLives.size(); i++) {
                            String isLive = mostPopularLives.get(i).getIs_live();
                            String id = mostPopularLives.get(i).getId();

                            if (event_id != null && isLive != null && isLive.equals("1") && event_id.equals(id)) {
                                Utility.PrintLog("call", "onCountEventSubscribe = streamer id = " + id);

                                mostPopularLives.get(i).setCount(count);
                                Utility.PrintLog(TAG, "onCountEventSubscribe set count" + count);
                            }
                        }
                    }

                    //for top categories live streamer---------------------------
                    if (topCategoryLiveList != null && topCategoryLiveList.size() > 0) {

                        for (int i = 0; i < topCategoryLiveList.size(); i++) {
                            String isLive = topCategoryLiveList.get(i).getIs_live();
                            String id = topCategoryLiveList.get(i).getId();

                            if (event_id != null && isLive != null && isLive.equals("1") && event_id.equals(id)) {
                                topCategoryLiveList.get(i).setCount(count);
                            }
                        }
                    }

                    //for streamer details upcomming live---------------------------
                    if (streamerDetailLiveList != null && streamerDetailLiveList.size() > 0) {

                        for (int i = 0; i < streamerDetailLiveList.size(); i++) {
                            String isLive = streamerDetailLiveList.get(i).getIsLive();
                            String id = streamerDetailLiveList.get(i).getId();

                            if (event_id != null && isLive != null && isLive.equals("1") && event_id.equals(id)) {
                                streamerDetailLiveList.get(i).setCount(count);
                                Utility.PrintLog(TAG, "onCountEventSubscribe set count" + count);
                            }
                        }
                    }

                    if (mostPopularListener != null) {
                        mostPopularListener.MostPopularStreamer(mostPopularLives);
                    }
                    if (topCategoriesListener != null) {
                        topCategoriesListener.MostPopularStreamer(topCategoryLiveList);
                    }
                    if (streamerDetailLive != null) {
                        streamerDetailLive.StreamerDetailScreenLive(streamerDetailLiveList);
                    }
                }
            }

        } catch (Exception e) {
            Utility.PrintLog("call", "onCountEventSubscribe() Exception  = " + e.toString());
        }

    });
    private final Emitter.Listener onIntervalCall = args -> runOnUiThread(() ->
    {


        if (args[0] != null && args[0].toString().length() > 0) {
            Utility.PrintLog("call", "onIntervalCall = " + args[0]);
        } else {
            Utility.PrintLog("call", "onIntervalCall");
        }

    });

    public Map<String, String> liveStreamingSlugDashboard = new HashMap<>();

    private final Emitter.Listener onNormalUserCount = args -> runOnUiThread(new Runnable() {
        @Override
        public void run() {
            Utility.PrintLog("call", "onNormalUserCount = " + args[0].toString());

            try {

                String count = "", streamerId = "";

                if (args[0] != null && args[0].toString().length() > 0) {
                    Utility.PrintLog(TAG, "socket.on onNormalUserCount = " + args[0].toString());
                    JSONObject jsonObject = new JSONObject(args[0].toString());

                    if (jsonObject != null && jsonObject.has("count")) {
                        Utility.PrintLog(TAG, "watcherUpdate() = " + args[0].toString());
                        count = jsonObject.getString("count");
                        streamerId = jsonObject.getString("user_id");
                        Utility.PrintLog(TAG, "recommended streamer count" + count);


                        //for recommended streamers----------------------------------------
                        if (recommendedStreamers != null && recommendedStreamers.size() > 0) {

                            for (int i = 0; i < recommendedStreamers.size(); i++) {
                                String isLive = recommendedStreamers.get(i).getIs_live();
                                String id = recommendedStreamers.get(i).getId();

                                if (isLive != null && isLive.equals("1") && streamerId.equals(id)) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        recommendedStreamers.get(i).setCount(count);
                                    }
                                }
                            }
                        }


                        //for swipe banner
                        if (bannerDetails != null && bannerDetails.size() > 0) {
                            Utility.PrintLog("call", "bannerDetails : " + bannerDetails);

                            for (int i = 0; i < bannerDetails.size(); i++) {
                                String id = bannerDetails.get(i).getId();

                                if (streamerId.equals(id)) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        Utility.PrintLog("call", "match id  :  " + id);
                                        bannerDetails.get(i).setCount(count);
                                    }
                                }
                            }
                        }


                        // featured streamer--------------------------------------------
                        if (featureStreamers != null && featureStreamers.size() > 0) {
                            Utility.PrintLog("call", "newStreamers : " + featureStreamers);

                            for (int i = 0; i < featureStreamers.size(); i++) {
                                String isLive = featureStreamers.get(i).getIs_live();
                                String id = featureStreamers.get(i).getId();

                                if (isLive != null && isLive.equals("1") && streamerId.equals(id)) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        Utility.PrintLog("call", "match id  :  " + id);
                                    }
                                }
                            }
                        }


                        //new streamer ---------------------------------------------------
                        if (newStreamers != null && newStreamers.size() > 0) {

                            for (int i = 0; i < newStreamers.size(); i++) {
                                String isLive = newStreamers.get(i).getIs_live();
                                String id = newStreamers.get(i).getId();

                                if (isLive != null && isLive.equals("1") && streamerId.equals(id)) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        Utility.PrintLog("call", "match id  :  " + id);
                                        newStreamers.get(i).setCount(count);
                                    }
                                }
                            }
                        }


                        // for trending streamer ----------------------------------------------------
                        if (trendingStreamersList != null && trendingStreamersList.size() > 0) {

                            for (int i = 0; i < trendingStreamersList.size(); i++) {
                                String isLive = trendingStreamersList.get(i).getIs_live();
                                String id = trendingStreamersList.get(i).getId();

                                if (isLive != null && isLive.equals("1") && streamerId.equals(id)) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        Utility.PrintLog("call", "match id  :  " + id);
                                        trendingStreamersList.get(i).setCount(count);
                                    }
                                }
                            }
                        }

                        //refresh all dashboard adapter
                        refreshAllAdapter();
                    }
                }

            } catch (Exception e) {
                Utility.PrintLog("call", "exception : " + e.getMessage());
            }
        }
    });
    private final Emitter.Listener onAppNormalUserRefreshCount = args -> runOnUiThread(new Runnable() {
        @Override
        public void run() {

            try {

                String counts = "0";

                if (args[0] != null && args[0].toString().length() > 0) {

                    Utility.PrintLog("call", "socket.on onAppNormalUserRefreshCount = " + args[0].toString());

                    String jsonString = args[0].toString();
                    JSONObject jsonResult = new JSONObject(jsonString);
                    JSONArray data = jsonResult.getJSONArray("data");

                    if (data != null) {
                        for (int i = 0; i < data.length(); i++) {

                            String id = data.getJSONObject(i).getString("id");
                            String live_streaming_slug = data.getJSONObject(i).getString("live_streaming_slug");
                            String count = data.getJSONObject(i).getString("count");

                            liveWatcherCountDashboard.put(id, count);
                            liveStreamingLiveIdDashboard.add(id);
                            liveStreamingSlugDashboard.put(id, live_streaming_slug);
                        }


                        //for recommended streamers----------------------------------------
                        if (recommendedStreamers != null && recommendedStreamers.size() > 0) {

                            for (int i = 0; i < recommendedStreamers.size(); i++) {
                                String isLive = recommendedStreamers.get(i).getIs_live();
                                String id = recommendedStreamers.get(i).getId();

                                if (liveStreamingSlugDashboard.containsKey(id)) {
                                    String streamingSlug = liveStreamingSlugDashboard.get(id);
                                    String count = liveWatcherCountDashboard.get(id);

                                    Utility.PrintLog("call", "homeActivity is live : " + isLive);

                                    recommendedStreamers.get(i).setLive_streaming_slug(streamingSlug);
                                    recommendedStreamers.get(i).setCount(count);
                                }

                            }
                        }


                        //for swipe banner--------------------------------------------
                        if (bannerDetails != null && bannerDetails.size() > 0) {

                            for (int i = 0; i < bannerDetails.size(); i++) {
                                String id = bannerDetails.get(i).getId();

                                if (liveStreamingSlugDashboard.containsKey(id)) {
                                    String streamingSlug = liveStreamingSlugDashboard.get(id);
                                    String count = liveWatcherCountDashboard.get(id);

                                    bannerDetails.get(i).setLive_streaming_slug(streamingSlug);
                                    bannerDetails.get(i).setCount(count);
                                }
                            }
                        }

                        // for new streamer ----------------------------------------------------
                        if (newStreamers != null && newStreamers.size() > 0) {

                            for (int i = 0; i < newStreamers.size(); i++) {
                                String isLive = newStreamers.get(i).getIs_live();
                                String id = newStreamers.get(i).getId();

                                if (isLive.equals("1")) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        String streamingSlug = liveStreamingSlugDashboard.get(id);
                                        String count = liveWatcherCountDashboard.get(id);
                                        newStreamers.get(i).setLive_streaming_slug(streamingSlug);
                                        newStreamers.get(i).setCount(count);
                                    }
                                }
                            }
                        }


                        // for trending streamer ----------------------------------------------------
                        if (trendingStreamersList != null && trendingStreamersList.size() > 0) {
                            for (int i = 0; i < trendingStreamersList.size(); i++) {
                                String isLive = trendingStreamersList.get(i).getIs_live();
                                String id = trendingStreamersList.get(i).getId();

                                if (isLive.equals("1")) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        String streamingSlug = liveStreamingSlugDashboard.get(id);
                                        String count = liveWatcherCountDashboard.get(id);
                                        trendingStreamersList.get(i).setLive_streaming_slug(streamingSlug);
                                        trendingStreamersList.get(i).setCount(count);
                                    }
                                }
                            }
                        }


                        // for featured streamer ----------------------------------------------------
                        if (featureStreamers != null && featureStreamers.size() > 0) {
                            Utility.PrintLog("call", "newStreamers : " + featureStreamers);

                            for (int i = 0; i < featureStreamers.size(); i++) {
                                String isLive = featureStreamers.get(i).getIs_live();
                                String id = featureStreamers.get(i).getId();

                                if (isLive.equals("1")) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        String streamingSlug = liveStreamingSlugDashboard.get(id);
                                        String count = liveWatcherCountDashboard.get(id);

                                        featureStreamers.get(i).setLive_streaming_slug(streamingSlug);
                                        featureStreamers.get(i).setCount(count);
                                    }
                                }
                            }
                        }


                        // for featured streamer ----------------------------------------------------
                        if (mostPopularLives != null && mostPopularLives.size() > 0) {

                            for (int i = 0; i < mostPopularLives.size(); i++) {
                                String isLive = mostPopularLives.get(i).getIs_live();
                                String id = mostPopularLives.get(i).getId();

                                if (isLive.equals("1")) {
                                    if (liveStreamingSlugDashboard.containsKey(id)) {
                                        String streamingSlug = liveStreamingSlugDashboard.get(id);
                                        String count = liveWatcherCountDashboard.get(id);

                                        mostPopularLives.get(i).setLive_streaming_slug(streamingSlug);
                                        mostPopularLives.get(i).setCount(count);
                                    }
                                }
                            }
                        }


                        //refresh all dashboard adapter
                        homeFragmentClickEventListener.RefreshAllAdapter();

                        if (onClickFeturedFullScreen != null) {
                            onClickFeturedFullScreen.RefreshFeaturedFullScreenAdapter(featureStreamers);
                        }
                        if (onClickTrendingFullScreen != null) {
                            onClickTrendingFullScreen.RefreshTrendingFullScreenAdapter(trendingStreamersList);
                        }
                        if (onClickNewStreamerFullScreen != null) {
                            onClickNewStreamerFullScreen.RefreshNewStreamerFullScreenAdapter(newStreamers);
                        }
                    }
                }

            } catch (Exception e) {
                Utility.PrintLog("call", "exception : " + e.getMessage());
            }

        }
    });
    public Map<String, String> removedEventIdSlug = new HashMap<>();
    private final Emitter.Listener onStreamUserEventStop = args -> {
        runOnUiThread(() -> {

            Utility.PrintLog("call", "socket.on onStreamUserEventStop = " + args[0].toString());

            if (args[0] != null && args[0].toString().length() > 0) {

                try {

                    String jsonString = args[0].toString();
                    JSONObject jsonResult = null;

                    try {

                        jsonResult = new JSONObject(jsonString);

                        if (fragment instanceof DiscoverFragment) {
                            ((DiscoverFragment) fragment).removeLiveShort(jsonResult);
                        } else {

                            if (isTopCategoriesActivity != null) {
                                isTopCategoriesActivity.removeLiveShort(jsonResult);
                            }

                            if (streamerDetailsActivity != null) {
                                streamerDetailsActivity.removeLiveShort(jsonResult);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject jsonObject = new JSONObject(args[0].toString());

                    if (jsonObject != null && jsonObject.has("event_id")) {
                        String event_id = jsonObject.getString("event_id");

                        if (eventId != null && !eventId.equals("") && liveActivity != null) {

                            if (event_id.equals(eventId)) {
                                isLiveActivity = true;
                                liveActivity.finishActivity();
                            }
                        }

                        if (liveStreamingSlugDashboard != null && liveStreamingSlugDashboard.containsKey(event_id)) {
                            removedEventIdSlug.put(event_id, liveStreamingSlugDashboard.get(event_id));
                            liveStreamingSlugDashboard.remove(event_id);


                            for (int i = 0; i < mostPopularLives.size(); i++) {
                                if (mostPopularLives.get(i).getId().equals(event_id)) {
                                    mostPopularLives.get(i).setIs_live("0");
                                    mostPopularLives.get(i).setLive_streaming_slug(null);
                                }
                            }

                            for (int i = 0; i < topCategoryLiveList.size(); i++) {
                                if (topCategoryLiveList.get(i).getId().equals(event_id)) {
                                    topCategoryLiveList.get(i).setIs_live("0");
                                    topCategoryLiveList.get(i).setLive_streaming_slug(null);
                                }
                            }

                            for (int i = 0; i < streamerDetailLiveList.size(); i++) {
                                if (streamerDetailLiveList.get(i).getId().equals(event_id)) {
                                    streamerDetailLiveList.get(i).setIsLive("0");
                                    streamerDetailLiveList.get(i).setLive_streaming_slug(null);
                                }
                            }

                            if (mostPopularListener != null) {
                                mostPopularListener.MostPopularStreamer(mostPopularLives);
                            }

                            if (topCategoriesListener != null) {
                                topCategoriesListener.MostPopularStreamer(topCategoryLiveList);
                            }

                            if (streamerDetailLive != null) {
                                streamerDetailLive.StreamerDetailScreenLive(streamerDetailLiveList);
                            }

                        }
                    }

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "Exception : " + e.toString());
                }

            }
        });
    };
    private final Emitter.Listener onStreamUserEventStart = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(() -> {

                if (args[0] != null && args[0].toString().length() > 0) {
                    Utility.PrintLog("call", "socket.on onStreamUserEventStart = " + args[0].toString());

                    try {

                        JSONObject jsonObject = new JSONObject(args[0].toString());

                        if (jsonObject != null && jsonObject.has("event_id")) {
                            String event_id = jsonObject.getString("event_id");

                            if (removedEventIdSlug != null && removedEventIdSlug.containsKey(event_id)) {
                                liveStreamingSlugDashboard.put(event_id, removedEventIdSlug.get(event_id));

                                if (eventId != null && !eventId.equals("") && liveActivity != null) {
                                    if (event_id.equals(eventId)) {
                                        liveActivity.onBackPressed();
                                    }
                                }

                                for (int i = 0; i < mostPopularLives.size(); i++) {
                                    if (mostPopularLives.get(i).getId().equals(event_id)) {
                                        mostPopularLives.get(i).setIs_live("1");
                                        mostPopularLives.get(i).setLive_streaming_slug(removedEventIdSlug.get(event_id));
                                    }
                                }

                                for (int i = 0; i < topCategoryLiveList.size(); i++) {
                                    if (topCategoryLiveList.get(i).getId().equals(event_id)) {
                                        topCategoryLiveList.get(i).setIs_live("1");
                                        topCategoryLiveList.get(i).setLive_streaming_slug(removedEventIdSlug.get(event_id));
                                    }
                                }

                                for (int i = 0; i < streamerDetailLiveList.size(); i++) {
                                    if (streamerDetailLiveList.get(i).getId().equals(event_id)) {
                                        streamerDetailLiveList.get(i).setIsLive("1");
                                        streamerDetailLiveList.get(i).setLive_streaming_slug(removedEventIdSlug.get(event_id));
                                    }
                                }

                                if (mostPopularListener != null) {
                                    mostPopularListener.MostPopularStreamer(mostPopularLives);
                                }

                                if (topCategoriesListener != null) {
                                    topCategoriesListener.MostPopularStreamer(topCategoryLiveList);
                                }

                                if (streamerDetailLive != null) {
                                    streamerDetailLive.StreamerDetailScreenLive(streamerDetailLiveList);
                                }

                            }
                        }

                    } catch (Exception e) {

                    }
                }
            });
        }
    };
    public Handler handler = new Handler();
    public Runnable runnable = null;
    private final Emitter.Listener onConnect = args -> runOnUiThread(() ->
    {

        Utility.PrintLog("call", "socket.on onConnect");

        if (Common.socket != null && Common.socket.connected()) {
            //connectUser();
            if (Constants.isFirstTimeIntervalConnect) {
                Constants.isFirstTimeIntervalConnect = false;
                connectUserInterval();
            }


        } else {
            Utility.PrintLog("call", "socket.on onConnect socket null or not connected");
        }
    });
    public ActivityHomeBinding binding;
    public Emitter.Listener onStopLiveStreaming = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args[0] != null && args[0].toString().length() > 0) {
                        Utility.PrintLog("call", "socket.on onStopLiveStreaming = " + args[0].toString());

                        if (isLiveActivity) {
                            isLiveActivity = false;
                            Utility.PrintLog("isLiveActivity", "HomeActivity onStopLive" + isLiveActivity);
                            if (liveActivity != null) {
                                //liveActivity.onBackPressed();
                                if (isApplicationSentToBackground(HomeActivity.this)) {
                                    liveActivity.whenAppPause();

                                    return;
                                } else {

                                    liveActivity.finishActivity();
                                }
                            }
                        }
                    }
                }
            });
        }
    };
    private String countSubscribe = "0";
    private final Emitter.Listener onCountSubscriber = args -> runOnUiThread(() ->
    {

        if (args[0] != null && args[0].toString().length() > 0) {

            Utility.PrintLog("call", "socket.on onCountSubscriber = " + args[0].toString());

            try {

                JSONObject jsonObject = new JSONObject(args[0].toString());

                if (jsonObject.has("event_id") && jsonObject.has("count")) {
                    countSubscribe = jsonObject.getString("count");
                    String event_id = jsonObject.getString("event_id");

                    if (eventId != null && !eventId.equals("") && event_id != null && !event_id.equals("") && event_id.equals(eventId)) {
                        liveCountingListener.refreshCount(countSubscribe);
                    }
                }


                if (jsonObject.has("heart_counter") && jsonObject.has("event_id")) {
                    String totalCount = jsonObject.getString("heart_counter");
                    String event_id = jsonObject.getString("event_id");

                    if (liveCountingListener != null) {
                        liveCountingListener.totalCount(totalCount, event_id);
                    }
                }


            } catch (Exception e) {
                Utility.PrintLog(TAG, "Exception onCountSubscriber() " + e.toString());

            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initt();
        Utility.PrintLog("onCreate", "HomeActivity onCreate()");
    }

    private void initt() {

        openHome = this;
        homeActivity = this;
        imageRefreshListener = this;
        connectivityListener = this;

        isFromNotification();
        isLiveActivity = false;
        Constants.isHomeActivityIsAlredayOpen = false;
        isHomeActivityForNotification = false;
        Utility.PrintLog("isLiveActivity", "HomeActivity initt()" + isLiveActivity);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        clickEventListener = this;
        Random r = new Random();
        int low = 10000;
        int high = 100000;
        int randomUserId = r.nextInt(high - low) + low;
        userPreferences.setRandomUserId(String.valueOf(randomUserId));
        connectSocket();
        binding.ivFooterVideoClip.setSelected(true);
        showVideoClipFragment();
        init();
        setLanguageLable();
        callLanguagUpdateApi(); // update language api


        //if is from  deep link
        if (Constants.isForStreamerDetail) {
            Constants.isForStreamerDetail = false;
            if (getIntent() != null && getIntent().getStringExtra(Constants.INTENT_KEY_USERNAME) != null) {
                String userName = getIntent().getStringExtra(Constants.INTENT_KEY_USERNAME);
                startActivity(new Intent(this, StreamerDetailsActivity.class).putExtra(Constants.INTENT_KEY_USERNAME, userName));
            }
        }
    }

    private String decodeMessage(String message) {
        message = message.replaceAll(":and:", "&");
        message = message.replaceAll(":plus:", "+");
        return StringEscapeUtils.unescapeJava(message);
    }

    private void refreshAllAdapter() {

        homeFragmentClickEventListener.RefreshAllAdapter();

        if (onClickFeturedFullScreen != null) {
            onClickFeturedFullScreen.RefreshFeaturedFullScreenAdapter(featureStreamers);
        }
        if (onClickTrendingFullScreen != null) {
            onClickTrendingFullScreen.RefreshTrendingFullScreenAdapter(trendingStreamersList);
        }

        if (onClickNewStreamerFullScreen != null) {
            onClickNewStreamerFullScreen.RefreshNewStreamerFullScreenAdapter(newStreamers);
        }

   /*     if (mostPopularListener != null) {
            mostPopularListener.MostPopularStreamer(mostPopularLives);
        }

        if (topCategoriesListener != null) {
            topCategoriesListener.MostPopularStreamer(topCategoryLiveList);
        }

        if (streamerDetailLive != null) {
            streamerDetailLive.StreamerDetailScreenLive(streamerDetailLiveList);
        }*/

    }


    public void callLiveStreamingAPI(String liveStreamingSlug, Context mContext) {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<CommonResponse> call = apiService.doLiveStreamingSlugData(userPreferences.getApiKey(), liveStreamingSlug, userPreferences.getUserId());
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
                                                .putExtra(Constants.INTENT_KEY_LIVE_WATCHER_COUNTS, countSubscribe));

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


    public void isFromNotification() {

        if (getIntent() != null && getIntent().getStringExtra(INTENT_KEY_DIRECTION_CODE) != null) {
            String point = getIntent().getStringExtra(INTENT_KEY_DIRECTION_CODE);
            String orderId = getIntent().getStringExtra(Constants.ORDER_ID);

            if (point.equals("3")) {
                /*if (liveActivity!=null)
                {
                    liveActivity.finishWhenLiveRunningAndClickOnLiveNotification();
                }*/

                if (builder != null) {
                    builder.dismiss();
                }
                if (cartPageActivity != null) {
                    cartPageActivity.finish();
                }
                Utility.PrintLog(TAG, "isFromNotification Handler()");
                new Handler().postDelayed(() -> imageRefreshListener.goToProductDetailsScreen(orderId, false, true), 500);

            } else if (point.equals("4")) {
                startActivity(new Intent(this, CartPageActivity.class));
            }

        } else {
            if (Constants.order_id != null && !Constants.order_id.equals("")) {

                if (builder != null) {
                    builder.dismiss();
                }
                if (cartPageActivity != null) {
                    cartPageActivity.finish();
                }
                Utility.PrintLog(TAG, "isFromNotification Handler()");
                new Handler().postDelayed(() -> imageRefreshListener.goToProductDetailsScreen(Constants.order_id, false, true), 500);
            }

        }
    }


    private void setLanguageLable() {
        try {

            Utility.PrintLog("LanguageLable", " key " + language.getLanguage(KEY.home_app));

            binding.txtFooterHome.setText(language.getLanguage(KEY.home_app));
            binding.txtFooterProfile.setText(language.getLanguage(KEY.profile));
            binding.txtFooterOrder.setText(language.getLanguage(KEY.orders));
            binding.txtFooterDiscover.setText(language.getLanguage(KEY.lives));
            binding.txtFooterNotification.setText(language.getLanguage(KEY.notifications));
            binding.txtFooterVideoClip.setText(language.getLanguage(KEY.clips));

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }

    private void init() {

        binding.llFooterHome.setOnClickListener(this);
        binding.llFooterOrder.setOnClickListener(this);
        binding.llFooterDiscover.setOnClickListener(this);
        binding.llFooterNotification.setOnClickListener(this);
        binding.llFooterVideoClip.setOnClickListener(this);
        binding.llFooterProfile.setOnClickListener(this);

        if (userPreferences.getProfileImage() != null && !userPreferences.getProfileImage().trim().equalsIgnoreCase("")) {
            BaseActivity.showGlideImage(this, userPreferences.getProfileImage(), binding.ivFooterProfile);
        }
    }

    //start socket code............................
    public void connectSocket() {
        Utility.PrintLog("call", "connectSocket()");

        try {


            if (Common.socket != null && Common.socket.connected()) {
                Utility.PrintLog("call", "connectSocket() socket is already connected");
            } else {

                Utility.PrintLog("call", "connectSocket() try to connect");

                Common.socket = null;
                SocketSingleObject.instance = null;
                Common.socket = SocketSingleObject.get(HomeActivity.this).getSocket();
                Common.socket.on(Socket.EVENT_CONNECT, onConnect);
                Common.socket.on(Common.CONNECT_ALL_USER_INTERVAL, onIntervalCall);
                // Common.socket.on(Common.CONNECT_APP_NORMAL_USER_REFRESH_COUNT, onAppNormalUserRefreshCount);
                Common.socket.on(Common.CURRENT_LIVE_EVENT, onCurrentLiveEvent);
                Common.socket.on(Common.CONNECT_APP_STREAMER_USER_EVENT, onStreamerUserEvent);
                Common.socket.on(Common.CONNECT_START_LIVE_STREAMING, onStartLiveStreaming);
                //Common.socket.on(Common.CONNECT_NORMAL_STOP_LIVE_STREAMING, onNormalStopLiveStreaming);
                //Common.socket.on(Common.CONNECT_NORMAL_USER_COUNT, onNormalUserCount);
                Common.socket.on(Common.COUNT_EVENT_SUBSCRIBE, onCountEventSubscribe);
                Common.socket.on(Common.CONNECT_STREAM_USER_EVENT_START, onStreamUserEventStart);
                Common.socket.on(Common.CONNECT_STREAM_USER_EVENT_STOP, onStreamUserEventStop);
                Common.socket.on(Common.CONNECT_CONNECT_SUBSCRIBER, onConnectSubscriber);
                Common.socket.on(Common.CONNECT_COUNT_SUBSCRIBER, onCountSubscriber);
                Common.socket.on(Common.CONNECT_STOP_LIVE_STREAMING, onStopLiveStreaming);
                Common.socket.on(Common.CONNECT_COMMENT, onComment);
                Common.socket.on(Common.CONNECT_HEART_COUNT, onHeartClick);
                Common.socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                Common.socket.connect();
            }

        } catch (Exception e) {
            Utility.PrintLog("call", "connectSocket() exception = " + e.getMessage());
        }
    }

    public void disconnectSocket() {

        Utility.PrintLog("call", "disconnectSocket()");

        try {

            if (Common.socket != null && Common.socket.connected()) {
                Common.socket.disconnect();
                Common.socket.off(Socket.EVENT_CONNECT, onConnect);
                Common.socket.off(Common.CONNECT_ALL_USER_INTERVAL, onIntervalCall);
                //Common.socket.off(Common.CONNECT_APP_NORMAL_USER_REFRESH_COUNT, onAppNormalUserRefreshCount);
                Common.socket.off(Common.CURRENT_LIVE_EVENT, onCurrentLiveEvent);
                Common.socket.off(Common.CONNECT_APP_STREAMER_USER_EVENT, onStreamerUserEvent);
                Common.socket.off(Common.CONNECT_START_LIVE_STREAMING, onStartLiveStreaming);
                // Common.socket.off(Common.CONNECT_NORMAL_STOP_LIVE_STREAMING, onNormalStopLiveStreaming);
                // Common.socket.off(Common.CONNECT_NORMAL_USER_COUNT, onNormalUserCount);
                Common.socket.off(Common.COUNT_EVENT_SUBSCRIBE, onCountEventSubscribe);
                Common.socket.off(Common.CONNECT_STREAM_USER_EVENT_START, onStreamUserEventStart);
                Common.socket.off(Common.CONNECT_STREAM_USER_EVENT_STOP, onStreamUserEventStop);
                Common.socket.off(Common.CONNECT_CONNECT_SUBSCRIBER, onConnectSubscriber);
                Common.socket.off(Common.CONNECT_COUNT_SUBSCRIBER, onCountSubscriber);
                Common.socket.off(Common.CONNECT_STOP_LIVE_STREAMING, onStopLiveStreaming);
                Common.socket.off(Common.CONNECT_COMMENT, onComment);
                Common.socket.off(Common.CONNECT_HEART_COUNT, onHeartClick);
                Common.socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                Common.socket = null;
                SocketSingleObject.instance = null;

            } else {
                Utility.PrintLog("call", "disconnectSocket() socket already disconnected");
            }

        } catch (Exception e) {
            Utility.PrintLog("call", "disconnectSocket() exception = " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Utility.PrintLog("call", "onBackPress() HomeActivity");

        disconnectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disconnectSocket();
    }

    public void connectUser() {

        try {

            if (Common.socket != null && Common.socket.connected()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Common.USER_ID, userPreferences.getRandomUserId());
                Common.socket.emit(Common.CONNECT_ALL_USER, jsonObject);

                Utility.PrintLog("call", "connectUser() jsonObject = " + jsonObject.toString());
                Utility.PrintLog("call", "random user id  = " + userPreferences.getRandomUserId());

            } else {
                Utility.PrintLog("call", "connectUser() socket null or not connected");

            }

        } catch (Exception e) {
            Utility.PrintLog("call", "connectUser() exception = " + e.getMessage());
        }
    }

    public void connectUserInterval() {

        runnable = new Runnable() {
            @Override
            public void run() {

                try {

                    if (Common.socket != null && Common.socket.connected()) {
                        String randomUserId = userPreferences.getRandomUserId();

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(Common.USER_ID, randomUserId);
                        jsonObject.put("type", "Android");
                        jsonObject.put("android_user_id", userPreferences.getUserId());
                        Common.socket.emit(Common.CONNECT_ALL_USER_INTERVAL, jsonObject);

                    } else {

                        Utility.PrintLog("call", "onIntervalCall connectUser() socket null or not connected");
                        connectSocket();
                    }

                } catch (Exception e) {
                    Utility.PrintLog("call", "onIntervalCall connectUser() exception = " + e.getMessage());
                }

                handler.postDelayed(runnable, 3000);
            }
        };

        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();


        Utility.PrintLog(TAG, "onResume()" + isRunning(this));

        if (isForDiscover) { // its true when user press tryAgain button from black screen its direct to LIVEs screen
            isForDiscover = false;
            binding.llFooterDiscover.performClick();
        }

        if (Constants.idFromProfile) { // its true when user change language form user profile screen switch
            Constants.idFromProfile = false;

            recreate();

            onSelectedView(binding.ivFooterProfile, binding.txtFooterProfile);
            showProfileFragment();

        }

        if (Constants.isHomeActivityIsAlredayOpen) {
            Constants.isHomeActivityIsAlredayOpen = false;
            Constants.isForRefreshHomeApi = true;
            showHomeFragment();
        }

        if (isHomeActivityForNotification) {
            isHomeActivityForNotification = false;
            isFromNotification();
        }

      /*  if (Constants.isFromConnectionLost)
        {
            Constants.isFromConnectionLost = false;
            showHomeFragment();
        }*/

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llFooterHome:
                onSelectedView(binding.ivFooterHome, binding.txtFooterHome);
                showHomeFragment();
                break;

            case R.id.llFooterOrder:
                onSelectedView(binding.ivFooterOrder, binding.txtFooterOrder);
                showOrderHistoryFragment();
                break;

            case R.id.llFooterDiscover:
                onSelectedView(binding.ivFooterDiscover, binding.txtFooterDiscover);
                showDiscoverFragment();
                break;

            case R.id.llFooterVideoClip:
                if (liveActivity != null && BaseActivity.isInPictureInPictureMode) {
                    liveActivity.finishWhenLiveRunningAndClickOnLiveNotification();
                }
                onSelectedView(binding.ivFooterVideoClip, binding.txtFooterVideoClip);
                if (liveActivity != null) {
                    liveActivity.eventIdForConfetti = "";
                }
                showVideoClipFragment();
                break;

            case R.id.llFooterNotification:
                onSelectedView(binding.ivFooterNotification, binding.txtFooterNotification);
                showNotificationFragment();
                break;

            case R.id.llFooterProfile:
                onSelectedView(binding.ivFooterProfile, binding.txtFooterProfile);
                showProfileFragment();
                break;
        }
    }


    public void onSelectedView(ImageView view, TextView txtview) {

        binding.ivFooterHome.setSelected(false);
        binding.ivFooterOrder.setSelected(false);
        binding.ivFooterDiscover.setSelected(false);
        binding.ivFooterVideoClip.setSelected(false);
        binding.ivFooterNotification.setSelected(false);
        binding.ivFooterProfile.setSelected(false);

        binding.txtFooterHome.setTextColor(getResources().getColor(R.color.unselected_color));
        binding.txtFooterOrder.setTextColor(getResources().getColor(R.color.unselected_color));
        binding.txtFooterDiscover.setTextColor(getResources().getColor(R.color.unselected_color));
        binding.txtFooterVideoClip.setTextColor(getResources().getColor(R.color.unselected_color));
        binding.txtFooterNotification.setTextColor(getResources().getColor(R.color.unselected_color));
        binding.txtFooterProfile.setTextColor(getResources().getColor(R.color.unselected_color));

        view.setSelected(true);
        txtview.setTextColor(getResources().getColor(R.color.bgSplash));
    }

    @Override
    public void onClickPosition(int position) {

        if (userPreferences.getSocialId() != null && !userPreferences.getSocialId().equals("") && !userPreferences.getSocialId().equals("null")) {

            if (position == 0) {
                goToNextScreen(HomeActivity.this, MyProfile.class, false);

            } else if (position == 1) {
                goToNextScreen(HomeActivity.this, AddressList.class, false);

            } else if (position == 2) {
                goToNextScreen(HomeActivity.this, PaymentCardList.class, false);

            } else if (position >= 3 && position <= 8) {
                if (position == 4)
                    goToNextScreen(this, ContactUs.class, false);
                else
                    goToActivity(position);

            } else if (position == 9) {
                //showInternetDialog();
                doLogOut();
            }

        } else {

            if (position == 0) {
                goToNextScreen(HomeActivity.this, MyProfile.class, false);

            } else if (position == 1) {
                Intent intent = new Intent(this, ForgotPasswordChangePasswordActivity.class);
                intent.putExtra(SCREEN, 2);
                startActivity(intent);

            } else if (position == 2) {
                goToNextScreen(HomeActivity.this, AddressList.class, false);

            } else if (position == 3) {
                goToNextScreen(HomeActivity.this, PaymentCardList.class, false);

            } else if (position >= 4 && position <= 9) {
                if (position == 5)
                    goToNextScreen(this, ContactUs.class, false);
                else
                    goToActivity(position);

            } else if (position == 10) {
                // showInternetDialog();
                doLogOut();
            }
        }
    }


    private void doLogOut() {
        showLoader(true);

        if (userPreferences != null && isLogged()) {

            try {
                Utility.PrintLog(TAG, "Api kye :" + userPreferences.getApiKey() + "\nuserID : " + userPreferences.getUserId());

                if (checkInternetConnectionWithMessage()) {
                    Call<BaseResponse> call = apiService.doLogout(userPreferences.getApiKey(), userPreferences.getUserId());

                    call.enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response) {
                            Utility.PrintLog(TAG, response.toString());
                            checkErrorCode(response.code());

                            if (response.body() != null) {

                                if (checkResponseStatusWithMessage(response.body(), true)) {
                                    //close PIP mode if ON
                                    if (liveActivity != null && isInPictureInPictureMode) {
                                        liveActivity.finishActivity();
                                    }


                                    userPreferences.removeCurrentUser();
                                    goToNextScreen(HomeActivity.this, SecondSplashScreen.class, true);

                                } else
                                    showSnackErrorMessage(language.getLanguage(response.body().getMessage()));

                                hideLoader();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                            Utility.PrintLog(TAG, t.toString());
                            hideLoader();
                        }
                    });
                } else hideLoader();

            } catch (Exception e) {
                Utility.PrintLog(TAG, e.toString());
                showSnackResponse(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                hideLoader();
            }

        } else hideLoader();

    }

    private void goToActivity(int position) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WEBVIEW_SCREEN, position);
        startActivity(intent);
    }

    public void currencyRateAPI(String currencyCode) {

        try {

            if (checkInternetConnectionWithMessage()) {

                Call<String> call = apiService.doCurrencyRate(API_KEY, currencyCode);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            return;
                        }

                        if (response.body() != null && response.isSuccessful()) {
                            try {

                                JSONObject jsonObject = new JSONObject(response.body());

                                if (jsonObject != null && jsonObject.has("rates")) {
                                    Object languageObject = jsonObject.get("rates");
                                    userPreferences.setCurrencyRate(languageObject.toString());
                                    Utility.PrintLog(TAG, "rate : " + userPreferences.getCurrencyRate("ARS"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
        }
    }

    @Override
    public void getImageUrl(String url) {
        if (url != null) {
            BaseActivity.showGlideImage(this, url, binding.ivFooterProfile);
        }
    }

    @Override
    public void discoverClick() {

    }

    @Override
    public void goToProductDetailsScreen(String product_id, boolean destination, boolean isFromOrderDetauls) {


        if (isFromOrderDetauls) {
            startActivity(new Intent(this, OrderDetailsActivity.class).putExtra(Constants.ORDER_ID, product_id));
            return;
        }


        if (productDetailsActivity != null) {
            productDetailsActivity.finish();
        }

        if (cartPageActivity != null) {
            cartPageActivity.finish();
        }

        if (destination) {
            startActivity(new Intent(this, CartPageActivity.class));
        } else {
            startActivity(new Intent(this, ProductDetailsActivity.class).putExtra(PARAM_PRODUCT_ID, product_id));
        }
    }

    @Override
    public void showDialog() {

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

    @Override
    public void onNetworkConnected() {
        super.onNetworkConnected();

        //recreate();
        if (Common.socket!=null && !Common.socket.connected()) {
            connectSocket();
            if (VideoClipsFragment.currentFragment!=null){
                if (VideoClipsFragment.currentFragment.mVideoClipAdapter!=null){
                    VideoClipsFragment.currentFragment.mVideoClipAdapter.notifyDataSetChanged();
                }
            }
        }

        if (DiscoverFragment.discoverFragment!=null){ // refresh LIVEs screen listing
            DiscoverFragment.discoverFragment.callMostPopularLives();
        }

        if (Constants.isFromConnectionLost) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utility.PrintLog(TAG, "user disconnected");
                    Constants.isFromConnectionLost = false;
                    userDisconnect();
                }
            }, 5000);
        }
    }


    @Override
    public void home() {

        recreate();
        //showHomeFragment();
    }
}