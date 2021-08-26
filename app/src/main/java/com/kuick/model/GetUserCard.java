package com.kuick.model;

import com.google.gson.annotations.SerializedName;
import com.kuick.Response.BaseResponse;

public class GetUserCard extends BaseResponse {

    @SerializedName("zip_code")
    String zip_code;
    @SerializedName("name_on_card")
    String name_on_card;
    @SerializedName("card_number")
    String card_number;
    @SerializedName("expiry_month")
    String expiry_month;
    @SerializedName("expiry_year")
    String expiry_year;
    @SerializedName("cvv")
    String cvv;

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }


    public String getName_on_card() {
        return name_on_card;
    }

    public void setName_on_card(String name_on_card) {
        this.name_on_card = name_on_card;
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
}
