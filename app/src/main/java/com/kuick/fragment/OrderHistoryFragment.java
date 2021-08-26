package com.kuick.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.OrderHistoryResponse;
import com.kuick.activity.CartPageActivity;
import com.kuick.adapter.OrdersHistoryAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.OrderHistoryBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Constants.NO_ORDERS;
import static com.kuick.util.comman.Utility.PrintLog;
import static com.kuick.util.comman.Utility.goToNextScreen;

public class OrderHistoryFragment extends BaseFragment{
    private OrderHistoryBinding binding;

    public static OrderHistoryFragment newInstance() {
        Bundle bundle = new Bundle();
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static  String TAG = "OrderHistoryFragment";
    private TextView txtTitle,btnTryAgain;
    ImageView btnCart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setLanguageLable()
    {
        try{

            if (language!=null){

                txtTitle.setText(language.getLanguage(KEY.order_history));
                btnTryAgain.setText(language.getLanguage(KEY.discover_the_best_lives));
                Utility.PrintLog(TAG,"title : " + BaseActivity.baseActivity.language.getLanguage(KEY.order_history));
            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OrderHistoryBinding.inflate(inflater, container, false);
        setToolBar();
        callOrderHistory();
        setLanguageLable();

        binding.swiperefresh.setColorSchemeResources(R.color.bgSplash);
        binding.swiperefresh.setOnRefreshListener(() -> {
            hideShowView(binding.dataNotFound,"");
            binding.getRoot().findViewById(R.id.btnTryAgain).setVisibility(View.GONE);
            callOrderHistory();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
      /*  if (Constants.isRefreshAPI){
            Constants.isRefreshAPI = false;
            callOrderHistory();
        }*/

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    private void callOrderHistory() {

        try {

            if (checkInternetConnectionWithMessage(getContext())) {

                if (!binding.swiperefresh.isRefreshing()){
                    showLoader(true);
                }

                Call<CommonResponse> call = apiService.doOrderHistory(userPreferences.getApiKey(),userPreferences.getUserId());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {

                        checkErrorCode(response.code(),getActivity());

                        if (!response.isSuccessful()) {
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            PrintLog(TAG,"is not successfully : " + userPreferences.getUserId());
                            return;
                        }

                        if (response.body()!=null){

                            if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful()) {
                                hideShowView(binding.swiperefresh,null);
                                binding.swiperefresh.setRefreshing(false);
                                setAdapter(response.body());
                                PrintLog(TAG,"success : " + userPreferences.getUserId());
                            }else
                                hideShowView(binding.dataNotFound,language.getLanguage(response.body().getMessage()));
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                        }
                        binding.swiperefresh.setRefreshing(false);
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideShowView(binding.dataNotFound,language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        binding.swiperefresh.setRefreshing(false);
                        hideLoader();
                    }
                });
            }else {
                hideShowView(binding.dataNotFound,language.getLanguage(KEY.internet_connection_lost_please_retry_again));
                binding.swiperefresh.setRefreshing(false);
                hideLoader();
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            binding.swiperefresh.setRefreshing(false);
            hideShowView(binding.dataNotFound,language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }
    }

    private void setToolBar() {
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnCart = binding.getRoot().findViewById(R.id.img_cart);
        binding.getRoot().findViewById(R.id.btnTryAgain).setOnClickListener(this);
        btnCart.setOnClickListener(this);
        btnTryAgain = binding.getRoot().findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

    }

    private void setAdapter(CommonResponse response) {

        List<OrderHistoryResponse> orderHistoryList = response.getOrderHistoryResponse();
        if (orderHistoryList!=null && orderHistoryList.size() > 0){
            binding.rvOrdersHistory.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            binding.rvOrdersHistory.setAdapter(new OrdersHistoryAdapter(orderHistoryList,homeActivity));

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img_cart:
                goToNextScreen(getActivity(), CartPageActivity.class,false);
                break;
            case R.id.btnTryAgain:
                 homeActivity.onSelectedView(homeActivity.binding.ivFooterDiscover, homeActivity.binding.txtFooterDiscover);
                 homeActivity.showDiscoverFragment();
                break;
        }
    }
    public void hideShowView(View view,String message){
        binding.dataNotFound.setVisibility(View.GONE);
        //binding.rvOrdersHistory.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        TextView button = binding.getRoot().findViewById(R.id.btnTryAgain);
        button.setText(language.getLanguage(KEY.discover_the_best_lives));
        error.setText(message);
    }
}
