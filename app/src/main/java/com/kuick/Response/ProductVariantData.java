package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

public class ProductVariantData {

    @SerializedName("streamer_id")
    String streamer_id;
    @SerializedName("product_variant")
    String product_variant;
    @SerializedName("currency_code")
    String currency_code;
    @SerializedName("message_type")
    String message_type;
    @SerializedName("message")
    String message;

    public String getStreamer_id() {
        return streamer_id;
    }

    public void setStreamer_id(String streamer_id) {
        this.streamer_id = streamer_id;
    }

    public String getProduct_variant() {
        return product_variant;
    }

    public void setProduct_variant(String product_variant) {
        this.product_variant = product_variant;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}