package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

public class CartOverviewData extends ProductVariantData {


    @SerializedName("sub_total")
    String sub_total;
    @SerializedName("discount_name")
    String discount_name;
    @SerializedName("discount")
    String discount;
    @SerializedName("tax")
    String tax;
    @SerializedName("shipping_charge")
    String shipping_charge;
    @SerializedName("total")
    String total;
    @SerializedName("o_total")
    String o_total;

    public String getO_total() {
        return o_total;
    }

    public void setO_total(String o_total) {
        this.o_total = o_total;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }

    public String getDiscount_name() {
        return discount_name;
    }

    public void setDiscount_name(String discount_name) {
        this.discount_name = discount_name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(String shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
