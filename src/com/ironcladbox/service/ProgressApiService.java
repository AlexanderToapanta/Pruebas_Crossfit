package com.ironcladbox.service;

import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;

public class ProgressApiService {
    private static ProgressApiService instance;
    private final ApiService api;

    private ProgressApiService() {
        this.api = ApiService.getInstance();
    }

    public static synchronized ProgressApiService getInstance() {
        if (instance == null) instance = new ProgressApiService();
        return instance;
    }

    public ApiResponse getEjerciciosConProgreso() {
        return api.get(ApiConfig.PROGRESO_EJERCICIOS);
    }

    public ApiResponse getEstadisticas() {
        return api.get(ApiConfig.PROGRESO_ESTADISTICAS);
    }

    public ApiResponse actualizarMarca(int idEjercicio, double marcaMaxima) {
        JsonObject body = new JsonObject();
        body.addProperty("id_ejercicio", idEjercicio);
        body.addProperty("marca_maxima", marcaMaxima);
        return api.post(ApiConfig.PROGRESO_MARCA, body);
    }

    public ApiResponse eliminarMarca(int idEjercicio) {
        return api.delete(ApiConfig.PROGRESO_MARCA + "/" + idEjercicio);
    }
}
