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
import com.example.proyecto_chinanko.dto.TravelResponse;
import com.example.proyecto_chinanko.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder> {

    private List<TravelResponse> travels;
    private OnTravelClickListener listener;

    public interface OnTravelClickListener {
        void onTravelClick(long pointId);
        void onImageClick(String imageUrl);
    }

    public TravelAdapter(List<TravelResponse> travels, OnTravelClickListener listener) {
        this.travels = travels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viaje, parent, false);
        return new TravelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        TravelResponse travel = travels.get(position);

        String titulo = travel.getInterestPointName() != null ? travel.getInterestPointName() : "Viaje guardado";
        holder.tvTitulo.setText(titulo);

        String fecha = travel.getTravelDate();
        if (fecha != null && fecha.contains("T")) {
            holder.tvFecha.setText("Realizada el " + fecha.split("T")[0]);
        } else {
            holder.tvFecha.setText("Realizada el " + (fecha != null ? fecha : "Fecha desconocida"));
        }

        // 🟢 NUEVO: Lógica visual de Estado
        String estado = travel.getStatus();
        if ("COMPLETED".equalsIgnoreCase(estado)) {
            holder.tvEstado.setText("COMPLETADO");
            holder.cardEstado.setCardBackgroundColor(android.graphics.Color.parseColor("#4CAF50")); // Verde
        } else if ("CANCELED".equalsIgnoreCase(estado)) {
            holder.tvEstado.setText("CANCELADO");
            holder.cardEstado.setCardBackgroundColor(android.graphics.Color.parseColor("#F44336")); // Rojo
        } else {
            holder.tvEstado.setText("DESCONOCIDO");
            holder.cardEstado.setCardBackgroundColor(android.graphics.Color.parseColor("#757575")); // Gris
        }

        holder.itemView.setOnClickListener(v -> {
            if(listener != null) listener.onTravelClick(travel.getInterestPointId());
        });

        holder.ivPunto.setImageResource(R.color.chinanko_purple_dark);
        cargarImagenParaElPunto(travel.getInterestPointId(), holder.ivPunto, travel);
    }

    private void cargarImagenParaElPunto(Long pointId, ImageView imageView, TravelResponse travel) {
        if (travel.getImageUrl() != null && !travel.getImageUrl().isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(travel.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.color.chinanko_purple_dark)
                    .into(imageView);

            imageView.setOnClickListener(v -> {
                if(listener != null) listener.onImageClick(travel.getImageUrl());
            });
            return;
        }

        RetrofitClient.getApiService(imageView.getContext()).getInterestPointImages(pointId).enqueue(new Callback<List<InterestPointImageResponse>>() {
            @Override
            public void onResponse(Call<List<InterestPointImageResponse>> call, Response<List<InterestPointImageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String url = response.body().get(0).getImageUrl();
                    travel.setImageUrl(url);

                    Glide.with(imageView.getContext())
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.color.chinanko_purple_dark)
                            .into(imageView);

                    imageView.setOnClickListener(v -> {
                        if(listener != null) listener.onImageClick(url);
                    });
                } else {
                    imageView.setImageResource(R.color.chinanko_purple_dark);
                }
            }

            @Override
            public void onFailure(Call<List<InterestPointImageResponse>> call, Throwable t) {
                imageView.setImageResource(R.color.chinanko_purple_dark);
            }
        });
    }

    @Override
    public int getItemCount() { return travels.size(); }

    static class TravelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPunto;
        TextView tvTitulo, tvFecha, tvEstado; // 🟢 NUEVO
        com.google.android.material.card.MaterialCardView cardEstado; // 🟢 NUEVO

        public TravelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPunto = itemView.findViewById(R.id.ivViajePunto);
            tvTitulo = itemView.findViewById(R.id.tvViajeTitulo);
            tvFecha = itemView.findViewById(R.id.tvViajeFecha);
            tvEstado = itemView.findViewById(R.id.tvEstadoViaje); // 🟢 NUEVO
            cardEstado = itemView.findViewById(R.id.cardEstadoViaje); // 🟢 NUEVO
        }
    }
}