package com.kuick.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.kuick.R;
import com.kuick.Remote.ApiInterface;
import com.kuick.Remote.Networking;
import com.kuick.Response.AllStreamerData;
import com.kuick.Response.BaseResponse;
import com.kuick.Response.DashBoardResponse;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.SplashScreen.SecondSplashScreen;
import com.kuick.activity.HomeActivity;
import com.kuick.base.BaseActivity;
import com.kuick.base.BaseView;
import com.kuick.interfaces.ClickEventListener;
import com.kuick.model.FeatureStreamers;
import com.kuick.model.NewStreamers;
import com.kuick.model.TrendingStreamers;
import com.kuick.pref.Language;
import com.kuick.pref.UserPreferences;
import com.kuick.util.comman.CommanDialogs;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.loader.LoaderDialogFragment;
import com.kuick.util.loader.LoadingDialog;
import com.kuick.util.network.NetworkHelperImpl;

import java.util.List;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Constants.OK;
import static com.kuick.util.comman.Constants.SESSION_EXPIRE;
import static com.kuick.util.comman.Utility.goToNextScreen;

public class BaseFragment extends Fragment implements BaseView, View.OnClickListener, ClickEventListener {

    public BaseActivity baseActivity;
    public UserPreferences userPreferences;
    public ApiInterface apiService;
    private NetworkHelperImpl networkHelper;
    private LoaderDialogFragment loaderDialogFragment;
    private Fragment activeFragment;
    public Language language;
    private LoaderDialogFragment loader;
    private String TAG = "BaseFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseActivity = new BaseActivity();
        userPreferences = UserPreferences.newInstance(getContext());
        language = Language.newInstance(getContext());
        apiService = Networking.getClient().create(ApiInterface.class);
        networkHelper = new NetworkHelperImpl();
       loader =  new LoaderDialogFragment();
    }


    @Override
    public void onCancelCall()
    {

    }

    @Override
    public void showLoader(boolean isClose) {

        try {
            if (!loader.isAdded()) {
                loader.show(getChildFragmentManager(), LoaderDialogFragment.TAG);
            }
        }catch (Exception e){
            Utility.PrintLog(TAG,"BaseFragment showLoader() Exception() ");
        }
    }

    @Override
    public void hideLoader() {

        try {
            if (loader != null) {
                loader.dismiss();
            }
        } catch (Exception e) {
            Utility.PrintLog(TAG, "Loader Exception");
        }
    }


    @Override
    public void showMessageAndFinish(String message) {

    }

    public void showSnackResponse(String message) {
        try {
            if (!TextUtils.isEmpty(message)) {
                Utility.showInfoSnackBar(message, getActivity().findViewById(android.R.id.content), true);
            }
        }catch (Exception e){

        }
    }

    @Override
    public Activity getViewActivity() {
        return null;
    }


    public void showSnackErrorMessage(String message){
      try {

          final Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
          View snackBarView = snackbar.getView();
          TextView snackTextView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
          snackTextView.setTypeface(ResourcesCompat.getFont(getViewActivity(), R.font.quicksand_regular));
          snackTextView.setMaxLines(4);

          if (true) {
              snackBarView.setBackgroundColor(ContextCompat.getColor(getViewActivity(), R.color.colorErrorRed));
          } else {
              snackBarView.setBackgroundColor(ContextCompat.getColor(getViewActivity(), R.color.successGreen));
          }

          if (Looper.myLooper() == Looper.getMainLooper()) {
              snackbar.show();
          } else {
              // logger.d(TAG, getViewActivity() + "Non UI thread detected. skipping snackbar");
          }

      }catch (Exception e){

      }
    }

    public boolean checkInternetConnectionWithMessage(Context context) {
        if (networkHelper.isNetworkConnected(context)) {
            return true;
        } else {
            showErrorDialogInternet(R.string.oops,  language.getLanguage(KEY.internet_connection_lost_please_retry_again)/*R.string.network_connection_error*/);
            return false;
        }
    }

    protected void showErrorDialogInternet(int title, String message) {

        String button = "OK";

        if (language != null && language.getLanguage(KEY.ok) != null) {
            button = language.getLanguage(KEY.ok);
        }

        CommanDialogs.showOneButtonDialog(
                getActivity(),
                getString(title),
                message,
                button,
                (dialog, which) -> dialog.dismiss(),
                true
        );
    }

    protected void showErrorDialog(int title, int message) {

        String button = "OK";

        if (language!=null &&  language.getLanguage(KEY.ok)!=null)
        {
            button = language.getLanguage(KEY.ok);
        }



        CommanDialogs.showOneButtonDialog(
                getActivity(),
                getString(title),
                getString(message),
                button,
                (dialog, which) -> dialog.dismiss(),
                true
        );
    }

    @Override
    public void onClick(View v) {
    }
    protected <T extends BaseResponse> boolean checkResponseStatusWithMessage(T response, boolean showMessage) {

        boolean isResponseValid;

        if (response == null) {
            isResponseValid = false;
        } else {

            final String messageFromServer = response.getMessage();

            if (response.getStatus()) {
                isResponseValid = true;

                if (showMessage) {
                    showSnackResponse(messageFromServer);
                }

            } else {
                isResponseValid = false;
                if (showMessage) {
                    showSnackErrorMessage(messageFromServer);
                }
            }

        }

        return isResponseValid;
    }
    public  void checkErrorCode(int errorcode,Activity context){
        if (errorcode == Networking.ErrorCode){
            if(userPreferences!=null && userPreferences.getUserId()!=null){
                if (homeActivity!=null){
                    homeActivity.disconnectSocket();
                }

                Utility.PrintLog("contextcontext","contextcontext = " + context);
                if (context!=null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(SESSION_EXPIRE);
                    //.setTitle("Warning");
                    builder.setCancelable(false);
                    builder.setPositiveButton(OK, (dialog, id) -> {
                        userPreferences.removeCurrentUser();

                        goToNextScreen(context, SecondSplashScreen.class, true);
                        return;
                    });

                    AlertDialog alert =builder.create();
                    if (!alert.isShowing()){
                        alert.show();
                    }
                }
        }
    }
    }


    @Override
    public void onClickPosition(int position) {

    }

    @Override
    public void RefreshAllAdapter() {

    }


    @Override
    public void RefreshTrendingFullScreenAdapter(List<TrendingStreamers> trendingStreamersListFullScreen) {

    }

    @Override
    public void RefreshFeaturedFullScreenAdapter(List<FeatureStreamers> featureStreamersListFullScreen) {

    }

    @Override
    public void RefreshNewStreamerFullScreenAdapter(List<NewStreamers> newStreamersListFullScreen) {

    }

    @Override
    public void MostPopularStreamer(List<MostPopularLivesResponse> mostPopularLivesResponses) {

    }

    @Override
    public void StreamerDetailScreenLive(List<AllStreamerData> allStreamerData)
    {

    }

    public void showCartCount(TextView btnCartCount){
        if (userPreferences!=null && userPreferences.getTotalCartSize()!=null && !userPreferences.getTotalCartSize().equals("") && !userPreferences.getTotalCartSize().equals("0")){
            btnCartCount.setVisibility(View.VISIBLE);
            btnCartCount.setText(userPreferences.getTotalCartSize());
        }else {
            btnCartCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void hideView(View view){
        view.setVisibility(View.GONE);
    }

    public void showView(View view){
        view.setVisibility(View.VISIBLE);
    }
}
