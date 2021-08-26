package com.kuick.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.activity.CartPageActivity;
import com.kuick.activity.HomeActivity;
import com.kuick.adapter.MostPopulerLiveAdapter;
import com.kuick.common.Common;
import com.kuick.databinding.ActivityNewStreamersFullScreenBinding;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.kuick.util.comman.Utility.goToNextScreen;

public class DiscoverFragment extends BaseFragment implements View.OnClickListener ,ClickEventListener {
    public static ClickEventListener mostPopularListener;
    private ActivityNewStreamersFullScreenBinding binding;
    private TextView txtTitle;
    public static List<MostPopularLivesResponse> mostPopularLives;
    private MostPopulerLiveAdapter adapterMostPopularLives;
    private long tStart;

    public static DiscoverFragment newInstance() {
        Bundle bundle = new Bundle();
        DiscoverFragment fragment = new DiscoverFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

   public static  String TAG = "DiscoverFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityNewStreamersFullScreenBinding.inflate(inflater, container, false);
        mostPopularListener = this;
        setToolBar();
        setLanguageLable();
        tStart = System.currentTimeMillis();
        callMostPopularLives();

        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() -> {
            tStart = System.currentTimeMillis();
            binding.rcMostPopularLive.setVisibility(View.GONE);
            callMostPopularLives();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

    }

    private void refreshAdapter() {

        if (adapterMostPopularLives!=null && adapterMostPopularLives.getItemCount() > 0){

            new Handler().postDelayed(() -> {
                adapterMostPopularLives.notifyDataSetChanged();
            },1000);
        }
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
//        mostPopularLives.addAll(0,liveEventData);

//        Collections.sort(mostPopularLives, (s1, s2) -> s1.getCountry().compareToIgnoreCase("United States of America"));
    }


    private void callMostPopularLives() {

        try {

            if (checkInternetConnectionWithMessage(getContext())) {

                if (!binding.swiperefresh.isRefreshing()){
                    showLoader(true);
                }

                Call<CommonResponse> call = apiService.doMostPopularLives(userPreferences.getApiKey());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code(),getActivity());

                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true)) {
                                setResponseData(response.body());

                            }else {
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                            }
                        }

