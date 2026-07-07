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

public class AtletaController {
    private final ClassApiService classService;
    private final AthleteApiService athleteService;
    private final MembershipApiService membershipService;
    private final WodApiService wodService;
    private final SocketService socketService;
    private Runnable onDataChanged;

    public AtletaController() {
        this.classService = ClassApiService.getInstance();
        this.athleteService = AthleteApiService.getInstance();
        this.membershipService = MembershipApiService.getInstance();
        this.wodService = WodApiService.getInstance();
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
        socketService.on("enrollment:created", data -> notifyChange());
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

    public Suscripcion obtenerSuscripcionActiva(int idAtleta) {
        try {
            ApiResponse resp = athleteService.getMyMembership();
            if (resp.isOk() && resp.data != null && resp.data.isJsonObject()) {
                JsonObject json = resp.data.getAsJsonObject();
                Suscripcion s = new Suscripcion();
                s.setIdAtleta(idAtleta);
                if (json.has("id_membresia")) s.setIdMembresia(json.get("id_membresia").getAsInt());
                if (json.has("nombre_membresia")) s.setNombreMembresia(json.get("nombre_membresia").getAsString());
                if (json.has("precio")) s.setPrecioMembresia(json.get("precio").getAsDouble());
                if (json.has("fecha_inicio_membresia")) {
                    try { s.setFechaInicio(LocalDate.parse(json.get("fecha_inicio_membresia").getAsString())); } catch (Exception ex) {}
                }
                if (json.has("fecha_fin_membresia")) {
                    try { s.setFechaFin(LocalDate.parse(json.get("fecha_fin_membresia").getAsString())); } catch (Exception ex) {}
                }
                s.setActiva(true);
                return s;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Asistencia> obtenerHistorialAsistencia(int idAtleta) {
        return new ArrayList<>();
    }

    public void registrarAsistencia(int idAtleta, int idClase) {
    }

    public double calcularPorcentajeAsistencia(int idAtleta) {
        return 0;
    }

    public Membresia obtenerMembresiaActiva(int idAtleta) {
        return null;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void renovarMembresia(int idAtleta, int idNuevaMembresia) {
        athleteService.updateMyMembership(idNuevaMembresia);
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
