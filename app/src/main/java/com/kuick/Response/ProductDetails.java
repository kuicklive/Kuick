package com.kuick.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductDetails extends BaseResponse {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("centry_id")
    @Expose
    String centry_id;

    @SerializedName("category_id")
    @Expose
    String category_id;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("local_shipping")
    @Expose
    String local_shipping;

    @SerializedName("international_shipping")
    @Expose
    String international_shipping;

    @SerializedName("inventory_location")
    @Expose
    String inventory_location;

    @SerializedName("image")
    @Expose
    String image;

    @SerializedName("product_type")
    @Expose
    String product_type;

    @SerializedName("price")
    @Expose
    String price;

    @SerializedName("currency_code")
    @Expose
    String currency_code;

    @SerializedName("currency_symbol")
    @Expose
    String currency_symbol;

    @SerializedName("quantity")
    @Expose
    String quantity;

    @SerializedName("created_by")
    @Expose
    String created_by;

    @SerializedName("created_at")
    @Expose
    String created_at;

    @SerializedName("updated_by")
    @Expose
    String updated_by;

    @SerializedName("updated_at")
    @Expose
    String updated_at;

    @SerializedName("trash")
    @Expose
    String trash;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCentry_id() {
        return centry_id;
    }

    public void setCentry_id(String centry_id) {
        this.centry_id = centry_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
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

    public String getLocal_shipping() {
        return local_shipping;
    }

    public void setLocal_shipping(String local_shipping) {
        this.local_shipping = local_shipping;
    }

    public String getInternational_shipping() {
        return international_shipping;
    }

    public void setInternational_shipping(String international_shipping) {
        this.international_shipping = international_shipping;
    }

    public String getInventory_location() {
        return inventory_location;
    }

    public void setInventory_location(String inventory_location) {
        this.inventory_location = inventory_location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public void setCurrency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getTrash() {
        return trash;
    }

    public void setTrash(String trash) {
        this.trash = trash;
    }




}
