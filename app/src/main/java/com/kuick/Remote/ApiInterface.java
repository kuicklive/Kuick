package com.kuick.Remote;

import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.Response.DashBoardResponse;
import com.kuick.Response.InitResponse;
import com.kuick.Response.NotificationResponse;
import com.kuick.Response.UserAddressResponse;
import com.kuick.Response.ContactUsDetails;
import com.kuick.model.UserCard;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static com.kuick.Remote.EndPoints.ENDPOINT_ADD_SHOPIFY_ADDRESS;
import static com.kuick.Remote.EndPoints.ENDPOINT_ADD_TO_CART_CHECK;
import static com.kuick.Remote.EndPoints.ENDPOINT_ALL_ADDRESS;
import static com.kuick.Remote.EndPoints.ENDPOINT_CART_DETAILS;
import static com.kuick.Remote.EndPoints.ENDPOINT_CATEGORY_LIVES;
import static com.kuick.Remote.EndPoints.ENDPOINT_CHECK_SHIPPING_CHARGE;
import static com.kuick.Remote.EndPoints.ENDPOINT_CLIP_CART_OR_DISLIKE;
import static com.kuick.Remote.EndPoints.ENDPOINT_CONTACT_US;
import static com.kuick.Remote.EndPoints.ENDPOINT_CONTACT_US_GET_VALUE;
import static com.kuick.Remote.EndPoints.ENDPOINT_COUNTRY_LIST;
import static com.kuick.Remote.EndPoints.ENDPOINT_COUNTRY_REGIONS;
import static com.kuick.Remote.EndPoints.ENDPOINT_DASHBOARD;
import static com.kuick.Remote.EndPoints.ENDPOINT_DELETE_CARD;
import static com.kuick.Remote.EndPoints.ENDPOINT_DELETE_CART;
import static com.kuick.Remote.EndPoints.ENDPOINT_EVENT_ORDER;
import static com.kuick.Remote.EndPoints.ENDPOINT_GET_ALL_CLIPS;
import static com.kuick.Remote.EndPoints.ENDPOINT_GET_ALL_COUNTRY_CODE;
import static com.kuick.Remote.EndPoints.ENDPOINT_GET_URL_OF_DLOCAL;
import static com.kuick.Remote.EndPoints.ENDPOINT_ID;
import static com.kuick.Remote.EndPoints.ENDPOINT_INIT;
import static com.kuick.Remote.EndPoints.ENDPOINT_LANGUAGE_LABLE;
import static com.kuick.Remote.EndPoints.ENDPOINT_LOGIN;
import static com.kuick.Remote.EndPoints.ENDPOINT_LOGOUT;
import static com.kuick.Remote.EndPoints.ENDPOINT_MERCADO_PAGO;
import static com.kuick.Remote.EndPoints.ENDPOINT_MOST_POPULAR_LIVES;
import static com.kuick.Remote.EndPoints.ENDPOINT_PAGOFACIL_URL;
import static com.kuick.Remote.EndPoints.ENDPOINT_PAYPAL_URL;
import static com.kuick.Remote.EndPoints.ENDPOINT_PLACE_ORDER;
import static com.kuick.Remote.EndPoints.ENDPOINT_PRODUCT_DETAILS;
import static com.kuick.Remote.EndPoints.ENDPOINT_PROMOCODE;
import static com.kuick.Remote.EndPoints.ENDPOINT_REGISTER;
import static com.kuick.Remote.EndPoints.ENDPOINT_REMOVE_PROMOCODE;
import static com.kuick.Remote.EndPoints.ENDPOINT_SHOPIFY_COUNTRY;
import static com.kuick.Remote.EndPoints.ENDPOINT_SHOPIFY_COUNTRY_PROVINCE;
import static com.kuick.Remote.EndPoints.ENDPOINT_SOCIAL;
import static com.kuick.Remote.EndPoints.ENDPOINT_SOCIAL_WITH_COUNTRY;
import static com.kuick.Remote.EndPoints.ENDPOINT_UPDATE_LANGUAGE;
import static com.kuick.Remote.EndPoints.ENDPOINT_UPDATE_PROFILE;
import static com.kuick.Remote.EndPoints.ENDPOINT_UPDATE_USER_CART;
import static com.kuick.Remote.EndPoints.ENDPOINT_USER_BACK_CLICK;
import static com.kuick.Remote.EndPoints.ENDPOINT_USER_ID;
import static com.kuick.Remote.EndPoints.ENDPOINT_USER_NOTIFICATION;
import static com.kuick.Remote.EndPoints.END_POINT_ADDRESS_DELETE;
import static com.kuick.Remote.EndPoints.END_POINT_ADD_ADDRESS;
import static com.kuick.Remote.EndPoints.END_POINT_ADD_PAYMENT_CARD;
import static com.kuick.Remote.EndPoints.END_POINT_ADD_TO_CART;
import static com.kuick.Remote.EndPoints.END_POINT_CURRENCY_RATE;
import static com.kuick.Remote.EndPoints.END_POINT_FORGOT_PASSWORD;
import static com.kuick.Remote.EndPoints.END_POINT_GET_ALL_CARD;
import static com.kuick.Remote.EndPoints.END_POINT_LIVE_STREAMING_SLUG_DATA;
import static com.kuick.Remote.EndPoints.END_POINT_NOTIFY_ME;
import static com.kuick.Remote.EndPoints.END_POINT_ORDER_DETAILS;
import static com.kuick.Remote.EndPoints.END_POINT_ORDER_HISTORY;
import static com.kuick.Remote.EndPoints.END_POINT_SINGLE_CARD_DETAIL;
import static com.kuick.Remote.EndPoints.END_POINT_STREAMER_PROFILE_DATA;
import static com.kuick.Remote.EndPoints.END_POINT_TOP_CATEGORY_FULL_SCREEN;
import static com.kuick.Remote.EndPoints.HEADER_KEY;
import static com.kuick.Remote.EndPoints.HEADER_X_API_KEY;
import static com.kuick.Remote.EndPoints.PARAM_ADDRESS_ID;
import static com.kuick.Remote.EndPoints.PARAM_ADDRESS_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_CARD_ID;
import static com.kuick.Remote.EndPoints.PARAM_CART_ID;
import static com.kuick.Remote.EndPoints.PARAM_CLICK;
import static com.kuick.Remote.EndPoints.PARAM_CODE;
import static com.kuick.Remote.EndPoints.PARAM_COUNTRY;
import static com.kuick.Remote.EndPoints.PARAM_COUNTRY_ID;
import static com.kuick.Remote.EndPoints.PARAM_COUNTRY_NAME;
import static com.kuick.Remote.EndPoints.PARAM_CURRENCY_CODE;
import static com.kuick.Remote.EndPoints.PARAM_DATE_OF_BIRTH;
import static com.kuick.Remote.EndPoints.PARAM_DISCOUNT;
import static com.kuick.Remote.EndPoints.PARAM_EMAIL;
import static com.kuick.Remote.EndPoints.PARAM_EVENT_ID;
import static com.kuick.Remote.EndPoints.PARAM_FULL_NAME;
import static com.kuick.Remote.EndPoints.PARAM_GENDER;
import static com.kuick.Remote.EndPoints.PARAM_MOBILE_NUMBER;
import static com.kuick.Remote.EndPoints.PARAM_ORDER_ID;
import static com.kuick.Remote.EndPoints.PARAM_PASSWORD_UPDATE;
import static com.kuick.Remote.EndPoints.PARAM_PROMOCODE;
import static com.kuick.Remote.EndPoints.PARAM_SHOPIFY_STREAMER_ID;
import static com.kuick.Remote.EndPoints.PARAM_STREAMER_ID;
import static com.kuick.Remote.EndPoints.PARAM_STREAM_SLUG;
import static com.kuick.Remote.EndPoints.PARAM_TOTAL_AMOUNT;
import static com.kuick.Remote.EndPoints.PARAM_USERNAME;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.Remote.EndPoints.PATH_APP_VERSION;
import static com.kuick.Remote.EndPoints.PATH_DEVICE_TYPE;

