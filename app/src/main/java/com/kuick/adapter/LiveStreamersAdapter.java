package com.kuick.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.activity.LiveActivity;
import com.kuick.activity.StreamerDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.LiveStreamersItemBinding;
import com.kuick.model.RecommendedStreamers;
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

public class LiveStreamersAdapter extends RecyclerView.Adapter<LiveStreamersAdapter.ViewHolder> {
    private final List<RecommendedStreamers> recommendedStreamers;
    private Context mContext;
    private final String TAG = "LiveStreamersAdapter";

    public LiveStreamersAdapter(List<RecommendedStreamers> recommendedStreamers) {
        this.recommendedStreamers = recommendedStreamers;
    }

    @NonNull
    @Override
    public LiveStreamersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LiveStreamersItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LiveStreamersAdapter.ViewHolder holder, int position) {
        onBindData(holder, position);
    }

    @SuppressLint("ResourceAsColor")
    private void onBindData(ViewHolder holder, int position) {

        RecommendedStreamers live = recommendedStreamers.get(position);



        Utility.PrintLog("liveTestDeeply", "Recommended Adapetr is live : " + live.getIs_live());
        if (live.getIs_live().equals("1") && live.getLive_streaming_slug()!=null) {
            holder.binding.parentImage.getBackground().setColorFilter(Color.parseColor("#7932EB"), PorterDuff.Mode.SRC_ATOP);
            holder.binding.btnLive.setVisibility(View.VISIBLE);

        } else {
            holder.binding.parentImage.getBackground().setColorFilter(Color.parseColor("#9a9a9a"), PorterDuff.Mode.SRC_ATOP);
            holder.binding.btnLive.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(v -> {

            RecommendedStreamers data = recommendedStreamers.get(position);

            if (data.getLive_streaming_slug()!=null && data.getIs_live().equals("1")){
                callLiveStreamingAPI(data);
            }else {
                mContext.startActivity(new Intent(mContext, StreamerDetailsActivity.class).putExtra(Constants.INTENT_KEY_USERNAME,data.getUsername()).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
            }
        });

        BaseActivity.showGlideImage(mContext, live.getProfile_pic(), holder.binding.userImage);
        holder.binding.userName.setText(live.getFull_name());
        holder.binding.countryName.setText(live.getCountry());
        Utility.PrintLog(TAG, "name : " + live.getFull_name() + " id : " + live.getId());
    }


    @Override
    public int getItemCount() {
        return recommendedStreamers.size();
    }

    public void callLiveStreamingAPI(RecommendedStreamers data) {

        try {

            String liveStreamingSlug = data.getLive_streaming_slug();
            String count = data.getCount();

            Utility.PrintLog(TAG,"liveStremingSlug : " + liveStreamingSlug);

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
                                    Utility.PrintLog(TAG,"live streaming slug = " + lievResponse.getSlugData().getLive_streaming_slug());
                                    homeActivity.config().setChannelName(lievResponse.getSlugData().getLive_streaming_slug());

                                    try {
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        public LiveStreamersItemBinding binding;

        public ViewHolder(@NonNull LiveStreamersItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

}
