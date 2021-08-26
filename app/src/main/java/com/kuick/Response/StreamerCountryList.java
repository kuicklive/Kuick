package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

public class StreamerCountryList extends BaseResponse {
    @SerializedName("country_id")
    String country_id;
    @SerializedName("name")
    String name;
    @SerializedName("count_province")
    String count_province;

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount_province() {
        return count_province;
    }

    public void setCount_province(String count_province) {
        this.count_province = count_province;
    }

}
