package com.kuick.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InitResponse extends BaseResponse {

    @SerializedName("maintenance")
    String maintenance;
    @SerializedName("update")
    String update;
    @SerializedName("time_zone_in_GMT")
    String time_zone_in_GMT;
    @SerializedName("time_zone")
    @Expose
    String time_zone;
    @SerializedName("image_base_url")
    @Expose
    String image_base_url;
    @SerializedName("stripe_secretkey")
    @Expose
    String stripe_secretkey;
    @SerializedName("stripe_publishablekey")
    @Expose
    String stripe_publishablekey;
    @SerializedName("tax")
    private Tax tax;
    @SerializedName("current_time")
    @Expose
    private String current_time;

    public String getMessage_key() {
        return message_key;
    }

    public void setMessage_key(String message_key) {
        this.message_key = message_key;
    }

    @SerializedName("message_key")
    @Expose
    private String message_key;

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }

    public String getTime_zone_in_GMT() {
        return time_zone_in_GMT;
    }

    public void setTime_zone_in_GMT(String time_zone_in_GMT) {
        this.time_zone_in_GMT = time_zone_in_GMT;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public String getImage_base_url() {
        return image_base_url;
    }

    public void setImage_base_url(String image_base_url) {
        this.image_base_url = image_base_url;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getStripe_secretkey() {
        return stripe_secretkey;
    }

    public void setStripe_secretkey(String stripe_secretkey) {
        this.stripe_secretkey = stripe_secretkey;
    }

    public String getStripe_publishablekey() {
        return stripe_publishablekey;
    }

    public void setStripe_publishablekey(String stripe_publishablekey) {
        this.stripe_publishablekey = stripe_publishablekey;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

    public static class Tax {

        @SerializedName("tax_status")
        String tax_status;
        @SerializedName("tax_type")
        String tax_type;
        @SerializedName("tax_amount")
        String tax_amount;

        public String getTax_status() {
            return tax_status;
        }

        public void setTax_status(String tax_status) {
            this.tax_status = tax_status;
        }

        public String getTax_type() {
            return tax_type;
        }

        public void setTax_type(String tax_type) {
            this.tax_type = tax_type;
        }

        public String getTax_amount() {
            return tax_amount;
        }

        public void setTax_amount(String tax_amount) {
            this.tax_amount = tax_amount;
        }


    }

}
