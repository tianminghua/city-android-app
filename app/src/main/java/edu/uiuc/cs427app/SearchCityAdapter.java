package edu.uiuc.cs427app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SearchCityAdapter extends RecyclerView.Adapter<SearchCityAdapter.CityViewHolder> {

    private final List<CityWeather> cityList;
    private final OnCityClickListener listener;

    public SearchCityAdapter(List<CityWeather> cityList, OnCityClickListener listener) {
        this.cityList = cityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        CityWeather city = cityList.get(position);
        holder.bind(city, listener);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        public void bind(CityWeather city, OnCityClickListener listener) {
            textView.setText(city.getFullName());
            itemView.setOnClickListener(v -> listener.onCityClick(city));
        }
    }

    public interface OnCityClickListener {
        void onCityClick(CityWeather city);
    }
}

