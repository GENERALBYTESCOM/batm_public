package com.generalbytes.batm.server.extensions.extra.nano.rpc;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;

/**
 * A WebSocket client which automatically re-connects upon closure.
 */
public class ReconnectingWebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(ReconnectingWebSocketClient.class);

    private static final long RETRY_DELAY_MS = 500;

    private final URI uri;
    private final Handler handler;
    private volatile WebSocketClient wsClient;
    private final Object connectMutex = new Object();
    private volatile boolean isReconnecting;

    public ReconnectingWebSocketClient(URI uri, Handler handler) {
        this.uri = uri;
        this.handler = handler;
    }


    public void send(String text) {
        if (!isOpen()) throw new NotYetConnectedException();
        wsClient.send(text);
    }

    public boolean isOpen() {
        return wsClient != null && wsClient.isOpen();
    }

    public boolean isReconnecting() {
        return !isOpen() && isReconnecting;
    }

    public void initConnection() {
        if (!isOpen() && !isReconnecting) {
            synchronized (connectMutex) {
                if (!isOpen() && !isReconnecting) {
                    isReconnecting = true;
                    Thread reconnectThread = new Thread(new ReconnectionTask(), "WebSocketReconnectThread");
                    reconnectThread.setDaemon(true);
                    reconnectThread.start();
                }
            }
        }
    }

    private boolean connect() {
        if (!isOpen()) {
            synchronized (connectMutex) {
                if (!isOpen()) {
                    log.debug("Attempting to connect to node WebSocket.");
                    wsClient = new WsClientImpl();
                    try {
                        return wsClient.connectBlocking();
                    } catch (InterruptedException e) {
                        log.error("WebSocket connection attempt interrupted.", e);
                    }
                }
            }
        }
        return true;
    }


    private class ReconnectionTask implements Runnable {
        @Override
        public void run() {
            try {
                boolean connected;
                do {
                    Thread.sleep(RETRY_DELAY_MS);
                    connected = connect();
                } while (!connected);
            } catch (InterruptedException e) {
                isReconnecting = false;
                log.warn("Reconnection thread interrupted. WebSocket will no longer attempt to reconnect!");
            }
        }
    }

    private class WsClientImpl extends WebSocketClient {
        public WsClientImpl() {
            super(ReconnectingWebSocketClient.this.uri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            log.debug("WebSocket opened, status = {}", handshakedata.getHttpStatus());
            boolean wasReconnect = isReconnecting;
            isReconnecting = false;
            handler.onOpen(wasReconnect);
        }

        @Override
        public void onMessage(String message) {
            log.trace("WebSocket message received = {}", message);
            handler.onMessage(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            log.debug("WebSocket closed, code = {}, reason = {}, remote = {}", code, reason, remote);
            handler.onDisconnect();
            initConnection(); // Reconnect
        }

        @Override
        public void onError(Exception ex) {
            log.error("Exception with WebSocket", ex);
        }
    }

    public interface Handler {
        void onOpen(boolean reconnect);
        void onMessage(String message);
        void onDisconnect();
    }

}
