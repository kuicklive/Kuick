package com.kuick.model;

import com.google.gson.annotations.SerializedName;
import com.kuick.Response.BaseResponse;

import java.io.Serializable;

public class UserCard extends BaseResponse implements Serializable {

    @SerializedName("id")
    String id;

    @SerializedName("name_on_card")
    String name_on_card;

    @SerializedName("last_4")
    String last_4;

    @SerializedName("expiry_month")
    String expiry_month;

    @SerializedName("expiry_year")
    String expiry_year;

    @SerializedName("card_image")
    String card_image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_on_card() {
        return name_on_card;
    }

    public void setName_on_card(String name_on_card) {
        this.name_on_card = name_on_card;
    }

    public String getLast_4() {
        return last_4;
    }

    public void setLast_4(String last_4) {
        this.last_4 = last_4;
    }

    public String getExpiry_month() {
        return expiry_month;
    }

    public void setExpiry_month(String expiry_month) {
        this.expiry_month = expiry_month;
    }

    public String getExpiry_year() {
        return expiry_year;
    }

    public void setExpiry_year(String expiry_year) {
        this.expiry_year = expiry_year;
    }

    public String getCard_image() {
        return card_image;
    }

    public void setCard_image(String card_image) {
        this.card_image = card_image;
    }
}
