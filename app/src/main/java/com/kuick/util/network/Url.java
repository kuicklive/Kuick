package com.kuick.util.network;

import java.util.ArrayList;

import static com.kuick.Remote.EndPoints.BaseUrl;

public class Url
{

    public static ArrayList<String> getUnsupportedUrls()
    {
        ArrayList<String> urlList = new ArrayList<>();

        urlList.add( BaseUrl + "reset-password/");
        urlList.add( BaseUrl + "streamer-kuicker-reset-password/");
        urlList.add( BaseUrl + "streamer-sign-in");
        urlList.add( BaseUrl + "streamer-sign-up");
        urlList.add( BaseUrl + "kuicker-sign-in");
        urlList.add( BaseUrl + "kuicker-sign-up");
        urlList.add( BaseUrl + "streamer-kuicker-forgot-password");

        return urlList;
    }

    public static ArrayList<String> getSupportedUrls()
    {

        ArrayList<String> urlList = new ArrayList<>();
        urlList.add( BaseUrl + "streamers-home-data");
        urlList.add( BaseUrl + "about-us");
        urlList.add( BaseUrl + "contact-us");
        urlList.add( BaseUrl + "delivery");
        urlList.add( BaseUrl + "returns");
        urlList.add( BaseUrl + "privacy-policy");
        urlList.add( BaseUrl + "terms-of-service");
        urlList.add( BaseUrl + "terms-and-conditions");
        urlList.add( BaseUrl + "upcoming-live-streams");
        urlList.add( BaseUrl + "sign-in");
        urlList.add( BaseUrl + "forgot-password");
        urlList.add( BaseUrl + "sign-up");
        urlList.add( BaseUrl + "subscribe-use");
        urlList.add( BaseUrl + "thank-you-subscriber");
        urlList.add( BaseUrl + "no-started-yet");
        urlList.add( BaseUrl + "profile");
        urlList.add( BaseUrl + "order-history");
        urlList.add( BaseUrl + "order-details");
        urlList.add( BaseUrl + "change-password");
        urlList.add( BaseUrl + "manage-addresses");
        urlList.add( BaseUrl + "payment-settings");
        urlList.add( BaseUrl + "product-catalog");
        urlList.add( BaseUrl + "product-details");
        urlList.add( BaseUrl + "cart-details");
        urlList.add( BaseUrl + "live-stream-mobile"); // Total 25 URLs

        return urlList;
    }

}
