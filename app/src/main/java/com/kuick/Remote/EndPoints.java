package com.kuick.Remote;

import com.kuick.BuildConfig;

public interface EndPoints {

    String  BaseUrl = BuildConfig.BASE_WEB;

    //url for all webview
    String WEBVIEW_BASE_URL = BaseUrl;
    //share icon url in live activity
    String shareLive =  BaseUrl + "live-stream/";
    //url for stream today in contact us page
    String URL_STREAM_TODAY = BaseUrl + "brands-and-kuickers";

    // header api key
    String HEADER_KEY = "key";
    String API_KEY = "Kuick#1";
    String HEADER_X_API_KEY = "x-api-key";

    //END POINT FOR INIT
    String ENDPOINT_INIT = "init";
    String PATH_DEVICE_TYPE = "device_type";
    String PATH_APP_VERSION = "path_version";
    String PATH_DEVICE_TYPE_VALUE = "android_viewer";

    //END FOR LOGIN
    String ENDPOINT_LOGIN = "login";
    String PARAM_EMAIL = "email";
    String PARAM_PASSWORD = "password";
    String PARAM_DEVICE_TYPE = "device_type";
    String PARAM_DEVICE_TOKEN = "device_token";
    String PARAM_DEVICE_TYPE_VALUE = "0"; //0 for Android 1 for ios

    //END FOR REGISTER
    String ENDPOINT_REGISTER = "register";
    String PARAM_FULL_NAME = "full_name";
    String PARAM_COUNTRY = "country";

    //END FOR LOGOUT
    String ENDPOINT_LOGOUT = "logout";
    String ENDPOINT_ID = "id";

    //END FOR UPDATE PROFILE
    String ENDPOINT_UPDATE_PROFILE = "profile_update";
    String PARAM_USER_ID = "user_id";
    String PARAM_DATE_OF_BIRTH = "date_of_birth";
    String PARAM_GENDER = "gender";
    String PARAM_MOBILE_NUMBER = "mobile_number";
    String PARAM_PROFILE_IMAGE = "profile_image";

    //END FOR PASSWORD CHANGE
    // USER ID SAME
    String PARAM_PASSWORD_UPDATE = "password_update";
    String PARAM_CURRENT_PASSWORD = "current_password";
    String PARAM_NEW_PASSWORD = "new_password";
    String PARAM_CONFIRM_PASSWORD = "confirm_password";

    //END FOR CATEGORIES
    String ENDPOINT_TOP_CATEGORIES = "categories";

    //END POINT DASHBOARD
    String ENDPOINT_DASHBOARD = "dashboard";

    //END POINT ADDRESS LIST
    String ENDPOINT_ALL_ADDRESS = "all_address";
    String ENDPOINT_USER_ID = "user_id";

    //END ADD ADDRESS
    String END_POINT_ADD_ADDRESS = "add_address";
    String PARAM_ADDRESS_TYPE = "address_type";
    String PARAM_ADDRESS = "address";
    String PARAM_APARTMENT = "apartment";
    String PARAM_CITY = "city";
    String PARAM_STATE = "state";
    String PARAM_PHONE_NUMBER = "phone_number";
    String PARAM_POSTAL_CODE = "postal_code";
    //HERE COUNTRY IS COMMON
    String PARAM_REGION = "region";

    //END POINT DELETE ADDRESS
    String END_POINT_ADDRESS_DELETE = "delete_address";
    String PARAM_ADDRESS_ID = "address_id";

    //ORDER HISTORY
    String END_POINT_ORDER_HISTORY = "order_history";

    //ORDER DETAILS
    String END_POINT_ORDER_DETAILS = "order_details";
    String PARAM_ORDER_ID = "order_id";

    // END_POINT ADD PAYMENT CARD
    String END_POINT_ADD_PAYMENT_CARD = "add_cards";

    //END POINT ALL CARD
    String END_POINT_GET_ALL_CARD = "all_cards";

