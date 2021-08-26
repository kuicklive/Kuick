package com.kuick.interfaces;

public interface ImageRefreshListener {

    void getImageUrl(String url);
    void discoverClick();
    void goToProductDetailsScreen(String product_id, boolean destination,boolean isFromOrderDetailsScreen);
    void showDialog();
}
