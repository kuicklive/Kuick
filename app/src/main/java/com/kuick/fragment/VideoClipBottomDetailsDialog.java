package com.kuick.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.icu.util.UniversalTimeScale;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.kuick.R;
import com.kuick.Remote.Networking;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.ClipsData;
import com.kuick.Response.CommonResponse;
import com.kuick.SplashScreen.SecondSplashScreen;
import com.kuick.activity.CartPageActivity;
import com.kuick.activity.HomeActivity;
import com.kuick.activity.ProductDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.base.BaseBottomSheetDialogFragment;
import com.kuick.base.BaseView;
import com.kuick.databinding.VideoClipsDetailsBottomFragmentBinding;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.loader.LoaderDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.ENDPOINT_CLIP_ID;
import static com.kuick.Remote.EndPoints.ENDPOINT_IS_CART;
import static com.kuick.Remote.EndPoints.ENDPOINT_IS_DISLIKE;
import static com.kuick.Remote.EndPoints.ENDPOINT_PRODUCT_VARIANT;
import static com.kuick.Remote.EndPoints.ENDPOINT_QUANTITY;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_SIZE_ID;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Constants.OK;
import static com.kuick.util.comman.Constants.SESSION_EXPIRE;
import static com.kuick.util.comman.Constants.isSizeVariant;
import static com.kuick.util.comman.Utility.goToNextScreen;

public class VideoClipBottomDetailsDialog extends BaseBottomSheetDialogFragment {


    private final ClipsData clipData;
    private final HomeActivity mContext;
    private final String TAG = "VideoClipBottomDetailsDialog";
    private VideoClipsDetailsBottomFragmentBinding binding;
    private HashMap<String, String> sizeIdHash;
    private HashMap<String, String> totalQuantity;
    private String selectedProductSizeId ="0";
    private String availableQuantity2 ="0";

