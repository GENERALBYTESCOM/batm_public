package com.generalbytes.batm.server.extensions.extra.identityverification;

import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IdentityCheckWebhookRunnableTest {

    @Test
    void getResponse() {
        Response ok = IdentityCheckWebhookRunnable.getResponse("label", () -> {});
        assertEquals(200, ok.getStatus());
        assertNull(ok.getEntity());

        Response runtime = IdentityCheckWebhookRunnable.getResponse("label", () -> {throw new RuntimeException("runtime");});
        assertEquals(500, runtime.getStatus());
        assertNull(runtime.getEntity());

        Response webhook = IdentityCheckWebhookRunnable.getResponse("label", () -> {
            throw new IdentityCheckWebhookException(Response.Status.UNAUTHORIZED.getStatusCode(), "unauthorized entity", "unauthorized message");
        });
        assertEquals(401, webhook.getStatus());
        assertEquals("unauthorized entity", webhook.getEntity());

        Response webhook2 = IdentityCheckWebhookRunnable.getResponse("label", () -> {
            throw new IdentityCheckWebhookException(Response.Status.UNAUTHORIZED.getStatusCode(), "unauthorized entity", "unauthorized message", new NullPointerException("test exception"));
        });
        assertEquals(401, webhook2.getStatus());
        assertEquals("unauthorized entity", webhook2.getEntity());


    }
}