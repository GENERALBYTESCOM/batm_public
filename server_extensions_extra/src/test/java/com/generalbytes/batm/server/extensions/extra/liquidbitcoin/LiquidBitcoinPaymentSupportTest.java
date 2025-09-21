package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LiquidBitcoinPaymentSupportTest {

    private static final BigDecimal MINIMUM_NETWORK_FEE = BigDecimal.valueOf(0.21);
    private LiquidBitcoinPaymentSupport paymentSupport;

    @BeforeEach
    void setUp() {
        paymentSupport = new LiquidBitcoinPaymentSupport();
    }

    @Test
    void testGetCurrency() {
        assertEquals(CryptoCurrency.L_BTC.getCode(), paymentSupport.getCurrency());
    }

    @Test
    void testGetMaximumWatchingTimeMillis() {
        assertEquals(TimeUnit.DAYS.toMillis(3), paymentSupport.getMaximumWatchingTimeMillis());
    }

    @Test
    void testGetMaximumWaitForPossibleRefundMillis() {
        assertEquals(TimeUnit.DAYS.toMillis(3), paymentSupport.getMaximumWaitForPossibleRefundInMillis());
    }

    @Test
    void testGetMinimumNetworkFee() {
        RPCClient client = mockRpcClient();

        assertEquals(MINIMUM_NETWORK_FEE, paymentSupport.getMinimumNetworkFee(client));
    }

    @Test
    void testAddressValidator() {
        assertInstanceOf(LiquidBitcoinAddressValidator.class, paymentSupport.getAddressValidator());
    }

    @Test
    void testCalculateTransactionSize() {
        assertEquals(1563, paymentSupport.calculateTransactionSize(7, 15));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -1})
    void testCalculateTxFee_zeroOrNegativeEstimate(double estimate) {
        RPCClient client = mockRpcClient();
        when(client.getEstimateFee(2)).thenReturn(estimate);

        assertEquals(MINIMUM_NETWORK_FEE, paymentSupport.calculateTxFee(7, 15, client));
    }

    @Test
    void testCalculateTxFee_unexpectedException() {
        RPCClient client = mockRpcClient();
        when(client.getEstimateFee(2)).thenThrow(new BitcoinRPCException("Text Exception"));

        assertEquals(MINIMUM_NETWORK_FEE, paymentSupport.calculateTxFee(7, 15, client));
    }

    private static Object[][] provideEstimateFees() {
        return new Object[][]{
            // It will be divided by 1000 and rounded up to the nearest integer
            {1d, 1},
            {1000d, 1},
            {2000d, 2},
            {2010d, 3}
        };
    }

    @ParameterizedTest
    @MethodSource("provideEstimateFees")
    void testCalculateTxFee(double estimate, int multiplier) {
        RPCClient client = mockRpcClient();
        when(client.getEstimateFee(2)).thenReturn(estimate);

        int resultingTransactionSize = 1563; // Result of #calculateTransactionSize(7, 15)
        // divide estimate by 1000, round up and multiply by transaction size
        BigDecimal expectedResult = BigDecimal.valueOf((long) multiplier * resultingTransactionSize);

        BigDecimal result = paymentSupport.calculateTxFee(7, 15, client);

        assertEquals(expectedResult, result);
    }

    @Test
    void testGetSigHashType() {
        assertEquals("ALL", paymentSupport.getSigHashType());
    }

    private static RPCClient mockRpcClient() {
        RPCClient client = mock(RPCClient.class);
        BitcoindRpcClient.NetworkInfo networkInfo = mock(BitcoindRpcClient.NetworkInfo.class);
        when(networkInfo.relayFee()).thenReturn(MINIMUM_NETWORK_FEE);
        when(client.getNetworkInfo()).thenReturn(networkInfo);
        return client;
    }
}