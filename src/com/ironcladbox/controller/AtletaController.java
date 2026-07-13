package com.ironcladbox.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcladbox.model.*;
import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AtletaController {
    private final ClassApiService classService;
    private final AthleteApiService athleteService;
    private final MembershipApiService membershipService;
    private final WodApiService wodService;
    private final ProgressApiService progressService;
    private final SocketService socketService;
    private Runnable onDataChanged;

    public AtletaController() {
        this.classService = ClassApiService.getInstance();
        this.athleteService = AthleteApiService.getInstance();
        this.membershipService = MembershipApiService.getInstance();
        this.wodService = WodApiService.getInstance();
        this.progressService = ProgressApiService.getInstance();
        this.socketService = SocketService.getInstance();

        socketService.on("class:created", data -> notifyChange());
        socketService.on("class:updated", data -> notifyChange());
        socketService.on("class:deleted", data -> notifyChange());
        socketService.on("class:enrollment_created", data -> notifyChange());
        socketService.on("class:enrollment_deleted", data -> notifyChange());
        socketService.on("membership:updated", data -> notifyChange());
        socketService.on("membership:cancelled", data -> notifyChange());
        socketService.on("wod:created", data -> notifyChange());
        socketService.on("wod:updated", data -> notifyChange());
        socketService.on("schedule:created", data -> notifyChange());
        socketService.on("schedule:deleted", data -> notifyChange());
        socketService.on("enrollment:created", data -> notifyChange());
        socketService.on("enrollment:deleted", data -> notifyChange());
        socketService.on("exercises:updated", data -> notifyChange());
        socketService.on("progress:updated", data -> notifyChange());
        socketService.setOnReconnected(() -> notifyChange());
    }

    public void setOnDataChanged(Runnable callback) {
        this.onDataChanged = callback;
    }

    private void notifyChange() {
        if (onDataChanged != null) {
            javax.swing.SwingUtilities.invokeLater(onDataChanged);
        }
    }

    public List<Clase> obtenerClasesDisponibles() {
        List<Clase> result = new ArrayList<>();
        try {
            ApiResponse resp = classService.getAvailable();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(parseClase(e.getAsJsonObject()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<Clase> obtenerClasesPorDia(String dia) {
        List<Clase> todas = obtenerClasesDisponibles();
        List<Clase> filtradas = new ArrayList<>();
        for (Clase c : todas) {
            if (c.getDiaSemana() != null && c.getDiaSemana().equalsIgnoreCase(dia)) {
                filtradas.add(c);
            }
        }
        return filtradas;
    }

    public List<Clase> obtenerMisClases() {
        List<Clase> result = new ArrayList<>();
        try {
            ApiResponse resp = classService.getMyClasses();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(parseClase(e.getAsJsonObject()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public boolean inscribirClase(int idClase) {
        try {
            ApiResponse resp = classService.enroll(idClase);
            if (resp != null && resp.isOk()) { notifyChange(); return true; }
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean cancelarInscripcionClase(int idClase) {
        try {
            ApiResponse resp = classService.unenroll(idClase);
            if (resp != null && resp.isOk()) { notifyChange(); return true; }
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public Suscripcion obtenerSuscripcionActiva(int idAtleta) {
        try {
            ApiResponse resp = athleteService.getMyMembership();
            if (resp.isOk() && resp.data != null && resp.data.isJsonObject()) {
                JsonObject json = resp.data.getAsJsonObject();
                Suscripcion s = new Suscripcion();
                s.setIdAtleta(idAtleta);
                if (json.has("id_membresia")) s.setIdMembresia(json.get("id_membresia").getAsInt());
                if (json.has("membresia_nombre")) s.setNombreMembresia(json.get("membresia_nombre").getAsString());
                if (json.has("precio")) s.setPrecioMembresia(json.get("precio").getAsDouble());
                if (json.has("fecha_inicio")) {
                    try { s.setFechaInicio(LocalDate.parse(json.get("fecha_inicio").getAsString().substring(0, 10))); } catch (Exception ex) {}
                }
                if (json.has("fecha_fin")) {
                    try { s.setFechaFin(LocalDate.parse(json.get("fecha_fin").getAsString().substring(0, 10))); } catch (Exception ex) {}
                }
                s.setActiva(true);
                return s;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<JsonObject> obtenerMisHorariosWOD() {
        List<JsonObject> result = new ArrayList<>();
        try {
            ApiResponse resp = wodService.getMySchedules();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(e.getAsJsonObject());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return result;
    }

    public JsonObject obtenerRacha() {
        try {
            ApiResponse resp = wodService.getRacha();
            if (resp.isOk() && resp.data != null && resp.data.isJsonObject()) {
                return resp.data.getAsJsonObject();
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        JsonObject fallback = new JsonObject();
        fallback.addProperty("racha_actual", 0);
        fallback.addProperty("racha_maxima", 0);
        fallback.addProperty("total_asistencias", 0);
        fallback.addProperty("asistencias_mes", 0);
        return fallback;
    }

    public List<JsonObject> obtenerHistorialAsistencias() {
        List<JsonObject> result = new ArrayList<>();
        try {
            ApiResponse resp = wodService.getHistorialAsistencias();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(e.getAsJsonObject());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return result;
    }

    public boolean marcarAsistencia(int idInscripcion) {
        try {
            ApiResponse resp = wodService.marcarAsistencia(idInscripcion);
            if (resp != null && resp.isOk()) { notifyChange(); return true; }
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean inscribirHorarioWOD(int idHorario) {
        try {
            ApiResponse resp = wodService.enrollSchedule(idHorario);
            if (resp != null && resp.isOk()) { notifyChange(); return true; }
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean cancelarInscripcionWOD(int idHorario) {
        try {
            ApiResponse resp = wodService.unenrollSchedule(idHorario);
            if (resp != null && resp.isOk()) { notifyChange(); return true; }
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public List<JsonObject> obtenerWODsPorMes(int year, int month) {
        List<JsonObject> result = new ArrayList<>();
        try {
            ApiResponse resp = wodService.getByMonth(year, month);
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(e.getAsJsonObject());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return result;
    }

    public List<JsonObject> obtenerHorariosWOD(int idWod) {
        List<JsonObject> result = new ArrayList<>();
        try {
            ApiResponse resp = wodService.getSchedulesByWod(idWod);
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(e.getAsJsonObject());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return result;
    }

    public JsonObject obtenerEstadisticasProgreso() {
        try {
            ApiResponse resp = progressService.getEstadisticas();
            if (resp.isOk() && resp.data != null && resp.data.isJsonObject()) {
                return resp.data.getAsJsonObject();
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        JsonObject fallback = new JsonObject();
        fallback.addProperty("total_ejercicios", 0);
        fallback.addProperty("promedio_marcas", 0);
        fallback.addProperty("marca_mas_alta", 0);
        return fallback;
    }

    public List<JsonObject> obtenerEjerciciosConProgreso() {
        List<JsonObject> result = new ArrayList<>();
        try {
            ApiResponse resp = progressService.getEjerciciosConProgreso();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(e.getAsJsonObject());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return result;
    }

    public JsonObject actualizarMarca(int idEjercicio, double marca) {
        try {
            ApiResponse resp = progressService.actualizarMarca(idEjercicio, marca);
            if (resp != null && resp.isOk() && resp.data != null && resp.data.isJsonObject()) {
                notifyChange();
                return resp.data.getAsJsonObject();
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return new JsonObject();
    }

    public List<JsonObject> obtenerEjercicios() {
        List<JsonObject> result = new ArrayList<>();
        try {
            ApiResponse resp = ExerciseApiService.getInstance().getAll();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(e.getAsJsonObject());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return result;
    }

    public List<Membresia> obtenerMembresiasCambio() {
        List<Membresia> result = new ArrayList<>();
        try {
            ApiResponse resp = membershipService.getAvailable();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) {
                        JsonObject json = e.getAsJsonObject();
                        Membresia m = new Membresia();
                        m.setIdMembresia(json.has("id_membresia") ? json.get("id_membresia").getAsInt() : 0);
                        m.setNombre(json.has("nombre") ? json.get("nombre").getAsString() : "");
                        m.setPrecio(json.has("precio") ? json.get("precio").getAsDouble() : 0);
                        m.setDuracionDias(json.has("duracion_dias") ? json.get("duracion_dias").getAsInt() : 30);
                        m.setActiva(true);
                        result.add(m);
                    }
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return result;
    }

    public boolean renovarMembresia(int idAtleta, int idNuevaMembresia) {
        ApiResponse resp = athleteService.updateMyMembership(idNuevaMembresia);
        if (resp != null && resp.isQueued()) {
            System.out.println("renovarMembresia: encolado sin conexion");
            return false;
        }
        if (resp != null && resp.isOk() && resp.success) notifyChange();
        return resp != null && resp.isOk() && resp.success;
    }

    public boolean cancelarMembresia() {
        try {
            ApiResponse resp = athleteService.cancelMyMembership();
            if (resp != null && resp.isOk()) { notifyChange(); return true; }
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public String getFotoPerfil() {
        try {
            ApiResponse resp = ApiService.getInstance().get(com.ironcladbox.config.ApiConfig.AUTH_PROFILE);
            if (resp.isOk() && resp.data != null && resp.data.isJsonObject()) {
                JsonObject json = resp.data.getAsJsonObject();
                if (json.has("foto_perfil") && !json.get("foto_perfil").isJsonNull()) {
                    return json.get("foto_perfil").getAsString();
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    private Clase parseClase(JsonObject json) {
        Clase c = new Clase();
        c.setIdClase(json.has("id_clase") ? json.get("id_clase").getAsInt() : 0);
        c.setNombre(json.has("nombre") ? json.get("nombre").getAsString() : "");
        c.setDescripcion(json.has("descripcion") ? json.get("descripcion").getAsString() : "");
        if (json.has("id_entrenador")) c.setIdEntrenador(json.get("id_entrenador").getAsInt());
        if (json.has("entrenador_nombre")) c.setNombreEntrenador(json.get("entrenador_nombre").getAsString());
        if (json.has("hora")) {
            try { c.setHorarioInicio(LocalTime.parse(json.get("hora").getAsString().substring(0, 5))); } catch (Exception ex) {}
        }
        if (json.has("fecha")) {
            try { c.setFecha(LocalDate.parse(json.get("fecha").getAsString().substring(0, 10))); } catch (Exception ex) {}
        }
        if (json.has("cupo_maximo")) c.setCapacidadMaxima(json.get("cupo_maximo").getAsInt());
        if (json.has("inscritos")) c.setInscritos(json.get("inscritos").getAsInt());
        c.setActiva("ACTIVA".equals(json.has("estado") ? json.get("estado").getAsString() : ""));
        return c;
    }
}
