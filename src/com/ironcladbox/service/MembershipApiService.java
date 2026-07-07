package com.ironcladbox.service;

import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;

public class MembershipApiService {
    private static MembershipApiService instance;
    private final ApiService api;

    private MembershipApiService() {
        this.api = ApiService.getInstance();
    }

    public static synchronized MembershipApiService getInstance() {
        if (instance == null) instance = new MembershipApiService();
        return instance;
    }

    public ApiResponse getAll() {
        return api.get(ApiConfig.ADMIN_MEMBERSHIPS);
    }

    public ApiResponse getAvailable() {
        return api.get(ApiConfig.MEMBERS_MEMBERSHIPS);
    }

    public ApiResponse getById(int id) {
        return api.get(ApiConfig.ADMIN_MEMBERSHIPS + "/" + id);
    }

    public ApiResponse create(JsonObject data) {
        return api.post(ApiConfig.ADMIN_MEMBERSHIPS, data);
    }

    public ApiResponse update(int id, JsonObject data) {
        return api.put(ApiConfig.ADMIN_MEMBERSHIPS + "/" + id, data);
    }

    public ApiResponse delete(int id) {
        return api.delete(ApiConfig.ADMIN_MEMBERSHIPS + "/" + id);
    }

    public ApiResponse assign(int idAtleta, int idMembresia) {
        JsonObject body = new JsonObject();
        body.addProperty("id_atleta", idAtleta);
        body.addProperty("id_membresia", idMembresia);
        return api.post(ApiConfig.ADMIN_ASSIGN_MEMBERSHIP, body);
    }

    public ApiResponse getStats() {
        return api.get(ApiConfig.ADMIN_STATS);
    }
}
