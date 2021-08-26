package com.kuick.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderHistoryResponse extends BaseResponse {

    @SerializedName("order_id")
    String order_id;

    @SerializedName("order_status")
    String order_status;

    @SerializedName("created_at")
    String created_at;

    @SerializedName("quantity")
    String quantity;

    @SerializedName("total_amount")
    String total_amount;

    @SerializedName("name")
    String name;

    @SerializedName("image")
    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }


    @SerializedName("product_variant")
    @Expose
    private ProductVariant productVariant;

    public static class ProductVariant{

        @SerializedName("variation")
        String variation;

        @SerializedName("color")
        String color;

        @SerializedName("color_hex")
        String color_hex;

        @SerializedName("size")
        String size;

        public String getVariation() {
            return variation;
        }

        public void setVariation(String variation) {
            this.variation = variation;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getColor_hex() {
            return color_hex;
        }

        public void setColor_hex(String color_hex) {
            this.color_hex = color_hex;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

    }

}
