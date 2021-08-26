package com.kuick.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.OrderHistoryResponse;
import com.kuick.activity.HomeActivity;
import com.kuick.activity.OrderDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.OrdersHistoryItemBinding;
import com.kuick.databinding.TreandingStreamersItemBinding;
import com.kuick.model.OrderHistoryModel;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.List;

import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.util.comman.KEY.Confirmed;
import static com.kuick.util.comman.KEY.Delivered;
import static com.kuick.util.comman.KEY.Rejected;
import static com.kuick.util.comman.KEY.Returned;
import static com.kuick.util.comman.KEY.Shipped;
import static com.kuick.util.comman.KEY.approved;
import static com.kuick.util.comman.KEY.canclled;
import static com.kuick.util.comman.KEY.completed;
import static com.kuick.util.comman.KEY.confirmed;
import static com.kuick.util.comman.KEY.delivered;
import static com.kuick.util.comman.KEY.pending;
import static com.kuick.util.comman.KEY.rejected;
import static com.kuick.util.comman.KEY.returned;
import static com.kuick.util.comman.KEY.shipped;


public class OrdersHistoryAdapter extends RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder> {
    private final List<OrderHistoryResponse> orderHistoryList;
    private HomeActivity mContext;
    private String TAG = "OrdersHistoryAdapter";

    public OrdersHistoryAdapter(List<OrderHistoryResponse> orderHistoryList, HomeActivity homeActivity) {
        this.orderHistoryList = orderHistoryList;
        this.mContext = homeActivity;

    }

    @NonNull
    @Override
    public OrdersHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(OrdersHistoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersHistoryAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        setLanguageLable(holder);
        onBindData(holder,position);
    }

    private void setLanguageLable(ViewHolder holder) {

        try {

            if (homeActivity.language != null) {

                Utility.PrintLog(TAG,"homeActivity.language.getLanguage(KEY.days) : "+ homeActivity.language.getLanguage(KEY.days));

            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception : " + e.toString());
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void onBindData(ViewHolder holder, int position) {
        OrderHistoryResponse data = orderHistoryList.get(position);

        BaseActivity.showGlideImage(mContext,data.getImage(),holder.itemBinding.productImg);
        holder.itemBinding.productName.setText(data.getName());
        holder.itemBinding.priceTag.setText(data.getTotal_amount());
        Utility.PrintLog(TAG,"Order History Price : " + data.getTotal_amount());
        holder.itemBinding.orderID.setText(mContext.language.getLanguage(KEY.order)  + " #" + data.getOrder_id());
        holder.itemBinding.orderQuantity.setText(mContext.language.getLanguage(KEY.quantity) + " " + data.getQuantity());

        holder.itemView.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, OrderDetailsActivity.class).putExtra(Constants.ORDER_ID,data.getOrder_id())));

        setOrderStatus(data,holder,position);

    }

    private void setOrderStatus(OrderHistoryResponse data, ViewHolder holder, int position) {
        holder.itemBinding.progressBtn.setText(data.getOrder_status());

        String status = data.getOrder_status();
        Utility.PrintLog(TAG,"status : " + status);

        if (status.equalsIgnoreCase(pending)){
            setButton(
                    holder,
                    mContext.language.getLanguage(KEY.in_progress),
                    mContext.getResources().getDrawable(R.drawable.yellow_shape));

        }else if (status.equalsIgnoreCase(canclled)){
            setButton(
                    holder,
                    mContext.language.getLanguage(KEY.canclled),
                    mContext.getResources().getDrawable(R.drawable.red_shape));
        }
        else if (status.equalsIgnoreCase(approved)){
            setButton(
                    holder,
                    mContext.language.getLanguage(confirmed),
                    mContext.getResources().getDrawable(R.drawable.dark_green));

        } else if (status.equalsIgnoreCase(rejected)){
            setButton(
                    holder,
                    mContext.language.getLanguage(rejected),
                    mContext.getResources().getDrawable(R.drawable.pink_shape));

        } else if (status.equalsIgnoreCase(shipped)){
            setButton(
                    holder,
                    mContext.language.getLanguage(shipped),
                    mContext.getResources().getDrawable(R.drawable.shipped_color));

        } else if (status.equalsIgnoreCase(returned)){
            setButton(
                    holder,
                    mContext.language.getLanguage(returned),
                    mContext.getResources().getDrawable(R.drawable.retrun_shape));

        } else if (status.equalsIgnoreCase(completed)){
            Utility.PrintLog(TAG,"completed : " + status);
            setButton(
                    holder,
                    mContext.language.getLanguage(delivered),
                    mContext.getResources().getDrawable(R.drawable.light_green));
        }


    }

    private void setButton(ViewHolder holder, String text, Drawable drawable) {

        holder.itemBinding.progressBtn.setText(text);
        holder.itemBinding.progressBtn.setBackground(drawable);
    }


    @Override
    public int getItemCount()
    {
        Log.e("orderHistoryList.size()","size : " + orderHistoryList.size());
        return orderHistoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private OrdersHistoryItemBinding itemBinding;
        public ViewHolder(@NonNull OrdersHistoryItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }
}
