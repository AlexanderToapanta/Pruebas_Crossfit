package com.ironcladbox.service;

import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;

public class ClassApiService {
    private static ClassApiService instance;
    private final ApiService api;

    private ClassApiService() {
        this.api = ApiService.getInstance();
    }

    public static synchronized ClassApiService getInstance() {
        if (instance == null) instance = new ClassApiService();
        return instance;
    }

    public ApiResponse getAll() {
        return api.get(ApiConfig.CLASSES);
    }

    public ApiResponse getAvailable() {
        return api.get(ApiConfig.CLASSES_AVAILABLE);
    }

    public ApiResponse getById(int id) {
        return api.get(ApiConfig.CLASSES + "/" + id);
    }

    public ApiResponse create(JsonObject classData) {
        return api.post(ApiConfig.CLASSES, classData);
    }

    public ApiResponse update(int id, JsonObject classData) {
        return api.put(ApiConfig.CLASSES + "/" + id, classData);
    }

    public ApiResponse delete(int id) {
        return api.delete(ApiConfig.CLASSES + "/" + id);
    }

    public ApiResponse enroll(int idClase) {
        JsonObject body = new JsonObject();
        body.addProperty("id_clase", idClase);
        return api.post(ApiConfig.CLASSES_ENROLL, body);
    }

    public ApiResponse unenroll(int id) {
        return api.delete(ApiConfig.CLASSES + "/unenroll/" + id);
    }

    public ApiResponse getMyClasses() {
        return api.get(ApiConfig.CLASSES_MY);
    }

    public ApiResponse getEnrolledStudents(int id) {
        return api.get(ApiConfig.CLASSES + "/" + id + "/students");
    }

    public ApiResponse deletePermanently(int id) {
        return api.delete(ApiConfig.ADMIN_CLASSES + "/" + id + "/permanent");
    }

    public ApiResponse reactivate(int id) {
        JsonObject body = new JsonObject();
        body.addProperty("estado", "ACTIVA");
        return api.put(ApiConfig.ADMIN_CLASSES + "/" + id + "/reactivate", body);
    }
}
