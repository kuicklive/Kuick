package com.kuick.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.activity.TopCategoriesLivesActivity;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.TopCategoriesItemBinding;
import com.kuick.model.Categories;

import java.util.List;

import static com.kuick.util.comman.Constants.INTENT_KEY;
import static com.kuick.util.comman.Constants.INTENT_KEY_PRODUCT_ID;
import static com.kuick.util.comman.Constants.INTENT_KEY_USER_NAME;
import static com.kuick.util.comman.Constants.SIX;

public class TopCategoriesAdapter extends RecyclerView.Adapter<TopCategoriesAdapter.ViewHolder> {

    private final boolean isFullscreen;
    private final List<Categories> categoriesList;

    private Context mContext;

    public TopCategoriesAdapter(List<Categories> categories, boolean isFullScreen) {
        this.isFullscreen = isFullScreen;
        this.categoriesList = categories;
    }

    @NonNull
    @Override
    public TopCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(TopCategoriesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopCategoriesAdapter.ViewHolder holder, int position) {
                onBindData(holder,position);
    }

    private void onBindData(ViewHolder holder, int position) {

        Categories categories = categoriesList.get(position);

        String categoriesName = categories.getName();
        String imgUrl = categories.getImage();
        holder.binding.categorieName.setText(categoriesName);
        BaseActivity.showGlideImage(mContext,imgUrl,holder.binding.categorieImg);
        holder.itemView.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, TopCategoriesLivesActivity.class)
                .putExtra(INTENT_KEY_USER_NAME,categories.getName())
                .putExtra(INTENT_KEY_PRODUCT_ID,categories.getId())));


    }

    @Override
    public int getItemCount() {
        if (isFullscreen)
        return categoriesList.size();
        else
            if (categoriesList!=null && categoriesList.size() > 0){
                return SIX;
            }
            return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private final TopCategoriesItemBinding binding;

        public ViewHolder(@NonNull TopCategoriesItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }
    }
}
