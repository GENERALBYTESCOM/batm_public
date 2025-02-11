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
 * Details about a failed order.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseCreateOrderErrorResponse {

    private String error;
    private String message;
    @JsonProperty("error_details")
    private String errorDetails;
    @JsonProperty("preview_failure_reason")
    private String previewFailureReason;
    @JsonProperty("new_order_failure_reason")
    private String newOrderFailureReason;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public String getPreviewFailureReason() {
        return previewFailureReason;
    }

    public void setPreviewFailureReason(String previewFailureReason) {
        this.previewFailureReason = previewFailureReason;
    }

    public String getNewOrderFailureReason() {
        return newOrderFailureReason;
    }

    public void setNewOrderFailureReason(String newOrderFailureReason) {
        this.newOrderFailureReason = newOrderFailureReason;
    }

    @Override
    public String toString() {
        return "CoinbaseCreateOrderErrorResponse{" +
            "error='" + error + '\'' +
            ", message='" + message + '\'' +
            ", errorDetails='" + errorDetails + '\'' +
            ", previewFailureReason='" + previewFailureReason + '\'' +
            ", newOrderFailureReason='" + newOrderFailureReason + '\'' +
            '}';
    }
}
