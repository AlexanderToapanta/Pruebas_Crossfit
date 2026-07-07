package com.ironcladbox.service;

import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;

public class TrainerApiService {
    private static TrainerApiService instance;
    private final ApiService api;

    private TrainerApiService() {
        this.api = ApiService.getInstance();
    }

    public static synchronized TrainerApiService getInstance() {
        if (instance == null) instance = new TrainerApiService();
        return instance;
    }

    public ApiResponse getAll() {
        return api.get(ApiConfig.ADMIN_TRAINERS);
    }

    public ApiResponse getById(int id) {
        return api.get(ApiConfig.ADMIN_TRAINERS + "/" + id);
    }

    public ApiResponse create(JsonObject trainerData) {
        return api.post(ApiConfig.ADMIN_TRAINERS, trainerData);
    }

    public ApiResponse update(int id, JsonObject trainerData) {
        return api.put(ApiConfig.ADMIN_TRAINERS + "/" + id, trainerData);
    }

    public ApiResponse updateStatus(int id, boolean activo) {
        JsonObject body = new JsonObject();
        body.addProperty("activo", activo);
        return api.put(ApiConfig.ADMIN_TRAINERS + "/" + id + "/status", body);
    }

    public ApiResponse delete(int id) {
        return api.delete(ApiConfig.ADMIN_TRAINERS + "/" + id);
    }

    public ApiResponse getPublicTrainers() {
        return api.get(ApiConfig.TRAINERS);
    }

    public ApiResponse getMyClasses() {
        return api.get(ApiConfig.TRAINERS_MY_CLASSES);
    }

    public ApiResponse getMyWods() {
        return api.get(ApiConfig.TRAINERS_MY_WODS);
    }

    public ApiResponse getMyAthletes() {
        return api.get(ApiConfig.TRAINERS_MY_ATHLETES);
    }
}
