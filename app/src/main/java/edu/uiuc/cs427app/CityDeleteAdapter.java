package edu.uiuc.cs427app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class CityDeleteAdapter extends RecyclerView.Adapter<CityDeleteAdapter.ViewHolder> {
    private List<String> cityList;
    private OnDeleteClickListener listener;

    // Interface when a delete button is clicked
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    // Constructor for CityDeleteAdapter
    public CityDeleteAdapter(List<String> cityList, OnDeleteClickListener listener) {
        this.cityList = cityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    // Called when RecyclerView needs a new ViewHolder to represent an item
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_delete, parent, false);
        return new ViewHolder(view, listener);
    }

    // Called to bind the data with a ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cityName = cityList.get(position);
        holder.textViewCityName.setText(cityName);
    }

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return cityList.size();
    }

    // ViewHolder class to hold the layout for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCityName;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView, final OnDeleteClickListener listener) {
            super(itemView);
            textViewCityName = itemView.findViewById(R.id.delCityNameTextView);
            buttonDelete = itemView.findViewById(R.id.cityDeleteButton);

            // Set an OnClickListener for the delete button
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}

