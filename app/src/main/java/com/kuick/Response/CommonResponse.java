package com.kuick.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kuick.model.Categories;
import com.kuick.model.GetUserCard;
import com.kuick.model.UserCard;
import com.kuick.model.UserDetail;

import java.io.Serializable;
import java.util.List;

public class CommonResponse extends BaseResponse implements Serializable {

    public List<ClipsData> getClipsData() {
        return clipsData;
    }

    public void setClipsData(List<ClipsData> clipsData) {
        this.clipsData = clipsData;
    }

    public String getClicks() {
        return clicks;
    }

    public void setClicks(String clicks) {
        this.clicks = clicks;
    }

    @SerializedName("clicks")
    String clicks;

    @SerializedName("clips_data")
    List<ClipsData> clipsData;

    @SerializedName("current_time")
    @Expose
    protected String server_current_time;
    @SerializedName("type")
    String type;
    @SerializedName("cart_count")
    String cart_count;
    @SerializedName("time_zone")
    String time_zone;
    @SerializedName("x-api-key")
    String x_api_key;
    @SerializedName("streamer_details")
    @Expose
    StreamerProfileResponse streamerProfileResponse;
    @SerializedName("all_streamer_data")
    @Expose
    List<AllStreamerData> allStreamerData;
    @SerializedName("slug_data")
    @Expose
    SlugData slugData;
    @SerializedName("comments")
    @Expose
    List<Comments> comments;
    @SerializedName("products_data")
    @Expose
    List<ProductData> productData;
    @SerializedName("product_details")
    ProductDetails productDetails;
    @SerializedName("product_images")
    @Expose
    List<ProductImages> productImages;
    @SerializedName("variant_pass_data")
    Variant variant;
    @SerializedName("tax")
    String tax;
    @SerializedName("total")
    String total;
    @SerializedName("categories")
    @Expose
    private List<Categories> categories;
    @SerializedName("category_data")
    private CateGoryData cateGoryData;
    @SerializedName("current_datetime")
    @Expose
    private String current_time;
    @SerializedName("data")
    private List<MostPopularLivesResponse> mostPopularLivesResponses;
    @SerializedName("user_details")
    @Expose
    private UserDetail user;
    @SerializedName("user_addresses")
    @Expose
    private List<UserAddressResponse> userAddressResponse;
    @SerializedName("user_orders")
    @Expose
    private List<OrderHistoryResponse> orderHistoryResponse;
    @SerializedName("order_details")
    @Expose
    private OrderDetailResponse orderDetailResponses;
    @SerializedName("user_cards")
    private List<UserCard> userCard;
    @SerializedName("cart_details")
    @Expose
    private List<CartDetails> cartDetails;
    @SerializedName("card_data")
    @Expose
    private GetUserCard getUserCard;
    @SerializedName("shipping_charge")
    @Expose
    private List<ShippingCharge> shippingCharges;
    @SerializedName("rates")
    @Expose
    private Rates rates;

    @SerializedName("discount_array")
    @Expose
    private PromoCodeDiscount promoCodeDiscount;

    @SerializedName("product_variant_data")
    private ProductVariantData productVariantData;
    @SerializedName("cart_overview_data")
    private CartOverviewData cartOverviewData;
    @SerializedName("streamer_countries")
    private List<StreamerCountryList> streamer_countries;

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<StreamerCountryList> getStreamer_countries() {
        return streamer_countries;
    }

    public void setStreamer_countries(List<StreamerCountryList> streamer_countries) {
        this.streamer_countries = streamer_countries;
    }

    public CartOverviewData getCartOverviewData() {
        return cartOverviewData;
    }

    public void setCartOverviewData(CartOverviewData cartOverviewData) {
        this.cartOverviewData = cartOverviewData;
    }

    public ProductVariantData getProductVariantData() {
        return productVariantData;
    }

    public void setProductVariantData(ProductVariantData productVariantData) {
        this.productVariantData = productVariantData;
    }


    public String getServer_current_time() {
        return server_current_time;
    }

