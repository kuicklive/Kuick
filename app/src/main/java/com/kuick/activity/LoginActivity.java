package com.kuick.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityLoginBinding;
import com.kuick.util.RandomString;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.countrypicker.CountryPicker;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.API_KEY;
import static com.kuick.Remote.EndPoints.PARAM_DEVICE_TOKEN;
import static com.kuick.Remote.EndPoints.PARAM_DEVICE_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_DEVICE_TYPE_VALUE;
import static com.kuick.Remote.EndPoints.PARAM_EMAIL;
import static com.kuick.Remote.EndPoints.PARAM_FULL_NAME;
import static com.kuick.Remote.EndPoints.PARAM_PASSWORD;
import static com.kuick.Remote.EndPoints.PARAM_SOCIAL_ID;
import static com.kuick.Remote.EndPoints.PARAM_SOCIAL_TYPE;
import static com.kuick.util.comman.Constants.BASE_GRAPH_URL;
import static com.kuick.util.comman.Constants.EN;
import static com.kuick.util.comman.Constants.END_POINT_GRAPH_URL;
import static com.kuick.util.comman.Constants.ES;
import static com.kuick.util.comman.Constants.FACEBOOK;
import static com.kuick.util.comman.Constants.FACEBOOK_EMAIL;
import static com.kuick.util.comman.Constants.FACEBOOK_ID;
import static com.kuick.util.comman.Constants.FACEBOOK_NAME;
import static com.kuick.util.comman.Constants.GOOGLE;
import static com.kuick.util.comman.Constants.INTENT_KEY_SOCIAL_ID;
import static com.kuick.util.comman.Constants.INTENT_KEY_USERNAME;
import static com.kuick.util.comman.Constants.INTENT_KEY_USER_EMAIL;
import static com.kuick.util.comman.Constants.SCREEN;
import static com.kuick.util.comman.Constants.SOCIAL_TYPE;
import static com.kuick.util.comman.Utility.MIN_PASSWORD_LENGTH;
import static com.kuick.util.comman.Utility.PrintLog;
import static com.kuick.util.comman.Utility.hideInputError;
import static com.kuick.util.comman.Utility.isPasswordEmpty;
import static com.kuick.util.comman.Utility.showTextInputLayoutError;

public class LoginActivity extends BaseActivity {


    private ActivityLoginBinding binding;
    public static String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    private String firebaseToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupView();
        getKeyHash();
        getToken();
        setLanguageLable();
        setLanguageSwitch();


