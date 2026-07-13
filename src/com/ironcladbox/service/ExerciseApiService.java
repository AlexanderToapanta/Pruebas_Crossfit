package com.ironcladbox.service;

import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;

public class ExerciseApiService {
    private static ExerciseApiService instance;
    private final ApiService api;

    private ExerciseApiService() {
        this.api = ApiService.getInstance();
    }

    public static synchronized ExerciseApiService getInstance() {
        if (instance == null) instance = new ExerciseApiService();
        return instance;
    }

    public ApiResponse getAll() {
        return api.get(ApiConfig.EJERCICIOS);
    }

    public ApiResponse getById(int id) {
        return api.get(ApiConfig.EJERCICIOS + "/" + id);
    }

    public ApiResponse search(String query) {
        return api.get(ApiConfig.EJERCICIOS + "/search?q=" + query);
    }

    public ApiResponse getStats() {
        return api.get(ApiConfig.EJERCICIOS + "/stats");
    }

    public ApiResponse create(JsonObject data) {
        return api.post(ApiConfig.EJERCICIOS, data);
    }

    public ApiResponse update(int id, JsonObject data) {
        return api.put(ApiConfig.EJERCICIOS + "/" + id, data);
    }

    public ApiResponse delete(int id) {
        return api.delete(ApiConfig.EJERCICIOS + "/" + id);
    }

    public ApiResponse reactivate(int id) {
        JsonObject body = new JsonObject();
        return api.patch(ApiConfig.EJERCICIOS + "/" + id + "/reactivate", body);
    }
}
