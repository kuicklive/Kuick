package com.kuick.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.ClipsData;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.Variant;
import com.kuick.activity.CartPageActivity;
import com.kuick.activity.HomeActivity;
import com.kuick.adapter.SpinnerAdapter;
import com.kuick.adapter.VariantColorSelectionAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.base.BaseBottomSheetDialogFragment;
import com.kuick.databinding.VideoClipsDetailsBottomFragmentBinding;
import com.kuick.interfaces.ColorSelectionListener;
import com.kuick.util.comman.Analytic;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

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
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_CENTRY_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_COLOR_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_SHOPIFY_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_SIZE_ID;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.activity.ProductDetailsActivity.colorSelectionListener;
import static com.kuick.util.comman.Constants.CENTRY_VARIANT;
import static com.kuick.util.comman.Constants.COLOR_VARIANT;
import static com.kuick.util.comman.Constants.No_VARIANT;
import static com.kuick.util.comman.Constants.SHOPIFY_VARIANT;
import static com.kuick.util.comman.Constants.SIZE_COLOR_VARIANT;
import static com.kuick.util.comman.Constants.SIZE_VARIANT;
import static com.kuick.util.comman.Constants.isCentryVariant;
import static com.kuick.util.comman.Constants.isColorSizeVariant;
import static com.kuick.util.comman.Constants.isColorVariant;
import static com.kuick.util.comman.Constants.isSizeVariant;

public class VideoClipBottomDetailsDialog extends BaseBottomSheetDialogFragment implements ColorSelectionListener  {


    private final ClipsData clipData;
    private final HomeActivity mContext;
    private final String TAG = "VideoClipBottomDetailsDialog";
    private VideoClipsDetailsBottomFragmentBinding binding;
    private HashMap<String, String> sizeIdHash;
    private HashMap<String, String> totalQuantity;
    private String selectedProductSizeId ="0";
    private String availableQuantity2 ="0";
    private String price = "";
    private HashMap<String, String> colorIdHash;
    private HashMap<String, String> priceHash;
    private HashMap<String, String> discountPriceHash;
    private String selectedProductColorId = "";
    private boolean isSizeColorVariant;
    private boolean isFirstTime;
    private String selectedProductCentryId ="";
    private HashMap<String, String> shopifyIdHash;
    private final HashMap<String, List<Variant.Sizes>> colorSizeArrayList = new HashMap<>();
    private String selectedShopifyId = "";
    private String discountPrice = "";

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


        colorSelectionListener = this;
        binding.btnAddToCart.setOnClickListener(this);
        binding.btnPluse.setOnClickListener(this);
        binding.btnMinus.setOnClickListener(this);

        if (clipData.getImage() != null) {
            BaseActivity.showGlideImageWithError(mContext, mContext.userPreferences.getImageUrl().concat(clipData.getImage()),
                    binding.productImage, ContextCompat.getDrawable(mContext, R.drawable.no_image));
        } else {
            binding.productImage.setImageResource(R.drawable.no_image);
        }

