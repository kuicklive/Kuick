package com.kuick.SplashScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.kuick.R;
import com.kuick.Remote.EndPoints;
import com.kuick.Response.InitResponse;
import com.kuick.activity.HomeActivity;
import com.kuick.activity.LoginActivity;
import com.kuick.activity.SignupActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivitySecondSplashScreenBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.PATH_DEVICE_TYPE_VALUE;
import static com.kuick.util.comman.Constants.ALLOW;
import static com.kuick.util.comman.Constants.CANCEL;
import static com.kuick.util.comman.Constants.EN;
import static com.kuick.util.comman.Constants.ES;

public class SecondSplashScreen extends BaseActivity {

    private static final String TAG = "SecondSplashScreen";
    private ActivitySecondSplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondSplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        callInitAPI();
       // slideUp(binding.viewBottom,true);
        onClickListener();
        setLanguageSwitch();
        setLanguageLable();


        if (checkPermission(this,PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            permissionGranted = false;
        }

        new Handler().postDelayed(() -> slideUp(binding.mIcon,false),500);

    }
    public void callInitAPI() {

        try {

            Call<InitResponse> call = apiService.doInitCall(EndPoints.API_KEY, appVersion, PATH_DEVICE_TYPE_VALUE);

            if (checkInternetConnectionWithMessage()) {
                call.enqueue(new Callback<InitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<InitResponse> call, @NotNull Response<InitResponse> response) {
                        if (response.body() != null) {

                            if (checkResponseStatusWithMessage(response.body(), true)) {



                                userPreferences.setTimeZone(response.body().getTime_zone_in_GMT());
                                userPreferences.setCurrentTime(response.body().getCurrent_time());
                                userPreferences.setStripePublishKey(response.body().getStripe_publishablekey());
                                userPreferences.setImageBaseUrl(response.body().getImage_base_url());

                                if (response.body().getTax()!=null){
                                    userPreferences.setTaxStatus(response.body().getTax().getTax_status());
                                    userPreferences.setTaxType(response.body().getTax().getTax_type());
                                    userPreferences.setTaxAmount(response.body().getTax().getTax_amount());
                                }

                                hideLoader();

                            } else {
                                if (language!=null && language.getLanguage(response.body().getMessage())!=null){
                                    showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                                }


                            }
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<InitResponse> call, @NotNull Throwable t) {
                        showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            hideLoader();
            //  showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
        }
    }


    public void showInternetDialog(Context context,String title,String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                }).setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        }).show();
    }

    private void setLanguageSwitch() {
        if (language!=null && language.getLanguageCode()!=null && !language.getLanguageCode().equals("")){
            if (language.getLanguageCode().equals(EN)){
                switchLanguage(binding,false);
            }else {
                switchLanguage(binding,true);
            }
        }
    }

    private void setLanguageLable() {

       try {
           if (language!=null){
               binding.btnCreateNewAccount.setText(language.getLanguage(KEY.create_new_account));
               binding.btnSignIn.setText(language.getLanguage(KEY.sign_in));
           }
       }catch (Exception e){
           Utility.PrintLog(TAG,"exception : " + language);
       }

    }


    private boolean checkPermission(Context context, String... permissions) {

        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }

            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }  else {
                    PermissionDeny(Constants.PERMISSION_DENY,language.getLanguage(KEY.permission_denied_you_cannot_read_the_storage));
                }

                return;
            }
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnCreateNewAccount:
                goToNextScreen(this, SignupActivity.class,true);
                break;
            case R.id.btnSignIn:
                goToNextScreen(this,LoginActivity.class,true);
                break;
            case R.id.btnSwitch:
                if (binding.btnSwitch.isSelected()){
                    callLanguageAPI(EN, false,binding.btnSwitch);
                   // switchLanguage(binding,false);
                } else{
                    callLanguageAPI(ES, false,binding.btnSwitch);
                   // switchLanguage(binding,true);
                }

                break;

        }
    }
    private void onClickListener() {

            binding.btnCreateNewAccount.setOnClickListener(this);
            binding.btnSignIn.setOnClickListener(this);
            binding.btnSwitch.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

        finishAffinity();
    }



}