package com.kuick.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.kuick.R;
import com.kuick.Remote.EndPoints;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityForgotPasswordBinding;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.kuick.Remote.EndPoints.PARAM_CONFIRM_PASSWORD;
import static com.kuick.Remote.EndPoints.PARAM_CURRENT_PASSWORD;
import static com.kuick.Remote.EndPoints.PARAM_NEW_PASSWORD;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.util.comman.Constants.SCREEN;
import static com.kuick.util.comman.Utility.PrintLog;
import static com.kuick.util.comman.Utility.hideInputError;
import static com.kuick.util.comman.Utility.showTextInputLayoutError;

public class ForgotPasswordChangePasswordActivity extends BaseActivity {

    private ActivityForgotPasswordBinding binding;
    private TextView txtTitle;
    private ImageView btnBack;
    private int screen = 0;
    private String TAG = "ForgotPassword";
    private String currentPassword ="",newPassword = "",conformPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        setLanguageLable();
    }

    private void setLanguageLable() {

        try {

            if (language!=null)
            {

                if (screen == 2)
                {
                    binding.inputCurrent.setHint(language.getLanguage(KEY.current_password));
                    binding.inputNew.setHint(language.getLanguage(KEY.new_password));
                    binding.inputConform.setHint(language.getLanguage(KEY.confirm_new_password));
                    binding.inputConform.setHint(language.getLanguage(KEY.confirm_new_password));
                    txtTitle.setText(language.getLanguage(KEY.change_password));

                }else{

                    txtTitle.setText(language.getLanguage(KEY.forgot_password));
                    binding.inputEmail.setHint(language.getLanguage(KEY.email));
                    binding.getRoot().findViewById(R.id.img_cart).setVisibility(View.GONE);
                }
                binding.btnSaveChanges.setText(language.getLanguage(KEY.submit));

            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"exception : " + language);
        }

    }


    private void setToolBar()
    {

        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);

        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        binding.btnSaveChanges.setOnClickListener(this);

        screen =  getIntent().getIntExtra(SCREEN,0);

        if (screen == 2)
        {
            txtTitle.setText(language.getLanguage(KEY.change_password));
            binding.mChangePasswordScreen.setVisibility(View.VISIBLE);
            binding.edtEmail.setVisibility(View.GONE);
            binding.inputEmail.setVisibility(View.GONE);
        }

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    private void hideForgotPasswordView()
    {
        binding.edtEmail.setVisibility(View.GONE);
        binding.inputEmail.setVisibility(View.GONE);
        binding.btnSaveChanges.setVisibility(View.GONE);
        binding.successMessage.setVisibility(View.VISIBLE);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.btnSaveChanges:
                hideKeyboardFrom(this,binding.layView);
                if (screen == 2){
                    changePassword();
                }else {
                    forgotPassword();
                }
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
        }
    }

    private void forgotPassword()
    {

        try {

            String email = binding.edtEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email))
            {
                showTextInputLayoutError(language.getLanguage(KEY.email_must_be_required), binding.inputEmail);
                return;
            }

            if (!Utility.validEmail(email))
            {
                showTextInputLayoutError(language.getLanguage(KEY.please_enter_proper_email), binding.inputEmail);
                return ;
            }

            hideInputError(binding.inputEmail);
            showLoader(true);

            if (checkInternetConnectionWithMessage())
            {

                Call<BaseResponse> call = apiService.doForgotPassword(EndPoints.API_KEY,email);
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response)
                    {
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                        if (!response.isSuccessful())
                        {
                            hideLoader();
                            return;
                        }
                        if (response.body()!=null)
                        {

                            if (checkResponseStatusWithMessage(response.body(), true))
                            {
                                hideForgotPasswordView();
                                binding.successMessage.setText(language.getLanguage(response.body().getMessage()));

                            }else showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                        }

                        hideLoader();
                    }
                    @Override
                    public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                        showSnackResponse(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        Utility.PrintLog(TAG, t.toString());
                        hideLoader();
                    }
                });

            } else hideLoader();

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            showSnackResponse(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }
    }

    private void changePassword()
    {

      try {

          currentPassword = binding.edtcurrent.getText().toString().trim();
          newPassword = binding.edtNewPassword.getText().toString().trim();
          conformPassword = binding.edtConformPassword.getText().toString().trim();

          if (TextUtils.isEmpty(currentPassword))
          {
              showTextInputLayoutError(language.getLanguage(KEY.current_password_must_be_required), binding.inputCurrent);
              return;
          }
          hideInputError(binding.inputCurrent);

          if (TextUtils.isEmpty(newPassword))
          {
              showTextInputLayoutError(language.getLanguage(KEY.new_password_must_be_required), binding.inputNew);
              return;
          }
          hideInputError(binding.inputNew);

          if (TextUtils.isEmpty(conformPassword))
          {
              showTextInputLayoutError(language.getLanguage(KEY.confirm_password_must_be_required), binding.inputConform);
              return;
          }
          hideInputError(binding.inputConform);

          if (!conformPassword.equals(newPassword))
          {
              showTextInputLayoutError(language.getLanguage(KEY.new_password_and_confirm_password_does_not_match), binding.inputConform);
              return;
          }
          hideInputError(binding.inputConform);

          PrintLog(TAG,currentPassword);
          PrintLog(TAG,newPassword);
          PrintLog(TAG,conformPassword);

          if (newPassword.length()  < 8)
          {
              showTextInputLayoutError(language.getLanguage(KEY.please_enter_minimum_8_character_password), binding.inputNew);
              return;
          }
          hideInputError(binding.inputNew);

          if (conformPassword.length()  < 8)
          {
              showTextInputLayoutError(language.getLanguage(KEY.please_enter_minimum_8_character_password), binding.inputConform);
              return;
          }
          hideInputError(binding.inputConform);

          callApiPasswordChange();

      }catch (Exception e){
          Utility.PrintLog(TAG,e.toString());
          showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
      }
    }

    private void callApiPasswordChange()
    {

        if (checkInternetConnectionWithMessage())
        {
            try {

                Map<String, String> changePassword = new HashMap<>();

                changePassword.put(PARAM_USER_ID, userPreferences.getUserId());
                changePassword.put(PARAM_CURRENT_PASSWORD, currentPassword);
                changePassword.put(PARAM_NEW_PASSWORD, newPassword);
                changePassword.put(PARAM_CONFIRM_PASSWORD, conformPassword);

                Utility.PrintLog(TAG, userPreferences.getApiKey() + "  User ID : " + userPreferences.getUserId());
                Call<CommonResponse> call = apiService.doPasswordChangeCall(userPreferences.getApiKey(), changePassword);

                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response)
                    {
                        Utility.PrintLog(TAG, response.toString());
                        checkErrorCode(response.code());

                        if (response.body()!=null)
                        {

                            if (checkResponseStatusWithMessage(response.body(), true))
                            {
                                hideChangePasswordView();
                                binding.successMessage.setText(language.getLanguage(response.body().getMessage()));
                                showSnackResponse(language.getLanguage(response.body().getMessage()));
                                userPreferences.saveCurrentUser(response.body());

                            }else showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                        hideLoader();
                    }
                });

            } catch (Exception e) {
                Utility.PrintLog(TAG, userPreferences.getApiKey() + "  User ID : " + userPreferences.getUserId());
                Utility.PrintLog(TAG, e.getMessage());
                showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            }
        }
    }

    private void hideChangePasswordView()
    {
        binding.mChangePasswordScreen.setVisibility(View.GONE);
        binding.btnSaveChanges.setVisibility(View.GONE);
        binding.successMessage.setVisibility(View.VISIBLE);
    }

    public static void hideKeyboardFrom(Context context, View view)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}