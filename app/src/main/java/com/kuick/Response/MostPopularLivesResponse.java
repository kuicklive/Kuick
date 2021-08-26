package com.kuick.Response;

import android.os.CountDownTimer;

import com.google.gson.annotations.SerializedName;

public class MostPopularLivesResponse {

    String count;
    String days = "00";
    String hours = "00";
    String minutes = "00";
    String seconds = "00";
    Long loadMilliSecond;
    long milliSecond = 0;
    CountDownTimer countDownTimer = null;
    @SerializedName("id")
    String id;
    @SerializedName("live_streaming_slug")
    String live_streaming_slug;
    @SerializedName("live_streaming_name")
    String live_streaming_name;
    @SerializedName("live_streaming_image")
    String live_streaming_image;
    @SerializedName("clock_end_time")
    String clock_end_time;
    @SerializedName("status")
    String status;
    @SerializedName("full_name")
    String full_name;
    @SerializedName("country")
    String country;
    @SerializedName("profile_pic")
    String profile_pic;
    @SerializedName("main_banner")
    String main_banner;
    @SerializedName("is_live")
    String is_live;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Long getLoadMilliSecond() {
        return loadMilliSecond;
    }

    public void setLoadMilliSecond(Long loadMilliSecond) {
        this.loadMilliSecond = loadMilliSecond;
    }

    public long getMilliSecond() {
        return milliSecond;
    }

    public void setMilliSecond(long milliSecond) {
        this.milliSecond = milliSecond;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLive_streaming_slug() {
        return live_streaming_slug;
    }

    public void setLive_streaming_slug(String live_streaming_slug) {
        this.live_streaming_slug = live_streaming_slug;
    }

    public String getLive_streaming_name() {
        return live_streaming_name;
    }

    public void setLive_streaming_name(String live_streaming_name) {
        this.live_streaming_name = live_streaming_name;
    }

    public String getClock_end_time() {
        return clock_end_time;
    }

    public void setClock_end_time(String clock_end_time) {
        this.clock_end_time = clock_end_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getMain_banner() {
        return main_banner;
    }

    public void setMain_banner(String main_banner) {
        this.main_banner = main_banner;
    }

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
    }

    public CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public void setCountDownTimer(CountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }

    public String getLive_streaming_image() {
        return live_streaming_image;
    }

    public void setLive_streaming_image(String live_streaming_image) {
        this.live_streaming_image = live_streaming_image;
    }
}
