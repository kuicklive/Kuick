package com.kuick.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kuick.R;
import com.kuick.Response.DashBoardResponse;
import com.kuick.activity.CartPageActivity;
import com.kuick.activity.FeaturedStreamersFullScreen;
import com.kuick.activity.NewStreamersFullScreen;
import com.kuick.activity.TopCategoriesFullScreen;
import com.kuick.activity.TrendingStreamersFullScreen;
import com.kuick.adapter.FeaturedStreamersDashboardAdapter;
import com.kuick.adapter.LiveBannerPagerAdapter;
import com.kuick.adapter.LiveStreamersAdapter;
import com.kuick.adapter.NewStreamersDashboardAdapter;
import com.kuick.adapter.TopCategoriesAdapter;
import com.kuick.adapter.TrendingStreamersDashboardAdapter;
import com.kuick.common.Common;
import com.kuick.databinding.FragmentHomeBinding;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.model.BannerDetails;
import com.kuick.model.Categories;
import com.kuick.model.FeatureStreamers;
import com.kuick.model.NewStreamers;
import com.kuick.model.RecommendedStreamers;
import com.kuick.model.TrendingStreamers;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Constants.live_slug_from_notification;
import static com.kuick.util.comman.Utility.PrintLog;
import static com.kuick.util.comman.Utility.goToNextScreen;

public class HomeFragment extends BaseFragment {

    public  static ClickEventListener homeFragmentClickEventListener;
    public static String TAG = "HomeFragment";
    public static FragmentHomeBinding binding;
    private ImageView btnCart;
    private DashBoardResponse dashBoardDataList;
    private  TextView btnTryAgain;
    private PagerAdapter mBannerPagerAdapter;

    public static List<BannerDetails> bannerDetails;
    public static List<FeatureStreamers> featureStreamers;
    public static List<TrendingStreamers> trendingStreamersList;
    public static List<NewStreamers> newStreamers;
    public static List<RecommendedStreamers> recommendedStreamers;
    public static List<Categories> categories;
    public static HomeFragment homeFragment;

    private LiveStreamersAdapter liveStreamerAdapter;
    private NewStreamersDashboardAdapter newStreamerDashboardAdapter;
    private TrendingStreamersDashboardAdapter trendingStreamerAdapter;
    private FeaturedStreamersDashboardAdapter featureStreamerAdapter;
    private TextView btnCartCount;


    public static HomeFragment newInstance() {
        Bundle bundle = new Bundle();
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        hideLoader();
        homeFragmentClickEventListener = this;
        new BaseFragment();
        homeFragment = this;

        setLanguageLable();

        setToolBar();

        hideAllView(true);

        clickListener();

        callDashboardAPI(); // call Dashboard API


        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() -> {
            hideShowView(binding.dataNotFound,"");
            binding.getRoot().findViewById(R.id.btnTryAgain).setVisibility(View.GONE);
            callDashboardAPI();
        });

