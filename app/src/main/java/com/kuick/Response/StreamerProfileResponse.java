package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

public class StreamerProfileResponse extends BaseResponse {


    @SerializedName("id")
    String id;

    @SerializedName("main_banner")
    String main_banner;
    @SerializedName("type")
    String type;
    @SerializedName("description")
    String description;
    @SerializedName("website")
    String website;
    @SerializedName("whatsapp_url")
    String whatsapp_url;
    @SerializedName("facebook_url")
    String facebook_url;
    @SerializedName("facebook_messenger_url")
    String facebook_messenger_url;
    @SerializedName("instagram_url")
    String instagram_url;
    @SerializedName("linkedin_url")
    String linkedin_url;
    @SerializedName("twitter_url")
    String twitter_url;
    @SerializedName("youtube_url")
    String youtube_url;
    @SerializedName("tiktok_url")
    String tiktok_url;
    @SerializedName("twitch_url")
    String twitch_url;
    @SerializedName("pinterest_url")
    String pinterest_url;
    @SerializedName("username")
    String username;
    @SerializedName("full_name")
    String full_name;
    @SerializedName("country")
    String country;
    @SerializedName("profile_pic")
    String profile_pic;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMain_banner() {
        return main_banner;
    }

    public void setMain_banner(String main_banner) {
        this.main_banner = main_banner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWhatsapp_url() {
        return whatsapp_url;
    }

    public void setWhatsapp_url(String whatsapp_url) {
        this.whatsapp_url = whatsapp_url;
    }

    public String getFacebook_url() {
        return facebook_url;
    }

    public void setFacebook_url(String facebook_url) {
        this.facebook_url = facebook_url;
    }

    public String getFacebook_messenger_url() {
        return facebook_messenger_url;
    }

    public void setFacebook_messenger_url(String facebook_messenger_url) {
        this.facebook_messenger_url = facebook_messenger_url;
    }

    public String getInstagram_url() {
        return instagram_url;
    }

    public void setInstagram_url(String instagram_url) {
        this.instagram_url = instagram_url;
    }

    public String getLinkedin_url() {
        return linkedin_url;
    }

    public void setLinkedin_url(String linkedin_url) {
        this.linkedin_url = linkedin_url;
    }

    public String getTwitter_url() {
        return twitter_url;
    }

    public void setTwitter_url(String twitter_url) {
        this.twitter_url = twitter_url;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(String youtube_url) {
        this.youtube_url = youtube_url;
    }

    public String getTiktok_url() {
        return tiktok_url;
    }

    public void setTiktok_url(String tiktok_url) {
        this.tiktok_url = tiktok_url;
    }

    public String getTwitch_url() {
        return twitch_url;
    }

    public void setTwitch_url(String twitch_url) {
        this.twitch_url = twitch_url;
    }

    public String getPinterest_url() {
        return pinterest_url;
    }

    public void setPinterest_url(String pinterest_url) {
        this.pinterest_url = pinterest_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }


}
