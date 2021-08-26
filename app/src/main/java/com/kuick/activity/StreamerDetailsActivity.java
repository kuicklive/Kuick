package com.kuick.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.AllStreamerData;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.Response.StreamerProfileResponse;
import com.kuick.adapter.StreamersDetailsAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.common.Common;
import com.kuick.databinding.ActivityStreamerDetailsBinding;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.util.comman.Utility.PrintLog;

public class StreamerDetailsActivity extends BaseActivity implements ClickEventListener {

    public static ClickEventListener streamerDetailLive;
    private ActivityStreamerDetailsBinding binding;
    private TextView txtTitle;
    private ImageView imgCart, btnBack;
    private String whatsapp;
    private String facebook;
    private String messanger;
    private String instagram;
    private String linkedin;
    private String twitter;
    private String youtube;
    private String tiktok;
    private String twich;
    private String pinterest;
    private String username;
    private final String TAG = "StreamerDetailsActivity";
        public static List<AllStreamerData> streamerDetailLiveList;
    public static StreamersDetailsAdapter streamerDetailsScreenLiveAdapter;
    private TextView btnTryAgain;
    public static StreamerDetailsActivity streamerDetailsActivity = null;
    private long tStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStreamerDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        streamerDetailLive = this;
        streamerDetailsActivity = this;
        setToolBar();
        setLanguageLable();
        tStart = System.currentTimeMillis();

