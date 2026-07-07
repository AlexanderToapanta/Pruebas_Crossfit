package com.ironcladbox.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import com.ironcladbox.dto.ApiResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiService {
    private static ApiService instance;
    private final HttpClient httpClient;
    private final Gson gson;
    private final CacheService cacheService;
    private final SyncQueueService queueService;
    private String authToken;
    private boolean offline;

    private ApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.gson = new Gson();
        this.cacheService = CacheService.getInstance();
        this.queueService = SyncQueueService.getInstance();
        this.authToken = null;
        this.offline = false;
    }

    public static synchronized ApiService getInstance() {
        if (instance == null) instance = new ApiService();
        return instance;
    }

    public void setToken(String token) { this.authToken = token; }
    public String getToken() { return authToken; }
    public void clearToken() { this.authToken = null; }
    public boolean isAuthenticated() { return authToken != null && !authToken.isEmpty(); }
    public boolean isOffline() { return offline; }
    public int getPendingCount() { return queueService.getPendingCount(); }
    public String getBaseUrl() { return ApiConfig.BASE_URL; }

    public ApiResponse get(String url) {
        ApiResponse response = request("GET", url, null);
        if (response.isOk()) {
            cacheService.save(url, response.rawBody);
            offline = false;
        } else if (response.statusCode == 0) {
            String cached = cacheService.get(url);
            if (cached != null) {
                offline = true;
                ApiResponse cachedResp = ApiResponse.fromHttp(200, cached);
                cachedResp.extras.put("fromCache", true);
                cachedResp.extras.put("cacheAge", cacheService.getAgeMinutes(url));
                cachedResp.success = true;
                JsonObject data = new JsonObject();
                data.addProperty("message", "Datos desde cache (" + cacheService.getAgeMinutes(url) + " min de antiguedad)");
                cachedResp.message = "Datos desde cache. Sin conexion al servidor.";
                return cachedResp;
            }
        }
        return response;
    }

    public ApiResponse post(String url, JsonObject body) {
        ApiResponse resp = request("POST", url, body);
        if (resp.isOk()) {
            offline = false;
            drainQueue();
            return resp;
        }
        if (resp.statusCode == 0) {
            offline = true;
            queueService.enqueue("POST", url, body);
            return queuedResponse(201, "Guardado localmente. Se sincronizara al reconectar.");
        }
        return resp;
    }

    public ApiResponse put(String url, JsonObject body) {
        ApiResponse resp = request("PUT", url, body);
        if (resp.isOk()) {
            offline = false;
            drainQueue();
            return resp;
        }
        if (resp.statusCode == 0) {
            offline = true;
            queueService.enqueue("PUT", url, body);
            return queuedResponse(200, "Actualizado localmente. Se sincronizara al reconectar.");
        }
        return resp;
    }

    public ApiResponse patch(String url, JsonObject body) {
        ApiResponse resp = request("PATCH", url, body);
        if (resp.isOk()) {
            offline = false;
            drainQueue();
            return resp;
        }
        if (resp.statusCode == 0) {
            offline = true;
            queueService.enqueue("PATCH", url, body);
            return queuedResponse(200, "Guardado localmente. Se sincronizara al reconectar.");
        }
        return resp;
    }

    public ApiResponse delete(String url) {
        ApiResponse resp = request("DELETE", url, null);
        if (resp.isOk()) {
            offline = false;
            drainQueue();
            return resp;
        }
        if (resp.statusCode == 0) {
            offline = true;
            queueService.enqueue("DELETE", url, null);
            return queuedResponse(200, "Eliminado localmente. Se sincronizara al reconectar.");
        }
        return resp;
    }

    public void drainQueue() {
        queueService.processQueue(op -> {
            try {
                String jsonBody = op.body != null ? gson.toJson(op.body) : null;
                HttpResponse<String> response;
                if ("POST".equals(op.method)) {
                    response = sendHttp("POST", op.url, jsonBody);
                } else if ("PUT".equals(op.method)) {
                    response = sendHttp("PUT", op.url, jsonBody);
                } else if ("PATCH".equals(op.method)) {
                    response = sendHttp("PATCH", op.url, jsonBody);
                } else if ("DELETE".equals(op.method)) {
                    response = sendHttp("DELETE", op.url, null);
                } else {
                    return false;
                }
                return response.statusCode() >= 200 && response.statusCode() < 300;
            } catch (Exception e) {
                return false;
            }
        });
    }

    private HttpResponse<String> sendHttp(String method, String url, String jsonBody) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30));

        if (jsonBody != null) {
            builder.header("Content-Type", "application/json")
                   .method(method, HttpRequest.BodyPublishers.ofString(jsonBody));
        } else {
            if ("PATCH".equals(method)) {
                builder.header("Content-Type", "application/json")
                       .method(method, HttpRequest.BodyPublishers.noBody());
            } else {
                builder.method(method, HttpRequest.BodyPublishers.noBody());
            }
        }

        if (authToken != null && !authToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + authToken);
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private ApiResponse queuedResponse(int status, String msg) {
        ApiResponse resp = ApiResponse.fromHttp(status, "{\"success\":true,\"queued\":true,\"message\":\"" + msg + "\"}");
        resp.extras.put("queued", true);
        resp.success = false;
        resp.message = msg + " (sin conexion)";
        return resp;
    }

    private ApiResponse request(String method, String url, JsonObject body) {
        try {
            String jsonBody = body != null ? gson.toJson(body) : null;
            HttpResponse<String> response = sendHttp(method, url, jsonBody);
            return ApiResponse.fromHttp(response.statusCode(), response.body());
        } catch (Exception e) {
            System.err.println("ApiService." + method + " Error: " + e.getMessage());
            return ApiResponse.fromHttp(0, "{\"success\":false,\"message\":\"Error de conexion: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
