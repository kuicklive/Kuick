package com.kuick.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.UniversalTimeScale;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.kuick.R;
import com.kuick.Response.CommonResponse;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.LiveBannerPagerBinding;
import com.kuick.databinding.ProductImagePagerItemBinding;
import com.kuick.model.BannerDetails;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.kuick.fragment.HomeFragment.TAG;

public class ProductImagePagerAdapter extends PagerAdapter {
    private final String imageURL;
    private final boolean isSingleImage;
    private final List<CommonResponse.ProductImages> productImageList;
    private Context mContext;

    public ProductImagePagerAdapter(String imageUrl, List<CommonResponse.ProductImages> productImage, boolean isSingleImage) {
        this.imageURL = imageUrl;
        this.isSingleImage = isSingleImage;
        this.productImageList = productImage;
    }

    @Override
    public int getCount() {
        if (isSingleImage)
        return 1;
        else
            return productImageList.size() + 1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mContext = container.getContext();
        @NonNull ProductImagePagerItemBinding view = ProductImagePagerItemBinding.inflate(LayoutInflater.from(container.getContext()),container,false);
        container.addView(view.getRoot());

        onDataSet(view,position);

        return view.getRoot();
    }

    private void onDataSet(ProductImagePagerItemBinding view, int position) {

        if (isSingleImage){
            Utility.PrintLog(TAG,"is single image : " + imageURL);
            BaseActivity.showGlideImage(mContext,imageURL,view.bannerImage);
        }else {
            if (position == 0){
                BaseActivity.showGlideImage(mContext,imageURL,view.bannerImage);
            }else {
                CommonResponse.ProductImages imageList = productImageList.get(position - 1);
                BaseActivity.showGlideImage(mContext,imageList.getImage(),view.bannerImage);
            }
        }
    }
}
