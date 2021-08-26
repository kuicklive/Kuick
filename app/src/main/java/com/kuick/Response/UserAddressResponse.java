package com.kuick.Response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserAddressResponse extends BaseResponse implements Serializable {


    @SerializedName("id")
    String id;

    @SerializedName("type")
    String type;

    @SerializedName("address")
    String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



}
