package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

public class CartDetails extends BaseResponse {

    @SerializedName("cart_id")
    String cart_id;

    @SerializedName("product_id")
    String product_id;

    @SerializedName("image")
    String image;

    @SerializedName("name")
    String name;

    @SerializedName("qty")
    String qty;

    @SerializedName("product_variant")
    String product_variant;

    @SerializedName("currency_code")
    String currency_code;

    @SerializedName("total_qty")
    String total_qty;

    @SerializedName("price")
    String price;

    @SerializedName("product_size")
    String product_size;

    @SerializedName("product_color")
    String product_color;
    @SerializedName("created_by")
    String created_by;

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
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

    public String getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(String total_qty) {
        this.total_qty = total_qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_size() {
        return product_size;
    }

    public void setProduct_size(String product_size) {
        this.product_size = product_size;
    }

    public String getProduct_color() {
        return product_color;
    }

    public void setProduct_color(String product_color) {
        this.product_color = product_color;
    }


}
