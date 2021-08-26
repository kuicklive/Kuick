package com.kuick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.Response.BaseResponse;
import com.kuick.Response.CommonResponse;
import com.kuick.activity.LiveActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ProductListItemBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_ID;
import static com.kuick.Remote.EndPoints.PARAM_PRODUCT_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.activity.LiveActivity.internet;
import static com.kuick.activity.LiveActivity.liveEventListener;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private final List<CommonResponse.ProductData> productData;
    private final LiveActivity liveActivity;
    private Context mContext;
    private final String TAG = "ProductListAdapter";

    public ProductListAdapter(List<CommonResponse.ProductData> productData, LiveActivity liveActivity) {
        this.productData = productData;
        this.liveActivity = liveActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(ProductListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
        holder.itemBinding.productImg.setOnClickListener(v ->{
            liveEventListener.startPictureInPicture(productData.get(position).getId(), false, false);
        });
        holder.itemBinding.btnCart.setOnClickListener(v -> CheckAPI(position));

        onBindData(holder,position);

    }

    private void onBindData(ViewHolder holder, int position) {
        CommonResponse.ProductData data = productData.get(position);

        if (data.getImage()!=null)
        {
            BaseActivity.showGlideImage(mContext,data.getImage(),holder.itemBinding.productImg);
        }
        holder.itemBinding.productName.setText(data.getName());

        String currencyCode = liveActivity.userPreferences.getCurrencyCode();
        Utility.PrintLog(TAG,"currency code :" + currencyCode);
        holder.itemBinding.productPrice.setText(data.getPrice());

    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ProductListItemBinding itemBinding;
        public ViewHolder(@NonNull ProductListItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;

        }
    }

        public void CheckAPI(int position) {
            try {

                Map<String, String> addCard = new HashMap<>();
                CommonResponse.ProductData data = productData.get(position);

                addCard.put(PARAM_USER_ID, liveActivity.userPreferences.getUserId());
                addCard.put(PARAM_PRODUCT_ID, data.getId());
                addCard.put(PARAM_PRODUCT_TYPE, data.getProduct_type());

                Call<BaseResponse> call = null;

                if (liveActivity.checkInternetConnectionWithMessage()) {

                    liveActivity.showLoader(true);

                    call = liveActivity.apiService.doAddToCartCheck(liveActivity.userPreferences.getApiKey(), addCard);

                    call.enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response) {
                            Utility.PrintLog(TAG, response.toString());

                            liveActivity.checkErrorCode(response.code());

                            if (!response.isSuccessful()) {
                                liveActivity.hideLoader();
                                return;
                            }

                            if (liveActivity.checkResponseStatusWithMessage(response.body(), true)) {
                                setResponse(response.body(),data.getId());
                            }
                            liveActivity.hideLoader();
                        }

                        @Override
                        public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                            //showSnackResponse(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                            Utility.PrintLog(TAG, t.toString());
                            liveActivity.hideLoader();
                        }
                    });
                }

            } catch (Exception e) {
                Utility.PrintLog(TAG, e.toString());
                //showSnackResponse(language.getLanguage(KEY.something_wrong_happened_please_retry_again));
                liveActivity.hideLoader();
            }
        }

        private void setResponse(BaseResponse response, String selectedProductId) {
            if (response != null) {

                if (response.getRedirect_page().equals(Constants.CART_PAGE))
                {
                    liveEventListener.startPictureInPicture(selectedProductId, true, false);
                    //liveActivity.startActivity(new Intent(mContext, CartPageActivity.class));

                } else if (response.getRedirect_page().equals(Constants.PRODUCT_PAGE))
                {
                    liveEventListener.startPictureInPicture(selectedProductId, false, false);
                    //liveActivity.startActivity(new Intent(mContext, ProductDetailsActivity.class).putExtra(PARAM_PRODUCT_ID, selectedProductId));

                }
            }
        }

}
