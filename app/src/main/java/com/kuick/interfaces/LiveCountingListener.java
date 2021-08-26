package com.kuick.interfaces;

public interface LiveCountingListener {

    void refreshCount(String count);
    void showDialog();
    void onHeartClick(String count, String event_id, String user_id);
    void totalCount(String totalCount, String eventId);
}
