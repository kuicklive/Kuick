package com.kuick.Response;

import com.google.gson.annotations.SerializedName;
import com.kuick.base.BaseActivity;

import java.util.List;

public class NotificationResponse extends BaseResponse {


    public List<Notification> getNotification() {
        return notification;
    }

    public void setNotification(List<Notification> notification) {
        this.notification = notification;
    }

    @SerializedName("data")
    private List<Notification> notification;


    public static class Notification{

        @SerializedName("type_id")
        String type_id;

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getShow_date() {
            return show_date;
        }

        public void setShow_date(String show_date) {
            this.show_date = show_date;
        }

        public String getTime_ago() {
            return time_ago;
        }

        public void setTime_ago(String time_ago) {
            this.time_ago = time_ago;
        }

        @SerializedName("type")
        String type;

        @SerializedName("message")
        String message;

        @SerializedName("created_at")
        String created_at;

        @SerializedName("show_date")
        String show_date;

        @SerializedName("time_ago")
        String time_ago;

    }

}
