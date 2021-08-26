package com.kuick.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuick.R;
import com.kuick.Response.ClipsData;
import com.kuick.Response.CommonResponse;
import com.kuick.activity.CartPageActivity;
import com.kuick.activity.HomeActivity;
import com.kuick.activity.ProductDetailsActivity;
import com.kuick.adapter.VideoRecyclerAdapter;
import com.kuick.common.Common;
import com.kuick.databinding.FragmentVideoClipsBinding;
import com.kuick.interfaces.MediaPlayer;
import com.kuick.util.comman.CommanDialogs;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.video_clip_animation.layoutmanager.OnItemSwiped;
import com.kuick.video_clip_animation.layoutmanager.SwipeableLayoutManager;
import com.kuick.video_clip_animation.layoutmanager.SwipeableTouchHelperCallback;
import com.kuick.video_clip_animation.layoutmanager.touchelper.ItemTouchHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.ENDPOINT_CLIP_ID;
import static com.kuick.Remote.EndPoints.ENDPOINT_IS_CART;
import static com.kuick.Remote.EndPoints.ENDPOINT_IS_DISLIKE;
import static com.kuick.Remote.EndPoints.ENDPOINT_PRODUCT_VARIANT;
import static com.kuick.Remote.EndPoints.ENDPOINT_QUANTITY;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_SIZE_ID;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Constants.live_slug_from_notification;
import static com.kuick.video_clip_animation.layoutmanager.touchelper.ItemTouchHelper.LEFT;
import static com.kuick.video_clip_animation.layoutmanager.touchelper.ItemTouchHelper.RIGHT;

public class VideoClipsFragment extends BaseFragment implements View.OnClickListener, MediaPlayer {

    public static String TAG = "VideoClipsFragment";
    public static MediaPlayer mediaPlayer;
    private final List<ClipsData> removedList = new ArrayList<>();
    public ArrayList<String> urlList = new ArrayList<>();
    String isCart = "0", isDislike = "0";
    private FragmentVideoClipsBinding binding;
    private TextView btnTryAgain;
    private VideoRecyclerAdapter mVideoClipAdapter;
    private ItemTouchHelper itemTouchHelper;
    private boolean isPause = false;
    private List<ClipsData> clipData;
    private SwipeableTouchHelperCallback swipeableTouchHelperCallback;
    public static VideoClipsFragment currentFragment;
    public static Boolean isFirstTimeOpened;
    public static Boolean isSwipeRight = false;
    public Handler handler = new Handler();
    public Runnable runnable = null;

    public static VideoClipsFragment newInstance() {
        Bundle bundle = new Bundle();
        VideoClipsFragment fragment = new VideoClipsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVideoClipsBinding.inflate(inflater, container, false);
        init();
        setLanguageLable();
        callVideoClipAPI();
        setSocketData();


        return binding.getRoot();
    }



    private void setSocketData() {

        if (homeActivity != null) {
            homeActivity.connectUser();
        }

        if (Constants.isForLiveStreaming && Constants.liveStreamingSlugFromDeepLinking != null) {
            Constants.isForLiveStreaming = false;
            if (homeActivity != null) {
                homeActivity.callLiveStreamingAPI(Constants.liveStreamingSlugFromDeepLinking, getContext());
            }
            Constants.liveStreamingSlugFromDeepLinking = null;
            return;

        } else if (homeActivity != null && homeActivity.getIntent() != null && homeActivity.getIntent().getStringExtra(Constants.INTENT_KEY_STREAM_SLUG) != null && Common.socket.connected()) {
            String liveSlug = homeActivity.getIntent().getStringExtra(Constants.INTENT_KEY_STREAM_SLUG);
            homeActivity.callLiveStreamingAPI(liveSlug, getContext());
            return;
        } else {

            if (homeActivity != null && live_slug_from_notification != null) {
                homeActivity.callLiveStreamingAPI(live_slug_from_notification, getContext());
                live_slug_from_notification = null;
                return;
            }

            Constants.responseSuccess = true;

        }
    }

