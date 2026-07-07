package com.ironcladbox.service;

import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;

public class AthleteApiService {
    private static AthleteApiService instance;
    private final ApiService api;

    private AthleteApiService() {
        this.api = ApiService.getInstance();
    }

    public static synchronized AthleteApiService getInstance() {
        if (instance == null) instance = new AthleteApiService();
        return instance;
    }

    public ApiResponse getAll() {
        return api.get(ApiConfig.ADMIN_ATHLETES);
    }

    public ApiResponse getById(int id) {
        return api.get(ApiConfig.ADMIN_ATHLETES + "/" + id);
    }

    public ApiResponse create(JsonObject athleteData) {
        return api.post(ApiConfig.ADMIN_ATHLETES, athleteData);
    }

    public ApiResponse update(int id, JsonObject athleteData) {
        return api.put(ApiConfig.ADMIN_ATHLETES + "/" + id, athleteData);
    }

    public ApiResponse updateStatus(int id, boolean activo) {
        JsonObject body = new JsonObject();
        body.addProperty("activo", activo);
        return api.put(ApiConfig.ADMIN_ATHLETES + "/" + id + "/status", body);
    }

    public ApiResponse updateMembership(int id, int idMembresia, String fechaInicio) {
        JsonObject body = new JsonObject();
        body.addProperty("id_membresia", idMembresia);
        body.addProperty("fecha_inicio", fechaInicio);
        return api.put(ApiConfig.ADMIN_ATHLETES + "/" + id + "/membership", body);
    }

    public ApiResponse delete(int id) {
        return api.delete(ApiConfig.ADMIN_ATHLETES + "/" + id);
    }

    public ApiResponse getMyMembership() {
        return api.get(ApiConfig.MEMBERS_MY_MEMBERSHIP);
    }

    public ApiResponse updateMyMembership(int idMembresia) {
        JsonObject body = new JsonObject();
        body.addProperty("id_membresia", idMembresia);
        return api.put(ApiConfig.MEMBERS_MY_MEMBERSHIP, body);
    }

    public ApiResponse cancelMyMembership() {
        return api.delete(ApiConfig.MEMBERS_MY_MEMBERSHIP);
    }

    public ApiResponse checkMembership() {
        return api.get(ApiConfig.MEMBERS_CHECK);
    }
}
