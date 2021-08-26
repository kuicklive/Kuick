package com.kuick.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.kuick.R;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.AddPaymentCardActivityBinding;
import com.kuick.model.UserCard;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.ENDPOINT_USER_ID;
import static com.kuick.Remote.EndPoints.PARAM_CARD_EXPIRY_MONTH;
import static com.kuick.Remote.EndPoints.PARAM_CARD_EXPIRY_YEAR;
import static com.kuick.Remote.EndPoints.PARAM_CARD_HOLDER_NAME;
import static com.kuick.Remote.EndPoints.PARAM_CARD_NUMBER;
import static com.kuick.Remote.EndPoints.PARAM_CVV;
import static com.kuick.Remote.EndPoints.PARAM_DOC_NUMBER;
import static com.kuick.Remote.EndPoints.PARAM_TOKEN;
import static com.kuick.Remote.EndPoints.PARAM_ZIP_CODE;
import static com.kuick.util.comman.Constants.selectedPaymentCard;
import static com.kuick.util.comman.CurrencyCode.ARS;
import static com.kuick.util.comman.CurrencyCode.CLP;
import static com.kuick.util.comman.Utility.PrintLog;

public class AddPaymentCard extends BaseActivity {

    final Calendar myCalendar = Calendar.getInstance();
    String TAG = "AddPaymentCard";
    private AddPaymentCardActivityBinding binding;
    private TextView txtTitle;
    private ImageView btnBack, imgCart;
    private String stripeToken = "";
    private String zipCode = null;
    private String docNum = null;
    private String isDLocal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddPaymentCardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("isDLocal")) {
            isDLocal = getIntent().getStringExtra("isDLocal");
        }

        setToolBar();
        setLanguageLable();
        setTextChangeListener();


    }

    private void setLanguageLable() {

        try {
            if (language != null) {
                txtTitle.setText(language.getLanguage(KEY.payment_information));
                binding.txtZipCode.setText(language.getLanguage(KEY.zipcode_only_usa));
                binding.edtCardHoderName.setHint(language.getLanguage(KEY.first_and_last_name));
                binding.txtName.setText(language.getLanguage(KEY.name_app));
                binding.txtCardNumber.setText(language.getLanguage(KEY.card_number));
                binding.txtExpireDate.setText(language.getLanguage(KEY.expires));
                binding.txt.setText(language.getLanguage(KEY.cvv));
                binding.saveCard.setText(language.getLanguage(KEY.save_card));
                binding.powerdBy.setText(language.getLanguage(KEY.powered_by_stripe));
                binding.txtDate.setHint(language.getLanguage(KEY.mmyyyy));
                binding.txtDocNumRUT.setHint(language.getLanguage(KEY.document_number_rut));

                if (userPreferences != null && userPreferences.getCountry() != null) {
                    if (userPreferences.getCurrencyCode().equals(CLP)) {
                        binding.edtDocNumRUT.setHint(language.getLanguage(KEY.note_if_your_rut_has_letters_replace_them_with_a_1));

                    } else if (userPreferences.getCurrencyCode().equals(ARS)) {
                        binding.edtDocNumRUT.setHint(language.getLanguage(KEY.rut_ie_111111111));

                    }
                }

                //validation error message
                binding.mErrorName.setText(language.getLanguage(KEY.holder_name_cannot_be_empty));
                binding.mErrorCardNumber.setText(language.getLanguage(KEY.card_number_cannot_be_empty));
                binding.mErrorExpires.setText(language.getLanguage(KEY.expiry_cannot_be_empty));
                binding.mErrorCVV.setText(language.getLanguage(KEY.cvv_cannot_be_empty));
                binding.mErrorZipCode.setText(language.getLanguage(KEY.please_enter_valid_zip_code));
                binding.mErrorDocNumRUT.setText(language.getLanguage(KEY.please_enter_valid_document_number_rut));
            }

            if (!isDLocal.equals("")) {
                binding.powerdBy.setVisibility(View.GONE);
                binding.bottomCardImage.setVisibility(View.GONE);
                binding.layPostalCode.setVisibility(View.GONE);
                binding.layDocNumRUT.setVisibility(View.VISIBLE);
            } else {
                binding.powerdBy.setVisibility(View.VISIBLE);
                binding.bottomCardImage.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }

    }

    private void getTokenId(String cardNumber, String month, String year, String cvv, String name) {


        final Card card = new Card(
                cardNumber,
                Integer.parseInt(month),
                Integer.parseInt(year),
                cvv,
                null,
                null,
                null,
                null,
                null,
                zipCode,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        new Stripe().createToken(card, userPreferences.getStripePublishKey(), new TokenCallback() {
            @Override
            public void onError(Exception error) {
                PrintLog(TAG, "card_number_is_invalid" + error.toString());
                showSnackErrorMessage(language.getLanguage(KEY.card_number_is_invalid));
                hideLoader();
            }

            @Override
            public void onSuccess(Token token) {
                PrintLog(TAG, "token success: " + token.getId());
                stripeToken = token.getId();
                callAPI(cardNumber, month, year, cvv, name);
            }
        });

        Utility.PrintLog(TAG, "zip code - " + zipCode);
    }

    private void callAPI(String cardNumber, String month, String year, String cvv, String name) {
        try {

            if (checkInternetConnectionWithMessage()) {

                Map<String, String> addCard = new HashMap<>();

                addCard.put(ENDPOINT_USER_ID, userPreferences.getUserId());
                addCard.put(PARAM_CARD_HOLDER_NAME, name);
                addCard.put(PARAM_CARD_NUMBER, cardNumber);
                addCard.put(PARAM_CARD_EXPIRY_MONTH, month);
                addCard.put(PARAM_CARD_EXPIRY_YEAR, year);
                addCard.put(PARAM_CVV, cvv);
                if (isDLocal.equals("")) {
                    addCard.put(PARAM_TOKEN, stripeToken);
                    addCard.put(PARAM_DOC_NUMBER, "");
                } else {
                    addCard.put(PARAM_TOKEN, "");
                    if (docNum != null && !docNum.equals("")) {
                        addCard.put(PARAM_DOC_NUMBER, docNum);
                    }
                }
                if (zipCode != null && !zipCode.equals("") && isDLocal.equals("")) {
                    addCard.put(PARAM_ZIP_CODE, zipCode);
                } else {
                    addCard.put(PARAM_ZIP_CODE, "");
                }

                Call<UserCard> call = apiService.doAddPaymentCard(userPreferences.getApiKey(), addCard);
                call.enqueue(new Callback<UserCard>() {
                    @Override
                    public void onResponse(@NotNull Call<UserCard> call, @NotNull Response<UserCard> response) {
                        PrintLog(TAG, "response : " + response);

                        checkErrorCode(response.code());

                        if (!response.isSuccessful()) {
                            showSnackErrorMessage(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            hideLoader();
                            return;
                        }

                        if (response.body() != null) {
                            if (checkResponseStatusWithMessage(response.body(), true) && response.isSuccessful()) {
                                onBackPressed();
                            } else
                                showSnackErrorMessage(language.getLanguage(response.body().getMessage()));
                        }

                        hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<UserCard> call, @NotNull Throwable t) {
                        PrintLog(TAG, "onFailure : " + t.getMessage());
                        hideLoader();
                    }
                });
            }

        } catch (Exception e) {
            PrintLog(TAG, "Exception : " + e.toString());
            hideLoader();
        }
    }

    private void setTextChangeListener() {

        onTextChange(binding.edtCardHoderName);
        onTextChange(binding.edtCardNumber);
        onTextChange(binding.edtcvv);
        onTextChange(binding.edtZipCode);
        onTextChange(binding.edtDocNumRUT);

        binding.edtCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        binding.edtCardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());
    }

    private void onTextChange(TextInputEditText txtView) {

        txtView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                showError(txtView, binding.edtCardHoderName, binding.mErrorName);
                showError(txtView, binding.edtCardNumber, binding.mErrorCardNumber);
                showError(txtView, binding.edtcvv, binding.mErrorCVV);
                showError(txtView, binding.edtZipCode, binding.mErrorZipCode);
                showError(txtView, binding.edtDocNumRUT, binding.mErrorDocNumRUT);


            }

            private void showError(TextInputEditText txtView, TextInputEditText edtView, TextView mErrorView) {

                if (txtView.getId() == edtView.getId() && edtView.getText().toString().equals("")) {
                    if (txtView.getId() == binding.edtZipCode.getId()) {
                        mErrorView.setVisibility(View.GONE);
                        return;
                    }
                    mErrorView.setVisibility(View.VISIBLE);
                } else {
                    mErrorView.setVisibility(View.GONE);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    private void setToolBar() {

        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);
        btnBack = binding.getRoot().findViewById(R.id.img_back);
        imgCart = binding.getRoot().findViewById(R.id.img_cart);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        binding.txtDate.setOnClickListener(this);
        binding.saveCard.setOnClickListener(this);
        imgCart.setOnClickListener(this);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class, false);
                break;
            case R.id.txtDate:
                onClickBirthDate();
                break;
            case R.id.saveCard:
                SaveCardDetail();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void SaveCardDetail() {

        String name = binding.edtCardHoderName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            binding.mErrorName.setVisibility(View.VISIBLE);
            return;
        }

        String docNm = binding.edtDocNumRUT.getText().toString().trim();
        if (!isDLocal.equals("") && TextUtils.isEmpty(docNm)) {
            binding.mErrorDocNumRUT.setVisibility(View.VISIBLE);
            return;
        } else {
            docNum = docNm;
        }

        String cardNumber = binding.edtCardNumber.getText().toString().trim();
        if (TextUtils.isEmpty(cardNumber)) {
            binding.mErrorCardNumber.setVisibility(View.VISIBLE);
            return;
        }

        String zip = binding.edtZipCode.getText().toString();
        if (!TextUtils.isEmpty(zip) && zip.length() < 5) {
            binding.mErrorZipCode.setVisibility(View.VISIBLE);
            binding.mErrorCVV.setVisibility(View.GONE);
            binding.mErrorExpires.setVisibility(View.GONE);
            return;
        } else {
            zipCode = zip;
        }

        if (binding.txtDate.getText().toString().equals("")) {
            binding.mErrorExpires.setVisibility(View.VISIBLE);
            return;
        }
        String cvv = binding.edtcvv.getText().toString().trim();
        if (TextUtils.isEmpty(cvv)) {
            binding.mErrorCVV.setVisibility(View.VISIBLE);
            return;
        }


        showLoader(true);

        String[] expire = binding.txtDate.getText().toString().split("/");
        String month = expire[0];
        String year = expire[1];

        //get stripe token from here
        if (isDLocal.equals("")) {
            getTokenId(cardNumber, month, year, cvv, name);
        } else {
            callAPI(cardNumber, month, year, cvv, name);
        }

    }

    private void onClickBirthDate() {

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) ->
        {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            binding.txtDate.setText(Utility.getExpireDateFormat(myCalendar.getTime()));
            binding.mErrorExpires.setVisibility(View.GONE);
        };

        DatePickerDialog dpd = new DatePickerDialog(AddPaymentCard.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.getDatePicker()
                .findViewById(Resources.getSystem().getIdentifier("day", "id", "android"))
                .setVisibility(View.GONE);
        dpd.show();

    }

    public class FourDigitCardFormatWatcher implements TextWatcher {

        // Change this to what you want... ' ', '-' etc..
        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }
}