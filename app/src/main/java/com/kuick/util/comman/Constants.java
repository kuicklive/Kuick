package com.kuick.util.comman;

import android.app.PictureInPictureParams;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.kuick.Response.UserAddressResponse;
import com.kuick.model.UserCard;

import io.agora.rtc.video.VideoEncoderConfiguration;

public class Constants {

    ///////////////////////// for agora live streaming

    public static final String PREF_NAME = "io.agora.openlive";
    public static final int DEFAULT_PROFILE_IDX = 2;
    public static final String PREF_RESOLUTION_IDX = "pref_profile_index";
    public static final String PREF_ENABLE_STATS = "pref_enable_stats";
    public static final String PREF_MIRROR_LOCAL = "pref_mirror_local";
    public static final String PREF_MIRROR_REMOTE = "pref_mirror_remote";
    public static final String PREF_MIRROR_ENCODE = "pref_mirror_encode";
    public static final String KEY_CLIENT_ROLE = "key_client_role";
    public static final String NO_ORDERS = "no_orders";
    public static final String ZERO = "00";
    public static final String INTENT_KEY_LIVE_WATCHER_COUNTS = "watcherCount";
    public static final String PLUS = "plus";
    public static final String MINUS = "minus";
    public static final String INTENT_KEY_DIRECTION_CODE = "INTENT_KEY_DIRECTION_CODE";
    public static final String INTENT_KEY_DISCOVER_PAGE = "INTENT_KEY_DISCOVER_PAGE";
    public static final String INTENT_KEY_LOGOUT = "INTENT_KEY_LOGOUT";
    public static final String FALSE = "false";
    public static final String CART_PAGE = "cart_page";
    public static final String PRODUCT_PAGE = "product_page";

    //intent data transfer key
    public static final String INTENT_KEY = "dashBoardResponse";
    public static final String INTENT_KEY_USER_NAME = "INTENT_KEY_PRODUCT_NAME";
    public static final String INTENT_KEY_PRODUCT_ID = "INTENT_KEY_PRODUCT_ID";
    public static final String INTENT_KEY_IS_ADDRESS_SELECTED = "is_address_selected";
    public static final String INTENT_KEY_IS_PAYMENT_SELECTED = "is_payment_selected";
    public static final String INTENT_KEY_IS_SHOPIFY = "is_SHOPIFY";
    public static final String INTENT_KEY_IS_ADDRESS_TYPE = "address_type";
    public static final String INTENT_KEY_IS_STREAMER_ID = "streamer_id";
    public static final String INTENT_KEY_USERNAME = "user_name";
    public static final String INTENT_KEY_USER_EMAIL = "user_email";
    public static final String INTENT_KEY_SOCIAL_ID = "social_id";
    public static final String INTENT_KEY_STREAM_SLUG = "stream_slug";
    public static final String INTENT_KEY_IS_FORM_CART = "is_from_cart";
    public static final String TRUE = "true";
    public static final String INTENT_KEY_SELECTED_CARD_DETAILS = "SELECTED_CARD_DETAILS";
    public static final String INTENT_KEY_SELECTED_ADDRESS_DETAILS = "SELECTED_ADDRESS_DETAILS";
    public static final String ORDER_ID = "order_id";
    public static final String BANNER_TYPE_SIMPLE = "simple";
    public static final String BANNER_TYPE_TIMER = "timer";
    // if user exist on database
    public static final String USER_ALREADY_EXIST = "email_already_exist";
    public static final String LOGIN_FAIL = "Login Failed, Try again";
    public static final String USER_NOT_EXIST = "invalid_email_or_password";
    public static final String HOME = "Home";
    public static final String OFFICE = "Office";
    public static final String DEFAULT_TIMEZONE = "America/Santiago";
    //activity result code
    public static final int REQUEST_FOR_PAYMENT_CARD = 11;
    public static final int REQUEST_FOR_ADDRESS_SELECTION = 12;
    //for tax enable
    public static final String ENABLE = "E";
    public static final String PERCENTAGE = "percentage";
    public static VideoEncoderConfiguration.VideoDimensions[] VIDEO_DIMENSIONS = new VideoEncoderConfiguration.VideoDimensions[]{
            VideoEncoderConfiguration.VD_320x240,
            VideoEncoderConfiguration.VD_480x360,
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.VD_640x480,
            new VideoEncoderConfiguration.VideoDimensions(960, 540),
            VideoEncoderConfiguration.VD_1280x720
    };
    public static int[] VIDEO_MIRROR_MODES = new int[]{
            io.agora.rtc.Constants.VIDEO_MIRROR_MODE_AUTO,
            io.agora.rtc.Constants.VIDEO_MIRROR_MODE_ENABLED,
            io.agora.rtc.Constants.VIDEO_MIRROR_MODE_DISABLED,
    };
    //FOR FACEBOOK
    public static String FACEBOOK_EMAIL = "email";
    public static String FACEBOOK_NAME = "name";
    public static String FACEBOOK_ID = "id";
    public static String BASE_GRAPH_URL = "http://graph.facebook.com/";
    public static String END_POINT_GRAPH_URL = "/picture?type=large";
    public static String FACEBOOK = "Facebook";
    public static String GOOGLE = "Google";
    public static String SOCIAL_TYPE = FACEBOOK;
    //sharedPref key
    public static String SHARED_PREFERENCE_KUICK = "Local_Data_Kuick";
    //web view screen
    public static String WEBVIEW_SCREEN = "WEBVIEWSCREEN";
    public static String NOT_LOGIN = "NOT_LOGIN";
    public static String SCREEN = "SCREEN";
    public static String OK = "OK";
    public static String NULL = "null";
    //Male Female
    public static String Male = "Male";
    public static String Female = "Female";
    //order proccess
    static public String inProgress = "Order In Progress";
    static public String conformed = "Order Confirmed";
    static public String orderShipped = "Order Shipped";
    public static String KEY_MESSAGE = "dialog message";
    public static String KEY_STATUS = "order place response status";
    public static String KEY_BUTTON_TEXT = "button text";
    public static String KEY_BUTTON_IMAGE = "image for success or error";
    //categories max size for dashboard
    public static int SIX = 6;
    public static String ADDRESS = "City, Aisén del General Carlos Ibáñez delCampo, Chile-396445";
    public static String PERMISSION_DENY = "Permission Deny";
    public static String CANCEL = "Cancel";
    public static String ALLOW = "Allow";
    public static String USER = "user";
    public static String ADMIN = "admin";
    public static String USER_END = "images/user.png";
    public static String ADMIN_END = "images/admin.png";
    public static String DeActivateMessage = "Your account has been deactivated by administrator. Please contact the support team!";
    public static String countryScreen = "country_screen";
    static public String comment = "Ok lets wait for them";
    // selected payment card
    public static int selectedCardPosition = -1;
    public static UserCard selectedPaymentCard;
    public static boolean isPaypalSelected;
    public static boolean isMercadoPagoSelected;
    public static boolean isPagofacilSelected;
    public static boolean isdLocalSelected;
    //selected address
    public static UserAddressResponse selectedAddress;
    public static String CARD = "Card";
    public static String PAYPAL = "Paypal";
    public static String PAGOFACIL = "Pagofacil";
    public static String DLOCAL = "dLocal";
    public static String DLOCAL_DIRECT = "dLocalDirect";
    public static String MERCADO_PAGO = "Mercado Pago";
    public static String MercadoPago = "MercadoPago";

