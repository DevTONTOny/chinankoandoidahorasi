package com.example.proyecto_chinanko.dto;

import com.google.gson.annotations.SerializedName;

public class CommentResponse {
    private Long id;
    private Integer rating;
    private String content;

    // 🟢 Le decimos que en el JSON el campo se llama "authorUsername"
    @SerializedName("authorUsername")
    private String userUsername;

    private Long interestPointId;
    private String createdAt;

    // Getters
    public Long getId() { return id; }
    public Integer getRating() { return rating; }
    public String getContent() { return content; }

    // Este getter ahora sí retornará "admin" porque los nombres están vinculados
    public String getUserUsername() { return userUsername; }
}