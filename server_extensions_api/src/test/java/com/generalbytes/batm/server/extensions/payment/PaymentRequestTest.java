package com.generalbytes.batm.server.extensions.payment;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class PaymentRequestTest {

    @Test
    public void getForwardingTransactionMiningFee() {
        assertEquals(BigDecimal.ONE, getPaymentRequest(false, getPaymentOutput(new BigDecimal(8)), getPaymentOutput(BigDecimal.ONE)).getForwardingTransactionMiningFee());
        assertEquals(BigDecimal.ONE, getPaymentRequest(null, getPaymentOutput(new BigDecimal(8)), getPaymentOutput(BigDecimal.ONE)).getForwardingTransactionMiningFee());
        assertEquals(BigDecimal.ONE, getPaymentRequest(false, getPaymentOutput(new BigDecimal(9))).getForwardingTransactionMiningFee());
        assertEquals(BigDecimal.TEN, getPaymentRequest(false).getForwardingTransactionMiningFee());

        assertEquals(BigDecimal.ZERO, getPaymentRequest(true, getPaymentOutput(new BigDecimal(10))).getForwardingTransactionMiningFee());
    }

    private PaymentRequest getPaymentRequest(Boolean nonForwarding, IPaymentOutput... outputs) {
        return new PaymentRequest(null, null, 0L, null, BigDecimal.TEN, null, 0, 0, null, null, Arrays.asList(outputs), nonForwarding, null, null, null);
    }

    private IPaymentOutput getPaymentOutput(final BigDecimal amount) {
        return new IPaymentOutput() {
            @Override
            public String getAddress() {
                return null;
            }

            @Override
            public void setAddress(String address) {

            }

            @Override
            public BigDecimal getAmount() {
                return amount;
            }

            @Override
            public void removeAmount(BigDecimal amountToRemove) {

            }
        };
    }

    private void assertEquals(BigDecimal expected, BigDecimal actual) {
        Assert.assertTrue("expected: " + expected + ", actual: " + actual, expected.compareTo(actual) == 0);
    }
}