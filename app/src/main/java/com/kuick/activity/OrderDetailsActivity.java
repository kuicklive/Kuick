package com.kuick.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.OrderDetailResponse;
import com.kuick.adapter.OrderDetailAdapter;
import com.kuick.adapter.OrderProccessAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityOrderDetailsBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.base.BaseActivity.CreateDialogDialogFragment.builder;
import static com.kuick.util.comman.Constants.DLOCAL;
import static com.kuick.util.comman.Constants.DLOCAL_DIRECT;
import static com.kuick.util.comman.Constants.MERCADO_PAGO;
import static com.kuick.util.comman.Constants.MercadoPago;
import static com.kuick.util.comman.Constants.PAGOFACIL;
import static com.kuick.util.comman.Constants.PAYPAL;
import static com.kuick.util.comman.KEY.Confirmed;
import static com.kuick.util.comman.KEY.approved;
import static com.kuick.util.comman.KEY.arrived;
import static com.kuick.util.comman.KEY.canclled;
import static com.kuick.util.comman.KEY.completed;
import static com.kuick.util.comman.KEY.confirmed;
import static com.kuick.util.comman.KEY.delivered;
import static com.kuick.util.comman.KEY.pending;
import static com.kuick.util.comman.KEY.rejected;
import static com.kuick.util.comman.KEY.returned;
import static com.kuick.util.comman.KEY.shipped;
import static com.kuick.util.comman.Utility.PrintLog;

public class OrderDetailsActivity extends BaseActivity {

