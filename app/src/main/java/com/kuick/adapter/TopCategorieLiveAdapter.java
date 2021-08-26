package com.kuick.adapter;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.activity.LiveActivity;
import com.kuick.activity.TopCategoriesLivesActivity;
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
import static com.kuick.util.comman.Utility.PrintLog;

public class TopCategorieLiveAdapter extends RecyclerView.Adapter<TopCategorieLiveAdapter.ViewHolder> {

    private static final String TAG = "MostPopulerLiveAdapter";
    private final String currentTime, timeZone;
    private List<MostPopularLivesResponse> topCategoryLiveList;
    private final TopCategoriesLivesActivity mContext;

    public TopCategorieLiveAdapter(List<MostPopularLivesResponse> mostPopularLives, String current_time, String time_zone, TopCategoriesLivesActivity topCategoriesLivesActivity) {
        this.topCategoryLiveList = mostPopularLives;
        this.currentTime = current_time;
        this.timeZone = time_zone;
        this.mContext = topCategoriesLivesActivity;
    }


    @NonNull
    @Override
    public TopCategorieLiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(StreamersDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopCategorieLiveAdapter.ViewHolder holder, int position) {

        if (holder.timer != null) {
            holder.timer.cancel();
        }

        holder.setIsRecyclable(false);
        setLanguageLable(holder);
        onBindData(holder, position);


    }

    public void RefreshAdapter(List<MostPopularLivesResponse> mostPopularLivesResponses) {
        this.topCategoryLiveList = mostPopularLivesResponses;
        notifyDataSetChanged();

    }

    private void onBindData(ViewHolder holder, int position) {

        try {

            MostPopularLivesResponse data = topCategoryLiveList.get(position);


            holder.itemBinding.streamreName.setText(data.getFull_name());
            holder.itemBinding.mCountry.setText(data.getCountry());

            BaseActivity.showGlideImage(mContext, data.getMain_banner(), holder.itemBinding.streamersBanner);
            BaseActivity.showGlideImage(mContext, data.getProfile_pic(), holder.itemBinding.profilePicture);


            if (data.getIs_live() != null && data.getIs_live().equals("1") && data.getLive_streaming_slug() != null) {
                ShowView(holder);

                holder.itemBinding.liveWatcher.setText(Utility.setValidWatcherCount(data.getCount()));
                setZero(holder.itemBinding);
            } else {
                hideView(holder);
            }

            if (data.getClock_end_time() != null && !data.getClock_end_time().equals("")) {
                countDownTimer(holder, position, data);
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "TopCategory most popular exception : " + e.toString());
        }

        holder.itemBinding.btnNotifyMe.setOnClickListener(v -> {

            MostPopularLivesResponse data = topCategoryLiveList.get(position);

            if (holder.itemBinding.btnNotifyMe.getText().equals(mContext.language.getLanguage(KEY.watch_live_streaming))) {
                //watch live streaming go to live Activity
                callLiveStreamingAPI(data);

            } else {
                //notify for live start
                onClickNotifyMe(data.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {

            MostPopularLivesResponse data = topCategoryLiveList.get(position);

            if (data.getIs_live() != null && data.getIs_live().equals("1") && data.getLive_streaming_slug() != null) {
                callLiveStreamingAPI(data);
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
        //holder.itemBinding.btnNotifyMe.setVisibility(View.VISIBLE);
    }

    public void callLiveStreamingAPI(MostPopularLivesResponse data) {

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


    private void countDownTimer(ViewHolder holder, int position, MostPopularLivesResponse data) {


        long milliseconds = 0, diff = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date endDate;
        String today;
        try {
            endDate = formatter.parse(topCategoryLiveList.get(position).getClock_end_time());
            today = Utility.getTodayDate(timeZone);

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

      /*      if (today.equals(topCategoryLiveList.get(position).getClock_end_time())) {
                setZero(holder.itemBinding);

                holder.itemBinding.layLive.setVisibility(View.GONE);
                holder.itemBinding.btnNotifyMe.setText(mContext.language.getLanguage(KEY.watch_live_streaming));
                Utility.PrintLog(TAG, "count down zero ");
                return;
            }*/

            milliseconds = endDate.getTime();


          /*  if (new Date().getTime() >= milliseconds) {
                setZero(holder.itemBinding);
                return;
            }*/

            if (topCategoryLiveList.get(position).getClock_end_time() != null && !topCategoryLiveList.get(position).getClock_end_time().equals("")) {
                holder.itemBinding.btnNotifyMe.setVisibility(View.VISIBLE);
                holder.itemBinding.btnNotifyMe.setText(mContext.language.getLanguage(KEY.notify_me));

            } else {
                holder.itemBinding.btnNotifyMe.setVisibility(View.GONE);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long[] startTime = {Utility.getStartTime(currentTime, timeZone)};
        diff = milliseconds - startTime[0];

        Long loadMilli = topCategoryLiveList.get(topCategoryLiveList.size() - 1).getLoadMilliSecond();
        Long currentMilli = System.currentTimeMillis();
        diff = diff - (currentMilli - loadMilli);


        holder.timer = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {

                    startTime[0] = startTime[0] + 2;

                    long mil = millisUntilFinished ;

                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mil);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mil);
                    long hours = TimeUnit.MILLISECONDS.toHours(mil) - ((TimeUnit.MILLISECONDS.toDays(mil) * 24));
                    long days = TimeUnit.MILLISECONDS.toDays(mil);


                  /*  if (hours == 24)
                    {
                        holder.itemBinding.txthours.setText(String.valueOf(hours));
                    }else {
                        holder.itemBinding.txthours.setText(String.valueOf(hours + 1));
                    }*/
                    holder.itemBinding.txthours.setText(String.valueOf(hours));
                    String time = days + ":" + hours + ":" + minutes + ":" + seconds;
                    holder.itemBinding.txtDays.setText(String.valueOf(days));
                    holder.itemBinding.txtMunutes.setText(String.valueOf(minutes % 60));
                    holder.itemBinding.txtSeconds.setText(String.valueOf(seconds % 60));

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "Exception " + e.toString());
                }
            }

            @Override
            public void onFinish() {

                MostPopularLivesResponse data = topCategoryLiveList.get(position);

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


    @Override
    public int getItemCount() {
        return topCategoryLiveList.size();
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
