package com.generalbytes.batm.server.extensions.extra.lightningbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ILightningWallet;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LightningBitcoinPaymentSupportTest {

    @Mock
    private ILightningWallet wallet;
    @Mock
    private IPaymentRequestListener paymentRequestListener;

    private LightningBitcoinPaymentSupport paymentSupport;

    @BeforeEach
    void setUp() {
        paymentSupport = new LightningBitcoinPaymentSupport();
    }

    @Test
    void testPoll_stateSeenInBlockchain() {
        PaymentRequest paymentRequest = createPaymentRequest();
        paymentRequest.setState(PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);

        paymentSupport.poll(paymentRequest);

        assertEquals(BigDecimal.ZERO, paymentRequest.getTxValue());
        assertNull(paymentRequest.getIncomingTransactionHash());
        verify(wallet, never()).getReceivedAmount(any());
        verifyNoInteractions(paymentRequestListener);
    }

    private static Object[][] provideInvalidReceivedAmount() {
        return new Object[][]{
                {null},
                {new ReceivedAmount(BigDecimal.ZERO, 0)},
                {new ReceivedAmount(BigDecimal.valueOf(-1), 0)},
                {new ReceivedAmount(BigDecimal.valueOf(-100), 0)},
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidReceivedAmount")
    void testPoll_invalidReceivedAmount(ReceivedAmount receivedAmount) {
        PaymentRequest paymentRequest = createPaymentRequest();

        when(wallet.getReceivedAmount(any())).thenReturn(receivedAmount);

        paymentSupport.poll(paymentRequest);

        assertEquals(BigDecimal.ZERO, paymentRequest.getTxValue());
        assertNull(paymentRequest.getIncomingTransactionHash());
        verify(wallet).getReceivedAmount(paymentRequest.getAddress());
        verifyNoInteractions(paymentRequestListener);
    }

    @Test
    void testPoll_exceptionOnReceivedAmount() {
        PaymentRequest paymentRequest = createPaymentRequest();

        when(wallet.getReceivedAmount(any())).thenThrow(new RuntimeException("Test exception"));

        paymentSupport.poll(paymentRequest);

        assertEquals(BigDecimal.ZERO, paymentRequest.getTxValue());
        assertNull(paymentRequest.getIncomingTransactionHash());
        verify(wallet).getReceivedAmount(paymentRequest.getAddress());
        verifyNoInteractions(paymentRequestListener);
    }

    @Test
    void testPoll_differentAmount() {
        PaymentRequest paymentRequest = createPaymentRequest();
        paymentRequest.setAmount(BigDecimal.valueOf(100));
        ReceivedAmount receivedAmount = new ReceivedAmount(BigDecimal.valueOf(200), 0);

        when(wallet.getReceivedAmount(any())).thenReturn(receivedAmount);

        paymentSupport.poll(paymentRequest);

        assertEquals(BigDecimal.ZERO, paymentRequest.getTxValue());
        assertNull(paymentRequest.getIncomingTransactionHash());
        assertEquals(PaymentRequest.STATE_TRANSACTION_INVALID, paymentRequest.getState());
        verify(wallet).getReceivedAmount(paymentRequest.getAddress());
        verify(paymentRequestListener).stateChanged(paymentRequest, 0, PaymentRequest.STATE_TRANSACTION_INVALID);
    }

    @Test
    void testPoll_differentAmount_alreadyInvalidState() {
        PaymentRequest paymentRequest = createPaymentRequest();
        paymentRequest.setState(PaymentRequest.STATE_TRANSACTION_INVALID);
        paymentRequest.setAmount(BigDecimal.valueOf(100));
        ReceivedAmount receivedAmount = new ReceivedAmount(BigDecimal.valueOf(200), 0);

        when(wallet.getReceivedAmount(any())).thenReturn(receivedAmount);

        paymentSupport.poll(paymentRequest);

        assertEquals(BigDecimal.valueOf(200), paymentRequest.getTxValue());
        assertNull(paymentRequest.getIncomingTransactionHash());
        assertEquals(PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN, paymentRequest.getState());
        verify(wallet).getReceivedAmount(paymentRequest.getAddress());
        verify(paymentRequestListener, never()).stateChanged(any(), anyInt(), eq(PaymentRequest.STATE_TRANSACTION_INVALID));
    }

    @Test
    void testPoll() {
        PaymentRequest paymentRequest = createPaymentRequest();
        paymentRequest.setAmount(BigDecimal.valueOf(200));
        ReceivedAmount receivedAmount = new ReceivedAmount(BigDecimal.valueOf(200), 0);

        when(wallet.getReceivedAmount(any())).thenReturn(receivedAmount);

        paymentSupport.poll(paymentRequest);

        assertEquals(BigDecimal.valueOf(200), paymentRequest.getTxValue());
        assertNull(paymentRequest.getIncomingTransactionHash());
        assertEquals(PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN, paymentRequest.getState());
        verify(wallet).getReceivedAmount(paymentRequest.getAddress());
        verify(paymentRequestListener).stateChanged(paymentRequest, 0, PaymentRequest.STATE_SEEN_TRANSACTION);
        verify(paymentRequestListener).stateChanged(paymentRequest, PaymentRequest.STATE_SEEN_TRANSACTION, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
        verify(paymentRequestListener).numberOfConfirmationsChanged(paymentRequest, 999, IPaymentRequestListener.Direction.INCOMING);
    }

    private static Object[][] provideTransactionHashes() {
        return new Object[][]{
                {List.of("tx1", "tx2"), "tx1 tx2"},
                {List.of("tx3"), "tx3"},
                {List.of(), null},
                {null, null}
        };
    }

    @ParameterizedTest
    @MethodSource("provideTransactionHashes")
    void testPoll_transactionHashes(List<String> transactionHashes, String expectedIncomingTransactionHash) {
        PaymentRequest paymentRequest = createPaymentRequest();
        paymentRequest.setAmount(BigDecimal.valueOf(200));
        ReceivedAmount receivedAmount = new ReceivedAmount(BigDecimal.valueOf(200), 0);
        receivedAmount.setTransactionHashes(transactionHashes);

        when(wallet.getReceivedAmount(any())).thenReturn(receivedAmount);

        paymentSupport.poll(paymentRequest);

        assertEquals(expectedIncomingTransactionHash, paymentRequest.getIncomingTransactionHash());
    }

    @Test
    void testGetCryptocurrency() {
        assertEquals(CryptoCurrency.LBTC.getCode(), paymentSupport.getCryptoCurrency());
    }

    @Test
    void testGetPollingPeriodMillis() {
        assertEquals(1000, paymentSupport.getPollingPeriodMillis());
    }

    @Test
    void testGetPollingInitialDelayMillis() {
        assertEquals(3000, paymentSupport.getPollingInitialDelayMillis());
    }

    private PaymentRequest createPaymentRequest() {
        PaymentRequest paymentRequest = new PaymentRequest(
                CryptoCurrency.LBTC.getCode(),
                "description",
                Long.MAX_VALUE,
                "address",
                BigDecimal.TEN,
                BigDecimal.ZERO,
                true,
                1,
                2,
                wallet,
                "timeoutRefundAddress",
                List.of(),
                true,
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        paymentRequest.setState(PaymentRequest.STATE_NEW);
        paymentRequest.setListener(paymentRequestListener);
        return paymentRequest;
    }

}