    private final ArrayList<String> statusList = new ArrayList<>();
    private final String TAG = "OrderDetailsActivity";
    private ActivityOrderDetailsBinding binding;
    private TextView txtTitle;
    private ImageView btnBack, btnCart;
    private TextView btnTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setClickListener();
        setToolBar();
        setLanguageLable();
        callOrderDetailApi();

    }


    private void setLanguageLable() {

        try {

            if (language != null) {

                txtTitle.setText(language.getLanguage(KEY.order_detail));
                binding.txtConstomerInfo.setText(language.getLanguage(KEY.customer_info));

                binding.fullname.setText(language.getLanguage(KEY.full_name));
                binding.contact.setText(language.getLanguage(KEY.contact));
                binding.tAddress.setText(language.getLanguage(KEY.address));
                binding.paymentType.setText(language.getLanguage(KEY.payment_type));

                binding.sutotal.setText(language.getLanguage(KEY.subtotal));
                binding.txtdiscount.setText(language.getLanguage(KEY.discount));
                binding.txtTax.setText(language.getLanguage(KEY.tax));
                binding.txtShippedCharge.setText(language.getLanguage(KEY.shipping_charge));
                binding.txtTotal.setText(language.getLanguage(KEY.total));

                binding.discount.setText(userPreferences.getCurrencySymbol() + "00");
                binding.txtSubtotal.setText(userPreferences.getCurrencySymbol() + "00");
                binding.shippingCharge.setText(userPreferences.getCurrencySymbol() + "00");
                binding.tax.setText(userPreferences.getCurrencySymbol() + "00");
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }

    private void callOrderDetailApi() {
        String orderId = getIntent().getStringExtra(Constants.ORDER_ID);

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<CommonResponse> call = apiService.doOrderDetails(userPreferences.getApiKey(), userPreferences.getUserId(), orderId);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        PrintLog(TAG, "response : " + response);

                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful()) {
                                hideShowView(binding.dataView, null);
                                setData(response.body());
                            } else {
                                hideShowView(binding.dataNotFound, language.getLanguage(response.body().getMessage()));
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                            }
                        }
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        //showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        hideLoader();
                    }
                });
            } else {
                hideShowView(binding.dataNotFound, language.getLanguage(KEY.internet_connection_lost_please_retry_again));
                hideLoader();
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            //showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideShowView(binding.dataNotFound, language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }
    }

    private void setProccessAdapter(String order_status) {

        try {
            if (language != null) {
                String arrived = "";
                String confirmed = "";

                String inProgress = language.getLanguage(KEY.order_in_progress);
                String shipped = language.getLanguage(KEY.order_shipped);

                if (order_status.equalsIgnoreCase(canclled)) {
                    confirmed = language.getLanguage(KEY.order_canclled);

                } else if (order_status.equalsIgnoreCase(rejected)) {
                    confirmed = language.getLanguage(KEY.order_rejected);
                } else {
                    confirmed = language.getLanguage(KEY.order_confirmed);
                }

                if (order_status.equalsIgnoreCase(returned)) {
                    arrived = language.getLanguage(KEY.order_returned);
                } else {
                    arrived = language.getLanguage(KEY.order_arrived);
                }


                statusList.add(inProgress);
                statusList.add(confirmed);
                statusList.add(shipped);
                statusList.add(arrived);

                binding.rcvOrderStatus.setLayoutManager(new LinearLayoutManager(this));
                binding.rcvOrderStatus.setAdapter(new OrderProccessAdapter(statusList, order_status));
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }

    }

    private void setClickListener() {

        if (builder != null) {
            builder.dismiss();
            Utility.PrintLog(TAG, "isFromNotification dismiss");

        }

        btnBack = binding.getRoot().findViewById(R.id.img_back);
        btnTryAgain = binding.getRoot().findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(this);
        binding.layCard.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void setData(CommonResponse response) {

        OrderDetailResponse orderDetail = response.getOrderDetailResponses();

        binding.orderId.setText(language.getLanguage(KEY.order) + " #" + orderDetail.getOrder_id());
        binding.dateOfOrder.setText(language.getLanguage(KEY.date_of_order) + " - " + orderDetail.getShow_created_at());
        binding.txtName.setText(orderDetail.getUser_name());
        binding.txtEmail.setText(orderDetail.getUser_email());
        binding.txtMobile.setText(orderDetail.getUser_phone());
        binding.txtFullAddress.setText(orderDetail.getUser_address());

        if (orderDetail.getPayment_method().equalsIgnoreCase(PAYPAL)) {
            binding.cardImage.setImageResource(R.drawable.paypal);
            binding.cardNumber.setText(PAYPAL);

        } else if (orderDetail.getPayment_method().equalsIgnoreCase(PAGOFACIL)) {

            binding.cardImage.setImageResource(R.drawable.pagofacil_icon);
            binding.cardNumber.setText(PAGOFACIL);

        } else if (orderDetail.getPayment_method().equalsIgnoreCase(DLOCAL) /*||
                orderDetail.getPayment_method().equalsIgnoreCase(DLOCAL_DIRECT)*/) {

            binding.cardImage.setImageResource(R.drawable.dlocal_icon);
            binding.cardNumber.setText(DLOCAL);

        } else if (orderDetail.getPayment_method().equalsIgnoreCase(MercadoPago)) {

            binding.cardImage.setImageResource(R.drawable.mercadopago_logo);
            binding.cardNumber.setText(MERCADO_PAGO);

        } else if (orderDetail.getPayment_method().equalsIgnoreCase(DLOCAL_DIRECT))
        {
            BaseActivity.showGlideImage(this, orderDetail.getCard_image(), binding.cardImage);
            if (orderDetail.getLast_4() != null) {
                binding.cardNumber.setText(orderDetail.getLast_4());
            }
        }
        else {

            BaseActivity.showGlideImage(this, orderDetail.getCard_image(), binding.cardImage);
            if (orderDetail.getLast_4() != null) {
                binding.cardNumber.setText(orderDetail.getLast_4());
            }

        }


        binding.discount.setText(orderDetail.getDiscount());
        binding.tax.setText(orderDetail.getTax());
        binding.shippingCharge.setText(orderDetail.getShipping_charge());
        binding.total.setText(orderDetail.getTotal_amount());
        binding.txtSubtotal.setText(orderDetail.getAmount());
        Utility.PrintLog(TAG, "Order detail total Price : " + orderDetail.getTotal_amount());

        setOrderStatus(orderDetail);


        List<OrderDetailResponse.OrderProductDetails> orderProductDetail = orderDetail.getOrderProductDetails();
        if (orderProductDetail != null && orderProductDetail.size() > 0) {
            binding.rvProductItem.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            binding.rvProductItem.setAdapter(new OrderDetailAdapter(orderProductDetail, this));
        }

        setProccessAdapter(orderDetail.getOrder_status());

    }

    private void setButton(String text, Drawable drawable) {

        binding.progressBtn.setText(text);
        binding.progressBtn.setBackground(drawable);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setOrderStatus(OrderDetailResponse orderStatus) {

        String status = orderStatus.getOrder_status();
        String trackingNumber = orderStatus.getTracking_number_details();
        String arriveDate = orderStatus.getArrival_date();


        if (status.equalsIgnoreCase(pending)) {
            setButton(
                    language.getLanguage(KEY.in_progress),
                    this.getResources().getDrawable(R.drawable.yellow_shape));

        } else if (status.equalsIgnoreCase(canclled)) {
            setButton(
                    language.getLanguage(KEY.canclled),
                    this.getResources().getDrawable(R.drawable.red_shape));
        } else if (status.equalsIgnoreCase(approved) || status.equalsIgnoreCase(Confirmed)) {
            setButton(
                    language.getLanguage(confirmed),
                    this.getResources().getDrawable(R.drawable.dark_green));

        } else if (status.equalsIgnoreCase(rejected)) {
            setButton(
                    language.getLanguage(rejected),
                    this.getResources().getDrawable(R.drawable.pink_shape));

        } else if (status.equalsIgnoreCase(shipped)) {
            setButton(
                    language.getLanguage(shipped),
                    this.getResources().getDrawable(R.drawable.shipped_color));

            binding.trackingNumber.setVisibility(View.VISIBLE);
            binding.estimateDate.setVisibility(View.VISIBLE);
            binding.trackingNumber.setText(language.getLanguage(KEY.tracking_number_details) + " - " + trackingNumber);
            binding.estimateDate.setText(language.getLanguage(KEY.estimated_delivery_date) + " - " + arriveDate);

        } else if (status.equalsIgnoreCase(returned)) {
            setButton(
                    language.getLanguage(returned),
                    this.getResources().getDrawable(R.drawable.retrun_shape));

        } else if (status.equalsIgnoreCase(completed) || status.equalsIgnoreCase(arrived)) {
            setButton(
                    language.getLanguage(delivered),
                    this.getResources().getDrawable(R.drawable.light_green));
        }


    }

    private void setToolBar() {
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        btnCart = binding.getRoot().findViewById(R.id.img_cart);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        binding.tAddress.setOnClickListener(this);
        binding.txtFullAddress.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:
                Constants.isRefreshAPI = true;
                onBackPressed();
                break;
            case R.id.tAddress:
            case R.id.txtFullAddress:
                //goToNextScreen(this,AddressInformation.class,false);
                break;
            case R.id.layCard:
                // goToNextScreen(this,PaymentCardList.class,false);
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
            case R.id.btnTryAgain:
                isForDiscover = true;
                goToNextScreen(this, HomeActivity.class, true);
                break;

        }
    }

    public void hideShowView(View view, String message) {
        binding.dataNotFound.setVisibility(View.GONE);
        binding.dataView.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        error.setText(message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constants.isRefreshAPI = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}