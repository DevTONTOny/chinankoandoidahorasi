package com.example.proyecto_chinanko.dto;

import com.google.gson.annotations.SerializedName;

public class SuggestedPointImageResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Constructor vacío (necesario para algunas librerías de serialización)
    public SuggestedPointImageResponse() {
    }

    // Constructor completo
    public SuggestedPointImageResponse(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}