package com.kuick.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.kuick.R;
import com.kuick.Remote.EndPoints;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.ContactUsDetails;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityContactUsBinding;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.PARAM_EMAIL;
import static com.kuick.Remote.EndPoints.PARAM_FIRST_NAME;
import static com.kuick.Remote.EndPoints.PARAM_LAST_NAME;
import static com.kuick.Remote.EndPoints.PARAM_MESSAGE;

public class  ContactUs extends BaseActivity {

    private static final String TAG = "ContactUs";
    private ActivityContactUsBinding binding;
    private TextView txtTitle;
    private ImageView btnBack;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTollBar();
        setLanguageLable();
        getContactDetailsApi();
    }

    private void getContactDetailsApi() {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Call<ContactUsDetails> call = apiService.doContactUsDetails(userPreferences.getApiKey());
                call.enqueue(new Callback<ContactUsDetails>() {
                    @Override
                    public void onResponse(@NotNull Call<ContactUsDetails> call, @NotNull Response<ContactUsDetails> response) {
                        Utility.PrintLog(TAG, response.toString());

                        checkErrorCode(response.code());


                        if (response.body() != null) {

                            if (response.isSuccessful())
                            {

                                try {

                                   String siteAddress = response.body().getData().getSite_address();
                                   binding.address.setText(siteAddress);
                                   String help_center_email = response.body().getData().getHelp_center_email();
                                   binding.email.setText(help_center_email);
                                   phone = response.body().getData().getSite_phone_no();
                                   binding.phone.setText(phone +"  "+ language.getLanguage(KEY.whatsapp_chat));

                                }catch (Exception e)
                                {
                                    Utility.PrintLog(TAG, "Exception() " + response.body());
                                }

                            }else {

                                if (response.message()!=null)
                                {
                                    showSnackErrorMessage(response.message());
                                }
                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<ContactUsDetails> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, "onFailure exception : " + t.toString());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }
    }

    private void setTollBar()
    {
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        binding.btnSend.setOnClickListener(this);
        binding.layCall.setOnClickListener(this);
        binding.btnStreamToday.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    private void setLanguageLable()
    {
        try {

            if (language!=null )
            {
                txtTitle.setText(language.getLanguage(KEY.contact_us));
                binding.inputFirstName.setHint(language.getLanguage(KEY.first_name));
                binding.fillthe.setText(language.getLanguage(KEY.fill_the_form)+"\n"+language.getLanguage(KEY.its_easy));
                binding.inputLastName.setHint(language.getLanguage(KEY.last_name));
                binding.inputEmail.setHint(language.getLanguage(KEY.email));
                binding.btnSend.setText(language.getLanguage(KEY.send));
                binding.contactDetail.setText(language.getLanguage(KEY.contact_detail));
                binding.btnStreamToday.setText(language.getLanguage(KEY.stream_today));
                binding.doYouWantToLive.setText(language.getLanguage(KEY.do_you_want_to_live_stream));
                binding.youAreNowClick.setText(language.getLanguage(KEY.you_are_now_a_click_away_to_start_selling_your_products_via_live_streams_click_the_button_below_to_get_started));
                binding.txtMessage.setHint(language.getLanguage(KEY.message));

            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.btnSend:
                Utility.hideKeyboard(this);
                callContactUsAPI();
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
            case R.id.btnStreamToday:
                openDefaultBrowser(EndPoints.URL_STREAM_TODAY);
                break;
            case R.id.layCall:

                String phone = binding.phone.getText().toString().trim().replace("+","");

                    if (isAppInstalled("com.whatsapp"))
                    {
                       /* Uri uri = Uri.parse("smsto:" + phone);
                        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                        //i.putExtra("sms_body", smsText);
                        i.setPackage("com.whatsapp");
                        startActivity(i);*/

                        PackageManager packageManager = this.getPackageManager();
                        Intent i = new Intent(Intent.ACTION_VIEW);

                        try {
                            String url = "https://api.whatsapp.com/send?phone="+ phone;
                            i.setPackage("com.whatsapp");
                            i.setData(Uri.parse(url));
                            if (i.resolveActivity(packageManager) != null) {
                                startActivity(i);
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            Utility.PrintLog(TAG,"open whatsapp exception : "+ e.toString());
                            openDefaultBrowser("https://api.whatsapp.com/send/?phone=" + phone);
                        }

                    }else {

                        openDefaultBrowser("https://api.whatsapp.com/send/?phone=" + phone);
                    }
                    Utility.PrintLog(TAG,"phone number = "+ phone);

                break;
        }
    }

    private void callContactUsAPI() {

        try {

            if (checkInternetConnectionWithMessage())
            {

                String firstName = binding.edtFirstName.getText().toString();
                String lastName = binding.edtLastName.getText().toString();
                String email = binding.edtEmail.getText().toString();
                String message = binding.txtMessage.getText().toString();

                if (TextUtils.isEmpty(firstName)){
                    showSnackErrorMessage(language.getLanguage(KEY.please_enter_name));
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    showSnackErrorMessage(language.getLanguage(KEY.please_enter_email));
                    return;
                }

                if (!Utility.validEmail(email)){
                    showSnackErrorMessage(language.getLanguage(KEY.please_enter_proper_email));
                    return;
                }

                if (TextUtils.isEmpty(message)){
                    showSnackErrorMessage(language.getLanguage(KEY.message_must_required));
                    return;
                }

                showLoader(true);

                Map<String, String> contactUs = new HashMap<>();
                contactUs.put(PARAM_FIRST_NAME,firstName);
                contactUs.put(PARAM_LAST_NAME,lastName);
                contactUs.put(PARAM_EMAIL,email);
                contactUs.put(PARAM_MESSAGE,message);


                Call<BaseResponse> call = apiService.doContactUs(userPreferences.getApiKey(), contactUs);
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response)
                    {
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                        if (response.body() != null)
                        {

                            if (checkResponseStatusWithMessage(response.body(), true))
                            {
                                if (language!=null)
                                {
                                    showSnackResponse(language.getLanguage(KEY.thanks_for_filling_out_our_form));
                                }
                                setClear();

                            }
                        }
                        hideLoader();
                    }

                    private void setClear()
                    {
                        binding.edtFirstName.setText("");
                        binding.edtLastName.setText("");
                        binding.edtEmail.setText("");
                        binding.txtMessage.setText("");
                    }

                    @Override
                    public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
        }
    }

    public void openDefaultBrowser(String url)
    {
        try {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

        } catch (Exception e) {
            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}