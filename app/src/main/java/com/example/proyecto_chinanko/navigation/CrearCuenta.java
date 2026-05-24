package com.example.proyecto_chinanko.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_chinanko.R;
import com.google.android.material.textfield.TextInputEditText;

import com.example.proyecto_chinanko.network.RetrofitClient;
import com.example.proyecto_chinanko.network.ChinankoApiService;
import com.example.proyecto_chinanko.dto.UserRequest;
import com.example.proyecto_chinanko.dto.UserResponse;

// IMPORTACIONES DE FIREBASE
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearCuenta extends Fragment {

    private TextInputEditText etUsername;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnRegistrar;

    private FirebaseAuth mAuth;

    private static final String TAG = "API_REGISTRO";

    public CrearCuenta() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crear_cuenta, container, false);

        mAuth = FirebaseAuth.getInstance();

        etUsername = view.findViewById(R.id.usernameEditText);
        etEmail = view.findViewById(R.id.emailEditText);
        etPassword = view.findViewById(R.id.passwordEditText);
        btnRegistrar = view.findViewById(R.id.btnCrearCuenta);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrarUsuario();
            }
        });

        return view;
    }

    private void RegistrarUsuario(){
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(requireContext(), "Todos los campos deben de ser llenados", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrar.setEnabled(false);
        Toast.makeText(requireContext(), "Creando cuenta...", Toast.LENGTH_SHORT).show();

        // 1. PRIMERO CREAMOS EL USUARIO EN FIREBASE
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // 2. ENVIAR CORREO DE VERIFICACIÓN
                            user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                if (emailTask.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Verifica tu bandeja de entrada para confirmar el correo", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        // 3. GUARDAMOS EN TU BACKEND (API RETROFIT)
                        guardarUsuarioEnBackend(username, email, password);

                    } else {
                        btnRegistrar.setEnabled(true);
                        String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(requireContext(), "Error Firebase: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarUsuarioEnBackend(String username, String email, String password) {

        // 🟢 LIMPIEZA DE SEGURIDAD: Borramos cualquier token viejo antes de registrar
        SharedPreferences prefs = requireContext().getSharedPreferences("ChinankoPrefs", Context.MODE_PRIVATE);
        prefs.edit().remove("JWT_TOKEN").apply();

        UserRequest request = new UserRequest(username, password, email);
        ChinankoApiService service = RetrofitClient.getApiService(requireContext());

        service.createUser(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                btnRegistrar.setEnabled(true);

                if(response.isSuccessful() && response.body() != null){
                    UserResponse usuarioCreado = response.body();
                    Log.d(TAG, "Usuario creado en backend con ID: " + usuarioCreado.getId());

                    Toast.makeText(requireContext(), "Cuenta creada exitosamente en servidor", Toast.LENGTH_SHORT).show();

                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    // 🟢 MEGA-DIAGNÓSTICO: Esto te dirá exactamente por qué Spring Boot te rechaza
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin cuerpo de error";
                        Log.e(TAG, "Error Spring Boot - Código: " + response.code() + " | Detalle: " + errorBody);
                        Toast.makeText(requireContext(), "Error del Servidor (Cód " + response.code() + "). Revisa el Logcat.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                btnRegistrar.setEnabled(true);
                Log.e(TAG, "Fallo de conexión HTTP: " + t.getMessage());
                Toast.makeText(requireContext(), "Sin conexión al servidor local", Toast.LENGTH_LONG).show();
            }
        });
    }
}