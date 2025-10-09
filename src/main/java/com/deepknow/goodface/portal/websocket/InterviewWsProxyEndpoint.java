package com.deepknow.goodface.portal.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * WebSocket 代理端点：前端连接到 portal 的该端点，portal 作为客户端直连 interview 的 /audio/stream 并进行双向转发。
 */
@Component
@ServerEndpoint(value = "/ws/interview/audio/stream")
public class InterviewWsProxyEndpoint {
    private static final Logger log = LoggerFactory.getLogger(InterviewWsProxyEndpoint.class);

    private Session clientSession; // 与前端浏览器的会话
    private WebSocket remote;      // 与 interview 的远端会话

    private final AtomicBoolean remoteOpen = new AtomicBoolean(false);
    private final Map<String, String> queryParams = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        this.clientSession = session;
        String query = session.getRequestURI() != null ? session.getRequestURI().getQuery() : null;
        parseQueryParams(query);
        String sessionId = queryParams.getOrDefault("sessionId", "sess_" + System.currentTimeMillis());

        // 连接到 interview 的真实 WS 端点
        String remoteUrl = "ws://127.0.0.1:8003/audio/stream?sessionId=" + sessionId;
        HttpClient httpClient = HttpClient.newHttpClient();
        CompletableFuture<WebSocket> future = httpClient.newWebSocketBuilder()
                .buildAsync(URI.create(remoteUrl), new WebSocket.Listener() {
                    @Override
                    public void onOpen(WebSocket webSocket) {
                        remoteOpen.set(true);
                        webSocket.request(1);
                        log.info("Proxy connected to interview: {}", remoteUrl);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        try {
                            if (clientSession != null && clientSession.isOpen()) {
                                clientSession.getAsyncRemote().sendText(data.toString());
                            }
                        } catch (Exception e) {
                            log.warn("Proxy forward text failed: {}", e.getMessage());
                        }
                        webSocket.request(1);
                        return CompletableFuture.completedFuture(null);
                    }

                    @Override
                    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                        try {
                            if (clientSession != null && clientSession.isOpen()) {
                                clientSession.getAsyncRemote().sendBinary(data);
                            }
                        } catch (Exception e) {
                            log.warn("Proxy forward binary failed: {}", e.getMessage());
                        }
                        webSocket.request(1);
                        return CompletableFuture.completedFuture(null);
                    }

                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        log.warn("Proxy remote error: {}", error.getMessage(), error);
                        safeCloseClient(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Remote error"));
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                        log.info("Proxy remote closed: {} - {}", statusCode, reason);
                        safeCloseClient(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Remote closed"));
                        return CompletableFuture.completedFuture(null);
                    }
                });

        future.whenComplete((ws, throwable) -> {
            if (throwable != null) {
                log.error("Failed to connect remote interview WS: {}", throwable.getMessage(), throwable);
                safeCloseClient(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Remote connect failed"));
            } else {
                this.remote = ws;
            }
        });
    }

    @OnMessage
    public void onBinaryMessage(ByteBuffer message) {
        if (remote != null && remoteOpen.get()) {
            try {
                remote.sendBinary(message, true);
            } catch (Exception e) {
                log.warn("Proxy send binary to remote failed: {}", e.getMessage());
            }
        }
    }

    @OnMessage
    public void onTextMessage(String text) {
        if (remote != null && remoteOpen.get()) {
            try {
                remote.sendText(text, true);
            } catch (Exception e) {
                log.warn("Proxy send text to remote failed: {}", e.getMessage());
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.warn("Proxy client error: {}", throwable.getMessage(), throwable);
        safeCloseRemote();
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        log.info("Proxy client closed: {}", reason);
        safeCloseRemote();
    }

    private void safeCloseClient(CloseReason reason) {
        try {
            if (clientSession != null && clientSession.isOpen()) {
                clientSession.close(reason);
            }
        } catch (IOException ignored) {
        }
    }

    private void safeCloseRemote() {
        try {
            if (remote != null) {
                remote.sendClose(WebSocket.NORMAL_CLOSURE, "close");
            }
        } catch (Exception ignored) {
        }
    }

    private void parseQueryParams(String query) {
        if (query == null || query.isEmpty()) return;
        String[] pairs = query.split("&");
        for (String p : pairs) {
            int idx = p.indexOf('=');
            if (idx > 0 && idx < p.length() - 1) {
                String k = p.substring(0, idx);
                String v = p.substring(idx + 1);
                queryParams.put(k, v);
            }
        }
    }
}