package com.kuick.util.loader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kuick.R;
import com.kuick.base.BaseView;

import java.util.logging.Logger;

import static com.kuick.util.comman.Constants.BundleKeys.ARG_LOADER_FINISH_ACTIVITY;


public class LoaderDialogFragment extends DialogFragment {


    public static final String TAG = "LoaderDialogFragment";

    private BaseView mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BaseView) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("activity must implement onCancelCallListener");
        }

    }

    public static LoaderDialogFragment newInstance(boolean isClose) {
        LoaderDialogFragment loaderDialogFragment = new LoaderDialogFragment();
        Bundle bundle = new Bundle();
        //bundle.putString(ARG_LOADER_MESSAGE, message);
        bundle.putBoolean(ARG_LOADER_FINISH_ACTIVITY, isClose);
        loaderDialogFragment.setArguments(bundle);
        return loaderDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_loader, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(true);
        setupView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }


    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setupView(View view) {
        /*if (getArguments() != null) {
            String message = getArguments().getString(ARG_LOADER_MESSAGE);
            if (!TextUtils.isEmpty(message)) {

                TextView tvMessage = view.findViewById(R.id.tvLoader);
                tvMessage.setText(message);
            }
            Utility.hideKeyboard(getActivity());
        }*/
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
       /* final boolean isClose = getArguments().getBoolean(ARG_LOADER_FINISH_ACTIVITY);
        if (isClose) {
            getActivity().finish();
        }*/
    }
}
