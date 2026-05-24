package com.example.proyecto_chinanko.dto;

public class CommentRequest {
    private Integer rating;
    private String content;

    public CommentRequest(Integer rating, String content) {
        this.rating = rating;
        this.content = content;
    }

    // Getters y Setters
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}