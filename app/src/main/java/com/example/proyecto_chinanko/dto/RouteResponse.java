package com.example.proyecto_chinanko.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteResponse {
    @SerializedName("features")
    public List<Feature> features;

    public static class Feature {
        @SerializedName("geometry")
        public Geometry geometry;

        // NUEVO: Aquí vienen las instrucciones de texto
        @SerializedName("properties")
        public Properties properties;
    }

    public static class Geometry {
        @SerializedName("coordinates")
        public List<List<Double>> coordinates;
    }

    // ==========================================
    // NUEVAS CLASES PARA LEER LAS INSTRUCCIONES
    // ==========================================
    public static class Properties {
        @SerializedName("segments")
        public List<Segment> segments;
    }

    public static class Segment {
        @SerializedName("distance")
        public Double distance; // Distancia total en metros

        @SerializedName("duration")
        public Double duration; // Tiempo en segundos

        @SerializedName("steps")
        public List<Step> steps; // Cada uno de los pasos (gira a la derecha, etc)
    }

    public static class Step {
        @SerializedName("instruction")
        public String instruction; // El texto real "Sigue caminando recto..."
    }
}