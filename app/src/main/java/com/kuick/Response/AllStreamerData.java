package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

public class AllStreamerData {


    String count;
    String isLive;
    String days = "00";
    String hours = "00";
    String minutes = "00";
    String seconds = "00";
    Long loadMilliSecond;
    @SerializedName("id")
    String id;
    @SerializedName("created_by")
    String created_by;
    @SerializedName("live_streaming_name")
    String live_streaming_name;
    @SerializedName("live_streaming_slug")
    String live_streaming_slug;
    @SerializedName("live_streaming_image")
    String live_streaming_image;
    @SerializedName("clock_end_time")
    String clock_end_time;
    @SerializedName("status")
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public Long getLoadMilliSecond() {
        return loadMilliSecond;
    }

    public void setLoadMilliSecond(Long loadMilliSecond) {
        this.loadMilliSecond = loadMilliSecond;
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

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getLive_streaming_name() {
        return live_streaming_name;
    }

    public void setLive_streaming_name(String live_streaming_name) {
        this.live_streaming_name = live_streaming_name;
    }

    public String getLive_streaming_slug() {
        return live_streaming_slug;
    }

    public void setLive_streaming_slug(String live_streaming_slug) {
        this.live_streaming_slug = live_streaming_slug;
    }

    public String getLive_streaming_image() {
        return live_streaming_image;
    }

    public void setLive_streaming_image(String live_streaming_image) {
        this.live_streaming_image = live_streaming_image;
    }

    public String getClock_end_time() {
        return clock_end_time;
    }

    public void setClock_end_time(String clock_end_time) {
        this.clock_end_time = clock_end_time;
    }


}
