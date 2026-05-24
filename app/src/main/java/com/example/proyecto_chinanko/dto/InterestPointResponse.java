package com.example.proyecto_chinanko.dto;
import com.google.gson.annotations.SerializedName;

public class InterestPointResponse {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;

    @SerializedName("town_id")
    private Long townId;

    @SerializedName("town_name")
    private String townName;

    @SerializedName("average_rating")
    private Double averageRating;

    @SerializedName("total_reviews")
    private Integer totalReviews;

    @SerializedName("category")
    private String Category;

    public String getCategory() {
        return Category;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Long getTownId() { return townId; }
    public String getTownName() { return townName; }
}