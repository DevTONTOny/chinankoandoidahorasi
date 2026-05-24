package com.example.proyecto_chinanko.dto;

public class TravelRequest {
    private Long interestPointId;
    private Integer durationInMinutes;
    private String status; // 🟢 NUEVO: Estado del viaje

    public TravelRequest(Long interestPointId, Integer durationInMinutes, String status) {
        this.interestPointId = interestPointId;
        this.durationInMinutes = durationInMinutes;
        this.status = status;
    }

    public Long getInterestPointId() { return interestPointId; }
    public void setInterestPointId(Long interestPointId) { this.interestPointId = interestPointId; }

    public Integer getDurationInMinutes() { return durationInMinutes; }
    public void setDurationInMinutes(Integer durationInMinutes) { this.durationInMinutes = durationInMinutes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}