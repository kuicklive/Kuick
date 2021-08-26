package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClipsData extends BaseResponse {

    @SerializedName("id")
    String id;
    @SerializedName("discount")
    String discount;
    @SerializedName("discount_price")
    String discount_price;
    @SerializedName("no_of_units")
    String no_of_units;
    @SerializedName("video_path")
    String video_path;
    @SerializedName("product_variant")
    String product_variant;
    @SerializedName("product_id")
    String product_id;
    @SerializedName("name")
    String name;
    @SerializedName("is_show")
    String is_show;
    @SerializedName("inventory_location")
    String inventory_location;
    @SerializedName("description")
    String description;
    @SerializedName("image")
    String image;
    @SerializedName("currency_code")
    String currency_code;
    @SerializedName("profile_pic")
    String profile_pic;
    @SerializedName("price")
    String price;
    @SerializedName("quantity")
    String quantity;
    @SerializedName("username")
    String username;
    @SerializedName("sizes")
    List<Sizes> sizes;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getInventory_location() {
        return inventory_location;
    }

    public void setInventory_location(String inventory_location) {
        this.inventory_location = inventory_location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getNo_of_units() {
        return no_of_units;
    }

    public void setNo_of_units(String no_of_units) {
        this.no_of_units = no_of_units;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public String getProduct_variant() {
        return product_variant;
    }

    public void setProduct_variant(String product_variant) {
        this.product_variant = product_variant;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public List<Sizes> getSizes() {
        return sizes;
    }

    public void setSizes(List<Sizes> sizes) {
        this.sizes = sizes;
    }

    public static class Sizes {

        @SerializedName("id")
        String id;
        @SerializedName("size")
        String size;
        @SerializedName("quantity")
        String quantity;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
    }

    public String getIs_show() {
        return is_show;
    }

    public void setIs_show(String is_show) {
        this.is_show = is_show;
    }
}
