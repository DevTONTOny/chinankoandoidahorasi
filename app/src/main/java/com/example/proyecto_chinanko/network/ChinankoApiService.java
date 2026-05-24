package com.example.proyecto_chinanko.network;

import com.example.proyecto_chinanko.dto.AuthResponse;
import com.example.proyecto_chinanko.dto.CommentRequest;
import com.example.proyecto_chinanko.dto.CommentResponse;
import com.example.proyecto_chinanko.dto.InterestPointImageResponse;
import com.example.proyecto_chinanko.dto.InterestPointResponse;
import com.example.proyecto_chinanko.dto.LoginRequest;
import com.example.proyecto_chinanko.dto.NotificationResponse;
import com.example.proyecto_chinanko.dto.RouteRequest;
import com.example.proyecto_chinanko.dto.RouteResponse;
import com.example.proyecto_chinanko.dto.SuggestedPointImageResponse;
import com.example.proyecto_chinanko.dto.SuggestedPointRequest;
import com.example.proyecto_chinanko.dto.SuggestedPointResponse;
import com.example.proyecto_chinanko.dto.TravelRequest;
import com.example.proyecto_chinanko.dto.TravelResponse;
import com.example.proyecto_chinanko.dto.UserRequest;
import com.example.proyecto_chinanko.dto.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChinankoApiService {


    // === ENDPOINTS DE USUARIOS ===
    @GET("api/v1/users")
    Call<List<UserResponse>> getAllUsers();

    @GET("api/v1/users/{id}")
    Call<UserResponse> getUserById(@Path("id") Long id);

    @POST("api/v1/users")
    Call<UserResponse> createUser(@Body UserRequest user);

    @POST("api/v1/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
    @GET("api/v1/interest-points")
    Call<List<InterestPointResponse>> getAllInterestPoints();

    @GET("api/v1/interest-points/town/{townId}")
    Call<List<InterestPointResponse>> getInterestPointsByTown(@Path("townId") Long townId);

    @GET("api/v1/interest-points/{id}/images")
    Call<List<InterestPointImageResponse>> getInterestPointImages(@Path("id") Long id);

    @retrofit2.http.Headers("Content-Type: application/json")
    @POST("https://api.openrouteservice.org/v2/directions/{profile}/geojson")
    Call<RouteResponse> getRoute(
            @Path("profile") String profile,
            @retrofit2.http.Header("Authorization") String apiKey,
            @Body RouteRequest request
    );
    @POST("api/v1/travels")
    Call<TravelResponse> recordTravel(@Body TravelRequest request);

    @GET("api/v1/travels/me")
    Call<List<TravelResponse>> getMyTravels();

    @POST("api/v1/suggested-points")
    Call<SuggestedPointResponse> createSuggestion(@Body SuggestedPointRequest request);

    @GET("api/v1/suggested-points/me")
    Call<List<SuggestedPointResponse>> getMySuggestions();

    @GET("api/v1/interest-points/search")
    Call<List<InterestPointResponse>> searchInterestPoints(@Query("name") String name);

    @GET("api/v1/comments/interest-points/{id}")
    Call<List<CommentResponse>> getCommentsByInterestPoint(@Path("id") Long pointId);

    @POST("api/v1/comments/interest-points/{id}")
    Call<CommentResponse> createComment(@Path("id") Long pointId, @Body CommentRequest request);

    @GET("api/v1/notifications")
    Call<List<NotificationResponse>> getMyNotifications();

    @PUT("api/v1/notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") Long id);

    // 🟢 NUEVO: Subir imagen a un punto sugerido
    @Multipart
    @POST("api/v1/suggested-points/{id}/images")
    Call<SuggestedPointImageResponse> uploadSuggestedPointImage(
            @Path("id") Long suggestedPointId,
            @Part okhttp3.MultipartBody.Part file
    );

    @GET("api/v1/suggested-points/{id}/images")
    Call<List<SuggestedPointImageResponse>> getImagesBySuggestedPoint(@Path("id") Long suggestedPointId);
/*



    // 🟢 ESTE ES EL ÚNICO getRoute QUE DEBES TENER AHORA:











    // === ENDPOINTS DE SUGERENCIAS ===




   */
}
