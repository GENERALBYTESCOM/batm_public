package com.generalbytes.batm.server.extensions.payment;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentRequestTest {

    @Test
    public void getForwardingTransactionMiningFee() {
        assertThat(getPaymentRequest(false, getPaymentOutput(new BigDecimal(8)), getPaymentOutput(BigDecimal.ONE)).getForwardingTransactionMiningFee()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(getPaymentRequest(null, getPaymentOutput(new BigDecimal(8)), getPaymentOutput(BigDecimal.ONE)).getForwardingTransactionMiningFee()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(getPaymentRequest(false, getPaymentOutput(new BigDecimal(9))).getForwardingTransactionMiningFee()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(getPaymentRequest(false).getForwardingTransactionMiningFee()).isEqualByComparingTo(BigDecimal.TEN);

        assertThat(getPaymentRequest(true, getPaymentOutput(new BigDecimal(10))).getForwardingTransactionMiningFee()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private PaymentRequest getPaymentRequest(Boolean nonForwarding, IPaymentOutput... outputs) {
        return new PaymentRequest(null, null, 0L, null, BigDecimal.TEN, null, false, 0, 0, null, null, Arrays.asList(outputs), nonForwarding, null, null, null);
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
}