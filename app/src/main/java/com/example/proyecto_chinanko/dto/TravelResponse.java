package com.example.proyecto_chinanko.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "travels")
public class TravelResponse {

    @PrimaryKey
    private Long id;

    private Integer durationInMinutes;
    private String travelDate;

    @SerializedName("interestPointName")
    private String interestPointName;

    @SerializedName("interestPointImageUrl")
    private String imageUrl;

    @SerializedName("interestPointId")
    private long interestPointId;

    @SerializedName("status")
    private String status; // 🟢 NUEVO

    // ==========================================
    // GETTERS
    // ==========================================
    public Long getId() { return id; }
    public Integer getDurationInMinutes() { return durationInMinutes; }
    public String getTravelDate() { return travelDate; }
    public String getInterestPointName() { return interestPointName; }
    public String getImageUrl() { return imageUrl; }
    public long getInterestPointId() { return interestPointId; }
    public String getStatus() { return status; }

    // ==========================================
    // SETTERS
    // ==========================================
    public void setId(Long id) { this.id = id; }
    public void setDurationInMinutes(Integer durationInMinutes) { this.durationInMinutes = durationInMinutes; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }
    public void setInterestPointName(String interestPointName) { this.interestPointName = interestPointName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setInterestPointId(long interestPointId) { this.interestPointId = interestPointId; }
    public void setStatus(String status) { this.status = status; }
}