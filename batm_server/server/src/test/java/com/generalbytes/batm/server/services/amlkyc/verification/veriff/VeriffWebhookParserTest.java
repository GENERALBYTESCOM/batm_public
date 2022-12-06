package com.generalbytes.batm.server.services.amlkyc.verification.veriff;

import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationEventWebhookRequest;
import com.generalbytes.batm.server.services.web.IdentityCheckWebhookException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class VeriffWebhookParserTest {
    private static final VeriffWebhookParser parser = new VeriffWebhookParser();

    @Test
    public void invalidJson() {
        assertThrows(IdentityCheckWebhookException.class, () -> test("{invalid"));
    }

    @Test
    public void unknownType() {
        assertThrows(IdentityCheckWebhookException.class, () -> test("{}"));
    }

    @Test
    public void event() throws IOException, URISyntaxException, IdentityCheckWebhookException {
        AcceptResults results = test(readResource("verification-event.json"));
        assertEquals("submitted", results.event.action);
        assertEquals("QWE123", results.event.vendorData);
    }

    @Test
    public void decision() throws IOException, URISyntaxException, IdentityCheckWebhookException {
        AcceptResults results = test(readResource("decision.json"));
        assertEquals("MORGAN", results.decision.verification.person.lastName);
        assertEquals("1234 Snowy Ridge Road, Indiana, 56789 USA", results.decision.verification.person.addresses.get(0).fullAddress);
        assertEquals("USA", results.decision.verification.person.addresses.get(0).parsedAddress.country);
        assertNull(results.decision.verification.person.addresses.get(0).parsedAddress.city);
        assertEquals("null", results.decision.verification.person.addresses.get(0).parsedAddress.houseNumber);
        assertEquals("GB", results.decision.verification.document.country);
    }


    private String readResource(String filename) throws IOException, URISyntaxException, RuntimeException {
        return new String(Files.readAllBytes(Paths.get(getClass().getResource(filename).toURI())));
    }

    private static class AcceptResults {
        public VerificationDecisionWebhookRequest decision = null;
        public VerificationEventWebhookRequest event = null;
    }

    private AcceptResults test(String payload) throws IdentityCheckWebhookException {
        AcceptResults results = new AcceptResults();
        parser.accept(payload,
                (rawPayload, parsedPayload) -> results.decision = parsedPayload,
                (rawPayload, parsedPayload) -> results.event = parsedPayload);
        return results;
    }
}