        binding.productName.setText(clipData.getName());
        binding.txtDescription.setText(clipData.getDescription());
        binding.txtDescription.setMovementMethod(new ScrollingMovementMethod());
        binding.productPrice.setPaintFlags(binding.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        if (clipData.getVariant()!=null){

            if (clipData.getVariant().getNoVariant() != null) {
                setNoVariant(clipData.getVariant().getNoVariant());
            }
            if (clipData.getVariant().getSizeVariant() != null) {
                setSizeVariant(clipData.getVariant().getSizeVariant());
            }

            if (clipData.getVariant().getColorVariant() != null) {
                setColorVariant(clipData.getVariant().getColorVariant());
            }

            if (clipData.getVariant().getSizeColorVariant() != null) {
                setSizeColorVariant(clipData.getVariant().getSizeColorVariant());
            }

            if (clipData.getVariant().getCentryVariant() != null) {
                setCentryVariant(clipData.getVariant().getCentryVariant());
            }

            if (clipData.getVariant().getShopify_variant()!=null)
            {
                setShopifyVariant(clipData.getVariant().getShopify_variant());
            }

        }


        if(clipData.getIs_show().equals("1")){
            binding.productPrice.setVisibility(View.VISIBLE);
        }else{
            binding.productPrice.setVisibility(View.INVISIBLE);
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

    private void setShopifyVariant(List<Variant.ShopifyVariant> shopify_variant) {

        binding.shopifySpinnerView.setVisibility(View.VISIBLE);

        if (shopify_variant != null) {

            new Handler().post(() -> {
                try {
                    if (shopify_variant.size() > 0)
                    {

                        ArrayList<String> ShopifyVariantId = new ArrayList<>();
                        shopifyIdHash = new HashMap<>();
                        totalQuantity = new HashMap<>();
                        HashMap<String, String> priceList = new HashMap<>();
                        HashMap<String, String> discountPriceList = new HashMap<>();

                        price = shopify_variant.get(0).getPrice();
                        discountPrice = shopify_variant.get(0).getDiscount_price();

                        for (int i = 0; i < shopify_variant.size(); i++)
                        {

                            String title = shopify_variant.get(i).getTitle();
                            String id = shopify_variant.get(i).getId();
                            String quantity = shopify_variant.get(i).getQuantity();
                            String price = shopify_variant.get(i).getPrice();
                            String discountPrice = shopify_variant.get(i).getDiscount_price();

                            shopifyIdHash.put(title, id);
                            totalQuantity.put(title, quantity);
                            priceList.put(title, price);
                            discountPriceList.put(title, discountPrice);
                            ShopifyVariantId.add(title);

                            selectedShopifyId = shopify_variant.get(0).getId();
                            availableQuantity2 = shopify_variant.get(0).getQuantity();
                            Utility.PrintLog(TAG, "shopify_variant : " + availableQuantity2);
                        }


                        binding.productPrice.setText(price);
                        binding.discountPrice.setText(discountPrice);

                        Utility.PrintLog(TAG, "shopify_variant price" + price);
                        ArrayAdapter listAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, ShopifyVariantId);
                        listAdapter.setDropDownViewResource(R.layout.size_spinner_item);
                        binding.shopifyListSpinner.setAdapter(listAdapter);
                        isFirstTime = true;
                        binding.shopifyListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (isFirstTime) {
                                    isFirstTime = false;
                                    return;
                                }

                                selectedShopifyId = shopifyIdHash.get(ShopifyVariantId.get(position));
                                availableQuantity2 = totalQuantity.get(ShopifyVariantId.get(position));
                                price = priceList.get(ShopifyVariantId.get(position));
                                discountPrice = discountPriceList.get(ShopifyVariantId.get(position));
                                binding.productPrice.setText(price);
                                binding.discountPrice.setText(discountPrice);
                                binding.numberOfItem.setText(getValidQuantity(1));

                                Utility.PrintLog(TAG, "selectedShopifySizeId : id " + selectedShopifyId);
                                Utility.PrintLog(TAG, "selectedShopifySizeId : quantity" + availableQuantity2);
                                Utility.PrintLog(TAG, "selectedShopifySizeId : price " + price);

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                } catch (Exception e) {
                    Utility.PrintLog(TAG, "size variant exception : " + e.toString());
                }
            });

        }
    }

    private void setCentryVariant(Variant.CentryVariant centryVariant) {


        if (centryVariant != null) {
            try {
                hideView(isCentryVariant);
                totalQuantity = new HashMap<>();
                price = centryVariant.getPrice();
                discountPrice = centryVariant.getDiscount_price();

                Utility.PrintLog(TAG, "centryVariant.getPrice()" + price);
                ArrayList<String> sizeList = new ArrayList<>();
                ArrayList<String> colorList = new ArrayList<>();
                ArrayList<String> idList = new ArrayList<>();

                binding.productPrice.setText(price);
                binding.discountPrice.setText(discountPrice);

                if (centryVariant.getCentryVariants() != null && centryVariant.getCentryVariants().size() > 0) {
                    for (int i = 0; i < centryVariant.getCentryVariants().size(); i++) {
                        Variant.CentryVariants centryObject = centryVariant.getCentryVariants().get(i);

                        if (centryObject.getSizeData() != null) {
                            sizeList.add(centryObject.getSizeData().getName());
                        } else sizeList.add("");

                        if (centryObject.getColor_data() != null) {

                            if (centryObject.getColor_data().getHexadecimal() != null && !centryObject.getColor_data().getHexadecimal().equals("")) {

                                String hexColor = centryObject.getColor_data().getHexadecimal();
                                colorList.add(hexColor);
                                Utility.PrintLog(TAG, "COLOR CODE : " + hexColor);

                            }

                        } else {
                            colorList.add("");
                        }

                        if (centryObject.getSizeData() == null && centryObject.getColor_data() == null) {
                            sizeList.add(No_VARIANT);
                            colorList.add("");
                        }

                        idList.add(centryObject.getId());
                        String quantity = centryObject.getQuantity();
                        totalQuantity.put(centryObject.getId(), quantity);
                    }

                    SpinnerAdapter adapter = new SpinnerAdapter(getContext(), sizeList, colorList);
                    binding.spinnerCentryVariant.setAdapter(adapter);
                    binding.spinnerCentryVariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (idList.size() > 0) {
                                try {
                                    selectedProductCentryId = idList.get(position);
                                    Utility.PrintLog("selectedId", " " + selectedProductCentryId);

                                    if (idList.get(position).equals(No_VARIANT)) {
                                        availableQuantity2 = totalQuantity.get(idList.get(position));
                                        binding.numberOfItem.setText(getValidQuantity(1));
                                    } else {
                                        selectedProductCentryId = idList.get(position);
                                        binding.numberOfItem.setText(getValidQuantity(1));
                                        availableQuantity2 = totalQuantity.get(idList.get(position));
                                    }


                                } catch (Exception e) {
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }

            } catch (Exception e) {
                Utility.PrintLog(TAG, "centry variant exception : " + e.toString());
            }
        }
    }

    private void setSizeColorVariant(List<Variant.SizeColorVariant> sizeColorVariant) {


        if (sizeColorVariant != null && sizeColorVariant.size() > 0) {
            isSizeColorVariant = true;
            ArrayList<String> RGBColorList = new ArrayList<>();
            ArrayList<String> productID = new ArrayList<>();
            colorIdHash = new HashMap<>();
            priceHash = new HashMap<>();
            discountPriceHash = new HashMap<>();
            totalQuantity = new HashMap<>();

            new Handler().post(() -> {
                try {
                    hideView(isColorSizeVariant);

                    price = sizeColorVariant.get(0).getPrice();
                    discountPrice = sizeColorVariant.get(0).getDiscount_price();

                    for (int i = 0; i < sizeColorVariant.size(); i++)
                    {

                        String color = sizeColorVariant.get(i).getColor_rgba();
                        String id = sizeColorVariant.get(i).getId();
                        selectedProductColorId = sizeColorVariant.get(0).getId();

                        colorIdHash.put(color, id);
                        priceHash.put(id, sizeColorVariant.get(i).getPrice());
                        discountPriceHash.put(id, sizeColorVariant.get(i).getDiscount_price());
                        RGBColorList.add(color);
                        productID.add(id);
                        colorSizeArrayList.put(id, sizeColorVariant.get(i).getSizes());

                    }

                    setColorSelectionAdapter(RGBColorList,productID);
                    setSize(sizeColorVariant.get(0).getSizes());
                    binding.productPrice.setText(price);
                    binding.discountPrice.setText(discountPrice);

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "size color variant exception : " + e.toString());
                }
            });

        }

    }

    private void setSize(List<Variant.Sizes> sizeList) {

        if (sizeList != null && sizeList.size() > 0) {

            new Handler().post(() -> {
                try {

                    ArrayList<String> SizeVariantId = new ArrayList<>();
                    sizeIdHash = new HashMap<>();

                    for (int i = 0; i < sizeList.size(); i++) {

                        String size = sizeList.get(i).getSize();
                        String id = sizeList.get(i).getId();
                        sizeIdHash.put(size, id);
                        SizeVariantId.add(size);
                        selectedProductSizeId = sizeList.get(0).getId(); // set default product color id
                        availableQuantity2 = sizeList.get(0).getQuantity();

                        Utility.PrintLog("selectedProductColorId", "default setSize () sizeList.get(0).getId( " + selectedProductColorId);
                        Utility.PrintLog(TAG, "availableQuantity2 centryVariant" + availableQuantity2);

                        String quantity = sizeList.get(i).getQuantity();
                        totalQuantity.put(size, quantity);

                    }

                    isFirstTime = true;
                    setSizeVariantSpinner(SizeVariantId);

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "exception : " + e.toString());
                }
            });

        }
    }

    private void setColorVariant(List<Variant.ColorVariant> colorVariant) {

        ArrayList<String> rgbColorList = new ArrayList<>();
        ArrayList<String> productID = new ArrayList<>();
        colorIdHash = new HashMap<>();
        priceHash = new HashMap<>();
        discountPriceHash = new HashMap<>();
        totalQuantity = new HashMap<>();

        if (colorVariant != null && colorVariant.size() > 0) {
            new Handler().post(() -> {
                try {
                    hideView(isColorVariant);

                    price = colorVariant.get(0).getPrice();
                    discountPrice = colorVariant.get(0).getDiscount_price();

                    for (int i = 0; i < colorVariant.size(); i++)
                    {

                        selectedProductColorId = colorVariant.get(0).getId();
                        Utility.PrintLog("selectedProductColorId", "setColorVariant() colorVariant.get(0).getId() " + selectedProductColorId);

                        availableQuantity2 = colorVariant.get(0).getQuantity();
                        Utility.PrintLog(TAG, "setColorVariant : " + availableQuantity2);

                        String color = colorVariant.get(i).getColor_rgba();
                        String id = colorVariant.get(i).getId();
                        String quantity = colorVariant.get(i).getQuantity();

                        Utility.PrintLog("selectedProductColorId", "colorVariant.get(i).getId() " + id);

                        totalQuantity.put(id, quantity);
                        colorIdHash.put(id, id);
                        rgbColorList.add(color);
                        productID.add(id);
                        priceHash.put(id, colorVariant.get(i).getPrice());
                        discountPriceHash.put(id, colorVariant.get(i).getDiscount_price());
                    }

                    binding.productPrice.setText(price);
                    binding.discountPrice.setText(discountPrice);
                    setColorSelectionAdapter(rgbColorList, productID);

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "size color exception : " + e.toString());
                }

            });
        }

    }

    private void setColorSelectionAdapter(ArrayList<String> RGBColorList, ArrayList<String> productId) {

        binding.rcvColorSelection.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rcvColorSelection.setAdapter(new VariantColorSelectionAdapter(RGBColorList,productId));
    }

    private void hideView(String variant) {

        if (variant.equals(isSizeVariant)) {
            binding.sizeSpinnerView.setVisibility(View.VISIBLE);
        } else if (variant.equals(isColorVariant)) {
            binding.colorVariant.setVisibility(View.VISIBLE);
        } else if (variant.equals(isColorSizeVariant)) {
            binding.sizeSpinnerView.setVisibility(View.VISIBLE);
            binding.colorVariant.setVisibility(View.VISIBLE);
        } else if (variant.equals(isCentryVariant)) {
            binding.centryVariantProduct.setVisibility(View.VISIBLE);
        }

    }

    private void setNoVariant(Variant.NoVariant noVariant) {

        price = noVariant.getPrice();
        binding.discountPrice.setText(noVariant.getDiscount_price());
        binding.productPrice.setText(noVariant.getPrice());
    }

    private void setLanguageLable() {

        if (language != null) {
            binding.btnAddToCart.setText(language.getLanguage(KEY.add_to_cart));
        }
    }


    private void setSizeVariant(Variant.SizeVariant sizeVariant) {

        if (sizeVariant != null) {

            new Handler().post(() -> {
                try {
                    if (sizeVariant.getSizes() != null && sizeVariant.getSizes().size() > 0) {
                        hideView(isSizeVariant);
                        ArrayList<String> SizeVariantId = new ArrayList<>();
                        sizeIdHash = new HashMap<>();
                        totalQuantity = new HashMap<>();

                        price = sizeVariant.getPrice();
                        discountPrice = sizeVariant.getDiscount_price();

                        for (int i = 0; i < sizeVariant.getSizes().size(); i++) {

                            String size = sizeVariant.getSizes().get(i).getSize();
                            String id = sizeVariant.getSizes().get(i).getId();
                            String quantity = sizeVariant.getSizes().get(i).getQuantity();

                            sizeIdHash.put(size, id);
                            totalQuantity.put(size, quantity);
                            SizeVariantId.add(size);

                            selectedProductSizeId = sizeVariant.getSizes().get(0).getId();
                            availableQuantity2 = sizeVariant.getSizes().get(0).getQuantity();
                            Utility.PrintLog(TAG, "setSizeVariant : " + availableQuantity2);
                        }

                        binding.productPrice.setText(price);
                        binding.discountPrice.setText(discountPrice);
                        setSizeVariantSpinner(SizeVariantId);
                    }
                } catch (Exception e) {
                    Utility.PrintLog(TAG, "size variant exception : " + e.toString());
                }
            });

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

        Map<String, String> addCard = new HashMap<>();
        addCard.put(PARAM_USER_ID, BaseActivity.baseActivity.userPreferences.getUserId());
        addCard.put(ENDPOINT_CLIP_ID, clipData.getId());
        addCard.put(ENDPOINT_PRODUCT_VARIANT, clipData.getProduct_variant());
        addCard.put(ENDPOINT_IS_CART, "1");
        addCard.put(ENDPOINT_IS_DISLIKE, "0");
        addCard.put(ENDPOINT_QUANTITY,  binding.numberOfItem.getText().toString());

        if (clipData.getProduct_variant().equals(SIZE_VARIANT)) {
            addCard.put(PARAM_PRODUCT_SIZE_ID, selectedProductSizeId);
        }

        if (clipData.getProduct_variant().equals(COLOR_VARIANT)) {
            addCard.put(PARAM_PRODUCT_COLOR_ID, selectedProductColorId);
        }

        if (clipData.getProduct_variant().equals(SIZE_COLOR_VARIANT)) {
            addCard.put(PARAM_PRODUCT_SIZE_ID, selectedProductSizeId);
            addCard.put(PARAM_PRODUCT_COLOR_ID, selectedProductColorId);
        }

        if (clipData.getProduct_variant().equals(CENTRY_VARIANT)) {
            addCard.put(PARAM_PRODUCT_CENTRY_ID, selectedProductCentryId);
        }

        if (clipData.getProduct_variant().equals(SHOPIFY_VARIANT)) {
            addCard.put(PARAM_PRODUCT_SHOPIFY_ID, selectedShopifyId);
        }

        if (checkInternetConnectionWithMessage(getContext())) {
            showLoader(true);

            Call<CommonResponse> call = apiService.doVideoClipCartOrDislike(userPreferences.getApiKey(), addCard);
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
                                mContext.addFirebaseLogEvent(Analytic.eventAdd_to_cart,Analytic.ScreenVideoClipsDetails,Analytic.btnAddToCart);
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

            if (clipData.getVariant()!=null && clipData.getVariant().getNoVariant()!=null) {
                totalQty = Integer.parseInt(clipData.getNo_of_units());
            } else {
                totalQty = Math.min(Integer.parseInt(availableQuantity2), Integer.parseInt(clipData.getNo_of_units()));
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

    @Override
    public void onClickColor(String color, int position) {



        Utility.PrintLog(TAG, "color : " + color);

        if (colorIdHash != null && colorIdHash.size() > 0) {
            String id = colorIdHash.get(color);
            Utility.PrintLog(TAG, "color id : " + id);
            selectedProductColorId = colorIdHash.get(color);
            Utility.PrintLog("selectedProductColorId", "onClickColor() color id " + selectedProductColorId);
            binding.productPrice.setText(priceHash.get(color));
            binding.discountPrice.setText(discountPriceHash.get(color));
        }

        if (isSizeColorVariant) {

            new Handler().post(() -> {
                try {

                    List<Variant.Sizes> sizeList = colorSizeArrayList.get(color);
                    setSize(sizeList);

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "size variant exception : " + e.toString());
                }
            });
        }
        binding.numberOfItem.setText(getValidQuantity(1));
    }
}
