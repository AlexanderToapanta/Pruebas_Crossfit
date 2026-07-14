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
        socketService.on("wod:created", data -> notifyChange());
        socketService.on("wod:updated", data -> notifyChange());
        socketService.on("wod:deleted", data -> notifyChange());
        socketService.on("schedule:created", data -> notifyChange());
        socketService.on("schedule:cancelled", data -> notifyChange());
        socketService.on("exercise:created", data -> notifyChange());
        socketService.on("exercise:updated", data -> notifyChange());
        socketService.on("exercise:deleted", data -> notifyChange());
        socketService.on("exercise:reactivated", data -> notifyChange());
        socketService.on("progress:updated", data -> notifyChange());
        socketService.on("progress:deleted", data -> notifyChange());
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

    public boolean crearMembresia(Membresia membresia) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", membresia.getNombre());
        body.addProperty("descripcion", membresia.getDescripcion() != null ? membresia.getDescripcion() : "");
        body.addProperty("precio", membresia.getPrecio());
        body.addProperty("duracion_dias", membresia.getDuracionDias());
        body.addProperty("beneficios", membresia.getBeneficios() != null ? membresia.getBeneficios() : "");
        ApiResponse resp = membershipService.create(body);
        if (resp != null && resp.isQueued()) {
            System.out.println("crearMembresia: encolado sin conexion");
            return false;
        }
        if (resp != null && resp.isOk() && resp.success) notifyChange();
        return resp != null && resp.isOk() && resp.success;
    }

    public boolean actualizarMembresia(Membresia membresia) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", membresia.getNombre());
        body.addProperty("descripcion", membresia.getDescripcion() != null ? membresia.getDescripcion() : "");
        body.addProperty("precio", membresia.getPrecio());
        body.addProperty("duracion_dias", membresia.getDuracionDias());
        body.addProperty("beneficios", membresia.getBeneficios() != null ? membresia.getBeneficios() : "");
        body.addProperty("estado", membresia.isActiva());
        ApiResponse resp = membershipService.update(membresia.getIdMembresia(), body);
        if (resp != null && resp.isQueued()) {
            System.out.println("actualizarMembresia: encolado sin conexion");
            return false;
        }
        if (resp != null && resp.isOk() && resp.success) notifyChange();
        return resp != null && resp.isOk() && resp.success;
    }

    public boolean crearClase(Clase clase, String fecha) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", clase.getNombre());
        body.addProperty("descripcion", clase.getDescripcion() != null ? clase.getDescripcion() : "");
        body.addProperty("id_entrenador", clase.getIdEntrenador());
        if (clase.getHorarioInicio() != null) body.addProperty("hora", clase.getHorarioInicio().toString() + ":00");
        body.addProperty("cupo_maximo", clase.getCapacidadMaxima());
        body.addProperty("fecha", fecha != null ? fecha : LocalDate.now().toString());
        ApiResponse resp = classService.create(body);
        if (resp != null && resp.isQueued()) {
            System.out.println("crearClase: encolado sin conexion");
            return false;
        }
        if (resp != null && resp.isOk() && resp.success) notifyChange();
        return resp != null && resp.isOk() && resp.success;
    }

    public boolean crearClase(String nombre, String descripcion, int idEntrenador, String hora, int cupoMaximo, String fecha) {
        Clase c = new Clase();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setIdEntrenador(idEntrenador);
        c.setCapacidadMaxima(cupoMaximo);
        return crearClase(c, fecha);
    }

    public boolean crearClase(Clase clase) {
        return crearClase(clase, LocalDate.now().toString());
    }

    public boolean actualizarClase(Clase clase) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", clase.getNombre());
        body.addProperty("descripcion", clase.getDescripcion() != null ? clase.getDescripcion() : "");
        if (clase.getHorarioInicio() != null) body.addProperty("hora", clase.getHorarioInicio().toString() + ":00");
        body.addProperty("cupo_maximo", clase.getCapacidadMaxima());
        ApiResponse resp = classService.update(clase.getIdClase(), body);
        if (resp != null && resp.isQueued()) {
            System.out.println("actualizarClase: encolado sin conexion");
            return false;
        }
        if (resp != null && resp.isOk() && resp.success) notifyChange();
        return resp != null && resp.isOk() && resp.success;
    }

    public boolean eliminarClase(int idClase) {
        ApiResponse resp = classService.delete(idClase);
        if (resp != null && resp.isOk()) notifyChange();
        return resp.isOk();
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

    public boolean actualizarAtleta(Atleta atleta) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", atleta.getNombre());
        body.addProperty("apellido", atleta.getApellido());
        body.addProperty("email", atleta.getEmail());
        body.addProperty("telefono", atleta.getTelefono() != null ? atleta.getTelefono() : "");
        if (atleta.getPeso() > 0) body.addProperty("peso", atleta.getPeso());
        if (atleta.getAltura() > 0) body.addProperty("altura", atleta.getAltura());
        body.addProperty("direccion", atleta.getDireccion() != null ? atleta.getDireccion() : "");
        body.addProperty("contacto_emergencia", atleta.getContactoEmergencia() != null ? atleta.getContactoEmergencia() : "");
        if (atleta.getFechaNacimiento() != null) body.addProperty("fecha_nacimiento", atleta.getFechaNacimiento().toString());
        ApiResponse resp = athleteService.update(atleta.getIdAtleta(), body);
        if (resp != null && resp.isQueued()) {
            System.out.println("actualizarAtleta: encolado sin conexion");
            return false;
        }
        if (resp != null && resp.isOk() && resp.success) notifyChange();
        return resp != null && resp.isOk() && resp.success;
    }

    public boolean eliminarAtleta(int idAtleta) {
        ApiResponse resp = athleteService.delete(idAtleta);
        if (resp != null && resp.isOk()) notifyChange();
        return resp.isOk();
    }

    public boolean actualizarEntrenador(Entrenador entrenador) {
        JsonObject body = new JsonObject();
        body.addProperty("especialidad", entrenador.getEspecialidad() != null ? entrenador.getEspecialidad() : "");
        body.addProperty("anios_experiencia", entrenador.getExperienciaAnios());
        body.addProperty("certificaciones", entrenador.getCertificacion() != null ? entrenador.getCertificacion() : "");
        body.addProperty("biografia", entrenador.getBiografia() != null ? entrenador.getBiografia() : "");
        body.addProperty("telefono", entrenador.getTelefono() != null ? entrenador.getTelefono() : "");
        body.addProperty("direccion", entrenador.getDireccion() != null ? entrenador.getDireccion() : "");
        if (entrenador.getFechaNacimiento() != null) body.addProperty("fecha_nacimiento", entrenador.getFechaNacimiento().toString());
        ApiResponse resp = trainerService.update(entrenador.getIdEntrenador(), body);
        if (resp != null && resp.isQueued()) {
            System.out.println("actualizarEntrenador: encolado sin conexion");
            return false;
        }
        if (resp != null && resp.isOk() && resp.success) notifyChange();
        return resp != null && resp.isOk() && resp.success;
    }

    public boolean eliminarEntrenador(int idEntrenador) {
        ApiResponse resp = trainerService.delete(idEntrenador);
        if (resp != null && resp.isOk()) notifyChange();
        return resp.isOk();
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
        JsonObject body = new JsonObject();
        body.addProperty("id_atleta", idAtleta);
        body.addProperty("id_membresia", idMembresia);
        if (fechaInicio != null) body.addProperty("fecha_inicio", fechaInicio.toString());
        if (fechaFin != null) body.addProperty("fecha_fin", fechaFin.toString());
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

    private boolean safeBool(JsonObject json, String key) {
        return json.has(key) && !json.get(key).isJsonNull() && json.get(key).getAsBoolean();
    }

    private Atleta parseAtleta(JsonObject json) {
        Atleta a = new Atleta();
        a.setIdAtleta(json.has("id_atleta") && !json.get("id_atleta").isJsonNull() ? json.get("id_atleta").getAsInt() : 0);
        a.setIdUsuario(json.has("id_usuario") && !json.get("id_usuario").isJsonNull() ? json.get("id_usuario").getAsInt() : 0);
        a.setNombre(json.has("nombre") && !json.get("nombre").isJsonNull() ? json.get("nombre").getAsString() : "");
        a.setApellido(json.has("apellido") && !json.get("apellido").isJsonNull() ? json.get("apellido").getAsString() : "");
        a.setEmail(json.has("email") && !json.get("email").isJsonNull() ? json.get("email").getAsString() : "");
        a.setTelefono(json.has("telefono") && !json.get("telefono").isJsonNull() ? json.get("telefono").getAsString() : "");
        a.setRol(Rol.ATLETA);
        a.setActivo(json.has("activo") && !json.get("activo").isJsonNull() ? json.get("activo").getAsBoolean() : true);
        if (json.has("peso") && !json.get("peso").isJsonNull() && !json.get("peso").getAsString().isEmpty()) {
            try { a.setPeso(json.get("peso").getAsDouble()); } catch (Exception ignored) {}
        }
        if (json.has("altura") && !json.get("altura").isJsonNull() && !json.get("altura").getAsString().isEmpty()) {
            try { a.setAltura(json.get("altura").getAsDouble()); } catch (Exception ignored) {}
        }
        a.setIdMembresia(json.has("id_membresia") && !json.get("id_membresia").isJsonNull() ? json.get("id_membresia").getAsInt() : 0);
        a.setNombreMembresia(json.has("membresia_nombre") && !json.get("membresia_nombre").isJsonNull() ? json.get("membresia_nombre").getAsString() : null);
        a.setMembresiaActiva(json.has("id_membresia") && !json.get("id_membresia").isJsonNull() && json.get("id_membresia").getAsInt() > 0);
        if (json.has("fecha_inicio") && !json.get("fecha_inicio").isJsonNull()) {
            try { a.setFechaInicioMembresia(LocalDate.parse(json.get("fecha_inicio").getAsString().substring(0, 10))); } catch (Exception ignored) {}
        }
        if (json.has("fecha_fin") && !json.get("fecha_fin").isJsonNull()) {
            try { a.setFechaFinMembresia(LocalDate.parse(json.get("fecha_fin").getAsString().substring(0, 10))); } catch (Exception ignored) {}
        }
        a.setDireccion(json.has("direccion") && !json.get("direccion").isJsonNull() ? json.get("direccion").getAsString() : "");
        a.setContactoEmergencia(json.has("contacto_emergencia") && !json.get("contacto_emergencia").isJsonNull() ? json.get("contacto_emergencia").getAsString() : "");
        if (json.has("fecha_nacimiento") && !json.get("fecha_nacimiento").isJsonNull()) {
            try { a.setFechaNacimiento(LocalDate.parse(json.get("fecha_nacimiento").getAsString().substring(0, 10))); } catch (Exception ignored) {}
        }
        return a;
    }

    public boolean toggleEstadoAtleta(int idAtleta, boolean activo) {
        JsonObject body = new JsonObject();
        body.addProperty("activo", activo);
        ApiResponse resp = athleteService.updateStatus(idAtleta, activo);
        if (resp != null && resp.isOk()) notifyChange();
        return resp.isOk();
    }

    public boolean toggleEstadoEntrenador(int idEntrenador, boolean activo) {
        ApiResponse resp = trainerService.updateStatus(idEntrenador, activo);
        if (resp != null && resp.isOk()) notifyChange();
        return resp.isOk();
    }

    public boolean asignarMembresia(int idAtleta, int idMembresia) {
        ApiResponse resp = membershipService.assign(idAtleta, idMembresia);
        if (resp != null && resp.isOk()) notifyChange();
        return resp.isOk();
    }

    private Entrenador parseEntrenador(JsonObject json) {
        Entrenador e = new Entrenador();
        e.setIdEntrenador(json.has("id_entrenador") && !json.get("id_entrenador").isJsonNull() ? json.get("id_entrenador").getAsInt() : 0);
        e.setIdUsuario(json.has("id_usuario") && !json.get("id_usuario").isJsonNull() ? json.get("id_usuario").getAsInt() : 0);
        e.setNombre(json.has("nombre") && !json.get("nombre").isJsonNull() ? json.get("nombre").getAsString() : "");
        e.setApellido(json.has("apellido") && !json.get("apellido").isJsonNull() ? json.get("apellido").getAsString() : "");
        e.setEmail(json.has("email") && !json.get("email").isJsonNull() ? json.get("email").getAsString() : "");
        e.setTelefono(json.has("telefono") && !json.get("telefono").isJsonNull() ? json.get("telefono").getAsString() : "");
        e.setRol(Rol.ENTRENADOR);
        e.setActivo(json.has("activo") && !json.get("activo").isJsonNull() ? json.get("activo").getAsBoolean() : (json.has("estado") && !json.get("estado").isJsonNull() ? json.get("estado").getAsBoolean() : true));
        if (json.has("especialidad") && !json.get("especialidad").isJsonNull()) e.setEspecialidad(json.get("especialidad").getAsString());
        if (json.has("anios_experiencia") && !json.get("anios_experiencia").isJsonNull()) e.setExperienciaAnios(json.get("anios_experiencia").getAsInt());
        if (json.has("certificaciones") && !json.get("certificaciones").isJsonNull()) e.setCertificacion(json.get("certificaciones").getAsString());
        if (json.has("biografia") && !json.get("biografia").isJsonNull()) e.setBiografia(json.get("biografia").getAsString());
        e.setDireccion(json.has("direccion") && !json.get("direccion").isJsonNull() ? json.get("direccion").getAsString() : "");
        if (json.has("fecha_nacimiento") && !json.get("fecha_nacimiento").isJsonNull()) {
            try { e.setFechaNacimiento(LocalDate.parse(json.get("fecha_nacimiento").getAsString().substring(0, 10))); } catch (Exception ignored) {}
        }
        return e;
    }

    private Membresia parseMembresia(JsonObject json) {
        Membresia m = new Membresia();
        m.setIdMembresia(json.has("id_membresia") && !json.get("id_membresia").isJsonNull() ? json.get("id_membresia").getAsInt() : 0);
        m.setNombre(json.has("nombre") && !json.get("nombre").isJsonNull() ? json.get("nombre").getAsString() : "");
        m.setDescripcion(json.has("descripcion") && !json.get("descripcion").isJsonNull() ? json.get("descripcion").getAsString() : "");
        m.setPrecio(json.has("precio") && !json.get("precio").isJsonNull() ? json.get("precio").getAsDouble() : 0.0);
        m.setDuracionDias(json.has("duracion_dias") && !json.get("duracion_dias").isJsonNull() ? json.get("duracion_dias").getAsInt() : 30);
        m.setBeneficios(json.has("beneficios") && !json.get("beneficios").isJsonNull() ? json.get("beneficios").getAsString() : "");
        m.setActiva(json.has("estado") && !json.get("estado").isJsonNull() ? json.get("estado").getAsBoolean() : true);
        return m;
    }

    private Clase parseClase(JsonObject json) {
        Clase c = new Clase();
        c.setIdClase(json.has("id_clase") && !json.get("id_clase").isJsonNull() ? json.get("id_clase").getAsInt() : 0);
        c.setNombre(json.has("nombre") && !json.get("nombre").isJsonNull() ? json.get("nombre").getAsString() : "");
        c.setDescripcion(json.has("descripcion") && !json.get("descripcion").isJsonNull() ? json.get("descripcion").getAsString() : "");
        if (json.has("id_entrenador") && !json.get("id_entrenador").isJsonNull()) c.setIdEntrenador(json.get("id_entrenador").getAsInt());
        if (json.has("entrenador_nombre") && !json.get("entrenador_nombre").isJsonNull()) c.setNombreEntrenador(json.get("entrenador_nombre").getAsString());
        if (json.has("hora") && !json.get("hora").isJsonNull()) {
            try { c.setHorarioInicio(LocalTime.parse(json.get("hora").getAsString().substring(0, 5))); } catch (Exception ex) {}
        }
        if (json.has("capacidad_maxima") && !json.get("capacidad_maxima").isJsonNull()) c.setCapacidadMaxima(json.get("capacidad_maxima").getAsInt());
        if (json.has("cupo_maximo") && !json.get("cupo_maximo").isJsonNull()) c.setCapacidadMaxima(json.get("cupo_maximo").getAsInt());
        c.setActiva(!json.has("estado") || json.get("estado").isJsonNull() || "ACTIVA".equals(json.get("estado").getAsString()));
        return c;
    }
}