    //END POINT FORGOT PASSWORD
    String END_POINT_FORGOT_PASSWORD = "forgot_password";
    // HERE EMAIL PARAMETER IS COMMON

    //END POINT ADD CARD
    String PARAM_TOKEN = "token";
    String PARAM_CARD_HOLDER_NAME = "card_holder_name";
    String PARAM_CARD_NUMBER = "card_number";
    String PARAM_CARD_EXPIRY_MONTH = "expiry_month";
    String PARAM_CARD_EXPIRY_YEAR = "expiry_year";
    String PARAM_CVV = "cvv";
    String PARAM_ZIP_CODE= "zip_code";
    String PARAM_DOC_NUMBER= "document_number";

    //END POINT STREAMER PROFILE DATA
    String END_POINT_STREAMER_PROFILE_DATA = "streamer_profile_data";
    String PARAM_USERNAME = "username";

    //END POINT FOR SOCIAL LOGIN
    String ENDPOINT_SOCIAL = "social";
    String PARAM_SOCIAL_TYPE = "social_type";
    String PARAM_SOCIAL_ID = "social_id";

    // SOCIAL LOGIN WITH COUNTRY
    String ENDPOINT_SOCIAL_WITH_COUNTRY = "social_country";

    //END POINT LIVE STREAMING SLUG DATA
    String END_POINT_LIVE_STREAMING_SLUG_DATA = "live_stream";
    String PARAM_STREAM_SLUG = "stream_slug";

    //END POINT PRODUCT DETAILS
    String ENDPOINT_PRODUCT_DETAILS = "product_details";

    //END POINT ADD TO CART
    String END_POINT_ADD_TO_CART = "add_to_cart";
    String PARAM_PRODUCT_ID = "product_id";
    String PARAM_PRODUCT_TYPE = "product_type";
    String PARAM_PRODUCT_QTY = "product_qty";
    String PARAM_PRODUCT_SIZE_ID = "product_size_id";
    String PARAM_PRODUCT_COLOR_ID = "product_color_id";
    String PARAM_PRODUCT_CENTRY_ID = "product_centry_id";
    String PARAM_PRODUCT_SHOPIFY_ID = "tp_variant_id";

    //GET CART DETAILS
    String ENDPOINT_CART_DETAILS = "cart_details";

    //END POINT ADD TO CART CHECK
    String ENDPOINT_ADD_TO_CART_CHECK = "add_to_cart_check";

    //END POINT APPLY PROMO CODE
    String ENDPOINT_PROMOCODE = "apply_promocode";
    String PARAM_PROMOCODE = "promocode";

    //GET SINGLE CARD DETAILS
    String END_POINT_SINGLE_CARD_DETAIL = "get_card_details";
    String PARAM_CARD_ID = "card_id";

    //PLACE ORDER
    String ENDPOINT_PLACE_ORDER = "place_order";
    String PARAM_PAYMENT_TYPE = "payment_type";
    String PARAM_KUICKER_ID = "kuicker_id";
    String PARAM_PAYMENT_RESULT = "paypal_result";
    String PARAM_DISCOUNT_ID = "discount_id";
    String PARAM_TOTAL_AMOUNT = "total_amount";
    String PARAM_DOCUMENT_NUMBER = "document_number";
    String PARAM_TRANSACTION_ID = "transaction_id";

    //ENDPOINT SHIPPING CHARGE
    String ENDPOINT_CHECK_SHIPPING_CHARGE = "checkout_shippint_charge";

    //END POINT CURRENCY CODE
    String END_POINT_CURRENCY_RATE = "currency_rates";
    String PARAM_CURRENCY_CODE = "currency_code";

    //END POINT LANGUAGE LABLE
    String ENDPOINT_LANGUAGE_LABLE = "language_labels";
    String PARAM_CODE = "code";

    //endpoint delete cart
    String ENDPOINT_DELETE_CART = "delete_cart";
    String PARAM_CART_ID = "cart_id";
    String PARAM_DISCOUNT = "discount";

