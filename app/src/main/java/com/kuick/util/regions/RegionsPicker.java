package com.kuick.util.regions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.DialogFragment;
import com.kuick.R;
import com.kuick.base.BaseActivity;
import com.kuick.util.countrypicker.CountryAdapter;
import com.kuick.util.countrypicker.CountryPickerListener;
import java.util.ArrayList;
import java.util.List;


public class RegionsPicker extends DialogFragment {

    private final String TAG = "CountryPicker";
    private  ArrayList<String> countryList;
    private  ArrayList<String> allCountryList = new ArrayList<>();
    private ListView countryListView;
    private CountryAdapter adapter;
    private ArrayList<String> countriesList = new ArrayList();
    private ArrayList<String> selectedCountriesList = new ArrayList();
    private CountryPickerListener listener;

    public RegionsPicker(ArrayList<String> countryList) {
        this.countryList = countryList;
        this.allCountryList.addAll(countryList);
        this.setCountriesList(BaseActivity.getAllRegions());
    }

    public static RegionsPicker newInstance(String dialogTitle, ArrayList<String> countryList) {
        RegionsPicker picker = new RegionsPicker(countryList);
        Bundle bundle = new Bundle();
        bundle.putString("dialogTitle", dialogTitle);
        picker.setArguments(bundle);
        return picker;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker, (ViewGroup) null);
        Bundle args = this.getArguments();
        if (args != null) {
            String dialogTitle = args.getString("dialogTitle");
            this.getDialog().setTitle(dialogTitle);
            int width = this.getResources().getDimensionPixelSize(R.dimen.cp_dialog_width);
            int height = this.getResources().getDimensionPixelSize(R.dimen.cp_dialog_height);
            this.getDialog().getWindow().setLayout(width, height);
        }
        this.countryListView = view.findViewById(R.id.country_code_picker_listview);
        this.selectedCountriesList.addAll(this.countriesList);

        allCountryList = new ArrayList<>();
        allCountryList.addAll(countryList);
        this.adapter = new CountryAdapter(this.getActivity(), this.countryList);
        this.countryListView.setAdapter(this.adapter);

        this.countryListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (RegionsPicker.this.listener != null) {
                String region = RegionsPicker.this.countryList.get(position);
                RegionsPicker.this.listener.onSelectCountry(region);

            }

        });

        return view;
    }

    public void setListener(CountryPickerListener listener) {
        this.listener = listener;
    }

    public void setCountriesList(List<String> newCountries) {
        this.countriesList.clear();
        this.countriesList.addAll(newCountries);
    }
}
