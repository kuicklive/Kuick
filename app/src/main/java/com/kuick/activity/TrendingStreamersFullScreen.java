package com.kuick.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.adapter.TrendingStreamersFullScreenAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityTrendingStreamersFullScreenBinding;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.model.FeatureStreamers;
import com.kuick.model.NewStreamers;
import com.kuick.model.TrendingStreamers;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import java.util.List;


public class TrendingStreamersFullScreen extends BaseActivity implements ClickEventListener {


    public static ClickEventListener onClickTrendingFullScreen;
    private static final String TAG = "TrendingStreamersFullScreen";
    private ActivityTrendingStreamersFullScreenBinding binding;
    private TextView txtTitle;
    private ImageView btnBack,btnCart;
    public static TrendingStreamersFullScreenAdapter trendingFullScreenAdapter;
    public static List<TrendingStreamers> trendingStreamersListFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onClickTrendingFullScreen = this;
        binding = ActivityTrendingStreamersFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        setLanguageLable();
        getIntentData();

        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() -> {
            binding.rvTrendingStreamerfullscreen.setVisibility(View.GONE);
            new Handler().postDelayed(() -> {
                binding.swiperefresh.setRefreshing(false);
                binding.rvTrendingStreamerfullscreen.setVisibility(View.VISIBLE);
            }, 1000);

        });

    }
    private void setLanguageLable() {

        try {
            if (language!=null ){
                txtTitle.setText(language.getLanguage(KEY.trending_streamers));

            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }
    }

    private void getIntentData() {

        Bundle bundle = getIntent().getExtras();
        trendingStreamersListFullScreen = (List<TrendingStreamers>) bundle.getSerializable(Constants.INTENT_KEY);

        if (trendingStreamersListFullScreen !=null && trendingStreamersListFullScreen.size() > 0){
            binding.rvTrendingStreamerfullscreen.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
            trendingFullScreenAdapter = new TrendingStreamersFullScreenAdapter(trendingStreamersListFullScreen,this);
            binding.rvTrendingStreamerfullscreen.setAdapter(trendingFullScreenAdapter);
        }

    }

    private void setToolBar() {
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
    public void RefreshTrendingFullScreenAdapter(List<TrendingStreamers> trendingStreamersListFullScreen) {

        Utility.PrintLog(TAG,"Refresh Trending() ");
        if (trendingFullScreenAdapter!=null){
            trendingFullScreenAdapter.refreshAdapter(trendingStreamersListFullScreen);
        }
    }

    @Override
    public void MostPopularStreamer(List<MostPopularLivesResponse> mostPopularLivesResponses) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}