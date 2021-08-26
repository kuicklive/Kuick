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

import static com.kuick.activity.HomeActivity.clickEventListener;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private final String[] profileTabList;
    private final int[] profileIconList;
    private Context mContext;

    public ProfileAdapter(String[] profileTabList, int[] profileIconList) {
        this.profileTabList = profileTabList;
        this.profileIconList = profileIconList;
    }


    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {

        holder.txtTitle.setText(profileTabList[position]);
        holder.tabIcon.setImageResource(profileIconList[position]);

        holder.itemView.setOnClickListener(v -> clickEventListener.onClickPosition(position));
    }

    @Override
    public int getItemCount() {
        return profileTabList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView tabIcon,goBtn;
        TextView txtTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tabIcon = itemView.findViewById(R.id.iconTab);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            goBtn = itemView.findViewById(R.id.btnGo);
        }
    }
}
