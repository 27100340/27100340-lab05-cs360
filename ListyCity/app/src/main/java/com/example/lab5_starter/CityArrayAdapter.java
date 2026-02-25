package com.example.lab5_starter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CityArrayAdapter extends ArrayAdapter<City> {

    public interface DeleteCityListener {
        void deleteCity(City city);
    }

    private ArrayList<City> cities;
    private Context context;
    private DeleteCityListener deleteListener;

    public CityArrayAdapter(Context context, ArrayList<City> cities, DeleteCityListener listener) {
        super(context, 0, cities);
        this.cities = cities;
        this.context = context;
        this.deleteListener = listener;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_city, parent, false);
        }

        City city = cities.get(position);
        TextView cityName = view.findViewById(R.id.textCityName);
        TextView cityProvince = view.findViewById(R.id.textCityProvince);
        Button deleteButton = view.findViewById(R.id.buttonDeleteCity);

        cityName.setText(city.getName());
        cityProvince.setText(city.getProvince());

        deleteButton.setOnClickListener(v -> deleteListener.deleteCity(city));

        return view;
    }
}