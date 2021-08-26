package com.kuick.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.activity.LiveActivity;
import com.kuick.adapter.LiveStreamersAdapter;
import com.kuick.adapter.ProductListAdapter;
import com.kuick.databinding.BottomProductListDilaogBinding;
import com.kuick.databinding.FragmentHomeBinding;
import com.kuick.pref.UserPreferences;

import java.util.List;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private List<CommonResponse.ProductData> productData;
    private  LiveActivity liveActivity;

    public BottomSheetDialog(List<CommonResponse.ProductData> productData, LiveActivity liveActivity) {
        this.productData = productData;
        this.liveActivity = liveActivity;
    }
    public BottomSheetDialog(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @NonNull BottomProductListDilaogBinding binding = BottomProductListDilaogBinding.inflate(inflater, container, false);

        if (productData!=null && productData.size() > 0){
            binding.rvProductList.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
            binding.rvProductList.setAdapter(new ProductListAdapter(productData,liveActivity));
        }

        return binding.getRoot();
    }
}
