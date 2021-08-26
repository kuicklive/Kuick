package com.kuick.base;

import android.app.Activity;

public interface BaseView {
    void showLoader(boolean isClose);

    void hideLoader();

    void showMessageAndFinish(String message);

    void showSnackResponse(String message);

    Activity getViewActivity();

    void showSnackErrorMessage(String message);

    void onCancelCall();

}
