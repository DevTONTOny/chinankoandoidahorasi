package com.example.proyecto_chinanko.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteRequest {
    @SerializedName("coordinates")
    public List<List<Double>> coordinates;

    @SerializedName("language")
    public String language;

    public RouteRequest(List<List<Double>> coordinates, String language) {
        this.coordinates = coordinates;
        this.language = language;
    }
}