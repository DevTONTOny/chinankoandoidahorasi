package com.example.proyecto_chinanko;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_chinanko.adapter.CommentAdapter;
import com.example.proyecto_chinanko.adapter.ImageCarouselAdapter;
import com.example.proyecto_chinanko.adapter.PointListAdapter;
import com.example.proyecto_chinanko.adapter.SearchAdapter;
import com.example.proyecto_chinanko.database.ChinankoDatabase;
import com.example.proyecto_chinanko.dto.CommentRequest;
import com.example.proyecto_chinanko.dto.CommentResponse;
import com.example.proyecto_chinanko.dto.InterestPointImageResponse;
import com.example.proyecto_chinanko.dto.InterestPointResponse;
import com.example.proyecto_chinanko.dto.RouteRequest;
import com.example.proyecto_chinanko.dto.RouteResponse;
import com.example.proyecto_chinanko.dto.SuggestedPointImageResponse;
import com.example.proyecto_chinanko.dto.SuggestedPointRequest;
import com.example.proyecto_chinanko.dto.SuggestedPointResponse;
import com.example.proyecto_chinanko.dto.TravelRequest;
import com.example.proyecto_chinanko.dto.TravelResponse;
import com.example.proyecto_chinanko.navigation.MisSugerencias;
import com.example.proyecto_chinanko.navigation.MisViajes;
import com.example.proyecto_chinanko.navigation.Notificaciones;
import com.example.proyecto_chinanko.network.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import android.widget.Spinner;
import android.widget.ArrayAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    private Polyline currentPolyline;
    private final String ORS_API_KEY = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6ImY0MzY0YzFjYzY5MDQ1OTliZmMyYWJhNDBiYTUzMTFkIiwiaCI6Im11cm11cjY0In0=";

    private ActivityResultLauncher<Intent> photoPickerLauncher;
    private List<Uri> urisFotosSugerencia = new ArrayList<>();
    private TextView tvContadorFotosSugerencia;

    private View topBarContainer;
    private View cardDetallePunto;
    private TextView tvDetalleTitulo;
    private TextView tvDetalleDescripcion;
    private View cardNavegacion;
    private TextView tvInstructionNavigation;
    private TextView tvDistanceTimeNavigation;
    private Button btnCancelarViaje;
    private View fabAdd;
    private View fabCentrarMapa;
    private ExtendedFloatingActionButton fabVerLista;
    private View bottomNavCard;

    private RatingBar rbDetallePunto;
    private TextView tvDetalleTotalReviews;

    private ImageView ivMenu;
    private EditText etBuscador;
    private View cardResultadosBusqueda;
    private RecyclerView rvResultadosBusqueda;
    private SearchAdapter searchAdapter;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean isNavigating = false;
    private boolean isRecalculando = false;
    private String tipoViajeActual = "driving-car";

    private static final int PERMISO_UBICACION_ID = 100;

    private Animation slideUp;
    private Animation slideDown;

    private View llVerComentarios;
    private View cardComentarios;
    private ImageView btnCerrarComentarios;
    private TextView tvComentariosTituloPunto;
    private RecyclerView rvComentarios;
    private EditText etNuevoComentario;
    private ImageView btnEnviarComentario;
    private CommentAdapter commentAdapter;
    private androidx.viewpager2.widget.ViewPager2 vpImagenesPunto;

    private InterestPointResponse puntoSeleccionado = null;
    private long startTimeMillis;

    // ESTADO EXCLUSIVO DE LA TARJETA INFERIOR
    private List<InterestPointResponse> listaPuntosGlobal = new ArrayList<>();
    private String categoryFilter = "Todas";
    private boolean sortByRating = false;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private RecyclerView rvListaPuntosCompleta;
    private PointListAdapter pointListAdapter;
    private EditText etBuscadorTarjeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CONFIGURACIÓN ASÍNCRONA DEL SELECCIONADOR DE FOTOS
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                urisFotosSugerencia.add(result.getData().getClipData().getItemAt(i).getUri());
                            }
                        } else if (result.getData().getData() != null) {
                            urisFotosSugerencia.add(result.getData().getData());
                        }
                        if (tvContadorFotosSugerencia != null) {
                            tvContadorFotosSugerencia.setText(urisFotosSugerencia.size() + " foto(s) seleccionada(s)");
                            tvContadorFotosSugerencia.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                        }
                    }
                }
        );
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) { mapFragment.getMapAsync(this); }

        vpImagenesPunto = findViewById(R.id.vpImagenesPunto);
        topBarContainer = findViewById(R.id.topBarContainer);
        cardDetallePunto = findViewById(R.id.cardDetallePunto);
        tvDetalleTitulo = findViewById(R.id.tvDetalleTitulo);
        tvDetalleDescripcion = findViewById(R.id.tvDetalleDescripcion);
        cardNavegacion = findViewById(R.id.cardNavegacion);
        tvInstructionNavigation = findViewById(R.id.tvInstructionNavigation);
        tvDistanceTimeNavigation = findViewById(R.id.tvDistanceTimeNavigation);
        btnCancelarViaje = findViewById(R.id.btnCancelarViaje);
        fabAdd = findViewById(R.id.fabAdd);
        fabCentrarMapa = findViewById(R.id.fabCentrarMapa);
        fabVerLista = findViewById(R.id.fabVerLista);
        bottomNavCard = findViewById(R.id.bottomNavCardInclude);

        rbDetallePunto = findViewById(R.id.rbDetallePunto);
        tvDetalleTotalReviews = findViewById(R.id.tvDetalleTotalReviews);

        ivMenu = findViewById(R.id.ivMenu);
        etBuscador = findViewById(R.id.etBuscador);
        cardResultadosBusqueda = findViewById(R.id.cardResultadosBusqueda);
        rvResultadosBusqueda = findViewById(R.id.rvResultadosBusqueda);

        llVerComentarios = findViewById(R.id.llVerComentarios);
        cardComentarios = findViewById(R.id.cardComentarios);
        btnCerrarComentarios = findViewById(R.id.btnCerrarComentarios);
        tvComentariosTituloPunto = findViewById(R.id.tvComentariosTituloPunto);
        rvComentarios = findViewById(R.id.rvComentarios);
        etNuevoComentario = findViewById(R.id.etNuevoComentario);
        btnEnviarComentario = findViewById(R.id.btnEnviarComentario);

        rvListaPuntosCompleta = findViewById(R.id.rvListaPuntosCompleta);
        etBuscadorTarjeta = findViewById(R.id.etBuscadorTarjeta);
        View bottomSheetLista = findViewById(R.id.bottomSheetListaPuntos);

        if (bottomSheetLista != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLista);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_DRAGGING) {
                        topBarContainer.setVisibility(View.GONE);
                        fabAdd.setVisibility(View.GONE);
                        fabCentrarMapa.setVisibility(View.GONE);
                        fabVerLista.setVisibility(View.GONE);
                        bottomNavCard.setVisibility(View.GONE);
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
                        if (currentFragment == null) {
                            topBarContainer.setVisibility(View.VISIBLE);
                            if (!isNavigating) {
                                fabAdd.setVisibility(View.VISIBLE);
                                fabCentrarMapa.setVisibility(View.VISIBLE);
                                fabVerLista.setVisibility(View.VISIBLE);
                                bottomNavCard.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
            });
        }

        if (rvListaPuntosCompleta != null) {
            rvListaPuntosCompleta.setLayoutManager(new LinearLayoutManager(this));
            pointListAdapter = new PointListAdapter(new ArrayList<>(), punto -> {
                if (bottomSheetBehavior != null) bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etBuscadorTarjeta.getWindowToken(), 0);

                seleccionarPuntoDesdeBusqueda(punto);
            });
            rvListaPuntosCompleta.setAdapter(pointListAdapter);
        }

        fabVerLista.setOnClickListener(v -> {
            if (bottomSheetBehavior != null) bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        etBuscadorTarjeta.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { aplicarFiltrosTarjeta(); }
        });

        Chip chipOrdenRating = findViewById(R.id.chipOrdenRating);
        ChipGroup cgCategorias = findViewById(R.id.cgCategorias);

        if (chipOrdenRating != null) {
            chipOrdenRating.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sortByRating = isChecked;
                aplicarFiltrosTarjeta();
            });
        }

        if (cgCategorias != null) {
            cgCategorias.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    categoryFilter = "Todas";
                } else {
                    int id = checkedIds.get(0);
                    if (id == R.id.chipCatTodas) categoryFilter = "Todas";
                    else if (id == R.id.chipCatGastronomia) categoryFilter = "Gastronomía";
                    else if (id == R.id.chipCatCultura) categoryFilter = "Cultura y Religión";
                    else if (id == R.id.chipCatHistoria) categoryFilter = "Historia";
                    else if (id == R.id.chipCatEntretenimiento) categoryFilter = "Entretenimiento";
                }
                aplicarFiltrosTarjeta();
            });
        }

        rvResultadosBusqueda.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(new ArrayList<>(), this::seleccionarPuntoDesdeBusqueda);
        rvResultadosBusqueda.setAdapter(searchAdapter);

        etBuscador.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 2) { buscarPuntosEnApi(query); }
                else { cardResultadosBusqueda.setVisibility(View.GONE); }
            }
        });

        ivMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(Mapa.this, ivMenu);
            popup.getMenu().add(Menu.NONE, 1, 1, "Consultar mis sugerencias");
            popup.getMenu().add(Menu.NONE, 2, 2, "Cerrar sesión");

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        if (getSupportFragmentManager().findFragmentByTag("SUGERENCIAS") == null) {
                            abrirFragmento(new MisSugerencias(), "SUGERENCIAS");
                        }
                        return true;
                    case 2:
                        cerrarSesion();
                        return true;
                    default: return false;
                }
            });
            popup.show();
        });

        fabAdd.setOnClickListener(v -> {
            if (mMap != null) mostrarDialogoSugerencia(mMap.getCameraPosition().target);
        });

        fabCentrarMapa.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null && mMap != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16f));
                    } else { Toast.makeText(Mapa.this, "Buscando tu ubicación...", Toast.LENGTH_SHORT).show(); }
                });
            }
        });

        rvComentarios.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(new ArrayList<>());
        rvComentarios.setAdapter(commentAdapter);

        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) { cardDetallePunto.setVisibility(View.GONE); }
            @Override public void onAnimationRepeat(Animation animation) {}
        });

        llVerComentarios.setOnClickListener(v -> {
            if (puntoSeleccionado != null) {
                cardDetallePunto.setVisibility(View.GONE);
                topBarContainer.setVisibility(View.GONE);
                fabAdd.setVisibility(View.GONE);
                fabCentrarMapa.setVisibility(View.GONE);
                fabVerLista.setVisibility(View.GONE);
                bottomNavCard.setVisibility(View.GONE);

                tvComentariosTituloPunto.setText(puntoSeleccionado.getName());
                cardComentarios.setVisibility(View.VISIBLE);
                cardComentarios.startAnimation(slideUp);
                cargarComentarios(puntoSeleccionado.getId());
            }
        });

        btnCerrarComentarios.setOnClickListener(v -> {
            cardComentarios.setVisibility(View.GONE);
            topBarContainer.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);
            fabCentrarMapa.setVisibility(View.VISIBLE);
            fabVerLista.setVisibility(View.VISIBLE);
            bottomNavCard.setVisibility(View.VISIBLE);
            cardDetallePunto.setVisibility(View.VISIBLE);
        });

        btnEnviarComentario.setOnClickListener(v -> {
            String textoComentario = etNuevoComentario.getText().toString().trim();
            if (textoComentario.isEmpty()) { Toast.makeText(this, "Escribe un comentario primero", Toast.LENGTH_SHORT).show(); return; }
            mostrarDialogoCalificacion(textoComentario);
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    if (isNavigating && mMap != null && currentPolyline != null && !isRecalculando) {
                        LatLng miUbicacionActual = new LatLng(location.getLatitude(), location.getLongitude());

                        float rotacion = location.hasBearing() ? location.getBearing() : mMap.getCameraPosition().bearing;
                        com.google.android.gms.maps.model.CameraPosition cameraPosition = new com.google.android.gms.maps.model.CameraPosition.Builder()
                                .target(miUbicacionActual)
                                .zoom(19f)
                                .tilt(60f)
                                .bearing(rotacion)
                                .build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        if (puntoSeleccionado != null) {
                            float[] distanceToDest = new float[1];
                            Location.distanceBetween(location.getLatitude(), location.getLongitude(), puntoSeleccionado.getLatitude(), puntoSeleccionado.getLongitude(), distanceToDest);

                            // 🟢 ARRIBOS GPS: Se ejecuta una sola petición limpia enviando "COMPLETED"
                            if (distanceToDest[0] < 15.0f && isNavigating) {
                                isNavigating = false;
                                registrarViajeEnBaseDeDatos("COMPLETED");
                            }
                        }

                        List<LatLng> puntosRuta = currentPolyline.getPoints();
                        if (puntosRuta.size() > 0) {
                            float minDistance = Float.MAX_VALUE;
                            int closestIndex = 0;
                            for (int i = 0; i < puntosRuta.size(); i++) {
                                LatLng p = puntosRuta.get(i);
                                float[] results = new float[1];
                                Location.distanceBetween(miUbicacionActual.latitude, miUbicacionActual.longitude, p.latitude, p.longitude, results);
                                if (results[0] < minDistance) { minDistance = results[0]; closestIndex = i; }
                            }
                            if (minDistance > 50.0f && puntoSeleccionado != null) {
                                isRecalculando = true;
                                Toast.makeText(Mapa.this, "Recalculando ruta...", Toast.LENGTH_SHORT).show();
                                trazarRuta(miUbicacionActual.latitude, miUbicacionActual.longitude, puntoSeleccionado.getLatitude(), puntoSeleccionado.getLongitude(), tipoViajeActual);
                            } else {
                                if (closestIndex > 0) {
                                    puntosRuta.subList(0, closestIndex).clear();
                                    currentPolyline.setPoints(puntosRuta);
                                }
                            }
                        }
                    }
                }
            }
        };

        Button btnComenzarViaje = findViewById(R.id.btnComenzarViaje);
        btnComenzarViaje.setOnClickListener(v -> {
            if (mMap != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null && puntoSeleccionado != null) {
                        isNavigating = true;
                        isRecalculando = false;
                        startTimeMillis = System.currentTimeMillis();

                        RadioGroup rgTipoViaje = findViewById(R.id.rgTipoViaje);
                        tipoViajeActual = "driving-car";
                        if (rgTipoViaje != null && rgTipoViaje.getCheckedRadioButtonId() == R.id.rbPie) { tipoViajeActual = "foot-walking"; }

                        trazarRuta(location.getLatitude(), location.getLongitude(), puntoSeleccionado.getLatitude(), puntoSeleccionado.getLongitude(), tipoViajeActual);
                        Toast.makeText(this, "Iniciando viaje...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (btnCancelarViaje != null) {
            btnCancelarViaje.setOnClickListener(v -> {
                // 🟢 CUADRO DE DIÁLOGO: Confirmación rigurosa antes de proceder a guardar el viaje como cancelado
                new AlertDialog.Builder(Mapa.this)
                        .setTitle("Cancelar Viaje")
                        .setMessage("¿Seguro que deseas cancelar tu viaje?")
                        .setPositiveButton("Sí, cancelar", (dialog, which) -> registrarViajeEnBaseDeDatos("CANCELED"))
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        View btnNavMapa = bottomNavCard.findViewById(R.id.btnNavMapa);
        View btnNavViajes = bottomNavCard.findViewById(R.id.btnNavViajes);
        View btnNavNotif = bottomNavCard.findViewById(R.id.btnNavNotif);

        actualizarEstadoNavegacion("mapa");
        btnNavMapa.setOnClickListener(v -> {
            cerrarFragmentos();
        });

        btnNavViajes.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentByTag("VIAJES") == null) {
                abrirFragmento(new MisViajes(), "VIAJES");
            }
        });

        btnNavNotif.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentByTag("NOTIF") == null) {
                abrirFragmento(new Notificaciones(), "NOTIF");
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomSheetBehavior != null && bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) != null) {
                    cerrarFragmentos();
                } else if (cardComentarios != null && cardComentarios.getVisibility() == View.VISIBLE) {
                    btnCerrarComentarios.performClick();
                } else if (cardDetallePunto != null && cardDetallePunto.getVisibility() == View.VISIBLE) {
                    cardDetallePunto.startAnimation(slideDown);
                } else if (isNavigating) {
                    Toast.makeText(Mapa.this, "Debes cancelar el viaje para salir", Toast.LENGTH_SHORT).show();
                } else {
                    mostrarDialogoSalir();
                }
            }
        });
    }

    private void abrirFragmento(Fragment fragment, String tag) {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment, tag)
                .commit();

        if (tag.equals("VIAJES")) actualizarEstadoNavegacion("viajes");
        else if (tag.equals("NOTIF")) actualizarEstadoNavegacion("notificaciones");
        else if (tag.equals("SUGERENCIAS")) actualizarEstadoNavegacion("sugerencias");
    }

    private void cerrarFragmentos() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commit();
        }
        actualizarEstadoNavegacion("mapa");
    }

    private void actualizarEstadoNavegacion(String seccion) {
        View bottomNav = findViewById(R.id.bottomNavCardInclude);
        ImageView ivMapa = bottomNav.findViewById(R.id.ivNavMapa);
        TextView tvMapa = bottomNav.findViewById(R.id.tvNavMapa);
        ImageView ivViajes = bottomNav.findViewById(R.id.ivNavViajes);
        TextView tvViajes = bottomNav.findViewById(R.id.tvNavViajes);
        ImageView ivNotif = bottomNav.findViewById(R.id.ivNavNotif);
        TextView tvNotif = bottomNav.findViewById(R.id.tvNavNotif);

        int colorActivo = ContextCompat.getColor(this, R.color.chinanko_purple_dark);
        int colorInactivo = ContextCompat.getColor(this, R.color.chinanko_text_hint);

        if (ivMapa != null) ivMapa.setColorFilter(colorInactivo);
        if (tvMapa != null) { tvMapa.setTextColor(colorInactivo); tvMapa.setTypeface(null, android.graphics.Typeface.NORMAL); }
        if (ivViajes != null) ivViajes.setColorFilter(colorInactivo);
        if (tvViajes != null) { tvViajes.setTextColor(colorInactivo); tvViajes.setTypeface(null, android.graphics.Typeface.NORMAL); }
        if (ivNotif != null) ivNotif.setColorFilter(colorInactivo);
        if (tvNotif != null) { tvNotif.setTextColor(colorInactivo); tvNotif.setTypeface(null, android.graphics.Typeface.NORMAL); }

        if (seccion.equals("mapa")) {
            if (ivMapa != null) ivMapa.setColorFilter(colorActivo);
            if (tvMapa != null) { tvMapa.setTextColor(colorActivo); tvMapa.setTypeface(null, android.graphics.Typeface.BOLD); }
            if (!isNavigating) {
                if (fabAdd != null) fabAdd.setVisibility(View.VISIBLE);
                if (fabCentrarMapa != null) fabCentrarMapa.setVisibility(View.VISIBLE);
                if (fabVerLista != null) fabVerLista.setVisibility(View.VISIBLE);
                if (topBarContainer != null) topBarContainer.setVisibility(View.VISIBLE);
            }
        } else if (seccion.equals("viajes") || seccion.equals("notificaciones") || seccion.equals("sugerencias")) {
            if (seccion.equals("viajes") && ivViajes != null) {
                ivViajes.setColorFilter(colorActivo);
                if (tvViajes != null) { tvViajes.setTextColor(colorActivo); tvViajes.setTypeface(null, android.graphics.Typeface.BOLD); }
            } else if (seccion.equals("notificaciones") && ivNotif != null) {
                ivNotif.setColorFilter(colorActivo);
                if (tvNotif != null) { tvNotif.setTextColor(colorActivo); tvNotif.setTypeface(null, android.graphics.Typeface.BOLD); }
            }
            if (fabAdd != null) fabAdd.setVisibility(View.GONE);
            if (fabCentrarMapa != null) fabCentrarMapa.setVisibility(View.GONE);
            if (fabVerLista != null) fabVerLista.setVisibility(View.GONE);
            if (topBarContainer != null) topBarContainer.setVisibility(View.GONE);
        }
    }

    private void cerrarSesion() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
        SharedPreferences prefs = getSharedPreferences("ChinankoPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            ChinankoDatabase db = ChinankoDatabase.getInstance(getApplicationContext());
            db.clearAllTables();
        });

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Mapa.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void buscarPuntosEnApi(String query) {
        RetrofitClient.getApiService(this).searchInterestPoints(query).enqueue(new Callback<List<InterestPointResponse>>() {
            @Override public void onResponse(Call<List<InterestPointResponse>> call, Response<List<InterestPointResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    cardResultadosBusqueda.setVisibility(View.VISIBLE);
                    searchAdapter.updateData(response.body());
                } else { cardResultadosBusqueda.setVisibility(View.GONE); }
            }
            @Override public void onFailure(Call<List<InterestPointResponse>> call, Throwable t) { cardResultadosBusqueda.setVisibility(View.GONE); }
        });
    }

    public void seleccionarPuntoDesdeBusqueda(InterestPointResponse punto) {
        cardResultadosBusqueda.setVisibility(View.GONE);
        etBuscador.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etBuscador.getWindowToken(), 0);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(punto.getLatitude(), punto.getLongitude()), 17f));
        puntoSeleccionado = punto;
        tvDetalleTitulo.setText(punto.getName());
        tvDetalleDescripcion.setText(punto.getDescription());
        rbDetallePunto.setRating(punto.getAverageRating() != null ? punto.getAverageRating().floatValue() : 0f);
        tvDetalleTotalReviews.setText("(" + (punto.getTotalReviews() != null ? punto.getTotalReviews() : 0) + ")");

        cargarImagenesDelPunto(punto.getId());

        if (cardDetallePunto.getVisibility() != View.VISIBLE) {
            cardDetallePunto.setVisibility(View.VISIBLE);
            cardDetallePunto.startAnimation(slideUp);
        }
    }

    private void mostrarDialogoSugerencia(LatLng coordenadas) {
        urisFotosSugerencia.clear();

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_agregar_sugerencia, null);
        EditText etNombre = dialogView.findViewById(R.id.etSugerenciaNombre);
        EditText etDescripcion = dialogView.findViewById(R.id.etSugerenciaDescripcion);

        Spinner spCategoria = dialogView.findViewById(R.id.spSugerenciaCategoria);
        String[] opcionesCat = {"Cultura y Religión", "Gastronomía", "Historia", "Entretenimiento", "Deportes", "Area verde", "Comercio"};
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesCat);
        spCategoria.setAdapter(adapterCat);

        Button btnElegirFotos = dialogView.findViewById(R.id.btnElegirFotosSugerencia);
        tvContadorFotosSugerencia = dialogView.findViewById(R.id.tvContadorFotosSugerencia);

        btnElegirFotos.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerLauncher.launch(Intent.createChooser(intent, "Selecciona fotos del lugar"));
        });

        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarSugerencia);
        Button btnEnviar = dialogView.findViewById(R.id.btnEnviarSugerencia);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnEnviar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String categoriaSeleccionada = spCategoria.getSelectedItem().toString();

            if (nombre.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(Mapa.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            btnEnviar.setEnabled(false);
            btnEnviar.setText("Subiendo datos...");

            SuggestedPointRequest request = new SuggestedPointRequest(nombre, descripcion, coordenadas.latitude, coordenadas.longitude, categoriaSeleccionada);

            RetrofitClient.getApiService(Mapa.this).createSuggestion(request).enqueue(new Callback<SuggestedPointResponse>() {
                @Override public void onResponse(Call<SuggestedPointResponse> call, Response<SuggestedPointResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Long newSuggestedPointId = response.body().getId();

                        if (!urisFotosSugerencia.isEmpty()) {
                            btnEnviar.setText("Subiendo fotos...");
                            subirFotosAlPunto(newSuggestedPointId, dialog);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(Mapa.this, "Sugerencia enviada a revisión", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        btnEnviar.setEnabled(true);
                        btnEnviar.setText("Enviar");
                        Toast.makeText(Mapa.this, "Error de servidor", Toast.LENGTH_LONG).show();
                    }
                }
                @Override public void onFailure(Call<SuggestedPointResponse> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(Mapa.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void limpiarModoNavegacion() {
        isNavigating = false;
        isRecalculando = false;
        if (cardNavegacion != null) cardNavegacion.setVisibility(View.GONE);
        if (btnCancelarViaje != null) btnCancelarViaje.setVisibility(View.GONE);
        topBarContainer.setVisibility(View.VISIBLE);
        fabAdd.setVisibility(View.VISIBLE);
        fabCentrarMapa.setVisibility(View.VISIBLE);
        fabVerLista.setVisibility(View.VISIBLE);
        bottomNavCard.setVisibility(View.VISIBLE);
        mMap.clear();
        puntoSeleccionado = null;
        pintarTodosLosPinesMapa();

        if (mMap != null) {
            com.google.android.gms.maps.model.CameraPosition vistaNormal = new com.google.android.gms.maps.model.CameraPosition.Builder()
                    .target(mMap.getCameraPosition().target)
                    .zoom(16f)
                    .tilt(0f)
                    .bearing(0f)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(vistaNormal));
        }
    }

    private void trazarRuta(double sLat, double sLon, double dLat, double dLon, String perfilViaje) {
        List<List<Double>> coordinates = new ArrayList<>();
        coordinates.add(java.util.Arrays.asList(sLon, sLat));
        coordinates.add(java.util.Arrays.asList(dLon, dLat));

        RouteRequest request = new RouteRequest(coordinates, "es");
        RetrofitClient.getApiService(this).getRoute(perfilViaje, ORS_API_KEY, request).enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                isRecalculando = false;
                if (response.isSuccessful() && response.body() != null && !response.body().features.isEmpty()) {
                    mMap.clear();

                    List<List<Double>> coords = response.body().features.get(0).geometry.coordinates;
                    PolylineOptions lineOptions = new PolylineOptions();
                    for (List<Double> point : coords) { lineOptions.add(new LatLng(point.get(1), point.get(0))); }
                    lineOptions.width(12f);
                    lineOptions.color(android.graphics.Color.parseColor("#65558F"));
                    lineOptions.jointType(com.google.android.gms.maps.model.JointType.ROUND);
                    currentPolyline = mMap.addPolyline(lineOptions);

                    BitmapDescriptor miIcono = obtenerIconoPorCategoria(puntoSeleccionado.getCategory());
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(puntoSeleccionado.getLatitude(), puntoSeleccionado.getLongitude()))
                            .title(puntoSeleccionado.getName())
                            .icon(miIcono)
                    );

                    topBarContainer.setVisibility(View.GONE);
                    fabAdd.setVisibility(View.GONE);
                    fabCentrarMapa.setVisibility(View.GONE);
                    fabVerLista.setVisibility(View.GONE);
                    bottomNavCard.setVisibility(View.GONE);
                    if (cardDetallePunto.getVisibility() == View.VISIBLE) cardDetallePunto.startAnimation(slideDown);

                    if (cardNavegacion != null) cardNavegacion.setVisibility(View.VISIBLE);
                    if (btnCancelarViaje != null) btnCancelarViaje.setVisibility(View.VISIBLE);

                    try {
                        RouteResponse.Segment segment = response.body().features.get(0).properties.segments.get(0);
                        if (tvInstructionNavigation != null) tvInstructionNavigation.setText(segment.steps.get(0).instruction);
                        if (tvDistanceTimeNavigation != null) tvDistanceTimeNavigation.setText("Tiempo estimado: " + Math.round(segment.duration / 60.0) + " min.");
                    } catch (Exception e) {
                        if (tvInstructionNavigation != null) tvInstructionNavigation.setText("Sigue la ruta en el mapa");
                    }
                }
            }
            @Override public void onFailure(Call<RouteResponse> call, Throwable t) { isRecalculando = false; }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        try { mMap.setMapStyle(com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)); } catch (Exception ignored) {}

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            iniciarRastreoUbicacion();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION_ID);
        }

        cargarPuntosDeInteresYMapa();

        mMap.setOnMarkerClickListener(marker -> {
            if (isNavigating) return true;
            if (marker.getTag() != null) {
                puntoSeleccionado = (InterestPointResponse) marker.getTag();
                tvDetalleTitulo.setText(puntoSeleccionado.getName());
                tvDetalleDescripcion.setText(puntoSeleccionado.getDescription());
                rbDetallePunto.setRating(puntoSeleccionado.getAverageRating() != null ? puntoSeleccionado.getAverageRating().floatValue() : 0f);
                tvDetalleTotalReviews.setText("(" + (puntoSeleccionado.getTotalReviews() != null ? puntoSeleccionado.getTotalReviews() : 0) + ")");
                cargarImagenesDelPunto(puntoSeleccionado.getId());
                if (cardDetallePunto.getVisibility() != View.VISIBLE) {
                    cardDetallePunto.setVisibility(View.VISIBLE);
                    cardDetallePunto.startAnimation(slideUp);
                }
            }
            return true;
        });

        mMap.setOnMapClickListener(latLng -> {
            if (cardDetallePunto.getVisibility() == View.VISIBLE) cardDetallePunto.startAnimation(slideDown);
            cardResultadosBusqueda.setVisibility(View.GONE);
            etBuscador.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etBuscador.getWindowToken(), 0);
        });
    }

    private void cargarPuntosDeInteresYMapa() {
        RetrofitClient.getApiService(this).getAllInterestPoints().enqueue(new Callback<List<InterestPointResponse>>() {
            @Override
            public void onResponse(Call<List<InterestPointResponse>> call, Response<List<InterestPointResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPuntosGlobal = response.body();
                    pintarTodosLosPinesMapa();
                    aplicarFiltrosTarjeta();
                }
            }
            @Override public void onFailure(Call<List<InterestPointResponse>> call, Throwable t) {}
        });
    }

    private void pintarTodosLosPinesMapa() {
        if(mMap == null) return;
        mMap.clear();
        for (InterestPointResponse punto : listaPuntosGlobal) {
            BitmapDescriptor icono = obtenerIconoPorCategoria(punto.getCategory());
            com.google.android.gms.maps.model.Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(punto.getLatitude(), punto.getLongitude()))
                    .title(punto.getName())
                    .icon(icono));
            if (marker != null) marker.setTag(punto);
        }
    }

    private void aplicarFiltrosTarjeta() {
        if (listaPuntosGlobal == null) return;

        String query = etBuscadorTarjeta.getText().toString().trim().toLowerCase();
        List<InterestPointResponse> filtrados = new ArrayList<>();

        for (InterestPointResponse punto : listaPuntosGlobal) {
            String cat = punto.getCategory() != null ? punto.getCategory() : "";
            boolean matchCat = categoryFilter.equals("Todas") || cat.equalsIgnoreCase(categoryFilter);
            boolean matchQuery = query.isEmpty() || punto.getName().toLowerCase().contains(query);

            if (matchCat && matchQuery) {
                filtrados.add(punto);
            }
        }

        if (sortByRating) {
            filtrados.sort((p1, p2) -> {
                double r1 = p1.getAverageRating() != null ? p1.getAverageRating() : 0.0;
                double r2 = p2.getAverageRating() != null ? p2.getAverageRating() : 0.0;
                return Double.compare(r2, r1);
            });
        }

        if (pointListAdapter != null) {
            pointListAdapter.updateData(filtrados);
        }
    }

    public void abrirPuntoDesdeViaje(long pointId) {
        cerrarFragmentos();
        for (InterestPointResponse punto : listaPuntosGlobal) {
            if (punto.getId() == pointId) {
                seleccionarPuntoDesdeBusqueda(punto);
                break;
            }
        }
    }

    private BitmapDescriptor obtenerIconoPorCategoria(String categoria) {
        int idIcono, colorFondo;
        String cat = categoria != null ? categoria.toLowerCase() : "";
        switch (cat) {
            case "cultura y religión": case "cultura y religion": idIcono = R.drawable.ic_religion; colorFondo = android.graphics.Color.parseColor("#9C27B0"); break;
            case "gastronomía": case "gastronomia": idIcono = R.drawable.ic_comida; colorFondo = android.graphics.Color.parseColor("#FF9800"); break;
            case "historia": idIcono = R.drawable.ic_historia; colorFondo = android.graphics.Color.parseColor("#795548"); break;
            case "entretenimiento": idIcono = R.drawable.ic_entretenimiento; colorFondo = android.graphics.Color.parseColor("#E91E63"); break;
            default: return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }
        int size = 100;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(); paint.setColor(colorFondo); paint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

        Drawable vectorDrawable = ContextCompat.getDrawable(this, idIcono);
        if (vectorDrawable != null) {
            vectorDrawable.setTint(android.graphics.Color.WHITE);
            vectorDrawable.setBounds(22, 22, size - 22, size - 22);
            vectorDrawable.draw(canvas);
        }
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void cargarImagenesDelPunto(Long pointId) {
        RetrofitClient.getApiService(this).getInterestPointImages(pointId).enqueue(new Callback<List<InterestPointImageResponse>>() {
            @Override public void onResponse(Call<List<InterestPointImageResponse>> call, Response<List<InterestPointImageResponse>> response) {
                List<String> urls = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    for (InterestPointImageResponse img : response.body()) { urls.add(img.getImageUrl()); }
                } else { urls.add("https://via.placeholder.com/400x200?text=Sin+Imagen"); }

                vpImagenesPunto.setAdapter(new ImageCarouselAdapter(urls, (position, allUrls) -> {
                    if (!allUrls.get(position).contains("via.placeholder.com")) { mostrarImagenAmpliada(position, allUrls); }
                }));
            }
            @Override public void onFailure(Call<List<InterestPointImageResponse>> call, Throwable t) {}
        });
    }

    private void cargarComentarios(Long pointId) {
        RetrofitClient.getApiService(this).getCommentsByInterestPoint(pointId).enqueue(new Callback<List<CommentResponse>>() {
            @Override public void onResponse(Call<List<CommentResponse>> call, Response<List<CommentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) commentAdapter.updateComments(response.body());
            }
            @Override public void onFailure(Call<List<CommentResponse>> call, Throwable t) {}
        });
    }

    private void mostrarDialogoCalificacion(String contenido) {
        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5); ratingBar.setStepSize(1.0f); ratingBar.setRating(5.0f);
        LinearLayout layout = new LinearLayout(this); layout.setGravity(Gravity.CENTER); layout.setPadding(0, 30, 0, 0); layout.addView(ratingBar);
        new AlertDialog.Builder(this).setTitle("Califica este lugar").setView(layout)
                .setPositiveButton("Enviar", (dialog, which) -> enviarComentarioAPI((int) ratingBar.getRating(), contenido))
                .setNegativeButton("Cancelar", null).show();
    }

    private void enviarComentarioAPI(int rating, String contenido) {
        if (puntoSeleccionado == null) return;
        RetrofitClient.getApiService(this).createComment(puntoSeleccionado.getId(), new CommentRequest(rating, contenido)).enqueue(new Callback<CommentResponse>() {
            @Override public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Mapa.this, "Comentario enviado", Toast.LENGTH_SHORT).show();
                    etNuevoComentario.setText(""); cargarComentarios(puntoSeleccionado.getId());
                }
            }
            @Override public void onFailure(Call<CommentResponse> call, Throwable t) {}
        });
    }

    private void iniciarRastreoUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        mMap.setMyLocationEnabled(true);
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).setMinUpdateDistanceMeters(2).build();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null && mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16f));
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) iniciarRastreoUbicacion();
    }

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) Toast.makeText(context, "¡Imagen guardada!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        ContextCompat.registerReceiver(this, downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(downloadReceiver); } catch (Exception ignored) {}
        if (fusedLocationClient != null && locationCallback != null) fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void mostrarImagenAmpliada(int posicionInicial, List<String> urls) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_imagen_ampliada);
        androidx.viewpager2.widget.ViewPager2 vpImagenGrande = dialog.findViewById(R.id.vpImagenGrande);
        dialog.findViewById(R.id.btnCerrarDialog).setOnClickListener(v -> dialog.dismiss());
        vpImagenGrande.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ImageView iv = new ImageView(parent.getContext());
                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return new RecyclerView.ViewHolder(iv) {};
            }
            @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Glide.with(holder.itemView.getContext()).load(urls.get(position)).fitCenter().into((ImageView) holder.itemView);
            }
            @Override public int getItemCount() { return urls.size(); }
        });
        vpImagenGrande.setCurrentItem(posicionInicial, false);

        dialog.findViewById(R.id.btnDescargarImagen).setOnClickListener(v -> {
            try {
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(urls.get(vpImagenGrande.getCurrentItem())));
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "chinanko_" + System.currentTimeMillis() + ".jpg");
                dm.enqueue(req);
                Toast.makeText(Mapa.this, "Descargando...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) { Toast.makeText(Mapa.this, "Error de descarga", Toast.LENGTH_SHORT).show(); }
        });
        dialog.show();
    }

    private void mostrarDialogoSalir() {
        new AlertDialog.Builder(this).setTitle("Salir de la aplicación").setMessage("¿Estás seguro de que quieres salir?")
                .setPositiveButton("Sí, salir", (dialog, which) -> finish()).setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()).show();
    }

    private void subirFotosAlPunto(Long suggestedPointId, AlertDialog dialog) {
        if (urisFotosSugerencia.isEmpty()) return;

        Uri uriFoto = urisFotosSugerencia.get(0);
        java.io.File fileLocal = getFileFromUri(uriFoto);

        if (fileLocal == null) {
            dialog.dismiss();
            Toast.makeText(this, "Sugerencia creada, pero error al procesar la foto en el celular", Toast.LENGTH_SHORT).show();
            return;
        }

        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), fileLocal);
        okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("file", fileLocal.getName(), requestFile);

        RetrofitClient.getApiService(this).uploadSuggestedPointImage(suggestedPointId, body).enqueue(new Callback<SuggestedPointImageResponse>() {
            @Override
            public void onResponse(Call<SuggestedPointImageResponse> call, Response<SuggestedPointImageResponse> response) {
                dialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Mapa.this, "¡Sugerencia y foto enviadas exitosamente!", Toast.LENGTH_LONG).show();
                    fileLocal.delete();
                } else {
                    Log.e("API_UPLOAD", "El servidor rechazó la foto. Código: " + response.code());
                    Toast.makeText(Mapa.this, "Sugerencia creada, pero el servidor rechazó la foto (Código " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SuggestedPointImageResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e("API_UPLOAD", "Error de red al subir foto: " + t.getMessage());
                Toast.makeText(Mapa.this, "Sugerencia creada, pero falló la conexión al subir la foto", Toast.LENGTH_LONG).show();
            }
        });
    }

    private java.io.File getFileFromUri(Uri uri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            java.io.File tempFile = java.io.File.createTempFile("upload_sugerencia", ".jpg", getCacheDir());
            java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            Log.e(TAG, "Error al crear archivo desde URI", e);
            return null;
        }
    }

    // 🟢 NUEVO MÉTODO UNIFICADO: Administra los estados "COMPLETED" y "CANCELED" limpiando capas adecuadamente
    private void registrarViajeEnBaseDeDatos(String status) {
        if (puntoSeleccionado == null) return;

        long durationMillis = System.currentTimeMillis() - startTimeMillis;
        int durationMinutes = Math.max((int) (durationMillis / (1000 * 60)), 1);

        TravelRequest request = new TravelRequest(puntoSeleccionado.getId(), durationMinutes, status);
        RetrofitClient.getApiService(this).recordTravel(request).enqueue(new Callback<TravelResponse>() {
            @Override public void onResponse(Call<TravelResponse> call, Response<TravelResponse> response) {
                if (response.isSuccessful()) {
                    String mensaje = status.equals("COMPLETED") ? "¡Has llegado! Viaje guardado." : "Viaje cancelado y registrado.";
                    Toast.makeText(Mapa.this, mensaje, Toast.LENGTH_LONG).show();
                }
                limpiarModoNavegacion();
            }
            @Override public void onFailure(Call<TravelResponse> call, Throwable t) {
                limpiarModoNavegacion();
            }
        });
    }
}