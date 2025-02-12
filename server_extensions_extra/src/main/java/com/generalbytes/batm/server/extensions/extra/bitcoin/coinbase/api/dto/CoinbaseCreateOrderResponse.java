/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response to creating a new Order at Coinbase.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseCreateOrderResponse {

    /**
     * Whether the order was created.
     */
    private boolean success;
    @JsonProperty("success_response")
    private CoinbaseCreateOrderSuccessResponse successResponse;
    @JsonProperty("error_response")
    private CoinbaseCreateOrderErrorResponse errorResponse;

    /**
     * @return {@code true} if the order was created, {@code false} otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return Data about the created order. Null if not successful.
     */
    public CoinbaseCreateOrderSuccessResponse getSuccessResponse() {
        return successResponse;
    }

    public void setSuccessResponse(CoinbaseCreateOrderSuccessResponse successResponse) {
        this.successResponse = successResponse;
    }

    public CoinbaseCreateOrderErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(CoinbaseCreateOrderErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}
