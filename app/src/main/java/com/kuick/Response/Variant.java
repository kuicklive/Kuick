package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Variant extends BaseResponse {


    @SerializedName("no_variant")
    private NoVariant noVariant;

    @SerializedName("size_variant")
    private SizeVariant sizeVariant;

    @SerializedName("color_variant")
    private List<ColorVariant> colorVariant;

    @SerializedName("size_color_variant")
    private List<SizeColorVariant> sizeColorVariant;

    @SerializedName("centry_variant")
    private CentryVariant centryVariant;
    @SerializedName("shopify_variant")
    private List<ShopifyVariant> shopify_variant;

    public List<ShopifyVariant> getShopify_variant() {
        return shopify_variant;
    }

    public void setShopify_variant(List<ShopifyVariant> shopify_variant) {
        this.shopify_variant = shopify_variant;
    }

    public CentryVariant getCentryVariant() {
        return centryVariant;
    }

    public void setCentryVariant(CentryVariant centryVariant) {
        this.centryVariant = centryVariant;
    }

    public List<SizeColorVariant> getSizeColorVariant() {
        return sizeColorVariant;
    }

    public void setSizeColorVariant(List<SizeColorVariant> sizeColorVariant) {
        this.sizeColorVariant = sizeColorVariant;
    }


    public NoVariant getNoVariant() {
        return noVariant;
    }

    public void setNoVariant(NoVariant noVariant) {
        this.noVariant = noVariant;
    }

    public SizeVariant getSizeVariant() {
        return sizeVariant;
    }

    public void setSizeVariant(SizeVariant sizeVariant) {
        this.sizeVariant = sizeVariant;
    }

    public List<ColorVariant> getColorVariant() {
        return colorVariant;
    }

    public void setColorVariant(List<ColorVariant> colorVariant) {
        this.colorVariant = colorVariant;
    }

    public static class ShopifyVariant {
        @SerializedName("id")
        String id;
        @SerializedName("title")
        String title;
        @SerializedName("price")
        String price;
        @SerializedName("quantity")
        String quantity;
        @SerializedName("discount_price")
        String discount_price;

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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
    }


    public static class CentryVariants {

        @SerializedName("id")
        String id;

        @SerializedName("color_id")
        String color_id;

        @SerializedName("size_id")
        String size_id;

        @SerializedName("quantity")
        String quantity;

        @SerializedName("color_data")
        ColorData color_data;

        @SerializedName("size_data")
        SizeData sizeData;
        @SerializedName("color_hex")
        SizeData color_hex;

        public SizeData getColor_hex() {
            return color_hex;
        }

        public void setColor_hex(SizeData color_hex) {
            this.color_hex = color_hex;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getColor_id() {
            return color_id;
        }

        public void setColor_id(String color_id) {
            this.color_id = color_id;
        }

        public String getSize_id() {
            return size_id;
        }

        public void setSize_id(String size_id) {
            this.size_id = size_id;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public ColorData getColor_data() {
            return color_data;
        }

        public void setColor_data(ColorData color_data) {
            this.color_data = color_data;
        }

        public SizeData getSizeData() {
            return sizeData;
        }

        public void setSizeData(SizeData sizeData) {
            this.sizeData = sizeData;
        }

        public static class ColorData {


            @SerializedName("name")
            String name;

            @SerializedName("hexadecimal")
            String hexadecimal;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getHexadecimal() {
                return hexadecimal;
            }

            public void setHexadecimal(String hexadecimal) {
                this.hexadecimal = hexadecimal;
            }

        }

        public static class SizeData {

            @SerializedName("name")
            String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

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

    public static class NoVariant {

        @SerializedName("price")
        String price;
        @SerializedName("discount_price")
        String discount_price;
        @SerializedName("quantity")
        String quantity;

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
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

    }

    public static class SizeVariant {

        @SerializedName("price")
        String price;
        @SerializedName("discount_price")
        String discount_price;
        @SerializedName("sizes")
        private List<Sizes> sizes;

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public List<Sizes> getSizes() {
            return sizes;
        }

        public void setSizes(List<Sizes> sizes) {
            this.sizes = sizes;
        }

    }

    public static class ColorVariant {

        @SerializedName("id")
        String id;

        @SerializedName("color")
        String color;
        @SerializedName("color_rgba")
        String color_rgba;
        @SerializedName("color_hex")
        String color_hex;
        @SerializedName("price")
        String price;
        @SerializedName("discount_price")
        String discount_price;
        @SerializedName("quantity")
        String quantity;

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
        }

        public String getColor_rgba() {
            return color_rgba;
        }

        public void setColor_rgba(String color_rgba) {
            this.color_rgba = color_rgba;
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public static class SizeColorVariant {

        @SerializedName("id")
        String id;

        @SerializedName("color")
        String color;
        @SerializedName("color_rgba")
        String color_rgba;
        @SerializedName("color_hex")
        String color_hex;
        @SerializedName("price")
        String price;
        @SerializedName("discount_price")
        String discount_price;
        @SerializedName("quantity")
        String quantity;
        @SerializedName("sizes")
        private List<Sizes> sizes;

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
        }

        public String getColor_rgba() {
            return color_rgba;
        }

        public void setColor_rgba(String color_rgba) {
            this.color_rgba = color_rgba;
        }

        public List<Sizes> getSizes() {
            return sizes;
        }

        public void setSizes(List<Sizes> sizes) {
            this.sizes = sizes;
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class CentryVariant {

        @SerializedName("price")
        String price;
        @SerializedName("discount_price")
        String discount_price;
        @SerializedName("centry_variants")
        List<CentryVariants> centryVariants;

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public List<CentryVariants> getCentryVariants() {
            return centryVariants;
        }

        public void setCentryVariants(List<CentryVariants> centryVariants) {
            this.centryVariants = centryVariants;
        }
    }
}
