package com.kuick.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.activity.LiveActivity;
import com.kuick.activity.StreamerDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.TreandingStreamersItemBinding;
import com.kuick.model.TrendingStreamers;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Utility.PrintLog;

public class TrendingStreamersDashboardAdapter extends RecyclerView.Adapter<TrendingStreamersDashboardAdapter.ViewHolder> {

    List<TrendingStreamers> trendingStreamersList;
    private Context mContext;

    private String TAG = "TrendingStreamersDashboard";

    public TrendingStreamersDashboardAdapter(List<TrendingStreamers> trendingStreamersList) {
            this.trendingStreamersList =trendingStreamersList;

    }

    @NonNull
    @Override
    public TrendingStreamersDashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(TreandingStreamersItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingStreamersDashboardAdapter.ViewHolder holder, int position) {
            onBindData(holder,position);
    }
    private void onBindData(ViewHolder holder, int position) {

        TrendingStreamers trendingStreamers = trendingStreamersList.get(position);

        holder.binding.userName.setText(trendingStreamers.getFull_name());
        holder.binding.countryName.setText(trendingStreamers.getCountry());

        BaseActivity.showGlideImage(mContext, trendingStreamers.getMain_banner(), holder.binding.streamersBanner);
        BaseActivity.showGlideImage(mContext, trendingStreamers.getProfile_pic(), holder.binding.profilePicture);

        Utility.PrintLog(TAG,"trending adapter is_live : " + trendingStreamers.getIs_live());
        Utility.PrintLog(TAG,"trending adapter id : " + trendingStreamers.getId());

        if (trendingStreamers.getIs_live()!=null && trendingStreamers.getIs_live().equals("1") && trendingStreamers.getLive_streaming_slug()!=null) {
            ShowView(holder);
            holder.binding.liveWatcher.setText(Utility.setValidWatcherCount(trendingStreamers.getCount()));
        } else {
            hideView(holder);
        }

        holder.itemView.setOnClickListener(v -> {

            TrendingStreamers data = trendingStreamersList.get(position);

            if (data.getIs_live()!=null && data.getIs_live().equals("1") && data.getLive_streaming_slug()!=null){
                callLiveStreamingAPI(data);
            }else mContext.startActivity(new Intent(mContext, StreamerDetailsActivity.class).putExtra(Constants.INTENT_KEY_USERNAME,data.getUsername()));


        } );

    }

    private void hideView(ViewHolder holder) {
        holder.binding.liveIcon.setVisibility(View.GONE);
        holder.binding.liveWatcher.setVisibility(View.GONE);
        holder.binding.btnLive.setVisibility(View.GONE);
    }

    private void ShowView(ViewHolder holder) {
        holder.binding.liveIcon.setVisibility(View.VISIBLE);
        holder.binding.liveWatcher.setVisibility(View.VISIBLE);
        holder.binding.btnLive.setVisibility(View.VISIBLE);
    }


    @Override
    public int getItemCount() {
        return trendingStreamersList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TreandingStreamersItemBinding binding;
        public ViewHolder(@NonNull TreandingStreamersItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
    public void callLiveStreamingAPI(TrendingStreamers data) {

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

                        if (response.body()!=null) {
                            if (homeActivity.checkResponseStatusWithMessage(response.body(), true)) {
                                if (response.body() != null) {
                                    CommonResponse lievResponse = response.body();

                                    homeActivity.config().setChannelName(lievResponse.getSlugData().getLive_streaming_slug());

                                    try {
                                        Utility.PrintLog(TAG,"liveStreamingSlug : " + liveStreamingSlug);

                                        mContext.startActivity(new Intent(mContext, LiveActivity.class)
                                                .putExtra(Constants.INTENT_KEY_STREAM_SLUG, liveStreamingSlug)
                                                .putExtra(Constants.INTENT_KEY_LIVE_WATCHER_COUNTS,count));


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

}
