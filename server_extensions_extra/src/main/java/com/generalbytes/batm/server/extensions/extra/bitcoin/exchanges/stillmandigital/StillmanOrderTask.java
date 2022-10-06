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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.OrdType;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.OrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.OrderStatus;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.RowOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.Side;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class StillmanOrderTask implements ITask {

    private final IStillmanDigitalAPI api;
    private final OrderRequest request;
    private final Logger log;

    private boolean failed;
    private OrderStatus result;
    private long orderId;

    public StillmanOrderTask(IStillmanDigitalAPI api, Side side, String symbol, BigDecimal qty, Logger log) {
        this.log = log;
        this.api = api;
        request = new OrderRequest();
        request.side = side;
        request.symbol = symbol;
        request.orderQty = qty;
        request.ordType = OrdType.MARKET;
        request.clOrdId = "GB_" + ThreadLocalRandom.current().nextInt();
    }

    @Override
    public boolean onCreate() {
        try {
            orderId = api.sendOrder(request).id;
        } catch (IOException e) {
            log.error("Create order task failed", e);
            failed = true;
            result = OrderStatus.UNKNOWN;
            return false;
        }
        return true;
    }

    @Override
    public boolean onDoStep() {
        RowOrderResponse orderResponse;
        try {
            orderResponse = api.getOrder(orderId);
        } catch (IOException e) {
            log.error("Get order failed, try next time", e);
            return false;
        }
        if (orderResponse.leavesQty.compareTo(BigDecimal.ZERO) > 0) {
            log.debug("Order is still active {}", orderResponse);
            return false;
        }
        switch (orderResponse.ordStatus) {
            case REJECTED: {
                result = orderResponse.ordStatus;
                failed = true;
                return true;
            }
            case FILLED: {
                result = orderResponse.ordStatus;
                return true;
            }
            case CANCELED:
            case CANCEL: {
                if (orderResponse.cumQty.compareTo(BigDecimal.ZERO) > 0) {
                    result = OrderStatus.PARTIALLY_FILLED;
                } else {
                    result = orderResponse.ordStatus;
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void onFinish() {

    }

    @Override
    public boolean isFinished() {
        return result != null;
    }

    @Override
    public Object getResult() {
        return result.toString();
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public long getShortestTimeForNexStepInvocation() {
        return 5 * 1000;
    }
}
