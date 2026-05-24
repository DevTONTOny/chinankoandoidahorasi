package com.example.proyecto_chinanko.dto;

import com.google.gson.annotations.SerializedName;

public class InterestPointImageResponse {

    private Long id;

    // 🟢 Usamos SerializedName por si en el futuro cambias el nombre en el JSON
    @SerializedName("imageUrl")
    private String imageUrl;

    // Constructor vacío requerido por Gson
    public InterestPointImageResponse() {
    }

    public InterestPointImageResponse(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters (Opcionales para Gson, pero útiles)
    public void setId(Long id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}