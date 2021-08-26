package com.kuick.model;

import com.google.gson.annotations.SerializedName;
import com.kuick.Response.BaseResponse;

import java.io.Serializable;

public class BannerDetails extends BaseResponse implements Serializable {

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

    @SerializedName("banner_image")
    String banner_image;

    @SerializedName("banner_mobile_image")
    String banner_mobile_image;

    @SerializedName("rsvp_link")
    String rsvp_link;

    @SerializedName("banner_type")
    String banner_type;

    @SerializedName("clock_end_time")
    String clock_end_time;

    @SerializedName("live_stream_id")
    String live_stream_id;

    @SerializedName("live_streaming_name")
    String live_streaming_name;

    @SerializedName("live_streaming_slug")
    String live_streaming_slug;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBanner_image() {
        return banner_image;
    }

    public void setBanner_image(String banner_image) {
        this.banner_image = banner_image;
    }

    public String getBanner_mobile_image() {
        return banner_mobile_image;
    }

    public void setBanner_mobile_image(String banner_mobile_image) {
        this.banner_mobile_image = banner_mobile_image;
    }

    public String getRsvp_link() {
        return rsvp_link;
    }

    public void setRsvp_link(String rsvp_link) {
        this.rsvp_link = rsvp_link;
    }

    public String getBanner_type() {
        return banner_type;
    }

    public void setBanner_type(String banner_type) {
        this.banner_type = banner_type;
    }

    public String getClock_end_time() {
        return clock_end_time;
    }

    public void setClock_end_time(String clock_end_time) {
        this.clock_end_time = clock_end_time;
    }

    public String getLive_stream_id() {
        return live_stream_id;
    }

    public void setLive_stream_id(String live_stream_id) {
        this.live_stream_id = live_stream_id;
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


}
