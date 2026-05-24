package com.example.proyecto_chinanko.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.proyecto_chinanko.R;
import com.example.proyecto_chinanko.dto.InterestPointImageResponse;
import com.example.proyecto_chinanko.dto.InterestPointResponse;
import com.example.proyecto_chinanko.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.ViewHolder> {

    private List<InterestPointResponse> points;
    private OnPointClickListener listener;

    public interface OnPointClickListener {
        void onPointClick(InterestPointResponse point);
    }

    public PointListAdapter(List<InterestPointResponse> points, OnPointClickListener listener) {
        this.points = points;
        this.listener = listener;
    }

    public void updateData(List<InterestPointResponse> newPoints) {
        this.points = newPoints;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reutilizamos tu diseño de item_viaje que ya tiene una imagen a la izquierda y textos a la derecha
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viaje, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InterestPointResponse point = points.get(position);

        holder.tvTitulo.setText(point.getName());

        // Formateamos el subtítulo: "Gastronomía • ⭐ 4.5"
        String ratingStr = point.getAverageRating() != null ? String.valueOf(point.getAverageRating()) : "0.0";
        holder.tvFecha.setText(point.getCategory() + " • ⭐ " + ratingStr);

        holder.itemView.setOnClickListener(v -> listener.onPointClick(point));

        // Descargar la imagen y guardarla en caché local
        RetrofitClient.getApiService(holder.itemView.getContext()).getInterestPointImages(point.getId()).enqueue(new Callback<List<InterestPointImageResponse>>() {
            @Override
            public void onResponse(Call<List<InterestPointImageResponse>> call, Response<List<InterestPointImageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Glide.with(holder.itemView.getContext())
                            .load(response.body().get(0).getImageUrl())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.color.chinanko_purple_dark)
                            .into(holder.ivPunto);
                } else {
                    holder.ivPunto.setImageResource(R.color.chinanko_purple_dark);
                }
            }
            @Override
            public void onFailure(Call<List<InterestPointImageResponse>> call, Throwable t) {
                holder.ivPunto.setImageResource(R.color.chinanko_purple_dark);
            }
        });
    }

    @Override
    public int getItemCount() { return points.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPunto;
        TextView tvTitulo, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPunto = itemView.findViewById(R.id.ivViajePunto);
            tvTitulo = itemView.findViewById(R.id.tvViajeTitulo);
            tvFecha = itemView.findViewById(R.id.tvViajeFecha);
        }
    }
}