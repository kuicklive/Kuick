package com.kuick.common;


import io.socket.client.Socket;

public class Common {

    //FOR SOCKET
    public static Socket socket;

    //key
    public static String CONNECT_ALL_USER = "connect_all_user";
    public static String CONNECT_NORMAL_USER_REFRESH_COUNT = "normal_user_refresh_count";
    public static String CONNECT_APP_NORMAL_USER_REFRESH_COUNT = "app_normal_user_refresh_count";
    //public static String CONNECT_STREAMER_USER_EVENT = "streamer_user_events";
    public static String CONNECT_APP_STREAMER_USER_EVENT = "app_streamer_user_events";
    public static String CONNECT_START_LIVE_STREAMING = "start_live_streaming";
    public static String CONNECT_NORMAL_STOP_LIVE_STREAMING = "normal_stop_live_streaming";
    public static String CONNECT_NORMAL_USER_COUNT = "normal_user_count";
    public static String CONNECT_STREAM_USER_EVENT_START = "streamer_user_event_start";
    public static String CONNECT_STREAM_USER_EVENT_STOP = "streamer_user_event_stop";
    public static String CONNECT_CONNECT_SUBSCRIBER = "connect_subscriber";
    public static String CONNECT_COUNT_SUBSCRIBER = "count_subscriber";
    public static String CONNECT_STOP_LIVE_STREAMING = "stop_live_streaming";
    public static String CONNECT_COMMENT = "comment";
    public static String CONNECT_COMMENT_ON = "comment_for_android";
    public static String DISCONNECT_USER = "user_disconnect";
    public static String CONNECT_ALL_USER_INTERVAL = "connect_all_user_interval";
    public static String CONNECT_ANDROID = "Android";
    public static String CONNECT_HEART_CLICK = "heart_click";
    public static String CONNECT_HEART_COUNT = "heart_count";
    public static String CONNECT_EVENT_ORDER_GENERATED_ON = "event_order_generated_on";

    //parameter
    public static String EVENT_ID = "event_id";
    public static String USER_ID = "user_id";
    public static String USER_TYPE = "user_type";
    public static String COMMENT = "comment";
    public static String CREATED_AT = "created_at";

    //for most popular screen and top screen live
    public static String CURRENT_LIVE_EVENT = "current_live_events";
    public static String COUNT_EVENT_SUBSCRIBE = "count_event_subscriber";

}
