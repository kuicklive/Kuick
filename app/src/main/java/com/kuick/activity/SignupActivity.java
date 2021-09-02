package com.kuick.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kuick.R;
import com.kuick.Remote.EndPoints;
import com.kuick.Response.CommonResponse;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivitySignupBinding;
import com.kuick.util.RandomString;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.countrypicker.CountryPicker;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.API_KEY;
import static com.kuick.Remote.EndPoints.PARAM_COUNTRY;
import static com.kuick.Remote.EndPoints.PARAM_DEVICE_TOKEN;
import static com.kuick.Remote.EndPoints.PARAM_DEVICE_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_DEVICE_TYPE_VALUE;
import static com.kuick.Remote.EndPoints.PARAM_EMAIL;
import static com.kuick.Remote.EndPoints.PARAM_FULL_NAME;
import static com.kuick.Remote.EndPoints.PARAM_PASSWORD;
import static com.kuick.Remote.EndPoints.PARAM_SOCIAL_ID;
import static com.kuick.Remote.EndPoints.PARAM_SOCIAL_TYPE;
import static com.kuick.activity.LoginActivity.doGoogleLogout;
import static com.kuick.activity.LoginActivity.isFbLoggedIn;
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
import static com.kuick.util.comman.Constants.NOT_LOGIN;
import static com.kuick.util.comman.Constants.SOCIAL_TYPE;
import static com.kuick.util.comman.Constants.WEBVIEW_SCREEN;
import static com.kuick.util.comman.Utility.MIN_PASSWORD_LENGTH;
import static com.kuick.util.comman.Utility.PrintLog;
import static com.kuick.util.comman.Utility.hideInputError;
import static com.kuick.util.comman.Utility.isPasswordEmpty;
import static com.kuick.util.comman.Utility.showTextInputLayoutError;

