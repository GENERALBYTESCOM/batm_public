package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi;

import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.Payment;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.HttpStatusIOException;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletOfSatoshiWalletTest {

    @Mock
    private WalletOfSatoshiAPI api;
    private WalletOfSatoshiWallet wallet;

    @BeforeEach
    void setUp() throws GeneralSecurityException {
        try (MockedStatic<WalletOfSatoshiAPI> mockedApi = mockStatic(WalletOfSatoshiAPI.class)) {
            mockedApi.when(() -> WalletOfSatoshiAPI.create("apiToken", "apiSecret")).thenReturn(api);

            wallet = new WalletOfSatoshiWallet("apiToken", "apiSecret");
        }
    }

    @Test
    void testGetReceivedAmount_deprecated() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> wallet.getReceivedAmount("address", "LBTC"));

        assertEquals("This method is deprecated and should not be used.", exception.getMessage());
    }

    @Test
    void testGetReceivedAmount_noPayments() throws IOException {
        when(api.getPayments(null, null)).thenReturn(Collections.emptyList());

        ReceivedAmount result = wallet.getReceivedAmount("address");

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalAmountReceived());
        assertEquals(0, result.getConfirmations());
        assertNull(result.getTransactionHashes());
    }

    @Test
    void testGetReceivedAmount_noValidPayments() throws IOException {
        when(api.getPayments(null, null)).thenReturn(List.of(
                // Invalid payment type
                createPayment("address", "invalidType", BigDecimal.TEN),
                // Invalid address
                createPayment("differentAddress", "CREDIT", BigDecimal.TEN),
                // Both invalid
                createPayment("differentAddress", "invalidType", BigDecimal.ZERO)
        ));

        ReceivedAmount result = wallet.getReceivedAmount("address");

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalAmountReceived());
        assertEquals(0, result.getConfirmations());
        assertNull(result.getTransactionHashes());
    }

    @Test
    void testGetReceivedAmount_validPayments() throws IOException {
        when(api.getPayments(null, null)).thenReturn(List.of(
                // Valid payment
                createPayment("address", "CREDIT", BigDecimal.TEN),
                // Invalid payment type
                createPayment("address", "invalidType", BigDecimal.ONE)
        ));

        ReceivedAmount result = wallet.getReceivedAmount("address");

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getTotalAmountReceived());
        assertEquals(Integer.MAX_VALUE, result.getConfirmations());
        assertNull(result.getTransactionHashes());
    }

    @Test
    void testGetReceivedAmount_paymentHash() throws IOException {
        Payment payment = createPayment("address", "CREDIT", BigDecimal.TEN);
        payment.transactionId = "paymentHash";

        when(api.getPayments(null, null)).thenReturn(List.of(payment));

        ReceivedAmount result = wallet.getReceivedAmount("address");

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getTotalAmountReceived());
        assertEquals(Integer.MAX_VALUE, result.getConfirmations());
        assertNotNull(result.getTransactionHashes());
        assertEquals(1, result.getTransactionHashes().size());
        assertEquals(payment.transactionId, result.getTransactionHashes().get(0));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            HttpStatusIOException.class,
            IOException.class
    })
    void testGetReceivedAmount_exception(Class<? extends Exception> exceptionClass) throws IOException {
        Exception exception = mock(exceptionClass);
        if (exceptionClass == HttpStatusIOException.class) {
            when(((HttpStatusIOException) exception).getHttpBody()).thenReturn("HTTP error");
            when(((HttpStatusIOException) exception).getHttpStatusCode()).thenReturn(500);
        }

        when(api.getPayments(null, null)).thenThrow(exception);

        ReceivedAmount result = wallet.getReceivedAmount("address");

        assertNull(result);
    }

    private Payment createPayment(String address, String type, BigDecimal amount) {
        Payment payment = new Payment();
        payment.address = address;
        payment.type = type;
        payment.amount = amount;
        return payment;
    }
}