    public VideoClipBottomDetailsDialog(ClipsData sizes, HomeActivity mContext) {
        this.clipData = sizes;
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = VideoClipsDetailsBottomFragmentBinding.inflate(inflater, container, false);
        init();
        setLanguageLable();

        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {



        binding.btnAddToCart.setOnClickListener(this);
        binding.btnPluse.setOnClickListener(this);
        binding.btnMinus.setOnClickListener(this);

        BaseActivity.showGlideImage(mContext, clipData.getImage(), binding.productImage);
        binding.productName.setText(clipData.getName());
        binding.discountPrice.setText(clipData.getDiscount_price());
        binding.productPrice.setText(clipData.getPrice());
        binding.productPrice.setPaintFlags(binding.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.txtDescription.setText(clipData.getDescription());
        binding.txtDescription.setMovementMethod(new ScrollingMovementMethod());

        if(clipData.getIs_show().equals("1")){
            binding.productPrice.setVisibility(View.VISIBLE);
        }else{
            binding.productPrice.setVisibility(View.GONE);
        }

        if (clipData.getSizes()!=null && clipData.getSizes().size() > 0){
            setSizeVariant(clipData.getSizes());
        }


        binding.scrollView.setOnTouchListener((v, event) -> {

            binding.txtDescription.getParent().requestDisallowInterceptTouchEvent(false);

            return false;
        });

        binding.txtDescription.setOnTouchListener((v, event) -> {

            binding.txtDescription.getParent().requestDisallowInterceptTouchEvent(true);

            return false;
        });
    }

    private void setLanguageLable() {

        if (language != null) {
            binding.btnAddToCart.setText(language.getLanguage(KEY.add_to_cart));
        }
    }

    private void setSizeVariant(List<ClipsData.Sizes> sizeVariant) {
        try {

            ArrayList<String> SizeVariantId = new ArrayList<>();
            sizeIdHash = new HashMap<>();
            totalQuantity = new HashMap<>();

            for (int i = 0; i < sizeVariant.size(); i++) {

                String size = sizeVariant.get(i).getSize();
                String id = sizeVariant.get(i).getId();
                String quantity = sizeVariant.get(i).getQuantity();

                sizeIdHash.put(size, id);
                totalQuantity.put(size, quantity);
                SizeVariantId.add(size);

                selectedProductSizeId = sizeVariant.get(0).getId();
                availableQuantity2 = sizeVariant.get(0).getQuantity();
            }

            setSizeVariantSpinner(SizeVariantId);

        } catch (Exception e) {
            Utility.PrintLog(TAG, "size variant exception : " + e.toString());
        }
    }

    private String getValidQuantity(int qty) {
        String quantity = "0";

        if (qty < 10) {
            quantity = "0" + qty;
        } else {
            quantity = String.valueOf(qty);
        }

        return quantity;

    }

    private void setSizeVariantSpinner(ArrayList<String> sizeVariantId) {


        Utility.showView(binding.sizeSpinnerView);

        if (sizeVariantId != null && sizeVariantId.size() > 0) {

            try {
                Utility.PrintLog(TAG, "sizeVariantId" + sizeVariantId);
                ArrayAdapter listAdapter = new ArrayAdapter(HomeActivity.homeActivity, R.layout.spinner_item, sizeVariantId);
                listAdapter.setDropDownViewResource(R.layout.size_spinner_item);
                binding.sizeListSpinner.setAdapter(listAdapter);

                binding.sizeListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        selectedProductSizeId = sizeIdHash.get(sizeVariantId.get(position));
                        availableQuantity2 = totalQuantity.get(sizeVariantId.get(position));
                        binding.numberOfItem.setText(getValidQuantity(1));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } catch (Exception e) {
                Utility.PrintLog(TAG, "size list adapter exception : " + e.toString());
            }
        }
    }

    private void addToCartAPI() {

        Map<String, String> cartOrDislike = new HashMap<>();
        cartOrDislike.put(PARAM_USER_ID, BaseActivity.baseActivity.userPreferences.getUserId());
        cartOrDislike.put(ENDPOINT_CLIP_ID, clipData.getId());
        cartOrDislike.put(ENDPOINT_PRODUCT_VARIANT, clipData.getProduct_variant());
        cartOrDislike.put(ENDPOINT_IS_CART, "1");
        cartOrDislike.put(ENDPOINT_IS_DISLIKE, "0");
        cartOrDislike.put(ENDPOINT_QUANTITY,  binding.numberOfItem.getText().toString());

        if (clipData.getSizes() != null && clipData.getSizes().size() > 0) {
            cartOrDislike.put(PARAM_PRODUCT_SIZE_ID, selectedProductSizeId);
        }

        if (checkInternetConnectionWithMessage(getContext())) {
            showLoader(true);

            Call<CommonResponse> call = apiService.doVideoClipCartOrDislike(userPreferences.getApiKey(), cartOrDislike);
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                    Utility.PrintLog(TAG, response.toString());
                    checkErrorCode(response.code(), getActivity());


                    if (checkResponseStatusWithMessage(response.body(), true)) {
                        Utility.PrintLog(TAG, "video add to cart or dislike response successfully");
                        if (response.body() != null) {
                            if (response.body().getCart_count() != null) {
                                userPreferences.setTotalCartSize(response.body().getCart_count());
                                VideoClipsFragment.mediaPlayer.onReleasePlayer();
                                dismiss();
                                startActivity(new Intent(getContext(), CartPageActivity.class));
                            }
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


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnAddToCart:
                Utility.vibrate();
                addToCartAPI();
                break;
            case R.id.btnPluse:
                qauntityPlus();
                break;
            case R.id.btnMinus:
                qauntityMinus();
                break;
        }
    }

    private void qauntityMinus() {

        try {

            int quan = Integer.parseInt(binding.numberOfItem.getText().toString());

            if (quan == 1) {
                return;
            }

            quan = quan - 1;
            binding.numberOfItem.setText(getValidQuantity(quan));


        } catch (Exception e) {
            Utility.PrintLog(TAG, "onMinus() Exception : " + e.toString());
        }
    }

    private void qauntityPlus() {

        try {

            int totalQty = 0;
            int qty = Integer.parseInt(binding.numberOfItem.getText().toString());

            if (clipData.getSizes() != null && clipData.getSizes().size() > 0) {
                totalQty = Math.min(Integer.parseInt(availableQuantity2), Integer.parseInt(clipData.getNo_of_units()));

            } else {
                totalQty = Integer.parseInt(clipData.getNo_of_units());
            }


            if (qty == totalQty) {
                Utility.showToast(mContext.language.getLanguage(KEY.quantity_is_not_available_for_this_product),getContext());
                //localShowSnackErrorMessage(mContext.language.getLanguage(KEY.quantity_is_not_available_for_this_product));
                return;
            }

            qty = qty + 1;
            binding.numberOfItem.setText(getValidQuantity(qty));

        } catch (Exception e) {
            Utility.PrintLog(TAG,"snack exception : " + e);
        }
    }

}
