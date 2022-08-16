/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoincash.test;

import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.payment.IPaymentOutput;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PRS implements IPaymentRequestSpecification{

    private String cryptoCurrency;
    private String description;
    private long validInSeconds;
    private int removeAfterNumberOfConfirmationsOfIncomingTransaction = -1;
    private int removeAfterNumberOfConfirmationsOfOutgoingTransaction = -1;
    private String timeoutRefundAddress;
    private boolean doNotForward;
    private boolean doNotForwardOriginalValue;
    private boolean zeroFixedFee;
    private BigDecimal minimumMiningFeePerByte;
    private BigDecimal maximumMiningFeePerByte;
    private BigDecimal tolerance;
    private boolean overageAllowed;
    private IWallet wallet;

    private List<IPaymentOutput> outputs = new LinkedList<>();

    public PRS(String cryptoCurrency, String description, long validInSeconds, int removeAfterNumberOfConfirmations,
               boolean doNotForward, boolean zeroFixedFee,
               BigDecimal minimumMiningFeePerByte, BigDecimal maximumMiningFeePerByte,
               BigDecimal tolerance, boolean overageAllowed, IWallet wallet) {
        this.cryptoCurrency = cryptoCurrency;
        this.description = description;
        this.validInSeconds = validInSeconds;
        this.removeAfterNumberOfConfirmationsOfIncomingTransaction = removeAfterNumberOfConfirmations;
        this.doNotForward = doNotForward;
        this.doNotForwardOriginalValue = doNotForward;
        this.zeroFixedFee = zeroFixedFee;
        this.minimumMiningFeePerByte = (minimumMiningFeePerByte != null) ? minimumMiningFeePerByte : BigDecimal.ZERO;
        this.maximumMiningFeePerByte = (maximumMiningFeePerByte != null) ? maximumMiningFeePerByte : BigDecimal.ZERO;
        this.tolerance = tolerance;
        this.overageAllowed = overageAllowed;
        this.wallet = wallet;
    }

    public PRS(String cryptoCurrency, String description, long validInSeconds, boolean zeroFixedFee,
               BigDecimal minimumMiningFeePerByte, BigDecimal maximumMiningFeePerByte,
               BigDecimal tolerance, boolean overageAllowed, IWallet wallet) {
        this.cryptoCurrency = cryptoCurrency;
        this.description = description;
        this.validInSeconds = validInSeconds;
        this.zeroFixedFee = zeroFixedFee;
        this.minimumMiningFeePerByte = (minimumMiningFeePerByte != null) ? minimumMiningFeePerByte : BigDecimal.ZERO;
        this.maximumMiningFeePerByte = (maximumMiningFeePerByte != null) ? maximumMiningFeePerByte : BigDecimal.ZERO;
        this.tolerance = tolerance;
        this.overageAllowed = overageAllowed;
        this.wallet = wallet;
    }

    public BigDecimal getOptimalMiningFee(BigDecimal feeCalculated, int transactionSize) {
        BigDecimal size = new BigDecimal(Integer.valueOf(transactionSize).toString());
        BigDecimal feeMinimum = minimumMiningFeePerByte.multiply(size).movePointLeft(8);
        BigDecimal feeMaximum = maximumMiningFeePerByte.multiply(size).movePointLeft(8);
        BigDecimal fee = (feeMinimum.compareTo(feeCalculated) > 0) ? feeMinimum : feeCalculated;
        if (feeMaximum.compareTo(BigDecimal.ZERO) > 0 && feeMaximum.compareTo(fee) < 0) {
            fee = feeMaximum;
        }
        return fee.setScale(8, BigDecimal.ROUND_HALF_UP);
    }

    public void addOutput(String address, BigDecimal amount) {
        outputs.add(new PO(address, amount));
        if (outputs.size() > 1) {
            doNotForward = false; // must be forwarded because it has multiple outputs.
        }
    }

    public List<IPaymentOutput> getOutputs() {
        return outputs;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (IPaymentOutput paymentOutput : outputs) {
            total = total.add(paymentOutput.getAmount());
        }
        return total;
    }

    public void removeTotalAmountFromOutputs(BigDecimal totalAmountToRemove) {
        BigDecimal totalAmount = getTotal();
        BigDecimal removalRatio = BigDecimal.ONE.subtract(totalAmount.subtract(totalAmountToRemove).divide(totalAmount, 16, BigDecimal.ROUND_HALF_UP));
        for (IPaymentOutput paymentOutput : outputs) {
            paymentOutput.removeAmount(paymentOutput.getAmount().multiply(removalRatio));
        }
    }

    public String getDescription() {
        return description;
    }

    public long getValidInSeconds() {
        return validInSeconds;
    }

    public int getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction() {
        return removeAfterNumberOfConfirmationsOfOutgoingTransaction;
    }

    public void setRemoveAfterNumberOfConfirmationsOfOutgoingTransaction(int removeAfterNumberOfConfirmationsOfOutgoingTransaction) {
        this.removeAfterNumberOfConfirmationsOfOutgoingTransaction = removeAfterNumberOfConfirmationsOfOutgoingTransaction;
    }

    public int getRemoveAfterNumberOfConfirmationsOfIncomingTransaction() {
        return removeAfterNumberOfConfirmationsOfIncomingTransaction;
    }

    public void setRemoveAfterNumberOfConfirmationsOfIncomingTransaction(int removeAfterNumberOfConfirmationsOfIncomingTransaction) {
        this.removeAfterNumberOfConfirmationsOfIncomingTransaction = removeAfterNumberOfConfirmationsOfIncomingTransaction;
    }

    public void setTimeoutRefundAddress(String timeoutRefundAddress) {
        this.timeoutRefundAddress = timeoutRefundAddress;
    }

    public String getTimeoutRefundAddress() {
        return timeoutRefundAddress;
    }

    public boolean isDoNotForward() {
        return doNotForward;
    }

    public void setDoNotForward(boolean doNotForward) {
        this.doNotForward = doNotForward;
        this.doNotForwardOriginalValue = doNotForward;
    }

    public boolean isZeroFixedFee() {
        return zeroFixedFee;
    }

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
        return tolerance;
    }

    @Override
    public boolean isOverageAllowed() {
        return overageAllowed;
    }

    @Override
    public String toString() {
        return "PRS{" +
                "description='" + description + '\'' +
                ", cryptoCurrency=" + cryptoCurrency +
                ", validInSeconds=" + validInSeconds +
                ", removeAfterNumberOfConfirmationsOfIncomingTransaction=" + removeAfterNumberOfConfirmationsOfIncomingTransaction +
                ", removeAfterNumberOfConfirmationsOfOutgoingTransaction=" + removeAfterNumberOfConfirmationsOfOutgoingTransaction +
                ", timeoutRefundAddress='" + timeoutRefundAddress + '\'' +
                ", doNotForward=" + doNotForward +
                ", doNotForwardOriginalValue=" + doNotForwardOriginalValue +
                ", outputs=" + outputs +
                ", zeroFixedFee=" + zeroFixedFee +
                ", minimumMiningFeePerByte=" + minimumMiningFeePerByte +
                ", maximumMiningFeePerByte=" + maximumMiningFeePerByte +
                '}';
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    @Override
    public BigDecimal getMinimumMiningFeePerByte() {
        return minimumMiningFeePerByte;
    }

    @Override
    public BigDecimal getMaximumMiningFeePerByte() {
        return maximumMiningFeePerByte;
    }
}
