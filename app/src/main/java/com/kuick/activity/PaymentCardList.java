package com.kuick.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.adapter.AddPaymentCardAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.callback.SwipeToDeleteCallback;
import com.kuick.databinding.ActivityPaymentAddCardBinding;
import com.kuick.interfaces.PaymentInformationListener;
import com.kuick.model.UserCard;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.util.comman.Constants.INTENT_KEY_IS_PAYMENT_SELECTED;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_SHOPIFY;
import static com.kuick.util.comman.Constants.TRUE;
import static com.kuick.util.comman.Constants.selectedCardPosition;
import static com.kuick.util.comman.Constants.selectedPaymentCard;
import static com.kuick.util.comman.CurrencyCode.ARS;
import static com.kuick.util.comman.CurrencyCode.CLP;
import static com.kuick.util.comman.CurrencyCode.PaypalAllowedCountry;
import static com.kuick.util.comman.Utility.PrintLog;

public class PaymentCardList extends BaseActivity implements PaymentInformationListener {

    public static PaymentInformationListener paymentInformationListener;
    String TAG = "AddPaymentCardAdapter";
    private ActivityPaymentAddCardBinding binding;
    private TextView txtTitle;
    private ImageView btnBack;
    private AddPaymentCardAdapter mAdapter;
    private boolean isFromCart;
    private boolean isShopify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentAddCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {

        setToolBar();
        setLanguageLable();
        getIntentData();
        enableSwipeToDeleteAndUndo();
        setOnClickListener();

        if (Constants.isPaypalSelected) {
            onSelectPaymentMethod(binding.layPaypal);

        } else if (Constants.isPagofacilSelected) {
            onSelectPaymentMethod(binding.layPogofacil);

        }/* else if (Constants.isdLocalSelected) {
            onSelectPaymentMethod(binding.laydLocal);
        } */ else if (Constants.isMercadoPagoSelected) {
            onSelectPaymentMethod(binding.layMarcadoPago);
        }

    }

    private void getIntentData() {

        if (getIntent() != null && getIntent().getStringExtra(INTENT_KEY_IS_PAYMENT_SELECTED) != null &&
                getIntent().getStringExtra(INTENT_KEY_IS_PAYMENT_SELECTED).equals(TRUE)) {
            isFromCart = true;

            if (getIntent().getBooleanExtra(INTENT_KEY_IS_SHOPIFY, false)) {
                isShopify = true;
            }
        }
    }

