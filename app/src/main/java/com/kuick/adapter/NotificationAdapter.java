package com.kuick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.NotificationResponse;
import com.kuick.databinding.FullTreandingStreamersItemBinding;
import com.kuick.databinding.NotificationItemBinding;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final List<NotificationResponse.Notification> notificationList;


    public NotificationAdapter(List<NotificationResponse.Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(NotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
                onBindData(holder,position);
    }

    private void onBindData(ViewHolder holder, int position) {

        NotificationResponse.Notification notification = notificationList.get(position);

        setIconAccordingType(holder,notification.getType());

        holder.itemBinding.notificationTitle.setText(notification.getMessage());
        holder.itemBinding.txtDate.setText(notification.getShow_date());
        holder.itemBinding.txtMin.setText(notification.getTime_ago());
    }

    private void setIconAccordingType(ViewHolder holder, String type) {

        if (type!=null && !type.equals(""))
        {
            switch (type)
            {
                case "order":
                case "order_confirmed":
                case "order_rejected":
                case "order_shipped":
                case "order_delivered":
                case "order_returned":
                     holder.itemBinding.notificationIcon.setImageResource(R.drawable.order_notification_icon);
                    break;
                case "live_streaming":
                    holder.itemBinding.notificationIcon.setImageResource(R.drawable.live_notification_icon);
                    break;
                default:
                    holder.itemBinding.notificationIcon.setImageResource(R.drawable.notification_icon);
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
       private NotificationItemBinding itemBinding;
        public ViewHolder(@NonNull NotificationItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;

        }
    }


}
