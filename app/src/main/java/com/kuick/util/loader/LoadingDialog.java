package com.kuick.util.loader;

import android.app.Dialog;
import android.content.Context;

import com.kuick.R;
import com.kuick.util.comman.Utility;

public class LoadingDialog {

    static Dialog dialog = null;

    public static void showLoadDialog(Context context) {
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.fragment_loader);
        dialog.show();
        Utility.PrintLog("LoaderTAG","ShowLoader()");
    }

    public static void hideLoadDialog() {
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
            Utility.PrintLog("LoaderTAG","hideLoadDialog() dialog.dismiss()");
        }
        Utility.PrintLog("LoaderTAG","hideLoadDialog() ");
    }

}
