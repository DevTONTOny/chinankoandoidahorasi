package com.example.proyecto_chinanko.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "suggestions") // 🟢 Le decimos a Room que esta es una tabla
public class SuggestedPointResponse {

    @PrimaryKey // 🟢 Indicamos la clave principal
    private Long id;

    private String name;
    private String status;
    private String description;

    private String Category;

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    // ==========================================
    // GETTERS
    // ==========================================
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }

    // ==========================================
    // SETTERS (Obligatorios para Room)
    // ==========================================
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
}