package com.generalbytes.batm.server.extensions.extra.lightningbitcoin;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ILightningWallet;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentReceipt;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LightningBitcoinPaymentSupport implements IPaymentSupport {
    private static final Logger log = LoggerFactory.getLogger(LightningBitcoinPaymentSupport.class);

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public boolean init(IExtensionContext context) {
        return false;
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (!(spec.getWallet() instanceof ILightningWallet)) {
            throw new IllegalArgumentException("Unsupported Wallet: " + spec.getWallet().getClass());
        }
        ILightningWallet wallet = (ILightningWallet) spec.getWallet();

        if (spec.getOutputs().size() != 1) {
            throw new IllegalStateException("Only 1 output supported");
        }
        String invoice = spec.getOutputs().get(0).getAddress();

        long validTillMillis = System.currentTimeMillis() + (spec.getValidInSeconds() * 1000);

        PaymentRequest request = new PaymentRequest(spec.getCryptoCurrency(), spec.getDescription(), validTillMillis,
            invoice, spec.getTotal(), BigDecimal.ZERO, spec.getRemoveAfterNumberOfConfirmationsOfIncomingTransaction(),
            spec.getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction(), wallet);

        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(() -> {
            try {
                if (request.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
                    return;
                }

                BigDecimal receivedAmount = wallet.getReceivedAmount(invoice, spec.getCryptoCurrency());
                if (receivedAmount != null && receivedAmount.compareTo(BigDecimal.ZERO) > 0) {
                    log.info("Received: {}, Requested: {}, {}", receivedAmount, spec.getTotal(), request);
                    if (spec.getTotal().compareTo(receivedAmount) != 0 && request.getState() != PaymentRequest.STATE_TRANSACTION_INVALID) {
                        // This should not happen, receiving node should not accept a payment when amount does not match the invoice.
                        log.info("Received amount does not match the requested amount");
                        setState(request, PaymentRequest.STATE_TRANSACTION_INVALID);
                        return;
                    }

                    log.info("Amounts matches");
                    setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                    fireNumberOfConfirmationsChanged(request, 999);
                }

            } catch (Throwable t) {
                log.error("", t);
            }

        }, 10, 10, TimeUnit.SECONDS);

        executorService.schedule(() -> {
            try {
                scheduledFuture.cancel(false);
                if (request.getState() != PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
                    log.info("Cancelling {}", request);
                    setState(request, PaymentRequest.STATE_TRANSACTION_TIMED_OUT);
                }
            } catch (Throwable t) {
                log.error("", t);
            }
        }, spec.getValidInSeconds(), TimeUnit.SECONDS);

        return request;
    }


    @Override
    public boolean isPaymentReceived(String paymentAddress) {
        return false;
    }

    @Override
    public PaymentReceipt getPaymentReceipt(String paymentAddress) {
        return null;
    }

    private void fireNumberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations) {
        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.numberOfConfirmationsChanged(request, numberOfConfirmations, IPaymentRequestListener.Direction.INCOMING);
        }
    }

    private void setState(PaymentRequest request, int newState) {
        int previousState = request.getState();
        request.setState(newState);
        log.debug("Transaction state changed: {} -> {} {}", previousState, newState, request);

        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.stateChanged(request, previousState, request.getState());
        }
    }
}
