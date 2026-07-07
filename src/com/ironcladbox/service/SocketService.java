package com.ironcladbox.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class SocketService {
    private static SocketService instance;

    private final HttpClient httpClient;
    private final Gson gson;
    private WebSocket webSocket;
    private final Map<String, List<Consumer<JsonElement>>> listeners;
    private final ScheduledExecutorService pingExecutor;
    private ScheduledFuture<?> pingFuture;
    private boolean connected;
    private String token;
    private int pingInterval = 25000;
    private int connectCount = 0;
    private Runnable onReconnected;
    private Consumer<Boolean> onConnectionChange;

    private SocketService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.listeners = new ConcurrentHashMap<>();
        this.pingExecutor = Executors.newSingleThreadScheduledExecutor();
        this.connected = false;
    }

    public static synchronized SocketService getInstance() {
        if (instance == null) {
            instance = new SocketService();
        }
        return instance;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setOnConnectionChange(Consumer<Boolean> callback) {
        this.onConnectionChange = callback;
    }

    public void setOnReconnected(Runnable callback) {
        this.onReconnected = callback;
    }

    public void connect() {
        if (connected) return;
        if (token == null || token.isEmpty()) return;

        try {
            String wsUrl = ApiService.getInstance().getBaseUrl()
                    .replace("http://", "ws://")
                    .replace("https://", "wss://")
                    + "/socket.io/?EIO=4&transport=websocket";

            CompletableFuture<WebSocket> wsFuture = httpClient.newWebSocketBuilder()
                    .buildAsync(URI.create(wsUrl), new SocketListener());

            webSocket = wsFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Socket: Error al conectar: " + e.getMessage());
            connected = false;
            notifyConnectionChange();
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            try {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "");
            } catch (Exception ignored) {}
            webSocket = null;
        }
        if (pingFuture != null) {
            pingFuture.cancel(false);
        }
        connected = false;
        notifyConnectionChange();
    }

    public void on(String event, Consumer<JsonElement> callback) {
        listeners.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>()).add(callback);
    }

    public void off(String event) {
        listeners.remove(event);
    }

    private void notifyConnectionChange() {
        if (onConnectionChange != null) {
            onConnectionChange.accept(connected);
        }
    }

    private void startPing() {
        if (pingFuture != null) pingFuture.cancel(false);
        pingFuture = pingExecutor.scheduleAtFixedRate(() -> {
            try {
                if (webSocket != null && connected) {
                    webSocket.sendText("3", true);
                }
            } catch (Exception ignored) {}
        }, pingInterval, pingInterval, TimeUnit.MILLISECONDS);
    }

    private class SocketListener implements WebSocket.Listener {
        private StringBuilder buffer = new StringBuilder();

        @Override
        public void onOpen(WebSocket ws) {
            connected = true;
            connectCount++;
            if (connectCount > 1) {
                ApiService.getInstance().drainQueue();
                if (onReconnected != null) onReconnected.run();
            }
            notifyConnectionChange();
            ws.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                String message = buffer.toString();
                buffer = new StringBuilder();
                handleMessage(message);
            }
            ws.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
            connected = false;
            notifyConnectionChange();
            if (pingFuture != null) pingFuture.cancel(false);
            return null;
        }

        @Override
        public void onError(WebSocket ws, Throwable error) {
            System.err.println("Socket error: " + error.getMessage());
            connected = false;
            notifyConnectionChange();
        }

        private void handleMessage(String message) {
            if (message == null || message.isEmpty()) return;

            try {
                char type = message.charAt(0);

                switch (type) {
                    case '0': // OPEN - handshake
                        String json = message.substring(1);
                        JsonObject handshake = gson.fromJson(json, JsonObject.class);
                        if (handshake.has("pingInterval")) {
                            pingInterval = handshake.get("pingInterval").getAsInt();
                        }
                        // Send auth
                        JsonObject auth = new JsonObject();
                        auth.addProperty("token", token);
                        webSocket.sendText("40" + gson.toJson(auth), true);
                        startPing();
                        break;

                    case '2': // PING
                        webSocket.sendText("3", true);
                        break;

                    case '3': // PONG - ignore
                        break;

                    case '4': // MESSAGE
                        String msgData = message.substring(1);
                        if (msgData.startsWith("0")) {
                            // Auth OK (40 response)
                            break;
                        }
                        if (msgData.startsWith("4")) {
                            // Error (44 response)
                            System.err.println("Socket auth error: " + msgData.substring(1));
                            break;
                        }
                        // Event: 42["event", {...}]
                        try {
                            JsonArray arr = gson.fromJson(msgData, JsonArray.class);
                            if (arr.size() >= 2) {
                                String eventName = arr.get(0).getAsString();
                                JsonElement eventData = arr.get(1);
                                dispatchEvent(eventName, eventData);
                            }
                        } catch (Exception ex) {
                            // Not a JSON array event, ignore
                        }
                        break;
                }
            } catch (Exception e) {
                // Ignore parse errors for individual messages
            }
        }

        private void dispatchEvent(String event, JsonElement data) {
            List<Consumer<JsonElement>> callbacks = listeners.get(event);
            if (callbacks != null) {
                for (Consumer<JsonElement> cb : callbacks) {
                    try {
                        cb.accept(data);
                    } catch (Exception ignored) {}
                }
            }
        }
    }
}
