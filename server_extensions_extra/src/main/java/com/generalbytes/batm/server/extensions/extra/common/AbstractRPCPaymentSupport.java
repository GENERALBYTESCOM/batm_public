/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcher;
import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherAddressListener;
import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherTransactionListener;
import com.generalbytes.batm.server.extensions.payment.IPaymentOutput;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentReceipt;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

public abstract class AbstractRPCPaymentSupport implements IPaymentSupport{
    private static final Logger log = LoggerFactory.getLogger("batm.master.RPCPaymentSupport");

    private IExtensionContext ctx;
    private final Map<PaymentRequest, PaymentTracker> requests = new HashMap<>();
    private final Map<RPCClient, IBlockchainWatcher> watchers = new HashMap<>();


    @Override
    public boolean init(IExtensionContext ctx) {
        this.ctx = ctx;
        if (ctx != null) {
            ctx.addTask("PaymentRequestTimeoutInvalidator-" + getCurrency(), new PaymentRequestTimeoutInvalidatorTask(), null);
        }
        log_info("Payment support initialized.");
        log_debug("Debug mode enabled.");
        return true;
    }

    public abstract String getCurrency();
    public abstract BigDecimal getMinimumNetworkFee(RPCClient client);
    public abstract BigDecimal getTolerance();
    public abstract long getMaximumWatchingTimeMillis();
    public abstract long getMaximumWaitForPossibleRefundInMillis();
    public abstract int calculateTransactionSize(int numberOfInputs, int numberOfOutputs);
    public abstract BigDecimal calculateTxFee(int numberOfInputs, int numberOfOutputs, RPCClient client);
    public abstract ICryptoAddressValidator getAddressValidator();

    private void log_info(String message) {
        log.info(getCurrency() + ": " + message);
    }
    private void log_debug(String message) {
        log.debug(getCurrency() + ": " + message);
    }
    private void log_warn(String message) {
        log.warn(getCurrency() + ": " + message);
    }
    private void log_error(String message, Throwable e) {
        log.error(getCurrency() + ": " + message, e);
    }

    public String getSigHashType() {
        return "ALL";
    }




    private RPCClient getClient(IWallet wallet) {
        if (wallet != null && (wallet instanceof IRPCWallet)) {
            return ((IRPCWallet) wallet).getClient();
        }
        log.info("Wallet not supported: {}. Must be an instance of {}.", wallet == null ? null : wallet.getClass().getSimpleName(), IRPCWallet.class.getSimpleName());
        return null;
    }

    private synchronized IBlockchainWatcher getWatcher(RPCClient client) {
        IBlockchainWatcher watcher = watchers.get(client);
        if (watcher == null) {
            try {
                if (client.getNetworkInfo().connections() > 0) {
                    watcher = new RPCBlockchainWatcher(client);
                    watchers.put(client, watcher);
                    watcher.start();
                    return watcher;
                }else{
                    log_warn(getCurrency() + " payment support initialization FAILED. Node is not running or is not connected to network.");
                }
            } catch (BitcoinRPCException e) {
                log.error("", e);
            }
        }
        return watcher;
    }

    private static boolean hasHashOfOne(BitcoindRpcClient.RawTransaction transaction, List<BitcoindRpcClient.RawTransaction> transactions) {
        if (transaction == null || transactions == null || transactions.isEmpty()) {
            return false;
        }
        for (BitcoindRpcClient.RawTransaction tx : transactions) {
            if (tx.txId().equals(transaction.txId())) {
                return true;
            }
        }
        return false;
    }



    class PaymentRequestTimeoutInvalidatorTask implements ITask {
        @Override
        public boolean onCreate() {
            log_info("PaymentRequestTimeoutInvalidatorTask.onCreate - Starting to look for timed out transactions.");
            return true;
        }

