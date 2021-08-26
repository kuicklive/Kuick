package com.kuick.interfaces;

import com.kuick.Response.CommonResponse;

public interface PriceCalculationListener {

    void hidePromo();
    void removeShipping();
    void removePaymentMethod();
    void onProductCalculation(CommonResponse commonResponse);


}
