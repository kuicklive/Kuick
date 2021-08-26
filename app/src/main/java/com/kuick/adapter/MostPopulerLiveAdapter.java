package com.kuick.adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.util.UniversalTimeScale;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.activity.LiveActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.StreamersDetailsItemBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.activity.LiveActivity.liveActivity;
import static com.kuick.util.comman.Utility.PrintLog;

public class MostPopulerLiveAdapter extends RecyclerView.Adapter<MostPopulerLiveAdapter.ViewHolder> {

    private static final String TAG = "MostPopulerLiveAdapter";
    private List<MostPopularLivesResponse> mostPopularLives;
    private final String current_time, timeZone;
    private Context mContext;
    private String imageBaseUrl;

    public MostPopulerLiveAdapter(List<MostPopularLivesResponse> mostPopularLives, String current_time, String time_zone, String imageBaseUrl) {
        this.mostPopularLives = mostPopularLives;
        this.current_time = current_time;
        this.timeZone = time_zone;
        this.imageBaseUrl = imageBaseUrl;
    }


    @NonNull
    @Override
    public MostPopulerLiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(StreamersDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull MostPopulerLiveAdapter.ViewHolder holder, int position) {

        if (holder.timer != null) {
            holder.timer.cancel();
        }


        holder.setIsRecyclable(false);
        setLanguageLable(holder);
        onBindData(holder, position);

        if (mostPopularLives.get(position).getIs_live() != null && mostPopularLives.get(position).getIs_live().equals("1")) {
            Utility.PrintLog("LIVEID", "Live id " + mostPopularLives.get(position).getId());
        }


        holder.itemBinding.btnNotifyMe.setOnClickListener(v -> {

            MostPopularLivesResponse data = mostPopularLives.get(position);

            if (holder.itemBinding.btnNotifyMe.getText().equals(homeActivity.language.getLanguage(KEY.watch_live_streaming))) {
                //watch live streaming go to live Activity
                callLiveStreamingAPI(data);
            } else {
                //notify for live start
                onClickNotifyMe(data.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {

            MostPopularLivesResponse data = mostPopularLives.get(position);

            if (LiveActivity.liveActivity!=null && BaseActivity.isInPictureInPictureMode){

                if (LiveActivity.liveStreamerId.equals(data.getId())) {

                    if (data.getIs_live() != null && data.getIs_live().equals("1") && data.getLive_streaming_slug() != null) {
                        callLiveStreamingAPI(data);
                    }

                }else {

                    LiveActivity.liveActivity.finishWhenLiveRunningAndClickOnLiveNotification();
                    if (data.getIs_live() != null && data.getIs_live().equals("1") && data.getLive_streaming_slug() != null) {
                        LiveActivity.liveStreamerId = data.getId();
                        callLiveStreamingAPI(data);
                    }
                }

            }else {

                if (data.getIs_live() != null && data.getIs_live().equals("1") && data.getLive_streaming_slug() != null) {
                    LiveActivity.liveStreamerId = data.getId();
                    callLiveStreamingAPI(data);
                }
            }

        });

    }

    private void setLanguageLable(ViewHolder holder) {

        try {

            if (homeActivity.language != null) {

                Utility.PrintLog(TAG, "homeActivity.language.getLanguage(KEY.days) : " + homeActivity.language.getLanguage(KEY.days));
                holder.itemBinding.txtDay.setText(homeActivity.language.getLanguage(KEY.days));
                holder.itemBinding.txtHourse.setText(homeActivity.language.getLanguage(KEY.hours));
                holder.itemBinding.txtMinutes.setText(homeActivity.language.getLanguage(KEY.minutes));
                holder.itemBinding.txtSecondTag.setText(homeActivity.language.getLanguage(KEY.seconds));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }

    public void RefreshAdapter(List<MostPopularLivesResponse> mostPopularLivesResponses, RecyclerView rcMostPopularLive) {
        this.mostPopularLives = mostPopularLivesResponses;
        notifyDataSetChanged();
        //rcMostPopularLive.scrollToPosition(0);
        rcMostPopularLive.setVisibility(View.VISIBLE);
        Log.e(TAG, "is refresh Adapter" + System.currentTimeMillis());

    }

    public void onClickNotifyMe(String streamer_id) {

        try {

            if (homeActivity.checkInternetConnectionWithMessage()) {
                homeActivity.showLoader(true);


                Call<BaseResponse> call = homeActivity.apiService.doNotifyMe(homeActivity.userPreferences.getApiKey(), homeActivity.userPreferences.getUserId(), streamer_id);
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response) {
                        PrintLog(TAG, "response : " + response);
                        homeActivity.checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            homeActivity.showSnackErrorMessage(homeActivity.language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            homeActivity.hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (homeActivity.checkResponseStatusWithMessage(response.body(), true)) {
                                homeActivity.showSnackResponse(homeActivity.language.getLanguage(response.body().getMessage()));
                            } else
                                homeActivity.showSnackErrorMessage(homeActivity.language.getLanguage(response.body().getMessage()));
                        }
                        homeActivity.hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        homeActivity.hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            homeActivity.hideLoader();
        }
    }


    public void callLiveStreamingAPI(MostPopularLivesResponse data) {

        try {

            String liveStreamingSlug = data.getLive_streaming_slug();
            String count = data.getCount();


            if (homeActivity.checkInternetConnectionWithMessage()) {
                homeActivity.showLoader(true);


                Call<CommonResponse> call = homeActivity.apiService.doLiveStreamingSlugData(homeActivity.userPreferences.getApiKey(), liveStreamingSlug, homeActivity.userPreferences.getUserId());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        PrintLog(TAG, "response : " + response);
                        homeActivity.checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            homeActivity.showSnackErrorMessage(homeActivity.language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            homeActivity.hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (homeActivity.checkResponseStatusWithMessage(response.body(), true)) {
                                if (response.body() != null) {
                                    CommonResponse lievResponse = response.body();

                                    homeActivity.config().setChannelName(lievResponse.getSlugData().getLive_streaming_slug());

                                    try {

                                        mContext.startActivity(new Intent(mContext, LiveActivity.class)
                                                .putExtra(Constants.INTENT_KEY_STREAM_SLUG, liveStreamingSlug)
                                                .putExtra(Constants.INTENT_KEY_LIVE_WATCHER_COUNTS, count));

                                    } catch (Exception e) {
                                        Utility.PrintLog(TAG, "exception " + e.toString());
                                    }
                                }
                            } else
                                homeActivity.showSnackErrorMessage(homeActivity.language.getLanguage(response.body().getMessage()));
                        }
                        homeActivity.hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        homeActivity.hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            homeActivity.hideLoader();
        }
    }


    private void countDownTimer(ViewHolder holder, int position) {


        long milliseconds = 0, diff = 0;
        CountDownTimer mCountDownTimer;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date endDate;
        String today;
        try {
            endDate = formatter.parse(mostPopularLives.get(position).getClock_end_time());
            today = Utility.getTodayDate(timeZone);
            Utility.PrintLog("TODAYANDENDDATE", "Today Date = " + today + "\nEnd Date = " + endDate + "\nTime zone = " + timeZone);

            Date currentChileTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(today);
            Utility.PrintLog("TODAYANDENDDATE", "start = " + currentChileTime);

            Date d1 = currentChileTime;//sdformat.parse("2019-04-15");
            Date d2 = endDate;//sdformat.parse("2019-08-10");

            if (d1.compareTo(d2) > 0) {
                holder.itemBinding.layLive.setVisibility(View.GONE);
                holder.itemBinding.btnNotifyMe.setText(homeActivity.language.getLanguage(KEY.watch_live_streaming));
                Utility.PrintLog("dateComparision", "Date 1 occurs after Date 2 : (Expired) " + d1);
                setZero(holder.itemBinding);
                return;
            } else if (d1.compareTo(d2) < 0) {

                Utility.PrintLog("dateComparision", "Date 1 occurs before Date 2 : (Valid date)" + d1);
            } else if (d1.compareTo(d2) == 0) {
                Utility.PrintLog("dateComparision", "both are equal : " + d1);
                holder.itemBinding.layLive.setVisibility(View.GONE);
                holder.itemBinding.btnNotifyMe.setText(homeActivity.language.getLanguage(KEY.watch_live_streaming));
            }


         /*   if (today.equals(mostPopularLives.get(position).getClock_end_time())) {
                setZero(holder.itemBinding);


                Utility.PrintLog(TAG, "count down zero ");
                return;
            }*/

            milliseconds = endDate.getTime();

        /*    if (new Date().getTime() >= milliseconds)
            {
                Utility.PrintLog(TAG,"End date is grater than today's date = " + milliseconds);
                setZero(holder.itemBinding);
                return;
            }*/

            if (mostPopularLives.get(position).getClock_end_time() != null && !mostPopularLives.get(position).getClock_end_time().equals("")) {
                holder.itemBinding.btnNotifyMe.setVisibility(View.VISIBLE);
                holder.itemBinding.btnNotifyMe.setText(homeActivity.language.getLanguage(KEY.notify_me));
                Utility.PrintLog(TAG, "data.getClock_end_time() if part: " + mostPopularLives.get(position).getClock_end_time() + "position : " + position);

            } else {
                holder.itemBinding.btnNotifyMe.setVisibility(View.GONE);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Utility.PrintLog(TAG, "exception : " + e.toString());
        }

        Utility.PrintLog(TAG, "CurrentTime - " + current_time);

        final long[] startTime = {Long.parseLong(current_time)};
        diff = milliseconds - startTime[0];
        Utility.PrintLog(TAG, "diff - " + diff);

        Long loadMilli = mostPopularLives.get(mostPopularLives.size() - 1).getLoadMilliSecond();
        Long currentMilli = System.currentTimeMillis();
        Utility.PrintLog(TAG, "diff - " + currentMilli);
        diff = diff - (currentMilli - loadMilli);
        Utility.PrintLog(TAG, "diff = dif - (currentMilli - loadMilli) " + diff);

        holder.timer = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {

                    startTime[0] = startTime[0] + 2;

                    long mil = millisUntilFinished;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mil);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mil);
                    long hours = TimeUnit.MILLISECONDS.toHours(mil) - ((TimeUnit.MILLISECONDS.toDays(mil) * 24));
                    long days = TimeUnit.MILLISECONDS.toDays(mil);


                    holder.itemBinding.txthours.setText(String.valueOf(hours));


                    holder.itemBinding.txthours.setText(String.valueOf(hours));
                    holder.itemBinding.txtDays.setText(String.valueOf(days));
                    holder.itemBinding.txtMunutes.setText(String.valueOf(minutes % 60));
                    holder.itemBinding.txtSeconds.setText(String.valueOf(seconds % 60));

                    String time = days + ":" + hours + ":" + minutes % 60 + ":" + seconds % 60;


                } catch (Exception e) {
                    Utility.PrintLog(TAG, "Exception " + e.toString());
                }
            }

            @Override
            public void onFinish() {

                MostPopularLivesResponse data = mostPopularLives.get(position);

                if (data.getIs_live().equals("1")) {
                    ShowView(holder);
                    holder.itemBinding.liveWatcher.setText(data.getCount());
                    holder.itemBinding.btnNotifyMe.setVisibility(View.GONE);
                } else {
                    hideView(holder);
                }

            }
        }.start();
    }

    private void ShowView(ViewHolder holder) {
        holder.itemBinding.liveIcon.setVisibility(View.VISIBLE);
        holder.itemBinding.liveWatcher.setVisibility(View.VISIBLE);
        holder.itemBinding.btnLive.setVisibility(View.VISIBLE);
        holder.itemBinding.btnNotifyMe.setVisibility(View.GONE);

    }

    private void hideView(ViewHolder holder) {
        holder.itemBinding.liveIcon.setVisibility(View.GONE);
        holder.itemBinding.liveWatcher.setVisibility(View.GONE);
        holder.itemBinding.btnLive.setVisibility(View.GONE);
        // holder.itemBinding.btnNotifyMe.setVisibility(View.VISIBLE);
    }

    private void onBindData(ViewHolder holder, int position) {

        try {

            MostPopularLivesResponse data = mostPopularLives.get(position);
            holder.itemBinding.streamreName.setText(data.getFull_name());
            Utility.PrintLog("newStreamers", "ID : " + data.getId() + "is Live " + data.getIs_live() + "    getCountry()   " + data.getCountry());
            holder.itemBinding.mCountry.setText(data.getCountry());

            Utility.PrintLog("newStreamers", "getLive_streaming_image : " + data.getLive_streaming_image());
            Utility.PrintLog("newStreamers", "getProfile_pic : " + data.getProfile_pic());

            if (data.getLive_streaming_image() != null) {
                BaseActivity.showGlideImageWithError(mContext, imageBaseUrl.concat(data.getLive_streaming_image()), holder.itemBinding.streamersBanner, ContextCompat.getDrawable(mContext, R.drawable.ic_company_banner_default));
            } else {
                BaseActivity.showGlideImageWithError(mContext, "", holder.itemBinding.streamersBanner, ContextCompat.getDrawable(mContext, R.drawable.ic_company_banner_default));

            }

            if (data.getProfile_pic() != null) {
                BaseActivity.showGlideImageWithError(mContext, imageBaseUrl.concat(data.getProfile_pic()), holder.itemBinding.profilePicture, ContextCompat.getDrawable(mContext, R.drawable.ic_admin));
            } else {
                BaseActivity.showGlideImageWithError(mContext, "", holder.itemBinding.profilePicture, ContextCompat.getDrawable(mContext, R.drawable.ic_admin));

            }


            Utility.PrintLog("data.getCount()", "count : " + data.getCount());

            if (data.getIs_live() != null && data.getIs_live().equals("1") && data.getLive_streaming_slug() != null) {
                ShowView(holder);
                holder.itemBinding.liveWatcher.setText(Utility.setValidWatcherCount(data.getCount()));
                setZero(holder.itemBinding);
            } else {
                hideView(holder);
            }

            if (data.getClock_end_time() != null && !data.getClock_end_time().equals("") && data.getIs_live().equals("0")) {
                Utility.PrintLog(TAG, "data.getClock_end_time() : " + data.getClock_end_time() + "id : " + data.getId());
                countDownTimer(holder, position);
            }


        } catch (Exception e) {
            Utility.PrintLog(TAG, "MostPopular most popular exception : " + e.toString());
        }

    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        if (holder instanceof ViewHolder) {
            holder.setIsRecyclable(false);
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        if (holder instanceof ViewHolder) {
            holder.setIsRecyclable(true);
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mostPopularLives.size();
    }


    private void setZero(StreamersDetailsItemBinding view) {
        view.txtDays.setText(Constants.ZERO);
        view.txthours.setText(Constants.ZERO);
        view.txtMunutes.setText(Constants.ZERO);
        view.txtSeconds.setText(Constants.ZERO);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public StreamersDetailsItemBinding itemBinding;
        CountDownTimer timer;

        public ViewHolder(@NonNull StreamersDetailsItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }


    }
}
