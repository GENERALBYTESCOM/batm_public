package com.generalbytes.batm.server.extensions.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReceivedAmountTest {

    @Test
    void testConstruction_nullAmount() {
        assertThrows(NullPointerException.class, () -> new ReceivedAmount(null, 0));
    }

    @Test
    void testConstruction() {
        ReceivedAmount receivedAmount = new ReceivedAmount(BigDecimal.valueOf(100), 10);

        assertEquals(BigDecimal.valueOf(100), receivedAmount.getTotalAmountReceived());
        assertEquals(10, receivedAmount.getConfirmations());
        assertNull(receivedAmount.getTransactionHashes());
    }

}