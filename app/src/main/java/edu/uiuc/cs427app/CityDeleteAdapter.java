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

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public CityDeleteAdapter(List<String> cityList, OnDeleteClickListener listener) {
        this.cityList = cityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_delete, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cityName = cityList.get(position);
        holder.textViewCityName.setText(cityName);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCityName;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView, final OnDeleteClickListener listener) {
            super(itemView);
            textViewCityName = itemView.findViewById(R.id.delCityNameTextView);
            buttonDelete = itemView.findViewById(R.id.cityDeleteButton);

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

