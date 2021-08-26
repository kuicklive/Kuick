package com.kuick.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.kuick.Response.CommonResponse;
import com.kuick.model.UserDetail;
import com.kuick.util.comman.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import static com.kuick.util.comman.Constants.SHARED_PREFERENCE_KUICK;

public class UserPreferences {

    public static final String PREF_USER_ID = "id";
    public static final String PREF_USER_FIRST_NAME = "first_name";
    public static final String PREF_USER_LAST_NAME = "last_name";
    public static final String PREF_USER_EMAIL = "email";
    public static final String PREF_USER_PASSWORD = "password";
    public static final String PREF_USER_DATE_OF_BIRTH = "date_of_birth";
    public static final String PREF_USER_GENDER = "gender";
    public static final String PREF_USER_MOBILE_NUMBER = "mobile_number";
    public static final String PREF_USER_COUNTRY = "country";
    public static final String PREF_USER_CURRENCY_CODE = "currency_code";
    public static final String PREF_USER_CURRENCY_SYMBOL = "currency_symbol";
    public static final String PREF_USER_PROFILE_IMAGE = "profile_image";
    public static final String PREF_USER_SOCIAL_TYPE = "social_type";
    public static final String PREF_USER_SOCIAL_ID = "social_id";
    public static final String PREF_USER_REMEMBER_TOKEN = "remember_token";
    public static final String PREF_USER_DEVICE_TYPE = "device_type";
    public static final String PREF_USER_DEVICE_TOKEN = "device_token";
    public static final String PREF_USER_CREATED_BY = "created_by";
    public static final String PREF_USER_CREATED_AT = "created_at";
    public static final String PREF_USER_UPDATED_BY = "updated_by";
    public static final String PREF_USER_UPDATED_AT = "updated_at";
    public static final String PREF_USER_STATUS = "status";
    public static final String PREF_USER_TRASH = "trash";
    public static final String PREF_USER_FULL_NAME = "full_name";
    public static final String PREF_USER_API_KEY = "x-api-key";
    public static final String PREF_USER_ADDRESS_ID = "address_id";
    public static final String PREF_USER_TIME_ZONE = "time_zone";
    public static final String PREF_USER_STRIPE_SECRET_KEY = "stripe_secretkey";
    public static final String PREF_USER_STRIPE_PUBLISH_KEY = "stripe_publishablekey";
    public static final String PREF_CURRENT_TIME_FROM_SERVER = "current_time_from_server";
    public static final String PREF_CURRENT_DATE_AND_TIME = "current_date_and_time";
    public static final String PREF_IMAGE_BASE_URL = "setImage_base_url";
    public static final String PREF_TOTAL_CART = "total_cart";
    public static final String PREF_TAX_STATUS = "tax_status";
    public static final String PREF_CURRENCY_CODE_VALUE = "currency_code_value";
    public static final String PREF_RANDOM_USER_ID = "random_user_id";
    public static final String PREF_TAX_TYPE = "tax_type";
    public static final String PREF_TAX_AMOUNT = "tax_amount";
    public static final String PREF_KUICKER_ID = "kuicker_id";
    public static final String PREF_BACK_CLICKS = "BACK_CLICKS";
    private static UserPreferences instance;
    public final SharedPreferences prefs;
    String TAG = "UserPreferences";

    public UserPreferences(Context context) {
        prefs = context.getSharedPreferences(
                SHARED_PREFERENCE_KUICK,
                Context.MODE_PRIVATE
        );
    }

