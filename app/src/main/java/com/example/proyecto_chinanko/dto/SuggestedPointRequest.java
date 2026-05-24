package com.example.proyecto_chinanko.dto;

public class SuggestedPointRequest {
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String category;

    public SuggestedPointRequest(String name, String description, Double latitude, Double longitude, String category) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }
}