    private void getPaypalVisibility() {

        if (userPreferences != null && userPreferences.getCurrencyCode() != null && !isShopify) {
            for (int i = 0; i < PaypalAllowedCountry.length; i++) {

                String currencyCode = PaypalAllowedCountry[i];
                if (currencyCode.equals(userPreferences.getCurrencyCode())) {
                    binding.layPaypal.setVisibility(View.VISIBLE);
                    binding.curentMethod.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void setLanguageLable() {

        try {
            if (language != null) {
                txtTitle.setText(language.getLanguage(KEY.payment_information));
                binding.curentMethod.setText(language.getLanguage(KEY.current_method));
                binding.paypalDefaultMethod.setText(language.getLanguage(KEY.default_method));
                //binding.pogofacilDefaultMethod.setText(language.getLanguage(KEY.default_method));
                binding.btnAddCard.setText(language.getLanguage(KEY.add_card));
                binding.txtMethodName.setText(language.getLanguage(KEY.redirect));
                binding.pogofacilDefaultMethod.setText(language.getLanguage(KEY.redirect));
                binding.dLocalDefaultMethodChile.setText(language.getLanguage(KEY.default_method));
                binding.cardNumberDlocal.setText(language.getLanguage(KEY.new_creditdebit_card));
                binding.cardNumberDlocalChile.setText(language.getLanguage(KEY.new_creditdebit_card));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }

    }

    private void callGetPaymentCardApi() {
        try {

            Log.e("userPreferences.getCurrencyCode()", "userPreferences.getCurrencyCode() => " + userPreferences.getCurrencyCode());
            if (userPreferences != null && userPreferences.getCurrencyCode() != null && !isShopify) {
                if (userPreferences.getCurrencyCode().equals(CLP)) {
                    // Constants.isdLocalSelected = true;
                    binding.rvPaymentAddCard.setVisibility(View.GONE);
                    binding.layPaypal.setVisibility(View.GONE);
                    binding.btnAddCard.setVisibility(View.GONE);
                    binding.curentMethod.setVisibility(View.VISIBLE);
                    binding.laydLocal.setVisibility(View.GONE);
                    binding.layPogofacil.setVisibility(View.VISIBLE);
                    binding.layMarcadoPago.setVisibility(View.VISIBLE);
                    binding.layoutdLocalChile.setVisibility(View.VISIBLE);
                    //  return;

                } else if (userPreferences.getCurrencyCode().equals(ARS)) {
                    // Constants.isdLocalSelected = true;
                    binding.rvPaymentAddCard.setVisibility(View.GONE);
                    binding.layPaypal.setVisibility(View.GONE);
                    binding.btnAddCard.setVisibility(View.GONE);
                    binding.curentMethod.setVisibility(View.VISIBLE);
                    binding.layPogofacil.setVisibility(View.GONE);
                    binding.laydLocal.setVisibility(View.VISIBLE);
                    binding.layoutdLocalChile.setVisibility(View.GONE);
                    //  return;

                }
            }


            if (checkInternetConnectionWithMessage()) {

                showLoader(true);
                Call<CommonResponse> call = apiService.doAllCardsList(userPreferences.getApiKey(), userPreferences.getUserId());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        PrintLog(TAG, "response : " + response);

                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }
                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful()) {
                                setAdapter(response.body());
                            } else
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                        }
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            hideLoader();
        }
    }

    private void setOnClickListener() {
        btnBack.setOnClickListener(this);
        binding.btnAddCard.setOnClickListener(this);
    }

    private void setAdapter(CommonResponse response) {

        List<UserCard> cardList = response.getUserCard();
        binding.rvPaymentAddCard.setVisibility(View.VISIBLE);
        binding.rvPaymentAddCard.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new AddPaymentCardAdapter(cardList, this, isFromCart);
        binding.rvPaymentAddCard.setAdapter(mAdapter);

    }

    private void callDeleteAddress(UserCard card) {
        try {

            String usercard = card.getId();

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<CommonResponse> call = apiService.doDeleteCard(
                        userPreferences.getApiKey(),
                        userPreferences.getUserId(),
                        usercard);

                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {

                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true)) {
                                List<UserCard> userCard = response.body().getUserCard();
                                if (userCard != null && userCard.size() > 0) {
                                    if (mAdapter != null) {
                                        mAdapter.DeleteCard(userCard);
                                        if (selectedPaymentCard != null && selectedPaymentCard.getId().equals(usercard)) {
                                            selectedPaymentCard = null;
                                        }
                                    }
                                } else {
                                    binding.rvPaymentAddCard.setVisibility(View.GONE);
                                    selectedPaymentCard = null;
                                }

                                //onRemoveFromList(viewHolder);
                            } else {

                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
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


    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final UserCard userCard = mAdapter.getData().get(position);
                callDeleteAddress(userCard);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.rvPaymentAddCard);
    }

    private void setToolBar() {


        paymentInformationListener = this;
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);
        btnBack.setVisibility(View.VISIBLE);
        binding.layPaypal.setOnClickListener(this);
        binding.layPogofacil.setOnClickListener(this);
        binding.laydLocal.setOnClickListener(this);
        binding.layoutdLocalChile.setOnClickListener(this);
        binding.layMarcadoPago.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.btnAddCard:
                //if (isShopify) {
                    goToNextScreen(this, AddPaymentCard.class, false);
                /*} else {
                    goToNextScreenWithParam(this, AddPaymentCard.class, false, "isDLocal", "1");
                }*/
                break;
            case R.id.layPaypal:
                onSelectPaymentMethod(binding.layPaypal);

                if (isFromCart) {
                    removeAllOthers();
                    Constants.isPaypalSelected = true;
                    onBackPressed();
                }
                break;
            case R.id.layMarcadoPago:

                binding.layPogofacil.setBackgroundColor(getResources().getColor(R.color.white));
                onSelectPaymentMethod(binding.layMarcadoPago);

                if (isFromCart) {
                    removeAllOthers();
                    Constants.isMercadoPagoSelected = true;
                    onBackPressed();
                }

                break;
            case R.id.layPogofacil:
                binding.layMarcadoPago.setBackgroundColor(getResources().getColor(R.color.white));
                onSelectPaymentMethod(binding.layPogofacil);

                if (isFromCart) {
                    removeAllOthers();
                    Constants.isPagofacilSelected = true;
                    onBackPressed();
                }
                break;
            case R.id.laydLocal:
                goToNextScreenWithParam(this, AddPaymentCard.class, false, "isDLocal", "1");
                // Constants.isdLocalSelected = true;
               /* onSelectPaymentMethod(binding.laydLocal);

                if (isFromCart) {
                    removeAllOthers();
                    Constants.isdLocalSelected = true;
                    onBackPressed();
                }*/

                break;
            case R.id.layoutdLocalChile:
                goToNextScreenWithParam(this, AddPaymentCard.class, false, "isDLocal", "1");
                //  Constants.isdLocalSelected = true;
               /* onSelectPaymentMethod(binding.laydLocal);

                if (isFromCart) {
                    removeAllOthers();
                    Constants.isdLocalSelected = true;
                    onBackPressed();
                }*/

                break;

            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
        }
    }

    private void removeAllOthers() {
        Constants.isPaypalSelected = false;
        Constants.isMercadoPagoSelected = false;
        Constants.isPagofacilSelected = false;
        Constants.isdLocalSelected = false;
    }

    private void onSelectPaymentMethod(RelativeLayout view) {

        selectedPaymentCard = null;
        selectedCardPosition = -1;
        //binding.cbPaypal.setChecked(true);
        view.setBackground(getResources().getDrawable(R.drawable.blueline));
        if (mAdapter != null) {
            mAdapter.refreshAdapter();
        }
    }

    @Override
    public void onClickPaypalCheckBox() {

    }

    @Override
    public void onClickCardCheckBox(UserCard card, int position) {
        selectedCardPosition = position;
        selectedPaymentCard = card;
        removeAllOthers();
        binding.layPaypal.setBackgroundColor(getResources().getColor(R.color.white));
        binding.cbPaypal.setChecked(false);
    }

    @Override
    public void onAddPaymentCard(String name, String cardNumber, String expiry) {
        mAdapter.AddCard();
    }


    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

        callGetPaymentCardApi();
        getPaypalVisibility();
    }
}