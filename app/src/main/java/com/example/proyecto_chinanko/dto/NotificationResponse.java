package com.example.proyecto_chinanko.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "notifications") // 🟢 Le decimos a Room que esta es una tabla
public class NotificationResponse {

    @PrimaryKey // 🟢 Indicamos la clave principal
    private Long id;

    private String message;

    @SerializedName("read")
    private boolean isRead;

    private String createdAt;

    // ==========================================
    // GETTERS
    // ==========================================
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }

    // ==========================================
    // SETTERS (Obligatorios para Room)
    // ==========================================
    public void setId(Long id) { this.id = id; }
    public void setMessage(String message) { this.message = message; }
    public void setRead(boolean read) { isRead = read; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}