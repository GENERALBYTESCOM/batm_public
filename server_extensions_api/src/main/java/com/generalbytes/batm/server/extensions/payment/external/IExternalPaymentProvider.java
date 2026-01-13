package com.generalbytes.batm.server.extensions.payment.external;

/**
 * Interface for payment providers that can be used to initiate payments outside BATM.
 * For example, by using a third-party payment gateway and redirecting the customer to their payment website.
 */
public interface IExternalPaymentProvider {

    /**
     * Name of the payment provider that can be noted in CAS.
     */
    String getPublicName();

    /**
     * Initiates an external payment session based on the provided request.
     * <p>
     * <b>Warning:</b> The payment session initiated by this call should expire before the order expires in CAS (see CAS configuration).
     * @param request details of the payment to be initiated
     * @return details of the initiated payment session (typically a payment link)
     */
    ExternalPaymentDetails initiateExternalPayment(ExternalPaymentRequest request);
}