public class SignupActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySignupBinding binding;
    public String socialId = null;
    private final String TAG = "SignupActivity";
    private final String[] countryList = {"CHILE", "IND", "USA", "AST",};
    private String selectedCountry = null;
    private boolean isSocialLogin = false;
    private String firebaseToken;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentData();
        getToken();
        setLanguageLable();
        setLanguageSwitch();
        buttonListener();
        callCountryList(true);
        setCountryName();

        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hideInputError(binding.inputName);
            }
        });

        binding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hideInputError(binding.inputEmail);
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
                hideInputError(binding.inputPassword);
            }
        });

    }


    private void getToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Utility.PrintLog(TAG, "Fetching FCM registration token failed : "+ task.getException());
                        return;
                    }

                    firebaseToken = task.getResult();
                    Utility.PrintLog(TAG, "device Token : "+ firebaseToken);
                });

        if (firebaseToken == null)
        {
            String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
            RandomString tickets = new RandomString(23, new SecureRandom(), easy);

            Utility.PrintLog(TAG,"randomtoken = " + tickets.nextString());


            firebaseToken = tickets.nextString();
        }

    }

    private void setLanguageLable() {

        try {

            if (language != null) {

                binding.inputName.setHint(language.getLanguage(KEY.full_name));
                binding.inputEmail.setHint(language.getLanguage(KEY.your_email));
                binding.inputPassword.setHint(language.getLanguage(KEY.choose_a_password));
                binding.txtSelectedCountry.setHint(language.getLanguage(KEY.your_country__region));
                binding.byRegistrationWithKuick.setText(language.getLanguage(KEY.registering_with_kuick_live));
                binding.btnTermsAndCondition.setText(language.getLanguage(KEY.terms_and_conditions_of_service));
                binding.signUpWith.setText(language.getLanguage(KEY.or_sign_up_with));
                binding.txtHaveAnAccount.setText(language.getLanguage(KEY.have_an_account));
                binding.txtBtnSignIn.setText(language.getLanguage(KEY.sign_in_app));
                binding.txtBtnSignIn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                binding.txtButtonSignUp.setText(language.getLanguage(KEY.sign_up_app_small));

                String create_account = language.getLanguage(KEY.create_your_account_today);
                String[] subString = create_account.split(" ");
                String create_your = subString[0] + " " + subString[1];
                String account_today = subString[2] + " " + subString[3];
                binding.createAccount.setText(create_your + "\n" +account_today);


            }
        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }

    }


    private void getIntentData()
    {

        if (getIntent() != null && getIntent().getStringExtra(INTENT_KEY_USERNAME) != null && getIntent().getStringExtra(INTENT_KEY_USER_EMAIL) != null) {
            binding.edtName.setText(getIntent().getStringExtra(INTENT_KEY_USERNAME));

            String email = getIntent().getStringExtra(INTENT_KEY_USER_EMAIL);
            binding.edtEmail.setText(email);
            socialId = getIntent().getStringExtra(INTENT_KEY_SOCIAL_ID);
            isSocialLogin = true;

           hide(email);
        }else {
            show();
        }
    }
    public void showEmailNotExistDialog(String email, String name, String socialid)
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

            binding.edtName.setText(name);
            binding.edtEmail.setText(email);
            socialId = socialid;
            isSocialLogin = true;
            hide(email);

        });
    }


    private void buttonListener() {
        PushDownAnim.setPushDownAnimTo(binding.btnTermsAndCondition, binding.txtBtnSignIn, binding.btnFb, binding.btnGoogle, binding.btnSignUp);

        binding.btnTermsAndCondition.setOnClickListener(this);
        binding.txtBtnSignIn.setOnClickListener(this);
        binding.btnSignUp.setOnClickListener(this);
        binding.btnFb.setOnClickListener(this);
        binding.btnGoogle.setOnClickListener(this);
        binding.inputCountry.setOnClickListener(this);
        binding.btnSwitch.setOnClickListener(this);
    }


    private boolean validateFieldWithMessage() {

        boolean isValid = false;
        String name = binding.edtName.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            showTextInputLayoutError(language.getLanguage(KEY.please_enter_name), binding.inputName);
            return isValid;
        }
        hideInputError(binding.inputName);
        if (TextUtils.isEmpty(email)) {
            showTextInputLayoutError(language.getLanguage(KEY.please_enter_email), binding.inputEmail);
            return isValid;
        }
        if (!Utility.validEmail(email)) {
            showTextInputLayoutError(language.getLanguage(KEY.please_enter_proper_email), binding.inputEmail);
            return isValid;
        }
        hideInputError(binding.inputEmail);

        if (socialId == null) {
            if (isPasswordEmpty(password)) {
                showTextInputLayoutError(language.getLanguage(KEY.please_enter_password), binding.inputPassword);
                return isValid;
            }
            if (password.length() < MIN_PASSWORD_LENGTH) {
                showTextInputLayoutError(language.getLanguage(KEY.please_enter_minimum_8_character_password), binding.inputPassword);
                return isValid;
            }
            hideInputError(binding.inputPassword);
        }

        if (selectedCountry == null){
            showSnackErrorMessage(language.getLanguage(KEY.country_must_required));
            return isValid;
        }

        isValid = true;
        return isValid;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTermsAndCondition:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WEBVIEW_SCREEN, 9);
                intent.putExtra(NOT_LOGIN, NOT_LOGIN);
                startActivity(intent);
                break;
            case R.id.txtBtnSignIn:
                goToNextScreen(this, LoginActivity.class, true);
                break;
            case R.id.btnSignUp:
                onClickRegister();
                break;
            case R.id.btnFb:
                onClickFacebookLogin();
                break;
            case R.id.btnGoogle:
                onClickGoogleLogin();
                break;
            case R.id.inputCountry:
                    openCountryPicker();
                break;
            case R.id.btnSwitch:

                if (binding.btnSwitch.isSelected()){
                    callLanguageAPIChange(EN, false, binding.btnSwitch);
                } else{
                    callLanguageAPIChange(ES, false, binding.btnSwitch);
                }

                break;
        }
    }

    public void callLanguageAPIChange(String languageCode, boolean isCallInit, LinearLayout languageSwitch) {
        languageSwitch(languageSwitch, false);
        try {

            Call<String> call = apiService.doGetLanguage(EndPoints.API_KEY, languageCode);

            if (checkInternetConnectionWithMessage()) {
                if (!isFromSpashScreen) {
                    showLoader(true);
                }

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {

                        Utility.PrintLog(TAG, "response code from language API : " + response.code());

                        if (!response.isSuccessful()) {
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            return;
                        }

                        if (response.body() != null) {
                            Utility.PrintLog(TAG, "response json : " + response);

                            try {

                                JSONObject jsonObject = new JSONObject(response.body());
                                Utility.PrintLog(TAG, "json response :" + jsonObject.toString());
                                language.setLanguageCode(jsonObject.getString("language_code"));

                                if (jsonObject.has("labels")) {
                                    Object languageObject = jsonObject.get("labels");
                                    Utility.PrintLog(TAG, "language :" + jsonObject.toString());
                                    language.setLanguage(languageObject.toString());
                                    if (isCallInit) {


                                    } else hideLoader();

                                    languageSwitch(languageSwitch, true);

                                    if (!isFromSpashScreen) {
                                        //onClickRefreshActivity();
                                        recreate();
                                        setLanguageLable();

                                    }

                                    isFromSpashScreen = false;

                                    if (Constants.isFromProfileScreen) {
                                        Constants.isFromProfileScreen = false;
                                        callLanguagUpdateApi();
                                    }

                                }

                            } catch (JSONException e) {
                                languageSwitch(languageSwitch, true);
                                e.printStackTrace();
                                Utility.PrintLog(TAG, "exception : " + e.toString());
                            }
                        }

                        hideLoader();

                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        languageSwitch(languageSwitch, true);
                        Utility.PrintLog(TAG, "onFailure language() : " + t.toString());
                        hideLoader();
                    }
                });
            } else languageSwitch(languageSwitch, true);

        } catch (Exception e) {
            languageSwitch(languageSwitch, true);
            Utility.PrintLog(TAG, "exception language() : " + e.toString());
            hideLoader();
        }
    }

    private void setLanguageSwitch() {
        if (language!=null && language.getLanguageCode()!=null && !language.getLanguageCode().equals("")){
            if (language.getLanguageCode().equals(EN)){
                switchLanguageSetup(binding, false);
            }else {
                switchLanguageSetup(binding, true);
            }
        }
    }

    private void onClickRegister() {

        if (validateFieldWithMessage()) {
            if (checkInternetConnectionWithMessage()) {
                showLoader(true);
                binding.btnSignUp.setEnabled(false);
                doRegisterResponse();

            }
        }
    }

    private void doRegisterResponse() {



        Call<CommonResponse> call = null;

        String fullName = binding.edtName.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        Map<String, String> registerRequest = new HashMap<>();

        registerRequest.put(PARAM_FULL_NAME, fullName);
        registerRequest.put(PARAM_EMAIL, email);
        registerRequest.put(PARAM_PASSWORD, password);
        registerRequest.put(PARAM_COUNTRY, selectedCountry);
        registerRequest.put(PARAM_DEVICE_TYPE, PARAM_DEVICE_TYPE_VALUE); // 0 for Android
        registerRequest.put(PARAM_DEVICE_TOKEN, firebaseToken);

        Utility.PrintLog(TAG,"registerRequest = " + registerRequest);

        if (!isSocialLogin) {
            call = apiService.doRegister(API_KEY, registerRequest);
        } else {
            registerRequest.put(PARAM_SOCIAL_TYPE, Constants.SOCIAL_TYPE);
            registerRequest.put(PARAM_SOCIAL_ID, socialId);
            call = apiService.socialLoginWithCountry(API_KEY, registerRequest);
        }


        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                Utility.PrintLog(TAG, response.toString());

                checkErrorCode(response.code());

                if (response.body()!=null){

                    if (checkResponseStatusWithMessage(response.body(), true)) {
                        userPreferences.saveCurrentUser(response.body());
                        // showSnackResponse(getString(R.string.sign_up_success));
                        binding.btnSignUp.setEnabled(true);
                        goToNextScreen(SignupActivity.this, HomeActivity.class, true);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
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
            }

            @Override
            public void onError(FacebookException error) {
                Utility.PrintLog(TAG,"error : ");
            }
        });

    }
    private void callForSocial(String email, Uri profilePicUri, String name, String socialid) {

        try {

            if (checkInternetConnectionWithMessage()) {
                showLoader(true);

                Map<String, String> socialLoginDetail = new HashMap<>();

                socialLoginDetail.put(PARAM_SOCIAL_TYPE,Constants.SOCIAL_TYPE);
                socialLoginDetail.put(PARAM_SOCIAL_ID,socialid);
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

                        if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful()) {
                            PrintLog(TAG, "user already exist : " + response);
                            showSnackResponse(language.getLanguage(response.body().getMessage()));
                            saveData(response.body());
                        }else {

                            if (response.body()!=null && response.body().getType()!=null && response.body().getType().equals(Constants.countryScreen))
                            {
                                PrintLog(TAG, "user not exist : " + response);
                                showEmailNotExistDialog(email,name,socialid);

                            }else {
                                disconnectGoogleAPIClient();
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                                PrintLog(TAG, "when user deactivated : " + language.getLanguage(response.body().getMessage()));

                                show();
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

    private void hide(String email) {
        binding.inputName.setEnabled(false);
        if (email!=null && !email.equals(""))
        {
            binding.inputEmail.setEnabled(false);
        }
        binding.inputPassword.setVisibility(View.GONE);
    }

    public void show(){
        binding.inputName.setEnabled(true);
        binding.inputEmail.setEnabled(true);
        binding.inputPassword.setVisibility(View.VISIBLE);
    }

    private void onClickGoogleLogin() {
        Constants.SOCIAL_TYPE  = GOOGLE;
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

     /*   if (googleApiClient!=null && googleApiClient.isConnected()){
            disconnectGoogleAPIClient();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();*/

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

       /* Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,RC_SIGN_IN);*/
    }

    private void setCountryName() {
        if (picker == null) {
            picker = CountryPicker.newInstance("Select Country", allCountriesList);
            picker.setListener((name) -> {
                binding.txtSelectedCountry.setText(name);
                selectedCountry = name;
                picker.dismiss();
            });
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

    private void handleSignInResult(Task<GoogleSignInAccount> result) {

        if (!result.isSuccessful()){
            Utility.PrintLog(TAG,"result false" );
            disconnectGoogleAPIClient();
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
            Utility.PrintLog(TAG,"onRestart() " );
            disconnectGoogleAPIClient();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Utility.PrintLog(TAG,"onRestart() " );
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectGoogleAPIClient();

    }
    public void disconnectGoogleAPIClient(){
        if (mGoogleSignInClient!=null){
            mGoogleSignInClient.signOut();
        }

        /*if (googleApiClient!=null && googleApiClient.isConnected()){
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectGoogleAPIClient();
    }

}