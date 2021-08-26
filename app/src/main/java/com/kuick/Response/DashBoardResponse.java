package com.kuick.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kuick.model.BannerDetails;
import com.kuick.model.Categories;
import com.kuick.model.FeatureStreamers;
import com.kuick.model.NewStreamers;
import com.kuick.model.RecommendedStreamers;
import com.kuick.model.TrendingStreamers;
import java.util.List;

public class DashBoardResponse extends BaseResponse {


    @SerializedName("current_datetime")
    @Expose
    private String current_time;

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

    // for recommended streamers
    @SerializedName("recommended_streamers")
    @Expose
    private List<RecommendedStreamers> recommendedStreamers;

    public List<RecommendedStreamers> getRecommendedStreamers() {
        return recommendedStreamers;
    }

    public void setRecommendedStreamers(List<RecommendedStreamers> recommendedStreamers) {
        this.recommendedStreamers = recommendedStreamers;
    }

    //for banner details
    @SerializedName("banners_details")
    @Expose
    private List<BannerDetails> bannerDetails;

    public List<BannerDetails> getBannerDetails() {
        return bannerDetails;
    }

    public void setBannerDetails(List<BannerDetails> bannerDetails) {
        this.bannerDetails = bannerDetails;
    }


    //featured_streamers
    @SerializedName("featured_streamers")
    @Expose
    private List<FeatureStreamers> featured_streamers;

    public List<FeatureStreamers> getFeatured_streamers() {
        return featured_streamers;
    }

    public void setFeatured_streamers(List<FeatureStreamers> featured_streamers) {
        this.featured_streamers = featured_streamers;
    }

    //feature_categories

    @SerializedName("feature_categories")
    @Expose
    private List<Categories> featureCategories;

    public List<Categories> getFeatureCategories() {
        return featureCategories;
    }

    public void setFeatureCategories(List<Categories> featureCategories) {
        this.featureCategories = featureCategories;
    }

    //trending_streamers
    @SerializedName("trending_streamers")
    @Expose
    private List<TrendingStreamers> trendingStreamers;

    public List<TrendingStreamers> getTrendingStreamers() {
        return trendingStreamers;
    }

    public void setTrendingStreamers(List<TrendingStreamers> trendingStreamers) {
        this.trendingStreamers = trendingStreamers;
    }

    // new streamers
    @SerializedName("new_streamers")
    @Expose
    private List<NewStreamers> newStreamers;

    public List<NewStreamers> getNewStreamers() {
        return newStreamers;
    }

    public void setNewStreamers(List<NewStreamers> newStreamers) {
        this.newStreamers = newStreamers;
    }

    public String getCart_count() {
        return cart_count;
    }

    public void setCart_count(String cart_count) {
        this.cart_count = cart_count;
    }

    @SerializedName("cart_count")
    String cart_count;


}
