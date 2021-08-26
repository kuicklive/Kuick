package com.kuick.util.network;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NetworkError {


    @Expose
    @SerializedName("code")
    int statusCode;

    @Expose
    @SerializedName("message")
    String message;

    public NetworkError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
