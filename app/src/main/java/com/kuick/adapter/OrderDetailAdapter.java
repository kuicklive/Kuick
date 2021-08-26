package com.kuick.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.OrderDetailResponse;
import com.kuick.activity.OrderDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.OrderDetailBinding;
import com.kuick.databinding.TreandingStreamersItemBinding;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import java.util.List;

import static com.kuick.util.comman.KEY.Confirmed;
import static com.kuick.util.comman.KEY.Delivered;
import static com.kuick.util.comman.KEY.Rejected;
import static com.kuick.util.comman.KEY.Returned;
import static com.kuick.util.comman.KEY.Shipped;
import static com.kuick.util.comman.KEY.approved;
import static com.kuick.util.comman.KEY.canclled;
import static com.kuick.util.comman.KEY.completed;
import static com.kuick.util.comman.KEY.pending;
import static com.kuick.util.comman.KEY.rejected;
import static com.kuick.util.comman.KEY.returned;
import static com.kuick.util.comman.KEY.shipped;


public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private final List<OrderDetailResponse.OrderProductDetails> orderProductDetail;
    private OrderDetailsActivity mContext;
    private String TAG = "OrderDetailAdapter" ;

    public OrderDetailAdapter(List<OrderDetailResponse.OrderProductDetails> orderProductDetail, OrderDetailsActivity orderDetailsActivity) {
        this.orderProductDetail = orderProductDetail;
        this.mContext = orderDetailsActivity;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(OrderDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ViewHolder holder, int position) { onBindData(holder,position); }

    @SuppressLint("SetTextI18n")
    private void onBindData(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        OrderDetailResponse.OrderProductDetails data = orderProductDetail.get(position);

        BaseActivity.showGlideImage(mContext,data.getImage(),holder.itemBinding.productImg);

        holder.itemBinding.productName.setText(data.getName());
        holder.itemBinding.priceTag.setText(data.getProduct_price());
        Utility.PrintLog(TAG,"Price : " + data.getProduct_price());
        holder.itemBinding.orderQuantity.setText(mContext.language.getLanguage(KEY.quantity) + " " + data.getNum_of_items());


    }



    @Override
    public int getItemCount() {
        return orderProductDetail.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public OrderDetailBinding itemBinding;
        public ViewHolder(@NonNull OrderDetailBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }
}
