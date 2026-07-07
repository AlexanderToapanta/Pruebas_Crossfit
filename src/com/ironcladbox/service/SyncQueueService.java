package com.ironcladbox.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class SyncQueueService {
    private static SyncQueueService instance;
    private final List<PendingOperation> queue;
    private final Path queueFile;
    private final Gson gson;

    private SyncQueueService() {
        this.gson = new Gson();
        this.queue = new ArrayList<>();
        String appData = System.getenv("APPDATA");
        if (appData == null) appData = System.getProperty("user.home");
        this.queueFile = Paths.get(appData, "IroncladBox", "pending_queue.json");
        try {
            Files.createDirectories(queueFile.getParent());
            loadFromDisk();
        } catch (IOException ignored) {}
    }

    public static synchronized SyncQueueService getInstance() {
        if (instance == null) instance = new SyncQueueService();
        return instance;
    }

    public int getPendingCount() {
        return queue.size();
    }

    public List<PendingOperation> getPendingOperations() {
        return new ArrayList<>(queue);
    }

    public void enqueue(String method, String url, JsonObject body) {
        queue.add(new PendingOperation(method, url, body, System.currentTimeMillis()));
        saveToDisk();
    }

    public boolean processQueue(java.util.function.Function<PendingOperation, Boolean> executor) {
        if (queue.isEmpty()) return true;

        List<PendingOperation> failed = new ArrayList<>();
        for (PendingOperation op : new ArrayList<>(queue)) {
            try {
                boolean ok = executor.apply(op);
                if (!ok) failed.add(op);
            } catch (Exception e) {
                failed.add(op);
            }
        }

        queue.clear();
        if (!failed.isEmpty()) {
            queue.addAll(failed);
            saveToDisk();
            return false;
        }

        saveToDisk();
        return true;
    }

    public void clear() {
        queue.clear();
        saveToDisk();
    }

    private void saveToDisk() {
        try {
            JsonArray arr = new JsonArray();
            for (PendingOperation op : queue) {
                JsonObject json = new JsonObject();
                json.addProperty("method", op.method);
                json.addProperty("url", op.url);
                json.add("body", op.body);
                json.addProperty("timestamp", op.timestamp);
                arr.add(json);
            }
            Files.writeString(queueFile, gson.toJson(arr));
        } catch (IOException ignored) {}
    }

    private void loadFromDisk() {
        try {
            if (!Files.exists(queueFile)) return;
            String content = Files.readString(queueFile);
            JsonArray arr = gson.fromJson(content, JsonArray.class);
            for (int i = 0; i < arr.size(); i++) {
                JsonObject json = arr.get(i).getAsJsonObject();
                queue.add(new PendingOperation(
                    json.get("method").getAsString(),
                    json.get("url").getAsString(),
                    json.has("body") && !json.get("body").isJsonNull() ? json.getAsJsonObject("body") : null,
                    json.get("timestamp").getAsLong()
                ));
            }
        } catch (Exception ignored) {}
    }

    public static class PendingOperation {
        public final String method;
        public final String url;
        public final JsonObject body;
        public final long timestamp;

        public PendingOperation(String method, String url, JsonObject body, long timestamp) {
            this.method = method;
            this.url = url;
            this.body = body;
            this.timestamp = timestamp;
        }
    }
}
