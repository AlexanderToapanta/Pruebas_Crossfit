package com.ironcladbox.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcladbox.model.*;
import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    private final AthleteApiService athleteService;
    private final TrainerApiService trainerService;
    private final MembershipApiService membershipService;
    private final ClassApiService classService;
    private final SocketService socketService;
    private Runnable onDataChanged;

    public AdminController() {
        this.athleteService = AthleteApiService.getInstance();
        this.trainerService = TrainerApiService.getInstance();
        this.membershipService = MembershipApiService.getInstance();
        this.classService = ClassApiService.getInstance();
        this.socketService = SocketService.getInstance();

        socketService.on("athlete:created", data -> notifyChange());
        socketService.on("athlete:status_updated", data -> notifyChange());
        socketService.on("athlete:deleted", data -> notifyChange());
        socketService.on("trainer:created", data -> notifyChange());
        socketService.on("trainer:status_updated", data -> notifyChange());
        socketService.on("trainer:deleted", data -> notifyChange());
        socketService.on("membership:created", data -> notifyChange());
        socketService.on("membership:updated", data -> notifyChange());
        socketService.on("membership:deleted", data -> notifyChange());
        socketService.on("membership:assigned", data -> notifyChange());
        socketService.on("membership:expired", data -> notifyChange());
        socketService.on("class:created", data -> notifyChange());
        socketService.on("class:updated", data -> notifyChange());
        socketService.on("class:deleted", data -> notifyChange());
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

    public List<Usuario> obtenerTodosUsuarios() {
        return new ArrayList<>();
    }

    public List<Atleta> obtenerTodosAtletas() {
        List<Atleta> result = new ArrayList<>();
        try {
            ApiResponse resp = athleteService.getAll();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(parseAtleta(e.getAsJsonObject()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<Entrenador> obtenerTodosEntrenadores() {
        List<Entrenador> result = new ArrayList<>();
        try {
            ApiResponse resp = trainerService.getAll();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(parseEntrenador(e.getAsJsonObject()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<Membresia> obtenerMembresias() {
        List<Membresia> result = new ArrayList<>();
        try {
            ApiResponse resp = membershipService.getAll();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) result.add(parseMembresia(e.getAsJsonObject()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<Clase> obtenerClases() {
        List<Clase> result = new ArrayList<>();
        try {
            ApiResponse resp = classService.getAll();
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

    public void crearMembresia(Membresia membresia) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", membresia.getNombre());
        body.addProperty("descripcion", membresia.getDescripcion() != null ? membresia.getDescripcion() : "");
        body.addProperty("precio", membresia.getPrecio());
        body.addProperty("duracion_dias", membresia.getDuracionDias());
        body.addProperty("beneficios", membresia.getBeneficios() != null ? membresia.getBeneficios() : "");
        membershipService.create(body);
    }

    public void actualizarMembresia(Membresia membresia) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", membresia.getNombre());
        body.addProperty("descripcion", membresia.getDescripcion() != null ? membresia.getDescripcion() : "");
        body.addProperty("precio", membresia.getPrecio());
        body.addProperty("duracion_dias", membresia.getDuracionDias());
        body.addProperty("beneficios", membresia.getBeneficios() != null ? membresia.getBeneficios() : "");
        body.addProperty("estado", membresia.isActiva());
        membershipService.update(membresia.getIdMembresia(), body);
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

    public void desactivarUsuario(int idUsuario) {
        // Buscar atleta por id_usuario y desactivar
        List<Atleta> atletas = obtenerTodosAtletas();
        for (Atleta a : atletas) {
            if (a.getIdUsuario() == idUsuario) {
                athleteService.updateStatus(a.getIdAtleta(), false);
                return;
            }
        }
    }

    public void actualizarAtleta(Atleta atleta) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", atleta.getNombre());
        body.addProperty("apellido", atleta.getApellido());
        body.addProperty("email", atleta.getEmail());
        body.addProperty("telefono", atleta.getTelefono() != null ? atleta.getTelefono() : "");
        athleteService.update(atleta.getIdAtleta(), body);
    }

    public void eliminarAtleta(int idAtleta) {
        athleteService.delete(idAtleta);
    }

    public void actualizarEntrenador(Entrenador entrenador) {
        JsonObject body = new JsonObject();
        body.addProperty("especialidad", entrenador.getEspecialidad() != null ? entrenador.getEspecialidad() : "");
        body.addProperty("anios_experiencia", entrenador.getExperienciaAnios());
        body.addProperty("certificaciones", entrenador.getCertificacion() != null ? entrenador.getCertificacion() : "");
        trainerService.update(entrenador.getIdEntrenador(), body);
    }

    public void eliminarEntrenador(int idEntrenador) {
        trainerService.delete(idEntrenador);
    }

    public int getTotalAtletas() {
        return obtenerTodosAtletas().size();
    }

    public int getTotalEntrenadores() {
        return obtenerTodosEntrenadores().size();
    }

    public int getTotalClases() {
        return obtenerClases().size();
    }

    public List<Suscripcion> obtenerTodasLasSuscripciones() {
        return new ArrayList<>();
    }

    public List<Suscripcion> obtenerSuscripcionesDeAtleta(int idAtleta) {
        return new ArrayList<>();
    }

    public Suscripcion obtenerSuscripcionActivaDeAtleta(int idAtleta) {
        return null;
    }

    public void crearSuscripcion(int idAtleta, int idMembresia, LocalDate fechaInicio, LocalDate fechaFin) {
        membershipService.assign(idAtleta, idMembresia);
    }

    public void actualizarSuscripcion(Suscripcion suscripcion) {
        membershipService.assign(suscripcion.getIdAtleta(), suscripcion.getIdMembresia());
    }

    public void revocarSuscripcion(int idSuscripcion) {
    }

    public void suspenderSuscripcion(int idSuscripcion, LocalDate nuevaFechaFin) {
    }

    public int getTotalSuscripciones() {
        return 0;
    }

    public int getTotalMembresias() {
        return obtenerMembresias().stream().filter(Membresia::isActiva).toList().size();
    }

    private Atleta parseAtleta(JsonObject json) {
        Atleta a = new Atleta();
        a.setIdAtleta(json.has("id_atleta") ? json.get("id_atleta").getAsInt() : 0);
        a.setIdUsuario(json.has("id_usuario") ? json.get("id_usuario").getAsInt() : 0);
        a.setNombre(json.has("nombre") ? json.get("nombre").getAsString() : "");
        a.setApellido(json.has("apellido") ? json.get("apellido").getAsString() : "");
        a.setEmail(json.has("email") ? json.get("email").getAsString() : "");
        a.setTelefono(json.has("telefono") ? json.get("telefono").getAsString() : "");
        a.setRol(Rol.ATLETA);
        a.setActivo(json.has("estado") ? json.get("estado").getAsBoolean() : true);
        if (json.has("peso")) a.setPeso(json.get("peso").getAsDouble());
        if (json.has("altura")) a.setAltura(json.get("altura").getAsDouble());
        return a;
    }

    private Entrenador parseEntrenador(JsonObject json) {
        Entrenador e = new Entrenador();
        e.setIdEntrenador(json.has("id_entrenador") ? json.get("id_entrenador").getAsInt() : 0);
        e.setIdUsuario(json.has("id_usuario") ? json.get("id_usuario").getAsInt() : 0);
        e.setNombre(json.has("nombre") ? json.get("nombre").getAsString() : "");
        e.setApellido(json.has("apellido") ? json.get("apellido").getAsString() : "");
        e.setEmail(json.has("email") ? json.get("email").getAsString() : "");
        e.setTelefono(json.has("telefono") ? json.get("telefono").getAsString() : "");
        e.setRol(Rol.ENTRENADOR);
        e.setActivo(json.has("estado") ? json.get("estado").getAsBoolean() : true);
        if (json.has("especialidad")) e.setEspecialidad(json.get("especialidad").getAsString());
        if (json.has("anios_experiencia")) e.setExperienciaAnios(json.get("anios_experiencia").getAsInt());
        if (json.has("certificaciones")) e.setCertificacion(json.get("certificaciones").getAsString());
        return e;
    }

    private Membresia parseMembresia(JsonObject json) {
        Membresia m = new Membresia();
        m.setIdMembresia(json.has("id_membresia") ? json.get("id_membresia").getAsInt() : 0);
        m.setNombre(json.has("nombre") ? json.get("nombre").getAsString() : "");
        m.setDescripcion(json.has("descripcion") ? json.get("descripcion").getAsString() : "");
        m.setPrecio(json.has("precio") ? json.get("precio").getAsDouble() : 0.0);
        m.setDuracionDias(json.has("duracion_dias") ? json.get("duracion_dias").getAsInt() : 30);
        m.setBeneficios(json.has("beneficios") ? json.get("beneficios").getAsString() : "");
        m.setActiva(json.has("estado") ? json.get("estado").getAsBoolean() : true);
        return m;
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
        if (json.has("capacidad_maxima")) c.setCapacidadMaxima(json.get("capacidad_maxima").getAsInt());
        if (json.has("cupo_maximo")) c.setCapacidadMaxima(json.get("cupo_maximo").getAsInt());
        c.setActiva("ACTIVA".equals(json.has("estado") ? json.get("estado").getAsString() : "ACTIVA"));
        return c;
    }
}