    public void setServer_current_time(String server_current_time) {
        this.server_current_time = server_current_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public void setCategories(List<Categories> featureCategories) {
        this.categories = featureCategories;
    }

    public CateGoryData getCateGoryData() {
        return cateGoryData;
    }

    public void setCateGoryData(CateGoryData cateGoryData) {
        this.cateGoryData = cateGoryData;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

    public String getCart_count() {
        return cart_count;
    }

    public void setCart_count(String cart_count) {
        this.cart_count = cart_count;
    }

    public List<MostPopularLivesResponse> getMostPopularLivesResponses() {
        return mostPopularLivesResponses;
    }

    public void setMostPopularLivesResponses(List<MostPopularLivesResponse> mostPopularLivesResponses) {
        this.mostPopularLivesResponses = mostPopularLivesResponses;
    }

    public PromoCodeDiscount getPromoCodeDiscount() {
        return promoCodeDiscount;
    }

    public void setPromoCodeDiscount(PromoCodeDiscount promoCodeDiscount) {
        this.promoCodeDiscount = promoCodeDiscount;
    }

    public UserDetail getUser() {
        return user;
    }

    public void setUser(UserDetail user) {
        this.user = user;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getX_api_key() {
        return x_api_key;
    }

    public void setX_api_key(String x_api_key) {
        this.x_api_key = x_api_key;
    }

    public List<UserAddressResponse> getUserAddressResponse() {
        return userAddressResponse;
    }

    public void setUserAddressResponse(List<UserAddressResponse> userAddressResponse) {
        this.userAddressResponse = userAddressResponse;
    }

    public List<OrderHistoryResponse> getOrderHistoryResponse() {
        return orderHistoryResponse;
    }

    public void setOrderHistoryResponse(List<OrderHistoryResponse> orderHistoryResponse) {
        this.orderHistoryResponse = orderHistoryResponse;
    }

    public OrderDetailResponse getOrderDetailResponses() {
        return orderDetailResponses;
    }

    public void setOrderDetailResponses(OrderDetailResponse orderDetailResponses) {
        this.orderDetailResponses = orderDetailResponses;
    }

    public List<UserCard> getUserCard() {
        return userCard;
    }

    public void setUserCard(List<UserCard> userCard) {
        this.userCard = userCard;
    }

    public StreamerProfileResponse getStreamerProfileResponse() {
        return streamerProfileResponse;
    }

    public void setStreamerProfileResponse(StreamerProfileResponse streamerProfileResponse) {
        this.streamerProfileResponse = streamerProfileResponse;
    }

    public List<AllStreamerData> getAllStreamerData() {
        return allStreamerData;
    }

    public void setAllStreamerData(List<AllStreamerData> allStreamerData) {
        this.allStreamerData = allStreamerData;
    }

    public SlugData getSlugData() {
        return slugData;
    }

    public void setSlugData(SlugData slugData) {
        this.slugData = slugData;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public List<ProductData> getProductData() {
        return productData;
    }

    public void setProductData(List<ProductData> productData) {
        this.productData = productData;
    }

    public ProductDetails getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ProductDetails productDetails) {
        this.productDetails = productDetails;
    }

    public List<ProductImages> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImages> productImages) {
        this.productImages = productImages;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public List<CartDetails> getCartDetails() {
        return cartDetails;
    }

    public void setCartDetails(List<CartDetails> cartDetails) {
        this.cartDetails = cartDetails;
    }

    public GetUserCard getGetUserCard() {
        return getUserCard;
    }

    public void setGetUserCard(GetUserCard getUserCard) {
        this.getUserCard = getUserCard;
    }

    public List<ShippingCharge> getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(List<ShippingCharge> shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public Rates getRates() {
        return rates;
    }

    public void setRates(Rates rates) {
        this.rates = rates;
    }

    public static class SlugData implements Serializable {

        @SerializedName("id")
        String id;

        @SerializedName("live_streaming_slug")
        String live_streaming_slug;

        @SerializedName("streaming_mode")
        String streaming_mode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLive_streaming_slug() {
            return live_streaming_slug;
        }

        public void setLive_streaming_slug(String live_streaming_slug) {
            this.live_streaming_slug = live_streaming_slug;
        }

        public String getStreaming_mode() {
            return streaming_mode;
        }

        public void setStreaming_mode(String streaming_mode) {
            this.streaming_mode = streaming_mode;
        }

    }

    public static class Comments {

        @SerializedName("comment")
        String comment;

        @SerializedName("full_name")
        String full_name;

        @SerializedName("profile_image")
        String profile_image;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }


    }

    public static class ProductData {

        @SerializedName("id")
        String id;

        @SerializedName("name")
        String name;

        @SerializedName("image")
        String image;

        @SerializedName("currency_code")
        String currency_code;

        @SerializedName("product_type")
        String product_type;

        @SerializedName("price")
        String price;

        @SerializedName("quantity")
        String quantity;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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

        public String getCurrency_code() {
            return currency_code;
        }

        public void setCurrency_code(String currency_code) {
            this.currency_code = currency_code;
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

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

    }

    public static class ProductImages {

        @SerializedName("id")
        String id;

        @SerializedName("product_id")
        String product_id;

        @SerializedName("image")
        String image;

        @SerializedName("created_at")
        String created_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

    }

    public static class ShippingCharge {

        @SerializedName("currency_code")
        String currency_code;
        @SerializedName("shipping_charge")
        String shipping_charge;

        public String getCurrency_code() {
            return currency_code;
        }

        public void setCurrency_code(String currency_code) {
            this.currency_code = currency_code;
        }

        public String getShipping_charge() {
            return shipping_charge;
        }

        public void setShipping_charge(String shipping_charge) {
            this.shipping_charge = shipping_charge;
        }


    }

    public static class Rates {

        @SerializedName("EUR")
        String EUR;

        @SerializedName("USD")
        String USD;

        @SerializedName("MXN")
        String MXN;

        @SerializedName("SGD")
        String SGD;

        @SerializedName("MYR")
        String MYR;

        @SerializedName("PHP")
        String PHP;

        @SerializedName("CLP")
        String CLP;

        @SerializedName("IDR")
        String IDR;


        public String getEUR() {
            return EUR;
        }

        public void setEUR(String EUR) {
            this.EUR = EUR;
        }

        public String getUSD() {
            return USD;
        }

        public void setUSD(String USD) {
            this.USD = USD;
        }

        public String getMXN() {
            return MXN;
        }

        public void setMXN(String MXN) {
            this.MXN = MXN;
        }

        public String getSGD() {
            return SGD;
        }

        public void setSGD(String SGD) {
            this.SGD = SGD;
        }

        public String getMYR() {
            return MYR;
        }

        public void setMYR(String MYR) {
            this.MYR = MYR;
        }

        public String getPHP() {
            return PHP;
        }

        public void setPHP(String PHP) {
            this.PHP = PHP;
        }

        public String getCLP() {
            return CLP;
        }

        public void setCLP(String CLP) {
            this.CLP = CLP;
        }

        public String getIDR() {
            return IDR;
        }

        public void setIDR(String IDR) {
            this.IDR = IDR;
        }
    }


    public static class PromoCodeDiscount  {

        @SerializedName("id")
        String id;

        @SerializedName("name")
        String name;

        @SerializedName("type")
        String type;

        @SerializedName("currency_code")
        String currency_code;

        @SerializedName("currency_symbol")
        String currency_symbol;

        @SerializedName("value")
        String value;

        @SerializedName("promocode_type")
        String promocode_type;
        @SerializedName("created_for")
        String created_for;
        @SerializedName("product_ids")
        String product_ids;

        public String getProduct_ids() {
            return product_ids;
        }

        public void setProduct_ids(String product_ids) {
            this.product_ids = product_ids;
        }

        public String getCreated_for() {
            return created_for;
        }

        public void setCreated_for(String created_for) {
            this.created_for = created_for;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPromocode_type() {
            return promocode_type;
        }

        public void setPromocode_type(String promocode_type) {
            this.promocode_type = promocode_type;
        }
    }

    public static class CateGoryData {

        @SerializedName("name")
        String name;
        @SerializedName("image")
        String image;

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

    }

}
