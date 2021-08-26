package com.kuick.activity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.NotificationResponse;
import com.kuick.adapter.NotificationAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.FragmentNotificationBinding;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Utility.goToNextScreen;

public class NotificationActivity extends BaseActivity  {


    private static final String TAG = "NotificationActivity";
    private FragmentNotificationBinding binding;
    private TextView txtTitle;
    private ImageView imgCart;
    private TextView btnTryAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        callNotification();
        setLanguageLable();
        //setAdapter();

        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() -> {
            hideShowView(binding.dataNotFound,"");
            binding.getRoot().findViewById(R.id.btnTryAgain).setVisibility(View.GONE);
            callNotification();
        });
    }

    private void setLanguageLable() {

        try{

            if (language!=null){

                txtTitle.setText(language.getLanguage(KEY.notifications));
                btnTryAgain.setText(language.getLanguage(KEY.discover_the_best_lives));
            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }
    }

    private void setToolBar() {
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        imgCart = binding.getRoot().findViewById(R.id.img_cart);
        btnTryAgain =  binding.getRoot().findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(this);
        imgCart.setOnClickListener(this);
        ImageView btnBack = binding.getRoot().findViewById(R.id.img_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);


        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    private void callNotification() {

        try {

            if (checkInternetConnectionWithMessage()) {
                if (!binding.swiperefresh.isRefreshing()){
                    showLoader(true);
                }

                Call<NotificationResponse> call = apiService.doNotification(userPreferences.getApiKey(),userPreferences.getUserId());
                call.enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<NotificationResponse> call, @NotNull Response<NotificationResponse> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());

                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true)) {
                                hideShowView(binding.swiperefresh, null);
                                setResponseData(response.body());
                            }else {
                                hideShowView(binding.dataNotFound, language.getLanguage(response.body().getMessage()));
                            }
                        }

                        hideLoader();
                        binding.swiperefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NotNull Call<NotificationResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                        hideLoader();
                        binding.swiperefresh.setRefreshing(false);
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
            binding.swiperefresh.setRefreshing(false);
        }
    }

    private void setResponseData(NotificationResponse response) {
        Utility.PrintLog(TAG,"notification response :" + response.getNotification());

        List<NotificationResponse.Notification> notificationList = response.getNotification();

        binding.rvNotification.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.rvNotification.setAdapter(new NotificationAdapter(notificationList));
    }

    public void hideShowView(View view, String message) {
        binding.dataNotFound.setVisibility(View.GONE);
        binding.swiperefresh.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        TextView button = binding.getRoot().findViewById(R.id.btnTryAgain);
        button.setText(language.getLanguage(KEY.discover_the_best_lives));
        error.setText(message);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.img_back:
                onBackPressed();
                break;

            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class,false);
                break;

            case R.id.btnTryAgain:
                if (HomeActivity.homeActivity != null) {
                    HomeActivity.homeActivity.finish();
                }

                isForDiscover = true;
                goToNextScreen(NotificationActivity.this, HomeActivity.class, true);
                break;
        }
    }
}