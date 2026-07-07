package com.ironcladbox.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcladbox.model.*;
import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorController {
    private final ClassApiService classService;
    private final TrainerApiService trainerService;
    private final WodApiService wodService;
    private final SocketService socketService;
    private Runnable onDataChanged;

    public EntrenadorController() {
        this.classService = ClassApiService.getInstance();
        this.trainerService = TrainerApiService.getInstance();
        this.wodService = WodApiService.getInstance();
        this.socketService = SocketService.getInstance();

        socketService.on("class:created", data -> notifyChange());
        socketService.on("class:updated", data -> notifyChange());
        socketService.on("class:deleted", data -> notifyChange());
        socketService.on("class:enrollment_created", data -> notifyChange());
        socketService.on("class:enrollment_deleted", data -> notifyChange());
        socketService.on("wod:created", data -> notifyChange());
        socketService.on("wod:updated", data -> notifyChange());
        socketService.on("wod:deleted", data -> notifyChange());
        socketService.on("schedule:created", data -> notifyChange());
        socketService.on("enrollment:created", data -> notifyChange());
        socketService.on("enrollment:deleted", data -> notifyChange());
        socketService.on("attendance:marked", data -> notifyChange());
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

    public List<Clase> obtenerMisClases(int idEntrenador) {
        List<Clase> result = new ArrayList<>();
        try {
            ApiResponse resp = classService.getEnrolledStudents(idEntrenador);
            if (!resp.isOk()) {
                resp = trainerService.getMyClasses();
            }
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

    public void registrarAsistencia(int idAtleta, int idClase, boolean presente) {
    }

    public List<Asistencia> obtenerAsistenciasPorClase(int idClase) {
        return new ArrayList<>();
    }

    public void crearClase(Clase clase) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", clase.getNombre());
        body.addProperty("descripcion", clase.getDescripcion() != null ? clase.getDescripcion() : "");
        body.addProperty("id_entrenador", clase.getIdEntrenador());
        if (clase.getHorarioInicio() != null) body.addProperty("hora", clase.getHorarioInicio().toString() + ":00");
        body.addProperty("cupo_maximo", clase.getCapacidadMaxima());
        body.addProperty("fecha", LocalDate.now().toString());
        classService.create(body);
    }

    public void actualizarClase(Clase clase) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", clase.getNombre());
        body.addProperty("descripcion", clase.getDescripcion() != null ? clase.getDescripcion() : "");
        if (clase.getHorarioInicio() != null) body.addProperty("hora", clase.getHorarioInicio().toString() + ":00");
        body.addProperty("cupo_maximo", clase.getCapacidadMaxima());
        classService.update(clase.getIdClase(), body);
    }

    public void eliminarClase(int idClase) {
        classService.delete(idClase);
    }

    public List<Clase> obtenerClasesPorDia(String dia) {
        List<Clase> todas = obtenerMisClases(0);
        List<Clase> filtradas = new ArrayList<>();
        for (Clase c : todas) {
            if (c.getDiaSemana() != null && c.getDiaSemana().equalsIgnoreCase(dia)) {
                filtradas.add(c);
            }
        }
        return filtradas;
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
        if (json.has("cupo_maximo")) c.setCapacidadMaxima(json.get("cupo_maximo").getAsInt());
        c.setActiva("ACTIVA".equals(json.has("estado") ? json.get("estado").getAsString() : ""));
        return c;
    }
}
