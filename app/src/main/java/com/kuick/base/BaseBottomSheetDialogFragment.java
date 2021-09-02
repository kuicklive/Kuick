package com.kuick.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kuick.R;
import com.kuick.Remote.ApiInterface;
import com.kuick.Remote.Networking;
import com.kuick.Response.BaseResponse;
import com.kuick.SplashScreen.SecondSplashScreen;
import com.kuick.activity.SignupActivity;
import com.kuick.pref.Language;
import com.kuick.pref.UserPreferences;
import com.kuick.util.comman.CommanDialogs;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;
import com.kuick.util.loader.LoaderDialogFragment;
import com.kuick.util.loader.LoadingDialog;
import com.kuick.util.network.NetworkHelperImpl;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.Constants.OK;
import static com.kuick.util.comman.Constants.SESSION_EXPIRE;
import static com.kuick.util.comman.Utility.goToNextScreen;

public class BaseBottomSheetDialogFragment  extends BottomSheetDialogFragment implements View.OnClickListener,BaseView {

    private NetworkHelperImpl networkHelper;
    public UserPreferences userPreferences;
    public Language language;
    public ApiInterface apiService;
    private LoaderDialogFragment loaderDialogFragment;
    private String TAG = "BaseBottomSheetDialogFragment";
    private LoaderDialogFragment loader;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkHelper = new NetworkHelperImpl();
        userPreferences = UserPreferences.newInstance(getContext());
        language = Language.newInstance(getContext());
        apiService = Networking.getClient().create(ApiInterface.class);
        loader =  new LoaderDialogFragment();
    }

    public boolean checkInternetConnectionWithMessage(Context context) {
        if (networkHelper.isNetworkConnected(context)) {
            return true;
        } else {
            showErrorDialogInternet(R.string.oops, language.getLanguage(KEY.internet_connection_lost_please_retry_again)/*R.string.network_connection_error*/);
            return false;
        }
    }

    protected void showErrorDialog(int title, int message) {
        CommanDialogs.showOneButtonDialog(
                getContext(),
                getString(title),
                getString(message),
                language.getLanguage(KEY.ok),
                (dialog, which) -> dialog.dismiss(),
                true
        );
    }

    protected void showErrorDialogInternet(int title, String message) {
        CommanDialogs.showOneButtonDialog(
                getContext(),
                getString(title),
                message,
                language.getLanguage(KEY.ok),
                (dialog, which) -> dialog.dismiss(),
                true
        );
    }


    @Override
    public void showLoader(boolean isClose) {


        try {
            if (!loader.isAdded()) {
                loader.show(getChildFragmentManager(), LoaderDialogFragment.TAG);
            }
        }catch (Exception e){
            Utility.PrintLog(TAG,"BaseBottomDialog showLoader() Exception() ");
        }
    }

    @Override
    public void hideLoader() {

      try {
          if (loader!=null){
              loader.dismiss();
          }
      }catch (Exception e){
          Utility.PrintLog(TAG,"Loader Exception");
      }
    }

    @Override
    public void showMessageAndFinish(String message) {

    }

    @Override
    public void showSnackResponse(String message) {

    }

    @Override
    public Activity getViewActivity() {
        return null;
    }

    @Override
    public void showSnackErrorMessage(String message) {

    }

    @Override
    public void onCancelCall() {

    }

    public <T extends BaseResponse> boolean checkResponseStatusWithMessage(T response, boolean showMessage) {

        boolean isResponseValid;

        if (response == null) {
            isResponseValid = false;
        } else {

            final String messageFromServer = response.getMessage();

            if (response.getStatus()) {
                isResponseValid = true;

                if (showMessage) {
                    // showSnackResponse(messageFromServer);
                }

            } else {
                isResponseValid = false;
                if (showMessage) {
                    //  showSnackErrorMessage(messageFromServer);
                }
            }

        }

        return isResponseValid;
    }

    public void checkErrorCode(int errorcode, Activity activity) {
        if (errorcode == Networking.ErrorCode)
        {
            if (userPreferences != null && userPreferences.getUserId() != null) {

                if (homeActivity!=null){
                    homeActivity.disconnectSocket();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(SESSION_EXPIRE);
                //.setTitle("Warning");
                builder.setCancelable(false);
                builder.setPositiveButton(OK, (dialog, id) -> {
                    userPreferences.removeCurrentUser();

                    goToNextScreen(activity, SecondSplashScreen.class, true);
                    return;
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }

    }

}
