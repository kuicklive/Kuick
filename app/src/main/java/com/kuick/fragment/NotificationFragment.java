package com.kuick.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.NotificationResponse;
import com.kuick.activity.CartPageActivity;
import com.kuick.adapter.NotificationAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.FragmentNotificationBinding;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Constants.INTENT_KEY;
import static com.kuick.util.comman.Utility.goToNextScreen;

public class NotificationFragment extends BaseFragment implements View.OnClickListener {

    private FragmentNotificationBinding binding;
    private TextView txtTitle;
    private ImageView imgCart;
    private TextView btnTryAgain;

    public static NotificationFragment newInstance() {
        Bundle bundle = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static  String TAG = "NotificationFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
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

        return binding.getRoot();
    }

    private void callNotification() {

        try {

            if (checkInternetConnectionWithMessage(getContext())) {
                if (!binding.swiperefresh.isRefreshing()){
                    showLoader(true);
                }

                Call<NotificationResponse> call = apiService.doNotification(userPreferences.getApiKey(),userPreferences.getUserId());
                call.enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<NotificationResponse> call, @NotNull Response<NotificationResponse> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code(),getActivity());

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

        binding.rvNotification.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        binding.rvNotification.setAdapter(new NotificationAdapter(notificationList));
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setToolBar() {
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        imgCart = binding.getRoot().findViewById(R.id.img_cart);
        btnTryAgain =  binding.getRoot().findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(this);
        imgCart.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
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

    public void hideShowView(View view, String message) {
        binding.dataNotFound.setVisibility(View.GONE);
        binding.swiperefresh.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        TextView button = binding.getRoot().findViewById(R.id.btnTryAgain);
        button.setText(language.getLanguage(KEY.discover_the_best_lives));
        error.setText(message);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}
