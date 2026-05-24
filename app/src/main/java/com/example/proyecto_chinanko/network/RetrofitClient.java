package com.example.proyecto_chinanko.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://chinankoapi.onrender.com/";
    private static Retrofit retrofit = null;

    public static ChinankoApiService getApiService(Context context) {
        if (retrofit == null) {

            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();

                    SharedPreferences prefs = context.getSharedPreferences("ChinankoPrefs", Context.MODE_PRIVATE);
                    String token = prefs.getString("JWT_TOKEN", null);

                    // Verifica que vaya a la IP correcta
                    if (token != null && originalRequest.url().toString().contains("chinankoapi.onrender.com")) {
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(newRequest);
                    }

                    return chain.proceed(originalRequest);
                }
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ChinankoApiService.class);
    }
}