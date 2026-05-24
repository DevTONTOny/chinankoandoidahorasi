package com.example.proyecto_chinanko.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import com.example.proyecto_chinanko.R;

public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ViewHolder> {
    private List<String> imageUrls;
    private OnImageClickListener listener;

    // 🟢 AHORA ENVIAMOS LA POSICIÓN Y LA LISTA COMPLETA
    public interface OnImageClickListener {
        void onImageClick(int position, List<String> allUrls);
    }

    public ImageCarouselAdapter(List<String> imageUrls, OnImageClickListener listener) {
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_carrousel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(imageUrls.get(position))
                .centerCrop()
                .placeholder(R.drawable.ic_map)
                .into(holder.imageView);

        // 🟢 PASAMOS LOS DATOS AL ESCUCHADOR
        holder.imageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(holder.getAdapterPosition(), imageUrls);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivCarouselItem);
        }
    }
}