package com.kuick.activity;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.kuick.R;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.ProductDetails;
import com.kuick.Response.Variant;
import com.kuick.adapter.ProductImagePagerAdapter;
import com.kuick.adapter.SpinnerAdapter;
import com.kuick.adapter.VariantColorSelectionAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityProductDetailsBinding;
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

import static com.kuick.Remote.EndPoints.PARAM_EVENT_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_CENTRY_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_COLOR_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_QTY;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_SHOPIFY_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_SIZE_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
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

public class ProductDetailsActivity extends BaseActivity implements ColorSelectionListener {

    public static  ProductDetailsActivity productDetailsActivity;
    public static ColorSelectionListener colorSelectionListener;
    private final String TAG = "ProductDetails";
    public String availableQuantity2 = "0";
    private ActivityProductDetailsBinding binding;
    private TextView txtTitle;
    private ImageView btnBack;
    private final int numberOfItem = 1;
    private CommonResponse productDetails;
    private String productId;
    private String productVariantType;
    private String selectedProductSizeId;
    private String selectedShopifyId;
    private String selectedProductColorId;
    private String selectedProductCentryId = "";
    private Map<String, String> colorIdHash, sizeIdHash, priceHash, totalQuantity;
    private String price;
    private boolean isSizeColorVariant;
    private final HashMap<String, List<Variant.Sizes>> colorSizeArrayList = new HashMap<>();
    private String productCurrencyCode;
    private boolean isFirstTime = false;
    private HashMap<String, String> shopifyIdHash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setToolBart();
        callProductDetailAPI();
        setLanguageLable();

    }

    private void setProductImageAdapter(String imageUrl, List<CommonResponse.ProductImages> productImage, boolean isSingleImage) {

        binding.productImagePager.setAdapter(new ProductImagePagerAdapter(imageUrl, productImage, isSingleImage));
        binding.productImagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int SCROLLING_RIGHT = 0;
            int SCROLLING_LEFT = 1;
            int SCROLLING_UNDETERMINED = 2;

            int currentScrollDirection = 2;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                if (isScrollDirectionUndetermined()) {
                    setScrollingDirection(positionOffset);
                }

                if (isScrollingLeft()) {
                    Utility.PrintLog("ViewPager", "Scrolling LEFT");

                    int tab = binding.productImagePager.getCurrentItem();
                    if (tab == 0) {
                        binding.vpButtonLeft.setVisibility(View.GONE);
                    }
                    binding.vpButtonRight.setVisibility(View.VISIBLE);

                }
                if (isScrollingRight()) {
                    Utility.PrintLog("ViewPager", "Scrolling RIGHT");

                    int tab = binding.productImagePager.getCurrentItem();
                    binding.vpButtonLeft.setVisibility(View.VISIBLE);

                    if (tab == (binding.productImagePager.getAdapter().getCount() - 1))
                        binding.vpButtonRight.setVisibility(View.GONE);
                }


            }

            private void setScrollingDirection(float positionOffset){
                if ((1-positionOffset)>= 0.5){
                    this.currentScrollDirection = SCROLLING_RIGHT;
                }
                else if ((1-positionOffset)<= 0.5){
                    this.currentScrollDirection =  SCROLLING_LEFT;
                }
            }

            private boolean isScrollDirectionUndetermined(){
                return currentScrollDirection == SCROLLING_UNDETERMINED;
            }

            private boolean isScrollingRight(){
                return currentScrollDirection == SCROLLING_RIGHT;
            }

            private boolean isScrollingLeft(){
                return currentScrollDirection == SCROLLING_LEFT;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE){
                    this.currentScrollDirection = SCROLLING_UNDETERMINED;
                }
            }
        });
    }

    private void setColorSelectionAdapter(ArrayList<String> RGBColorList, ArrayList<String> productID) {

        binding.rcvColorSelection.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.rcvColorSelection.setAdapter(new VariantColorSelectionAdapter(RGBColorList, productID));

    }

    private void setLanguageLable() {

        try {
            if (language != null) {
                txtTitle.setText(language.getLanguage(KEY.product_detail));
                binding.btnAddToCart.setText(language.getLanguage(KEY.add_to_cart));
                binding.txtQuantity.setText(language.getLanguage(KEY.quantity));


            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }

    }

    private int getRgbColor(String rgb) {
        try {
            String[] strings = rgb.split(", ");
            int R = Integer.parseInt(strings[0]);
            int G = Integer.parseInt(strings[1]);
            int B = Integer.parseInt(strings[2]);
            int A = Integer.parseInt(strings[3]);
            Utility.PrintLog(TAG, "color 0 : " + strings[0] + " color 1 :" + strings[1] + " color 2 :" + strings[2]);
            return Color.rgb(R, G, B);

        } catch (Exception e) {
            Utility.PrintLog(TAG, "RGB excpetion() " + e.toString());
        }
        return 0;
    }

    private void setSizeVariantSpinner(ArrayList<String> sizeVariantId) {


        if (sizeVariantId != null && sizeVariantId.size() > 0) {

            try {
                Utility.PrintLog(TAG, "sizeVariantId" + sizeVariantId);
                ArrayAdapter listAdapter = new ArrayAdapter(ProductDetailsActivity.this, R.layout.spinner_item, sizeVariantId);
                listAdapter.setDropDownViewResource(R.layout.size_spinner_item);
                binding.sizeListSpinner.setAdapter(listAdapter);

                binding.sizeListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (isFirstTime) {
                            isFirstTime = false;
                            return;
                        }
                        Utility.PrintLog(TAG, "selectedProductSizeId : " + sizeVariantId.get(position));
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

    private void callProductDetailAPI() {

        try {

            if (getIntent() != null && getIntent().getStringExtra(PARAM_PRODUCT_ID) != null) {
                productId = getIntent().getStringExtra(PARAM_PRODUCT_ID);
                Utility.PrintLog(TAG, "product id : " + productId);
            }

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<CommonResponse> call = apiService.doProductDetails(userPreferences.getApiKey(), productId,userPreferences.getUserId());
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            //hideShowView(binding.dataNotFound, getString(R.string.data_not_found));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (checkResponseStatusWithMessage(response.body(), true))
                            {
                                hideShowView(binding.dataView, null);
                                setResponseData(response.body());
                            } else {
                                hideShowView(binding.dataNotFound, language.getLanguage(response.body().getMessage()));
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
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

    @SuppressLint("SetTextI18n")
    private void setResponseData(CommonResponse response) {
        productDetails = response;

        if (response != null) {

            try {

                if (response.getProductDetails() != null) {
                    setProductDetail(response.getProductDetails());
                }

                if (response.getProductImages() != null) {
                    setProductsImage(response);
                }

                if (response.getVariant() != null) {

                    if (response.getVariant().getNoVariant() != null) {
                        setNoVariant(response.getVariant().getNoVariant());
                    }

                    if (response.getVariant().getSizeVariant() != null) {
                        setSizeVariant(response.getVariant().getSizeVariant());
                    }

                    if (response.getVariant().getColorVariant() != null) {
                        setColorVariant(response.getVariant().getColorVariant());
                    }

                    if (response.getVariant().getSizeColorVariant() != null) {
                        setSizeColorVariant(response.getVariant().getSizeColorVariant());
                    }

                    if (response.getVariant().getCentryVariant() != null) {
                        setCentryVariant(response.getVariant().getCentryVariant());
                    }

                    if (response.getVariant().getShopify_variant()!=null)
                    {
                        setShopifyVariant(response.getVariant().getShopify_variant());
                    }

                    Utility.PrintLog(TAG, "variant name : " + response.getProductDetails().getProduct_type());
                }


            } catch (Exception e) {
                Utility.PrintLog(TAG, "Exception : " + e.toString());

            }

        } //else hideShowView(binding.dataNotFound, getString(R.string.data_not_found));

    }

    private void setShopifyVariant(List<Variant.ShopifyVariant> shopify_variant)
    {
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

                        price = shopify_variant.get(0).getPrice();

                        for (int i = 0; i < shopify_variant.size(); i++)
                        {

                            String title = shopify_variant.get(i).getTitle();
                            String id = shopify_variant.get(i).getId();
                            String quantity = shopify_variant.get(i).getQuantity();
                            String price = shopify_variant.get(i).getPrice();

                            shopifyIdHash.put(title, id);
                            totalQuantity.put(title, quantity);
                            priceList.put(title, price);
                            ShopifyVariantId.add(title);

                            selectedShopifyId = shopify_variant.get(0).getId();
                            availableQuantity2 = shopify_variant.get(0).getQuantity();
                            Utility.PrintLog(TAG, "shopify_variant : " + availableQuantity2);
                        }


                        binding.productPrice.setText(price);

                        Utility.PrintLog(TAG, "shopify_variant" + shopify_variant);
                        ArrayAdapter listAdapter = new ArrayAdapter(ProductDetailsActivity.this, R.layout.spinner_item, ShopifyVariantId);
                        listAdapter.setDropDownViewResource(R.layout.size_spinner_item);
                        binding.shopifyListSpinner.setAdapter(listAdapter);

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
                                binding.productPrice.setText(price);
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
            new Handler().post(() -> {
                try {
                    hideView(isCentryVariant);
                    totalQuantity = new HashMap<>();
                    price = centryVariant.getPrice();
                    Utility.PrintLog(TAG, "centryVariant.getPrice()" + price);
                    ArrayList<String> sizeList = new ArrayList<>();
                    ArrayList<String> colorList = new ArrayList<>();
                    ArrayList<String> idList = new ArrayList<>();

                    binding.productPrice.setText(price);

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

                        SpinnerAdapter adapter = new SpinnerAdapter(this, sizeList, colorList);
                        binding.spinnerCentryVariant.setAdapter(adapter);
                        binding.spinnerCentryVariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (idList != null && idList.size() > 0) {
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
            });
        }

    }


    private void setSizeColorVariant(List<Variant.SizeColorVariant> sizeColorVariant) {

        if (sizeColorVariant != null && sizeColorVariant.size() > 0) {
            isSizeColorVariant = true;
            ArrayList<String> RGBColorList = new ArrayList<>();
            ArrayList<String> productId = new ArrayList<>();
            colorIdHash = new HashMap<>();
            priceHash = new HashMap<>();
            totalQuantity = new HashMap<>();

            new Handler().post(() -> {
                try {
                    hideView(isColorSizeVariant);

                    price = sizeColorVariant.get(0).getPrice();

                    for (int i = 0; i < sizeColorVariant.size(); i++)
                    {

                        String color = sizeColorVariant.get(i).getColor_rgba();
                        String id = sizeColorVariant.get(i).getId();
                        selectedProductColorId = sizeColorVariant.get(0).getId();

                        price = sizeColorVariant.get(0).getPrice(); // set default price
                        Utility.PrintLog("selectedProductColorId","sizeColorVariant id - " + id);

                        colorIdHash.put(id, id);
                        priceHash.put(id, sizeColorVariant.get(i).getPrice());
                        RGBColorList.add(color);
                        productId.add(id);
                        colorSizeArrayList.put(id, sizeColorVariant.get(i).getSizes());

                    }

                    setColorSelectionAdapter(RGBColorList,productId);
                    setSize(sizeColorVariant.get(0).getSizes());
                    binding.productPrice.setText(price);

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "size color variant exception : " + e.toString());
                }
            });

        }
    }

    private void setColorVariant(List<Variant.ColorVariant> colorVariant) {

        ArrayList<String> rgbColorList = new ArrayList<>();
        ArrayList<String> productId = new ArrayList<>();
        colorIdHash = new HashMap<>();
        priceHash = new HashMap<>();
        totalQuantity = new HashMap<>();

        if (colorVariant != null && colorVariant.size() > 0) {
            new Handler().post(() -> {
                try {
                    hideView(isColorVariant);

                    price = colorVariant.get(0).getPrice();

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
                        productId.add(id);
                        priceHash.put(id, colorVariant.get(i).getPrice());
                    }

                    binding.productPrice.setText(price);
                    setColorSelectionAdapter(rgbColorList,productId);

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "size color exception : " + e.toString());
                }

            });
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
                        setSizeVariantSpinner(SizeVariantId);
                    }
                } catch (Exception e) {
                    Utility.PrintLog(TAG, "size variant exception : " + e.toString());
                }
            });

        }

    }

    private void setNoVariant(Variant.NoVariant noVariant) {
        price = noVariant.getPrice();
        binding.productPrice.setText(noVariant.getPrice());
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

    private void setProductsImage(CommonResponse response) {
        List<CommonResponse.ProductImages> productImage = response.getProductImages();

        if (productImage != null && productImage.size() > 0) {
            binding.vpButtonLeft.setVisibility(View.VISIBLE);
            binding.vpButtonRight.setVisibility(View.VISIBLE);
            setProductImageAdapter(response.getProductDetails().getImage(), productImage, false);
        } else {
            binding.vpButtonLeft.setVisibility(View.GONE);
            binding.vpButtonRight.setVisibility(View.GONE);
        }


    }

    private void setProductDetail(ProductDetails productDetails) {

        productVariantType = productDetails.getProduct_type();
        productCurrencyCode = productDetails.getCurrency_code();
        availableQuantity2 = productDetails.getQuantity();
        binding.productPrice.setText(productDetails.getPrice());

        if (productDetails.getImage() != null) {
            setProductImageAdapter(productDetails.getImage(), null, true);
        }

        binding.productName.setText(productDetails.getName());
        binding.txtDescription.setText(productDetails.getDescription());

        if (productDetails.getInventory_location() != null && !productDetails.getInventory_location().equals("null")) {
            binding.location.setText(language.getLanguage(KEY.inventory_location) + " : " + productDetails.getInventory_location());
        }
    }

    private void setToolBart() {
        productDetailsActivity = this;
        colorSelectionListener = this;
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);

        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);
        binding.btnAddToCart.setOnClickListener(this);
        binding.btnMinus.setOnClickListener(this);
        binding.btnPluse.setOnClickListener(this);
        binding.vpButtonLeft.setOnClickListener(this);
        binding.vpButtonRight.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
            case R.id.btnAddToCart:
                onClickAddToCart();
                break;
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.btnMinus:

                try {

                    int quan = Integer.parseInt(binding.numberOfItem.getText().toString());

                    if (quan == 1) {
                        return;
                    }

                    quan = quan - 1;
                    binding.numberOfItem.setText(getValidQuantity(quan));


                } catch (Exception e) {
                    Utility.PrintLog(TAG, "minus() Exception " + e.toString());
                }

                break;
            case R.id.btnPluse:
                try {

                    int qty = Integer.parseInt(binding.numberOfItem.getText().toString());
                    int totalQty = Integer.parseInt(availableQuantity2);

                    if (qty == totalQty) {
                        showSnackErrorMessage(language.getLanguage(KEY.quantity_is_not_available_for_this_product));
                        return;
                    }

                    qty = qty + 1;
                    binding.numberOfItem.setText(getValidQuantity(qty));

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "plus() Exception " + e.toString());
                }

                break;
            case R.id.vpButtonLeft:

                buttonLeftSwipe();

                break;

            case R.id.vpButtonRight:

                buttonRightSwipe();
                    break;
        }
    }

    private void buttonRightSwipe() {

        int tab = binding.productImagePager.getCurrentItem();
        tab++;
        binding.productImagePager.setCurrentItem(tab);
        binding.vpButtonLeft.setVisibility(View.VISIBLE);

        if (tab == (binding.productImagePager.getAdapter().getCount() - 1))
            binding.vpButtonRight.setVisibility(View.GONE);
    }

    private void buttonLeftSwipe() {

        int tab = binding.productImagePager.getCurrentItem();
        if (tab > 0) {
            tab--;
            binding.productImagePager.setCurrentItem(tab);
        } else if (tab == 0) {
            binding.productImagePager.setCurrentItem(tab);
        }
        if (tab == 0) {
            binding.vpButtonLeft.setVisibility(View.GONE);
        }
        binding.vpButtonRight.setVisibility(View.VISIBLE);
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

    private void onClickAddToCart() {

        try {

            Map<String, String> addCard = new HashMap<>();


            addCard.put(PARAM_USER_ID, userPreferences.getUserId());
            addCard.put(PARAM_PRODUCT_ID, productDetails.getProductDetails().getId());
            addCard.put(PARAM_PRODUCT_TYPE, productDetails.getProductDetails().getProduct_type());
            addCard.put(PARAM_PRODUCT_QTY, binding.numberOfItem.getText().toString());
            addCard.put(PARAM_EVENT_ID, LiveActivity.eventId);

            if (productVariantType.equals(SIZE_VARIANT)) {
                addCard.put(PARAM_PRODUCT_SIZE_ID, selectedProductSizeId);
            }

            if (productVariantType.equals(COLOR_VARIANT)) {
                addCard.put(PARAM_PRODUCT_COLOR_ID, selectedProductColorId);
            }

            if (productVariantType.equals(SIZE_COLOR_VARIANT)) {
                addCard.put(PARAM_PRODUCT_SIZE_ID, selectedProductSizeId);
                addCard.put(PARAM_PRODUCT_COLOR_ID, selectedProductColorId);
            }

            if (productVariantType.equals(CENTRY_VARIANT)) {
                addCard.put(PARAM_PRODUCT_CENTRY_ID, selectedProductCentryId);
            }

            if (productVariantType.equals(SHOPIFY_VARIANT)) {
                addCard.put(PARAM_PRODUCT_SHOPIFY_ID, selectedShopifyId);
            }

            Utility.PrintLog(TAG, "Product data : " + addCard);
            if (checkInternetConnectionWithMessage()) {
                showLoader(true);
                Call<BaseResponse> call = apiService.doAddToCart(userPreferences.getApiKey(), addCard);

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
                                addFirebaseLogEvent(Analytic.eventAdd_to_cart,Analytic.ScreenProductDetail,Analytic.btnAddToCart);
                                goToNextScreen(ProductDetailsActivity.this, CartPageActivity.class, true);

                            } else
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                        }
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                        showSnackResponse(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        Utility.PrintLog(TAG, t.toString());
                        hideLoader();
                    }
                });
            } else hideLoader();

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            showSnackResponse(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }
    }

    public void hideShowView(View view, String message) {
        binding.dataNotFound.setVisibility(View.GONE);
        binding.dataView.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        TextView error = binding.getRoot().findViewById(R.id.errorMessage);
        error.setText(message);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClickColor(String color, int position) {

        Utility.PrintLog(TAG, "color : " + color);

        if (colorIdHash != null && colorIdHash.size() > 0) {
            String id = colorIdHash.get(color);
            Utility.PrintLog(TAG, "color id : " + id);
            selectedProductColorId = colorIdHash.get(color);
            Utility.PrintLog("selectedProductColorId", "onClickColor() color id " + selectedProductColorId);
            binding.productPrice.setText(priceHash.get(color));
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

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}