package com.kuick.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.kuick.R;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.activity.LiveActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.LiveBannerPagerBinding;
import com.kuick.model.BannerDetails;
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
import static com.kuick.util.comman.Utility.PrintLog;

public class LiveBannerPagerAdapter extends PagerAdapter {

    private final List<BannerDetails> bannerDetailsList;
    private final String current_time, timeZone;
    private Context mContext;

    public LiveBannerPagerAdapter(List<BannerDetails> bannerDetails, String current_time, String timeZone) {
        this.bannerDetailsList = bannerDetails;
        this.current_time = current_time;
        this.timeZone = timeZone;
    }

    @Override
    public int getCount() {
        return bannerDetailsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mContext = container.getContext();
        @NonNull LiveBannerPagerBinding view = LiveBannerPagerBinding.inflate(LayoutInflater.from(container.getContext()), container, false);
        container.addView(view.getRoot());

        onDataSet(view, position);

        return view.getRoot();
    }

    private void onDataSet(LiveBannerPagerBinding view, int position) {
        setLanguageLable(view);

        BannerDetails bannerDetail = bannerDetailsList.get(position);
        String imgUrl = bannerDetail.getBanner_mobile_image();
        BaseActivity.showGlideImage(mContext, imgUrl, view.bannerImage);

        if (bannerDetail.getBanner_type().equals(Constants.BANNER_TYPE_TIMER)) {
            view.layBottomCountingViewNButton.setVisibility(View.VISIBLE);


            if (bannerDetail.getClock_end_time() != null && !bannerDetail.getClock_end_time().equals("")) {
                if (homeActivity.language != null) {
                    view.btnNotifyMe.setText(homeActivity.language.getLanguage(KEY.notify_me));
                }
                countDown(view, bannerDetail.getClock_end_time());
            } else {
                if (homeActivity.language != null) {
                    view.btnNotifyMe.setText(homeActivity.language.getLanguage(KEY.watch_live_streaming));
                }
            }
        }else if (bannerDetail.getBanner_type().equals(Constants.BANNER_TYPE_SIMPLE)){
            view.layTimer.setVisibility(View.GONE);
            if (homeActivity.language != null) {
                view.btnNotifyMe.setText(homeActivity.language.getLanguage(KEY.discover_the_best_lives));
            }

        }

        view.btnNotifyMe.setOnClickListener(v -> {
            BannerDetails data = bannerDetailsList.get(position);

            if (data.getBanner_type().equals(Constants.BANNER_TYPE_SIMPLE)){
                homeActivity.onSelectedView(homeActivity.binding.ivFooterDiscover, homeActivity.binding.txtFooterDiscover);
                homeActivity.showDiscoverFragment();
                return;
            }

            if (view.btnNotifyMe.getText().equals(homeActivity.language.getLanguage(KEY.watch_live_streaming))) {
                //watch live streaming go to live Activity

                callLiveStreamingAPI(data);

            } else {
                //notify for live start
                onClickNotifyMe(data.getLive_stream_id());

            }

        });
    }

    private void setLanguageLable(LiveBannerPagerBinding view) {

        try {

            if (homeActivity.language != null) {

                view.txtDay.setText(homeActivity.language.getLanguage(KEY.days));
                view.txtHourse.setText(homeActivity.language.getLanguage(KEY.hours));
                view.txtMinutes.setText(homeActivity.language.getLanguage(KEY.minutes));
                view.txtSecond.setText(homeActivity.language.getLanguage(KEY.seconds));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }

    private void countDown(LiveBannerPagerBinding view, String endTime) {


        Long milliseconds = 0L, diff = 0L;

        CountDownTimer mCountDownTimer;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date endDate;
        String today;
        try {

            endDate = formatter.parse(endTime);
            today = Utility.getTodayDate(timeZone);

            if (today.equals(endTime)) {
                view.txtDays.setText(Constants.ZERO);
                view.txthours.setText(Constants.ZERO);
                view.txtMunutes.setText(Constants.ZERO);
                view.txtSeconds.setText(Constants.ZERO);

                view.layTimer.setVisibility(View.GONE);
                view.btnNotifyMe.setText(homeActivity.language.getLanguage("Watch Live Stream"));
                return;
            }

            milliseconds = endDate.getTime();

            if (new Date().getTime() >= milliseconds) {
                Utility.PrintLog(TAG, "is Expire date");
                return;
            }

        } catch (ParseException e) {
            Utility.PrintLog(TAG, "Days Left " + e.getMessage());
            e.printStackTrace();
        }

        final long[] startTime = {Long.parseLong(current_time)};
        diff = milliseconds - startTime[0];


        new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {

                    startTime[0] = startTime[0] + 2;

                    long mil = millisUntilFinished;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mil);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mil);
                    long hours = TimeUnit.MILLISECONDS.toHours(mil) - ((TimeUnit.MILLISECONDS.toDays(mil) * 24));
                    long days = TimeUnit.MILLISECONDS.toDays(mil);

                    String time = days + ":" + hours + ":" + minutes + ":" + seconds;
                    view.txtDays.setText(String.valueOf(days));
                    view.txthours.setText(String.valueOf(hours));
                    view.txtMunutes.setText(String.valueOf(minutes % 60));
                    view.txtSeconds.setText(String.valueOf(seconds % 60));


                } catch (Exception e) {
                    Utility.PrintLog(TAG, "Exception " + e.toString());
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void callLiveStreamingAPI(BannerDetails data) {

        try {

            String liveStreamingSlug = data.getLive_streaming_slug();
            String count = data.getCount();


            if (homeActivity.checkInternetConnectionWithMessage()) {
                homeActivity.showLoader(true);


                Call<CommonResponse> call = homeActivity.apiService.doLiveStreamingSlugData(homeActivity.userPreferences.getApiKey(), liveStreamingSlug,homeActivity.userPreferences.getUserId());
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


}
