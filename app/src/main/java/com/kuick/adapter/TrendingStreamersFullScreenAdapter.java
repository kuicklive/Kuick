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
import com.kuick.activity.TrendingStreamersFullScreen;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.FullTreandingStreamersItemBinding;
import com.kuick.databinding.TreandingStreamersItemBinding;
import com.kuick.model.NewStreamers;
import com.kuick.model.TrendingStreamers;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Utility.PrintLog;

public class TrendingStreamersFullScreenAdapter extends RecyclerView.Adapter<TrendingStreamersFullScreenAdapter.ViewHolder> {

    List<TrendingStreamers> trendingStreamersList;
    private TrendingStreamersFullScreen mContext;
    public String TAG = "TrendingStreamersFullScreenAdapter";

    public TrendingStreamersFullScreenAdapter(List<TrendingStreamers> trendingStreamersList, TrendingStreamersFullScreen trendingStreamersFullScreen) {
            this.trendingStreamersList =trendingStreamersList;
        this.mContext = trendingStreamersFullScreen;

    }

    @NonNull
    @Override
    public TrendingStreamersFullScreenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FullTreandingStreamersItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingStreamersFullScreenAdapter.ViewHolder holder, int position) {
            onBindData(holder,position);
    }
    private void onBindData(ViewHolder holder, int position) {

        TrendingStreamers trendingStreamers = trendingStreamersList.get(position);

        holder.binding.userName.setText(trendingStreamers.getFull_name());
        holder.binding.countryName.setText(trendingStreamers.getCountry());

        BaseActivity.showGlideImage(mContext, trendingStreamers.getMain_banner(), holder.binding.streamersBanner);
        BaseActivity.showGlideImage(mContext, trendingStreamers.getProfile_pic(), holder.binding.profilePicture);

        if (trendingStreamers.getIs_live()!=null &&  trendingStreamers.getIs_live().equals("1") && trendingStreamers.getLive_streaming_slug()!=null) {
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

    public void refreshAdapter(List<TrendingStreamers> trendingStreamersListFullScreen) {
        this.trendingStreamersList = trendingStreamersListFullScreen;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        FullTreandingStreamersItemBinding binding;
        public ViewHolder(@NonNull FullTreandingStreamersItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public void callLiveStreamingAPI(TrendingStreamers data) {


        try {

            String liveStreamingSlug = data.getLive_streaming_slug();
            String count = data.getCount();

            if (mContext.checkInternetConnectionWithMessage()) {
                mContext.showLoader(true);


                Call<CommonResponse> call = mContext.apiService.doLiveStreamingSlugData(homeActivity.userPreferences.getApiKey(), liveStreamingSlug,homeActivity.userPreferences.getUserId());
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

                        if (response.body()!=null){
                            if (mContext.checkResponseStatusWithMessage(response.body(), true)) {
                                if (response.body() != null) {
                                    CommonResponse lievResponse = response.body();

                                    mContext.config().setChannelName(lievResponse.getSlugData().getLive_streaming_slug());

                                    try {

                                        mContext.startActivity(new Intent(mContext, LiveActivity.class)
                                                .putExtra(Constants.INTENT_KEY_STREAM_SLUG, liveStreamingSlug)
                                                .putExtra(Constants.INTENT_KEY_LIVE_WATCHER_COUNTS,count));

                                    }catch (Exception e){
                                        Utility.PrintLog(TAG,"exception "+ e.toString());
                                    }
                                }
                            }else {
                                mContext.showSnackErrorMessage(mContext.language.getLanguage(response.body().getMessage()));
                            }
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

}
