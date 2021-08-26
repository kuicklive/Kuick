package com.kuick.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.activity.LiveActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.LiveBannerPagerBinding;
import com.kuick.util.comman.KEY;
import com.kuick.util.comman.Utility;

import java.util.ArrayList;

import static com.kuick.fragment.HomeFragment.TAG;
import static com.kuick.util.comman.KEY.Confirmed;
import static com.kuick.util.comman.KEY.Delivered;
import static com.kuick.util.comman.KEY.Rejected;
import static com.kuick.util.comman.KEY.Returned;
import static com.kuick.util.comman.KEY.Shipped;
import static com.kuick.util.comman.KEY.approved;
import static com.kuick.util.comman.KEY.arrived;
import static com.kuick.util.comman.KEY.canclled;
import static com.kuick.util.comman.KEY.completed;
import static com.kuick.util.comman.KEY.pending;
import static com.kuick.util.comman.KEY.rejected;
import static com.kuick.util.comman.KEY.returned;
import static com.kuick.util.comman.KEY.shipped;

public class OrderProccessAdapter extends RecyclerView.Adapter<OrderProccessAdapter.ViewHolder> {
    private final ArrayList<String> statusList;
    private final String status;
    private Context mContext;

    public OrderProccessAdapter(ArrayList<String> statusList, String order_status) {
        this.status = order_status;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public OrderProccessAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_order_status,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProccessAdapter.ViewHolder holder, int position) {

        if (position == statusList.size()-1){holder.ivTimelineLine.setVisibility(View.GONE); }

        holder.txtTitle.setText(statusList.get(position));
        Utility.PrintLog(TAG,"status "+ status);

        if (status.equalsIgnoreCase(pending)){

            if (position == 0){
                holder.ivTimeCircle.setImageResource(R.drawable.pluse_blue);
                holder.ivTimelineLine.setImageResource(R.drawable.line);
                holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.bgSplash));
            }

        }else if (status.equalsIgnoreCase(canclled)){

            if (position == 0 || position == 1){
                holder.ivTimeCircle.setImageResource(R.drawable.pluse_blue);
                holder.ivTimelineLine.setImageResource(R.drawable.line);
                holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.bgSplash));
            }

        } else if (status.equalsIgnoreCase(Confirmed) || status.equalsIgnoreCase(approved)){

            if (position == 0 || position == 1){
                holder.ivTimeCircle.setImageResource(R.drawable.pluse_blue);
                holder.ivTimelineLine.setImageResource(R.drawable.line);
                holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.bgSplash));
            }

        } else if (status.equalsIgnoreCase(rejected)){

            if (position == 0 || position == 1){
                holder.ivTimeCircle.setImageResource(R.drawable.pluse_blue);
                holder.ivTimelineLine.setImageResource(R.drawable.line);
                holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.bgSplash));
            }

        } else if (status.equalsIgnoreCase(shipped)){

            if (position == 0 || position == 1 || position == 2){
                holder.ivTimeCircle.setImageResource(R.drawable.pluse_blue);
                holder.ivTimelineLine.setImageResource(R.drawable.line);
                holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.bgSplash));
            }

        } else if (status.equalsIgnoreCase(returned)){

            if (position == 0 || position == 1 || position == 2 || position == 3){
                holder.ivTimeCircle.setImageResource(R.drawable.pluse_blue);
                holder.ivTimelineLine.setImageResource(R.drawable.line);
                holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.bgSplash));
            }

        } else if (status.equalsIgnoreCase(completed) || status.equalsIgnoreCase(arrived)){

            if (position == 0 || position == 1 || position == 2 || position == 3){
                holder.ivTimeCircle.setImageResource(R.drawable.pluse_blue);
                holder.ivTimelineLine.setImageResource(R.drawable.line);
                holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.bgSplash));
            }

        }

    }


    @Override
    public int getItemCount() {
        return statusList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivTimeCircle, ivTimelineLine;
        TextView txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTimeCircle =   itemView.findViewById(R.id.ivTimeCircle);
            ivTimelineLine =   itemView.findViewById(R.id.ivTimelineLine);
            txtTitle =   itemView.findViewById(R.id.txtTitle);

        }
    }
}
