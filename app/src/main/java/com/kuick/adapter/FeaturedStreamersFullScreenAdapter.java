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
import com.kuick.activity.FeaturedStreamersFullScreen;
import com.kuick.activity.LiveActivity;
import com.kuick.activity.StreamerDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.FullTreandingStreamersItemBinding;
import com.kuick.databinding.TreandingStreamersItemBinding;
import com.kuick.model.FeatureStreamers;
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

public class FeaturedStreamersFullScreenAdapter extends RecyclerView.Adapter<FeaturedStreamersFullScreenAdapter.ViewHolder> {

    private static final String TAG = "FeaturedStreamersFullScreenAdapter";
    private List<FeatureStreamers> featureStreamersList;
    private FeaturedStreamersFullScreen mContext;

    public FeaturedStreamersFullScreenAdapter(List<FeatureStreamers> featureStreamers, FeaturedStreamersFullScreen featuredStreamersFullScreen) {
        this.featureStreamersList = featureStreamers;
        this.mContext = featuredStreamersFullScreen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FullTreandingStreamersItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onBindData(holder, position);
    }

    private void onBindData(ViewHolder holder, int position) {

        FeatureStreamers featureStreamers = featureStreamersList.get(position);

        holder.binding.userName.setText(featureStreamers.getFull_name());
        holder.binding.countryName.setText(featureStreamers.getCountry());

        BaseActivity.showGlideImage(mContext, featureStreamers.getMain_banner(), holder.binding.streamersBanner);
        BaseActivity.showGlideImage(mContext, featureStreamers.getProfile_pic(), holder.binding.profilePicture);

        if (featureStreamers.getIs_live()!=null && featureStreamers.getIs_live().equals("1") && featureStreamers.getLive_streaming_slug()!=null) {
            ShowView(holder);
            holder.binding.liveWatcher.setText(Utility.setValidWatcherCount(featureStreamers.getCount()));
        } else {
            hideView(holder);
        }

        holder.itemView.setOnClickListener(v -> {
            FeatureStreamers data = featureStreamersList.get(position);

            if (data.getIs_live()!=null && data.getIs_live().equals("1") && data.getLive_streaming_slug()!=null){

                callLiveStreamingAPI(data);
            }else mContext.startActivity(new Intent(mContext, StreamerDetailsActivity.class).putExtra(Constants.INTENT_KEY_USERNAME,data.getUsername()));

        });
    }

    private void ShowView(ViewHolder holder) {
        holder.binding.liveIcon.setVisibility(View.VISIBLE);
        holder.binding.liveWatcher.setVisibility(View.VISIBLE);
        holder.binding.btnLive.setVisibility(View.VISIBLE);
    }

    private void hideView(ViewHolder holder) {
        holder.binding.liveIcon.setVisibility(View.GONE);
        holder.binding.liveWatcher.setVisibility(View.GONE);
        holder.binding.btnLive.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return featureStreamersList.size();
    }

    public void refreshAdapter(List<FeatureStreamers> featureStreamersListFullScreen) {
        this.featureStreamersList = featureStreamersListFullScreen;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public FullTreandingStreamersItemBinding binding;

        public ViewHolder(@NonNull FullTreandingStreamersItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
    public void callLiveStreamingAPI(FeatureStreamers data) {


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