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
package com.generalbytes.batm.server.extensions.payment;

import com.generalbytes.batm.server.extensions.IWallet;

import java.math.BigDecimal;
import java.util.List;

/**
 * PaymentRequest is created and maintained by implementation of @{@link IPaymentSupport} of particular cryptocurrency based on @{@link IPaymentRequestSpecification}
 * PaymentRequest is then used by server to allow withdrawals or start selling coins on exchange
 *
 */
public class PaymentRequest {

    /**
     * Newly created PaymentRequest should be in this state.
     */
    public static final int STATE_NEW = 0;
    /**
     * Somebody sent coins by incoming transaction to payment address but transaction is not added in blockchain yet.
     */
    public static final int STATE_SEEN_TRANSACTION = 1;
    /**
     * Incoming transaction (payment) was just added into the blockchain
     */
    public static final int STATE_SEEN_IN_BLOCK_CHAIN = 2;
    /**
     * Payment will no longer be monitored by payment support implementation
     */
    public static final int STATE_REMOVED = 3;

    /**
     * Payment (incoming transaction) didn't arrive in specified timeout @see @{@link IPaymentRequestSpecification}
     */
    public static final int STATE_TRANSACTION_TIMED_OUT = 4;

    /**
     * Something went wrong with the payment. Customer for example paid wrong amount.
     */
    public static final int STATE_TRANSACTION_INVALID = 5;

    /**
     * Coins received after timeout.
     */
    public static final int STATE_SOMETHING_ARRIVED_AFTER_TIMEOUT = 6;

    private int state = STATE_NEW;
    private String description;
    private long validTill;
    private String address;
    private String cryptoCurrency;
    private BigDecimal amount;
    private BigDecimal tolerance;
    private boolean nonForwarding;
    private Object tag;
    private IPaymentRequestListener listener;

    private int removeAfterNumberOfConfirmationsOfIncomingTransaction;
    private int removeAfterNumberOfConfirmationsOfOutgoingTransaction;

    private boolean removalConditionForIncomingTransaction;
    private boolean removalConditionForOutgoingTransaction;

    private BigDecimal txValue;
    private String incomingTransactionHash;
    private String outgoingTransactionHash;
    private String timeoutRefundAddress;
    private List<IPaymentOutput> outputs;
    private Integer paymentIndex;

    private boolean alreadyRefunded;

    private IWallet wallet;

    public PaymentRequest(String cryptoCurrency, String description, long validTill, String address, BigDecimal amount, BigDecimal tolerance, int removeAfterNumberOfConfirmationsOfIncomingTransaction, int removeAfterNumberOfConfirmationsOfOutgoingTransaction, IWallet wallet, String timeoutRefundAddress, List<IPaymentOutput> outputs, Boolean nonForwarding, Integer paymentIndex) {
        this.cryptoCurrency = cryptoCurrency;
        this.description = description;
        this.validTill = validTill;
        this.address = address;
        this.amount = amount;
        this.removeAfterNumberOfConfirmationsOfIncomingTransaction = removeAfterNumberOfConfirmationsOfIncomingTransaction;
        this.removeAfterNumberOfConfirmationsOfOutgoingTransaction = removeAfterNumberOfConfirmationsOfOutgoingTransaction;
        this.tolerance = tolerance;
        this.nonForwarding = Boolean.TRUE.equals(nonForwarding);
        txValue = BigDecimal.ZERO;
        this.wallet = wallet;
        this.timeoutRefundAddress = timeoutRefundAddress;
        this.outputs = outputs;
        this.paymentIndex = paymentIndex;
    }

    /**
     * Returns cryptocurrency of this payment
     * @return
     */
    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    /**
     * Returns payment address on which coins should be sent to
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns amount which should be sent to payment address.
     * @return
     */
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Returns description of this payment request from payment specification
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns time in milliseconds. After this time the payment will be marked as timed out.
     * Number of miliseconds since  January 1, 1970 UTC(coordinated universal time)
     * @return
     */
    @SuppressWarnings("all")
    public long getValidTill() {
        return validTill;
    }

    /**
     * Returns state of the the Payment request
     * @return
     */
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @SuppressWarnings("all")
    public int getRemoveAfterNumberOfConfirmationsOfIncomingTransaction() {
        return removeAfterNumberOfConfirmationsOfIncomingTransaction;
    }

