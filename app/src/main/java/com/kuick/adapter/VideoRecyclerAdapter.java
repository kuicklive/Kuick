package com.kuick.adapter;

import static com.kuick.fragment.VideoClipsFragment.isSwipeRight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.manager.LifecycleListener;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.kuick.R;
import com.kuick.Response.ClipsData;
import com.kuick.activity.HomeActivity;
import com.kuick.activity.StreamerDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.VideoclipViewpagerBinding;
import com.kuick.fragment.VideoClipBottomDetailsDialog;
import com.kuick.fragment.VideoClipsFragment;
import com.kuick.util.App;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.ViewHolder> implements LifecycleListener {

    private final List<ClipsData> urlList;
    private final RecyclerView rcVideo;
    private final HomeActivity mContext;
    private final long length = 0;
    LifecycleListener lifecycleListener;
    private MediaPlayer mediaPlayer;

    private DefaultHttpDataSource.Factory httpDataSourceFactory;
    private DefaultDataSourceFactory defaultDataSourceFactory;
    private CacheDataSource.Factory cacheDataSourceFactory;
    private SimpleExoPlayer simpleExoPlayer;
    private int position;

    public VideoRecyclerAdapter(List<ClipsData> urlList, RecyclerView rcVideo, HomeActivity activity) {
        this.urlList = urlList;
        this.rcVideo = rcVideo;
        this.mContext = activity;
        lifecycleListener = this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(VideoclipViewpagerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.position = position;
        holder.setIsRecyclable(false);
        setLanguage(holder, mContext);

        holder.mBinding.productPrice.setText(urlList.get(position).getPrice());
        holder.mBinding.txtDiscount.setText(urlList.get(position).getDiscount_price());

        holder.mBinding.productName.setText(urlList.get(position).getName());
        holder.mBinding.txtInventoryLocation.setText(urlList.get(position).getInventory_location());
        holder.mBinding.productPrice.setPaintFlags(holder.mBinding.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (urlList.get(position).getIs_show().equals("1")) {
            holder.mBinding.productPrice.setVisibility(View.VISIBLE);
        } else {
            holder.mBinding.productPrice.setVisibility(View.GONE);
        }


        if (urlList.get(position).getImage() != null) {
            BaseActivity.showGlideImageWithError(mContext, mContext.userPreferences.getImageUrl().concat(urlList.get(position).getImage()),
                    holder.mBinding.productImage, ContextCompat.getDrawable(mContext, R.drawable.no_image));
        } else {
            holder.mBinding.profilePicture.setImageResource(R.drawable.no_image);
        }

        if (urlList.get(position).getProfile_pic() != null) {
            BaseActivity.showGlideImageWithError(mContext, mContext.userPreferences.getImageUrl().concat(urlList.get(position).getProfile_pic()),
                    holder.mBinding.profilePicture, ContextCompat.getDrawable(mContext, R.drawable.admin));
        } else {
            holder.mBinding.profilePicture.setImageResource(R.drawable.admin);
        }

        holder.mBinding.transactionViewRight.setVisibility(View.GONE);
        holder.mBinding.transactionViewLeft.setVisibility(View.GONE);



        Utility.PrintLog("LoadedVideo", "url = " + urlList.get(position).getVideo_path() + " position = " + position);

        if (position == 0) {
            if (mContext.checkInternetConnectionWithMessage()) {
                setExoPlayer(holder, urlList, position);
            }
        }

        holder.mBinding.layDetails.setOnClickListener(v -> {

            if (!isSwipeRight) {
                VideoClipBottomDetailsDialog videoClipDitailsDialog = new VideoClipBottomDetailsDialog(urlList.get(position), mContext);
                videoClipDitailsDialog.show(mContext.getSupportFragmentManager(), "VideoClipBottomDetailsDialog");
            }
        });

        holder.mBinding.profilePicture.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, StreamerDetailsActivity.class)
                .putExtra(Constants.INTENT_KEY_USERNAME, urlList.get(position).getUsername()).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)));

    }

    private void setExoPlayer(ViewHolder holder, List<ClipsData> urlList, int position) {

        httpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
        defaultDataSourceFactory = new DefaultDataSourceFactory(mContext, httpDataSourceFactory);

        cacheDataSourceFactory = new CacheDataSource.Factory()
                .setCache(App.simpleCache)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

        // this is it!

        simpleExoPlayer = new SimpleExoPlayer.Builder(mContext)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(cacheDataSourceFactory)).build();

        Uri uri = Uri.parse(mContext.userPreferences.getImageUrl().concat(urlList.get(position).getVideo_path()));
        MediaItem mediaItem = MediaItem.fromUri(uri);
        holder.mBinding.playerView.setPlayer(simpleExoPlayer);
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem);

        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.seekTo(0, 0);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        simpleExoPlayer.setMediaSource(mediaSource, true);
        simpleExoPlayer.prepare();
        simpleExoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlaybackStateChanged(@Player.State int state) {
                if (state == ExoPlayer.STATE_BUFFERING) {
                    holder.mBinding.progressbar.setVisibility(View.VISIBLE);
                } else {
                    holder.mBinding.progressbar.setVisibility(View.GONE);
                }
            }
        });

        /*simpleExoPlayer.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTracksChanged(@NotNull TrackGroupArray trackGroups, @NotNull TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    holder.mBinding.progressbar.setVisibility(View.VISIBLE);
                } else {
                    holder.mBinding.progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlaybackParametersChanged(@NotNull PlaybackParameters playbackParameters) {
            }
        });*/
    }

    private void setLanguage(ViewHolder holder, HomeActivity mContext) {
        holder.mBinding.btnDetails.setText(mContext.language.getLanguage(KEY.details));
    }


    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public void removeTopItem() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.clearVideoSurface();
        }
        urlList.remove(0);
        notifyDataSetChanged();
    }

    public void addTopItem(ClipsData item) {
        urlList.add(0, item);
        notifyDataSetChanged();
    }

    public int gerCurrentEndPosition() {
        return urlList.size();
    }

    public ClipsData getCurrentItem() {
        return urlList.get(0);
    }

    public int getListSize() {
        return urlList.size();
    }

    public void stop() {

        try {

            simpleExoPlayer.stop();

        } catch (Exception e) {
            Utility.PrintLog(VideoClipsFragment.TAG, "stop() Exception = " + e.toString());
        }
    }

    public void pause() {

        try {

            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.getPlaybackState();

        } catch (Exception e) {
            Utility.PrintLog(VideoClipsFragment.TAG, "pause() Exception = " + e.toString());
        }
    }

    public void resume() {

        try {

            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.getPlaybackState();

        } catch (Exception e) {
            Utility.PrintLog(VideoClipsFragment.TAG, "resume() Exception = " + e.getLocalizedMessage());
        }
    }

    public void setVolume(float volume) {
        try {

            if (simpleExoPlayer != null) {
                simpleExoPlayer.setVolume(volume);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transactionRight(float alpha) {
        ViewHolder holder = (ViewHolder) rcVideo.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            holder.mBinding.transactionViewRight.setVisibility(View.VISIBLE);
            holder.mBinding.transactionViewLeft.setVisibility(View.GONE);
            holder.mBinding.transactionViewRight.setAlpha(alpha);
        }

    }

    public void transactionLeft(float alpha) {
        ViewHolder holder = (ViewHolder) rcVideo.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            holder.mBinding.transactionViewLeft.setVisibility(View.VISIBLE);
            holder.mBinding.transactionViewRight.setVisibility(View.GONE);
            holder.mBinding.transactionViewLeft.setAlpha(alpha);
        }
    }

    public void hideTransactionView() {
        ViewHolder holder = (ViewHolder) rcVideo.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            holder.mBinding.transactionViewLeft.setVisibility(View.GONE);
            holder.mBinding.transactionViewRight.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        Utility.PrintLog(VideoClipsFragment.TAG, "Adapter onStart() ");
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    public void reLoad() {
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final VideoclipViewpagerBinding mBinding;

        public ViewHolder(@NonNull VideoclipViewpagerBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }
    }
}
