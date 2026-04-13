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
package com.generalbytes.batm.server.extensions.examples.externalpayment;

import com.generalbytes.batm.server.extensions.payment.external.ExternalPaymentDetails;
import com.generalbytes.batm.server.extensions.payment.external.ExternalPaymentRequest;
import com.generalbytes.batm.server.extensions.payment.external.IExternalPaymentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock implementation of {@link IExternalPaymentProvider} for testing purposes.
 * Logs all input data and returns a test payment link.
 */
public class MockExternalPaymentProvider implements IExternalPaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(MockExternalPaymentProvider.class);

    @Override
    public String getPublicName() {
        return "Testing External Payment Provider";
    }

    @Override
    public ExternalPaymentDetails initiateExternalPayment(ExternalPaymentRequest request) {
        log.info("MockExternalPaymentProvider.initiateExternalPayment called");
        log.info("  externalPaymentId: {}", request.getExternalPaymentId());
        log.info("  remoteTransactionId: {}", request.getRemoteTransactionId());
        log.info("  fiatAmount: {}", request.getFiatAmount());
        log.info("  fiatCurrency: {}", request.getFiatCurrency());

        ExternalPaymentDetails paymentDetails = new ExternalPaymentDetails();
        String paymentLink = String.format("https://www.google.com/search?q=externalPaymentId=%s&remoteTransactionId=%s",
                request.getExternalPaymentId(), request.getRemoteTransactionId());
        paymentDetails.setPaymentLink(paymentLink);

        log.info("MockExternalPaymentProvider returning payment link: {}", paymentLink);
        return paymentDetails;
    }
}