        binding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hideInputError(binding.tilEmail);
            }
        });

        binding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hideInputError(binding.tilPassword);
            }
        });


    }
    private void setLanguageSwitch() {
        if (language!=null && language.getLanguageCode()!=null && !language.getLanguageCode().equals("")){
            if (language.getLanguageCode().equals(EN)){
                switchSetup(binding, false);
            }else {
                switchSetup(binding, true);
            }
        }
    }


    private void setLanguageLable() {

        try {

            if (language != null) {

                binding.tilEmail.setHint(language.getLanguage(KEY.email));
                binding.tilPassword.setHint(language.getLanguage(KEY.password));
                binding.mForgotPassword.setText(language.getLanguage(KEY.forgot_password));
                binding.mForgotPassword.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

                binding.txtButtonSignIn.setText(language.getLanguage(KEY.sign_in));
                binding.orSignUpWith.setText(language.getLanguage(KEY.or_sign_in_with));
                binding.dontHaveAccount.setText(language.getLanguage(KEY.dont_have_an_account));
                binding.txtBtnSignUp.setText(language.getLanguage(KEY.sign_up_app));
                binding.txtBtnSignUp.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                String welcome_back = language.getLanguage(KEY.welcome_back);
                String welcome = welcome_back.split(" ")[0];
                String back = welcome_back.split(" ")[1];
                binding.welcomeBack.setText(welcome +"\n"+back);

            }
        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }

    }

    private void getToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Utility.PrintLog(TAG, "Fetching FCM registration token failed : "+ task.getException());
                        return;
                    }

                    firebaseToken = task.getResult();
                    Utility.PrintLog(TAG, "Token : "+ firebaseToken);
                });

        if (firebaseToken == null)
        {
            String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
            RandomString tickets = new RandomString(23, new SecureRandom(), easy);

            Utility.PrintLog(TAG,"randomtoken = " + tickets.nextString());


            firebaseToken = tickets.nextString();
        }

    }

    private void onClickFacebookLogin() {
        Constants.SOCIAL_TYPE  = FACEBOOK;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, permissionNeeds);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Utility.PrintLog(TAG,"success : ");
                GraphRequest request = getRequest(loginResult);

                Bundle parameters = new Bundle();
                parameters.putString(
                        "fields",
                        FACEBOOK_EMAIL + "," + FACEBOOK_NAME + "," + FACEBOOK_ID);
                request.setParameters(parameters);
                request.executeAsync();


                if (isFbLoggedIn())
                        LoginManager.getInstance().logOut();
            }

            private GraphRequest getRequest(LoginResult loginResult) {
               return GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),

                        (object, response) -> {

                            try {
                                String name = object.getString(FACEBOOK_NAME);
                                String email = object.getString(FACEBOOK_EMAIL);
                                String fbUserID = object.getString(FACEBOOK_ID);
                                PrintLog(TAG, "user detail : " + object);

                                String fbUserProfilePics = null;
                                if (!TextUtils.isEmpty(fbUserID)) {
                                    fbUserProfilePics = BASE_GRAPH_URL + fbUserID + END_POINT_GRAPH_URL;
                                }
                                final Uri profilePicUri = Uri.parse(fbUserProfilePics);

                                callForSocial(email, profilePicUri, name,fbUserID);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                );
            }

            @Override
            public void onCancel() {
                Utility.PrintLog(TAG,"cancel : ");
                disconnectGoogleAPIClient();
            }

            @Override
            public void onError(FacebookException error) {
                Utility.PrintLog(TAG,"error : " + error.toString());
                disconnectGoogleAPIClient();
            }
        });

    }

    public void showEmailNotExistDialog(String name, String email, String socialId)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, 0);
        builder.setCancelable(false);
        //builder.setTitle("NOTE");

        if(SOCIAL_TYPE!=null && SOCIAL_TYPE.equals(GOOGLE))
        {
            builder.setMessage(language.getLanguage(KEY.google_did_not_provide_your_country_please_enter_this_information_to_access_the_kuick_app));

        }else if (SOCIAL_TYPE!=null && SOCIAL_TYPE.equals(FACEBOOK)){
            builder.setMessage(language.getLanguage(KEY.facebook_did_not_provide_your_email_address_or_country_please_enter_this_information_to_access_the_kuick_app));
        }

        String positiveText = language.getLanguage(KEY.ok);
        builder.setPositiveButton(positiveText, null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.bgSplash)));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
        {
            dialog.dismiss();

            Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
            intent.putExtra(INTENT_KEY_USERNAME,name);
            intent.putExtra(INTENT_KEY_USER_EMAIL,email);
            intent.putExtra(INTENT_KEY_SOCIAL_ID,socialId);
            startActivity(intent);
        });
    }



    private void callForSocial(String email, Uri profilePicUri, String name, String socialId) {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Map<String, String> socialLoginDetail = new HashMap<>();

                socialLoginDetail.put(PARAM_SOCIAL_TYPE,Constants.SOCIAL_TYPE);
                socialLoginDetail.put(PARAM_SOCIAL_ID,socialId);
                socialLoginDetail.put(PARAM_EMAIL,email);
                socialLoginDetail.put(PARAM_FULL_NAME,name);
                socialLoginDetail.put(PARAM_DEVICE_TYPE,PARAM_DEVICE_TYPE_VALUE);
                socialLoginDetail.put(PARAM_DEVICE_TOKEN,firebaseToken);

                Call<CommonResponse> call = apiService.doSocialLogin(API_KEY,socialLoginDetail);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        PrintLog(TAG, "response : " + response);

                        checkErrorCode(response.code());

                       if (response.body()!=null){
                           if (checkResponseStatusWithMessage(response.body(), true)) {
                               PrintLog(TAG, "user already exist : " + language.getLanguage(response.body().getMessage()));
                               showSnackResponse(language.getLanguage(response.body().getMessage()));
                               saveData(response.body());
                           }else {

                               if (response.body().getType()!=null && response.body().getType().equals(Constants.countryScreen))
                               {
                                   PrintLog(TAG, "Social User does not exist : " + language.getLanguage(response.body().getMessage()));

                                       showEmailNotExistDialog(name,email,socialId);


                               }else {
                                   disconnectGoogleAPIClient();
                                   showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                                   PrintLog(TAG, "when user deactivated : " + language.getLanguage(response.body().getMessage()));
                               }
                           }
                       }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        //showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }
    }



    private void getKeyHash() {
        try
        {

            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Utility.PrintLog(TAG,"key hash : " +  Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            PrintLog(TAG,"exception : " + e.toString());
        }
    }


    private void setupView() {
        PushDownAnim.setPushDownAnimTo(binding.btnSwitch, binding.btnFb, binding.btnGoogle);

        new Handler().post(() -> {
            binding.btnSignIn.setOnClickListener(LoginActivity.this);
            binding.btnFb.setOnClickListener(LoginActivity.this);
            binding.btnGoogle.setOnClickListener(LoginActivity.this);
            binding.btnSignIn.setOnClickListener(LoginActivity.this);
            binding.txtBtnSignUp.setOnClickListener(LoginActivity.this);
            binding.btnSwitch.setOnClickListener(LoginActivity.this);
            binding.mForgotPassword.setOnClickListener(LoginActivity.this);
        });
    }

    private boolean validateFieldWithMessage() {

        boolean isValid = false;
        String email = binding.edtEmail.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showTextInputLayoutError(language.getLanguage(KEY.please_enter_email), binding.tilEmail);
            return isValid;
        }
        if (!Utility.validEmail(email)) {
            showTextInputLayoutError(language.getLanguage(KEY.please_enter_proper_email), binding.tilEmail);
            return isValid;
        }
        hideInputError(binding.tilEmail);

        if (isPasswordEmpty(password)) {
            showTextInputLayoutError(language.getLanguage(KEY.please_enter_password), binding.tilPassword);
            return isValid;
        }
        hideInputError(binding.tilPassword);
       /* if (password.length() < MIN_PASSWORD_LENGTH) {
            showTextInputLayoutError(baseActivity.language.getLanguage(KEY.please_enter_minimum_8_character_password), binding.tilPassword);
            return isValid;
        }*/


        isValid = true;

        return isValid;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                    Utility.hideKeyboard(this);
                    onClickSignIn();
                break;

            case R.id.btnSwitch:

                if (binding.btnSwitch.isSelected()){
                    callLanguageAPI(EN, false, binding.btnSwitch);
                } else{
                    callLanguageAPI(ES, false, binding.btnSwitch);
                }
                break;
            case R.id.txtBtnSignUp:
                goToNextScreen(this, SignupActivity.class, true);
                break;
            case R.id.mForgotPassword:
                Intent intent = new Intent(this, ForgotPasswordChangePasswordActivity.class);
                intent.putExtra(SCREEN, 1);
                startActivity(intent);
                break;
            case R.id.btnFb:
                onClickFacebookLogin();
                break;
            case R.id.btnGoogle:
                onClickGoogleLogin();
                break;
        }
    }

    private void onClickGoogleLogin() {

        try {
            Constants.SOCIAL_TYPE  = GOOGLE;
            GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this,this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                    .build();

            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent,RC_SIGN_IN);

        }catch (Exception e){

            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
        }

    }


    private void handleSignInResult(Task<GoogleSignInAccount> result) {

        if (!result.isSuccessful()){
            disconnectGoogleAPIClient();
            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            return;
        }

        try {
            GoogleSignInAccount lastSignedInGoogleAccount = GoogleSignIn.getLastSignedInAccount(this);

            if (lastSignedInGoogleAccount != null) {
                //save to shared preference
                String email = lastSignedInGoogleAccount.getEmail();
                Uri profilePic = lastSignedInGoogleAccount.getPhotoUrl();
                String firstName = lastSignedInGoogleAccount.getGivenName();
                String lastName = lastSignedInGoogleAccount.getFamilyName();
                String socialId = lastSignedInGoogleAccount.getId();


                if (!TextUtils.isEmpty(email)) {
                    callForSocial(email, profilePic, firstName,socialId);
                }
            }

            doGoogleLogout(this);

        }catch (Exception e){
                showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            disconnectGoogleAPIClient();
        }

    }


    private void onClickSignIn() {

        if (validateFieldWithMessage()) {
            if (checkInternetConnectionWithMessage()) {
                showLoader(true);
                doLoginResponse();
            }
        }
    }

    private void doLoginResponse() {

            try {

                String email = binding.edtEmail.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();

                Map<String, String> loginRequest = new HashMap<>();

                loginRequest.put(PARAM_EMAIL, email);
                loginRequest.put(PARAM_PASSWORD, password);
                loginRequest.put(PARAM_DEVICE_TOKEN, firebaseToken);
                loginRequest.put(PARAM_DEVICE_TYPE, PARAM_DEVICE_TYPE_VALUE);

                Utility.PrintLog(TAG,"login details = " + loginRequest);


                Call<CommonResponse> call = apiService.doLoginCall(API_KEY,loginRequest);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG,"Login Failed Error code : " +response.code());

                        if (!response.isSuccessful()) {
                            Utility.PrintLog(TAG,"result false");
                            disconnectGoogleAPIClient();
                            //showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {

                            if(checkResponseStatusWithMessage(response.body(),true)){
                                Utility.PrintLog(TAG,"success message : " + language.getLanguage(response.body().getMessage()));
                                saveData(response.body());
                            }else {

                                Utility.PrintLog(TAG,"false message : " + language.getLanguage(response.body().getMessage()));
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                            }



                        }
                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG,t.getMessage());
                        hideLoader();
                    }
                });
            }catch (Exception e){
                Utility.PrintLog(TAG,e.toString());
                showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SOCIAL_TYPE.equals(GOOGLE) && requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }else if (SOCIAL_TYPE.equals(FACEBOOK)){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    public static boolean isFbLoggedIn() {
        //for facebook login
        AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
        return fbAccessToken != null && !fbAccessToken.isExpired();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
         disconnectGoogleAPIClient();
    }
    static void doGoogleLogout(Activity activity) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, task -> {
                    Utility.PrintLog(TAG, " logged out from google account");
                });
    }

    @Override
    protected void onPause() {
        disconnectGoogleAPIClient();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectGoogleAPIClient();
    }

    public void disconnectGoogleAPIClient(){
        if (googleApiClient!=null && googleApiClient.isConnected()){
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }else {
            Utility.PrintLog("LoginActivityonDestroy()","LoginActivity googleApiClient not cu onDestroy()");
        }
    }
}