package com.kuick.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.activity.CartPageActivity;
import com.kuick.activity.HomeActivity;
import com.kuick.adapter.ProfileAdapter;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.FragmentProfileBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import static com.kuick.util.comman.Utility.goToNextScreen;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = "ProfileFragment";
    public String[] profileTabList;
    private TextView txtTitle;
    public int[] profileIconList;
    private FragmentProfileBinding binding;
    public static  boolean isLanguageChanged = false;

    public static ProfileFragment newInstance() {
        Bundle bundle = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        onClickListener();
        setLanguageLable();
        setAdapter();

        return binding.getRoot();
    }

    private void onClickListener() {
        binding.getRoot().findViewById(R.id.img_cart).setOnClickListener(this);
        txtTitle = binding.getRoot().findViewById(R.id.txtTitle);

        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);

    }

    private void setLanguageLable() {

        try{

            if (language!=null){

                txtTitle.setText(language.getLanguage(KEY.profile));
            }

        }catch (Exception e){
            Utility.PrintLog(TAG,"Exception : " + e.toString());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setAdapter() {

        if (userPreferences.getSocialId()!=null && !userPreferences.getSocialId().equals("") && !userPreferences.getSocialId().equals("null")){
            profileTabList = new String[]{
                    language.getLanguage(KEY.my_profile),
                    language.getLanguage(KEY.address_information),
                    language.getLanguage(KEY.payment_information),
                    language.getLanguage(KEY.about_us),
                    language.getLanguage(KEY.contact_us),
                    language.getLanguage(KEY.delivery),
                    language.getLanguage(KEY.returns),
                    language.getLanguage(KEY.privacy_policy),
                    language.getLanguage(KEY.terms_of_service),
                    language.getLanguage(KEY.logout)
            };

            //for profile tab icon

            profileIconList = new int[]{
                    R.drawable.user_icon,
                    R.drawable.address_info,
                    R.drawable.icon_payment_card,
                    R.drawable.about,
                    R.drawable.contact_us,
                    R.drawable.delivery_icon,
                    R.drawable.outline,
                    R.drawable.privacy_policy,
                    R.drawable.insurance,
                    R.drawable.logout_icon
            };
        }else {

            profileTabList = new String[]{
                    language.getLanguage(KEY.my_profile),
                    language.getLanguage(KEY.change_password),
                    language.getLanguage(KEY.address_information),
                    language.getLanguage(KEY.payment_information),
                    language.getLanguage(KEY.about_us),
                    language.getLanguage(KEY.contact_us),
                    language.getLanguage(KEY.delivery),
                    language.getLanguage(KEY.returns),
                    language.getLanguage(KEY.privacy_policy),
                    language.getLanguage(KEY.terms_of_service),
                    language.getLanguage(KEY.logout)
            };

            //for profile tab icon

            profileIconList = new int[]{
                    R.drawable.user_icon,
                    R.drawable.forgot_password_icon,
                    R.drawable.address_info,
                    R.drawable.icon_payment_card,
                    R.drawable.about,
                    R.drawable.contact_us,
                    R.drawable.delivery_icon,
                    R.drawable.outline,
                    R.drawable.privacy_policy,
                    R.drawable.insurance,
                    R.drawable.logout_icon
            };

        }

        binding.rvProfileTab.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvProfileTab.setAdapter(new ProfileAdapter(profileTabList, profileIconList));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cart:
                goToNextScreen(getActivity(), CartPageActivity.class, false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Utility.PrintLog("onResume()" ,"onResume() ProfileFragment");
        if (isLanguageChanged){
            isLanguageChanged = false;
            HomeActivity.languageChangeListener.isLanguageChanged();
            setLanguageLable();
            setAdapter();
        }


        TextView btnCartCount = binding.getRoot().findViewById(R.id.cartCount);
        showCartCount(btnCartCount);
        hideLoader();
    }
}
