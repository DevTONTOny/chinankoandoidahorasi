package com.example.proyecto_chinanko.navigation;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.bumptech.glide.Glide;
import com.example.proyecto_chinanko.Mapa;
import com.example.proyecto_chinanko.R;
import com.example.proyecto_chinanko.database.ChinankoDatabase;
import com.example.proyecto_chinanko.network.RetrofitClient;
import com.example.proyecto_chinanko.dto.TravelResponse;
import com.example.proyecto_chinanko.adapter.TravelAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisViajes extends Fragment {

    private RecyclerView rvViajes;
    private TravelAdapter adapter;
    private ChinankoDatabase db;

    public MisViajes() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_viajes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvViajes = view.findViewById(R.id.rvViajes);
        rvViajes.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Configurar el adaptador con los listeners
        adapter = new TravelAdapter(new ArrayList<>(), crearListenerViajes());
        rvViajes.setAdapter(adapter);

        db = ChinankoDatabase.getInstance(requireContext());
        cargarDatosLocalesYRemotos();
    }

    // 🟢 FUNCIÓN PARA DETECTAR CLICS
    private TravelAdapter.OnTravelClickListener crearListenerViajes() {
        return new TravelAdapter.OnTravelClickListener() {
            @Override
            public void onTravelClick(long pointId) {
                if (!isOnline()) {
                    Toast.makeText(requireContext(), "Error al cargar viaje, revisa tu conexión a internet", Toast.LENGTH_LONG).show();
                    return;
                }
                // Si hay internet, le decimos a Mapa.java que abra este punto
                if (getActivity() instanceof Mapa) {
                    ((Mapa) getActivity()).abrirPuntoDesdeViaje(pointId);
                }
            }

            @Override
            public void onImageClick(String imageUrl) {
                mostrarImagenAmpliada(imageUrl);
            }
        };
    }

    // 🟢 VERIFICADOR DE INTERNET
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // 🟢 DIÁLOGO PARA VER/DESCARGAR LA IMAGEN
    private void mostrarImagenAmpliada(String url) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_imagen_ampliada);

        androidx.viewpager2.widget.ViewPager2 vpImagenGrande = dialog.findViewById(R.id.vpImagenGrande);
        dialog.findViewById(R.id.btnCerrarDialog).setOnClickListener(v -> dialog.dismiss());

        vpImagenGrande.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ImageView iv = new ImageView(parent.getContext());
                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return new RecyclerView.ViewHolder(iv) {};
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Glide.with(holder.itemView.getContext()).load(url).fitCenter().into((ImageView) holder.itemView);
            }
            @Override public int getItemCount() { return 1; }
        });

        dialog.findViewById(R.id.btnDescargarImagen).setOnClickListener(v -> {
            try {
                DownloadManager dm = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "chinanko_viaje_" + System.currentTimeMillis() + ".jpg");
                dm.enqueue(req);
                Toast.makeText(requireContext(), "Descargando imagen...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error de descarga", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void cargarDatosLocalesYRemotos() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<TravelResponse> viajesLocales = db.chinankoDao().getLocalTravels();
            if (isAdded() && viajesLocales != null && !viajesLocales.isEmpty()) {
                requireActivity().runOnUiThread(() -> {
                    adapter = new TravelAdapter(viajesLocales, crearListenerViajes());
                    rvViajes.setAdapter(adapter);
                });
            }
        });

        RetrofitClient.getApiService(requireContext()).getMyTravels().enqueue(new Callback<List<TravelResponse>>() {
            @Override
            public void onResponse(Call<List<TravelResponse>> call, Response<List<TravelResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TravelResponse> viajesNuevos = response.body();
                    if (isAdded()) {
                        adapter = new TravelAdapter(viajesNuevos, crearListenerViajes());
                        rvViajes.setAdapter(adapter);
                    }
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.chinankoDao().clearTravels();
                        db.chinankoDao().insertTravels(viajesNuevos);
                    });
                }
            }
            @Override
            public void onFailure(Call<List<TravelResponse>> call, Throwable t) {
                if (isAdded()) Toast.makeText(requireContext(), "Modo sin conexión. Mostrando datos guardados.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}