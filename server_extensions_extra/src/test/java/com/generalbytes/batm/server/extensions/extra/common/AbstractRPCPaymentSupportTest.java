package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.server.extensions.extra.groestlcoin.GroestlcoinPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import org.junit.jupiter.api.Test;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractRPCPaymentSupportTest {

    private final AbstractRPCPaymentSupport paymentSupport = new GroestlcoinPaymentSupport();

    @Test
    void testGetTotalCoinsReceived() {
        BitcoindRpcClient.RawTransaction.Out out = mockRawTransactionOut(List.of("address"));
        BitcoindRpcClient.Transaction transaction = createTransaction(List.of(out));
        PaymentRequest paymentRequest = createPaymentRequest();

        BigDecimal result = paymentSupport.getTotalCoinsReceived(transaction, paymentRequest);

        assertNotNull(result);
        assertEquals(0, BigDecimal.ONE.compareTo(result));
    }

    @Test
    void testGetTotalCoinsReceived_differentAddress() {
        BitcoindRpcClient.RawTransaction.Out out = mockRawTransactionOut(List.of("differentAddress"));
        BitcoindRpcClient.Transaction transaction = createTransaction(List.of(out));
        PaymentRequest paymentRequest = createPaymentRequest();

        BigDecimal result = paymentSupport.getTotalCoinsReceived(transaction, paymentRequest);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    private static BitcoindRpcClient.Transaction createTransaction(List<BitcoindRpcClient.RawTransaction.Out> outs) {
        BitcoindRpcClient.Transaction transaction = mock(BitcoindRpcClient.Transaction.class);
        BitcoindRpcClient.RawTransaction rawTransaction = mock(BitcoindRpcClient.RawTransaction.class);
        when(rawTransaction.vOut()).thenReturn(outs);
        when(transaction.raw()).thenReturn(rawTransaction);
        return transaction;
    }

    private static PaymentRequest createPaymentRequest() {
        PaymentRequest paymentRequest = mock(PaymentRequest.class);
        when(paymentRequest.getAddress()).thenReturn("address");
        return paymentRequest;
    }

    private static BitcoindRpcClient.RawTransaction.Out mockRawTransactionOut(List<String> addresses) {
        BitcoindRpcClient.RawTransaction.Out out = mock(BitcoindRpcClient.RawTransaction.Out.class);
        BitcoindRpcClient.RawTransaction.Out.ScriptPubKey scriptPubKey = mock(BitcoindRpcClient.RawTransaction.Out.ScriptPubKey.class);
        when(scriptPubKey.addresses()).thenReturn(addresses);
        when(out.value()).thenReturn(BigDecimal.ONE);
        when(out.scriptPubKey()).thenReturn(scriptPubKey);
        return out;
    }
}