package com.ironcladbox.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;
import java.util.ArrayList;
import java.util.List;

public class AuthApiService {
    private static AuthApiService instance;
    private final ApiService api;

    private AuthApiService() {
        this.api = ApiService.getInstance();
    }

    public static synchronized AuthApiService getInstance() {
        if (instance == null) instance = new AuthApiService();
        return instance;
    }

    public ApiResponse login(String email, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);
        return api.post(ApiConfig.AUTH_LOGIN, body);
    }

    public ApiResponse register(JsonObject userData) {
        return api.post(ApiConfig.AUTH_REGISTER, userData);
    }

    public ApiResponse verifyToken() {
        return api.get(ApiConfig.AUTH_VERIFY);
    }

    public ApiResponse getProfile() {
        return api.get(ApiConfig.AUTH_PROFILE);
    }

    public ApiResponse logout() {
        return api.post(ApiConfig.AUTH_LOGOUT, null);
    }

    public ApiResponse changePassword(String currentPassword, String newPassword) {
        JsonObject body = new JsonObject();
        body.addProperty("currentPassword", currentPassword);
        body.addProperty("newPassword", newPassword);
        return api.post(ApiConfig.AUTH_CHANGE_PASSWORD, body);
    }

    public ApiResponse getAvailableMemberships() {
        return api.get(ApiConfig.AUTH_MEMBERSHIPS);
    }
}
