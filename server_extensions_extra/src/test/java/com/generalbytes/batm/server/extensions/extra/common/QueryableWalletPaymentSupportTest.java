package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.UsdtPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.*;

public class QueryableWalletPaymentSupportTest {

    private ReceivedAmount received;
    private final QueryableWalletPaymentSupport paymentSupport = new UsdtPaymentSupport();

    @Test
    public void invalidAmount() {
        received = ReceivedAmount.ZERO;
        PaymentRequest request = getPaymentRequest("1", "0", false);
        paymentSupport.poll(request);
        assertEquals(PaymentRequest.STATE_NEW, request.getState());
        received = new ReceivedAmount(new BigDecimal("0.5"), 9);
        paymentSupport.poll(request);
        assertEquals(PaymentRequest.STATE_TRANSACTION_INVALID, request.getState());
    }

    @Test
    public void exactAmount() {
        PaymentRequest request = getPaymentRequest("1", "0", false);
        received = new ReceivedAmount(new BigDecimal("1"), 0);
        paymentSupport.poll(request);
        assertEquals(PaymentRequest.STATE_SEEN_TRANSACTION, request.getState());
        received = new ReceivedAmount(new BigDecimal("1"), 9);
        paymentSupport.poll(request);
        assertEquals(PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN, request.getState());
    }

    @Test
    public void inTolerance() {
        test("1", "0.2", false, "0.85", PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
    }

    @Test
    public void notInTolerance() {
        test("1", "0.1", false, "0.85", PaymentRequest.STATE_TRANSACTION_INVALID);
    }

    @Test
    public void overInTolerance() {
        test("1", "0.2", false, "1.15", PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
    }

    @Test
    public void overNotInTolerance() {
        test("1", "0.1", false, "1.15", PaymentRequest.STATE_TRANSACTION_INVALID);
    }

    @Test
    public void overInToleranceWithOverageAllowed() {
        test("1", "0.2", true, "1.15", PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
    }

    @Test
    public void overNotInToleranceWithOverageAllowed() {
        test("1", "0.1", true, "1.15", PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
    }

    private void test(String requestedAmount, String tolerance, boolean overageAllowed, String received, int expectedState) {
        PaymentRequest request = getPaymentRequest(requestedAmount, tolerance, overageAllowed);
        this.received = new ReceivedAmount(new BigDecimal(received), 9);
        paymentSupport.poll(request);
        assertEquals(expectedState, request.getState());
    }

    private PaymentRequest getPaymentRequest(String requestedAmount, String tolerance, boolean overageAllowed) {
        return new PaymentRequest(CryptoCurrency.USDT.getCode(), null, -1, "addr", new BigDecimal(requestedAmount),
            new BigDecimal(tolerance), overageAllowed, 0, 0,
            new Wallet(), null, null, false, null, null, null);
    }

    private class Wallet implements IWallet, IQueryableWallet {

        @Override
        public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
            return received;
        }

        @Override
        public String getCryptoAddress(String cryptoCurrency) {
            return null;
        }

        @Override
        public Set<String> getCryptoCurrencies() {
            return null;
        }

        @Override
        public String getPreferredCryptoCurrency() {
            return null;
        }

        @Override
        public BigDecimal getCryptoBalance(String cryptoCurrency) {
            return null;
        }

        @Override
        public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
            return null;
        }
    }

}