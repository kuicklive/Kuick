package com.kuick.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityMyProfileBinding;
import com.kuick.model.UserDetail;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.countrypicker.CountryPicker;
import com.kuick.util.utils.RealPathUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static com.kuick.Remote.EndPoints.PARAM_COUNTRY;
import static com.kuick.Remote.EndPoints.PARAM_DATE_OF_BIRTH;
import static com.kuick.Remote.EndPoints.PARAM_EMAIL;
import static com.kuick.Remote.EndPoints.PARAM_FULL_NAME;
import static com.kuick.Remote.EndPoints.PARAM_GENDER;
import static com.kuick.Remote.EndPoints.PARAM_MOBILE_NUMBER;
import static com.kuick.Remote.EndPoints.PARAM_PROFILE_IMAGE;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.activity.HomeActivity.imageRefreshListener;
import static com.kuick.util.comman.Constants.EN;
import static com.kuick.util.comman.Constants.ES;
import static com.kuick.util.comman.Constants.Female;
import static com.kuick.util.comman.Constants.Male;
import static com.kuick.util.comman.Constants.NULL;
import static com.kuick.util.comman.Utility.PrintLog;


public class MyProfile extends BaseActivity {

    public static Bitmap bitmapSumsung;
    public static Uri imageUri;
    final Calendar myCalendar = Calendar.getInstance();
    private final String TAG = "MyProfile";
    String chooseCamera = "camera", chooseGallery = "gallery", userChoosenTask = "";
    byte[] Profile_ByteImage = null;
    private ActivityMyProfileBinding binding;
    private TextView txtTitle;
    private ImageView btnBack;
    private String isGender = Male;
    private Bitmap bitmapImage;
    private String selectedCountry = "Chile";
    private String imageFilePath;


