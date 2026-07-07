package com.ironcladbox.controller;

import com.google.gson.JsonObject;
import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Rol;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.model.Entrenador;
import com.ironcladbox.service.ApiService;
import com.ironcladbox.service.AuthApiService;
import com.ironcladbox.service.SocketService;
import com.ironcladbox.dto.ApiResponse;

public class AuthController {
    private static AuthController instance;
    private Usuario usuarioActual;
    private final AuthApiService authService;
    private final ApiService apiService;

    private AuthController() {
        this.authService = AuthApiService.getInstance();
        this.apiService = ApiService.getInstance();
    }

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    public boolean login(String email, String contrasena) {
        try {
            ApiResponse response = authService.login(email, contrasena);
            if (!response.isOk() || !response.success) {
                return false;
            }

            if (response.data != null && response.data.isJsonObject()) {
                JsonObject data = response.data.getAsJsonObject();
                if (data.has("token")) {
                    String token = data.get("token").getAsString();
                    apiService.setToken(token);
                    SocketService.getInstance().setToken(token);
                    SocketService.getInstance().connect();
                }

                if (data.has("user")) {
                    JsonObject userJson = data.getAsJsonObject("user");
                    String rolStr = userJson.has("rol") ? userJson.get("rol").getAsString() : "";
                    Rol rol = Rol.fromString(rolStr);

                    Usuario user = new Usuario();
                    user.setIdUsuario(userJson.has("id_usuario") ? userJson.get("id_usuario").getAsInt() : 0);
                    user.setEmail(userJson.has("email") ? userJson.get("email").getAsString() : email);
                    user.setNombre(userJson.has("nombre") ? userJson.get("nombre").getAsString() : "");
                    user.setApellido(userJson.has("apellido") ? userJson.get("apellido").getAsString() : "");
                    user.setRol(rol);
                    user.setActivo(true);

                    if (rol == Rol.ATLETA) {
                        Atleta atleta = new Atleta();
                        atleta.setIdUsuario(user.getIdUsuario());
                        atleta.setEmail(user.getEmail());
                        atleta.setNombre(user.getNombre());
                        atleta.setApellido(user.getApellido());
                        atleta.setRol(Rol.ATLETA);
                        atleta.setActivo(true);
                        if (userJson.has("profile")) {
                            JsonObject profile = userJson.getAsJsonObject("profile");
                            if (profile.has("id_atleta")) atleta.setIdAtleta(profile.get("id_atleta").getAsInt());
                        }
                        this.usuarioActual = atleta;
                    } else if (rol == Rol.ENTRENADOR) {
                        Entrenador entrenador = new Entrenador();
                        entrenador.setIdUsuario(user.getIdUsuario());
                        entrenador.setEmail(user.getEmail());
                        entrenador.setNombre(user.getNombre());
                        entrenador.setApellido(user.getApellido());
                        entrenador.setRol(Rol.ENTRENADOR);
                        entrenador.setActivo(true);
                        if (userJson.has("profile")) {
                            JsonObject profile = userJson.getAsJsonObject("profile");
                            if (profile.has("id_entrenador")) entrenador.setIdEntrenador(profile.get("id_entrenador").getAsInt());
                        }
                        this.usuarioActual = entrenador;
                    } else {
                        user.setRol(rol);
                        this.usuarioActual = user;
                    }
                }
                return this.usuarioActual != null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registrarAtleta(Atleta atleta) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("nombre", atleta.getNombre());
            body.addProperty("apellido", atleta.getApellido());
            body.addProperty("email", atleta.getEmail());
            body.addProperty("password", atleta.getContrasena());
            body.addProperty("telefono", atleta.getTelefono() != null ? atleta.getTelefono() : "");
            body.addProperty("rol", "ATLETA");
            if (atleta.getPeso() > 0) body.addProperty("peso", atleta.getPeso());
            if (atleta.getAltura() > 0) body.addProperty("altura", atleta.getAltura());

            ApiResponse response = authService.register(body);
            return response.isOk() && response.success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        try {
            authService.logout();
        } catch (Exception e) {
            // ignore logout errors
        }
        SocketService.getInstance().disconnect();
        apiService.clearToken();
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Rol getRolActual() {
        return usuarioActual != null ? usuarioActual.getRol() : null;
    }

    public boolean estaAutenticado() {
        return usuarioActual != null && apiService.isAuthenticated();
    }

    public boolean esAdministrador() {
        return estaAutenticado() && usuarioActual.getRol() == Rol.ADMINISTRADOR;
    }

    public boolean esEntrenador() {
        return estaAutenticado() && usuarioActual.getRol() == Rol.ENTRENADOR;
    }

    public boolean esAtleta() {
        return estaAutenticado() && usuarioActual.getRol() == Rol.ATLETA;
    }
}
