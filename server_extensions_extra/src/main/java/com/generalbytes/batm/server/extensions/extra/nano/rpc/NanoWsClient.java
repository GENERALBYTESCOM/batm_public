package com.generalbytes.batm.server.extensions.extra.nano.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Karl Oczadly
 */
public class NanoWsClient {

    private static final Logger log = LoggerFactory.getLogger(NanoWsClient.class);

    private static final String ACTION_SUBSCRIBE = "subscribe";
    private static final String ACTION_UPDATE = "update";
    private static final String TOPIC_BLOCK_CONFIRMATIONS = "confirmation";

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private final ReconnectingWebSocketClient wsClient;
    private final ExecutorService handlerThreadPool = Executors.newCachedThreadPool();
    private final AtomicLong nextAckId = new AtomicLong();
    private final Map<Long, CountDownLatch> activeAckRequests = new ConcurrentHashMap<>();
    private final Map<String, DepositListener> activeListeners = new ConcurrentHashMap<>();
    private volatile boolean areTopicsRegistered;

    public NanoWsClient(URI uri) {
        this.wsClient = new ReconnectingWebSocketClient(uri, new WSHandler());
        this.wsClient.initConnection();
    }


    public boolean isActive() {
        return isSocketOpen() && areTopicsRegistered;
    }

    public boolean isSocketOpen() {
        return wsClient.isOpen();
    }


    public boolean addDepositListener(String address, DepositListener listener) {
        activeListeners.put(address, listener);
        // Update WS filter
        JsonNode options = JSON_MAPPER.createObjectNode()
            .set("accounts_add", JSON_MAPPER.createArrayNode().add(address));
        return sendTopicRequest(ACTION_UPDATE, TOPIC_BLOCK_CONFIRMATIONS, options);
    }

    public boolean removeDepositListener(String address) {
        activeListeners.remove(address);
        // Update WS filter
        JsonNode options = JSON_MAPPER.createObjectNode()
            .set("accounts_del", JSON_MAPPER.createArrayNode().add(address));
        return sendTopicRequest(ACTION_UPDATE, TOPIC_BLOCK_CONFIRMATIONS, options);
    }


    private void handleIncomingDeposit(String depositAccount) {
        DepositListener listener = activeListeners.get(depositAccount.toLowerCase());
        if (listener != null) {
            log.debug("Incoming block for deposit account {}, notifying listener...", depositAccount);
            listener.onDeposit();
        } else {
            log.debug("Handled block for account {} despite being inactive.", depositAccount);
        }
    }

    private void handleTopicMessage(String topic, ObjectNode message) {
        if (topic.equalsIgnoreCase(TOPIC_BLOCK_CONFIRMATIONS)) {
            ObjectNode block = (ObjectNode)message.get("block");
            if (block.has("subtype")) {
                String subtype = block.get("subtype").asText();
                if (subtype.equalsIgnoreCase("send")) {
                    handleIncomingDeposit(block.get("link_as_account").asText()); // Using destination
                } else if (subtype.equalsIgnoreCase("receive")) {
                    handleIncomingDeposit(block.get("account").asText()); // Using block owner
                }
            } else {
                log.warn("Couldn't interpret block data (hash {})", message.get("hash").asText());
            }
        }
    }

    /** Sends a topic request with the given data, and returns true if ack message is received, false if timeout. */
    private boolean sendTopicRequest(String action, String topic, JsonNode options) {
        // Create tracker
        long reqId = nextAckId.getAndIncrement();
        CountDownLatch latch = new CountDownLatch(1);
        activeAckRequests.put(reqId, latch);

        try {
            // Create request JSON
            ObjectNode request = JSON_MAPPER.createObjectNode()
                .put("action", action)
                .put("topic", topic)
                .put("ack", true)
                .put("id", Long.toString(reqId));
            if (options != null)
                request.set("options", options);
            String reqJson;
            try {
                reqJson = JSON_MAPPER.writeValueAsString(request);
            } catch (JsonProcessingException e) {
                log.error("Couldn't construct request JSON.", e);
                return false;
            }

            log.debug("Sending WS request {}", reqJson);
            try {
                wsClient.send(reqJson);
            } catch (NotYetConnectedException e) {
                log.debug("WS not connected, ignoring topic request.");
                return false;
            }

            // Await ack message
            try {
                if (latch.await(5000, TimeUnit.MILLISECONDS)) {
                    log.debug("Request ack received (action = {}, topic = {})", action, topic);
                    return true;
                } else {
                    log.warn("No ack received (action = {}, topic = {})", action, topic);
                    return false;
                }
            } catch (InterruptedException e) {
                log.warn("Awaiting request ack interrupted.", e);
                return false;
            }
        } finally {
            activeAckRequests.remove(reqId);
        }
    }


    public interface DepositListener {
        void onDeposit();
    }

    private class WSHandler implements ReconnectingWebSocketClient.Handler {
        @Override
        public void onOpen(boolean reconnect) {
            areTopicsRegistered = false;
            handlerThreadPool.submit(() -> {
                while (isSocketOpen() && !areTopicsRegistered) {
                    // Subscribe to confirmations
                    JsonNode options = JSON_MAPPER.createObjectNode()
                        .set("accounts", JSON_MAPPER.valueToTree(activeListeners.keySet()));
                    if (sendTopicRequest(ACTION_SUBSCRIBE, TOPIC_BLOCK_CONFIRMATIONS, options)) {
                        areTopicsRegistered = true;
                        log.debug("Topics registered to websocket.");
                    } else {
                        log.warn("Failed to register topic with WebSocket.");
                    }
                }
            });
        }

        @Override
        public void onMessage(String message) {
            handlerThreadPool.submit(() -> {
                ObjectNode json;
                try {
                    json = (ObjectNode)JSON_MAPPER.readTree(message);
                } catch (IOException | ClassCastException e) {
                    log.error("Couldn't parse WebSocket message as JSON {}", message, e);
                    return;
                }

                if (json.has("ack")) {
                    // Request acknowledgement, notify tracker
                    CountDownLatch tracker = activeAckRequests.get(json.get("id").asLong());
                    if (tracker != null) tracker.countDown();
                } else if (json.has("message")) {
                    // Message from subscribed topic
                    try {
                        handleTopicMessage(json.get("topic").asText(), (ObjectNode)json.get("message"));
                    } catch (Exception e) {
                        log.error("Exception occurred when handling topic message.", e);
                    }
                } else {
                    log.warn("Unrecognized WebSocket JSON message {}", message);
                }
            });
        }

        @Override
        public void onDisconnect() {
            areTopicsRegistered = false;
        }
    }

}
