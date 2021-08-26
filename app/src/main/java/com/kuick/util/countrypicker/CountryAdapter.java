package com.kuick.util.countrypicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.kuick.R;
import com.kuick.util.comman.Utility;

import java.util.ArrayList;
import java.util.List;

import static com.kuick.base.BaseActivity.allCountriesList;


public class CountryAdapter extends BaseAdapter {

    private static final String TAG = "CountryAdapter";
    private Context mContext;
    ArrayList<String> countries;
    LayoutInflater inflater;

    public CountryAdapter(Context context, ArrayList<String> countries) {
        this.mContext = context;
        this.countries = countries;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        Utility.PrintLog(TAG,"countries.size() = " + countries.size());
        return this.countries.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0L;
    }

    public View getView(int position, View view, ViewGroup parent) {
        String country = this.countries.get(position);
        if(view == null) {
            view = this.inflater.inflate(R.layout.row, (ViewGroup)null);
        }

        CountryAdapter.Cell cell = CountryAdapter.Cell.from(view);
        cell.textView.setText(country);
        Utility.PrintLog(TAG,"list "+ country);

        return view;
    }

    static class Cell
    {
        public TextView textView;
        public ImageView imageView;

        Cell() {
        }

        static CountryAdapter.Cell from(View view) {
            if(view == null) {
                return null;
            } else if(view.getTag() == null) {
                CountryAdapter.Cell cell = new CountryAdapter.Cell();
                cell.textView = (TextView)view.findViewById(R.id.row_title);
                view.setTag(cell);
                return cell;
            } else {
                return (CountryAdapter.Cell)view.getTag();
            }
        }
    }
}