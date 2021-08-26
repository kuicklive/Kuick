package com.kuick.util.comman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class CommanDialogs {

    public static void showOneButtonDialog(Context context,
                                           @Nullable String title,
                                           @NotNull String msg, String positiveLabel,
                                           @NotNull DialogInterface.OnClickListener positiveOnClick,
                                           boolean isCancelAble) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setCancelable(isCancelAble);
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showTwoButtonDialog(Context context, String title, String msg, String positiveLabel,
                                           DialogInterface.OnClickListener positiveOnClick,
                                           String negativeLabel, DialogInterface.OnClickListener negativeOnClick,
                                           boolean isCancelAble) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);
        builder.setCancelable(isCancelAble);

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
