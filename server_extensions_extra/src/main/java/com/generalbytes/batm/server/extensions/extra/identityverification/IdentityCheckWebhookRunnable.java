package com.generalbytes.batm.server.extensions.extra.identityverification;

import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;

/**
 * A utility to run a lambda and return an OK {@link Response} in case of no exception,
 * or a corresponding Response when a RuntimeException or {@link IdentityCheckWebhookException} is thrown.
 */
@FunctionalInterface
public interface IdentityCheckWebhookRunnable {
    void run() throws IdentityCheckWebhookException;

    Logger log = LoggerFactory.getLogger(IdentityCheckWebhookRunnable.class);

    static Response getResponse(String errorLabel, IdentityCheckWebhookRunnable runnable) {
        try {
            runnable.run();
            return Response.ok().build();
        } catch (RuntimeException e) {
            log.error("Failed to process webhook; {}", errorLabel, e);
            return Response.serverError().build();
        } catch (IdentityCheckWebhookException e) {
            log.error("Failed to process webhook: {}; {}", e.getResponseEntity(), errorLabel, e);
            return Response.status(e.getResponseStatus()).entity(e.getResponseEntity()).build();
        }
    }
}