public interface ApiInterface {

    // init call
    @GET(ENDPOINT_INIT + "/{" + PATH_DEVICE_TYPE + "}/" + "{" + PATH_APP_VERSION + "}")
    Call<InitResponse> doInitCall(@Header(HEADER_KEY) String apikey,
                                  @Path(PATH_DEVICE_TYPE) String deviceType,
                                  @Path(PATH_APP_VERSION) String appVersion);

    //login call
    @FormUrlEncoded
    @POST(ENDPOINT_LOGIN)
    Call<CommonResponse> doLoginCall(
            @Header(HEADER_KEY) String apiKey,
            @FieldMap Map<String, String> loginRequest);

    // Logout call
    @GET(ENDPOINT_LOGOUT + "/{" + ENDPOINT_ID + "}")
    Call<BaseResponse> doLogout(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Path(ENDPOINT_ID) String userId);

    //Register call
    @FormUrlEncoded
    @POST(ENDPOINT_REGISTER)
    Call<CommonResponse> doRegister(
            @Header(HEADER_KEY) String apiKey,
            @FieldMap Map<String, String> SignUpRequest
    );


    @Multipart
    @POST(ENDPOINT_UPDATE_PROFILE)
    Call<CommonResponse> updateProfile(
            @Part(PARAM_USER_ID) RequestBody user_id,
            @Part(PARAM_FULL_NAME) RequestBody full_name,
            @Part(PARAM_EMAIL) RequestBody email,
            @Part(PARAM_DATE_OF_BIRTH) RequestBody date_of_birth,
            @Part(PARAM_GENDER) RequestBody gender,
            @Part(PARAM_MOBILE_NUMBER) RequestBody mobile,
            @Part MultipartBody.Part image,
            @Part(PARAM_COUNTRY) RequestBody country);



