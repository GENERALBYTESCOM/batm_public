package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi;

import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.Payment;
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
    void testGetReceivedAmount_unsupportedCryptocurrency() {
        String unsupportedCryptoCurrency = "unsupportedCryptoCurrency"; // Anything other than LBTC

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> wallet.getReceivedAmount("address", unsupportedCryptoCurrency));

        assertEquals("unsupportedCryptoCurrency not supported", exception.getMessage());
    }

    @Test
    void testGetReceivedAmount_noPayments() throws IOException {
        when(api.getPayments(null, null)).thenReturn(Collections.emptyList());

        BigDecimal result = wallet.getReceivedAmount("address", "LBTC");

        assertEquals(BigDecimal.ZERO, result);
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

        BigDecimal result = wallet.getReceivedAmount("address", "LBTC");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testGetReceivedAmount_validPayments() throws IOException {
        when(api.getPayments(null, null)).thenReturn(List.of(
                // Valid payment
                createPayment("address", "CREDIT", BigDecimal.TEN),
                // Invalid payment type
                createPayment("address", "invalidType", BigDecimal.ONE)
        ));

        BigDecimal result = wallet.getReceivedAmount("address", "LBTC");

        assertEquals(BigDecimal.TEN, result);
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

        BigDecimal result = wallet.getReceivedAmount("address", "LBTC");

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