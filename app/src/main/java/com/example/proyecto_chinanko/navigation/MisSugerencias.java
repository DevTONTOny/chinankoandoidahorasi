package com.example.proyecto_chinanko.navigation; // Asegúrate de que el paquete es correcto

import android.os.Bundle;
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

import java.util.List;

import com.example.proyecto_chinanko.R;
import com.example.proyecto_chinanko.adapter.SugerenciasAdapter;
import com.example.proyecto_chinanko.dto.SuggestedPointResponse;
import com.example.proyecto_chinanko.network.RetrofitClient;
// import com.example.proyecto_chinanko.dto.SuggestedPointResponse; // Verifica tu import
// import com.example.proyecto_chinanko.adapter.SugerenciasAdapter; // Verifica tu import

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisSugerencias extends Fragment {

    private RecyclerView rvSugerencias;

    public MisSugerencias() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_sugerencias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSugerencias = view.findViewById(R.id.rvMisSugerencias);
        rvSugerencias.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Botón de regresar: Cierra este fragmento sacándolo de la pila
        ImageView btnRegresar = view.findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        obtenerMisSugerencias();
    }

    private void obtenerMisSugerencias() {
        RetrofitClient.getApiService(requireContext()).getMySuggestions().enqueue(new Callback<List<SuggestedPointResponse>>() {
            @Override
            public void onResponse(Call<List<SuggestedPointResponse>> call, Response<List<SuggestedPointResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Aún no has hecho ninguna sugerencia", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        SugerenciasAdapter adapter = new SugerenciasAdapter(response.body());
                        rvSugerencias.setAdapter(adapter);
                    }
                } else {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error al cargar sugerencias", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SuggestedPointResponse>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}