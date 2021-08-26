package com.kuick.util.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kuick.R;
import com.kuick.databinding.DialogFragmentSingleActionBinding;
import com.kuick.fragment.HomeFragment;

import static com.kuick.util.comman.Constants.KEY_BUTTON_IMAGE;
import static com.kuick.util.comman.Constants.KEY_BUTTON_TEXT;
import static com.kuick.util.comman.Constants.KEY_MESSAGE;
import static com.kuick.util.comman.Constants.KEY_STATUS;


public class SingleActionDialogFragment extends DialogFragment {

    public static final String TAG = "SingleActionDialogFragment";
    private SingleActionDialogFragment fragment = SingleActionDialogFragment.this;
    private DialogFragmentSingleActionBinding binding;
    private onSingleActionClickListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (onSingleActionClickListener) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static SingleActionDialogFragment newInstance() {
        Bundle bundle = new Bundle();
        SingleActionDialogFragment fragment = new SingleActionDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    //public static SingleActionDialogFragment newInstance(boolean status, String message, String buttonText, boolean showImage) {
    //    SingleActionDialogFragment takePictureDialogFragment = new SingleActionDialogFragment();
    //    Bundle bundle = new Bundle();
    //    bundle.putString(KEY_MESSAGE, message);
    //    bundle.putBoolean(KEY_STATUS, status);
    //    bundle.putString(KEY_BUTTON_TEXT, buttonText);
    //    bundle.putBoolean(KEY_BUTTON_IMAGE, showImage);
    //    takePictureDialogFragment.setArguments(bundle);
    //    return takePictureDialogFragment;
    //}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        binding = DialogFragmentSingleActionBinding.inflate(getActivity().getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        setCancelable(false);
        //setupView();
        setOnClickListeners();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void setOnClickListeners() {
        binding.btnOk.setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.btnOk) {
            final boolean status = getArguments().getBoolean(KEY_STATUS);
            listener.onSingleActionClick(status);
            dismiss();
        }
    }

    protected void setupView() {

        if (getArguments() != null) {
            String message = getArguments().getString(KEY_MESSAGE);
            String buttonText = getArguments().getString(KEY_BUTTON_TEXT);
            final boolean showImage = getArguments().getBoolean(KEY_BUTTON_IMAGE);
            binding.txtMessage.setText(message);
            binding.btnOk.setText(buttonText);


        }

    }

    public interface onSingleActionClickListener {
        void onSingleActionClick(boolean status);
    }

}

