package com.kuick.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("status")
    @Expose
    protected boolean status;
    @SerializedName("message")
    @Expose
    protected String message;
    @SerializedName("redirect_page")
    @Expose
    protected String redirect_page;
    @SerializedName("error")
    @Expose
    protected String error;
    @SerializedName("first_order_id")
    String first_order_id;

    public String getFirst_order_id() {
        return first_order_id;
    }

    public void setFirst_order_id(String first_order_id) {
        this.first_order_id = first_order_id;
    }

    public String getRedirect_page() {
        return redirect_page;
    }

    public void setRedirect_page(String redirect_page) {
        this.redirect_page = redirect_page;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