        return binding.getRoot();
    }


    private void hideAllView(boolean isTrue) {

        if (isTrue) {

            binding.txtFeaturedStreamer.setVisibility(View.GONE);
            binding.btnViewAllFeaturedStreamers.setVisibility(View.GONE);
            binding.txtTopCategories.setVisibility(View.GONE);
            binding.txtTrendingStreamer.setVisibility(View.GONE);
            binding.btnViewAllTreandingStreamers.setVisibility(View.GONE);
            binding.btnViewAllTopCategories.setVisibility(View.GONE);
            binding.txtNewStreamer.setVisibility(View.GONE);
            binding.btnViewAllNewStreamers.setVisibility(View.GONE);
        }else {

            binding.txtFeaturedStreamer.setVisibility(View.VISIBLE);
            binding.btnViewAllFeaturedStreamers.setVisibility(View.VISIBLE);
            binding.btnViewAllTreandingStreamers.setVisibility(View.VISIBLE);
            binding.txtTrendingStreamer.setVisibility(View.VISIBLE);
            binding.txtTopCategories.setVisibility(View.VISIBLE);
            binding.btnViewAllTopCategories.setVisibility(View.VISIBLE);
            binding.txtNewStreamer.setVisibility(View.VISIBLE);
            binding.btnViewAllNewStreamers.setVisibility(View.VISIBLE);

        }
    }

    private void setLanguageLable() {
        try {

            binding.txtFeaturedStreamer.setText(language.getLanguage(KEY.featured_streamers));
            binding.btnViewAllFeaturedStreamers.setText(language.getLanguage(KEY.view_all));
            binding.txtTopCategories.setText(language.getLanguage(KEY.top_categories));
            binding.btnViewAllTopCategories.setText(language.getLanguage(KEY.view_all));
            binding.txtTrendingStreamer.setText(language.getLanguage(KEY.trending_streamers));
            binding.btnViewAllTreandingStreamers.setText(language.getLanguage(KEY.view_all));
            binding.txtNewStreamer.setText(language.getLanguage(KEY.new_streamers));
            binding.btnViewAllNewStreamers.setText(language.getLanguage(KEY.view_all));

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //handler();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    private void callDashboardAPI() {

        try {

            if (checkInternetConnectionWithMessage(getContext()))
            {
                if (!binding.swiperefresh.isRefreshing())
                {
                    showLoader(true);
                }

                Call<DashBoardResponse> call = apiService.doDashboardData(userPreferences.getApiKey(),userPreferences.getUserId());
                call.enqueue(new Callback<DashBoardResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<DashBoardResponse> call, @NotNull Response<DashBoardResponse> response) {
                        PrintLog(TAG, "response : " + response);

                        checkErrorCode(response.code(),getActivity());

                        if (!response.isSuccessful()) {
                            hideLoader();
                            hideShowView(binding.dataNotFound,language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));

                            return;
                        }

                        if (response.body()!=null){

                            if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful()) {
                                hideShowView(binding.dataView,null);
                                setResponseData(response.body());
                                homeActivity.isFromNotification();
                            }else {
                                hideShowView(binding.dataNotFound,language.getLanguage(response.body().getMessage()));
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                            }

                        }
                        hideLoader();
                        binding.swiperefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NotNull Call<DashBoardResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideShowView(binding.dataNotFound,language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        hideLoader();
                        binding.swiperefresh.setRefreshing(false);
                    }
                });
            }else {
                hideShowView(binding.dataNotFound,language.getLanguage(KEY.internet_connection_lost_please_retry_again));
                binding.swiperefresh.setRefreshing(false);
                hideLoader();
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            hideShowView(binding.dataNotFound,language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
            binding.swiperefresh.setRefreshing(false);
        }
    }

    private void setResponseData(DashBoardResponse dashBoardResponse) {
        dashBoardDataList = dashBoardResponse;

        if (dashBoardResponse != null) {

            try {
                userPreferences.setCurrentTime(dashBoardResponse.getCurrent_time());
                Utility.PrintLog(TAG,"currentTimeFromServer dashboard api- " + dashBoardResponse.getCurrent_time());
                if (dashBoardResponse.getCart_count()!=null && !dashBoardResponse.getCart_count().equals("") && !dashBoardResponse.getCart_count().equals("0")){
                    userPreferences.setTotalCartSize(dashBoardResponse.getCart_count());
                }


                TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
                showCartCount(btnCartCount);

                List<Categories> categories = dashBoardResponse.getFeatureCategories();
                setTopCategoriAdapter(categories);

                List<FeatureStreamers> featureStreamers = dashBoardResponse.getFeatured_streamers();
                setFeaturedStreamersAdapter(featureStreamers);

                List<TrendingStreamers> trendingStreamersList = dashBoardResponse.getTrendingStreamers();
                setTrendingStreamers(trendingStreamersList);

                List<NewStreamers> newStreamers = dashBoardResponse.getNewStreamers();
                setNewStreamersAdapter(newStreamers);

                List<BannerDetails> bannerDetails = dashBoardResponse.getBannerDetails();
                Utility.PrintLog(TAG,"userPreferences.getCurrentTime() : " + userPreferences.getCurrentTime());
                setLiveBannerAdapter(bannerDetails,userPreferences.getCurrentTime(),userPreferences.getTimezone());

                List<RecommendedStreamers> recommendedStreamers = dashBoardResponse.getRecommendedStreamers();
                setLiveStreamersAdapter(recommendedStreamers);

                hideAllView(false);
                hideLoader();

                if (homeActivity!=null)
                {
                    homeActivity.connectUser();
                }

                if (Constants.isForLiveStreaming && Constants.liveStreamingSlugFromDeepLinking!=null)
                {
                    Constants.isForLiveStreaming = false;
                    homeActivity.callLiveStreamingAPI(Constants.liveStreamingSlugFromDeepLinking, getContext());
                    Constants.liveStreamingSlugFromDeepLinking = null;
                    return;

                } else if (homeActivity!=null && homeActivity.getIntent()!=null && homeActivity.getIntent().getStringExtra(Constants.INTENT_KEY_STREAM_SLUG)!=null && Common.socket.connected())
                {
                    String liveSlug = homeActivity.getIntent().getStringExtra(Constants.INTENT_KEY_STREAM_SLUG);
                    homeActivity.callLiveStreamingAPI(liveSlug, getContext());
                    return;
                }else {

                    if (homeActivity!=null && live_slug_from_notification!=null)
                    {
                        homeActivity.callLiveStreamingAPI(live_slug_from_notification, getContext());
                        live_slug_from_notification = null;
                        return;
                    }

                    Constants.responseSuccess = true;

                }

            } catch (Exception e) {
                showSnackErrorMessage(e.getMessage());
                hideLoader();
            }

        }else hideLoader();
    }

    private void setFeaturedStreamersAdapter(List<FeatureStreamers> featureStreamers) {
        this.featureStreamers = featureStreamers;
        binding.rvFeaturedStreamers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        featureStreamerAdapter = new FeaturedStreamersDashboardAdapter(featureStreamers);
        binding.rvFeaturedStreamers.setAdapter(featureStreamerAdapter);
    }

    private void clickListener() {
        btnCart.setOnClickListener(this);
        btnTryAgain.setOnClickListener(this);
        binding.vpButtonLeft.setOnClickListener(this);
        binding.vpButtonRight.setOnClickListener(this);
        binding.btnViewAllNewStreamers.setOnClickListener(this);
        binding.btnViewAllTopCategories.setOnClickListener(this);
        binding.btnViewAllFeaturedStreamers.setOnClickListener(this);
        binding.btnViewAllTreandingStreamers.setOnClickListener(this);

    }

    private void setLiveBannerAdapter(List<BannerDetails> bannerDetails, String currentTime, String time_zone) {
        if (time_zone == null || time_zone.equals("")){
            time_zone = Constants.DEFAULT_TIMEZONE;
        }

        this.bannerDetails = bannerDetails;

        binding.vpButtonLeft.setVisibility(View.GONE);
        mBannerPagerAdapter = new LiveBannerPagerAdapter(bannerDetails,currentTime,time_zone);
        binding.vPagerLiveStreamers.setAdapter(mBannerPagerAdapter);

        binding.vPagerLiveStreamers.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            final int SCROLLING_RIGHT = 0;
            final int SCROLLING_LEFT = 1;
            final int SCROLLING_UNDETERMINED = 2;

            int currentScrollDirection = 2;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                if (isScrollDirectionUndetermined()) {
                    setScrollingDirection(positionOffset);
                }

                if (isScrollingLeft()) {
                    Utility.PrintLog("ViewPager", "Scrolling LEFT");

                    int tab = binding.vPagerLiveStreamers.getCurrentItem();
                    if (tab == 0) {
                        binding.vpButtonLeft.setVisibility(View.GONE);
                    }
                    binding.vpButtonRight.setVisibility(View.VISIBLE);

                }
                if (isScrollingRight()) {
                    Utility.PrintLog("ViewPager", "Scrolling RIGHT");

                    int tab = binding.vPagerLiveStreamers.getCurrentItem();
                    binding.vpButtonLeft.setVisibility(View.VISIBLE);

                    if (tab == (binding.vPagerLiveStreamers.getAdapter().getCount() - 1))
                        binding.vpButtonRight.setVisibility(View.GONE);
                }

            }

            private void setScrollingDirection(float positionOffset) {
                if ((1 - positionOffset) >= 0.5) {
                    this.currentScrollDirection = SCROLLING_RIGHT;
                } else if ((1 - positionOffset) <= 0.5) {
                    this.currentScrollDirection = SCROLLING_LEFT;
                }
            }

            private boolean isScrollDirectionUndetermined() {
                return currentScrollDirection == SCROLLING_UNDETERMINED;
            }

            private boolean isScrollingRight() {
                return currentScrollDirection == SCROLLING_RIGHT;
            }

            private boolean isScrollingLeft() {
                return currentScrollDirection == SCROLLING_LEFT;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    this.currentScrollDirection = SCROLLING_UNDETERMINED;
                }
            }
        });
    }

    private void setNewStreamersAdapter(List<NewStreamers> newStreamers) {
        this.newStreamers = newStreamers;
        binding.rvNewStreamers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        newStreamerDashboardAdapter = new NewStreamersDashboardAdapter(newStreamers);
        binding.rvNewStreamers.setAdapter(newStreamerDashboardAdapter);
    }

    private void setTrendingStreamers(List<TrendingStreamers> trendingStreamersList) {
        this.trendingStreamersList = trendingStreamersList;
        binding.rvTreandingStreamers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        trendingStreamerAdapter = new TrendingStreamersDashboardAdapter(trendingStreamersList);
        binding.rvTreandingStreamers.setAdapter(trendingStreamerAdapter);
    }

    private void setTopCategoriAdapter(List<Categories> categories) {
        this.categories = categories;
        binding.rvTopCategories.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvTopCategories.setAdapter(new TopCategoriesAdapter(categories, false));
    }

    private void setLiveStreamersAdapter(List<RecommendedStreamers> recommendedStreamers) {

            this.recommendedStreamers = recommendedStreamers;
            binding.rvLiveStreamer.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            liveStreamerAdapter = new LiveStreamersAdapter(recommendedStreamers);
            binding.rvLiveStreamer.setAdapter(liveStreamerAdapter);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setToolBar() {
        binding.getRoot().findViewById(R.id.txtTitle).setVisibility(View.GONE);
        btnCart = binding.getRoot().findViewById(R.id.img_cart);
        btnTryAgain = binding.getRoot().findViewById(R.id.btnTryAgain);

        btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();

        switch (v.getId()) {

            case R.id.btnViewAllTopCategories:
                if (dashBoardDataList == null){
                    startActivity(new Intent(getContext(), TopCategoriesFullScreen.class).putExtras(bundle));
                    return;
                }
                bundle.putSerializable(Constants.INTENT_KEY, (Serializable) categories);
                startActivity(new Intent(getContext(), TopCategoriesFullScreen.class).putExtras(bundle));
                break;

            case R.id.btnViewAllTreandingStreamers:
                bundle = new Bundle();

                if (dashBoardDataList == null){
                    startActivity(new Intent(getContext(), TrendingStreamersFullScreen.class).putExtras(bundle));
                    return;
                }
                bundle.putSerializable(Constants.INTENT_KEY, (Serializable) trendingStreamersList);
                startActivity(new Intent(getContext(), TrendingStreamersFullScreen.class).putExtras(bundle));
                break;

            case R.id.btnViewAllNewStreamers:
                bundle = new Bundle();
                if (dashBoardDataList == null){
                    startActivity(new Intent(getContext(), NewStreamersFullScreen.class).putExtras(bundle));
                    return;
                }
                bundle.putSerializable(Constants.INTENT_KEY, (Serializable) newStreamers);
                startActivity(new Intent(getContext(), NewStreamersFullScreen.class).putExtras(bundle));
                break;

            case R.id.btnViewAllFeaturedStreamers:
                bundle = new Bundle();

                if (dashBoardDataList == null){
                    startActivity(new Intent(getContext(), FeaturedStreamersFullScreen.class).putExtras(bundle));
                    return;
                }
                bundle.putSerializable(Constants.INTENT_KEY, (Serializable) featureStreamers);
                startActivity(new Intent(getContext(), FeaturedStreamersFullScreen.class).putExtras(bundle));

                break;
            case R.id.cartCount:
            case R.id.img_cart:
                goToNextScreen(getActivity(), CartPageActivity.class, false);
                break;
            case R.id.vpButtonLeft:

                buttonLeftSwipe();

                break;
            case R.id.vpButtonRight:

                buttonRightSwipe();

                break;
            case R.id.btnTryAgain:
                 callDashboardAPI();
                break;
        }
    }

    private void buttonRightSwipe() {

        int tab = binding.vPagerLiveStreamers.getCurrentItem();
        tab++;
        binding.vPagerLiveStreamers.setCurrentItem(tab);
        binding.vpButtonLeft.setVisibility(View.VISIBLE);

        if (tab == (binding.vPagerLiveStreamers.getAdapter().getCount() - 1))
            binding.vpButtonRight.setVisibility(View.GONE);
    }

    private void buttonLeftSwipe() {

        int tab = binding.vPagerLiveStreamers.getCurrentItem();
        if (tab > 0) {
            tab--;
            binding.vPagerLiveStreamers.setCurrentItem(tab);
        } else if (tab == 0) {
            binding.vPagerLiveStreamers.setCurrentItem(tab);
        }
        if (tab == 0) {
            binding.vpButtonLeft.setVisibility(View.GONE);
        }
        binding.vpButtonRight.setVisibility(View.VISIBLE);
    }

    public void hideShowView(View view,String message){
        binding.dataNotFound.setVisibility(View.GONE);
        binding.dataView.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        btnTryAgain = binding.getRoot().findViewById(R.id.btnTryAgain);
        btnTryAgain.setVisibility(View.VISIBLE);
        btnTryAgain.setOnClickListener(this);
        error.setText(message);
    }



    @Override
    public void RefreshAllAdapter() {
        if (liveStreamerAdapter!=null){
            liveStreamerAdapter.notifyDataSetChanged();
        }

        if (newStreamerDashboardAdapter!=null ){
            newStreamerDashboardAdapter.notifyDataSetChanged();
        }

        if (trendingStreamerAdapter!=null){
            trendingStreamerAdapter.notifyDataSetChanged();
        }

        if (featureStreamerAdapter!=null){
            featureStreamerAdapter.notifyDataSetChanged();
        }

        if (mBannerPagerAdapter!=null){
            mBannerPagerAdapter.notifyDataSetChanged();
        }

    }

    public void refreshHomeFragment()
    {

            if (homeActivity!=null && homeActivity.getIntent()!=null && homeActivity.getIntent().getStringExtra(Constants.INTENT_KEY_STREAM_SLUG)!=null && Common.socket.connected())
            {
                if (homeActivity!=null && !Common.socket.connected())
                {
                    homeActivity.connectUser();
                }

                String liveSlug = homeActivity.getIntent().getStringExtra(Constants.INTENT_KEY_STREAM_SLUG);
                homeActivity.callLiveStreamingAPI(liveSlug, getContext());
                return;
            }else {

                if (homeActivity!=null && live_slug_from_notification!=null)
                {
                    if (homeActivity!=null && !Common.socket.connected())
                    {
                        homeActivity.connectUser();
                    }

                    homeActivity.callLiveStreamingAPI(live_slug_from_notification, getContext());
                    live_slug_from_notification = null;
                    return;
                }

            }
    }


}