    public static RequestBody getRequestBody(String content) {
        return RequestBody.create(content, MediaType.parse("multipart/form-data"));
    }

    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(context, fileUri);
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            realPath = RealPathUtil.getRealPathFromURI_API11to18(context, fileUri);
        }
        // SDK > 19 (Android 4.4) and up
        else {
            realPath = RealPathUtil.getRealPathFromURI_API19(context, fileUri);
        }
        return realPath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        setLanguageLable();
        setUserData();
        setLanguageSwitch();
        setGender();
        callCountryList(true);
        setCountryName();

    }

    public ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add("DAY TEST:" + i);
        }
        return list;
    }


    private MultipartBody.Part getMultipartImage(Uri imagePath) {

        File documentImage = new File(getRealPath(this, imagePath));

        if ((!TextUtils.isEmpty(userPreferences.getUserId())) && !(TextUtils.isEmpty(userPreferences.getApiKey()))) {
            if (checkInternetConnectionWithMessage()) {

                RequestBody imageBody = RequestBody.create(Profile_ByteImage, MediaType.parse("image/*"));
                return MultipartBody.Part.createFormData(PARAM_PROFILE_IMAGE, documentImage.getName(), imageBody);
            }
        }
        return null;
    }

    private void setCountryName() {
        if (picker == null) {

            try {

                picker = CountryPicker.newInstance("Select Country", allCountriesList);
                picker.setListener((name) -> {
                    binding.countyName.setText(name);
                    selectedCountry = name;
                    picker.dismiss();

                });

            } catch (Exception e) {
                Utility.PrintLog("Exception", "exception()");
            }
        }
    }

    private void setLanguageLable() {

        try {
            if (language != null) {

                txtTitle.setText(language.getLanguage(KEY.my_profile));
                binding.txtFullName.setText(language.getLanguage(KEY.full_name));
                binding.txtEmail.setText(language.getLanguage(KEY.email));
                binding.txtMobileNumber.setText(language.getLanguage(KEY.mobile_number));
                binding.txtDateOfBirth.setText(language.getLanguage(KEY.date_of_birth));
                binding.country.setText(language.getLanguage(KEY.your_country__region));
                binding.txtGender.setText(language.getLanguage(KEY.gender));
                binding.cbMale.setText(language.getLanguage(KEY.male));
                binding.cbFemale.setText(language.getLanguage(KEY.female));
                binding.btnSaveChanges.setText(language.getLanguage(KEY.save_changes));


            }
        } catch (Exception e) {
            Utility.PrintLog(TAG, "exception : " + language);
        }

    }

    private void setGender() {

        binding.rgButtons.setOnCheckedChangeListener((group, checkedId) -> {
            if (binding.cbMale.isChecked()) {
                isGender = Male;
            } else {
                isGender = Female;
            }
        });
    }

    private void setUserData() {

        if (userPreferences != null) {

            if (userPreferences.getProfileImage() != null && !userPreferences.getProfileImage().trim().equalsIgnoreCase("")) {
                BaseActivity.showGlideImage(this, userPreferences.getProfileImage(), binding.userImage);
            }

            if (userPreferences.getFullName() != null && !userPreferences.getFullName().equals(NULL)) {
                binding.txtUserName.setText(userPreferences.getFullName());
            }

            if (userPreferences.getEmail() != null && !userPreferences.getEmail().equals(NULL)) {
                binding.txtUserEmail.setText(userPreferences.getEmail());
            }

            if (userPreferences.getMobileNumber() != null && !userPreferences.getMobileNumber().equals(NULL)) {
                binding.txtUserMobileNumber.setText(userPreferences.getMobileNumber());
            }

            if (userPreferences.getDateOfBirth() != null && !userPreferences.getDateOfBirth().equals(NULL)) {
                binding.txtBirthDate.setText(userPreferences.getDateOfBirth());
            }

            if (userPreferences.getCountry() != null && !userPreferences.getCountry().equals(NULL)) {
                binding.countyName.setVisibility(View.VISIBLE);
                binding.countyName.setText(userPreferences.getCountry());
            }

            if (userPreferences.getGender() != null && !userPreferences.getGender().equals(NULL)) {
                if (userPreferences.getGender().equals(Male)) {
                    isGender = isGender(false, true);
                } else if (userPreferences.getGender().equals(Female)) {
                    isGender = isGender(true, false);
                }
            }
        }
    }

    private void setLanguageSwitch() {
        if (language != null && language.getLanguageCode() != null && !language.getLanguageCode().equals("")) {
            switchSetup(binding, !language.getLanguageCode().equals(EN));
        }
    }

    private void setToolBar() {
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);

        binding.layoutBirth.setOnClickListener(this);
        binding.selectImage.setOnClickListener(this);
        binding.btnSaveChanges.setOnClickListener(this);
        binding.inputCountry.setOnClickListener(this);
        binding.btnSwitch.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.inputCountry:
                if (LiveActivity.liveActivity != null && LiveActivity.isInPictureInPictureMode) {
                    showSnackErrorMessage(language.getLanguage(KEY.the_country_will_not_change_during_the_pip));
                    return;
                }

                if (allCountriesList != null && allCountriesList.size() > 0){
                    openCountryPicker();
                }

                break;
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.selectImage:
                ShowPictureDialog();
                break;
            case R.id.layoutBirth:
                onClickBirthDate();
                break;
            case R.id.btnSaveChanges:
                if (checkInternetConnectionWithMessage()) {
                    //onUpdateProfileData();
                    onUpdateProfileRequestBodyData();
                }
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
            case R.id.btnSwitch:
                Constants.isFromProfileScreen = true;
                Constants.idFromProfile = true;

                if (binding.btnSwitch.isSelected()) {
                    callLanguageAPI(EN, false, binding.btnSwitch);
                } else {
                    callLanguageAPI(ES, false, binding.btnSwitch);
                }

                break;
        }
    }

    public void switchSetup(ActivityMyProfileBinding binding, boolean isTrue) {

        if (!isTrue) {
            binding.btnEN.setBackgroundResource(R.drawable.switch_selected);
            binding.btnEN.setTextColor(Color.WHITE);
            binding.btnSPA.setTextColor(getResources().getColor(R.color.unselected_color));
            binding.btnSwitch.setSelected(isTrue);
            binding.btnSPA.setBackground(null);
        } else {
            binding.btnSPA.setBackgroundResource(R.drawable.switch_selected);
            binding.btnSPA.setTextColor(Color.WHITE);
            binding.btnEN.setTextColor(getResources().getColor(R.color.unselected_color));
            binding.btnSwitch.setSelected(isTrue);
            binding.btnEN.setBackground(null);
        }
    }

    private void onUpdateProfileData() {

        try {
            String email = binding.txtUserEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                //showSnackErrorMessage(language.getLanguage(KEY.email_must_be_required));
                return;
            }
            if (!Utility.validEmail(email)) {
                //showSnackErrorMessage(getString(R.string.error_enter_valid_email));
                return;
            }

            showLoader(true);

            Map<String, Object> updateRequest = new HashMap<>();


            updateRequest.put(PARAM_USER_ID, userPreferences.getUserId());
            updateRequest.put(PARAM_FULL_NAME, binding.txtUserName.getText().toString().trim());
            updateRequest.put(PARAM_EMAIL, email);
            updateRequest.put(PARAM_DATE_OF_BIRTH, binding.txtBirthDate.getText().toString().trim());
            updateRequest.put(PARAM_GENDER, isGender);
            updateRequest.put(PARAM_MOBILE_NUMBER, binding.txtUserMobileNumber.getText().toString().trim());
            updateRequest.put(PARAM_COUNTRY, selectedCountry);


            if (imageUri != null) {
                File fil = new File(getRealPath(this, imageUri));
                updateRequest.put(PARAM_PROFILE_IMAGE, fil);
            }

            PrintLog(TAG, userPreferences.getApiKey());

            Call<CommonResponse> call = apiService.doUpdateProfileCall(userPreferences.getApiKey(), updateRequest);

            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                    Utility.PrintLog(TAG, response.toString());

                    checkErrorCode(response.code());

                    if (response.body() != null) {

                        Utility.PrintLog(TAG, "response details : " + response.body());


                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            Object object = jsonObject.get("user_details");
                            Utility.PrintLog(TAG, "user details : " + object);
                        } catch (JSONException e) {

                        }

                        //userPreferences.saveCurrentUser(response.body());
                        if (checkResponseStatusWithMessage(response.body(), true)) {
                            userPreferences.saveCurrentUser(response.body());
                            showSnackResponse(language.getLanguage(response.body().getMessage()));
                        } else
                            showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
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
            Utility.PrintLog(TAG, e.toString());
            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
            hideLoader();
        }
    }


    private void onUpdateProfileRequestBodyData() {

        try {
            String email = binding.txtUserEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                showSnackErrorMessage(language.getLanguage(KEY.email_must_be_required));
                return;
            }
            if (!Utility.validEmail(email)) {
                showSnackErrorMessage(language.getLanguage(KEY.please_enter_proper_email));
                return;
            }

            showLoader(true);

            RequestBody apikey = getRequestBody(userPreferences.getApiKey());
            RequestBody userId = getRequestBody(userPreferences.getUserId());
            RequestBody myEmail = getRequestBody(email);
            RequestBody fullName = getRequestBody(binding.txtUserName.getText().toString().trim());
            RequestBody dateOfBirth = getRequestBody(binding.txtBirthDate.getText().toString().trim());
            RequestBody gender = getRequestBody(isGender);
            RequestBody mobileNumber = getRequestBody(binding.txtUserMobileNumber.getText().toString().trim());
            RequestBody country = getRequestBody(selectedCountry);

            MultipartBody.Part imagePath = null;
            if (imageUri != null) {
                imagePath = getMultipartImage(imageUri);
                Utility.PrintLog(TAG, "Image Uri Path : " + imageUri.toString());
            }


            Call<CommonResponse> call = apiService.updateProfile(
                    userId,
                    fullName,
                    myEmail,
                    dateOfBirth,
                    gender,
                    mobileNumber,
                    imagePath,
                    country);


            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                    Utility.PrintLog(TAG, response.toString());

                    checkErrorCode(response.code());

                    if (response.body() != null) {
                        userPreferences.saveCurrentUser(response.body());
                        UserDetail userDetail = response.body().getUser();
                        Utility.PrintLog(TAG, "userDetail.getCurrency_code() : " + userDetail.getCurrency_code());
                        if (response.body().getUser().getProfile_image() != null) {
                            imageRefreshListener.getImageUrl(response.body().getUser().getProfile_image());
                        }
                        showSnackResponse(language.getLanguage(response.body().getMessage()));
                    }

                    hideLoader();
                }

                @Override
                public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                    // showSnackErrorMessage(getString(R.string.failed_to_update));
                    Utility.PrintLog(TAG, t.toString());
                    hideLoader();
                }
            });

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            hideLoader();
            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
        }
    }


    private void onClickBirthDate() {

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            binding.txtBirthDate.setText(Utility.getDateFormat(myCalendar.getTime()));
        };

        DatePickerDialog dpd = new DatePickerDialog(MyProfile.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        DatePicker dp = dpd.getDatePicker();
        dp.setMaxDate(System.currentTimeMillis());
        dpd.show();

    }

    public String isGender(boolean isFemale, boolean isMale) {
        binding.cbFemale.setChecked(isFemale);
        binding.cbMale.setChecked(isMale);

        if (isMale)
            isGender = Male;
        else
            isGender = Female;
        return isGender;
    }

    private void ShowPictureDialog() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE_CAMERA);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_CODE_STORAGE);
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_photo_choose);

            TextView tv_camera = dialog.findViewById(R.id.dialog_tv_camera);
            TextView tv_gallery = dialog.findViewById(R.id.dialog_tv_gallery);

            try {

                if (language != null) {
                    tv_camera.setText(language.getLanguage(KEY.select_from_camera));
                    tv_gallery.setText(language.getLanguage(KEY.select_from_gallery));
                }

            } catch (Exception e) {

            }


            tv_gallery.setOnClickListener(v -> {
                dialog.dismiss();
                new Handler().postDelayed(this::choosePhotoFromGallary, 800);
            });

            tv_camera.setOnClickListener(v -> {
                dialog.dismiss();
                new Handler().postDelayed(this::takePhotoFromCamera, 800);

            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setAttributes(lp);
            dialog.show();
        }
    }


    private void takePhotoFromCamera() {
        bitmapSumsung = null;
        if (getDeviceName() != null && getDeviceName().toLowerCase().contains("samsung")) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        } else {
            userChoosenTask = chooseCamera;
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "NewPicture");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(intent, CAMERA);
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


    private void choosePhotoFromGallary() {


        if (checkPermission(this, PERMISSIONS)) {

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            permissionGranted = false;
        }

        userChoosenTask = chooseGallery;
        Intent intent_gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent_gallery, GALLERY);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        return manufacturer;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utility.PrintLog(TAG, "checkSelfPermission CAMERA permission granted");
                    ShowPictureDialog();
                } else {
                    PermissionDeny(Constants.PERMISSION_DENY, language.getLanguage(KEY.permission_denied_you_cannot_open_camera));
                }
                break;

            case MY_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utility.PrintLog(TAG, "checkSelfPermission GALLERY permission granted");
                    ShowPictureDialog();
                } else {
                    PermissionDeny(Constants.PERMISSION_DENY, language.getLanguage(KEY.permission_denied_you_cannot_read_the_storage));
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (resultCode == RESULT_OK) {
                if (requestCode == GALLERY) {
                    if (data != null) {
                        onSelectFromGalleryResult(data);
                    }
                } else if (requestCode == CAMERA) {
                    onCaptureImageResult(data);
                } else {
                    Profile_ByteImage = null;
                }
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "excreption() onActivityResult() " + e.toString());
        }
    }

    private void onCaptureImageResult(Intent data) {
        if (getDeviceName() != null && getDeviceName().toLowerCase().contains("samsung")) {
            bitmapSumsung = (Bitmap) data.getExtras().get("data");
            binding.userImage.setImageBitmap(bitmapSumsung);
            Profile_ByteImage = ConvertToByteArray(bitmapSumsung);
            imageUri = getImageUri(this, bitmapSumsung);
            BaseActivity.showGlideImage(this, imageUri.toString(), binding.userImage);
        } else {
            if (imageUri != null) {
                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    thumbnail = getResizedBitmap(thumbnail, 400);
                    bitmapImage = thumbnail;

                    if (thumbnail != null) {
                        Profile_ByteImage = ConvertToByteArray(bitmapImage);
                        Uri uri = imageUri;
                        BaseActivity.showGlideImage(this, uri.toString(), binding.userImage);
                    } else {
                        binding.userImage.setImageResource(R.drawable.dummy_user_icon);
                        //showSnackErrorMessage(getString(R.string.please_select_photo_again));
                    }
                } catch (Exception e) {
                    binding.userImage.setImageResource(R.drawable.dummy_user_icon);
                    userPreferences.setProfileImage("");
                    Utility.PrintLog(TAG, "onCaptureImageResult() : " + e.toString());
                }
            }
        }
    }


    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        bitmapImage = getResizedBitmap(bitmap, 400);

        if (bitmapImage != null) {
            Profile_ByteImage = ConvertToByteArray(bitmapImage);
            Uri uri = data.getData();
            imageUri = data.getData();
            BaseActivity.showGlideImage(this, uri.toString(), binding.userImage);
        } else {
            Profile_ByteImage = null;
            //showSnackErrorMessage(getString(R.string.please_select_photo_again));
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public byte[] ConvertToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, getResources().getString(R.string.app_name), null);
        return Uri.parse(path);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bitmapSumsung != null) {
            if (getDeviceName() != null && getDeviceName().toLowerCase().contains("samsung")) {
                Uri imageUri1 = getImageUri(this, bitmapSumsung);

                BaseActivity.showGlideImage(this, imageUri1.toString(), binding.userImage);
                Profile_ByteImage = ConvertToByteArray(bitmapSumsung);
            }
        }

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}