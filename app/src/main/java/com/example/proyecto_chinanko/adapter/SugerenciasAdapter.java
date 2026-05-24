package com.example.proyecto_chinanko.adapter;

import android.graphics.Color;
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
import com.example.proyecto_chinanko.dto.SuggestedPointImageResponse;
import com.example.proyecto_chinanko.dto.SuggestedPointResponse;
import com.example.proyecto_chinanko.network.RetrofitClient;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SugerenciasAdapter extends RecyclerView.Adapter<SugerenciasAdapter.SugerenciaViewHolder> {

    private List<SuggestedPointResponse> sugerencias;

    public SugerenciasAdapter(List<SuggestedPointResponse> sugerencias) {
        this.sugerencias = sugerencias;
    }

    @NonNull
    @Override
    public SugerenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sugerencia, parent, false);
        return new SugerenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SugerenciaViewHolder holder, int position) {
        SuggestedPointResponse sugerencia = sugerencias.get(position);

        holder.tvNombre.setText(sugerencia.getName());
        holder.tvCategoria.setText(sugerencia.getCategory() != null ? sugerencia.getCategory() : "Sugerencia de lugar");
        holder.tvDescripcion.setText(sugerencia.getDescription());

        // 🟢 SOLUCIÓN AL BUG DE RECICLAJE:
        // Siempre hacemos visible la vista y le ponemos el color de carga (placeholder)
        // ANTES de llamar a Retrofit. Así garantizamos un lienzo limpio para la nueva tarjeta.
        holder.ivImagen.setVisibility(View.VISIBLE);
        holder.ivImagen.setImageResource(R.color.chinanko_text_hint);

        // 🟢 Cargar imagen del punto sugerido
        cargarImagenSugerencia(sugerencia.getId(), holder.ivImagen);

        // Lógica de estados...
        String estado = sugerencia.getStatus();
        if (estado != null) {
            switch (estado) {
                case "PENDING":
                    holder.tvEstado.setText("PENDIENTE");
                    holder.cardEstado.setCardBackgroundColor(Color.parseColor("#FF9800"));
                    break;
                case "APPROVED":
                    holder.tvEstado.setText("APROBADO");
                    holder.cardEstado.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                    break;
                case "REJECTED":
                    holder.tvEstado.setText("RECHAZADO");
                    holder.cardEstado.setCardBackgroundColor(Color.parseColor("#F44336"));
                    break;
                default:
                    holder.tvEstado.setText(estado);
                    holder.cardEstado.setCardBackgroundColor(Color.parseColor("#757575"));
                    break;
            }
        }
    }

    private void cargarImagenSugerencia(Long suggestedPointId, ImageView imageView) {
        RetrofitClient.getApiService(imageView.getContext())
                .getImagesBySuggestedPoint(suggestedPointId)
                .enqueue(new Callback<List<SuggestedPointImageResponse>>() {
                    @Override
                    public void onResponse(Call<List<SuggestedPointImageResponse>> call, Response<List<SuggestedPointImageResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                            // 🟢 ASEGURAMOS QUE SEA VISIBLE SI LLEGÓ UNA FOTO
                            imageView.setVisibility(View.VISIBLE);

                            Glide.with(imageView.getContext())
                                    .load(response.body().get(0).getImageUrl())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Guarda en memoria local
                                    .centerCrop()
                                    .into(imageView);
                        } else {
                            // Si la lista de fotos viene vacía, la ocultamos para que no estorbe el espacio
                            imageView.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<SuggestedPointImageResponse>> call, Throwable t) {
                        // Si falla el internet, la ocultamos
                        imageView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return sugerencias.size();
    }

    static class SugerenciaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvDescripcion, tvEstado;
        MaterialCardView cardEstado;
        ImageView ivImagen;

        public SugerenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvSugerenciaNombre);
            tvCategoria = itemView.findViewById(R.id.tvSugerenciaCategoria);
            tvDescripcion = itemView.findViewById(R.id.tvSugerenciaDescripcion);
            tvEstado = itemView.findViewById(R.id.tvSugerenciaEstado);
            cardEstado = itemView.findViewById(R.id.cardEstado);
            ivImagen = itemView.findViewById(R.id.ivSugerenciaImagen);
        }
    }
}