                        binding.swiperefresh.setRefreshing(false);
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                        binding.swiperefresh.setRefreshing(false);
                        hideLoader();
                    }
                });
            } else {
                binding.swiperefresh.setRefreshing(false);
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            binding.swiperefresh.setRefreshing(false);
            hideLoader();
        }

    }

    private void setResponseData(CommonResponse response) {
        Utility.PrintLog(TAG, "response most popular screen () : " + response);
        this.mostPopularLives = response.getMostPopularLivesResponses();

        String current_time = userPreferences.getCurrentTime();
        String time_zone = userPreferences.getTimezone();
        userPreferences.setCurrentTime(response.getCurrent_time());
        Utility.PrintLog(TAG,"currentTimeFromServer - discover frgment" + response.getCurrent_time());

        if (time_zone == null || time_zone.equals(""))
        {
            time_zone = Constants.DEFAULT_TIMEZONE;
        }

        List<MostPopularLivesResponse> liveList = sortList(mostPopularLives, time_zone);
        liveList.get(liveList.size() - 1).setLoadMilliSecond(System.currentTimeMillis());

        mostPopularLives.clear();
        mostPopularLives.addAll(liveList);

        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        Utility.PrintLog(TAG,"tDelta - " + tDelta);
        Utility.PrintLog(TAG,"elapsedSeconds - " + elapsedSeconds);
        Utility.PrintLog(TAG,"response.getCurrent_time() - " + response.getCurrent_time());
        long time = Long.parseLong(response.getCurrent_time()) - tDelta;
        Utility.PrintLog(TAG,"time - " + time);

        adapterMostPopularLives = new MostPopulerLiveAdapter(mostPopularLives, response.getCurrent_time(), time_zone,userPreferences.getImageUrl());
        binding.rcMostPopularLive.setVisibility(View.VISIBLE);

        binding.rcMostPopularLive.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rcMostPopularLive.setAdapter(adapterMostPopularLives);

        connectUser();


    }
    public void connectUser()
    {

        try {

            if (Common.socket != null && Common.socket.connected())
            {
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


    private void setLanguageLable() {

        try{

            if (language!=null){

                txtTitle.setText(language.getLanguage(KEY.most_popular_lives));

            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setToolBar() {
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cart:
                goToNextScreen(getActivity(), CartPageActivity.class,false);
                break;
        }
    }

    @Override
    public void MostPopularStreamer(List<MostPopularLivesResponse> mostPopularLivesResponses){

        Utility.PrintLog("call", "Refresh Discover Adapter");

        if (adapterMostPopularLives!=null){
            adapterMostPopularLives.RefreshAdapter(mostPopularLivesResponses,binding.rcMostPopularLive);
        }
    }

    public void addLiveShort(JSONObject jsonObject) {

        try {
            JSONArray data = jsonObject.getJSONArray("data");
            List<MostPopularLivesResponse> listLiveData = new ArrayList<>();

            listLiveData.addAll(mostPopularLives);

            if (data != null) {

               Log.e(TAG, "dateSlug data" + data + System.currentTimeMillis());

               for (int i = 0; i < data.length(); i++) {
                   String id = "", live_streaming_slug = "", count = "";
                   if (data.getJSONObject(i).has("event_id")) {
                       id = data.getJSONObject(i).getString("event_id");
                   }
                   if (data.getJSONObject(i).has("live_streaming_slug")) {
                       live_streaming_slug = data.getJSONObject(i).getString("live_streaming_slug");
                   }
                   if (data.getJSONObject(i).has("count")) {
                       count = data.getJSONObject(i).getString("count");
                   }

                   Log.e(TAG, "id : " + id + " i : "+ i);



                   for (MostPopularLivesResponse liveData : mostPopularLives) {
                       if (liveData.getId().equals(id)) {


                           if (liveData.getIs_live().equals("1")){
                               Log.e(TAG, "is live  : " + id + " i : "+ i);
                               return;
                           }else {

                               liveData.setLive_streaming_slug(live_streaming_slug);
                               liveData.setCount(count);
                               liveData.setIs_live("1");
                               Log.e(TAG, "in side  : " + id + " i : "+ count);

                               listLiveData.add(0,liveData);
                               Log.e(TAG, "dateSlug is live" + mostPopularLives.get(0).getIs_live());
                           }

                       }
                   }
               }

                mostPopularLives.clear();
                mostPopularLives.addAll(listLiveData);

                Set<MostPopularLivesResponse> set = new LinkedHashSet<>(mostPopularLives);
                mostPopularLives.clear();
                mostPopularLives.addAll(set);


                Log.e(TAG, "dateSlug size" + System.currentTimeMillis());
                adapterMostPopularLives.notifyDataSetChanged();
           }



        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "connectUser() jsonObject = addLiveShort() Exception" + e.toString());
        }
    }

    public void addSingleNewLiveShort(JSONObject data) {

        try {
            List<MostPopularLivesResponse> listLiveData = new ArrayList<>();
            listLiveData.addAll(mostPopularLives);

            if (data != null) {

                Log.e(TAG, "dateSlug data" + data.toString());

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



                    for (MostPopularLivesResponse liveData : mostPopularLives) {
                        if (liveData.getId().equals(id)) {

                            if (liveData.getIs_live()!=null  &&  liveData.getIs_live().equals("1")){
                                return;
                            }else {

                                liveData.setLive_streaming_slug(live_streaming_slug);
                                liveData.setCount(count);
                                liveData.setIs_live("1");

                                listLiveData.add(0,liveData);
                                Log.e(TAG, "dateSlug is live" + mostPopularLives.get(0).getIs_live());
                            }

                        }
                    }


                mostPopularLives.clear();
                mostPopularLives.addAll(listLiveData);

                Set<MostPopularLivesResponse> set = new LinkedHashSet<>(mostPopularLives);
                mostPopularLives.clear();
                mostPopularLives.addAll(set);

                Log.e(TAG, "dateSlug size" + System.currentTimeMillis());
                adapterMostPopularLives.notifyDataSetChanged();
            }



        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "connectUser() jsonObject = addLiveShort() Exception" + e.toString());
        }
    }

    public void removeLiveShort(JSONObject data) {

        try {
            List<MostPopularLivesResponse> listLiveData = new ArrayList<>();
            //listLiveData.addAll(mostPopularLives);

            if (data != null) {

                Log.e(TAG, "dateSlug data" + data.toString());

                String id = "";
                if (data.has("event_id")) {
                    id = data.getString("event_id");
                }

                for (int i = 0; i < mostPopularLives.size(); i++) {
                    if (mostPopularLives.get(i).getId().equals(id)) {
                        mostPopularLives.get(i).setIs_live("0");
                        mostPopularLives.get(i).setLive_streaming_slug(null);
                        mostPopularLives.get(i).setCount(null);
                    }
                }

                List<MostPopularLivesResponse> offlineLive = new ArrayList<>();

                for (MostPopularLivesResponse liveData : mostPopularLives) {

                    if (liveData.getIs_live()!=null && liveData.getIs_live().equals("1")){
                        listLiveData.add(liveData);
                        Log.e(TAG, "listLiveData is set onPosition");
                    }else {
                        offlineLive.add(liveData);
                    }
                }
                mostPopularLives.clear();
                mostPopularLives.addAll(listLiveData);
                List<MostPopularLivesResponse> liveList = sortList(offlineLive, userPreferences.getTimezone());
                mostPopularLives.addAll(liveList);

                adapterMostPopularLives.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "connectUser() jsonObject = addLiveShort() Exception" + e.toString());
        }
    }

}
