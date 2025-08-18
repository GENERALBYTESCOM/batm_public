package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrWebhookHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for handling incoming webhook messages from Global Travel Rule.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE) // Private constructor to prevent instantiation.
public final class GtrWebhookHandlerService {

    // Singleton eager initialization
    private static final GtrWebhookHandlerService INSTANCE = new GtrWebhookHandlerService();
    private final Map<Integer, GtrWebhookHandler> requestHandlers = new ConcurrentHashMap<>();

    /**
     * Get the instance of {@link GtrWebhookHandlerService}.
     *
     * @return The instance.
     */
    public static GtrWebhookHandlerService getInstance() {
        return INSTANCE;
    }

    void registerHandler(Integer callbackType, GtrWebhookHandler requestHandler) {
        log.debug("Registering request handler for callback type {}", callbackType);
        requestHandlers.put(callbackType, requestHandler);
    }

    /**
     * Retrieves the request handler associated with the given callback type.
     *
     * @param callbackType The callback type used to identify the appropriate request handler.
     * @return The {@link GtrWebhookHandler} associated with the given callback type, or null if no handler is found.
     * @see GtrWebhookHandler
     */
    public GtrWebhookHandler getHandler(Integer callbackType) {
        return requestHandlers.get(callbackType);
    }
}
