/*************************************************************************************
 * Copyright (C) 2014-202 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.payment.IPaymentOutput;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PRS implements IPaymentRequestSpecification {

    private String cryptoCurrency;
    private String description;
    private long validInSeconds;
    private String timeoutRefundAddress;
    private boolean doNotForward;
    private boolean doNotForwardOriginalValue;
    private IWallet wallet;

    private List<IPaymentOutput> outputs = new LinkedList<>();

    public PRS(String cryptoCurrency, String description, long validInSeconds, boolean doNotForward, IWallet wallet) {
        this.cryptoCurrency = cryptoCurrency;
        this.description = description;
        this.validInSeconds = validInSeconds;
        this.doNotForward = doNotForward;
        this.doNotForwardOriginalValue = doNotForward;
        this.wallet = wallet;
    }

    @Override
    public BigDecimal getOptimalMiningFee(BigDecimal feeCalculated, int transactionSize) {
        return new BigDecimal("0");
    }

    @Override
    public void addOutput(String address, BigDecimal amount) {
        outputs.add(new PO(address, amount));
        if (outputs.size() > 1) {
            doNotForward = false; // must be forwarded because it has multiple outputs.
        }
    }

    @Override
    public List<IPaymentOutput> getOutputs() {
        return outputs;
    }

    @Override
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (IPaymentOutput paymentOutput : outputs) {
            total = total.add(paymentOutput.getAmount());
        }
        return total;
    }

    @Override
    public void removeTotalAmountFromOutputs(BigDecimal totalAmountToRemove) {
        BigDecimal totalAmount = getTotal();
        BigDecimal removalRatio = BigDecimal.ONE
                .subtract(totalAmount.subtract(totalAmountToRemove).divide(totalAmount, 16, BigDecimal.ROUND_HALF_UP));
        for (IPaymentOutput paymentOutput : outputs) {
            paymentOutput.removeAmount(paymentOutput.getAmount().multiply(removalRatio));
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public long getValidInSeconds() {
        return validInSeconds;
    }

    @Override
    public int getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction() {
        return -1;
    }

    @Override
    public int getRemoveAfterNumberOfConfirmationsOfIncomingTransaction() {
        return 1;
    }

    @Override
    public void setTimeoutRefundAddress(String timeoutRefundAddress) {
        this.timeoutRefundAddress = timeoutRefundAddress;
    }

    @Override
    public String getTimeoutRefundAddress() {
        return timeoutRefundAddress;
    }

    @Override
    public boolean isDoNotForward() {
        return doNotForward;
    }

    @Override
    public boolean isZeroFixedFee() {
        return true;
    }

    @Override
    public void optimize() {
        Map<String, BigDecimal> sums = new LinkedHashMap<>();
        for (IPaymentOutput output : outputs) {
            BigDecimal amount = sums.get(output.getAddress());
            if (amount != null) {
                sums.put(output.getAddress(), amount.add(output.getAmount()));
            } else {
                sums.put(output.getAddress(), output.getAmount());
            }
        }
        outputs.clear();
        for (Map.Entry<String, BigDecimal> entry : sums.entrySet()) {
            addOutput(entry.getKey(), entry.getValue());
        }
        if (doNotForwardOriginalValue && outputs.size() == 1) {
            doNotForward = true;
        }
    }

    @Override
    public IWallet getWallet() {
        return wallet;
    }

    @Override
    public BigDecimal getTolerance() {
        return new BigDecimal(0.0);
    }

    @Override
    public String toString() {
        return "PRS{" + "description='" + description + '\'' + ", cryptoCurrency=" + cryptoCurrency
                + ", validInSeconds=" + validInSeconds + ", timeoutRefundAddress='" + timeoutRefundAddress + '\''
                + ", doNotForward=" + doNotForward + ", doNotForwardOriginalValue=" + doNotForwardOriginalValue
                + ", outputs=" + outputs + '}';
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }
}
