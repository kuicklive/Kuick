package com.kuick.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.Response.CartDetails;
import com.kuick.Response.CommonResponse;
import com.kuick.activity.CartPageActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.ShippingBasketItemBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kuick.Remote.EndPoints.PARAM_CART_ID;
import static com.kuick.Remote.EndPoints.PARAM_TYPE;
import static com.kuick.Remote.EndPoints.PARAM_USER_ID;
import static com.kuick.activity.CartPageActivity.priceCalculationListener;


public class ShippingBasketAdapter extends RecyclerView.Adapter<ShippingBasketAdapter.ViewHolder> {
    public List<CartDetails> cartDetailsList;
    private final CartPageActivity mContext;
    private final String TAG = "ShippingBasketAdapter";
    public static double previousPrice = 0;

    public ShippingBasketAdapter(List<CartDetails> cartDetailsList, CartPageActivity mContext) {
        this.cartDetailsList = cartDetailsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ShippingBasketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ShippingBasketItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShippingBasketAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        onBindingData(holder, position);

    }

    @SuppressLint("SetTextI18n")
    private void onBindingData(ViewHolder holder, int position) {

        CartDetails data = cartDetailsList.get(position);

        BaseActivity.showGlideImage(mContext, data.getImage(), holder.itemBinding.productImg);
        holder.itemBinding.productName.setText(data.getName());
        holder.itemBinding.priceTag.setText(data.getPrice());
        holder.itemBinding.numberOfItem.setText(Utility.getFormatedValue(Integer.parseInt(data.getQty())));


        holder.itemBinding.btnMinus.setOnClickListener(v -> {

            try {
                CartDetails cardData = cartDetailsList.get(position);

                if (cardData.getQty().equals("1")) {
                    showInternetDialog(holder, mContext, position, cardData);
                    return;
                }
                updateCartAndGetCartDetails(cardData.getCart_id(), Constants.MINUS, holder.itemBinding.btnMinus);


            } catch (Exception e) {
                Utility.PrintLog(TAG, "onMinus() Exception : " + e.toString());
            }

        });

        holder.itemBinding.btnPluse.setOnClickListener(v -> {
            try {

                CartDetails cardData = cartDetailsList.get(position);
                int qty = Integer.parseInt(holder.itemBinding.numberOfItem.getText().toString());
                int totalQty = Integer.parseInt(cardData.getTotal_qty());

                if (qty == totalQty){
                    mContext.showSnackErrorMessage(mContext.language.getLanguage(KEY.quantity_is_not_available_for_this_product));
                    return;
                }
                updateCartAndGetCartDetails(cardData.getCart_id(), Constants.PLUS, holder.itemBinding.btnPluse);

            } catch (Exception e) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return cartDetailsList.size();
    }

    public List<CartDetails> getCardList() {
        return cartDetailsList;
    }

    public void getCardList(CartDetails cart) {
        cartDetailsList.remove(cart);
    }

    private void updateCartAndGetCartDetails(String cart_id, String type, ImageView button) {

        try {

            if (mContext.checkInternetConnectionWithMessage()) {
                mContext.showLoader(true);
                button.setEnabled(false);

                Map<String, String> updateCart = new HashMap<>();

                updateCart.put(PARAM_USER_ID, mContext.userPreferences.getUserId());
                updateCart.put(PARAM_CART_ID, cart_id);
                updateCart.put(PARAM_TYPE, type);

                Call<CommonResponse> call = mContext.apiService.doUpdateCart(mContext.userPreferences.getApiKey(), updateCart);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonResponse> call, @NotNull Response<CommonResponse> response) {
                        Utility.PrintLog(TAG, response.toString());

                        mContext.checkErrorCode(response.code());

                        if (response.body() != null) {

                            if (mContext.checkResponseStatusWithMessage(response.body(), true)) {

                                priceCalculationListener.removePaymentMethod();
                                priceCalculationListener.removeShipping();
                                cartDetailsList = response.body().getCartDetails();
                                RefreshAdapter(cartDetailsList);
                                priceCalculationListener.hidePromo();
                                priceCalculationListener.onProductCalculation(response.body());

                            } else {
                                mContext.showSnackErrorMessage(mContext.language.getLanguage(response.body().getMessage()));

                            }
                        }
                        button.setEnabled(true);
                        mContext.hideLoader();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                        Utility.PrintLog(TAG, t.toString());
                        mContext.hideLoader();
                        button.setEnabled(true);
                    }
                });
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, e.toString());
            mContext.hideLoader();
            button.setEnabled(true);
        }
    }

    public void RefreshAdapter(List<CartDetails> cartDetailsList) {
        previousPrice = 0;
        this.cartDetailsList = cartDetailsList;
        notifyDataSetChanged();
    }

    public void showInternetDialog(ViewHolder holder, CartPageActivity mContext, int position, CartDetails cardData) {
        new AlertDialog.Builder(this.mContext)
                .setTitle(this.mContext.language.getLanguage(KEY.message))
                .setMessage(mContext.language.getLanguage(KEY.do_you_want_to_sure_delete_this_product))
                .setPositiveButton(this.mContext.language.getLanguage(KEY.ok), (dialog, which) -> {
                    ShippingBasketAdapter.this.mContext.deleteCart(holder, ShippingBasketAdapter.this.mContext, position, cardData);
                    dialog.dismiss();
                }).setNegativeButton(mContext.language.getLanguage(KEY.cancel), (dialog, which) -> {
            dialog.dismiss();
        }).show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ShippingBasketItemBinding itemBinding;

        public ViewHolder(@NonNull ShippingBasketItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }


}