    public static UserPreferences newInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferences(context);
        }
        return instance;
    }


    public String getBackClicks() {
        return prefs.getString(PREF_BACK_CLICKS, "0");
    }

    public void setBackClicks(String size) {
        prefs.edit().putString(PREF_BACK_CLICKS, size).apply();
    }
    public String getTaxAmount() {
        return prefs.getString(PREF_TAX_AMOUNT, null);
    }

    public void setTaxAmount(String size) {
        prefs.edit().putString(PREF_TAX_AMOUNT, size).apply();
    }

    public String getTaxType() {
        return prefs.getString(PREF_TAX_TYPE, null);
    }

    public void setTaxType(String size) {
        prefs.edit().putString(PREF_TAX_TYPE, size).apply();
    }

    public String getTaxStatus() {
        return prefs.getString(PREF_TAX_STATUS, null);
    }

    public void setTaxStatus(String size) {
        prefs.edit().putString(PREF_TAX_STATUS, size).apply();
    }

    public String getTotalCartSize() {
        return prefs.getString(PREF_TOTAL_CART, null);
    }

    public void setTotalCartSize(String size) {
        prefs.edit().putString(PREF_TOTAL_CART, size).apply();
    }

    public void setImageBaseUrl(String url) {
        prefs.edit().putString(PREF_IMAGE_BASE_URL, url).apply();
    }

    public String getImageUrl() {
        return prefs.getString(PREF_IMAGE_BASE_URL, null);
    }

    public String getRandomUserId() {
        return prefs.getString(PREF_RANDOM_USER_ID, null);
    }

    public void setRandomUserId(String userId) {
        prefs.edit().putString(PREF_RANDOM_USER_ID, userId).apply();
    }

    public String getCurrentTime() {
        return prefs.getString(PREF_CURRENT_TIME_FROM_SERVER, null);
    }
    public String getCurrentDateAndTime() {
        return prefs.getString(PREF_CURRENT_DATE_AND_TIME, null);
    }

    public void setCurrentDateAndTime(String currentTime) {
        prefs.edit().putString(PREF_CURRENT_DATE_AND_TIME, currentTime).apply();
    }

    public void setCurrentTime(String currentTime) {
        prefs.edit().putString(PREF_CURRENT_TIME_FROM_SERVER, currentTime).apply();
    }

    public String getStripeKey() {
        return prefs.getString(PREF_USER_STRIPE_SECRET_KEY, null);
    }

    public void setStripeKey(String stripeKey) {
        prefs.edit().putString(PREF_USER_STRIPE_SECRET_KEY, stripeKey).apply();
    }

    public String getStripePublishKey() {
        return prefs.getString(PREF_USER_STRIPE_PUBLISH_KEY, null);
    }

    public void setStripePublishKey(String publishKey) {
        prefs.edit().putString(PREF_USER_STRIPE_PUBLISH_KEY, publishKey).apply();
    }

    public String getTimezone() {
        return prefs.getString(PREF_USER_TIME_ZONE, null);
    }

    public void setTimeZone(String timezone) {
        prefs.edit().putString(PREF_USER_TIME_ZONE, timezone).apply();
    }

    public String getAddressId() {
        return prefs.getString(PREF_USER_ADDRESS_ID, null);
    }

    public String getKuickerId() {
        return prefs.getString(PREF_KUICKER_ID, null);
    }

    public void setKuickerId(String addressId) {
        prefs.edit().putString(PREF_KUICKER_ID, addressId).apply();
    }

    public void setAddressId(String addressId) {
        prefs.edit().putString(PREF_USER_ADDRESS_ID, addressId).apply();
    }

    public String getUserId() {
        return prefs.getString(PREF_USER_ID, null);
    }

    public void setUserId(String id) {
        prefs.edit().putString(PREF_USER_ID, id).apply();
    }

    public String getFirstName() {
        return prefs.getString(PREF_USER_FIRST_NAME, null);
    }

    public void setFirstName(String id) {
        prefs.edit().putString(PREF_USER_FIRST_NAME, id).apply();
    }

    public String getLastName() {
        return prefs.getString(PREF_USER_LAST_NAME, null);
    }

    public void setLastName(String id) {
        prefs.edit().putString(PREF_USER_LAST_NAME, id).apply();
    }

    public String getEmail() {
        return prefs.getString(PREF_USER_EMAIL, null);
    }

    public void setEmail(String id) {
        prefs.edit().putString(PREF_USER_EMAIL, id).apply();
    }

    public String getPassword() {
        return prefs.getString(PREF_USER_PASSWORD, null);
    }

    public void setPassword(String id) {
        prefs.edit().putString(PREF_USER_PASSWORD, id).apply();
    }

    public String getDateOfBirth() {
        return prefs.getString(PREF_USER_DATE_OF_BIRTH, null);
    }

    public void setDateOfBirth(String id) {
        prefs.edit().putString(PREF_USER_DATE_OF_BIRTH, id).apply();
    }

    public String getGender() {
        return prefs.getString(PREF_USER_GENDER, null);
    }

    public void setGender(String id) {
        prefs.edit().putString(PREF_USER_GENDER, id).apply();
    }

    public String getMobileNumber() {
        return prefs.getString(PREF_USER_MOBILE_NUMBER, null);
    }

    public void setMobileNumber(String id) {
        prefs.edit().putString(PREF_USER_MOBILE_NUMBER, id).apply();
    }

    public String getCountry() {
        return prefs.getString(PREF_USER_COUNTRY, null);
    }

    public void setCountry(String id) {
        prefs.edit().putString(PREF_USER_COUNTRY, id).apply();
    }

    public String getCurrencyCode() {
        return prefs.getString(PREF_USER_CURRENCY_CODE, null);
    }

    public void setCurrencyCode(String id) {
        prefs.edit().putString(PREF_USER_CURRENCY_CODE, id).apply();
    }

    public String getCurrencySymbol() {
        return prefs.getString(PREF_USER_CURRENCY_SYMBOL, null);
    }

    public void setCurrencySymbol(String id) {
        prefs.edit().putString(PREF_USER_CURRENCY_SYMBOL, id).apply();
    }

    public String getProfileImage() {
        return prefs.getString(PREF_USER_PROFILE_IMAGE, null);
    }

    public void setProfileImage(String id) {
        prefs.edit().putString(PREF_USER_PROFILE_IMAGE, id).apply();
    }

    public String getSocialType() {
        return prefs.getString(PREF_USER_SOCIAL_TYPE, null);
    }

    public void setSocialType(String id) {
        prefs.edit().putString(PREF_USER_SOCIAL_TYPE, id).apply();
    }

    public String getSocialId() {
        return prefs.getString(PREF_USER_SOCIAL_ID, null);
    }

    public void setSocialId(String id) {
        prefs.edit().putString(PREF_USER_SOCIAL_ID, id).apply();
    }

    public String getRememberToken() {
        return prefs.getString(PREF_USER_REMEMBER_TOKEN, null);
    }

    public void setRememberToken(String id) {
        prefs.edit().putString(PREF_USER_REMEMBER_TOKEN, id).apply();
    }

    public String getDeviceType() {
        return prefs.getString(PREF_USER_DEVICE_TYPE, null);
    }

    public void setDeviceType(String id) {
        prefs.edit().putString(PREF_USER_DEVICE_TYPE, id).apply();
    }

    public String getDeviceToken() {
        return prefs.getString(PREF_USER_DEVICE_TOKEN, null);
    }

    public void setDeviceToken(String id) {
        prefs.edit().putString(PREF_USER_DEVICE_TOKEN, id).apply();
    }

    public String getCreatedBy() {
        return prefs.getString(PREF_USER_CREATED_BY, null);
    }

    public void setCreatedBy(String id) {
        prefs.edit().putString(PREF_USER_CREATED_BY, id).apply();
    }

    public String getCreatedAt() {
        return prefs.getString(PREF_USER_CREATED_AT, null);
    }

    public void setCreatedAt(String id) {
        prefs.edit().putString(PREF_USER_CREATED_AT, id).apply();
    }

    public String getUpdateBy() {
        return prefs.getString(PREF_USER_UPDATED_BY, null);
    }

    public void setUpdateBy(String id) {
        prefs.edit().putString(PREF_USER_UPDATED_BY, id).apply();
    }

    public String getUpdateAt() {
        return prefs.getString(PREF_USER_UPDATED_AT, null);
    }

    public void setUpdateAt(String id) {
        prefs.edit().putString(PREF_USER_UPDATED_AT, id).apply();
    }

    public boolean getStatus() {
        return Boolean.parseBoolean(prefs.getString(PREF_USER_STATUS, null));
    }

    public void setStatus(String id) {
        prefs.edit().putString(PREF_USER_STATUS, id).apply();
    }

    public String getTrash() {
        return prefs.getString(PREF_USER_TRASH, null);
    }

    public void setTrash(String id) {
        prefs.edit().putString(PREF_USER_TRASH, id).apply();
    }

    public String getFullName() {
        return prefs.getString(PREF_USER_FULL_NAME, null);
    }

    public void setFullName(String id) {
        prefs.edit().putString(PREF_USER_FULL_NAME, id).apply();
    }

    public String getApiKey() {
        return prefs.getString(PREF_USER_API_KEY, null);
    }

    public void setApiKey(String id) {
        prefs.edit().putString(PREF_USER_API_KEY, id).apply();
    }

    public void saveCurrentUser(CommonResponse commonResponse) {

        if (commonResponse != null) {

            Utility.PrintLog(TAG, commonResponse.toString());
            UserDetail userDetail = commonResponse.getUser();

            if (userDetail != null) {

                if (userDetail.getId() != null) {
                    setUserId(userDetail.getId());
                }

                if (userDetail.getFirst_name() != null) {
                    setFirstName(userDetail.getFirst_name());
                }

                if (userDetail.getLast_name() != null) {
                    setLastName(userDetail.getLast_name());
                }

                if (userDetail.getEmail() != null) {
                    setEmail(userDetail.getEmail());
                }

                if (userDetail.getPassword() != null) {
                    setPassword(userDetail.getPassword());
                }

                if (userDetail.getDate_of_birth() != null) {
                    setDateOfBirth(userDetail.getDate_of_birth());
                }

                if (userDetail.getGender() != null) {
                    setGender(userDetail.getGender());
                }

                if (userDetail.getMobile_number() != null) {
                    setMobileNumber(userDetail.getMobile_number());
                }

                if (userDetail.getCountry() != null) {
                    setCountry(userDetail.getCountry());
                }

                if (userDetail.getCurrency_code() != null) {
                    setCurrencyCode(userDetail.getCurrency_code());
                }

                if (userDetail.getCurrency_symbol() != null) {
                    setCurrencySymbol(userDetail.getCurrency_symbol());
                }

                if (userDetail.getProfile_image() != null) {
                    setProfileImage(userDetail.getProfile_image());
                }

                if (userDetail.getSocial_type() != null) {
                    setSocialType(userDetail.getSocial_type());
                }

                if (userDetail.getSocial_id() != null) {
                    setSocialId(userDetail.getSocial_id());
                }

                if (userDetail.getRemember_token() != null) {
                    setRememberToken(userDetail.getRemember_token());
                }

                if (userDetail.getDevice_type() != null) {
                    setDeviceType(userDetail.getDevice_type());
                }

                if (userDetail.getDevice_token() != null) {
                    setDeviceToken(userDetail.getDevice_token());
                }

                if (userDetail.getCreated_by() != null) {
                    setCreatedBy(userDetail.getCreated_by());
                }

                if (userDetail.getCreated_at() != null) {
                    setCreatedAt(userDetail.getCreated_at());
                }

                if (userDetail.getUpdated_by() != null) {
                    setUpdateBy(userDetail.getUpdated_by());
                }

                if (userDetail.getUpdated_at() != null) {
                    setUpdateAt(userDetail.getUpdated_at());
                }

                if (userDetail.getStatus() != null) {
                    setStatus(String.valueOf(userDetail.getStatus()));
                }

                if (userDetail.getTrash() != null) {
                    setTrash(userDetail.getTrash());
                }

                if (userDetail.getFull_name() != null) {
                    setFullName(userDetail.getFull_name());
                }

                if (commonResponse.getX_api_key() != null) {
                    setApiKey(commonResponse.getX_api_key());
                }

            }

        }
    }

    public void removeCurrentUser() {
        prefs.edit().clear().apply();
    }

    public boolean getCurrentUser() {
        return getUserId() != null && !getUserId().equals("") && !getUserId().equals("null");
    }


    public void setCurrencyRate(String object) {
        prefs.edit().putString(PREF_CURRENCY_CODE_VALUE, object).apply();
    }

    public String getCurrencyRate(String key) {

        try {

            if (prefs != null) {
                String pref = prefs.getString(PREF_CURRENCY_CODE_VALUE, null);

                if (pref != null && !pref.equals("")) {
                    JSONObject jsonObject = new JSONObject(pref);
                    return jsonObject.getString(key);
                }
                return "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }


}