    //variant validation
    public static String isSizeVariant = "isSizeVariant";
    public static String isColorVariant = "isColorVariant";
    public static String isColorSizeVariant = "isColorSizeVariant";
    public static String isCentryVariant = "isCentryVariant";
    public static String No_VARIANT = "No Variant";
    public static String SIZE_VARIANT = "size_variant";
    public static String COLOR_VARIANT = "color_variant";
    public static String SIZE_COLOR_VARIANT = "size_color_variant";
    public static String CENTRY_VARIANT = "centry_variant";
    public static String SHOPIFY_VARIANT = "shopify_variant";
    //for language code
    public static String EN = "en"; //  for English language
    public static String ES = "es"; //  for Spanish  language
    //for countdown status
    public static String Approved = "approved";
    //notification type
    public static String order = "order";
    public static String order_confirmed = "order_confirmed";
    public static String order_rejected = "order_rejected";
    public static String order_shipped = "order_shipped";
    public static String order_delivered = "order_delivered";
    public static String order_returned = "order_returned";
    public static String push_notification = "push_notification";
    public static String product_in_cart = "product_in_cart";


    public static String notify_me = "notify_me";
    public static String logout_device = "logout_device";
    //fro session expire
    public static final String SESSION_EXPIRE = "session expire";
    //cart screen
        public static final String MULTIPLE_PRODUCT = "multiple_products";
        public static final String SINGLE_PRODUCT = "single_product";
        public static final String ALL = "all";
        public static final String USER_TYPE = "user";
        public static final String STREAMER = "streamer";
    public static boolean isRefreshAPI = false;
    public static boolean isFromProfileScreen = false;
    public static boolean idFromProfile = false;
    public static boolean isHomeActivityIsAlredayOpen;
    public static boolean isHomeActivityForNotification;
    public static boolean isForRefreshHomeApi = false;
    public static String live_slug_from_notification = null ;
    public static String order_id = null ;
    public static String isLiveStatus ="";
    public static boolean responseSuccess = false;
    public static boolean isFromConnectionLost = false;
    public static boolean isForStreamerDetail = false;
    public static boolean isForLiveStreaming = false;
    public static String liveStreamingSlugFromDeepLinking = null;
    public static boolean isFirstTimeIntervalConnect = true;
    public static String shopifyCurrencyCode = "USD";

    public interface AppConstance {
        int FORWARD_SCREEN_CHANGE_TIME = 900;
    }
    public interface BundleKeys {
        String ARG_LOADER_FINISH_ACTIVITY = "when on back press should finish activity?";
    }

    //For address information
    public static final String COUNTRY_CODE = "COUNTRY_CODE";
    public static final String MOBILE_NUMBER = "MOBILE_NUMBER";
    public static final String ADDRESS_TYPE = "ADDRESS_TYPE";
    public static final String ADDRESS_TEXT = "ADDRESS_TEXT";
    public static final String APARTMENT = "APARTMENT";
    public static final String CITY_NAME = "CITY_NAME";

}
