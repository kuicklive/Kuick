package com.kuick.activity;

import com.google.gson.annotations.SerializedName;
import com.kuick.Response.BaseResponse;

public class ContactUsDetails extends BaseResponse {

    @SerializedName("data")
    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("site_address")
        String site_address;
        @SerializedName("help_center_email")
        String help_center_email;
        @SerializedName("site_phone_no")
        String site_phone_no;

        public String getSite_address() {
            return site_address;
        }

        public void setSite_address(String site_address) {
            this.site_address = site_address;
        }

        public String getHelp_center_email() {
            return help_center_email;
        }

        public void setHelp_center_email(String help_center_email) {
            this.help_center_email = help_center_email;
        }

        public String getSite_phone_no() {
            return site_phone_no;
        }

        public void setSite_phone_no(String site_phone_no) {
            this.site_phone_no = site_phone_no;
        }
    }
}
