package com.kuick.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.kuick.R;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ActivityWebViewBinding;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import static com.kuick.Remote.EndPoints.WEBVIEW_BASE_URL;
import static com.kuick.util.comman.Constants.NOT_LOGIN;
import static com.kuick.util.comman.Constants.WEBVIEW_SCREEN;

public class WebViewActivity extends BaseActivity {

    private ActivityWebViewBinding binding;
    private TextView txtTitle;
    private ImageView btnBack;
    String TAG = "WebViewActivity";

   public static String About_url = WEBVIEW_BASE_URL + "about-us/true";
   public static String Delivery_url = WEBVIEW_BASE_URL + "delivery/true";
   public static String Return_url = WEBVIEW_BASE_URL + "returns/true";
   public static String Privacy_Policy_url = WEBVIEW_BASE_URL + "privacy-policy/true";
   public static String Terms_of_service_url = WEBVIEW_BASE_URL + "terms-of-service/true";
    private ImageView btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int screen = getIntent().getIntExtra(WEBVIEW_SCREEN, 0);
        btnCart = binding.getRoot().findViewById(R.id.img_cart);

        if (getIntent().getStringExtra(NOT_LOGIN)!=null && !getIntent().getStringExtra(NOT_LOGIN).equals(""))
        {
            btnCart.setVisibility(View.GONE);
        }else {
            btnCart.setVisibility(View.VISIBLE);
        }
        setToolBar(screen);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setToolBar(int screen) {

        btnBack = binding.getRoot().findViewById(R.id.img_back);
        btnCart.setOnClickListener(this);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

        /*web view*/
        if (checkInternetConnectionWithMessage())
            loadWebView();
        else
            binding.progressBar1.setVisibility(View.GONE);

        if (userPreferences.getSocialId()!=null && !userPreferences.getSocialId().equals("") && !userPreferences.getSocialId().equals("null")){

            switch (screen){

                case 3:
                    setTitle(KEY.about_us);
                    binding.commonWebView.loadUrl(About_url);
                    break;
                case 5:
                    setTitle(KEY.delivery);
                    binding.commonWebView.loadUrl(Delivery_url);
                    break;
                case 6:
                    setTitle(KEY.returns);
                    binding.commonWebView.loadUrl(Return_url);
                    break;
                case 7:
                    setTitle(KEY.privacy_policy);
                    binding.commonWebView.loadUrl(Privacy_Policy_url);
                    break;
                case 8:
                    setTitle(KEY.terms_of_service);
                    binding.commonWebView.loadUrl(Terms_of_service_url);
                    break;
                case R.id.img_cart:
                    goToNextScreen(this, CartPageActivity.class,false);
                    break;
            }

        }else {

        switch (screen){

            case 4:
                setTitle(KEY.about_us);
                binding.commonWebView.loadUrl(About_url);
                break;
            case 6:
                setTitle(KEY.delivery);
                binding.commonWebView.loadUrl(Delivery_url);
                break;
            case 7:
                setTitle(KEY.returns);
                binding.commonWebView.loadUrl(Return_url);
                break;
            case 8:
                setTitle(KEY.privacy_policy);
                binding.commonWebView.loadUrl(Privacy_Policy_url);
                break;
            case 9:
                setTitle(KEY.terms_of_service);
                binding.commonWebView.loadUrl(Terms_of_service_url);
                break;
            case R.id.img_cart:
                goToNextScreen(this, CartPageActivity.class,false);
                break;
        }
        }

        btnCart.setOnClickListener(v -> goToNextScreen(WebViewActivity.this, CartPageActivity.class,false));

    }


    private void setTitle(String key) {

        try {

            if (language!=null){
                txtTitle.setText(language.getLanguage(key));
            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }

    }

    private void loadWebView() {
        binding.commonWebView.getSettings().setLoadsImagesAutomatically(true);
        binding.commonWebView.setWebViewClient(new myWebClient());
        binding.commonWebView.getSettings().setJavaScriptEnabled(true);
        binding.commonWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWebView();

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }
    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //show loader
            binding.progressBar1.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //hide loader
            binding.progressBar1.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            binding.progressBar1.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            binding.progressBar1.setVisibility(View.GONE);
        }

    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && binding.commonWebView.canGoBack()) {
            binding.commonWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}