    @SuppressWarnings("all")
    public int getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction() {
        return removeAfterNumberOfConfirmationsOfOutgoingTransaction;
    }

    @SuppressWarnings("all")
    public void setRemovalConditionForIncomingTransaction() {
        this.removalConditionForIncomingTransaction = true;
    }

    @SuppressWarnings("all")
    public void setRemovalConditionForOutgoingTransaction() {
        this.removalConditionForOutgoingTransaction = true;
    }

    @SuppressWarnings("all")
    public boolean isRemovalCondition() {
        boolean in = removeAfterNumberOfConfirmationsOfIncomingTransaction < 0 || removalConditionForIncomingTransaction;
        boolean out = removeAfterNumberOfConfirmationsOfOutgoingTransaction < 0 || removalConditionForOutgoingTransaction;
        return in && out;
    }

    /**
     * Amount difference in crypto in which payment is still valid (customer can send a bit less coins)
     * @return
     */
    @SuppressWarnings("all")
    public BigDecimal getTolerance() {
        return tolerance;
    }

    public BigDecimal getTxValue() {
        return txValue;
    }

    public void setTxValue(BigDecimal txValue) {
        this.txValue = txValue;
    }

    public String getIncomingTransactionHash() {
        return incomingTransactionHash;
    }

    public void setIncomingTransactionHash(String incomingTransactionHash) {
        this.incomingTransactionHash = incomingTransactionHash;
    }

    public String getOutgoingTransactionHash() {
        return outgoingTransactionHash;
    }

    public void setOutgoingTransactionHash(String outgoingTransactionHash) {
        this.outgoingTransactionHash = outgoingTransactionHash;
    }

    @SuppressWarnings("all")
    public boolean wasAlreadyRefunded() {
        return alreadyRefunded;
    }

    @SuppressWarnings("all")
    public void setAsAlreadyRefunded() {
        this.alreadyRefunded = true;
    }

    public String getTimeoutRefundAddress() {
        return timeoutRefundAddress;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "state=" + state +
                ", description='" + description + '\'' +
                ", cryptoCurrency='" + cryptoCurrency + '\'' +
                ", validTill=" + validTill +
                ", address='" + address + '\'' +
                ", amount=" + amount +
                ", tolerance=" + tolerance +
                ", tag=" + tag +
                ", listener=" + listener +
                ", removeAfterNumberOfConfirmationsOfIncomingTransaction=" + removeAfterNumberOfConfirmationsOfIncomingTransaction +
                ", removeAfterNumberOfConfirmationsOfOutgoingTransaction=" + removeAfterNumberOfConfirmationsOfOutgoingTransaction +
                ", txValue=" + txValue +
                ", incomingTransactionHash=" + incomingTransactionHash +
                ", alreadyRefunded=" + alreadyRefunded +
                '}';
    }

    /**
     * Tag property is used by Server to ad its internal data on the payment request
     * @param tag
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    public void setListener(IPaymentRequestListener listener) {
        this.listener = listener;
    }

    public IPaymentRequestListener getListener() {
        return listener;
    }

    @SuppressWarnings("all")
    public String getLogInfoWatchingFor() {
        String info = "";
        if (removeAfterNumberOfConfirmationsOfIncomingTransaction > -1) {
            info += removeAfterNumberOfConfirmationsOfIncomingTransaction + " confirmations of incoming transaction";
        }
        if (removeAfterNumberOfConfirmationsOfOutgoingTransaction > -1) {
            info += (info.isEmpty() ? "" : " and ");
            info += removeAfterNumberOfConfirmationsOfOutgoingTransaction + " confirmations of outgoing transaction";
        }
        if (info.isEmpty()) {
            return "ERROR! Missing configuration of watching for confirmations.";
        } else {
            return "Watching for " + info + ".";
        }
    }

    /**
     * Wallet used from used crypto configuration
     * @return
     */
    public IWallet getWallet() {
        return wallet;
    }

    public void setWallet(IWallet wallet) {
        this.wallet = wallet;
    }

    public List<IPaymentOutput> getOutputs() {
        return outputs;
    }

    public Integer getPaymentIndex() {
        return paymentIndex;
    }

    public boolean isNonForwarding() {
        return nonForwarding;
    }
}
