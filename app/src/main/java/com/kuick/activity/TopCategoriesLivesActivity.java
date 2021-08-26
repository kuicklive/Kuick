package com.kuick.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.adapter.TopCategorieLiveAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.common.Common;
import com.kuick.databinding.ActivityTopCategorieFullScreenBinding;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.Contacts.SettingsColumns.KEY;
import static com.kuick.activity.TopCategoriesFullScreen.topCategoriesFullScreen;
import static com.kuick.util.comman.Constants.INTENT_KEY_PRODUCT_ID;
import static com.kuick.util.comman.Constants.INTENT_KEY_USER_NAME;

public class TopCategoriesLivesActivity extends BaseActivity implements ClickEventListener{

    public static ClickEventListener topCategoriesListener;
    private static final String TAG = "TopCategorieFullScreenActivity";
    private ActivityTopCategorieFullScreenBinding binding;
    private TextView txtTitle;
    public static List<MostPopularLivesResponse> topCategoryLiveList;
    public static TopCategorieLiveAdapter adapterTopcategoriesLives;
    private String username;
    public static TopCategoriesLivesActivity isTopCategoriesActivity = null;
    private ImageView btnBack;
    private TextView btnTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopCategorieFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        topCategoriesListener = this;
        isTopCategoriesActivity = this;
        setToolBar();
        setLanguageLable();
        callTopCategoryLive();

        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() -> {
            binding.rcMostPopularLive.setVisibility(View.GONE);
            callTopCategoryLive();
        });

    }

    private void setLanguageLable() {

        if (getIntent()!=null && getIntent().getStringExtra(INTENT_KEY_USER_NAME)!=null) {

            String title = getIntent().getStringExtra(INTENT_KEY_USER_NAME);
            txtTitle.setText(title);
        }
    }

    private void setToolBar() {
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
    private void connectUser() {
        try {
            if (Common.socket != null && Common.socket.connected()) {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class,false);
                break;
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isTopCategoriesActivity = null;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isTopCategoriesActivity = null;
    }

    public List<MostPopularLivesResponse> sortList(List<MostPopularLivesResponse> mostPopularLives, String time_zone) {


        List<MostPopularLivesResponse> liveList = new ArrayList<>();

        Collections.sort(mostPopularLives, (a, b) -> {

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
            List<MostPopularLivesResponse> futureList = new ArrayList<>();
            List<MostPopularLivesResponse> pastList = new ArrayList<>();

            for (int i = 0; i < mostPopularLives.size(); i++) {

                MostPopularLivesResponse data = mostPopularLives.get(i);
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

    }


    private void callTopCategoryLive() {

        try {

            String productId = getIntent().getStringExtra(INTENT_KEY_PRODUCT_ID);
            Utility.PrintLog(TAG,"productId" + productId);

            if (checkInternetConnectionWithMessage()) {
                if (!binding.swiperefresh.isRefreshing()){
                    showLoader(true);
                }

                Call<CommonResponse> call = apiService.doCategoryLives(userPreferences.getApiKey(), productId);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());

                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true)) {
                                setResponseData(response.body());
                            }else {
                                hideShowView(binding.dataNotFound, language.getLanguage(com.kuick.util.comman.KEY.currently_we_dont_have_the_live_streaming_of_this_category));

                            }
                        }

                        hideLoader();
                        binding.swiperefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        hideShowView(binding.dataNotFound, language.getLanguage(com.kuick.util.comman.KEY.currently_we_dont_have_the_live_streaming_of_this_category));
                        Utility.PrintLog(TAG, t.toString());
                        hideLoader();
                        binding.swiperefresh.setRefreshing(false);
                    }
                });
            }

        } catch (Exception e) {
            hideShowView(binding.dataNotFound, language.getLanguage(com.kuick.util.comman.KEY.currently_we_dont_have_the_live_streaming_of_this_category));
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
            binding.swiperefresh.setRefreshing(false);
        }

    }

    public void hideShowView(View view, String message) {
        binding.dataNotFound.setVisibility(View.GONE);
        binding.swiperefresh.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        binding.getRoot().findViewById(R.id.btnTryAgain).setVisibility(View.VISIBLE);
        TextView tryAgain = binding.getRoot().findViewById(R.id.btnTryAgain);
        tryAgain.setText(language.getLanguage(com.kuick.util.comman.KEY.discover_the_best_lives));
        error.setText(message);

        tryAgain.setOnClickListener(v -> {
            isForDiscover = true;
            goToNextScreen(this,HomeActivity.class,true);
        });
    }

    private void setResponseData(CommonResponse response) {

        CommonResponse.CateGoryData categoryData = response.getCateGoryData();
        txtTitle.setText(categoryData.getName());

        this.topCategoryLiveList = response.getMostPopularLivesResponses();

        if (topCategoryLiveList!=null && topCategoryLiveList.size() > 0){

            Utility.PrintLog(TAG,"response most popular screen () : " + response);


            String current_time = userPreferences.getCurrentTime();
            String time_zone = userPreferences.getTimezone();
            userPreferences.setCurrentTime(response.getCurrent_time());
            Utility.PrintLog(TAG,"currentTimeFromServer top categories live- " + response.getCurrent_time());


            if (time_zone == null || time_zone.equals("")){
                time_zone = Constants.DEFAULT_TIMEZONE;
            }

            List<MostPopularLivesResponse> liveList = sortList(topCategoryLiveList, time_zone);
            liveList.get(liveList.size() - 1).setLoadMilliSecond(System.currentTimeMillis());

            topCategoryLiveList.clear();
            topCategoryLiveList.addAll(liveList);

            adapterTopcategoriesLives = new TopCategorieLiveAdapter(topCategoryLiveList,current_time,time_zone,this);
            binding.rcMostPopularLive.setVisibility(View.VISIBLE);
            binding.rcMostPopularLive.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
            binding.rcMostPopularLive.setAdapter(adapterTopcategoriesLives);
            connectUser();

        }
    }
    @Override
    public void MostPopularStreamer(List<MostPopularLivesResponse> topCategoryLiveList){

        if (adapterTopcategoriesLives !=null){
            adapterTopcategoriesLives.RefreshAdapter(topCategoryLiveList);
        }
    }
    public static void addLiveShort(JSONObject jsonObject) {
        try {
            JSONArray data = jsonObject.getJSONArray("data");
            List<MostPopularLivesResponse> listLiveData = new ArrayList<>();
            listLiveData.addAll(topCategoryLiveList);

            Log.e("jsonObject", "jsonObject : "+ jsonObject.toString());


            if (data != null) {

                for (int i = 0; i < data.length(); i++) {
                    String id = "", live_streaming_slug = "", count = "";
                    if (data.getJSONObject(i).has("event_id"))
                        id = data.getJSONObject(i).getString("event_id");
                    if (data.getJSONObject(i).has("live_streaming_slug"))
                        live_streaming_slug = data.getJSONObject(i).getString("live_streaming_slug");
                    if (data.getJSONObject(i).has("count"))
                        count = data.getJSONObject(i).getString("count");


                    for (MostPopularLivesResponse liveData : topCategoryLiveList) {
                        if (liveData.getId().equals(id)) {
                        if (liveData.getIs_live().equals("1")){
                            Log.e("TOPcATEGORIES", "is live  : " + id + " i : "+ i);
                            return;
                        }else {

                            liveData.setLive_streaming_slug(live_streaming_slug);
                            liveData.setCount(count);
                            liveData.setIs_live("1");
                            Log.e("TOPcATEGORIES", "in side  : " + id + " i : "+ i);

                            //mostPopularLives.remove(liveData);
                            //mostPopularLives.set(0, liveData);
                            listLiveData.add(0,liveData);

                        }
                        }
                    }
                }


                Log.e("TOPcATEGORIES", "before clear" + topCategoryLiveList.get(3).getIs_live());

                topCategoryLiveList.clear();
                topCategoryLiveList.addAll(listLiveData);

                Set<MostPopularLivesResponse> set = new LinkedHashSet<>(topCategoryLiveList);
                topCategoryLiveList.clear();
                topCategoryLiveList.addAll(set);

                Log.e("TOPcATEGORIES", "after clear" + topCategoryLiveList.get(3).getIs_live());

                adapterTopcategoriesLives.notifyDataSetChanged();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSingleNewLiveShort(JSONObject data) {

        try {
            List<MostPopularLivesResponse> listLiveData = new ArrayList<>();

            listLiveData.addAll(topCategoryLiveList);

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



                for (MostPopularLivesResponse liveData : topCategoryLiveList) {
                    if (liveData.getId().equals(id)) {

                        if (liveData.getIs_live()!=null &&  liveData.getIs_live().equals("1")){
                            return;
                        }else {

                            liveData.setLive_streaming_slug(live_streaming_slug);
                            liveData.setCount(count);
                            liveData.setIs_live("1");

                            listLiveData.add(0,liveData);
                          // Log.e(TAG, "dateSlug is live" + topCategoryLiveList.get(0).getIs_live());
                        }

                    }
                }


                topCategoryLiveList.clear();
                topCategoryLiveList.addAll(listLiveData);

                Set<MostPopularLivesResponse> set = new LinkedHashSet<>(topCategoryLiveList);
                topCategoryLiveList.clear();
                topCategoryLiveList.addAll(set);

                adapterTopcategoriesLives.notifyDataSetChanged();
            }



        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "connectUser() jsonObject = addLiveShort() Exception" + e.toString());
        }
    }

    public void removeLiveShort(JSONObject data) {

        try {
            List<MostPopularLivesResponse> listLiveData = new ArrayList<>();
            //listLiveData.addAll(mostPopularLives);

            if (data != null) {

                //Log.e(TAG, "dateSlug data" + data.toString());

                String id = "";
                if (data.has("event_id")) {
                    id = data.getString("event_id");
                }

                for (int i = 0; i < topCategoryLiveList.size(); i++) {
                    if (topCategoryLiveList.get(i).getId().equals(id)) {
                        topCategoryLiveList.get(i).setIs_live("0");
                        topCategoryLiveList.get(i).setLive_streaming_slug(null);
                        topCategoryLiveList.get(i).setCount(null);
                    }
                }

                List<MostPopularLivesResponse> offlineLive = new ArrayList<>();

                for (MostPopularLivesResponse liveData : topCategoryLiveList) {

                    if (liveData.getIs_live()!=null && liveData.getIs_live().equals("1")){
                        listLiveData.add(liveData);
                        //Log.e(TAG, "listLiveData is set onPosition");
                    }else {
                        offlineLive.add(liveData);
                    }
                }
                topCategoryLiveList.clear();
                topCategoryLiveList.addAll(listLiveData);
                List<MostPopularLivesResponse> liveList = sortList(offlineLive, userPreferences.getTimezone());
                topCategoryLiveList.addAll(liveList);

                adapterTopcategoriesLives.notifyDataSetChanged();
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