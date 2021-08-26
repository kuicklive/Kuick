package com.kuick.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuick.R;
import com.kuick.Response.CartDetails;
import com.kuick.Response.UserAddressResponse;
import com.kuick.activity.AddressInformation;
import com.kuick.activity.AddressList;
import com.kuick.activity.CartPageActivity;
import com.kuick.databinding.AddressInformationBinding;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;

import java.util.List;

public class AddressInformationAdapter extends RecyclerView.Adapter<AddressInformationAdapter.ViewHolder> {

    private List<UserAddressResponse> data;
    private final boolean isFromCart;
    private AddressList mContext;

    public AddressInformationAdapter(List<UserAddressResponse> data, boolean isFromCart, AddressList mContext) {
        this.data = data;
        this.isFromCart = isFromCart;
        this.mContext = mContext;
    }
    public List<UserAddressResponse> getAddressList() {
        return data;
    }

    @NonNull
    @Override
    public AddressInformationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AddressInformationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressInformationAdapter.ViewHolder holder, int position) {
        onBindData(holder, position);
    }

    public void DeleteAddress(List<UserAddressResponse> data){
        this.data = data;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(UserAddressResponse item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<UserAddressResponse> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AddressInformationBinding binding;

        public ViewHolder(@NonNull AddressInformationBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
    private void onBindData(ViewHolder holder, int position) {

        UserAddressResponse userAddress = data.get(position);
        if (userAddress.getType().equals(Constants.OFFICE)) {
            holder.binding.addressTypeIcon.setImageResource(R.drawable.office);
        }

        if (userAddress.getType().toLowerCase().equalsIgnoreCase("home")){
            holder.binding.addressTypeTitle.setText(mContext.language.getLanguage(KEY.home));
        }else {
            holder.binding.addressTypeTitle.setText(mContext.language.getLanguage(KEY.office));
        }


        holder.binding.fullAddress.setText(userAddress.getAddress());
        holder.itemView.setOnClickListener(v -> {
            if (isFromCart) {
                Constants.selectedAddress = data.get(position);
                mContext.onBackPressed();
            }
        });
    }
}
