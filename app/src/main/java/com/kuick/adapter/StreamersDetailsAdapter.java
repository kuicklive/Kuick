package com.kuick.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.AllStreamerData;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.activity.LiveActivity;
import com.kuick.activity.StreamerDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.LiveBannerPagerBinding;
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
import static com.kuick.fragment.HomeFragment.TAG;
import static com.kuick.fragment.HomeFragment.homeFragment;
import static com.kuick.util.comman.Utility.PrintLog;

public class StreamersDetailsAdapter extends RecyclerView.Adapter<StreamersDetailsAdapter.ViewHolder> {

    private List<AllStreamerData> allStreamerDataList;
    private final String currentTime,timezone,TAG = "StreamersDetailsAdapter";
    private StreamerDetailsActivity mContext;

    public StreamersDetailsAdapter(List<AllStreamerData> allStreamerData, String currentTime, String timezone, StreamerDetailsActivity streamerDetailsActivity) {
        this.allStreamerDataList = allStreamerData;
        this.currentTime = currentTime;
        this.timezone = timezone;
        this.mContext = streamerDetailsActivity;
    }

    public void RefreshAdapter(List<AllStreamerData> allStreamerData){
        this.allStreamerDataList = allStreamerData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StreamersDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(StreamersDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StreamersDetailsAdapter.ViewHolder holder, int position)
    {
        if (holder.timer != null) {
            holder.timer.cancel();
        }
                setLanguageLable(holder);
                holder.setIsRecyclable(false);
                onBindData(holder,position);
    }

    private void onBindData(ViewHolder holder, int position)
    {
        AllStreamerData data = allStreamerDataList.get(position);
        Utility.PrintLog(TAG,"data status : " + data.getStatus());

        BaseActivity.showGlideImage(mContext,data.getLive_streaming_image(),holder.itemBinding.streamersBanner);
        holder.itemBinding.liveStreamingName.setVisibility(View.VISIBLE);
        holder.itemBinding.liveStreamingName.setText(data.getLive_streaming_name());



        if (data.getIsLive()!=null &&  data.getIsLive().equals("1") && data.getLive_streaming_slug()!=null) {
            ShowView(holder);
            holder.itemBinding.liveWatcher.setText(Utility.setValidWatcherCount(data.getCount()));
            setZero(holder.itemBinding);
        } else {
            hideView(holder);
        }

        if (data.getStatus().equals(Constants.Approved))
        {
            countDownTimer(holder,position);
        }

        holder.itemBinding.btnNotifyMe.setOnClickListener(v -> {

            AllStreamerData dataList = allStreamerDataList.get(position);

            if (holder.itemBinding.btnNotifyMe.getText().equals(mContext.language.getLanguage(KEY.watch_live_streaming))) {
                //watch live streaming go to live Activity
                callLiveStreamingAPI(dataList);

            } else {
                //notify for live start
                onClickNotifyMe(dataList.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {

            AllStreamerData liveData = allStreamerDataList.get(position);

            if (liveData.getIsLive()!=null && liveData.getIsLive().equals("1") && liveData.getLive_streaming_slug()!=null){
                callLiveStreamingAPI(liveData);
            }

        });
    }

    private void setLanguageLable(ViewHolder holder) {

        try {

            if (homeActivity.language != null) {

                holder.itemBinding.txtDay.setText(homeActivity.language.getLanguage(KEY.days));
                holder.itemBinding.txtHourse.setText(homeActivity.language.getLanguage(KEY.hours));
                holder.itemBinding.txtMinutes.setText(homeActivity.language.getLanguage(KEY.minutes));
                holder.itemBinding.txtSecondTag.setText(homeActivity.language.getLanguage(KEY.seconds));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }

    public void onClickNotifyMe(String streamer_id) {

        try {

            if (mContext.checkInternetConnectionWithMessage()) {
                mContext.showLoader(true);


                Call<BaseResponse> call = mContext.apiService.doNotifyMe(mContext.userPreferences.getApiKey(), mContext.userPreferences.getUserId(), streamer_id);
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response) {
                        PrintLog(TAG, "response : " + response);
                        mContext.checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            mContext.showSnackErrorMessage(mContext.language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            mContext.hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (mContext.checkResponseStatusWithMessage(response.body(), true)) {
                                mContext.showSnackResponse(mContext.language.getLanguage(response.body().getMessage()));
                            } else
                                mContext.showSnackErrorMessage(mContext.language.getLanguage(response.body().getMessage()));
                        }
                        mContext.hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        mContext.hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            mContext.hideLoader();
        }
    }


    public void callLiveStreamingAPI(AllStreamerData data) {

        try {

            String liveStreamingSlug = data.getLive_streaming_slug();
            String count = data.getCount();


            if (mContext.checkInternetConnectionWithMessage()) {
                mContext.showLoader(true);


                Call<CommonResponse> call = mContext.apiService.doLiveStreamingSlugData(mContext.userPreferences.getApiKey(), liveStreamingSlug,homeActivity.userPreferences.getUserId());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        PrintLog(TAG, "response : " + response);
                        mContext.checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            mContext.showSnackErrorMessage(mContext.language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            mContext.hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (mContext.checkResponseStatusWithMessage(response.body(), true)) {
                                if (response.body() != null) {
                                    CommonResponse lievResponse = response.body();

                                    mContext.config().setChannelName(lievResponse.getSlugData().getLive_streaming_slug());

                                    try {

                                        mContext.startActivity(new Intent(mContext, LiveActivity.class)
                                                .putExtra(Constants.INTENT_KEY_STREAM_SLUG, liveStreamingSlug)
                                                .putExtra(Constants.INTENT_KEY_LIVE_WATCHER_COUNTS, count));

                                    } catch (Exception e) {
                                        Utility.PrintLog(TAG, "exception " + e.toString());
                                    }
                                }
                            } else
                                mContext.showSnackErrorMessage(mContext.language.getLanguage(response.body().getMessage()));
                        }
                        mContext.hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        mContext.hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            mContext.hideLoader();
        }
    }



    @Override
    public int getItemCount() {
        return allStreamerDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CountDownTimer timer;
        private StreamersDetailsItemBinding itemBinding;
        public ViewHolder(@NonNull StreamersDetailsItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }

    private void setZero(StreamersDetailsItemBinding view) {
        view.txtDays.setText(Constants.ZERO);
        view.txthours.setText(Constants.ZERO);
        view.txtMunutes.setText(Constants.ZERO);
        view.txtSeconds.setText(Constants.ZERO);
    }

    private void countDownTimer(ViewHolder holder, int position) {


        long milliseconds = 0, diff = 0;
        CountDownTimer mCountDownTimer;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        Utility.PrintLog(TAG,"TimeZone - " + timezone);
        Utility.PrintLog(TAG,"getTimeZone - " + TimeZone.getTimeZone(timezone));
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        Date endDate;
        String today;
        try {
            endDate = formatter.parse(allStreamerDataList.get(position).getClock_end_time());
            Utility.PrintLog(TAG,"formatter count down end date - " + endDate);
            Utility.PrintLog(TAG,"backend count down end date - " + allStreamerDataList.get(position).getClock_end_time());
            today = Utility.getTodayDate(timezone);

            Date currentChileTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(today);
            Utility.PrintLog("TODAYANDENDDATE","start = " + currentChileTime);

            Date d1 = currentChileTime;//sdformat.parse("2019-04-15");
            Date d2 = endDate;//sdformat.parse("2019-08-10");

            if(d1.compareTo(d2) > 0)
            {
                holder.itemBinding.layLive.setVisibility(View.GONE);
                holder.itemBinding.btnNotifyMe.setText(homeActivity.language.getLanguage("Watch Live Stream"));
                Utility.PrintLog("dateComparision","Date 1 occurs after Date 2 : (Expired) " + d1);
                setZero(holder.itemBinding);
                return;
            } else if(d1.compareTo(d2) < 0)
            {

                Utility.PrintLog("dateComparision","Date 1 occurs before Date 2 : (Valid date)" + d1);
            } else if(d1.compareTo(d2) == 0)
            {
                Utility.PrintLog("dateComparision","both are equal : " + d1);
                holder.itemBinding.layLive.setVisibility(View.GONE);
                holder.itemBinding.btnNotifyMe.setText(homeActivity.language.getLanguage("Watch Live Stream"));
            }

          /*  if (today.equals(allStreamerDataList.get(position).getClock_end_time())) {
                setZero(holder.itemBinding);

                holder.itemBinding.layLive.setVisibility(View.GONE);
                holder.itemBinding.btnNotifyMe.setText(homeActivity.language.getLanguage(KEY.watch_live_streaming));
                Utility.PrintLog(TAG, "count down zero ");
                return;
            }*/

            milliseconds = endDate.getTime();
            Utility.PrintLog(TAG,"count down end date miliseconds - " + endDate);

          /*  if (new Date().getTime() >= milliseconds) {
                setZero(holder.itemBinding);
                return;
            }*/

            if (allStreamerDataList.get(position).getClock_end_time() != null && !allStreamerDataList.get(position).getClock_end_time().equals("")){
                holder.itemBinding.btnNotifyMe.setVisibility(View.VISIBLE);
                holder.itemBinding.btnNotifyMe.setText(mContext.language.getLanguage(KEY.notify_me));

            }else {
                holder.itemBinding.btnNotifyMe.setVisibility(View.GONE);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Utility.PrintLog(TAG,"current star time - " + Long.parseLong(currentTime));
        final long[] startTime = {Long.parseLong(currentTime)};
        diff = milliseconds - startTime[0];
        Utility.PrintLog(TAG,"diff = milliseconds - startTime[0];" + diff);

        Long loadMilli = allStreamerDataList.get(allStreamerDataList.size() - 1).getLoadMilliSecond();
        Long currentMilli = System.currentTimeMillis();
        diff = diff - (currentMilli - loadMilli);

        Utility.PrintLog(TAG, "diff = diff - (currentMilli - loadMilli);" + diff);

        holder.timer = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {

                    startTime[0] = startTime[0] + 2;

                    long mil = millisUntilFinished ;
                    Utility.PrintLog(TAG,"millisUntilFinished - " + mil);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mil);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mil);
                    long hours = TimeUnit.MILLISECONDS.toHours(mil) - ((TimeUnit.MILLISECONDS.toDays(mil) * 24));
                    long days = TimeUnit.MILLISECONDS.toDays(mil);

                   /* if (hours == 24)
                    {
                        holder.itemBinding.txthours.setText(String.valueOf(hours));
                    }else {
                        holder.itemBinding.txthours.setText(String.valueOf(hours + 1));
                    }*/

                    holder.itemBinding.txthours.setText(String.valueOf(hours));
                    holder.itemBinding.txtDays.setText(String.valueOf(days));
                    holder.itemBinding.txtMunutes.setText(String.valueOf(minutes % 60));
                    holder.itemBinding.txtSeconds.setText(String.valueOf(seconds % 60));


                } catch (Exception e) {
                    Utility.PrintLog(TAG, "Exception " + e.toString());
                }
            }

            @Override
            public void onFinish() {

            //    Utility.PrintLog("onFinish()","show live");
                AllStreamerData data = allStreamerDataList.get(position);

                if (data.getIsLive()!=null && data.getIsLive().equals("1")) {
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

}
