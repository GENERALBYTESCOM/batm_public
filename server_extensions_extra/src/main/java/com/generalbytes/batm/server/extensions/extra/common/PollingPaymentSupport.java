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
package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentReceipt;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class PollingPaymentSupport implements IPaymentSupport {
    protected static final Logger log = LoggerFactory.getLogger(PollingPaymentSupport.class);
    private static final long MAXIMUM_WATCHING_TIME_MILLIS = TimeUnit.DAYS.toMillis(1);
    private static final long REMOVE_REQUESTS_AFTER_MILLIS = TimeUnit.MINUTES.toMillis(5);

    private final List<Integer> stopPollingStates = Arrays.asList(
        PaymentRequest.STATE_TRANSACTION_INVALID,
        PaymentRequest.STATE_REMOVED,
        PaymentRequest.STATE_SOMETHING_ARRIVED_AFTER_TIMEOUT,
        PaymentRequest.STATE_TRANSACTION_TIMED_OUT
    );

    protected final Map<String, PaymentRequest> requests = new ConcurrentHashMap<>();
    private final Map<PaymentRequest, Integer> requestConfirmations = Collections.synchronizedMap(new WeakHashMap<>());
    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private static boolean maxWatchingTimeReached(PaymentRequest request) {
        return System.currentTimeMillis() > request.getValidTill() + MAXIMUM_WATCHING_TIME_MILLIS;
    }

    @Override
    public boolean init(IExtensionContext context) {
        return true;
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (spec.getOutputs().size() != 1) {
            throw new IllegalStateException("Only 1 output supported");
        }
        if (spec.getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction() != -1) {
            throw new IllegalStateException("Only non-forwarding requests supported");
        }

        String address = spec.getOutputs().get(0).getAddress();

        long validTillMillis = System.currentTimeMillis() + (spec.getValidInSeconds() * 1000);

        PaymentRequest request = new PaymentRequest(spec.getCryptoCurrency(), spec.getDescription(), validTillMillis,
            address, spec.getTotal(), BigDecimal.ZERO, spec.getRemoveAfterNumberOfConfirmationsOfIncomingTransaction(), spec.getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction(), spec.getWallet(), spec.getTimeoutRefundAddress(),
            spec.getOutputs(), spec.isDoNotForward(), null);

        registerPaymentRequest(request);
        return request;
    }

    @Override
    public void registerPaymentRequest(PaymentRequest request) {

        executorService.scheduleAtFixedRate(() -> {

                try {
                    poll(request);
                } catch (StopPollingException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("", e);
                }

                if (request.isRemovalCondition()) {
                    stopPolling(request, PaymentRequest.STATE_REMOVED, "request got enough confirmations");
                }

                if (stopPollingStates.contains(request.getState())) {
                    stopPolling(request, request.getState(), "negative request state");
                }

                if (request.getState() == PaymentRequest.STATE_NEW && System.currentTimeMillis() > request.getValidTill()) {
                    stopPolling(request, PaymentRequest.STATE_TRANSACTION_TIMED_OUT, "transaction not seen");
                }

                if (maxWatchingTimeReached(request)) {
                    stopPolling(request, PaymentRequest.STATE_TRANSACTION_TIMED_OUT, "transaction not getting confirmed");
                }
            },
            getPollingInitialDelayMillis(), getPollingPeriodMillis(), TimeUnit.MILLISECONDS);

        requests.entrySet().removeIf(e -> maxWatchingTimeReached(e.getValue())); // just a safety catch
        requests.put(request.getAddress(), request);
    }

    private void stopPolling(PaymentRequest request, int newState, String message) throws StopPollingException {
        log.info("Stopping polling, {}, {}", message, request);
        // remove request after some time so a POS can still ask for a payment receipt
        executorService.schedule(() -> {
            setState(request, newState);
            PaymentRequest removed = requests.remove(request.getAddress());
            log.debug("Request removed: {}", removed);
        }, REMOVE_REQUESTS_AFTER_MILLIS, TimeUnit.MILLISECONDS);
        throw new StopPollingException();
    }

    private static class StopPollingException extends RuntimeException {
    }

    @Override
    public boolean isPaymentReceived(String paymentAddress) {
        PaymentRequest paymentRequest = requests.get(paymentAddress);
        return paymentRequest != null && paymentRequest.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN;
    }

    @Override
    public PaymentReceipt getPaymentReceipt(String paymentAddress) {
        PaymentReceipt result = new PaymentReceipt(getCryptoCurrency(), paymentAddress);
        PaymentRequest paymentRequest = requests.get(paymentAddress);
        if (paymentRequest != null && paymentRequest.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
            result.setStatus(PaymentReceipt.STATUS_PAID);
            result.setConfidence(PaymentReceipt.CONFIDENCE_SURE);
            result.setAmount(paymentRequest.getAmount());
            result.setTransactionId(paymentRequest.getIncomingTransactionHash());
        }
        return result;
    }

    /**
     * listener called if number of confirmations changed
     *
     * @param request
     * @param numberOfConfirmations
     */
    protected void updateNumberOfConfirmations(PaymentRequest request, int numberOfConfirmations) {
        Integer previous = requestConfirmations.put(request, numberOfConfirmations);
        if (previous != null && previous.equals(numberOfConfirmations)) {
            return;
        }
        log.info("{} confirmations for {}", numberOfConfirmations, request);

        if (numberOfConfirmations >= request.getRemoveAfterNumberOfConfirmationsOfIncomingTransaction()) {
            request.setRemovalConditionForIncomingTransaction();
        }

        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.numberOfConfirmationsChanged(request, numberOfConfirmations, IPaymentRequestListener.Direction.INCOMING);
        }
    }

    protected void setState(PaymentRequest request, int newState) {
        int previousState = request.getState();
        if (previousState == newState) {
            return;
        }
        request.setState(newState);
        log.debug("Transaction state changed: {} -> {} {}", previousState, newState, request);

        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.stateChanged(request, previousState, request.getState());
        }
    }

    protected long getPollingPeriodMillis() {
        return TimeUnit.SECONDS.toMillis(5);
    }

    protected long getPollingInitialDelayMillis() {
        return TimeUnit.SECONDS.toMillis(15);
    }

    protected abstract String getCryptoCurrency();

    /**
     * Polling is stopped if this method throws {@link StopPollingException} or {@link Error}
     * or if request state is set to one of {@link PollingPaymentSupport#stopPollingStates}
     * or numberOfConfirmations >= request.getRemoveAfterNumberOfConfirmationsOfIncomingTransaction().
     * Any other exceptions are just logged and does not stop future polling.
     *
     * @param request
     */
    protected abstract void poll(PaymentRequest request);
}
