package com.kuick.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.kuick.R;
import com.kuick.Remote.EndPoints;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.StreamerCountryList;
import com.kuick.Response.UserAddressResponse;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityAddressInformationBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.countrypicker.CountryPicker;
import com.kuick.util.regions.RegionsPicker;
import com.kuick.util.utils.AndroidBug5497Workaround;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Suppress;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.kuick.Remote.EndPoints.ENDPOINT_USER_ID;
import static com.kuick.Remote.EndPoints.PARAM_ADDRESS;
import static com.kuick.Remote.EndPoints.PARAM_ADDRESS_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_APARTMENT;
import static com.kuick.Remote.EndPoints.PARAM_CITY;
import static com.kuick.Remote.EndPoints.PARAM_COUNTRY;
import static com.kuick.Remote.EndPoints.PARAM_PHONE_NUMBER;
import static com.kuick.Remote.EndPoints.PARAM_POSTAL_CODE;
import static com.kuick.Remote.EndPoints.PARAM_REGION;
import static com.kuick.Remote.EndPoints.PARAM_SATE_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_STATE;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_ADDRESS_TYPE;
import static com.kuick.util.comman.Constants.INTENT_KEY_IS_STREAMER_ID;
import static com.kuick.util.comman.Utility.PrintLog;

public class AddressInformation extends BaseActivity {

    private final String TAG = "AddressInformation";
    private ActivityAddressInformationBinding binding;
    private String selectedAddressType = Constants.HOME;
    private String selectedCountry = null;
    private TextView txtTitle;
    private RegionsPicker regionPicker;
    private String selectedRegion = null;
    private String[] arraySpinner = null;
    private String selectedCountryCode = "+56";
    private ArrayList<String> allCountryCodeList = new ArrayList<>();
    private String seletedCountryCode = "";
    private String phoneNumber = null;
    private boolean isShopify;
    private String streamerId;
    HashMap<String,String> country_id = new HashMap();
    HashMap<String,String> count_province = new HashMap();
    HashMap<String,String> state_id = new HashMap();
    private String countryId;
    private String countProvince;
    private String stateType;
    private String stateId;
    private boolean isTrue = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        //customStatusBar();
      //  AndroidBug5497Workaround.assistActivity(this);

