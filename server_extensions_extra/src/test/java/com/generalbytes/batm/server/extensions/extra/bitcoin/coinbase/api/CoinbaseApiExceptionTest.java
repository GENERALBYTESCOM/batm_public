package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseApiError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CoinbaseApiExceptionTest {

    @Test
    public void testGetMessage_nullMessage() {
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, null, null, null);

        String message = exception.getMessage();

        assertEquals("", message);
    }

    @Test
    public void testGetMessage_emptyMessage() {
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "", null, null);

        String message = exception.getMessage();

        assertEquals("", message);
    }

    @Test
    public void testGetMessage_justMessage() {
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "message", null, null);

        String message = exception.getMessage();

        assertEquals("message", message);
    }

    @Test
    public void testGetMessage_zeroErrorsAndWarnings() {
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "message", new CoinbaseApiError[0], new CoinbaseApiError[0]);

        String message = exception.getMessage();

        assertEquals("message", message);
    }

    @Test
    public void testGetMessage_onlyErrors() {
        CoinbaseApiError error = createCoinbaseApiError("error", "Error");
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "message", new CoinbaseApiError[]{error}, null);

        String message = exception.getMessage();

        assertEquals("message | Errors: [CoinbaseApiError{id='error', message='Error', url='url'}]", message);
    }

    @Test
    public void testGetMessage_multipleErrors() {
        CoinbaseApiError error1 = createCoinbaseApiError("error1", "Error 1");
        CoinbaseApiError error2 = createCoinbaseApiError("error2", "Error 2");
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "message", new CoinbaseApiError[]{error1, error2}, null);

        String message = exception.getMessage();

        assertEquals("message | Errors: [CoinbaseApiError{id='error1', message='Error 1', url='url'}," +
            " CoinbaseApiError{id='error2', message='Error 2', url='url'}]", message);
    }

    @Test
    public void testGetMessage_onlyWarnings() {
        CoinbaseApiError warning = createCoinbaseApiError("warning", "Warning");
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "message", null, new CoinbaseApiError[]{warning});

        String message = exception.getMessage();

        assertEquals("message | Warnings: [CoinbaseApiError{id='warning', message='Warning', url='url'}]", message);
    }

    @Test
    public void testGetMessage_multipleWarnings() {
        CoinbaseApiError warning1 = createCoinbaseApiError("warning1", "Warning 1");
        CoinbaseApiError warning2 = createCoinbaseApiError("warning2", "Warning 2");
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "message", null, new CoinbaseApiError[]{warning1, warning2});

        String message = exception.getMessage();

        assertEquals("message | Warnings: [CoinbaseApiError{id='warning1', message='Warning 1', url='url'}," +
            " CoinbaseApiError{id='warning2', message='Warning 2', url='url'}]", message);
    }

    @Test
    public void testGetMessage_errorsAndWarnings() {
        CoinbaseApiError error = createCoinbaseApiError("error", "Error");
        CoinbaseApiError warning = createCoinbaseApiError("warning", "Warning");
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "message", new CoinbaseApiError[]{error},
            new CoinbaseApiError[]{warning});

        String message = exception.getMessage();

        assertEquals("message | Errors: [CoinbaseApiError{id='error', message='Error', url='url'}]" +
            " | Warnings: [CoinbaseApiError{id='warning', message='Warning', url='url'}]", message);
    }

    @Test
    public void testGetMessage_errorAndNullMessage() {
        CoinbaseApiError error = createCoinbaseApiError("error", "Error");
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, null, new CoinbaseApiError[]{error}, null);

        String message = exception.getMessage();

        assertEquals("Errors: [CoinbaseApiError{id='error', message='Error', url='url'}]", message);
    }

    @Test
    public void testGetMessage_warningAndEmptyMessage() {
        CoinbaseApiError warning = createCoinbaseApiError("warning", "Warning");
        CoinbaseApiException exception = new CoinbaseApiException("error", 500, "", null, new CoinbaseApiError[]{warning});

        String message = exception.getMessage();

        assertEquals("Warnings: [CoinbaseApiError{id='warning', message='Warning', url='url'}]", message);
    }

    private static CoinbaseApiError createCoinbaseApiError(String id, String message) {
        CoinbaseApiError error = new CoinbaseApiError();
        error.setId(id);
        error.setMessage(message);
        error.setUrl("url");
        return error;
    }

}