package com.kuick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.Response.CommonResponse;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.CommentItemBinding;
import com.kuick.util.comman.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {


    private List<CommonResponse.Comments> commentsList;
    private Context mContext;

    public CommentsAdapter(List<CommonResponse.Comments> comments) {
        this.commentsList = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(CommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommonResponse.Comments data = commentsList.get(position);


        holder.itemBinding.txtComment.setText(data.getComment());
        holder.itemBinding.txtName.setText(data.getFull_name());
        BaseActivity.showGlideImage(mContext, data.getProfile_image(), holder.itemBinding.mIcon);
    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public void RefreshAdapter(List<CommonResponse.Comments> commentsList) {
        this.commentsList = commentsList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CommentItemBinding itemBinding;

        public ViewHolder(@NonNull CommentItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }
}