    //most popular live
    //String ENDPOINT_MOST_POPULAR_LIVES = "most_popular_lives";
    String ENDPOINT_MOST_POPULAR_LIVES = "most_popular_lives_new";

    //END POINT ON CLICK TOP CATEGORIES SCREEN
    String ENDPOINT_CATEGORY_LIVES = "category_lives";

    //ENDPOINT USER NOTIFICATION
    String ENDPOINT_USER_NOTIFICATION = "user_notifications";

    //endpoint country list
    String ENDPOINT_COUNTRY_LIST = "country_list";

    //endpoint top category
    String END_POINT_TOP_CATEGORY_FULL_SCREEN = "categories";

    //end point notify me
    String END_POINT_NOTIFY_ME = "notify_me";
    String PARAM_STREAMER_ID = "stream_id";

    //update user cart
    String ENDPOINT_UPDATE_USER_CART = "update_cart";
    String PARAM_TYPE = "type";

    //country_regions
    String ENDPOINT_COUNTRY_REGIONS = "country_regions";
    String PARAM_COUNTRY_NAME = "country_name";

    //contact us
    String ENDPOINT_CONTACT_US = "contact_us";
    String ENDPOINT_CONTACT_US_GET_VALUE = "setting_values";
    String PARAM_FIRST_NAME = "first_name";
    String PARAM_LAST_NAME = "last_name";
    String PARAM_MESSAGE = "message";
    String ENDPOINT_DELETE_CARD = "delete_card";

    //ENDPOINT GET URL OF PAYPAL
    String ENDPOINT_PAYPAL_URL = "get_url_of_paypal";

    //ENDPOINT PAGOFACIL
    String ENDPOINT_PAGOFACIL_URL = "get_url_of_pagofacil";

    //update_user_language
    String ENDPOINT_UPDATE_LANGUAGE = "update_user_language";

    //get country code
    String ENDPOINT_GET_ALL_COUNTRY_CODE = "get_all_country_codes";

    //get dLocal Urls
    String ENDPOINT_GET_URL_OF_DLOCAL = "get_url_of_dlocal";

    //Remove Promocode when product variant is shopify
    String ENDPOINT_REMOVE_PROMOCODE = "remove_promocode";

    //Shopify Country
    String ENDPOINT_SHOPIFY_COUNTRY = "get_shopify_countries";
    String PARAM_SHOPIFY_STREAMER_ID = "streamer_id";

    //get_shopify_country_province
    String ENDPOINT_SHOPIFY_COUNTRY_PROVINCE = "get_shopify_country_province";
    String PARAM_COUNTRY_ID = "country_id";
    String PARAM_SATE_TYPE = "state_type";

    //Add Shopify Address
    String ENDPOINT_ADD_SHOPIFY_ADDRESS = "add_shopify_address";

    //MErcado pago
    String  ENDPOINT_MERCADO_PAGO = "get_url_of_mercadopago";

    // video clips
    String ENDPOINT_GET_ALL_CLIPS = "get_all_clips_new";

    //Video clip Add to cart or dislike
    String ENDPOINT_CLIP_CART_OR_DISLIKE = "clip_cart_or_dislike_new";
    String ENDPOINT_CLIP_ID = "clip_id";
    String ENDPOINT_PRODUCT_VARIANT = "product_variant";
    String ENDPOINT_IS_CART = "is_cart";
    String ENDPOINT_IS_DISLIKE = "is_dislike";
    String ENDPOINT_QUANTITY = "quantity";

    // video back button api
    String ENDPOINT_USER_BACK_CLICK = "user_back_click";
    String PARAM_CLICK = "clicks";

    //FOR EVENT ORDER GENERATE (CONFFITI ANIMATION)
    String ENDPOINT_EVENT_ORDER = "event_order";
    String PARAM_EVENT_ID = "event_id";

    // FOR DlOCAL CARD WEBVIEW
    String ENDPOINT_DLOCAL_WEBVIEW = "get_add_dlocal_card_url";
}