        @Override
        public boolean onDoStep() {
            synchronized (requests){
                log_info("PaymentRequestTimeoutInvalidatorTask.onDoStep - Checking for timed out requests");
                if (!requests.isEmpty()) {
                    for (Map.Entry<PaymentRequest, PaymentTracker> entry : requests.entrySet()) {
                        PaymentRequest request = entry.getKey();
                        try {
                            if (request.getState() == PaymentRequest.STATE_NEW && request.getValidTill() < System.currentTimeMillis()) {

                                log_warn("PaymentRequestTimeoutInvalidatorTask.onDoStep (1) - Transaction request " + request + " timed out");
                                int previousState = request.getState();
                                request.setState(PaymentRequest.STATE_TRANSACTION_TIMED_OUT);
                                fireStateChanged(request, previousState);
                            }
                        } catch (Throwable e) {
                            log_error("PaymentRequestTimeoutInvalidatorTask.onDoStep (1)", e);
                        }
                        try {
                            if (request.getState() != PaymentRequest.STATE_REMOVED && (request.getValidTill() + getMaximumWatchingTimeMillis()) < System.currentTimeMillis()) {

                                log_warn("PaymentRequestTimeoutInvalidatorTask.onDoStep (2) - Transaction request " + request + " timed out");
                                int previousState = request.getState();

                                if (previousState != PaymentRequest.STATE_TRANSACTION_TIMED_OUT) {
                                    request.setState(PaymentRequest.STATE_TRANSACTION_TIMED_OUT);
                                    fireStateChanged(request, previousState);
                                }

                                previousState = request.getState();
                                request.setState(PaymentRequest.STATE_REMOVED);
                                fireStateChanged(request, previousState);
                                PaymentTracker tracker = entry.getValue();
                                tracker.stopListening();
                            }
                        } catch (Throwable e) {
                            log_error("PaymentRequestTimeoutInvalidatorTask.onDoStep (2)", e);
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void onFinish() {}

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public Object getResult() {
            return null;
        }

        @Override
        public boolean isFailed() {
            return false;
        }

        @Override
        public long getShortestTimeForNexStepInvocation() {
            return 60 * 1000;
        }
    }



    class PaymentTracker implements IBlockchainWatcherAddressListener, IBlockchainWatcherTransactionListener {
        private long createdAt;
        private boolean seenInBlockChain = false;
        private boolean performForward;
        private IPaymentRequestSpecification spec;
        private PaymentRequest request;
        private List<BitcoindRpcClient.RawTransaction> incomingTransactions = new ArrayList<>();
        private List<BitcoindRpcClient.RawTransaction> outgoingTransactions = new ArrayList<>();

        public PaymentTracker(boolean performForward, PaymentRequest request, IPaymentRequestSpecification spec) {
            this.performForward = performForward;
            this.request = request;
            this.spec = spec;
            this.createdAt = System.currentTimeMillis();
        }

        @Override
        public void removedFromWatch(String cryptoCurrency, String transactionHash, Object tag) {
            log_debug("Stopped watching of " + transactionHash + " " + request);
        }

        @Override
        public void newBlockMined(String cryptoCurrency, String transactionHash, Object tag, long blockHeight) {
            if (request.getState() == PaymentRequest.STATE_NEW && (createdAt + getMaximumWaitForPossibleRefundInMillis()) < System.currentTimeMillis()) {
                //awaiting payment was too long even for refund
                log_warn("PaymentTransactionListener.newBlockMined - Removing payment request - it timed out(no refund possible). " + request);
                int previousState = request.getState();
                request.setState(PaymentRequest.STATE_TRANSACTION_TIMED_OUT);
                fireStateChanged(request, previousState);
                previousState = request.getState();
                request.setState(PaymentRequest.STATE_REMOVED);
                fireStateChanged(request, previousState);
                stopListening();
            }  // ELSE make it possible to receive payment (and send it as refund)
        }

        @Override
        public void numberOfConfirmationsChanged(String cryptoCurrency, String transactionHash, Object tag, int numberOfConfirmations) {
            if (incomingTransactions.size() > 0 && request.getState() != PaymentRequest.STATE_REMOVED) {
                if (!seenInBlockChain) {

                    boolean performConfirmation = false;
                    BitcoindRpcClient.RawTransaction transaction = (BitcoindRpcClient.RawTransaction) tag;

                    if (incomingTransactions.size() > 0 && hasHashOfOne(transaction, incomingTransactions)) {
                        log_debug("PaymentTransactionListener.numberOfConfirmationsChanged - Incoming payment " + request.getAddress() + " appeared in blockchain (" + numberOfConfirmations + "). Confirmed.");
                        performConfirmation = true;
                        seenInBlockChain = true;
                    }

                    if (performConfirmation) {
                        int previousState = request.getState();
                        request.setState(PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                        fireStateChanged(request, previousState);
                    }
                }

                if (request.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
                    if (numberOfConfirmations > 0) {
                        BitcoindRpcClient.RawTransaction transaction = (BitcoindRpcClient.RawTransaction) tag;
                        IPaymentRequestListener.Direction direction = IPaymentRequestListener.Direction.INCOMING;
                        if (hasHashOfOne(transaction, outgoingTransactions)) {
                            direction = IPaymentRequestListener.Direction.OUTGOING;
                        }
                        fireNumberOfConfirmationsChanged(request, numberOfConfirmations,direction);
                        //transaction is waiting to be removed
                        if (direction == IPaymentRequestListener.Direction.INCOMING) {
                            if (numberOfConfirmations >= request.getRemoveAfterNumberOfConfirmationsOfIncomingTransaction()) {
                                request.setRemovalConditionForIncomingTransaction();
                            }
                        } else {
                            if (numberOfConfirmations >= request.getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction()) {
                                request.setRemovalConditionForOutgoingTransaction();
                            }
                        }
                        if (request.isRemovalCondition()) {
                            int previousState = request.getState();
                            request.setState(PaymentRequest.STATE_REMOVED);
                            stopListening();
                            fireStateChanged(request,previousState);
                        }
                    }
                }
            }
        }

        @Override
        public void newTransactionSeen(String cryptoCurrency, String address, String transactionId, int confirmations, Object tag) {
            boolean performRefund = false;
            BitcoindRpcClient.Transaction tx = null;
            try {
                tx = getClient(request.getWallet()).getTransaction(transactionId);
            } catch (BitcoinRPCException e) {
                log.error("", e);
            }

            try {
                // Check the transaction
                if (request.getState() == PaymentRequest.STATE_NEW || request.getState() == PaymentRequest.STATE_SEEN_TRANSACTION || request.getState() == PaymentRequest.STATE_TRANSACTION_TIMED_OUT) {
                    BigDecimal totalCoinsReceived = BigDecimal.ZERO;
                    boolean addressMatched = false;
                    List<BitcoindRpcClient.RawTransaction.Out> outputs = tx.raw().vOut();
                    for (BitcoindRpcClient.RawTransaction.Out output : outputs) {
                        String receivingAddress = RPCClient.cleanAddressFromPossiblePrefix(output.scriptPubKey().addresses().get(0));

                        if (receivingAddress != null) {
                            if (receivingAddress.equals(request.getAddress())) {
                                totalCoinsReceived = totalCoinsReceived.add(new BigDecimal(output.value() +""));
                                addressMatched = true;
                            }
                        }
                    }

                    if (addressMatched) {
                        if (request.getState() == PaymentRequest.STATE_TRANSACTION_TIMED_OUT || (createdAt + (spec.getValidInSeconds() * 1000)) < System.currentTimeMillis()) {
                            log_warn("PaymentTransactionListener.onTransaction - Transaction ignored, it came too late.");

                            if (request.getState() != PaymentRequest.STATE_TRANSACTION_TIMED_OUT) {
                                int previousState = request.getState();
                                request.setState(PaymentRequest.STATE_TRANSACTION_TIMED_OUT);
                                fireStateChanged(request, previousState);
                            }

                            int previousState = request.getState();
                            request.setState(PaymentRequest.STATE_REMOVED);
                            fireStateChanged(request, previousState);
                            stopListening();
                            performRefund = true;
                            return;
                        }

                        // Does the total coins match the request?
                        boolean exactMatch = totalCoinsReceived.compareTo(request.getAmount()) == 0;
                        boolean matchInTolerance = false;
                        BigDecimal toleranceRemain = BigDecimal.ZERO;

                        if (!exactMatch) {
                            if (totalCoinsReceived.compareTo(request.getAmount()) < 0) { //customer sent less coins
                                if (totalCoinsReceived.add(request.getTolerance()).compareTo(request.getAmount()) >= 0) {
                                    matchInTolerance = true;
                                    toleranceRemain = totalCoinsReceived.subtract(request.getAmount());
                                }
                            }
                            if (totalCoinsReceived.compareTo(request.getAmount()) > 0) { //customer sent more coins
                                if (totalCoinsReceived.subtract(request.getTolerance()).compareTo(request.getAmount()) <= 0) {
                                    matchInTolerance = true;
                                    toleranceRemain = totalCoinsReceived.subtract(request.getAmount());
                                }
                            }
                        }

                        if (exactMatch || matchInTolerance) {
                            if (!performForward) {
                                // It is not forwarding transaction
                                log_info("PaymentTransactionListener.onTransaction - Amounts matched " + (exactMatch ? "exactly" : "") + (matchInTolerance ? "in tolerance" : "") + " " + request.getAddress()
                                    + ". NOT Creating forwarding transaction. " + request.getLogInfoWatchingFor());
                                request.setTxValue(totalCoinsReceived);
                                request.setIncomingTransactionHash(tx.txId());
                                incomingTransactions.add(tx.raw());
                                int previousState = request.getState();
                                request.setState(PaymentRequest.STATE_SEEN_TRANSACTION);
                                startWatchingTransaction(getClient(request.getWallet()), request.getCryptoCurrency(), tx.txId(), this, tx.raw());

                                fireStateChanged(request, previousState);
                                fireNumberOfConfirmationsChanged(request, 0, IPaymentRequestListener.Direction.INCOMING);
                            }else{
                                // It is forwarding transaction
                                log_info("Amounts matched " + (exactMatch ? "exactly" : "") + (matchInTolerance ? "in tolerance" : "") + " " + request.getAddress() + ". Creating forwarding transaction. " + request.getLogInfoWatchingFor() + "\ntx = " + tx);
                                TXForBroadcast newTx = createTransaction(tx.raw(), request, spec, toleranceRemain);
                                if (newTx != null) {
                                    BigDecimal txValue = totalCoinsReceived.subtract(getMinimumNetworkFee(getClient(request.getWallet())));
                                    request.setTxValue(txValue);
                                    request.setIncomingTransactionHash(tx.txId());
                                    incomingTransactions.add(tx.raw());
                                    int previousState = request.getState();
                                    request.setState(PaymentRequest.STATE_SEEN_TRANSACTION);
                                    outgoingTransactions.add(newTx.rawTx);
                                    log_debug("PaymentTransactionListener.onTransaction - Serialized transaction: " + newTx.rawTxSerializedHex);
                                    log_debug("PaymentTransactionListener.onTransaction - Broadcast transaction: " + newTx.rawTx);

                                    startWatchingTransaction(getClient(request.getWallet()), request.getCryptoCurrency(), tx.txId(), this, tx.raw());
                                    getClient(request.getWallet()).sendRawTransaction(newTx.rawTxSerializedHex);
                                    startWatchingTransaction(getClient(request.getWallet()), request.getCryptoCurrency(), newTx.rawTx.txId(), this, newTx.rawTx);

                                    fireStateChanged(request, previousState);
                                    fireNumberOfConfirmationsChanged(request, 0, IPaymentRequestListener.Direction.INCOMING);
                                    fireNumberOfConfirmationsChanged(request, 0, IPaymentRequestListener.Direction.OUTGOING);
                                } else {
                                    log_warn("PaymentTransactionListener.onTransaction - Transaction ignored, didn't match tolerance");
                                    performRefund = true;
                                    int previousState = request.getState();
                                    request.setState(PaymentRequest.STATE_TRANSACTION_INVALID);
                                    fireStateChanged(request, previousState);
                                    previousState = request.getState();
                                    request.setState(PaymentRequest.STATE_REMOVED);
                                    fireStateChanged(request, previousState);
                                    stopListening();
                                }
                            }
                        } else {
                            log_warn("PaymentTransactionListener.onTransaction - Amounts did not match " + request.getAddress() + " expected: " + request.getAmount() + " received: " + totalCoinsReceived.toPlainString());
                            performRefund = true;
                            int previousState = request.getState();
                            request.setState(PaymentRequest.STATE_TRANSACTION_INVALID);
                            fireStateChanged(request, previousState);
                            previousState = request.getState();
                            request.setState(PaymentRequest.STATE_REMOVED);
                            fireStateChanged(request, previousState);
                            stopListening();
                        }
                    }
                }
            } catch (BitcoinRPCException e) {
                log.error("", e);
            } finally {
                if (performRefund) {
                    if (!performForward) {
                        //refunding is possible only when forwarding
                        log_warn("PaymentTransactionListener.onTransaction - Removing payment request - it timed out or amount didn't match (NOT REFUNDING). " + request);
                    }else if (!request.wasAlreadyRefunded()) {
                        log_warn("PaymentTransactionListener.onTransaction - Removing payment request - it timed out or amount didn't match (refunding). " + request);
                        //send refund
                        request.setAsAlreadyRefunded();
                        TXForBroadcast newTx = createRefundTransaction(tx.raw(), request, spec);
                        if (newTx != null) {
                            log_debug("PaymentTransactionListener.onTransaction - Serialized refund transaction: " + newTx.rawTxSerializedHex);
                            log_debug("PaymentTransactionListener.onTransaction - Broadcast refund transaction = " + newTx.rawTx);
                            try {
                                getClient(request.getWallet()).sendRawTransaction(newTx.rawTxSerializedHex);
                            } catch (BitcoinRPCException e) {
                                log.error("", e);
                            }
                        }
                    }
                }
            }

        }

        public void stopListening() {
            stopWatchingAddresses(getClient(request.getWallet()), this);
            stopWatchingTransactions(getClient(request.getWallet()), this);
        }
    }

    private void stopWatchingTransactions(RPCClient client, IBlockchainWatcherTransactionListener l) {
        getWatcher(client).removeTransactions(l);
    }

    private void stopWatchingAddresses(RPCClient client, IBlockchainWatcherAddressListener l) {
        getWatcher(client).removeAddresses(l);
    }

    private void startWatchingTransaction(RPCClient client, String cryptoCurrency, String txId, IBlockchainWatcherTransactionListener l, Object tag) {
        getWatcher(client).addTransaction(cryptoCurrency, txId, l, tag);
    }

    private void startWatchingAddress(RPCClient client, String cryptoCurrency, String address, IBlockchainWatcherAddressListener l, Object tag) {
        getWatcher(client).addAddress(cryptoCurrency, address, l, tag);
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        try {
            long validTill = System.currentTimeMillis() + (spec.getValidInSeconds() * 1000);
            String paymentAddress = null;

            BigDecimal cryptoTotalToSend = spec.getTotal();
            if (spec.isDoNotForward() && spec.getOutputs().size() == 1) { //Sometimes it is inefficient to forward transaction when only one output is defined
                paymentAddress = spec.getOutputs().get(0).getAddress();
                //no need to modify cryptoTotalToSend value as we will not be forwarding coins we don't need extra money for forwarding
            }else{
                paymentAddress = RPCClient.cleanAddressFromPossiblePrefix(getClient(spec.getWallet()).getNewAddress());

                //add additional fee
                int outputs = spec.getOutputs().size();
                BigDecimal feeCalculated = calculateTxFee(1, outputs, getClient(spec.getWallet()));
                BigDecimal optimalMiningFee = spec.getOptimalMiningFee(feeCalculated, calculateTransactionSize(1, outputs));

                if (spec.isZeroFixedFee()) {
                    cryptoTotalToSend = cryptoTotalToSend.add(optimalMiningFee);
                } else {
                    // correct outputs: remove mining fee from expected total amount that will be I/O difference for creating mining fee
                    spec.removeTotalAmountFromOutputs(optimalMiningFee);
                }
            }

            cryptoTotalToSend = cryptoTotalToSend.setScale(6, BigDecimal.ROUND_HALF_UP); //round to 6 decimal places
            final PaymentRequest paymentRequest = new PaymentRequest(
                spec.getCryptoCurrency(),
                spec.getDescription(),
                validTill,
                paymentAddress,
                cryptoTotalToSend,
                getTolerance(),
                spec.getRemoveAfterNumberOfConfirmationsOfIncomingTransaction(),
                spec.getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction(),
                spec.getWallet()
                );



            PaymentTracker paymentTracker = new PaymentTracker(!spec.isDoNotForward(), paymentRequest, spec);
            requests.put(paymentRequest, paymentTracker);
            startWatchingAddress(getClient(spec.getWallet()), getCurrency(), paymentAddress, paymentTracker,paymentRequest); //start watching the address
            return paymentRequest;
        } catch (BitcoinRPCException e) {
            log.error("", e);
        }
        return null;
    }

    class TX {
        List<BitcoindRpcClient.TxInput> inputs = new ArrayList<>();
        List<BitcoindRpcClient.TxOutput> outputs = new ArrayList<>();

        void addOutput(BigDecimal value, String address) {
            outputs.add(new BitcoindRpcClient.BasicTxOutput(address,value));
        }

        public void addInput(BitcoindRpcClient.RawTransaction.Out sourceOutput) {
            inputs.add(new BitcoindRpcClient.BasicTxInput(sourceOutput.transaction().txId(),sourceOutput.n()));
        }

        @Override
        public String toString() {
            return "TX{" +
                "inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
        }
    }

    class TXForBroadcast {
        BitcoindRpcClient.RawTransaction rawTx;
        String rawTxSerializedHex;

        public TXForBroadcast(BitcoindRpcClient.RawTransaction rawTx, String rawTxSerializedHex) {
            this.rawTx = rawTx;
            this.rawTxSerializedHex = rawTxSerializedHex;
        }

        @Override
        public String toString() {
            return "TXForBroadcast{" +
                "rawTx=" + rawTx +
                ", rawTxSerializedHex='" + rawTxSerializedHex + '\'' +
                '}';
        }
    }

    private TXForBroadcast createTransaction(BitcoindRpcClient.RawTransaction sourceTransaction, PaymentRequest request, IPaymentRequestSpecification spec, BigDecimal remain) {
        TX tx = new TX();
        log_debug("createTransaction - Create new transaction for " + sourceTransaction + " " + request + " " + spec + " " + remain);
        try {
            boolean resolvedRemain = remain.compareTo(BigDecimal.ZERO) == 0;

            //add outputs
            List<IPaymentOutput> fwdOutputs = spec.getOutputs();
            for (IPaymentOutput paymentOutput : fwdOutputs) {
                BigDecimal outputAmount = paymentOutput.getAmount();
                if (!resolvedRemain) {
                    if (remain.compareTo(BigDecimal.ZERO) > 0) {
                        //customer sent more
                        outputAmount = outputAmount.add(remain);
                        resolvedRemain = true;
                    } else if (remain.compareTo(BigDecimal.ZERO) < 0) {
                        //customer sent less
                        if (remain.abs().compareTo(outputAmount) < 0) {
                            //we are able to compensate this value
                            outputAmount = outputAmount.subtract(remain.abs());
                            resolvedRemain = true;
                        }
                    }
                }
                tx.addOutput(outputAmount, paymentOutput.getAddress());
            }
            if (!resolvedRemain) {
                log_warn("createTransaction - Could not create new transaction for " + request.getAddress() + " remain solution doesn't exist.");
                return null;
            }

            //add inputs
            List<BitcoindRpcClient.RawTransaction.Out> sourceTransactionOutputs = sourceTransaction.vOut();
            for (BitcoindRpcClient.RawTransaction.Out sourceOutput : sourceTransactionOutputs) {
                String outputAddress = sourceOutput.scriptPubKey().addresses().get(0);
                if (outputAddress != null) {
                    if (RPCClient.cleanAddressFromPossiblePrefix(outputAddress).equals(request.getAddress())) {
                        tx.addInput(sourceOutput);
                    }
                }
            }
            log_debug("createTransaction - Created for " + request.getAddress() + " transaction: tx = " + tx);
            String hexRawTransaction =  getClient(request.getWallet()).createRawTransaction(tx.inputs, tx.outputs);
            hexRawTransaction = getClient(request.getWallet()).signRawTransaction(hexRawTransaction, null, null, this.getSigHashType());
            BitcoindRpcClient.RawTransaction rawTransaction = getClient(request.getWallet()).decodeRawTransaction(hexRawTransaction);
            return new TXForBroadcast(rawTransaction, hexRawTransaction);

        } catch (BitcoinRPCException e) {
            log_error("createTransaction", e);
        }
        return null;
    }


    @SuppressWarnings("Duplicates")
    private TXForBroadcast createRefundTransaction(BitcoindRpcClient.RawTransaction sourceTransaction, PaymentRequest request, IPaymentRequestSpecification spec) {
        TX tx = new TX();
        try {
            BigDecimal totalCoinsReceived = BigDecimal.ZERO;
            boolean addressMatched = false;

            List<BitcoindRpcClient.RawTransaction.Out> outputs = sourceTransaction.vOut();
            for (BitcoindRpcClient.RawTransaction.Out output : outputs) {
                String receivingAddress = output.scriptPubKey().addresses().get(0);
                if (receivingAddress != null) {
                    if (receivingAddress.equals(request.getAddress())) {
                        totalCoinsReceived = totalCoinsReceived.add(new BigDecimal(output.value() +""));
                        addressMatched = true;
                    }
                }
            }

            if (addressMatched) {
                String timeoutAddress = spec.getTimeoutRefundAddress();
                if (timeoutAddress != null) {
                    if (!getAddressValidator().isAddressValid(timeoutAddress)) {
                        timeoutAddress = null;
                    }
                }
                if (timeoutAddress == null) {
                    //timeout address wasn't defined, so send the coins back
                    List<BitcoindRpcClient.RawTransaction.In> inputs = sourceTransaction.vIn();
                    for (BitcoindRpcClient.RawTransaction.In input : inputs) {
                        String inputAddress = input.getTransactionOutput().scriptPubKey().addresses().get(0);
                        if (inputAddress != null) {
                            timeoutAddress = inputAddress;
                            break;
                        }
                    }
                }
                BigDecimal toSendBack = totalCoinsReceived.subtract(getMinimumNetworkFee(getClient(request.getWallet())));
                if (toSendBack.compareTo(BigDecimal.ZERO) > 0) {
                    tx.addOutput(toSendBack, timeoutAddress);
                    //add inputs
                    List<BitcoindRpcClient.RawTransaction.Out> sourceTransactionOutputs = sourceTransaction.vOut();
                    for (BitcoindRpcClient.RawTransaction.Out sourceOutput : sourceTransactionOutputs) {
                        String outputAddress = sourceOutput.scriptPubKey().addresses().get(0);
                        if (outputAddress != null) {
                            if (RPCClient.cleanAddressFromPossiblePrefix(outputAddress).equals(request.getAddress())) {
                                tx.addInput(sourceOutput);
                            }
                        }
                    }
                    log_debug("createTransaction - Created for " + request.getAddress() + " transaction: tx = " + tx);
                    String hexRawTransaction =  getClient(request.getWallet()).createRawTransaction(tx.inputs, tx.outputs);
                    hexRawTransaction = getClient(request.getWallet()).signRawTransaction(hexRawTransaction, null, null, this.getSigHashType());
                    BitcoindRpcClient.RawTransaction rawTransaction = getClient(request.getWallet()).decodeRawTransaction(hexRawTransaction);
                    log_debug("createRefundTransaction - Created for " + request.getAddress() + " refund transaction: tx = " + tx);
                    fireRefundSent(request,timeoutAddress,toSendBack);
                    return new TXForBroadcast(rawTransaction, hexRawTransaction);
                }
            }
        } catch (BitcoinRPCException e) {
            log_error("createRefundTransaction", e);
        }
        return null;
    }

    public boolean isPaymentReceived(String paymentAddress){
        synchronized (requests) {
            for (PaymentRequest paymentRequest : requests.keySet()) {
                if (paymentRequest.getAddress().equals(paymentAddress)) {
                    if (paymentRequest.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN || paymentRequest.getState() == PaymentRequest.STATE_SEEN_TRANSACTION) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public PaymentReceipt getPaymentReceipt(String paymentAddress) {
        PaymentReceipt result = new PaymentReceipt(getCurrency(), paymentAddress);
        for (PaymentRequest paymentRequest : requests.keySet()) {
            if (paymentRequest.getAddress().equals(paymentAddress)) {
                switch (paymentRequest.getState()) {
                    case PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN:
                        result.setStatus(PaymentReceipt.STATUS_PAID);
                        result.setConfidence(PaymentReceipt.CONFIDENCE_SURE);
                        break;
                    case PaymentRequest.STATE_SEEN_TRANSACTION:
                        result.setStatus(PaymentReceipt.STATUS_PAID);
                        result.setConfidence(PaymentReceipt.CONFIDENCE_NONE);
                        break;
                }
                result.setAmount(paymentRequest.getAmount());
                result.setTransactionId(paymentRequest.getIncomingTransactionHash());
            }
        }
        return result;
    }



    private void fireNumberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations, IPaymentRequestListener.Direction direction) {
        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.numberOfConfirmationsChanged(request,numberOfConfirmations, direction);
        }
    }


    private void fireRefundSent(PaymentRequest request, String toAddress, BigDecimal amount) {
        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.refundSent(request, request.getCryptoCurrency(), toAddress, amount);
        }
    }

    private void fireStateChanged(PaymentRequest request, int oldState) {
        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.stateChanged(request,oldState,request.getState());
        }
    }
}
