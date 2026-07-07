package com.ironcladbox.service;

import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;

public class WodApiService {
    private static WodApiService instance;
    private final ApiService api;

    private WodApiService() {
        this.api = ApiService.getInstance();
    }

    public static synchronized WodApiService getInstance() {
        if (instance == null) instance = new WodApiService();
        return instance;
    }

    public ApiResponse getByMonth(int year, int month) {
        return api.get(ApiConfig.WOD_CALENDAR + "/" + year + "/" + month);
    }

    public ApiResponse getByDate(String fecha) {
        return api.get(ApiConfig.WOD + "/date/" + fecha);
    }

    public ApiResponse getById(int idWod) {
        return api.get(ApiConfig.WOD + "/" + idWod);
    }

    public ApiResponse create(JsonObject wodData) {
        return api.post(ApiConfig.WOD, wodData);
    }

    public ApiResponse update(int idWod, JsonObject wodData) {
        return api.put(ApiConfig.WOD + "/" + idWod, wodData);
    }

    public ApiResponse delete(int idWod) {
        return api.delete(ApiConfig.WOD + "/" + idWod);
    }

    public ApiResponse createSchedule(int idWod, JsonObject scheduleData) {
        return api.post(ApiConfig.WOD + "/" + idWod + "/schedule", scheduleData);
    }

    public ApiResponse enrollSchedule(int idHorario) {
        return api.post(ApiConfig.WOD + "/schedule/" + idHorario + "/enroll", null);
    }

    public ApiResponse unenrollSchedule(int idHorario) {
        return api.delete(ApiConfig.WOD + "/schedule/" + idHorario + "/unenroll");
    }

    public ApiResponse cancelSchedule(int idHorario) {
        return api.put(ApiConfig.WOD + "/schedule/" + idHorario + "/cancel", null);
    }

    public ApiResponse getMySchedules() {
        return api.get(ApiConfig.WOD_MY_SCHEDULES);
    }

    public ApiResponse getRacha() {
        return api.get(ApiConfig.WOD_RACHA);
    }

    public ApiResponse marcarAsistencia(int idInscripcion) {
        return api.post(ApiConfig.WOD_ASISTENCIA + "/" + idInscripcion, null);
    }

    public ApiResponse getEnrolledAthletes(int idHorario) {
        return api.get(ApiConfig.WOD + "/schedule/" + idHorario + "/athletes");
    }

    public ApiResponse getSchedulesByWod(int idWod) {
        return api.get(ApiConfig.WOD + "/" + idWod + "/schedules");
    }
}
