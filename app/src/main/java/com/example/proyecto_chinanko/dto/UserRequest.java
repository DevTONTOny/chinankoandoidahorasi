package com.example.proyecto_chinanko.dto;

public class UserRequest {
    private String username;
    private String password;
    private String email;
    private String role; // 🟢 Nuevo campo requerido por tu backend

    public UserRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = "TOURIST"; // Todo nuevo registro desde la app será Turista
    }

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}