package com.kuick.interfaces;

import com.kuick.Response.CommonResponse;

public interface LiveEventListener {

    void onClickComment(CommonResponse.Comments comments);
    void watcherUpdate(String count);
    void startPictureInPicture(String id, boolean destination, boolean isForOrderDetails);

}
