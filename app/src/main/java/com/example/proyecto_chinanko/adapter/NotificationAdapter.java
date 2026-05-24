package com.example.proyecto_chinanko.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_chinanko.R;
import com.example.proyecto_chinanko.dto.NotificationResponse;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private List<NotificationResponse> notifications;
    private OnNotificationCloseListener listener;

    public interface OnNotificationCloseListener {
        void onCloseClick(NotificationResponse notification, int position);
    }

    public NotificationAdapter(List<NotificationResponse> notifications, OnNotificationCloseListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        NotificationResponse notif = notifications.get(position);

        holder.tvMensaje.setText(notif.getMessage());

        // Limpiar la fecha visualmente (puedes mejorar esto después con un formateador de "Hace X min")
        String fecha = notif.getCreatedAt();
        if (fecha != null && fecha.contains("T")) {
            holder.tvTiempo.setText(fecha.split("T")[0]);
        } else {
            holder.tvTiempo.setText(fecha);
        }

        holder.btnCerrar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCloseClick(notif, position);
            }
        });
    }

    @Override
    public int getItemCount() { return notifications.size(); }

    public void removeNotification(int position) {
        notifications.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notifications.size());
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvMensaje, tvTiempo;
        ImageView btnCerrar;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvNotifTitulo);
            tvMensaje = itemView.findViewById(R.id.tvNotifMensaje);
            tvTiempo = itemView.findViewById(R.id.tvNotifTiempo);
            btnCerrar = itemView.findViewById(R.id.btnCerrarNotif);
        }
    }
}