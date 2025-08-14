package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import com.generalbytes.batm.server.extensions.communication.ISmsErrorResponse;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SmsBranaCzResponseMapperTest {

    @Test
    void testMapErrorResponse() {
        ISmsResponse response = SmsBranaCzResponseMapper.mapErrorResponse("some error message");

        assertNull(response.getSid());
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        assertNull(response.getPrice());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals("some error message", errorResponse.getErrorMessage());
        assertFalse(errorResponse.isBlacklisted());
    }

    @Test
    void testMapXmlResponse_success() {
        ISmsResponse response = SmsBranaCzResponseMapper.mapXmlResponse(createSMSBranaCZXmlResponse());
        assertEquals("7777", response.getSid());
        assertEquals(ISmsResponse.ResponseStatus.OK, response.getStatus());
        assertEquals(0, new BigDecimal("1.23").compareTo(response.getPrice()));
        assertNull(response.getErrorResponse());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"123 Kc", "n/a"})
    void testMapXmlResponse_successUnknownPrice(String price) {
        SmsBranaCzXmlResponse xmlResponse = createSMSBranaCZXmlResponse();
        xmlResponse.setPrice(price);
        ISmsResponse response = SmsBranaCzResponseMapper.mapXmlResponse(xmlResponse);
        assertEquals("7777", response.getSid());
        assertEquals(ISmsResponse.ResponseStatus.OK, response.getStatus());
        assertNull(response.getPrice());
        assertNull(response.getErrorResponse());
    }

    static Object[] testErrorXmlResponseSource() {
        return new Object[]{
            new Object[]{-1, "Duplicate user_id - SMS with same ID was already sent"},
            new Object[]{1, "Unknown error"},
            new Object[]{2, "Invalid login"},
            new Object[]{3, "Invalid password or hash"},
            new Object[]{4, "Invalid time - time difference between servers too large"},
            new Object[]{5, "Forbidden IP address"},
            new Object[]{6, "Invalid action name"},
            new Object[]{7, "Salt already used today"},
            new Object[]{8, "Database connection failed"},
            new Object[]{9, "Insufficient credit"},
            new Object[]{10, "Invalid phone number"},
            new Object[]{11, "Empty message text"},
            new Object[]{12, "Message too long (max 459 characters)"},
            new Object[]{999, "Error code: 999"},
        };
    }

    @ParameterizedTest
    @MethodSource("testErrorXmlResponseSource")
    void testMapXmlResponse_error(Integer errorCode, String expectedErrorMessage) {
        ISmsResponse response = SmsBranaCzResponseMapper.mapXmlResponse(createErrorSMSBranaCZXmlResponse(errorCode));
        assertNull(response.getSid());
        assertNull(response.getPrice());
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorMessage, errorResponse.getErrorMessage());
    }

    @Test
    void testMapXmlResponse_missingSmsId() {
        SmsBranaCzXmlResponse xmlResponse = createSMSBranaCZXmlResponse();
        xmlResponse.setSmsId(null);
        xmlResponse.setErr(null); // handle a case when API returns no error code but also null SMS ID

        ISmsResponse response = SmsBranaCzResponseMapper.mapXmlResponse(xmlResponse);

        assertNull(response.getSid());
        assertNull(response.getPrice());
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals("Invalid response format - missing SMS ID", errorResponse.getErrorMessage());
    }


    private SmsBranaCzXmlResponse createErrorSMSBranaCZXmlResponse(Integer errorCode) {
        SmsBranaCzXmlResponse response = new SmsBranaCzXmlResponse();
        response.setErr(errorCode);
        return response;
    }

    private SmsBranaCzXmlResponse createSMSBranaCZXmlResponse() {
        SmsBranaCzXmlResponse response = new SmsBranaCzXmlResponse();
        response.setErr(0);
        response.setSmsId(7777L);
        response.setPrice("1.23");
        response.setSmsCount(100);
        response.setCredit("123.45");
        return response;
    }
}