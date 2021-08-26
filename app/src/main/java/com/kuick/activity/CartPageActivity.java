package com.kuick.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kuick.R;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.CartDetails;
import com.kuick.Response.CartOverviewData;
import com.kuick.Response.CommonResponse;
import com.kuick.adapter.ShippingBasketAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.callback.SwipeToDeleteCallback;
import com.kuick.databinding.ActivityCardPageBinding;
import com.kuick.interfaces.PriceCalculationListener;
import com.kuick.model.GetUserCard;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.PARAM_ADDRESS_ID;
import static com.kuick.Remote.EndPoints.PARAM_CARD_ID;
import static com.kuick.Remote.EndPoints.PARAM_DISCOUNT_ID;
import static com.kuick.Remote.EndPoints.PARAM_DOCUMENT_NUMBER;
import static com.kuick.Remote.EndPoints.PARAM_KUICKER_ID;
import static com.kuick.Remote.EndPoints.PARAM_PAYMENT_RESULT;
import static com.kuick.Remote.EndPoints.PARAM_PAYMENT_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_TOKEN;
import static com.kuick.Remote.EndPoints.PARAM_TOTAL_AMOUNT;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.adapter.ShippingBasketAdapter.previousPrice;
import static com.kuick.util.comman.Constants.CARD;
import static com.kuick.util.comman.Constants.DLOCAL;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_ADDRESS_SELECTED;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_ADDRESS_TYPE;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_PAYMENT_SELECTED;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_SHOPIFY;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_STREAMER_ID;
import static com.kuick.util.comman.Constants.MERCADO_PAGO;
import static com.kuick.util.comman.Constants.PAGOFACIL;
import static com.kuick.util.comman.Constants.PAYPAL;
import static com.kuick.util.comman.Constants.REQUEST_FOR_ADDRESS_SELECTION;
import static com.kuick.util.comman.Constants.REQUEST_FOR_PAYMENT_CARD;
import static com.kuick.util.comman.Constants.TRUE;
import static com.kuick.util.comman.Constants.isdLocalSelected;
import static com.kuick.util.comman.Constants.selectedAddress;
import static com.kuick.util.comman.Constants.selectedPaymentCard;
import static com.kuick.util.comman.CurrencyCode.ARS;
import static com.kuick.util.comman.CurrencyCode.CLP;
import static com.kuick.util.comman.Utility.PrintLog;
import static com.kuick.util.comman.Utility.hideKeyboard;

public class CartPageActivity extends BaseActivity implements PriceCalculationListener {

    public static PriceCalculationListener priceCalculationListener;
    public static CartPageActivity cartPageActivity;
    private final String TAG = "CardPageActivity";
    private final double Tax = 0;
    private final double disc = 0.0;
    public Handler handler = new Handler();
    public Runnable runnable = null;
    private ActivityCardPageBinding binding;
    private TextView txtTitle;
    private ImageView btnBack;
    private boolean isPaymentCardSelected;
    private String stripeToken;
    private String addressId;
    private String paymentType = CARD;
    private String cardId = "";
    private List<CartDetails> cartDetails;
    private ShippingBasketAdapter cardItemAdapter;
    private List<CartDetails> cartDetail;
    private double totalDiscount;
    private String discountId;
    private TextView btnTryAgain;
    private String finalTotal;
    private String PAYPAL_RESULT_OBJECT = "";
    private String redirect_url = "", cancel_url = "", success_url = "";
    private String first_order_id = "";
    private String zipCode = null;
    private boolean isShopify = false;
    private String streamerId;
    private String discountArray = "";
    private CartOverviewData cartOverviewDataTemp;
    private String discountStr = "";
    private String finalTotalAfterShipping = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {


        previousPrice = 0;

        selectedAddress = null;
        selectedPaymentCard = null;
        Constants.isPaypalSelected = false;
        Constants.isPagofacilSelected = false;
        Constants.isdLocalSelected = false;
        Constants.isMercadoPagoSelected = false;
        binding.cardImage.setImageBitmap(null);
        setClickListener();
        setToolBar();
        setLanguageLable();
        getSingleCard();
        getCartDetails();
        onTextChange();
        enableSwipeToDeleteAndUndo();

    }

