package com.kuick.model;

import com.google.gson.annotations.SerializedName;

public class NormalUserRefreshCount {

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

    @SerializedName("live_streaming_slug")
    String live_streaming_slug;

    @SerializedName("count")
    String count;
}