        callStreamerProfileDetailAPI();
        onClickListener();
    }

    private void setLanguageLable() {

        try {
            if (language != null) {
                txtTitle.setText(language.getLanguage(KEY.streamer_detail));
                binding.upcomingLive.setText(language.getLanguage(KEY.upcoming_live_streams));

            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }

    private void onClickListener() {
        binding.btnWp.setOnClickListener(this);
        binding.btnFB.setOnClickListener(this);
        binding.btnMessanger.setOnClickListener(this);
        binding.btnInsta.setOnClickListener(this);
        binding.btnLinkedin.setOnClickListener(this);
        binding.btnTwit.setOnClickListener(this);
        binding.btnyoutube.setOnClickListener(this);
        binding.btnTiktok.setOnClickListener(this);
        binding.btnTwich.setOnClickListener(this);
        binding.btnPen.setOnClickListener(this);
    }

    private void callStreamerProfileDetailAPI() {

        username = getIntent().getStringExtra(Constants.INTENT_KEY_USERNAME);
        Utility.PrintLog(TAG,"username : " + username);

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<CommonResponse> call = apiService.doStreamerProfileData(userPreferences.getApiKey(), username);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        PrintLog(TAG, "response : " + response);

                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful()) {
                                hideShowView(binding.dataView, null);
                                setResponseData(response.body());
                            } else {
                                hideShowView(binding.dataNotFound, language.getLanguage(response.body().getMessage()));
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                            }

                        }
                        hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        //showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        hideLoader();
                    }
                });
            } else {
                hideShowView(binding.dataNotFound, language.getLanguage(KEY.internet_connection_lost_please_retry_again));
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            //showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }
    }

    private void setResponseData(CommonResponse response) {

        StreamerProfileResponse streamerProfile = response.getStreamerProfileResponse();

        BaseActivity.showGlideImage(this, streamerProfile.getMain_banner(), binding.bannerImage);
        BaseActivity.showGlideImage(this, streamerProfile.getProfile_pic(), binding.userImg);

        binding.countryName.setText(streamerProfile.getCountry());
        binding.userName.setText(streamerProfile.getUsername());
        binding.website.setText(streamerProfile.getWebsite());
        binding.description.setText(streamerProfile.getDescription());

        getAllSocialUrls(streamerProfile);

        //live streamer slug
        this.streamerDetailLiveList = response.getAllStreamerData();
        if (response.getAllStreamerData() !=null && response.getAllStreamerData().size()> 0){

            List<AllStreamerData> liveList = sortList(streamerDetailLiveList,  userPreferences.getTimezone());
            liveList.get(liveList.size() - 1).setLoadMilliSecond(System.currentTimeMillis());

            streamerDetailLiveList.clear();
            streamerDetailLiveList.addAll(liveList);

            Utility.PrintLog(TAG,"streamerProfile.getCurrent_time() - " + response.getCurrent_time());
            Utility.PrintLog(TAG,"userPreferences.getCurrentTime() - " + userPreferences.getCurrentTime());

            binding.mUpcommingLive.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;
            Utility.PrintLog(TAG,"tDelta - " + tDelta);
            Utility.PrintLog(TAG,"elapsedSeconds - " + elapsedSeconds);
            Utility.PrintLog(TAG,"response.getCurrent_time() - " + response.getCurrent_time());
            long time = Long.parseLong(response.getCurrent_time()) - tDelta;
            Utility.PrintLog(TAG,"time - " + time);

            streamerDetailsScreenLiveAdapter = new StreamersDetailsAdapter(this.streamerDetailLiveList,response.getCurrent_time(), userPreferences.getTimezone(),this);
            binding.mUpcommingLive.setAdapter(streamerDetailsScreenLiveAdapter);

            connectUser();

        }

        //connect all user socket emit


    }
    public List<AllStreamerData> sortList( List<AllStreamerData> streamerDetailLiveList, String time_zone) {


        List<AllStreamerData> liveList = new ArrayList<>();

        Collections.sort(streamerDetailLiveList, (a, b) -> {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            formatter.setLenient(false);
            formatter.setTimeZone(TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));
            try {
                Date aDate = formatter.parse(a.getClock_end_time());
                Date bDate = formatter.parse(b.getClock_end_time());

                return bDate.compareTo(aDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        try {
            List<AllStreamerData> futureList = new ArrayList<>();
            List<AllStreamerData> pastList = new ArrayList<>();

            for (int i = 0; i < streamerDetailLiveList.size(); i++) {

                AllStreamerData data = streamerDetailLiveList.get(i);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formatter.setLenient(false);
                formatter.setTimeZone(TimeZone.getTimeZone(time_zone));
                Date bDate = formatter.parse(data.getClock_end_time());

                if (bDate.after(Calendar.getInstance(TimeZone.getTimeZone(time_zone)).getTime())) {

                    if (data.getStatus().equalsIgnoreCase("started")){
                        String yesterday = userPreferences.getCurrentDateAndTime();
                        Utility.PrintLog("yesterday","yesterday date : " + yesterday);
                        data.setClock_end_time(yesterday);
                        pastList.add(data);
                    }else {
                        futureList.add(data);
                    }
                } else {
                    pastList.add(data);
                }
            }

            Collections.reverse(futureList);
            liveList.addAll(futureList);
            liveList.addAll(pastList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return liveList;
//        mostPopularLives.addAll(0,liveEventData);

//        Collections.sort(mostPopularLives, (s1, s2) -> s1.getCountry().compareToIgnoreCase("United States of America"));
    }



    public void connectUser() {
        try {
            if (Common.socket != null && Common.socket.connected()) {

                Random r = new Random();
                int low = 10000;
                int high = 100000;
                int randomUserId = r.nextInt(high - low) + low;
                userPreferences.setRandomUserId(String.valueOf(randomUserId));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Common.USER_ID, userPreferences.getRandomUserId());
                Common.socket.emit(Common.CONNECT_ALL_USER, jsonObject);
                Log.e("call", "connectUser() jsonObject = " + jsonObject.toString());
                Log.e("call", "random user id  = " + userPreferences.getRandomUserId());

            } else {
                Log.e("call", "connectUser() socket null or not connected");
            }
        } catch (Exception e) {
            Log.e("call", "connectUser() exception = " + e.getMessage());
        }
    }

    private void getAllSocialUrls(StreamerProfileResponse streamerProfile) {

        whatsapp = streamerProfile.getWhatsapp_url();
        facebook = streamerProfile.getFacebook_url();
        messanger = streamerProfile.getFacebook_messenger_url();
        instagram = streamerProfile.getInstagram_url();
        linkedin = streamerProfile.getLinkedin_url();
        twitter = streamerProfile.getTwitter_url();
        youtube = streamerProfile.getYoutube_url();
        tiktok = streamerProfile.getTiktok_url();
        twich = streamerProfile.getTwitch_url();
        pinterest = streamerProfile.getPinterest_url();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
            case R.id.btnWp:
                openDefaultBrowser(whatsapp);
                break;
            case R.id.btnFB:
                openDefaultBrowser(facebook);
                break;
            case R.id.btnMessanger:
                openDefaultBrowser(messanger);
                break;
            case R.id.btnInsta:
                openDefaultBrowser(instagram);
                break;
            case R.id.btnLinkedin:
                openDefaultBrowser(linkedin);
                break;
            case R.id.btnTwit:
                openDefaultBrowser(twitter);
                break;
            case R.id.btnyoutube:
                openDefaultBrowser(youtube);
                break;
            case R.id.btnTiktok:
                openDefaultBrowser(tiktok);
                break;
            case R.id.btnTwich:
                openDefaultBrowser(twich);
                break;
            case R.id.btnPen:
                openDefaultBrowser(pinterest);
                break;
            case R.id.website:
                openDefaultBrowserForWebsire(binding.website.getText().toString());
                break;
            case R.id.btnTryAgain:
                callStreamerProfileDetailAPI();
                break;
        }

    }

    private void setToolBar() {
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        imgCart = binding.getRoot().findViewById(R.id.img_cart);
        binding.getRoot().findViewById(R.id.website).setOnClickListener(this);
        btnTryAgain = binding.getRoot().findViewById(R.id.btnTryAgain);
        txtTitle.setText(language.getLanguage(KEY.streamer_detail));
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        imgCart.setOnClickListener(this);
        btnTryAgain.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

    }

    public void hideShowView(View view, String message) {
        binding.dataNotFound.setVisibility(View.GONE);
        binding.dataView.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        error.setText(message);
    }

    public void openDefaultBrowser(String url) {
        Utility.PrintLog("urlurl","url " + url);

        try {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
        }
    }

    public void openDefaultBrowserForWebsire(String url) {
        Utility.PrintLog("urlurl","url " + url);

        try {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + url));
            startActivity(browserIntent);
        } catch (Exception e) {
            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
        }
    }

    @Override
    public void StreamerDetailScreenLive(List<AllStreamerData> allStreamerData) {

        if (streamerDetailsScreenLiveAdapter!=null) {
            streamerDetailsScreenLiveAdapter.RefreshAdapter(allStreamerData);
        }
    }
    public static void addLiveShort(JSONObject jsonObject) {
        try {
            JSONArray data = jsonObject.getJSONArray("data");

            List<AllStreamerData> listLiveData = new ArrayList<>();
            Log.e("streamerDetailLiveList","streamerDetailLiveList => "+streamerDetailLiveList);
            listLiveData.addAll(streamerDetailLiveList);

            if (data != null) {

                for (int i = 0; i < data.length(); i++) {
                    String id = "", live_streaming_slug = "", count = "";
                    if (data.getJSONObject(i).has("event_id"))
                        id = data.getJSONObject(i).getString("event_id");
                    if (data.getJSONObject(i).has("live_streaming_slug"))
                        live_streaming_slug = data.getJSONObject(i).getString("live_streaming_slug");
                    if (data.getJSONObject(i).has("count"))
                        count = data.getJSONObject(i).getString("count");


                    if (streamerDetailLiveList != null && streamerDetailLiveList.size() > 0) {
                        for (AllStreamerData liveData : streamerDetailLiveList) {
                            if (liveData.getId().equals(id)) {

                            if (liveData.getIsLive().equals("1")){
                                Log.e("TOPcATEGORIES", "is live  : " + id + " i : "+ i);
                                return;
                            }else {

                                liveData.setLive_streaming_slug(live_streaming_slug);
                                liveData.setCount(count);
                                liveData.setIsLive("1");
                                Log.e("TOPcATEGORIES", "in side  : " + id + " i : "+ i);

                                //mostPopularLives.remove(liveData);
                                //mostPopularLives.set(0, liveData);
                                listLiveData.add(0,liveData);
                                Log.e("TOPcATEGORIES", "dateSlug is live" + streamerDetailLiveList.get(0).getIsLive());
                            }
                        }
                        }
                    }
                }

                streamerDetailLiveList.clear();
                streamerDetailLiveList.addAll(listLiveData);

                Set<AllStreamerData> set = new LinkedHashSet<>(streamerDetailLiveList);
                streamerDetailLiveList.clear();
                streamerDetailLiveList.addAll(set);

                if (streamerDetailsScreenLiveAdapter!=null && streamerDetailLiveList!=null && streamerDetailLiveList.size() > 0){
                    streamerDetailsScreenLiveAdapter.RefreshAdapter(streamerDetailLiveList);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        streamerDetailsActivity = null;
    }

    public void addSingleNewLiveShort(JSONObject data) {

        try {
            List<AllStreamerData> listLiveData = new ArrayList<>();
            listLiveData.addAll(streamerDetailLiveList);

            Log.e("Exception", "streamer sctivity live " + data.toString());

            if (data != null) {

                //Log.e(TAG, "dateSlug data" + data.toString());

                String id = "", live_streaming_slug = "", count = "";
                if (data.has("event_id")) {
                    id = data.getString("event_id");
                }
                if (data.has("live_streaming_slug")) {
                    live_streaming_slug = data.getString("live_streaming_slug");
                }
                if (data.has("count")) {
                    count = data.getString("count");
                }



                for (AllStreamerData liveData : streamerDetailLiveList) {
                    if (liveData.getId().equals(id)) {

                        if (liveData.getIsLive()!=null && liveData.getIsLive().equals("1")){
                            return;
                        }else {

                            liveData.setLive_streaming_slug(live_streaming_slug);
                            liveData.setCount(count);
                            liveData.setIsLive("1");

                            listLiveData.add(0,liveData);
                            // Log.e(TAG, "dateSlug is live" + topCategoryLiveList.get(0).getIs_live());
                        }

                    }
                }


                streamerDetailLiveList.clear();
                streamerDetailLiveList.addAll(listLiveData);

                Set<AllStreamerData> set = new LinkedHashSet<>(streamerDetailLiveList);
                streamerDetailLiveList.clear();
                streamerDetailLiveList.addAll(set);

                streamerDetailsScreenLiveAdapter.notifyDataSetChanged();
            }



        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "connectUser() jsonObject = addLiveShort() Exception" + e.toString());
        }
    }

    public void removeLiveShort(JSONObject data) {

        try {
            List<AllStreamerData> listLiveData = new ArrayList<>();
            //listLiveData.addAll(mostPopularLives);

            if (data != null) {

                //Log.e(TAG, "dateSlug data" + data.toString());

                String id = "";
                if (data.has("event_id")) {
                    id = data.getString("event_id");
                }

                for (int i = 0; i < streamerDetailLiveList.size(); i++) {
                    if (streamerDetailLiveList.get(i).getId().equals(id)) {
                        streamerDetailLiveList.get(i).setIsLive("0");
                        streamerDetailLiveList.get(i).setLive_streaming_slug(null);
                        streamerDetailLiveList.get(i).setCount(null);
                    }
                }

                List<AllStreamerData> offlineLive = new ArrayList<>();

                for (AllStreamerData liveData : streamerDetailLiveList) {

                    if (liveData.getIsLive()!=null && liveData.getIsLive().equals("1")){
                        listLiveData.add(liveData);
                        //Log.e(TAG, "listLiveData is set onPosition");
                    }else {
                        offlineLive.add(liveData);
                    }
                }
                streamerDetailLiveList.clear();
                streamerDetailLiveList.addAll(listLiveData);
                List<AllStreamerData> liveList = sortList(offlineLive, userPreferences.getTimezone());
                streamerDetailLiveList.addAll(liveList);

                streamerDetailsScreenLiveAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, "connectUser() jsonObject = addLiveShort() Exception" + e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}