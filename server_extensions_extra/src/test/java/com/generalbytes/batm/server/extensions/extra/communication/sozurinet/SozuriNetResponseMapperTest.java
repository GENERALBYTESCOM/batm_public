package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import com.generalbytes.batm.server.extensions.communication.ISmsErrorResponse;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SozuriNetResponseMapperTest {

    @Test
    void testMapErrorResponse() {
        ISmsResponse response = SozuriNetResponseMapper.mapErrorResponse("some error message");

        assertNull(response.getSid());
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        assertNull(response.getPrice());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals("some error message", errorResponse.getErrorMessage());
        assertFalse(errorResponse.isBlacklisted());
    }

    @Test
    void testMapJsonResponse_success() {
        ISmsResponse response = SozuriNetResponseMapper.mapJsonResponse(createSuccessJsonResponse());
        assertEquals("795ecc08996e79d491cce39913fbf11d2bfdb18d", response.getSid());
        assertEquals(ISmsResponse.ResponseStatus.OK, response.getStatus());
        assertNull(response.getPrice());
        assertNull(response.getErrorResponse());
    }

    @Test
    void testMapJsonResponse_errorWithMessage() {
        SozuriNetJsonResponse jsonResponse = new SozuriNetJsonResponse();
        jsonResponse.setMessage("Unauthenticated.");

        ISmsResponse response = SozuriNetResponseMapper.mapJsonResponse(jsonResponse);

        assertNull(response.getSid());
        assertNull(response.getPrice());
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals("Unauthenticated.", errorResponse.getErrorMessage());
    }

    @Test
    void testMapJsonResponse_missingMessageId() {
        SozuriNetJsonResponse jsonResponse = createSuccessJsonResponse();
        jsonResponse.getRecipients().get(0).setMessageId(null);

        ISmsResponse response = SozuriNetResponseMapper.mapJsonResponse(jsonResponse);

        assertNull(response.getSid());
        assertNull(response.getPrice());
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals("Invalid response format - missing message ID", errorResponse.getErrorMessage());
    }

    @Test
    void testMapJsonResponse_emptyRecipients() {
        SozuriNetJsonResponse jsonResponse = new SozuriNetJsonResponse();
        jsonResponse.setRecipients(new ArrayList<>());

        ISmsResponse response = SozuriNetResponseMapper.mapJsonResponse(jsonResponse);

        assertNull(response.getSid());
        assertNull(response.getPrice());
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals("Invalid response format from SMS service", errorResponse.getErrorMessage());
    }

    @Test
    void testMapJsonResponse_nullRecipients() {
        SozuriNetJsonResponse jsonResponse = new SozuriNetJsonResponse();

        ISmsResponse response = SozuriNetResponseMapper.mapJsonResponse(jsonResponse);

        assertNull(response.getSid());
        assertNull(response.getPrice());
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals("Invalid response format from SMS service", errorResponse.getErrorMessage());
    }

    private SozuriNetJsonResponse createSuccessJsonResponse() {
        SozuriNetJsonResponse response = new SozuriNetJsonResponse();
        
        SozuriNetJsonResponse.MessageData messageData = new SozuriNetJsonResponse.MessageData();
        messageData.setMessages(1);
        response.setMessageData(messageData);

        SozuriNetJsonResponse.Recipient recipient = new SozuriNetJsonResponse.Recipient();
        recipient.setMessageId("795ecc08996e79d491cce39913fbf11d2bfdb18d");
        recipient.setTo("254603572525");
        recipient.setStatus("sent");
        recipient.setStatusCode("11");
        recipient.setBulkId("bulk69038c633ae6d2.861374281761840227");
        recipient.setMessagePart(1);
        recipient.setType("promotional");

        List<SozuriNetJsonResponse.Recipient> recipients = new ArrayList<>();
        recipients.add(recipient);
        response.setRecipients(recipients);

        return response;
    }
}

