package com.kuick.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailResponse extends BaseResponse {

    @SerializedName("tracking_number_details")
    String tracking_number_details;
    @SerializedName("order_id")
    String order_id;
    @SerializedName("user_name")
    String user_name;
    @SerializedName("user_email")
    String user_email;
    @SerializedName("user_phone")
    String user_phone;
    @SerializedName("user_address")
    String user_address;
    @SerializedName("order_status")
    String order_status;
    @SerializedName("arrival_date")
    String arrival_date;
    @SerializedName("created_at")
    String created_at;
    @SerializedName("show_created_at")
    String show_created_at;
    @SerializedName("amount")
    String amount;
    @SerializedName("discount")
    String discount;
    @SerializedName("tax")
    String tax;
    @SerializedName("shipping_charge")
    String shipping_charge;
    @SerializedName("total_amount")
    String total_amount;
    @SerializedName("payment_method")
    String payment_method;
    @SerializedName("payment_status")
    String payment_status;
    @SerializedName("last_4")
    String last_4;
    @SerializedName("expiry_month")
    String expiry_month;
    @SerializedName("expiry_year")
    String expiry_year;
    @SerializedName("card_image")
    String card_image;
    @SerializedName("order_product_details")
    @Expose
    private List<OrderProductDetails> orderProductDetails;

    public String getTracking_number_details() {
        return tracking_number_details;
    }

    public void setTracking_number_details(String tracking_number_details) {
        this.tracking_number_details = tracking_number_details;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(String arrival_date) {
        this.arrival_date = arrival_date;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getShow_created_at() {
        return show_created_at;
    }

    public void setShow_created_at(String show_created_at) {
        this.show_created_at = show_created_at;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
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

    public List<OrderProductDetails> getOrderProductDetails() {
        return orderProductDetails;
    }

    public void setOrderProductDetails(List<OrderProductDetails> orderProductDetails) {
        this.orderProductDetails = orderProductDetails;
    }

    public static class OrderProductDetails {

        @SerializedName("name")
        String name;

        @SerializedName("image")
        String image;

        @SerializedName("product_price")
        String product_price;

        @SerializedName("num_of_items")
        String num_of_items;

        @SerializedName("total_price")
        String total_price;
        @SerializedName("product_variant")
        @Expose
        private OrderHistoryResponse.ProductVariant productVariant;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getProduct_price() {
            return product_price;
        }

        public void setProduct_price(String product_price) {
            this.product_price = product_price;
        }

        public String getNum_of_items() {
            return num_of_items;
        }

        public void setNum_of_items(String num_of_items) {
            this.num_of_items = num_of_items;
        }

        public String getTotal_price() {
            return total_price;
        }

        public void setTotal_price(String total_price) {
            this.total_price = total_price;
        }

        public OrderHistoryResponse.ProductVariant getProductVariant() {
            return productVariant;
        }

        public void setProductVariant(OrderHistoryResponse.ProductVariant productVariant) {
            this.productVariant = productVariant;
        }
    }
}
