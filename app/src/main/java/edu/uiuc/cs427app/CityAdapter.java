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

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private List<String> cityList;

    public CityAdapter(List<String> cityList) {
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_item, parent, false);
        return new CityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        String cityName = cityList.get(position);
        holder.cityNameTextView.setText(cityName);
        //Log.d("CityAdapter", "City at position " + position + ": " + cityName);
    }

    @Override
    public int getItemCount() {
        //Log.d("CityAdapter", "Item count: " + cityList.size());
        return cityList.size();
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityNameTextView;
        Button showDetailsButton;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.cityNameTextView);
            showDetailsButton = itemView.findViewById(R.id.showDetails);
        }
    }
}

