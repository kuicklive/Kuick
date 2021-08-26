package com.kuick.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.AllStreamerData;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.adapter.FeaturedStreamersFullScreenAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityFeaturedStreamersFullScreenBinding;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.model.FeatureStreamers;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import java.util.List;


public class FeaturedStreamersFullScreen extends BaseActivity implements ClickEventListener {

    public static ClickEventListener onClickFeturedFullScreen;
    private static final String TAG = "FeaturedStreamersFullScreen";
    private ActivityFeaturedStreamersFullScreenBinding binding;
    private TextView txtTitle;
    private ImageView btnBack,btnCart;
    public static List<FeatureStreamers> featureStreamersListFullScreen;
    private FeaturedStreamersFullScreenAdapter featuredStreamerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onClickFeturedFullScreen = this;
        binding = ActivityFeaturedStreamersFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init()
    {
        setToolBar();
        setLanguageLable();
        getIntentData();
        autoRefresh();
    }

    private void autoRefresh()
    {

        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() ->
        {
            binding.rvFreaturedStreamerfullscreen.setVisibility(View.GONE);
            new Handler().postDelayed(() ->
            {
                binding.swiperefresh.setRefreshing(false);
                binding.rvFreaturedStreamerfullscreen.setVisibility(View.VISIBLE);
            }, 1000);

        });
    }

    private void setLanguageLable() {

        try {

            if (language!=null )
            {
                txtTitle.setText(language.getLanguage(KEY.featured_streamers));
            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }
    }

    private void getIntentData()
    {

        Bundle bundle = getIntent().getExtras();
        featureStreamersListFullScreen = (List<FeatureStreamers>) bundle.getSerializable(Constants.INTENT_KEY);

        if (featureStreamersListFullScreen!=null && featureStreamersListFullScreen.size() > 0)
        {
            binding.rvFreaturedStreamerfullscreen.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
            featuredStreamerAdapter = new FeaturedStreamersFullScreenAdapter(featureStreamersListFullScreen,this);
            binding.rvFreaturedStreamerfullscreen.setAdapter(featuredStreamerAdapter);
        }
    }

    private void setToolBar()
    {

        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        btnCart = binding.getRoot().findViewById(R.id.img_cart);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnCart.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class,false);
                break;
        }
    }

    @Override
    public void RefreshFeaturedFullScreenAdapter(List<FeatureStreamers> featureStreamersListFullScreen) {
        Utility.PrintLog(TAG,"Refresh Trending() ");

        if (featuredStreamerAdapter!=null)
        {
            featuredStreamerAdapter.refreshAdapter(featureStreamersListFullScreen);
        }
    }

    @Override
    public void MostPopularStreamer(List<MostPopularLivesResponse> mostPopularLivesResponses) {

    }

    @Override
    public void StreamerDetailScreenLive(List<AllStreamerData> allStreamerData) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}