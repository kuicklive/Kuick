package com.kuick.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.AllStreamerData;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.adapter.NewStreamersFullScreenAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityNewStreamersFullScreenBinding;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.model.NewStreamers;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import java.util.ArrayList;
import java.util.List;


public class NewStreamersFullScreen extends BaseActivity implements ClickEventListener {

    public static ClickEventListener onClickNewStreamerFullScreen;
    public List<NewStreamers> newStreamersListFullScreen;
    private ActivityNewStreamersFullScreenBinding binding;
    private TextView txtTitle;
    private ImageView btnBack, btnCart;
    private final String TAG = "NewStreamersFullScreen";
    private NewStreamersFullScreenAdapter newStreamerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onClickNewStreamerFullScreen = this;
        binding = ActivityNewStreamersFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        setLanguageLable();
        getIntentData();

        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() -> {
            binding.rcMostPopularLive.setVisibility(View.GONE);
            new Handler().postDelayed(() -> {
                binding.swiperefresh.setRefreshing(false);
                binding.rcMostPopularLive.setVisibility(View.VISIBLE);
            }, 1000);

        });


    }

    private void setLanguageLable() {

        try {
            if (language != null) {
                txtTitle.setText(language.getLanguage(KEY.new_streamers));

            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }

    private void getIntentData() {
        showLoader(true);
        Bundle bundle = getIntent().getExtras();
        newStreamersListFullScreen = (List<NewStreamers>) bundle.getSerializable(Constants.INTENT_KEY);

        if (newStreamersListFullScreen != null && newStreamersListFullScreen.size() > 0) {
            binding.rcMostPopularLive.setVisibility(View.VISIBLE);
            binding.rcMostPopularLive.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            newStreamerAdapter = new NewStreamersFullScreenAdapter(newStreamersListFullScreen, this);
            binding.rcMostPopularLive.setAdapter(newStreamerAdapter);
        }

        hideLoader();
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

        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
        }
    }


    @Override
    public void RefreshNewStreamerFullScreenAdapter(List<NewStreamers> newStreamersListFullScreen) {

        if (newStreamerAdapter != null) {
            newStreamerAdapter.refreshAdapter(newStreamersListFullScreen);
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