    private void cartORdislikeAIP(String isCart, String isDislike, ClipsData clipsData) {


        Map<String, String> cartOrDislike = new HashMap<>();
        cartOrDislike.put(PARAM_USER_ID, userPreferences.getUserId());
        cartOrDislike.put(ENDPOINT_CLIP_ID, clipsData.getId());
        cartOrDislike.put(ENDPOINT_PRODUCT_VARIANT, clipsData.getProduct_variant());
        cartOrDislike.put(ENDPOINT_IS_CART, isCart);
        cartOrDislike.put(ENDPOINT_IS_DISLIKE, isDislike);
        cartOrDislike.put(ENDPOINT_QUANTITY, "1");

        if (clipsData.getSizes() != null && clipsData.getSizes().size() > 0) {
            String id = clipsData.getSizes().get(0).getId();
            cartOrDislike.put(PARAM_PRODUCT_SIZE_ID, id);
        }

        if (checkInternetConnectionWithMessage(getContext())) {


            Call<CommonResponse> call = apiService.doVideoClipCartOrDislike(userPreferences.getApiKey(), cartOrDislike);
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                    Utility.PrintLog(TAG, response.toString());
                    checkErrorCode(response.code(), getActivity());


                    if (checkResponseStatusWithMessage(response.body(), true)) {
                        Utility.PrintLog(TAG, "video add to cart or dislike response successfully");
                        if (response.body() != null) {
                            if (response.body().getCart_count() != null) {
                                userPreferences.setTotalCartSize(response.body().getCart_count());
                                if (isCart.equals("1")) {
                                    startActivity(new Intent(getContext(), CartPageActivity.class));

                                }
                            }
                        }


                    } else {
                        showView(binding.dataNotFound);
                    }

                    hideLoader();
                }

                @Override
                public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                    Utility.PrintLog(TAG, t.toString());
                    showView(binding.dataNotFound);
                    hideLoader();
                }
            });
        }
    }

    private void callVideoClipAPI() {

        if (checkInternetConnectionWithMessage(getContext())) {
            showLoader(true);

            Call<CommonResponse> call = apiService.doGetVideoClips(userPreferences.getApiKey(), userPreferences.getUserId());
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                    Utility.PrintLog(TAG, response.toString());
                    checkErrorCode(response.code(), getActivity());


                    if (checkResponseStatusWithMessage(response.body(), true)) {
                        Utility.PrintLog(TAG, "video clip response successfully");

                        showView(binding.dataView);
                        if (response.body() != null) {
                            clipData = response.body().getClipsData();
                            userPreferences.setBackClicks(response.body().getClicks());
                            userPreferences.setTotalCartSize(response.body().getCart_count());
                            setRecyclerView(clipData);
                        }

                    } else {
                        if (response.body() != null) {
                            setMessage(response.body().getMessage());
                        }
                        showView(binding.dataNotFound);
                    }
                    runnable = () -> hideLoader();
                    handler.postDelayed(runnable, 50);

                }

                @Override
                public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                    Utility.PrintLog(TAG, t.toString());
                    showView(binding.dataNotFound);
                    hideLoader();
                }
            });

        }
    }

    private void setMessage(String message) {
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        error.setText(language.getLanguage(message));
    }

    private void setRecyclerView(List<ClipsData> clipData) {

        mVideoClipAdapter = new VideoRecyclerAdapter(clipData, binding.rcVideo, homeActivity);


        swipeableTouchHelperCallback =
                new SwipeableTouchHelperCallback(new OnItemSwiped() {
                    @Override
                    public void onItemSwiped() {
                        Utility.PrintLog(TAG, "onItemSwiped");
                    }

                    @Override
                    public void onItemSwipedLeft() {
                        Utility.PrintLog(TAG, "swipe left ");
                        isCart = "0";
                        isDislike = "1";
                        cartORdislikeAIP(isCart, isDislike, mVideoClipAdapter.getCurrentItem());
                        resetNewVideo();

                    }

                    @Override
                    public void onItemSwipedRight() {
                        Utility.PrintLog(TAG, "swipe right ");
                        ClipsData data = mVideoClipAdapter.getCurrentItem();
                        if (data.getSizes()!=null && data.getSizes().size() > 0){
                            reLoadVideo();
                            VideoClipBottomDetailsDialog videoClipDitailsDialog = new VideoClipBottomDetailsDialog(data, homeActivity);
                            videoClipDitailsDialog.show(HomeActivity.homeActivity.getSupportFragmentManager(), "VideoClipBottomDetailsDialog");
                            isSwipeRight = false;
                        }else {

                            isCart = "1";
                            isDislike = "0";
                            isSwipeRight = true;
                            cartORdislikeAIP(isCart, isDislike,data);
                            Utility.vibrate();
                            resetNewVideo();

                        }
                      /*  isCart = "1";
                        isDislike = "0";
                        isSwipeRight = true;
                        cartORdislikeAIP(isCart, isDislike,data);
                        Utility.vibrate();*/

                    }

                    @Override
                    public void onItemSwipedUp() {
                        Utility.PrintLog(TAG, "swipe up ");
                    }

                    @Override
                    public void onItemSwipedDown() {
                        Utility.PrintLog(TAG, "swipe down ");
                    }

                    @Override
                    public void onItemTransactionAnimation(float number) {

                        if (number > 0) {
                            mVideoClipAdapter.transactionRight(Utility.getAlpha(number));
                        } else if (number < 0) {
                            mVideoClipAdapter.transactionLeft(Utility.getAlpha(number));
                        } else {
                            mVideoClipAdapter.hideTransactionView();
                        }
                        Utility.PrintLog(TAG,"onItemTransactionAnimation");

                    }
                });


        itemTouchHelper = new ItemTouchHelper(swipeableTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(binding.rcVideo);

        binding.rcVideo.setLayoutManager(new SwipeableLayoutManager()
                .setAngle(10)
                .setAnimationDuratuion(500)
                .setScaleGap(0.1f)
                .setTransYGap(0));

        binding.rcVideo.setAdapter(mVideoClipAdapter);


    }

    private void reLoadVideo() {
        mVideoClipAdapter.stop();
        mVideoClipAdapter.notifyDataSetChanged();
        binding.btnVolume.setSelected(false);
        binding.btnVolume.setActivated(true);
        mVideoClipAdapter.setVolume(1.0f);
    }

    private void resetNewVideo() {

        removedList.add(mVideoClipAdapter.getCurrentItem());
        mVideoClipAdapter.removeTopItem();
        showView(binding.btnBack);
        binding.btnVolume.setSelected(false);
        binding.btnVolume.setActivated(true);
        mVideoClipAdapter.setVolume(1.0f);


        Utility.PrintLog(TAG, "getItemCount size = " + mVideoClipAdapter.gerCurrentEndPosition());

        if (0 == mVideoClipAdapter.gerCurrentEndPosition()) {
            currentFragment = null;
            removedList.clear();
            mVideoClipAdapter.stop();
            showView(binding.dataNotFound);
            hideView(binding.dataView);
            setMessage(KEY.no_clips_available_at_this_time_please_check_back_in_a_little_while_);
        }


    }

    private void hideViews() {
        hideView(binding.btnDislike);
        hideView(binding.btnCart);
        hideView(binding.layValume);
        hideView(binding.rcVideo);
    }

    private void init() {
        mediaPlayer = this;
        currentFragment= this;
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnCart.setOnClickListener(this);
        binding.btnDislike.setOnClickListener(this);
        binding.layValume.setActivated(true);
        binding.btnVolume.setOnClickListener(this);
        btnTryAgain = binding.getRoot().findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(this);

    }


    private void setLanguageLable() {

        if (language != null) {
            btnTryAgain.setText(language.getLanguage(KEY.discover_the_best_lives));
            setMessage(KEY.no_clips_available_at_this_time_please_check_back_in_a_little_while_);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTryAgain:
                if (mVideoClipAdapter != null) {
                    mVideoClipAdapter.stop();
                }
                homeActivity.binding.llFooterDiscover.performClick();
                break;
            case R.id.btnVolume:
                binding.btnVolume.setSelected(!binding.btnVolume.isSelected());
                v.setActivated(!v.isActivated());
                if (!v.isActivated()) {
                    //mute
                    mVideoClipAdapter.setVolume(0f);
                } else {
                    //unmute
                    mVideoClipAdapter.setVolume(1.0f);
                }

                break;
            case R.id.btnBack:

                /*if(mVideoClipAdapter.getListSize() == 0){
                    addNewData();
                }else {
                    isFromBackButton = true;
                    onClickSwipe(RIGHT);
                }*/

                Utility.disableView(binding.btnBack);
                callClipBackCountAPI();

                break;
            case R.id.btnDislike:
                onClickSwipe(LEFT);
                break;
            case R.id.btnCart:
                if (!isSwipeRight){
                    onClickSwipe(RIGHT);
                }
                isSwipeRight = true;

                break;
            case R.id.img_cart:
                startActivity(new Intent(getContext(), CartPageActivity.class));
                break;
        }
    }

    private void callClipBackCountAPI() {

        if (checkInternetConnectionWithMessage(getContext())) {

            Call<CommonResponse> call = apiService.doBackClick(userPreferences.getApiKey(), userPreferences.getUserId(), userPreferences.getBackClicks());
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                    Utility.PrintLog(TAG, response.toString());
                    checkErrorCode(response.code(), getActivity());


                    if (checkResponseStatusWithMessage(response.body(), true)) {
                        Utility.PrintLog(TAG, "video back click response successfully");
                        if (response.body() != null) {
                            userPreferences.setBackClicks(response.body().getClicks());

                            showViews();
                            mVideoClipAdapter.stop();
                            addNewData();

                        }
                    } else {

                        CommanDialogs.showOneButtonDialog(
                                getContext(),
                                "",
                                language.getLanguage(KEY.you_reached_up_your_daily_limit_of_the_back_button_clicks),
                                language.getLanguage(KEY.ok),
                                (dialog, which) -> dialog.dismiss(),
                                false
                        );
                    }
                    Utility.EnableView(binding.btnBack);
                    hideLoader();
                }

                @Override
                public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                    Utility.EnableView(binding.btnBack);
                    Utility.PrintLog(TAG, t.toString());
                    showView(binding.dataNotFound);
                    hideLoader();
                }
            });

        } else {
            Utility.EnableView(binding.btnBack);
        }

    }

    private void showViews() {
        if (binding.btnDislike.getVisibility() == View.GONE) {
            showView(binding.btnDislike);
            showView(binding.btnCart);
            showView(binding.layValume);
            showView(binding.rcVideo);
        }
    }

    private void addNewData() {
        if (removedList != null && removedList.size() > 0) {

            ClipsData item = removedList.get(removedList.size() - 1);
            mVideoClipAdapter.addTopItem(item);
            removedList.remove(removedList.size() - 1);

            int size = removedList.size();
            if (size == 0) {
                hideView(binding.btnBack);
            }
        }
    }

    private void onClickSwipe(int direction) {

        try {

            VideoRecyclerAdapter.ViewHolder holder = (VideoRecyclerAdapter.ViewHolder) binding.rcVideo.findViewHolderForAdapterPosition(0);
            itemTouchHelper.swipe(holder, direction);
        } catch (Exception e) {
            Utility.PrintLog(TAG, "swipe exception = " + e.toString());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
        isFirstTimeOpened = true;
        if (mVideoClipAdapter != null && mVideoClipAdapter.getListSize() > 0) {
            Utility.PrintLog(TAG, "onPause() VideoClipFragment");
            mVideoClipAdapter.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();



        if (mVideoClipAdapter != null && mVideoClipAdapter.getListSize() > 0 && isPause) {
            Utility.PrintLog(TAG, "onResume() VideoClipFragment");
            isPause = false;
            if (isSwipeRight){
                mVideoClipAdapter.reLoad();
                isSwipeRight = false;

            }else {
                mVideoClipAdapter.resume();
            }

        }

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    @Nullable
    @Override
    public Object getExitTransition() {
        Utility.PrintLog(TAG, "getExitTransition() VideoClipFragment");
        currentFragment = null;
        if (mVideoClipAdapter != null) {
            isFirstTimeOpened = true;
            mVideoClipAdapter.stop();
        }

        return super.getExitTransition();
    }

    @Override
    public void onReleasePlayer() {
        isSwipeRight = true;
        resetNewVideo();
    }

    @Override
    public void onVolumeChange() {

        if (!binding.btnVolume.isActivated()){
            binding.btnVolume.setSelected(true);
            binding.btnVolume.setActivated(false);

            if (mVideoClipAdapter!=null) {
                mVideoClipAdapter.setVolume(0f);
            }
        } else {
            binding.btnVolume.setSelected(false);
            binding.btnVolume.setActivated(true);

            if(mVideoClipAdapter!=null){
                mVideoClipAdapter.setVolume(1.0f);
            }
        }
    }
}