    //Update Profile
    @FormUrlEncoded
    @POST(ENDPOINT_UPDATE_PROFILE)
    Call<CommonResponse> doUpdateProfileCall(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, Object> updateProfileRequest);

    //change password call
    @FormUrlEncoded
    @POST(PARAM_PASSWORD_UPDATE)
    Call<CommonResponse> doPasswordChangeCall(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> passwordChange);

    //DASHBOARD API  CALL
    @GET(ENDPOINT_DASHBOARD  + "/{" + ENDPOINT_USER_ID + "}")
    Call<DashBoardResponse> doDashboardData(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Path(ENDPOINT_USER_ID) String userId);

    // ADDRESS LIST API
    @FormUrlEncoded
    @POST(ENDPOINT_ALL_ADDRESS)
    Call<CommonResponse> doAllAddressList(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(ENDPOINT_USER_ID) String user_id,
            @Field(PARAM_ADDRESS_TYPE) String address_type);

    //ADD ADDRESS
    @FormUrlEncoded
    @POST(END_POINT_ADD_ADDRESS)
    Call<UserAddressResponse> doAddAddress(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> add_address);

    // ADDRESS LIST API
    @FormUrlEncoded
    @POST(END_POINT_ADDRESS_DELETE)
    Call<CommonResponse> doDeleteAddress(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(ENDPOINT_USER_ID) String user_id,
            @Field(PARAM_ADDRESS_ID) String address_id,
            @Field(PARAM_ADDRESS_TYPE) String address_type);


    // ORDER HISTORY
    @FormUrlEncoded
    @POST(END_POINT_ORDER_HISTORY)
    Call<CommonResponse> doOrderHistory(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(ENDPOINT_USER_ID) String user_id);


    // ORDER DETAILS
    @FormUrlEncoded
    @POST(END_POINT_ORDER_DETAILS)
    Call<CommonResponse> doOrderDetails(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(ENDPOINT_USER_ID) String user_id,
            @Field(PARAM_ORDER_ID) String order_id);

    // ORDER PAYMENT CARD

    @FormUrlEncoded
    @POST(END_POINT_ADD_PAYMENT_CARD)
    Call<UserCard> doAddPaymentCard(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> add_card);

    //GET ALL PAYMENT CARDS
    @FormUrlEncoded
    @POST(END_POINT_GET_ALL_CARD)
    Call<CommonResponse> doAllCardsList(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(ENDPOINT_USER_ID) String user_id);

    //  FORGOT PASSWORD
    @FormUrlEncoded
    @POST(END_POINT_FORGOT_PASSWORD)
    Call<BaseResponse> doForgotPassword(
            @Header(HEADER_KEY) String apiKey,
            @Field(PARAM_EMAIL) String email);

