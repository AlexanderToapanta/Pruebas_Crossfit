package com.ironcladbox.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;
import java.util.HashMap;

public class ApiResponse {
    public boolean success;
    public String message;
    public JsonElement data;
    public int statusCode;
    public String rawBody;
    public Map<String, Object> extras;

    public ApiResponse() {
        extras = new HashMap<>();
    }

    public static ApiResponse fromHttp(int statusCode, String body) {
        ApiResponse resp = new ApiResponse();
        resp.statusCode = statusCode;
        resp.rawBody = body;

        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            resp.success = json.has("success") && json.get("success").getAsBoolean();
            resp.message = json.has("message") ? json.get("message").getAsString() : "";
            if (json.has("data")) {
                resp.data = json.get("data");
            }
            if (json.has("token")) {
                resp.extras.put("token", json.get("token").getAsString());
            }
            if (json.has("user")) {
                resp.extras.put("user", json.get("user"));
            }
            if (json.has("membershipExpired")) {
                resp.extras.put("membershipExpired", json.get("membershipExpired").getAsBoolean());
            }
            if (json.has("sessionExpired")) {
                resp.extras.put("sessionExpired", json.get("sessionExpired").getAsBoolean());
            }
        } catch (Exception e) {
            resp.success = false;
            resp.message = "Error al parsear respuesta: " + e.getMessage();
        }

        return resp;
    }

    public boolean isOk() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean isAuthError() {
        return statusCode == 401 || statusCode == 403;
    }

    public String getStringField(String field) {
        if (data == null || !data.isJsonObject()) return null;
        JsonObject obj = data.getAsJsonObject();
        return obj.has(field) ? obj.get(field).getAsString() : null;
    }

    public int getIntField(String field) {
        if (data == null || !data.isJsonObject()) return 0;
        JsonObject obj = data.getAsJsonObject();
        return obj.has(field) ? obj.get(field).getAsInt() : 0;
    }

    public double getDoubleField(String field) {
        if (data == null || !data.isJsonObject()) return 0.0;
        JsonObject obj = data.getAsJsonObject();
        return obj.has(field) ? obj.get(field).getAsDouble() : 0.0;
    }

    public boolean getBooleanField(String field) {
        if (data == null || !data.isJsonObject()) return false;
        JsonObject obj = data.getAsJsonObject();
        return obj.has(field) && obj.get(field).getAsBoolean();
    }

    public boolean isFromCache() {
        return extras.containsKey("fromCache") && Boolean.TRUE.equals(extras.get("fromCache"));
    }

    public int getCacheAgeMinutes() {
        Object age = extras.get("cacheAge");
        return age instanceof Integer ? (Integer) age : 0;
    }
}
