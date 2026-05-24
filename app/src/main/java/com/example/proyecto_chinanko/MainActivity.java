package com.example.proyecto_chinanko;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_chinanko.navigation.CrearCuenta;
import com.google.android.material.textfield.TextInputEditText;

import com.example.proyecto_chinanko.network.RetrofitClient;
import com.example.proyecto_chinanko.network.ChinankoApiService;
import com.example.proyecto_chinanko.dto.LoginRequest;
import com.example.proyecto_chinanko.dto.AuthResponse;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnCrearCuenta;
    private Button btnIniciarSesion;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    // 🟢 INSTANCIA DE FIREBASE
    private FirebaseAuth mAuth;

    private static final String TAG = "API_LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 🟢 INICIALIZAR FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        // 1. Vincular las vistas del XML con Java
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // 2. Acción para ir a Crear Cuenta
        btnCrearCuenta.setOnClickListener(v -> {
            CrearCuenta crearCuentaFragment = new CrearCuenta();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, crearCuentaFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnIniciarSesion.setOnClickListener(v -> realizarLoginFirebase());
    }

    // 🟢 COMPROBAR SI YA HAY SESIÓN INICIADA (AUTO-LOGIN)
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        android.content.SharedPreferences prefs = getSharedPreferences("ChinankoPrefs", MODE_PRIVATE);
        String savedToken = prefs.getString("JWT_TOKEN", null);

        // Si Firebase tiene sesión activa, el correo está verificado y tenemos el Token local
        if (currentUser != null && currentUser.isEmailVerified() && savedToken != null) {
            Intent intent = new Intent(MainActivity.this, Mapa.class);
            startActivity(intent);
            finish();
        }
    }

    private void realizarLoginFirebase() {
        String emailIngresado = emailEditText.getText().toString().trim();
        String passwordIngresada = passwordEditText.getText().toString().trim();

        if (emailIngresado.isEmpty() || passwordIngresada.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnIniciarSesion.setEnabled(false); // Evitar doble clic
        Toast.makeText(this, "Verificando credenciales...", Toast.LENGTH_SHORT).show();

        // Conectar con Firebase
        mAuth.signInWithEmailAndPassword(emailIngresado, passwordIngresada)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Verificar si el usuario ya confirmó su correo
                        if (user != null && user.isEmailVerified()) {
                            loginBackendRetrofit(emailIngresado, passwordIngresada);
                        } else {
                            Toast.makeText(MainActivity.this, "Por favor, verifica tu correo electrónico antes de iniciar sesión. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            btnIniciarSesion.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Correo o contraseña incorrectos.", Toast.LENGTH_LONG).show();
                        btnIniciarSesion.setEnabled(true);
                    }
                });
    }

    // 🟢 PASO 2: LOGIN EN TU BACKEND (RETROFIT) PARA OBTENER EL TOKEN JWT
    private void loginBackendRetrofit(String email, String password) {
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("ChinankoPrefs", MODE_PRIVATE);
        sharedPreferences.edit().remove("JWT_TOKEN").apply(); // Limpiar token viejo

        LoginRequest request = new LoginRequest(email, password);
        ChinankoApiService apiService = RetrofitClient.getApiService(this);

        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnIniciarSesion.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String tokenRecibido = response.body().getToken();
                    Log.d(TAG, "Éxito, Token: " + tokenRecibido);

                    // Guardar el token NUEVO
                    sharedPreferences.edit().putString("JWT_TOKEN", tokenRecibido).apply();

                    Toast.makeText(MainActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Mapa.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.e(TAG, "Error en login backend: Código " + response.code());
                    Toast.makeText(MainActivity.this, "Error de sincronización con el servidor local", Toast.LENGTH_LONG).show();
                    mAuth.signOut(); // Si falla el backend, cerramos la de Firebase por seguridad
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnIniciarSesion.setEnabled(true);
                Log.e(TAG, "Fallo de conexión: " + t.getMessage());
                Toast.makeText(MainActivity.this, "No se pudo conectar al servidor. Revisa tu conexión.", Toast.LENGTH_LONG).show();
                mAuth.signOut();
            }
        });
    }
}