    private void setLanguageLable() {

        try {

            if (language != null) {

                txtTitle.setText(language.getLanguage(KEY.your_shopping_basket));
                binding.txtPromocode.setText(language.getLanguage(KEY.promo_code));
                binding.btnApplyPromoCode.setText(language.getLanguage(KEY.apply));
                binding.edtPromocode.setHint(language.getLanguage(KEY.enter_your_promo_code));
                binding.txtPromoMessage.setText(language.getLanguage(KEY.add_your_promo_code_to_get_more_savings));
                binding.orderOverView.setText(language.getLanguage(KEY.order_overview));
                binding.sutotal.setText(language.getLanguage(KEY.subtotal));
                binding.discount.setText(language.getLanguage(KEY.discount));
                binding.tax.setText(language.getLanguage(KEY.tax));
                binding.shippingCharge.setText(language.getLanguage(KEY.shipping_charge));
                binding.txtTotal.setText(language.getLanguage(KEY.total));
                binding.address.setText(language.getLanguage(KEY.address));
                binding.txtAddress.setText(language.getLanguage(KEY.please_select_address));
                binding.paymentType.setText(language.getLanguage(KEY.payment_type));
                binding.cardNumber.setText(language.getLanguage(KEY.please_select_payment_card));
                binding.btnPlaceOrder.setText(language.getLanguage(KEY.place_order));
                btnTryAgain.setText(language.getLanguage(KEY.discover_the_best_lives));

                binding.errorDocNumber.setText(language.getLanguage(KEY.please_enter_document_number));

                binding.txtDiscount.setText("-" + userPreferences.getCurrencySymbol() + "0.00");
                binding.txtShippingCharge.setText(userPreferences.getCurrencySymbol() + "0.00");
                binding.txtTax.setText(userPreferences.getCurrencySymbol() + "0.00");
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }


    private void CheckShippingChargeAPI(String addressId) {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);


                Utility.PrintLog(TAG, "discountArray = " + discountArray);
                Call<CommonResponse> call = apiService.doCheckoutShippingCharge(userPreferences.getApiKey(), userPreferences.getUserId(), addressId, discountArray);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());
                        if (checkResponseStatusWithMessage(response.body(), true)) {
                            if (response.body() != null) {
                                setCalculation(response.body());
                            }

                            runnable = () -> hideLoader();
                            handler.postDelayed(runnable, 1000);

                        } else {

                            if (response.body() != null) {
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                                hideLoader();
                            }
                        }


                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        hideLoader();
                        Utility.PrintLog(TAG, t.toString());
                    }
                });

            } else {
                hideShowView(binding.dataNotFound, language.getLanguage(KEY.internet_connection_lost_please_retry_again));
                hideLoader();
            }

        } catch (Exception e) {
            hideLoader();
            Utility.PrintLog(TAG, e.toString());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.txtAddress.setEnabled(true);
        binding.paymentType.setEnabled(true);
        binding.layCard.setEnabled(true);
        getSingleCard();
    }

    private void getSingleCard() {
        if (selectedAddress != null) {
            binding.txtAddress.setText(Constants.selectedAddress.getAddress());
            addressId = selectedAddress.getId();
            CheckShippingChargeAPI(addressId);

        } else {
            removeShipping();
        }

        if (selectedPaymentCard != null) {
            isPaymentCardSelected = true;
            binding.cardNumber.setText(selectedPaymentCard.getLast_4());
            binding.cardImage.setVisibility(View.VISIBLE);
            BaseActivity.showGlideImage(this, selectedPaymentCard.getCard_image(), binding.cardImage);
            paymentType = CARD;
            cardId = selectedPaymentCard.getId();

            if (userPreferences != null && userPreferences.getCountry() != null && !userPreferences.getCurrencyCode().equals(CLP) && !userPreferences.getCurrencyCode().equals(ARS)) {
                callSingleCardAPI(selectedPaymentCard.getId());
            }
        } else if (Constants.isPaypalSelected) {
            paymentType = PAYPAL;
            binding.cardImage.setVisibility(View.VISIBLE);
            binding.cardImage.setImageResource(R.drawable.paypal);
            binding.cardNumber.setText(paymentType);

        } else if (Constants.isPagofacilSelected) {
            paymentType = PAGOFACIL;
            binding.cardImage.setVisibility(View.VISIBLE);
            binding.cardImage.setImageResource(R.drawable.pagofacil_icon);
            binding.cardNumber.setText(paymentType);

        } else if (Constants.isdLocalSelected) {

            paymentType = DLOCAL;
            binding.cardImage.setVisibility(View.VISIBLE);
            binding.cardImage.setImageResource(R.drawable.dlocal_icon);
            binding.cardNumber.setText(paymentType);

            if (userPreferences != null && userPreferences.getCountry() != null && userPreferences.getCountry().equals("Argentina")) {
                binding.layDocNumber.setVisibility(View.VISIBLE);
            }
        } else if (Constants.isMercadoPagoSelected) {

            paymentType = MERCADO_PAGO;
            binding.cardImage.setVisibility(View.VISIBLE);
            binding.cardImage.setImageResource(R.drawable.mercadopago_logo);
            binding.cardNumber.setText(paymentType);

        } else {
            removePaymentMethod();
        }

        Utility.PrintLog(TAG, "payment type = " + Constants.isMercadoPagoSelected);
    }

    private void callSingleCardAPI(String card_id) {

        try {

            if (checkInternetConnectionWithMessage()) {

                Call<CommonResponse> call = apiService.doGetSingleCardDetails(userPreferences.getApiKey(), userPreferences.getUserId(), card_id);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            return;
                        }

                        if (response.body() != null) {
                            if (checkResponseStatusWithMessage(response.body(), true)) {
                                if (response.body() != null) {
                                    getTokenId(response.body());
                                }

                            } else {
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                    }
                });
            } else {
                hideShowView(binding.dataNotFound, language.getLanguage(KEY.internet_connection_lost_please_retry_again));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
        }

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();

    }

    private void getTokenId(CommonResponse commonResponse) {
        GetUserCard response = commonResponse.getGetUserCard();

        if (response.getZip_code() != null && !response.getZip_code().equals("")) {
            zipCode = response.getZip_code();
        }

        try {

            final Card card = new Card(
                    response.getCard_number()
                    , Integer.parseInt(response.getExpiry_month())
                    , Integer.parseInt(response.getExpiry_year())
                    , response.getCvv(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    zipCode,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            new Stripe().createToken(card, userPreferences.getStripePublishKey(), new TokenCallback() {
                @Override
                public void onError(Exception error) {
                    PrintLog(TAG, "error : " + error.toString());
                    //showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                }

                @Override
                public void onSuccess(Token token) {
                    PrintLog(TAG, "token : " + token.getId());
                    stripeToken = token.getId();
                }
            });

        } catch (Exception e) {
            PrintLog(TAG, "exception : " + e.toString());
            hideLoader();
        }

    }

    private void onTextChange() {

        binding.edtPromocode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (language != null) {
                    binding.txtPromoMessage.setText(language.getLanguage(KEY.add_your_promo_code_to_get_more_savings));
                    binding.txtPromoMessage.setTextColor(getResources().getColor(R.color.login_text_color));
                }
            }
        });
    }

    private void getCartDetails() {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<CommonResponse> call = apiService.doGetCart(userPreferences.getApiKey(), userPreferences.getUserId());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true)) {
                                hideShowView(binding.dataView, null);
                                setResponseData(response.body());
                                showSnackResponse(language.getLanguage(response.body().getMessage()));

                            } else {

                                hideShowView(binding.dataNotFound, (language.getLanguage(response.body().getMessage())));
                                userPreferences.setTotalCartSize(null);
                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                        hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        hideLoader();
                    }
                });

            } else {
                hideShowView(binding.dataNotFound, language.getLanguage(KEY.internet_connection_lost_please_retry_again));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }
    }

    private void setResponseData(CommonResponse response) {

        if (response != null) {

            String totalCart = response.getCart_count();
            if (totalCart != null && !totalCart.equals("") && !totalCart.equals("0")) {
                userPreferences.setTotalCartSize(totalCart);
            } else {
                userPreferences.setTotalCartSize(null);
            }

            cartDetail = response.getCartDetails();
            setAdapter(cartDetail);

            if (response.getCartOverviewData() != null) {

                setCalculation(response);

                if (response.getCartOverviewData().getProduct_variant().equals("shopify_variant")) {
                    streamerId = response.getCartOverviewData().getStreamer_id();
                    isShopify = true;
                }

                if (response.getCartOverviewData().getMessage() != null && !response.getCartOverviewData().getMessage().equals("")) {
                    showDialogMessage(language.getLanguage(response.getCartOverviewData().getMessage()));

                }
            }
        }
    }

    private void showDialogMessage(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, 0);
        builder.setCancelable(false);
        // builder.setTitle(title);
        String positiveText = language.getLanguage(KEY.ok);

        builder.setPositiveButton(positiveText, null);
        builder.setMessage(message);


        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.bgSplash)));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void setClickListener() {
        cartPageActivity = this;
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        binding.getRoot().findViewById(R.id.img_cart).setVisibility(View.GONE);
        binding.btnApplyPromoCode.setOnClickListener(this);
        binding.btnPlaceOrder.setOnClickListener(this);
        binding.txtAddress.setOnClickListener(this);
        binding.paymentType.setOnClickListener(this);
        binding.address.setOnClickListener(this);
        binding.layCard.setOnClickListener(this);
        binding.promoCancel.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        binding.edtDocumentNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Utility.PrintLog("afterTextChanged", "text = " + editable);

                if (editable == null || (editable != null && editable.toString().equals("")) || TextUtils.isEmpty(editable.toString())) {
                    binding.errorDocNumber.setVisibility(View.VISIBLE);
                } else {
                    binding.errorDocNumber.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setAdapter(List<CartDetails> cartDetails) {
        this.cartDetails = cartDetails;

        if (cartDetails != null && cartDetails.size() > 0) {
            binding.rvProductItem.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            cardItemAdapter = new ShippingBasketAdapter(cartDetails, this);
            binding.rvProductItem.setAdapter(cardItemAdapter);
        }

    }

    private void setToolBar() {

        priceCalculationListener = this;
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        btnTryAgain = binding.getRoot().findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(this);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.promoCancel:
                callRemovePromocode();

                break;
            case R.id.address:
            case R.id.txtAddress:
                binding.txtAddress.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removePaymentMethod();
                        removeDlocal();
                    }
                }, 500);

                Intent intent = new Intent(this, AddressList.class);
                intent.putExtra(INTENT_KEY_IS_ADDRESS_SELECTED, TRUE);
                if (isShopify) {
                    intent.putExtra(INTENT_KEY_IS_ADDRESS_TYPE, "shopify");
                    intent.putExtra(INTENT_KEY_IS_STREAMER_ID, streamerId);
                } else {
                    intent.putExtra(INTENT_KEY_IS_ADDRESS_TYPE, "normal");
                }
                startActivityForResult(intent, REQUEST_FOR_ADDRESS_SELECTION);
                break;
            case R.id.paymentType:
            case R.id.layCard:
                binding.paymentType.setEnabled(false);
                binding.layCard.setEnabled(false);
                Intent intent1 = new Intent(this, PaymentCardList.class);
                intent1.putExtra(INTENT_KEY_IS_PAYMENT_SELECTED, TRUE);
                if (isShopify) {
                    intent1.putExtra(INTENT_KEY_IS_SHOPIFY, true);
                }
                startActivityForResult(intent1, REQUEST_FOR_PAYMENT_CARD);
                break;
            case R.id.btnPlaceOrder:
                onClickPlaceOrder();
                break;
            case R.id.btnApplyPromoCode:
                hideKeyboard(this);
                onClickPromoCode();
                break;
            case R.id.btnTryAgain:

                if (HomeActivity.homeActivity != null) {
                    HomeActivity.homeActivity.finish();
                }

                isForDiscover = true;
                goToNextScreen(CartPageActivity.this, HomeActivity.class, true);
                /*startActivity(new Intent(CartPageActivity.this,HomeActivity.class));
                finish();*/
                break;
        }
    }

    private void callRemovePromocode() {

        if (checkInternetConnectionWithMessage()) {
            showLoader(true);

            Call<CommonResponse> call = apiService.doRemovePromoCode(userPreferences.getApiKey(), userPreferences.getUserId());
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                    Utility.PrintLog(TAG, response.toString());
                    checkErrorCode(response.code());

                    if (response.body() != null) {

                        if (checkResponseStatusWithMessage(response.body(), true)) {
                            removePromocode();
                            discountStr = response.body().getCartOverviewData().getDiscount();
                            finalTotalAfterShipping = response.body().getCartOverviewData().getTotal();

                            setCalculation(response.body());
                        }
                    }

                    hideLoader();
                }

                @Override
                public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                    Utility.PrintLog(TAG, t.toString());
                    hideLoader();
                }
            });

        }
    }


    private void onClickPlaceOrder() {

        if (addressId == null) {
            showSnackErrorMessage(language.getLanguage(KEY.please_select_address));
            return;
        }

        if (!isPaymentCardSelected && !Constants.isPaypalSelected && !Constants.isPagofacilSelected &&
                !Constants.isdLocalSelected && !Constants.isMercadoPagoSelected) {
            showSnackErrorMessage(language.getLanguage(KEY.please_select_payment_card));
            return;
        }

        try {

            if (checkInternetConnectionWithMessage()) {


                Utility.PrintLog(TAG, "final Total Amount : " + finalTotal);

                if (finalTotal == null || finalTotal.equals("")) {
                    showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                    return;
                }

                Map<String, String> placeOrder = new HashMap<>();

                placeOrder.put(PARAM_USER_ID, userPreferences.getUserId());
                placeOrder.put(PARAM_ADDRESS_ID, addressId);

                if (userPreferences.getKuickerId() != null) {
                    placeOrder.put(PARAM_KUICKER_ID, userPreferences.getKuickerId());
                } else {
                    placeOrder.put(PARAM_KUICKER_ID, "0");
                }


                if (discountId != null && !discountId.equals("")) {
                    placeOrder.put(PARAM_DISCOUNT_ID, discountId);
                }


                if (userPreferences != null && userPreferences.getCountry() != null && (userPreferences.getCurrencyCode().equals(CLP) || userPreferences.getCurrencyCode().equals(ARS))) {
                    placeOrder.put(PARAM_PAYMENT_TYPE, "dLocalDirect");
                } else {
                    placeOrder.put(PARAM_PAYMENT_TYPE, paymentType);
                }


                if (paymentType.equals(CARD)) {
                    placeOrder.put(PARAM_CARD_ID, cardId);

                    if (isShopify) {
                        placeOrder.put(PARAM_PAYMENT_TYPE, "ShopifyPayment");
                    } else {

                        if (userPreferences != null && userPreferences.getCountry() != null && !userPreferences.getCurrencyCode().equals(CLP) && !userPreferences.getCurrencyCode().equals(ARS)) {
                            if (stripeToken == null) {
                                return;
                            }

                            placeOrder.put(PARAM_TOKEN, stripeToken);
                        }

                        placeOrder.put(PARAM_TOTAL_AMOUNT, finalTotal);

                    }

                    placeOrder(placeOrder);

                } else if (paymentType.equals(Constants.PAYPAL)) {
                    getPaypalUrlApiCall(placeOrder, finalTotal);

                } else if (paymentType.equalsIgnoreCase(PAGOFACIL)) {
                    placeOrder.put(PARAM_TOTAL_AMOUNT, finalTotal);
                    getPagoFacilUrlApiCall(placeOrder);

                } else if (paymentType.equalsIgnoreCase(DLOCAL)) {
                    getDLocalUrlAPICall(finalTotal);
                } else if (paymentType.equalsIgnoreCase(MERCADO_PAGO)) {
                    placeOrder.put(PARAM_TOTAL_AMOUNT, finalTotal);
                    getMercadoPagoPayment(placeOrder);
                }

                Utility.PrintLog(TAG, "place order parameters = " + placeOrder.toString());

            } else {
                hideShowView(binding.dataNotFound, language.getLanguage(KEY.internet_connection_lost_please_retry_again));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }
    }

    private void getMercadoPagoPayment(Map<String, String> placeOrder) {

        if (checkInternetConnectionWithMessage()) {
            showLoader(true);

            Call<String> call = apiService.doGetMercadoPago(userPreferences.getApiKey(), placeOrder);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    Utility.PrintLog(TAG, response.toString());
                    checkErrorCode(response.code());

                    if (response.body() != null) {
                        if (response.isSuccessful()) {

                            try {

                                Utility.PrintLog(TAG, "success " + response.body());

                                JSONObject jsonObject = new JSONObject(response.body());

                                if (jsonObject.has("status")) {

                                    String status = jsonObject.getString("status");

                                    if (status.equalsIgnoreCase("false")) {
                                        String message = jsonObject.getString("message");
                                        if (language.getLanguage(message) != null && !language.getLanguage(message).equals("")) {
                                            hideLoader();
                                            showSnackErrorMessage(language.getLanguage(message));
                                            return;
                                        }
                                    }

                                }

                                if (jsonObject.has("redirect_url")) {

                                    redirect_url = jsonObject.getString("redirect_url");
                                    success_url = jsonObject.getString("success_url");
                                    cancel_url = jsonObject.getString("cancel_url");
                                    first_order_id = jsonObject.getString("first_order_id");

                                    GetToken getToken = new GetToken(CartPageActivity.this, redirect_url, first_order_id);
                                    getToken.execute();
                                }

                            } catch (Exception e) {
                                Utility.PrintLog(TAG, "Exception() " + response.body());
                            }

                        } else {

                            if (response.message() != null) {
                                showSnackErrorMessage(response.message());
                            }
                        }
                    }

                    hideLoader();
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                    Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                    hideLoader();
                }
            });
        }

    }

    private void getDLocalUrlAPICall(String totalAmount) {


        try {

            String docNumber = binding.edtDocumentNumber.getText().toString();

            if (TextUtils.isEmpty(docNumber)) {
                binding.errorDocNumber.setVisibility(View.VISIBLE);
                return;
            } else {
                binding.errorDocNumber.setVisibility(View.GONE);
            }


            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Map<String, String> dLocal = new HashMap<>();
                dLocal.put(PARAM_USER_ID, userPreferences.getUserId());
                dLocal.put(PARAM_ADDRESS_ID, addressId);
                dLocal.put(PARAM_TOTAL_AMOUNT, totalAmount);
                dLocal.put(PARAM_DOCUMENT_NUMBER, binding.edtDocumentNumber.getText().toString());

                if (userPreferences.getKuickerId() != null) {
                    dLocal.put(PARAM_KUICKER_ID, userPreferences.getKuickerId());
                } else {
                    dLocal.put(PARAM_KUICKER_ID, "0");
                }

                if (discountId != null && !discountId.equals("")) {
                    dLocal.put(PARAM_DISCOUNT_ID, discountId);
                }

                Call<String> call = apiService.dogetDlocalUrl(userPreferences.getApiKey(), dLocal);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());


                        if (response.body() != null) {

                            if (response.isSuccessful()) {

                                try {

                                    Utility.PrintLog(TAG, "success " + response.body());

                                    JSONObject jsonObject = new JSONObject(response.body());

                                    if (jsonObject.has("status")) {

                                        String status = jsonObject.getString("status");

                                        if (status.equalsIgnoreCase("false")) {
                                            String message = jsonObject.getString("message");
                                            if (language.getLanguage(message) != null && !language.getLanguage(message).equals("")) {
                                                hideLoader();
                                                showSnackErrorMessage(language.getLanguage(message));
                                                return;
                                            }
                                        }

                                    }

                                    if (jsonObject.has("redirect_url")) {

                                        redirect_url = jsonObject.getString("redirect_url");
                                        success_url = jsonObject.getString("success_url");
                                        cancel_url = jsonObject.getString("cancel_url");
                                        first_order_id = jsonObject.getString("first_order_id");

                                        GetToken getToken = new GetToken(CartPageActivity.this, redirect_url, first_order_id);
                                        getToken.execute();
                                    }

                                } catch (Exception e) {
                                    Utility.PrintLog(TAG, "Exception() " + response.body());
                                }

                            } else {

                                if (response.message() != null) {
                                    showSnackErrorMessage(response.message());
                                }
                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }

    }

    private void getPagoFacilUrlApiCall(Map<String, String> placeOrder) {

        try {


            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<String> call = apiService.doPagofacialUrl(userPreferences.getApiKey(), placeOrder);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());


                        if (response.body() != null) {

                            if (response.isSuccessful()) {

                                try {

                                    Utility.PrintLog(TAG, "success " + response.body());


                                    JSONObject jsonObject = new JSONObject(response.body());

                                    if (jsonObject.has("status")) {

                                        String status = jsonObject.getString("status");

                                        if (status.equalsIgnoreCase("false")) {
                                            String message = jsonObject.getString("message");
                                            if (language.getLanguage(message) != null && !language.getLanguage(message).equals("")) {
                                                hideLoader();
                                                showSnackErrorMessage(language.getLanguage(message));
                                                return;
                                            }
                                        }

                                    }

                                    if (jsonObject.has("redirect_url")) {

                                        redirect_url = jsonObject.getString("redirect_url");
                                        success_url = jsonObject.getString("success_url");
                                        cancel_url = jsonObject.getString("cancel_url");
                                        first_order_id = jsonObject.getString("first_order_id");

                                        GetToken getToken = new GetToken(CartPageActivity.this, redirect_url, first_order_id);
                                        getToken.execute();
                                    }

                                } catch (Exception e) {
                                    Utility.PrintLog(TAG, "Exception() " + response.body());
                                }

                            } else {

                                if (response.message() != null) {
                                    showSnackErrorMessage(response.message());
                                }
                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }
    }

    private void getPaypalUrlApiCall(Map<String, String> placeOrder, String total_amount) {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<String> call = apiService.doPaypalUrl(userPreferences.getApiKey(), userPreferences.getUserId(), total_amount);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());


                        if (response.body() != null) {

                            if (response.isSuccessful()) {

                                try {

                                    Utility.PrintLog(TAG, "success " + response.body());

                                    JSONObject jsonObject = new JSONObject(response.body());

                                    if (jsonObject.has("paypal_result")) {

                                        PAYPAL_RESULT_OBJECT = jsonObject.getJSONObject("paypal_result").toString();
                                        Utility.PrintLog(TAG, "PAYPAL_RESULT_OBJECT " + PAYPAL_RESULT_OBJECT);

                                        placeOrder.put(PARAM_PAYMENT_RESULT, PAYPAL_RESULT_OBJECT);
                                    }

                                    if (jsonObject.has("redirect_url")) {

                                        redirect_url = jsonObject.getString("redirect_url");
                                        success_url = jsonObject.getString("success_url");
                                        cancel_url = jsonObject.getString("cancel_url");

                                        GetToken getToken = new GetToken(CartPageActivity.this, redirect_url, placeOrder);
                                        getToken.execute();

                                    }

                                } catch (Exception e) {
                                    Utility.PrintLog(TAG, "Exception() " + response.body());
                                }

                            } else {

                                if (response.message() != null) {
                                    showSnackErrorMessage(response.message());
                                }
                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }
    }

    private void removeDlocal() {
        Constants.isdLocalSelected = false;
        binding.layDocNumber.setVisibility(View.GONE);
        binding.edtDocumentNumber.setText(null);
        binding.errorDocNumber.setVisibility(View.GONE);

    }

    private void removeDiscount() {
        totalDiscount = 0;
    }

    private void placeOrder(Map<String, String> placeOrder) {

        showLoader(true);
        Call<BaseResponse> call = apiService.doPlaceOrder(userPreferences.getApiKey(), placeOrder);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response) {
                Utility.PrintLog(TAG, response.toString());
                checkErrorCode(response.code());

                if (!response.isSuccessful()) {
                    hideLoader();
                    return;
                }

                if (response.body() != null) {
                    if (checkResponseStatusWithMessage(response.body(), true)) {
                        userPreferences.setTotalCartSize(null);
                        userPreferences.setKuickerId("0");
                        showPaymentSuccessDilaog(response.body().getFirst_order_id(), CartPageActivity.this);

                    } else {
                        showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                    }
                }
                hideLoader();
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                Utility.PrintLog(TAG, t.toString());
                hideLoader();
            }
        });
    }

    private void onClickPromoCode() {

        try {

            String promoCode = binding.edtPromocode.getText().toString();
            if (TextUtils.isEmpty(promoCode)) {
                if (language != null) {
                    binding.txtPromoMessage.setText(language.getLanguage(KEY.promocode_not_found));
                }
                binding.txtPromoMessage.setTextColor(getResources().getColor(R.color.colorErrorRed));
                return;
            }


            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<CommonResponse> call = apiService.doApplyPromoCode(userPreferences.getApiKey(), userPreferences.getUserId(), promoCode);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (checkResponseStatusWithMessage(response.body(), true)) {

                                if (response.body().getPromoCodeDiscount() != null) {
                                    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                                    try {
                                        discountArray = ow.writeValueAsString(response.body().getPromoCodeDiscount());
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                }


                                discountId = response.body().getPromoCodeDiscount().getId();
                                viewchangeApplybutton(true);
                                discountStr = response.body().getCartOverviewData().getDiscount();
                                finalTotalAfterShipping = response.body().getCartOverviewData().getTotal();
                                setCalculation(response.body());

                                priceCalculationListener.removePaymentMethod();
                                priceCalculationListener.removeShipping();
                                showSnackResponse(language.getLanguage(response.body().getMessage()));
                                Utility.hideKeyboard(CartPageActivity.this);


                            } else {
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                                priceCalculationListener.removePaymentMethod();
                                priceCalculationListener.removeShipping();
                            }
                        }
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                        hideLoader();
                    }
                });
            } else {
                hideShowView(binding.dataNotFound, language.getLanguage(KEY.internet_connection_lost_please_retry_again));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }
    }

    private void viewChangedOnApplyPromocode(CommonResponse response) {

        CommonResponse.PromoCodeDiscount discount = response.getPromoCodeDiscount();


        List<CartDetails> cart_detail = response.getCartDetails();
        String PromocodeType = discount.getPromocode_type();
        String createdFor = discount.getCreated_for();

        if (PromocodeType.equals(Constants.MULTIPLE_PRODUCT)) {
            double convertedTemp = 0.0;
            String productsIds = discount.getProduct_ids();
            String[] idList = productsIds.split(",");

            for (int i = 0; i < cart_detail.size(); i++) {
                String createdBy = cart_detail.get(i).getCreated_by();

                if (createdBy != null && createdFor != null) {
                    if (createdFor.equals("1") || createdFor.equals(createdBy)) {
                        for (String id : idList) {
                            String productId = cart_detail.get(i).getProduct_id();

                            Utility.PrintLog("promocodeIdList", "id = " + id + "   " + "product_id = " + productId);

                            if (id.equals(productId)) {

                                Utility.PrintLog("promocodeIdList", "same id = " + id);

                                String type = discount.getType();
                                String dis = discount.getValue().replace(",", ".");
                                double value = Double.parseDouble(dis);

                                String price = cart_detail.get(i).getPrice();
                                price = price.replace(",", "");

                                double singleProductPrice = Double.parseDouble(price);
                                String productCurrencyCode = cart_detail.get(i).getCurrency_code();
                                Utility.PrintLog(TAG, "single price : " + singleProductPrice);

                                String qty = cart_detail.get(i).getQty();
                                int productQty = Integer.parseInt(qty);
                                double temp = 0.0;

                                if (type.equals(Constants.PERCENTAGE)) {

                                    totalDiscount = totalDiscount + ((singleProductPrice * productQty) * value) / 100;

                                    temp = totalDiscount;
                                    String dic = Utility.getValidPrice(String.valueOf(totalDiscount), productCurrencyCode);
                                    dic = dic.replace(",", "");
                                    totalDiscount = Double.parseDouble(dic);


                                } else { // if flat

                                    totalDiscount = totalDiscount + (singleProductPrice - value);
                                    temp = totalDiscount;

                                    String dic = Utility.getValidPrice(String.valueOf(totalDiscount), productCurrencyCode);
                                    dic = dic.replace(",", "");
                                    totalDiscount = Double.parseDouble(dic);
                                }
                                convertedTemp = totalDiscount;
                                totalDiscount = temp;

                            }
                        }
                    }
                }
                if (i == cart_detail.size() - 1) {
                    totalDiscount = convertedTemp;
                    //binding.txtDiscount.setText("-" + userPreferences.getCurrencySymbol() + setValidFormat(totalDiscount));
                }
            }
            viewchangeApplybutton(true);

        } else if (PromocodeType.equals(Constants.SINGLE_PRODUCT)) {

            String id = discount.getProduct_ids();

            for (int i = 0; i < cart_detail.size(); i++) {
                String createdBy = cart_detail.get(i).getCreated_by();

                if (createdBy != null && createdFor != null) {
                    if (createdFor.equals("1") || createdFor.equals(createdBy)) {
                        String cartId = cart_detail.get(i).getProduct_id();

                        if (id.equals(cartId)) {

                            String type = discount.getType();
                            String dis = discount.getValue();

                            dis = dis.replace(",", "");

                            double value = Double.parseDouble(dis);

                            String sp = cart_detail.get(i).getPrice();
                            sp = sp.replace(",", "");

                            double singleProductPrice = Double.parseDouble(sp);
                            String productCurrencyCode = cart_detail.get(i).getCurrency_code();
                            Utility.PrintLog(TAG, "single price : " + singleProductPrice);

                            String qty = cart_detail.get(i).getQty();
                            int productQty = Integer.parseInt(qty);

                            if (type.equals(Constants.PERCENTAGE)) {
                                totalDiscount = ((singleProductPrice * productQty) * value) / 100;
                                String dic = Utility.getValidPrice(String.valueOf(totalDiscount), productCurrencyCode);
                                dic = dic.replace(",", "");
                                totalDiscount = Double.parseDouble(dic);

                            } else { // if flat

                                totalDiscount = (singleProductPrice - value);
                                String dic = Utility.getValidPrice(String.valueOf(totalDiscount), productCurrencyCode);
                                dic = dic.replace(",", "");
                                totalDiscount = Double.parseDouble(dic);
                            }

                            //binding.txtDiscount.setText("-" + userPreferences.getCurrencySymbol() + setValidFormat(totalDiscount));

                        }
                    }
                }
            }

            viewchangeApplybutton(true);
        } else if (PromocodeType.equals(Constants.ALL) || PromocodeType.equals(Constants.USER_TYPE) || PromocodeType.equals(Constants.STREAMER)) {


            for (int i = 0; i < cart_detail.size(); i++) {
                String createdBy = cart_detail.get(i).getCreated_by();

                if (createdFor != null && createdBy != null) {

                    if (createdFor.equals("1") || createdFor.equals(createdBy)) {

                        String type = discount.getType();
                        String dis = discount.getValue();

                        dis = dis.replace(",", "");
                        double value = Double.parseDouble(dis);

                        String sp = cart_detail.get(i).getPrice();
                        sp = sp.replace(",", "");

                        double singleProductPrice = Double.parseDouble(sp);
                        String productCurrencyCode = cart_detail.get(i).getCurrency_code();
                        Utility.PrintLog(TAG, "(all,usertype,streamer) single price : " + singleProductPrice);

                        String qty = cart_detail.get(i).getQty();
                        int productQty = Integer.parseInt(qty);

                        if (type.equals(Constants.PERCENTAGE)) {
                            totalDiscount = totalDiscount + ((singleProductPrice * productQty) * value) / 100;
                            Utility.PrintLog(TAG, "discount on original price * qty = " + totalDiscount);

                            if (i == cart_detail.size() - 1) {
                                String dic = Utility.getValidPrice(String.valueOf(totalDiscount), productCurrencyCode);
                                dic = dic.replace(",", "");
                                totalDiscount = Double.parseDouble(dic);
                                Utility.PrintLog(TAG, "discount on converted  price * qty = " + totalDiscount);
                            }

                        } else { // if flat

                            totalDiscount = totalDiscount + ((singleProductPrice * productQty) - value);

                            if (i == cart_detail.size() - 1) {
                                String dic = Utility.getValidPrice(String.valueOf(totalDiscount), productCurrencyCode);
                                dic = dic.replace(",", "");
                                totalDiscount = Double.parseDouble(dic);
                            }

                        }

                        // binding.txtDiscount.setText("-" + userPreferences.getCurrencySymbol() + setValidFormat(totalDiscount));

                    }
                }
            }
            viewchangeApplybutton(true);
        }

    }

    private void viewchangeApplybutton(boolean isPress) {

        if (isPress) {
            binding.promoCancelay.setVisibility(View.VISIBLE);
            String promoCode = binding.edtPromocode.getText().toString();
            Utility.PrintLog("Promocode", "promo = " + promoCode);
            binding.appliedPromo.setText(promoCode);

            binding.edtPromocode.setText(null);
            binding.btnApplyPromoCode.setAlpha(0.5f);
            binding.btnApplyPromoCode.setEnabled(false);

        } else {

            binding.promoCancelay.setVisibility(View.GONE);
            binding.btnApplyPromoCode.setAlpha(1f);
            binding.btnApplyPromoCode.setEnabled(true);
        }

    }

    public void hideShowView(View view, String message) {
        binding.dataNotFound.setVisibility(View.GONE);
        binding.dataView.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        TextView button = binding.getRoot().findViewById(R.id.btnTryAgain);
        button.setText(language.getLanguage(KEY.discover_the_best_lives));
        error.setText(message);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //   getSingleCard();
        //getCartDetails();
    }


    private void setCalculation(CommonResponse response) {
        CartOverviewData cartOverviewData = response.getCartOverviewData();
        cartOverviewDataTemp = cartOverviewData;
        binding.txtSubtotal.setText(cartOverviewData.getSub_total());
        binding.txtDiscount.setText(cartOverviewData.getDiscount());
        binding.txtTax.setText(cartOverviewData.getTax());
        binding.txtShippingCharge.setText(cartOverviewData.getShipping_charge());
        binding.total.setText(cartOverviewData.getTotal());

        finalTotal = cartOverviewData.getO_total();


    }

    @Override
    public void hidePromo() {
        removePromocode();
    }

    @Override
    public void removeShipping() {
        selectedAddress = null;
        addressId = null;
        binding.txtAddress.setText(language.getLanguage(KEY.please_select_address));
        //binding.txtDiscount.setText("-" + userPreferences.getCurrencySymbol() + "0.00");
        binding.txtShippingCharge.setText(userPreferences.getCurrencySymbol() + "0.00");
        binding.txtTax.setText(userPreferences.getCurrencySymbol() + "0.00");

        if (!discountStr.equals("")) {
            binding.txtDiscount.setText(discountStr);
        }
        //finalTotalAfterShipping
        // binding.txtDiscount.setText(userPreferences.getCurrencySymbol() + "0.00");

        if (!finalTotalAfterShipping.equals("")) {
            binding.txtTotal.setText(finalTotalAfterShipping);
        }


    }

    @Override
    public void removePaymentMethod() {

        isPaymentCardSelected = false;
        binding.cardNumber.setText(language.getLanguage(KEY.please_select_payment_card));
        binding.cardImage.setImageBitmap(null);
        binding.cardImage.setVisibility(View.GONE);
        cardId = null;
        zipCode = null;
        Constants.isMercadoPagoSelected = false;
        Constants.isPaypalSelected = false;
        Constants.isPagofacilSelected = false;
        removeDlocal();
    }

    @Override
    public void onProductCalculation(CommonResponse response) {

        setCalculation(response);
    }

    private double getValidTax(double subTotal) {

        try {

            if (userPreferences != null && userPreferences.getTaxStatus() != null) {

                if (userPreferences.getTaxStatus().equals(Constants.ENABLE)) {
                    String type = userPreferences.getTaxType();
                    String amount = userPreferences.getTaxAmount();

                    if (amount != null && !amount.equals("0")) {
                        amount = amount.replace(",", "");
                        double txtAmount = Double.parseDouble(amount);

                        if (type.equals(Constants.PERCENTAGE)) {
                            return subTotal / txtAmount;
                        } else { // if flat
                            return subTotal + txtAmount;
                        }
                    }
                }
            }

        } catch (Exception e) {

        }

        return 0;
    }

    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final CartDetails cart = cardItemAdapter.getCardList().get(position);

                deleteCart(viewHolder, CartPageActivity.this, position, cart);
            }

        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.rvProductItem);
    }

    public void deleteCart(RecyclerView.ViewHolder viewHolder, CartPageActivity mContext, int position, CartDetails cart) {


        try {

            Call<CommonResponse> call = mContext.apiService.doDeleteCart(mContext.userPreferences.getApiKey(), mContext.userPreferences.getUserId(), cart.getCart_id());

            if (mContext.checkInternetConnectionWithMessage()) {
                mContext.showLoader(true);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, "response : " + response);

                        if (!response.isSuccessful()) {
                            mContext.showSnackErrorMessage(mContext.language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            return;
                        }

                        if (response.body() != null) {
                            if (mContext.checkResponseStatusWithMessage(response.body(), true)) {
                                Utility.PrintLog(TAG, "status true : " + response);
                                mContext.showSnackResponse(mContext.language.getLanguage(response.body().getMessage()));

                                removeAll();
                                cardItemAdapter.getCardList(cart);
                                cartDetail = response.body().getCartDetails();
                                setCalculation(response.body());
                                userPreferences.setTotalCartSize(String.valueOf(cartDetail.size()));
                                cardItemAdapter.notifyDataSetChanged();

                            } else {
                                userPreferences.setTotalCartSize("");
                                hideShowView(binding.dataNotFound, (language.getLanguage(response.body().getMessage())));
                            }
                        }
                        mContext.hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, "onFailure language() : " + t.toString());
                        mContext.hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "exception language() : " + e.toString());
            mContext.hideLoader();
        }
    }

    private void removeAll() {
        if (cardItemAdapter != null) {
            previousPrice = 0;
        }
        discountStr = "";

        removePaymentMethod();
        removePromocode();
        removeShipping();
        removeDiscount();
        removeDlocal();
    }

    public void removePromocode() {
        discountArray = "";
        discountId = "";
        discountStr = "";
        viewchangeApplybutton(false);
        priceCalculationListener.removeShipping();
        priceCalculationListener.removePaymentMethod();
    }

    public class GetToken extends AsyncTask<String, String, String> {


        String oauth_url;
        Dialog auth_dialog;
        WebView web;
        TextView progressingMessage;
        ProgressDialog progress;
        Context context;
        Map<String, String> placeOrder;

        String url, first_order_id;

        // call when paypal selected
        public GetToken(Context context, String url, Map<String, String> placeOrder) {

            this.url = url;
            this.context = context;
            this.placeOrder = placeOrder;
            progress = new ProgressDialog(context);
            progress.setMessage("Please wait...");
            progress.setCancelable(false);
        }

        //call when pagofacil selected
        public GetToken(CartPageActivity context, String redirect_url, String first_order_id) {
            this.context = context;
            this.url = redirect_url;
            this.first_order_id = first_order_id;
            progress = new ProgressDialog(context);
            progress.setMessage("Please wait...");
            progress.setCancelable(false);
            Utility.PrintLog(TAG, "redirect URL = " + url);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if (progress != null && !progress.isShowing()) {

                progress.show();
            }
        }

        @Override
        protected String doInBackground(String... args) {

            try {

                oauth_url = url;
            } catch (Exception e) {
                e.printStackTrace();
                PrintLog("WebViewClient", "Exception() " + oauth_url);
            }
            return oauth_url;
        }

        @Override
        protected void onPostExecute(String oauth_url) {
            if (oauth_url != null) {
                PrintLog("WebViewClient", "Auth url : " + oauth_url);

                progress.dismiss();

                auth_dialog = new Dialog(context);
                auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                auth_dialog.setContentView(R.layout.auth_dialog);
                web = auth_dialog.findViewById(R.id.webv);
                progressingMessage = auth_dialog.findViewById(R.id.progressingMessage);

                if (language != null) {

                    progressingMessage.setText(language.getLanguage(KEY.remember_not_to_close_the_application_until_that_your_recharge_is_reflected));
                    //progressingMessage.setText("La conexión puede ser lenta. No cierres el APP de Kuick antes de que se haya procesado tu pedido. Gracias :)");
                }


                web.getSettings().setJavaScriptEnabled(true);
                final ProgressBar pbr = auth_dialog.findViewById(R.id.progressBar);
                web.loadUrl(oauth_url);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(auth_dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                auth_dialog.getWindow().setAttributes(lp);


                web.setWebViewClient(new WebViewClient() {
                    final boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);


                        String[] split_url = url.split("\\?");
                        Utility.PrintLog("WebViewClient", "full url = " + url);
                        Utility.PrintLog("WebViewClient", "spilt array = " + split_url);
                        Utility.PrintLog("WebViewClient", "spilt url 0 = " + split_url[0]);
                        Utility.PrintLog("WebViewClient", "success_url " + success_url);
                        Utility.PrintLog("WebViewClient", "cancel_url " + cancel_url);

                        if (split_url[0].equalsIgnoreCase(success_url)) {
                            PrintLog("WebViewClient", "inside success url " + url.trim());
                            auth_dialog.dismiss();

                            Utility.PrintLog(TAG, "success time payment type = " + paymentType);
                            if (paymentType.equals(PAYPAL)) {
                                setSuccess(placeOrder);

                            } else if (paymentType.equals(DLOCAL) || paymentType.equals(PAGOFACIL) || paymentType.equals(MERCADO_PAGO)) {
                                userPreferences.setTotalCartSize(null);
                                showPaymentSuccessDilaog(first_order_id, CartPageActivity.this);
                                userPreferences.setKuickerId("0");
                            }

                        } else if (split_url[0].equals(cancel_url)) {
                            PrintLog("WebViewClient", "inside cancel url = " + url.trim());
                            auth_dialog.dismiss();
                            setFail();
                        } else {
                            PrintLog("WebViewClient", "contains = other");
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }
                });

                web.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress < 80 && pbr.getVisibility() == ProgressBar.GONE) {
                            pbr.setVisibility(ProgressBar.VISIBLE);
                        }
                        pbr.setProgress(progress);
                        if (progress >= 80) {
                            pbr.setVisibility(ProgressBar.GONE);
                        }
                    }
                });

                auth_dialog.show();
                auth_dialog.setCancelable(true);
            } else {
                Log.e("url", " null = " + oauth_url);
            }
        }

        public void setSuccess(Map<String, String> placeOrder) {
            placeOrder(placeOrder);
        }

        public void setFail() {
            //removeAll();
            removeDlocal();
            removePaymentMethod();
            removeShipping();
            removeDiscount();
            //getCartDetails();
            removePromocode();
        }
    }

}