        //final View activityRootView = findViewById(R.id.scrollAddress);
    }
    public  float dpToPx( float valueInDp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    private void customStatusBar() {

        try {
            final View activityRootView = findViewById(R.id.mainLayout);
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                Rect r = new Rect();

                activityRootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = binding.getRoot().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    //enter your code here
                    Utility.PrintLog(TAG,"open");
                    binding.mainLayout.setFitsSystemWindows(true);
                    binding.mainLayout.requestFitSystemWindows();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                }
            });
        }catch (Exception e){
            Utility.PrintLog(TAG,"customStatusBar() Exception = " + e.toString());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    private void init() {
        getIntentData();
        setToolBar();
        getCountryCode();
        setLanguageLable();
        setCountryName();
        setRegion();
        setAddressSpinner();
        setSelectedCountryCode();

    }


    private void getIntentData() {

        if (getIntent()!=null && getIntent().getBooleanExtra(INTENT_KEY_IS_ADDRESS_TYPE,false)){
            isShopify = true;
            streamerId = getIntent().getStringExtra(INTENT_KEY_IS_STREAMER_ID);
            callStreamerCountryList(false);
            Utility.PrintLog(TAG,"isShopify");

        }else {
            binding.txtStateOptional.setVisibility(View.VISIBLE);
            callCountryList(true);
        }
    }

    private void callStreamerCountryList(boolean isShowLoader) {

        allCountriesList = new ArrayList<>();

        if (checkInternetConnectionWithMessage()) {
            if (isShowLoader)
            {
                showLoader(true);
            }

            Call<String> call = apiService.doShopifyCountry(EndPoints.API_KEY,streamerId);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    Utility.PrintLog(TAG, response.toString());

                    checkErrorCode(response.code());


                    if (response.body() != null) {

                        if (response.isSuccessful()) {
                            try {

                                JSONObject json = new JSONObject(response.body());
                                String jsonString = json.getString("streamer_countries");
                                JSONArray jsonArray = new JSONArray(jsonString);

                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    String countryName = jsonObject.getString("name");
                                    String countryId = jsonObject.getString("country_id");
                                    String countProvince = jsonObject.getString("count_province");

                                    country_id.put(countryName,countryId);
                                    count_province.put(countryName,countProvince);
                                    allCountriesList.add(countryName);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Utility.PrintLog(TAG, "json exception : " + e.toString());
                            }
                        }
                    }

                    hideLoader();
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                    Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                    Utility.PrintLog(TAG, t.toString());
                    hideLoader();
                }
            });
        }
    }

    private void setStreamerCountryListener() {


    }

    private void setSelectedCountryCode()
    {
        binding.countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allCountryCodeList!=null && allCountryCodeList.size() > 0)
                {
                    selectedCountryCode = allCountryCodeList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCountryCode()
    {

        try {

            if (checkInternetConnectionWithMessage()) {
                //showLoader(true);

                Call<String> call = apiService.doGetCountryCode(EndPoints.API_KEY);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                        if (response.body() != null)
                        {

                            if (response.isSuccessful())
                            {

                                try {

                                    JSONObject jsonObject = new JSONObject(response.body());

                                    if (jsonObject.has("status"))
                                    {
                                        String status = jsonObject.getString("status");

                                        if (status.equals(Constants.FALSE))
                                        {
                                            binding.layRegions.setVisibility(View.GONE);
                                            //hideLoader();
                                            return;
                                        }
                                    }

                                    String jsonArray = String.valueOf(jsonObject.get("country_codes"));
                                    JSONArray arrayList = new JSONArray(jsonArray);

                                    for (int i = 0; i < arrayList.length(); i++)
                                    {
                                        selectedCountryCode = String.valueOf(arrayList.get(0));
                                        String regionList = String.valueOf(arrayList.get(i));
                                        allCountryCodeList.add(regionList);

                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddressInformation.this, android.R.layout.simple_spinner_item, allCountryCodeList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    binding.countryCodeSpinner.setAdapter(adapter);
                                    binding.countryCodeSpinner.setSelection(25);
                                    seletedCountryCode = allCountryCodeList.get(25);
                                    Utility.PrintLog(TAG,"selected country - " + allCountryCodeList.get(25));

                                    hideLoader();

                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                    Utility.PrintLog(TAG, "json exception : " + e.toString());
                                }
                            }
                        }

                        //hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                        //hideLoader();
                    }
                });
            }

        } catch (Exception e)
        {
            Utility.PrintLog(TAG, e.toString());
            //hideLoader();
        }

    }

    private void setAddressSpinner()
    {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.addressSpinner.setAdapter(adapter);
        binding.addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arraySpinner!=null && arraySpinner.length > 0){
                    selectedAddressType = arraySpinner[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRegion() {
        if (regionPicker == null) {

                regionPicker = RegionsPicker.newInstance("Select Region", allRegionsList);
                regionPicker.setListener((name) -> {

                    if (isShopify)
                    {
                        binding.stateRegionProvinceName.setText(name);
                        stateId = state_id.get(name);

                    }else {

                        binding.region.setText(name);
                        selectedRegion = name;
                    }

                    regionPicker.dismiss();
                });
        }
    }


    private void setLanguageLable() {

        try {
            if (language != null) {

                Utility.PrintLog("LanguageLable"," key " + language.getLanguage(KEY.home));

                arraySpinner = new String[]{language.getLanguage(KEY.home), language.getLanguage(KEY.office)};
                txtTitle.setText(language.getLanguage(KEY.address_information));
                binding.address.setText(language.getLanguage(KEY.address_type));
                binding.txtMobile.setText(language.getLanguage(KEY.phone_number));
                binding.addressLable.setText(language.getLanguage(KEY.address));
                binding.txtApartment.setText(language.getLanguage(KEY.apartment_suite_etc_optional));
                binding.txtCity.setText(language.getLanguage(KEY.city));
                binding.txtPostalCode.setText(language.getLanguage(KEY.postal_code));
                binding.country.setText(language.getLanguage(KEY.your_country__region));
                binding.btnSaveAddress.setText(language.getLanguage(KEY.save_address));
                binding.txtRegion.setText(language.getLanguage(KEY.regions));

                if (isShopify){
                    binding.txtState.setText(language.getLanguage(KEY.state__region__province) + " ("+language.getLanguage(KEY.optional)+")");
                    binding.stateRegionProvince.setText(language.getLanguage(KEY.state__region__province));
                }else {
                    binding.txtState.setText(language.getLanguage(KEY.stateterritory_optional));
                }
            }
        } catch (Exception e) {
            Utility.PrintLog(TAG, "exception language: " + e.toString());
        }

    }

    public void openCountryPicker() {

        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");

    }

    public void callCheckCountryRegions(String countryName) {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<String> call = apiService.doCheckCountryRegions(EndPoints.API_KEY, countryName);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());

                        if (response.body() != null) {

                            if (response.isSuccessful()) {

                                try {

                                    //allCountriesList = new ArrayList<>();

                                    JSONObject jsonObject = new JSONObject(response.body());

                                    if (jsonObject.has("status"))
                                    {
                                        String status = jsonObject.getString("status");

                                            if (status.equals(Constants.FALSE))
                                            {
                                                    binding.layRegions.setVisibility(View.GONE);
                                                    hideLoader();
                                                    return;
                                            }
                                    }

                                    binding.layRegions.setVisibility(View.VISIBLE);
                                    String jsonArray = String.valueOf(jsonObject.get("data"));
                                    JSONArray arrayList = new JSONArray(jsonArray);

                                    for (int i = 0; i < arrayList.length(); i++)
                                    {
                                        selectedRegion = String.valueOf(arrayList.get(0));
                                        binding.region.setText(String.valueOf(arrayList.get(0)));
                                        String regionList = String.valueOf(arrayList.get(i));
                                        allRegionsList.add(regionList);
                                    }

                                    hideLoader();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Utility.PrintLog(TAG, "json exception : " + e.toString());
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

    private void setCountryName() {
        if (picker == null) {

               try {
                   picker = CountryPicker.newInstance("Select Country", allCountriesList);
                   picker.setListener((name) ->
                   {
                       binding.countyName.setText(name);
                       picker.dismiss();

                       if (isShopify){

                           countryId = country_id.get(name);
                           selectedCountry = country_id.get(name);
                           countProvince = count_province.get(name);

                           if (countProvince != null && !countProvince.equals("0")) {
                               callCountryProvince(true,countryId);
                               binding.inputStateRegionProvince.setVisibility(View.VISIBLE);
                               binding.stateRegionProvinceName.setText("");
                               binding.txtStateOptional.setVisibility(View.GONE);
                               stateType = "id";
                           }else {
                               binding.txtStateOptional.setVisibility(View.VISIBLE);
                               binding.state.setText("");
                               binding.inputStateRegionProvince.setVisibility(View.GONE);
                               stateType = "direct";
                           }

                       }else {

                           selectedCountry = name;

                           if(name!=null && name.equals("Chile"))
                           {
                               binding.layRegions.setVisibility(View.VISIBLE);
                           }else {
                               binding.layRegions.setVisibility(View.GONE);
                           }

                           if (allRegionsList!=null && allRegionsList.size() > 0)
                           {
                               selectedRegion = String.valueOf(allRegionsList.get(0));
                               binding.region.setText(String.valueOf(allRegionsList.get(0)));
                               return;
                           }
                           callCheckCountryRegions(selectedCountry);
                       }

                   });
               }catch (Exception e){

               }
        }
    }

    private void callCountryProvince(boolean isShowLoader, String countryId) {


        if (checkInternetConnectionWithMessage()) {
            if (isShowLoader)
            {
                showLoader(true);
            }

            allRegionsList.clear();
            Call<String> call = apiService.doProvince(EndPoints.API_KEY,streamerId,countryId);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    Utility.PrintLog(TAG, response.toString());
                    checkErrorCode(response.code());

                    if (response.body() != null) {

                        if (response.isSuccessful()) {
                            try {

                                JSONObject json = new JSONObject(response.body());
                                String jsonString = json.getString("streamer_provinces");
                                JSONArray jsonArray = new JSONArray(jsonString);

                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    String name = jsonObject.getString("name");
                                    String stateid = jsonObject.getString("province_id");

                                    state_id.put(name,stateid);
                                    allRegionsList.add(name);

                                    //set default
                                    if (i == 0){
                                        binding.stateRegionProvinceName.setText(name);
                                        stateId = stateid;
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Utility.PrintLog(TAG, "json exception : " + e.toString());
                            }
                        }
                    }

                    hideLoader();
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                    Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                    Utility.PrintLog(TAG, t.toString());
                    hideLoader();
                }
            });
        }

    }


    private void setToolBar() {

        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        ImageView btnBack = binding.getRoot().findViewById(R.id.img_back);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.btnSaveAddress).setOnClickListener(this);

        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        binding.inputStateRegionProvince.setOnClickListener(this);
        binding.inputCountry.setOnClickListener(this);
        binding.layRegions.setOnClickListener(this);
        binding.btnRagion.setOnClickListener(this);
        binding.txtRegion.setOnClickListener(this);
        binding.region.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.layRegions:
            case R.id.btnRagion:
            case R.id.txtRegion:
            case R.id.region:
            case R.id.inputStateRegionProvince:
                openRagionsPicker();
                break;
            case R.id.inputCountry:
                if (allCountriesList!=null && allCountriesList.size() > 0)
                {
                    openCountryPicker();
                }

                break;
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.btnSaveAddress:
                onClickSaveAddress();
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
        }
    }

    private void openRagionsPicker() {
        Utility.PrintLog(TAG,"country list side = " + allRegionsList.size());
        regionPicker.show(getSupportFragmentManager(), "REGIONS_PICKER");
    }

    private void onClickSaveAddress() {

        String edtMobileNumber = binding.edtMobileNumber.getText().toString();
        String fullAddress = binding.fullAddress.getText().toString();
        String city = binding.cityName.getText().toString();
        String postalCode = binding.postalCode.getText().toString();
        String apartment = binding.apartment.getText().toString();
        String state = binding.state.getText().toString();

        try {

            if (checkInternetConnectionWithMessage()) {

                try {

                    if (language != null) {

                        if (TextUtils.isEmpty(edtMobileNumber)) {
                            showSnackErrorMessage(language.getLanguage(KEY.please_enter_phone_number));
                            return;

                        } if (edtMobileNumber.length() < 6)
                        {
                            showSnackErrorMessage(language.getLanguage(KEY.please_enter_proper_phone_number));
                            return;

                        } else {

                            phoneNumber = selectedCountryCode + edtMobileNumber;
                        }

                        if (TextUtils.isEmpty(fullAddress)) {
                            showSnackErrorMessage(language.getLanguage(KEY.address_must_required));
                            return;
                        }
                        if (TextUtils.isEmpty(city)) {
                            showSnackErrorMessage(language.getLanguage(KEY.city_must_required));
                            return;
                        }
                        if (TextUtils.isEmpty(postalCode)) {
                            showSnackErrorMessage(language.getLanguage(KEY.postal_code_must_required));
                            return;
                        }
                        if (selectedCountry == null) {
                            showSnackErrorMessage(language.getLanguage(KEY.country_must_required));
                            return;
                        }

                        if (selectedAddressType == null)
                        {
                            showSnackErrorMessage(language.getLanguage(KEY.address_type_must_required));
                            return;
                        }

                        if (isShopify && stateType == null){
                            showSnackErrorMessage(language.getLanguage(KEY.please_select_state__region__province));
                            return;
                        }

                    }

                } catch (Exception e) {
                    Utility.PrintLog(TAG, "Exception set error message : " + e.toString());
                    return;

                }


                Map<String, String> addAddress = new HashMap<>();

                addAddress.put(ENDPOINT_USER_ID, userPreferences.getUserId());
                addAddress.put(PARAM_ADDRESS_TYPE, selectedAddressType);
                addAddress.put(PARAM_ADDRESS, fullAddress);
                addAddress.put(PARAM_APARTMENT, apartment);
                addAddress.put(PARAM_CITY, city);

                if (!isShopify){
                    addAddress.put(PARAM_STATE, state);
                }else {

                    if (stateType.equals("direct")){
                        addAddress.put(PARAM_STATE, state);
                    }else {
                        addAddress.put(PARAM_STATE, stateId);
                    }

                    addAddress.put(PARAM_SATE_TYPE, stateType);
                }
                addAddress.put(PARAM_POSTAL_CODE, postalCode);
                addAddress.put(PARAM_COUNTRY, selectedCountry);

                if (phoneNumber == null)
                {
                    showSnackErrorMessage(language.getLanguage(KEY.please_enter_phone_number));
                    return;
                }

                addAddress.put(PARAM_PHONE_NUMBER, phoneNumber);

                if (selectedRegion != null)

                {
                    addAddress.put(PARAM_REGION, selectedRegion);
                }

                showLoader(true);

                Utility.PrintLog(TAG,"addAddressInfo" + addAddress);

                Call<UserAddressResponse> call = null;

                if (isShopify){
                    call = apiService.doAddShopifyAddress(userPreferences.getApiKey(), addAddress);
                }else {
                    call = apiService.doAddAddress(userPreferences.getApiKey(), addAddress);
                }

                call.enqueue(new Callback<UserAddressResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<UserAddressResponse> call, @NotNull Response<UserAddressResponse> response) {
                        PrintLog(TAG, "response : " + response);

                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful())
                            {
                                userPreferences.setAddressId(response.body().getId());
                                onBackPressed();

                            } else {
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<UserAddressResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            hideLoader();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}