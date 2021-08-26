package com.kuick.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.UserAddressResponse;
import com.kuick.adapter.AddressInformationAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.callback.SwipeToDeleteCallback;
import com.kuick.databinding.ActivityAddressListBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_ADDRESS_SELECTED;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_ADDRESS_TYPE;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_STREAMER_ID;
import static com.kuick.util.comman.Constants.TRUE;
import static com.kuick.util.comman.Utility.PrintLog;

public class AddressList extends BaseActivity {

    private ActivityAddressListBinding binding;
    private TextView txtTitle;
    private ImageView btnBack;
    private AddressInformationAdapter mAdapter;
    private final String TAG = "AddressList";
    private boolean isFromCart;
    private String addressType = "all";
    private boolean isShopify;
    private String streamerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setToolBar();
        //callAllAddress();
        enableSwipeToDeleteAndUndo();

    }

    private void callAllAddress() {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);
                PrintLog(TAG, " user id " + userPreferences.getUserId());

                Call<CommonResponse> call = apiService.doAllAddressList(
                        userPreferences.getApiKey(),
                        userPreferences.getUserId(),
                        addressType
                );
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        PrintLog(TAG, "response : " + response);

                        checkErrorCode(response.code());

                        if (!response.isSuccessful())
                        {
                            hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null)
                        {
                            if (checkResponseStatusWithMessage(response.body(), true))
                            {
                                setAdapter(response.body());
                                showSnackResponse(language.getLanguage(response.body().getMessage()));

                            } else {
                               // Constants.selectedAddress = null;
                                hideShowView(binding.dataNotFound, language.getLanguage(response.body().getMessage()));
                                //showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                            }
                        }
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        hideLoader();
                    }
                });
            } else {
                hideShowView(binding.dataNotFound, language.getLanguage(KEY.internet_connection_lost_please_retry_again));
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }

    }

    private void setAdapter(CommonResponse commonResponse) {

        List<UserAddressResponse> userAddressList = commonResponse.getUserAddressResponse();

        if (userAddressList != null && userAddressList.size() > 0)
        {
            hideShowView(binding.rvAddressInformation, null);
            binding.rvAddressInformation.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mAdapter = new AddressInformationAdapter(userAddressList, isFromCart, this);
            binding.rvAddressInformation.setAdapter(mAdapter);
        }
    }

    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i)
            {

                final int position = viewHolder.getAdapterPosition();
                UserAddressResponse address = mAdapter.getAddressList().get(position);
                callDeleteAddress(address);
            }

            private void callDeleteAddress(UserAddressResponse address) {
                try {

                    String addressId = address.getId();

                    if (checkInternetConnectionWithMessage())
                    {
                        showLoader(true);

                        Call<CommonResponse> call = apiService.doDeleteAddress(
                                userPreferences.getApiKey(),
                                userPreferences.getUserId(),
                                addressId,
                                addressType
                                );

                        call.enqueue(new Callback<CommonResponse>()
                        {
                            @Override
                            public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response)
                            {

                                checkErrorCode(response.code());

                                if (!response.isSuccessful())
                                {
                                    showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                                    hideLoader();
                                    return;
                                }

                                if (response.body() != null)
                                {

                                    if (checkResponseStatusWithMessage(response.body(), true))
                                    {
                                        List<UserAddressResponse> addressList = response.body().getUserAddressResponse();
                                        if (addressList!=null && addressList.size() > 0)
                                        {
                                            if (mAdapter!=null)
                                            {
                                                mAdapter.DeleteAddress(addressList);
                                                if (Constants.selectedAddress!=null && Constants.selectedAddress.getId().equals(addressId))
                                                {
                                                    Constants.selectedAddress = null;
                                                }
                                            }

                                        }else {
                                            binding.rvAddressInformation.setVisibility(View.GONE);
                                            Constants.selectedAddress = null;
                                        }

                                    } else {

                                        hideShowView(binding.dataNotFound, language.getLanguage(response.body().getMessage()));
                                        Constants.selectedAddress = null;
                                       // showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                                    }
                                }

                                hideLoader();
                            }

                            @Override
                            public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                                PrintLog(TAG, "onFailure : " + t.getMessage());
                                showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                                hideLoader();
                            }
                        });
                    }

                } catch (Exception e) {
                    PrintLog(TAG, "Exception : " + e.toString());
                    showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                    hideLoader();
                }

            }

        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.rvAddressInformation);
    }

    private void setToolBar() {

        if (getIntent() != null && getIntent().getStringExtra(INTENT_KEY_IS_ADDRESS_SELECTED) != null &&
            getIntent().getStringExtra(INTENT_KEY_IS_ADDRESS_SELECTED).equals(TRUE))
        {
            isFromCart = true;
            Utility.PrintLog(TAG,"AddressListActivity shopify");
        }

        if (getIntent() != null && getIntent().getStringExtra(INTENT_KEY_IS_ADDRESS_TYPE) != null){
            if (getIntent().getStringExtra(INTENT_KEY_IS_ADDRESS_TYPE).equals("shopify")){
                isShopify = true;
                streamerId = getIntent().getStringExtra(INTENT_KEY_IS_STREAMER_ID);
            }
            addressType = getIntent().getStringExtra(INTENT_KEY_IS_ADDRESS_TYPE);
        }

        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        binding.getRoot().findViewById(R.id.btnAddAddress).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.btnTryAgain).setOnClickListener(this);
        btnBack.setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.btnTryAgain).setVisibility(View.GONE);
        btnBack.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

        try {

            if (language!=null)
            {
                txtTitle.setText(language.getLanguage(KEY.address_information));
                binding.btnAddAddress.setText(language.getLanguage(KEY.add_address));
            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"exception : " + e.toString());
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.btnAddAddress:
                Intent intent = new Intent(this,AddressInformation.class);
                if (isShopify){
                    intent.putExtra(INTENT_KEY_IS_ADDRESS_TYPE,isShopify);
                    intent.putExtra(INTENT_KEY_IS_STREAMER_ID,streamerId);
                }
                startActivity(intent);

                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
            case R.id.btnTryAgain:
                binding.dataNotFound.setVisibility(View.GONE);
                callAllAddress();
                break;
        }
    }

    public void hideShowView(View view, String message)
    {
        binding.dataNotFound.setVisibility(View.GONE);
        binding.rvAddressInformation.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        error.setText(message);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

        callAllAddress();
    }
}