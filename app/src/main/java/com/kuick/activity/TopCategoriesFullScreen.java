package com.kuick.activity;

import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuick.R;
import com.kuick.Remote.EndPoints;
import com.kuick.Response.CommonResponse;
import com.kuick.adapter.TopCategoriesAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityTopCategoriesFullScreenBinding;
import com.kuick.model.Categories;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TopCategoriesFullScreen extends BaseActivity {

    private ActivityTopCategoriesFullScreenBinding binding;
    private TextView txtTitle;
    private ImageView btnBack,btnCart;
    private String TAG = "TopCategoriesFullScreen";
    public static List<Categories> categoriesList;
    public static TopCategoriesFullScreen topCategoriesFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopCategoriesFullScreenBinding.inflate(getLayoutInflater());
        topCategoriesFullScreen = this;
        setContentView(binding.getRoot());
        setToolBar();
        callTopCategoryAPI();
        setLanguageLable();
        setIntentData();

        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() -> {
            binding.rvTopCategoriesfullscreen.setVisibility(View.GONE);
            callTopCategoryAPI();

        });


    }

    private void callTopCategoryAPI() {

        try {


            if (checkInternetConnectionWithMessage()) {
                if (!binding.swiperefresh.isRefreshing()){
                    showLoader(true);
                }

                Call<CommonResponse> call = apiService.doTopCategoryFullScreen(userPreferences.getApiKey());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());


                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(),true)){
                                Utility.PrintLog(TAG,"top category response : " + response.body());
                                setData(response.body());

                            }
                        }

                        hideLoader();
                        binding.swiperefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                        hideLoader();
                        binding.swiperefresh.setRefreshing(false);
                    }
                });
            }

        } catch (Exception e) {
            binding.swiperefresh.setRefreshing(false);
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }

    }

    private void setData(CommonResponse response) {

        if (response!=null && response.getCategories()!=null && response.getCategories().size() > 0){
            this.categoriesList = response.getCategories();

            if (categoriesList!=null && categoriesList.size() > 0){
                binding.rvTopCategoriesfullscreen.setVisibility(View.VISIBLE);
                binding.rvTopCategoriesfullscreen.setLayoutManager(new GridLayoutManager(this,3));
                binding.rvTopCategoriesfullscreen.setAdapter(new TopCategoriesAdapter(categoriesList, true));
            }
        }

    }

    private void setLanguageLable() {

        try {
            if (language!=null ){
                Utility.PrintLog(TAG,"title : " + language.getLanguage(KEY.top_categories));
                txtTitle.setText(language.getLanguage(KEY.top_categories));

            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }
    }

    private void setIntentData() {
        /*Bundle bundle = getIntent().getExtras();
        List<Categories> featureCategoriesList = (List<Categories>) bundle.getSerializable(Constants.INTENT_KEY);

        if (featureCategoriesList!=null && featureCategoriesList.size() > 0){
            binding.rvTopCategoriesfullscreen.setLayoutManager(new GridLayoutManager(this,3));
            binding.rvTopCategoriesfullscreen.setAdapter(new TopCategoriesAdapter(featureCategoriesList, true));
        }*/
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
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}