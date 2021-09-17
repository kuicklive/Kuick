package com.kuick.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.databinding.VariantColorSlectionBinding;
import com.kuick.util.comman.Utility;

import java.util.ArrayList;

import static com.kuick.activity.ProductDetailsActivity.colorSelectionListener;

public class VariantColorSelectionAdapter extends RecyclerView.Adapter<VariantColorSelectionAdapter.ViewHolder> {
    private final ArrayList<String> colorList;
    private final ArrayList<String> productId;
    String TAG = "VariantColorSelectionAdapter";
    private Context mContext;
    private int pos = 0;
    private boolean isisFirstTime = true;

    public VariantColorSelectionAdapter(ArrayList<String> RGBColorList, ArrayList<String> productId) {
        this.colorList = RGBColorList;
        this.productId = productId;
    }


    @NonNull
    @Override
    public VariantColorSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(VariantColorSlectionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VariantColorSelectionAdapter.ViewHolder holder, int position) {
                onBindData(holder,position);
    }

    private void onBindData(ViewHolder holder, int position) {

            holder.binding.colorView.setBackgroundColor(getRgbColor(colorList.get(position)));

            if (pos!=position)
                holder.binding.layBlackRing.setBackgroundColor(mContext.getResources().getColor(R.color.common_bg));

            if (position == 0 && isisFirstTime){
                holder.binding.layBlackRing.setBackgroundColor(Color.BLACK);
            }

        holder.itemView.setOnClickListener(v -> {
            isisFirstTime = false;
            pos = position;
            holder.binding.layBlackRing.setBackgroundColor(Color.BLACK);
            notifyDataSetChanged();
            colorSelectionListener.onClickColor(productId.get(position),position);
        });
    }

    private int getRgbColor(String rgb) {
        try {
            String[] strings = rgb.split(", ");
            int R = Integer.parseInt(strings[0]);
            int G = Integer.parseInt(strings[1]);
            int B = Integer.parseInt(strings[2]);
            int A = Integer.parseInt(strings[3]);
            Utility.PrintLog(TAG,"color 0 : "+strings[0] + " color 1 :" +strings[1] + " color 2 :" + strings[2]);
            return Color.rgb(R, G, B);

        }catch (Exception e){
            Utility.PrintLog(TAG,"RGB excpetion() "+ e.toString());
        }
            return 0;
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public VariantColorSlectionBinding binding;
        public ViewHolder(@NonNull VariantColorSlectionBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
