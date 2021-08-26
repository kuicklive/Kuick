package com.kuick.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewStreamers implements Serializable {

    @SerializedName("live_streaming_slug")
    String live_streaming_slug;

    public String getLive_streaming_slug() {
        return live_streaming_slug;
    }

    public void setLive_streaming_slug(String live_streaming_slug) {
        this.live_streaming_slug = live_streaming_slug;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @SerializedName("count")
    String count;

    @SerializedName("id")
    String id;

    @SerializedName("full_name")
    String full_name;

    @SerializedName("username")
    String username;

    @SerializedName("profile_pic")
    String profile_pic;

    @SerializedName("main_banner")
    String main_banner;

    @SerializedName("country")
    String country;

    @SerializedName("is_live")
    String is_live;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getMain_banner() {
        return main_banner;
    }

    public void setMain_banner(String main_banner) {
        this.main_banner = main_banner;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }


}
