package com.kuick.interfaces;

import com.kuick.model.UserCard;

public interface PaymentInformationListener {

    void onClickPaypalCheckBox();
    void onClickCardCheckBox(UserCard card, int position);
    void onAddPaymentCard(String name,String cardNumber,String expiry);

}
