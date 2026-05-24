package com.example.proyecto_chinanko.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.example.proyecto_chinanko.R;
import com.example.proyecto_chinanko.adapter.NotificationAdapter;
import com.example.proyecto_chinanko.database.ChinankoDatabase;
import com.example.proyecto_chinanko.dto.NotificationResponse;
import com.example.proyecto_chinanko.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notificaciones extends Fragment {

    private RecyclerView rvNotificaciones;
    private NotificationAdapter adapter;
    private ChinankoDatabase db; // 🟢 Instancia de la base de datos

    public Notificaciones() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notificaciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotificaciones = view.findViewById(R.id.rvNotificaciones);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inicializamos la base de datos local
        db = ChinankoDatabase.getInstance(requireContext());

        cargarDatosLocalesYRemotos();
    }

    // 🟢 FUNCIÓN QUE COMBINA EL MODO OFFLINE Y ONLINE
    private void cargarDatosLocalesYRemotos() {
        // 1. Carga ultra-rápida desde la base de datos local (Modo Offline)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<NotificationResponse> notifsLocales = db.chinankoDao().getLocalUnreadNotifications();
            if (isAdded() && notifsLocales != null && !notifsLocales.isEmpty()) {
                requireActivity().runOnUiThread(() -> {
                    // Si el adaptador aún no existe, lo creamos con lo que hay en local
                    if (adapter == null) {
                        configurarAdaptador(notifsLocales);
                    }
                });
            }
        });

        // 2. Consulta a la API para traer los datos más frescos (Modo Online)
        RetrofitClient.getApiService(requireContext()).getMyNotifications().enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<NotificationResponse> noLeidas = new ArrayList<>();
                    for (NotificationResponse notif : response.body()) {
                        if (!notif.isRead()) {
                            noLeidas.add(notif);
                        }
                    }

                    // Actualizamos la UI
                    if (isAdded()) {
                        configurarAdaptador(noLeidas);
                    }

                    // 🟢 Guardamos el clon exacto en Room para la próxima vez que no haya internet
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.chinankoDao().clearNotifications();
                        db.chinankoDao().insertNotifications(noLeidas);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Modo sin conexión. Mostrando notificaciones guardadas.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configurarAdaptador(List<NotificationResponse> notificaciones) {
        adapter = new NotificationAdapter(notificaciones, (notificacion, position) -> {
            marcarComoLeida(notificacion.getId(), position);
        });
        rvNotificaciones.setAdapter(adapter);
    }

    private void marcarComoLeida(Long id, int position) {
        // Le avisamos al servidor que ya leímos esto
        RetrofitClient.getApiService(requireContext()).markAsRead(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && adapter != null) {

                    // Quitar visualmente de la lista
                    adapter.removeNotification(position);

                    // 🟢 Quitar físicamente de la base local para que no salga como "fantasma" en offline
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.chinankoDao().deleteNotificationLocal(id);
                    });
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Conéctate a internet para cerrar notificaciones", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}