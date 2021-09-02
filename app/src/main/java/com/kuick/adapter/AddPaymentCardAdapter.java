package com.kuick.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.data.DataHolder;
import com.kuick.R;
import com.kuick.activity.PaymentCardList;
import com.kuick.base.BaseActivity;
import com.kuick.databinding.AddPaymentCardBinding;
import com.kuick.model.UserCard;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.KEY;


import java.util.ArrayList;
import java.util.List;

import static com.kuick.activity.PaymentCardList.paymentInformationListener;
import static com.kuick.util.comman.Constants.selectedCardPosition;
import static com.kuick.util.comman.Constants.selectedPaymentCard;

public class AddPaymentCardAdapter extends RecyclerView.Adapter<AddPaymentCardAdapter.ViewHolder> {

    private final boolean isFromCart;
    private List<UserCard> cardList;
    private PaymentCardList mContext;
    String TAG = "AddPaymentCardAdapter";

    private int selectedPosition = -1;
    public static CheckBox checkBox;

    public AddPaymentCardAdapter(List<UserCard> cardList, PaymentCardList paymentCardList, boolean isFromCart){
        this.cardList = cardList;
        this.isFromCart = isFromCart;
        this.mContext = paymentCardList;
    }

    @NonNull
    @Override
    public AddPaymentCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ViewHolder(AddPaymentCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddPaymentCardAdapter.ViewHolder holder, int position) {
                UserCard card = cardList.get(position);
                onBindData(holder,position,card);



        holder.itemView.setOnClickListener(v -> {
            if (isFromCart){
                viewSelected(holder,card,position);
                mContext.onBackPressed();
            }else {
                viewSelected(holder,card,position);
            }



        });

        holder.itemBinding.cbCheckbox.setOnClickListener(view -> {
            if (isFromCart){
                viewSelected(holder,card,position);
                mContext.onBackPressed();
            }else {
                viewSelected(holder,card,position);
            }

        });

        if (selectedPosition==position){
            checkBox = holder.itemBinding.cbCheckbox;
            holder.itemBinding.cbCheckbox.setChecked(true);

        } else {
            holder.itemBinding.cbCheckbox.setChecked(false);
            holder.itemBinding.mLinearView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        if (selectedPaymentCard!=null && selectedPaymentCard.getId().equals(card.getId()) && selectedCardPosition!=-1 && selectedCardPosition == position){
            paymentInformationListener.onClickCardCheckBox(card,position);
            holder.itemBinding.mLinearView.setBackground(mContext.getResources().getDrawable(R.drawable.blueline));

        }
    }

    private void SelectedCard(ViewHolder holder, UserCard card, int position) {
        selectedPosition = holder.getAdapterPosition();
        paymentInformationListener.onClickCardCheckBox(card,position);
        holder.itemBinding.mLinearView.setBackground(mContext.getResources().getDrawable(R.drawable.blueline));
        mContext.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    private void onBindData(ViewHolder holder, int position, UserCard card) {


        BaseActivity.showGlideImage(mContext,card.getCard_image(),holder.itemBinding.cardIcon);
        holder.itemBinding.cardNumber.setText(card.getLast_4());
        holder.itemBinding.txtExpiry.setText(mContext.language.getLanguage(KEY.expires)+" "+ card.getExpiry_month()+"/"+card.getExpiry_year());

        if (selectedPaymentCard!=null && selectedPaymentCard.getId().equals(card.getId()) && selectedCardPosition!=-1 && selectedCardPosition == position){
            paymentInformationListener.onClickCardCheckBox(card,position);
            holder.itemBinding.mLinearView.setBackground(mContext.getResources().getDrawable(R.drawable.blueline));
        }
    }


    public void viewSelected(ViewHolder holder, UserCard card, int position) {
        selectedPosition = holder.getAdapterPosition();
        notifyDataSetChanged();
        paymentInformationListener.onClickCardCheckBox(card,position);
        holder.itemBinding.mLinearView.setBackground(mContext.getResources().getDrawable(R.drawable.blueline));
        if(isFromCart){
            mContext.onBackPressed();
        }
    }


    public void removeItem(int position) {
        cardList.remove(position);
        notifyItemRemoved(position);
    }


    public void restoreItem(UserCard item, int position) {
        cardList.add(position, item);
        notifyItemInserted(position);
    }

    public void refreshAdapter(){
        if (checkBox == null)
            return;

        selectedPosition = -1;

        checkBox.setChecked(false);
        notifyDataSetChanged();
    }

    public void AddCard(){
        notifyDataSetChanged();
    }



    public List<UserCard> getData(){
        return cardList;
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void DeleteCard(List<UserCard> userCard) {
        this.cardList = userCard;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public  AddPaymentCardBinding itemBinding;
        public ViewHolder(@NonNull AddPaymentCardBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }
}
