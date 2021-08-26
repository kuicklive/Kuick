package com.kuick.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.kuick.R;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.Utility;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> sizeList;
    private final ArrayList<String> colorList;
    private  Context ctx;
    private String TAG = "SpinnerAdapter";

    public SpinnerAdapter(Context context, ArrayList<String> sizeList, ArrayList<String> colorList) {
        super(context,  R.layout.spinner_value_layout, R.id.txtsize);
        this.ctx = context;
        this.sizeList = sizeList;
        this.colorList = colorList;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.spinner_value_layout,null);
        }

        TextView textView = convertView.findViewById(R.id.txtsize);
        TextView txtColor = convertView.findViewById(R.id.txtColor);
        TextView colorView = convertView.findViewById(R.id.colorView);
        CardView cardView = convertView.findViewById(R.id.cardView);

        if (sizeList!=null && !sizeList.get(position).equals("")){
            textView.setVisibility(View.VISIBLE);
            if (sizeList.get(position).equals(Constants.No_VARIANT)){

                textView.setText(Constants.No_VARIANT);
                txtColor.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);

            }else {
                textView.setText("Size : " + sizeList.get(position));
            }

        }
        if (colorList!=null && !colorList.get(position).equals("")){

            txtColor.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);

            try {
                Utility.PrintLog(TAG,"color format : "+ colorList.get(position));
                colorView.setBackgroundColor(Color.parseColor(colorList.get(position)));
            }catch (Exception e){
                Utility.PrintLog(TAG,"exception : " + e.toString());
            }

        }else {
            txtColor.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Utility.PrintLog(TAG,"getView() : " + position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_value_layout, null);

        }

        TextView textView =  convertView.findViewById(R.id.txtsize);
        TextView txtColor =  convertView.findViewById(R.id.txtColor);
        TextView colorView = convertView.findViewById(R.id.colorView);
        CardView cardView =  convertView.findViewById(R.id.cardView);

        if (sizeList!=null && !sizeList.get(position).equals("")){
            textView.setVisibility(View.VISIBLE);
            if (sizeList.get(position).equals(Constants.No_VARIANT)){
                textView.setText(Constants.No_VARIANT);
            }else {
                textView.setText("Size : " + sizeList.get(position));
            }

        }

        if (colorList!=null && !colorList.get(position).equals("")){
            txtColor.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);

            try {
                colorView.setBackgroundColor(Color.parseColor(colorList.get(position)));
            }catch (Exception e){
                Utility.PrintLog(TAG,"exception : " + e.toString());
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return (colorList == null ? 0 : colorList.size());

    }
}
