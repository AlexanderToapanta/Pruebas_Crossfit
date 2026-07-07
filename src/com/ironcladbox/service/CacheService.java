package com.ironcladbox.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.*;

public class CacheService {
    private static CacheService instance;
    private final Path cacheDir;
    private final Gson gson;

    private CacheService() {
        this.gson = new Gson();
        String appData = System.getenv("APPDATA");
        if (appData == null) appData = System.getProperty("user.home");
        this.cacheDir = Paths.get(appData, "IroncladBox", "cache");
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            System.err.println("CacheService: No se pudo crear directorio cache: " + e.getMessage());
        }
    }

    public static synchronized CacheService getInstance() {
        if (instance == null) instance = new CacheService();
        return instance;
    }

    public void save(String key, String jsonData) {
        try {
            JsonObject envelope = new JsonObject();
            envelope.addProperty("timestamp", System.currentTimeMillis());
            envelope.addProperty("data", jsonData);
            Path file = cacheDir.resolve(sanitizeKey(key) + ".json");
            Files.writeString(file, gson.toJson(envelope));
        } catch (IOException e) {
            System.err.println("CacheService.save Error: " + e.getMessage());
        }
    }

    public String get(String key) {
        try {
            Path file = cacheDir.resolve(sanitizeKey(key) + ".json");
            if (!Files.exists(file)) return null;
            String content = Files.readString(file);
            JsonObject envelope = gson.fromJson(content, JsonObject.class);
            if (envelope.has("data")) return envelope.get("data").getAsString();
        } catch (Exception e) {
            System.err.println("CacheService.get Error: " + e.getMessage());
        }
        return null;
    }

    public int getAgeMinutes(String key) {
        try {
            Path file = cacheDir.resolve(sanitizeKey(key) + ".json");
            if (!Files.exists(file)) return 999999;
            String content = Files.readString(file);
            JsonObject envelope = gson.fromJson(content, JsonObject.class);
            long ts = envelope.has("timestamp") ? envelope.get("timestamp").getAsLong() : 0;
            long ageMs = System.currentTimeMillis() - ts;
            return (int)(ageMs / (1000 * 60));
        } catch (Exception e) {
            System.err.println("CacheService.getAgeMinutes Error: " + e.getMessage());
            return 999999;
        }
    }

    public void clear() {
        try {
            Files.walk(cacheDir)
                .filter(Files::isRegularFile)
                .forEach(f -> {
                    try { Files.delete(f); } catch (IOException e) {
                        System.err.println("CacheService.clear: No se pudo eliminar " + f + ": " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            System.err.println("CacheService.clear Error: " + e.getMessage());
        }
    }

    private String sanitizeKey(String key) {
        return key.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}
