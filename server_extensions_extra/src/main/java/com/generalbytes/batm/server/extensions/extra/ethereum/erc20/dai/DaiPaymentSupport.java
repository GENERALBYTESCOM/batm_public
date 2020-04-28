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
package com.generalbytes.batm.server.extensions.extra.ethereum.erc20.dai;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.PollingPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.ethereum.etherscan.EtherScan;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;

import java.math.BigDecimal;

public class DaiPaymentSupport extends PollingPaymentSupport implements IPaymentSupport {
    protected EtherScan etherScan = new EtherScan();

    public void poll(PaymentRequest request) {
        try {
            ReceivedAmount addressBalance = etherScan.getAddressBalance(request.getAddress(), request.getCryptoCurrency());

            if (addressBalance.getTotalAmountReceived().compareTo(BigDecimal.ZERO) > 0) {
                log.info("Received: {}, Requested: {}, {}", addressBalance.getTotalAmountReceived(), request.getAmount(), request);
                if (addressBalance.getTotalAmountReceived().compareTo(request.getAmount()) == 0) {
                    if (request.getState() == PaymentRequest.STATE_NEW) {
                        log.info("Amounts matches {}", request);
                        request.setTxValue(addressBalance.getTotalAmountReceived());
                        setState(request, PaymentRequest.STATE_SEEN_TRANSACTION);
                    }
                    if (addressBalance.getConfirmations() > 0) {
                        if (request.getState() == PaymentRequest.STATE_SEEN_TRANSACTION) {
                            setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                        }
                        log.info("{} confirmations for {}", addressBalance.getConfirmations(), request);
                        updateNumberOfConfirmations(request, addressBalance.getConfirmations());
                    }
                } else if (request.getState() != PaymentRequest.STATE_TRANSACTION_INVALID) {
                    log.info("Received amount does not match the requested amount");
                    setState(request, PaymentRequest.STATE_TRANSACTION_INVALID);
                }
            }

        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    protected String getCryptoCurrency() {
        return CryptoCurrency.DAI.getCode();
    }
}