    //STREAMER PROFILE DATA
    @FormUrlEncoded
    @POST(END_POINT_STREAMER_PROFILE_DATA)
    Call<CommonResponse> doStreamerProfileData(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USERNAME) String username);

    //FOR SOCIAL API (FACEBOOK AND GOOGLE LOGIN)
    @FormUrlEncoded
    @POST(ENDPOINT_SOCIAL)
    Call<CommonResponse> doSocialLogin(
            @Header(HEADER_KEY) String apiKey,
            @FieldMap Map<String, String> social_details);

    //for social login with country
    @FormUrlEncoded
    @POST(ENDPOINT_SOCIAL_WITH_COUNTRY)
    Call<CommonResponse> socialLoginWithCountry(
            @Header(HEADER_KEY) String apiKey,
            @FieldMap Map<String, String> SocialSignUpRequest
    );

    //for social login with country
    @FormUrlEncoded
    @POST(END_POINT_LIVE_STREAMING_SLUG_DATA)
    Call<CommonResponse> doLiveStreamingSlugData(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_STREAM_SLUG) String streamSlug,
            @Field(PARAM_USER_ID) String user_id);

    // PRODUCT DETAILS
    @GET(ENDPOINT_PRODUCT_DETAILS + "/{" + ENDPOINT_ID + "}" + "/{" + PARAM_USER_ID + "}")
    Call<CommonResponse> doProductDetails(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Path(ENDPOINT_ID) String product_id,
            @Path(PARAM_USER_ID) String user_id);

    //ADD TO CART API
    @FormUrlEncoded
    @POST(END_POINT_ADD_TO_CART)
    Call<BaseResponse> doAddToCart(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> addToCart
    );

    //GET CART DETAILS
    @GET(ENDPOINT_CART_DETAILS + "/{" + ENDPOINT_ID + "}")
    Call<CommonResponse> doGetCart(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Path(ENDPOINT_ID) String userId);

    //ADD TO CART CHECK
    @FormUrlEncoded
    @POST(ENDPOINT_ADD_TO_CART_CHECK)
    Call<BaseResponse> doAddToCartCheck(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> addToCart
    );

    // APPLY PROMO CODE
    @FormUrlEncoded
    @POST(ENDPOINT_PROMOCODE)
    Call<CommonResponse> doApplyPromoCode(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_PROMOCODE) String promo_code
    );

    // GET SINGLE CARD DETAILS
    @FormUrlEncoded
    @POST(END_POINT_SINGLE_CARD_DETAIL)
    Call<CommonResponse> doGetSingleCardDetails(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_CARD_ID) String card_id
    );

    //PLACE ORDER
    @FormUrlEncoded
    @POST(ENDPOINT_PLACE_ORDER)
    Call<BaseResponse> doPlaceOrder(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> place_order
    );

    //SHIPPING CHARGE
    @FormUrlEncoded
    @POST(ENDPOINT_CHECK_SHIPPING_CHARGE)
    Call<CommonResponse> doCheckoutShippingCharge(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_ADDRESS_ID) String address_id,
            @Field(PARAM_DISCOUNT) String discount_array
    );

    //currency rate
    @FormUrlEncoded
    @POST(END_POINT_CURRENCY_RATE)
    Call<String> doCurrencyRate(
            @Header(HEADER_KEY) String apiKey,
            @Field(PARAM_CURRENCY_CODE) String currency_code);


    // LANGUAGE API
    @FormUrlEncoded
    @POST(ENDPOINT_LANGUAGE_LABLE)
    Call<String> doGetLanguage(
            @Header(HEADER_KEY) String apiKey,
            @Field(PARAM_CODE) String code);

    //DELETE CART
    @FormUrlEncoded
    @POST(ENDPOINT_DELETE_CART)
    Call<CommonResponse> doDeleteCart(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_CART_ID) String cart_id);

    //GET MOST POPULAR LIVES STREAMER
    @GET(ENDPOINT_MOST_POPULAR_LIVES)
    Call<CommonResponse> doMostPopularLives(
            @Header(HEADER_X_API_KEY) String apiKey);

    //top categories on click screen
    @GET(ENDPOINT_CATEGORY_LIVES + "/{" + ENDPOINT_ID + "}")
    Call<CommonResponse> doCategoryLives(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Path(ENDPOINT_ID) String product_id);

    // NOTIFICATION
    @GET(ENDPOINT_USER_NOTIFICATION + "/{" + PARAM_USER_ID + "}")
    Call<NotificationResponse> doNotification(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Path(PARAM_USER_ID) String user_id);

    //Country List
    @GET(ENDPOINT_COUNTRY_LIST )
    Call<String> doCountryList(
            @Header(HEADER_KEY) String apiKey);

    //for top category full screen

    @GET(END_POINT_TOP_CATEGORY_FULL_SCREEN )
    Call<CommonResponse> doTopCategoryFullScreen(
            @Header(HEADER_KEY) String apiKey);

    //for notify me button click
    @FormUrlEncoded
    @POST(END_POINT_NOTIFY_ME)
    Call<BaseResponse> doNotifyMe(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_STREAMER_ID) String streamer_id);

    //Update user cart
    @FormUrlEncoded
    @POST(ENDPOINT_UPDATE_USER_CART)
    Call<CommonResponse> doUpdateCart(
            @Header(HEADER_X_API_KEY) String apikey,
            @FieldMap Map<String, String> place_order);

    //country regions
    @FormUrlEncoded
    @POST(ENDPOINT_COUNTRY_REGIONS)
    Call<String> doCheckCountryRegions(
            @Header(HEADER_X_API_KEY) String apikey,
            @Field(PARAM_COUNTRY_NAME) String country_name);

    //FOR CONTACT US
    @FormUrlEncoded
    @POST(ENDPOINT_CONTACT_US)
    Call<BaseResponse> doContactUs(
            @Header(HEADER_X_API_KEY) String apikey,
            @FieldMap Map<String, String> contactUs);

    // DELETE CARD
    @FormUrlEncoded
    @POST(ENDPOINT_DELETE_CARD)
    Call<CommonResponse> doDeleteCard(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_CARD_ID) String card_id);

    //for PAYPAL
    @FormUrlEncoded
    @POST(ENDPOINT_PAYPAL_URL)
    Call<String> doPaypalUrl(
            @Header(HEADER_X_API_KEY) String apikey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_TOTAL_AMOUNT) String total_amount);


    //for PAGOFACIL
    @FormUrlEncoded
    @POST(ENDPOINT_PAGOFACIL_URL)
    Call<String> doPagofacialUrl(
            @Header(HEADER_X_API_KEY) String apikey,
            @FieldMap Map<String, String> pagofacil);

    //update_user_language
    @FormUrlEncoded
    @POST(ENDPOINT_UPDATE_LANGUAGE)
    Call<String> doUpdateLanguage(
            @Header(HEADER_X_API_KEY) String apikey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_CODE) String code);

    //CONTACT US
    //GET ENDPOINT_CONTACT_US_GET_VALUE
    @GET(ENDPOINT_CONTACT_US_GET_VALUE)
    Call<ContactUsDetails> doContactUsDetails(
            @Header(HEADER_X_API_KEY) String apiKey);

    //GET ALL COUNTRY CODE
    @GET(ENDPOINT_GET_ALL_COUNTRY_CODE )
    Call<String> doGetCountryCode(
            @Header(HEADER_KEY) String apiKey);


    //
    @FormUrlEncoded
    @POST(ENDPOINT_GET_URL_OF_DLOCAL)
    Call<String> dogetDlocalUrl(
            @Header(HEADER_X_API_KEY) String apikey,
            @FieldMap Map<String, String> dLocal);

    //Remove Promocode When Shopify
    @FormUrlEncoded
    @POST(ENDPOINT_REMOVE_PROMOCODE)
    Call<CommonResponse> doRemovePromoCode(
            @Header(HEADER_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id);


    //Shopify Country List
    @FormUrlEncoded
    @POST(ENDPOINT_SHOPIFY_COUNTRY)
    Call<String> doShopifyCountry(
            @Header(HEADER_KEY) String apiKey,
            @Field(PARAM_SHOPIFY_STREAMER_ID) String streamer_id);


    //COUNTRY_PROVINCE
    @FormUrlEncoded
    @POST(ENDPOINT_SHOPIFY_COUNTRY_PROVINCE)
    Call<String> doProvince(
            @Header(HEADER_KEY) String apiKey,
            @Field(PARAM_SHOPIFY_STREAMER_ID) String streamer_id,
            @Field(PARAM_COUNTRY_ID) String country_id);

    //ADD SHOPIFY ADDRESS
    @FormUrlEncoded
    @POST(ENDPOINT_ADD_SHOPIFY_ADDRESS)
    Call<UserAddressResponse> doAddShopifyAddress(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> add_address);

    //
    //ADD MERCADO PAGO PAYMENT
    @FormUrlEncoded
    @POST(ENDPOINT_MERCADO_PAGO)
    Call<String> doGetMercadoPago(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> addPlaceorder);

    // Video Clips
    @FormUrlEncoded
    @POST(ENDPOINT_GET_ALL_CLIPS)
    Call<CommonResponse> doGetVideoClips(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id);

    //Video clip Add to cart and dislike
    @FormUrlEncoded
    @POST(ENDPOINT_CLIP_CART_OR_DISLIKE)
    Call<CommonResponse> doVideoClipCartOrDislike(
            @Header(HEADER_X_API_KEY) String apiKey,
            @FieldMap Map<String, String> addPlaceorder);

    @FormUrlEncoded
    @POST(ENDPOINT_USER_BACK_CLICK)
    Call<CommonResponse> doBackClick(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_USER_ID) String user_id,
            @Field(PARAM_CLICK) String click);

    //FOR CONFFITI ANIMATION IN LIVE SCREEN
    @FormUrlEncoded
    @POST(ENDPOINT_EVENT_ORDER)
    Call<CommonResponse> doOrderEvent(
            @Header(HEADER_X_API_KEY) String apiKey,
            @Field(PARAM_EVENT_ID) String eventId,
            @Field(PARAM_ORDER_ID) String order_id);

}
