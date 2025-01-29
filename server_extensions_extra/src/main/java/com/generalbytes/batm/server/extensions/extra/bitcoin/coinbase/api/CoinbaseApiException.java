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
package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.CoinbaseException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseApiError;

import java.util.Arrays;

/**
 * An exception that may be thrown by the Coinbase API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseApiException extends CoinbaseException {

    private final String error;
    private final int code;
    private final CoinbaseApiError[] errors;
    private final CoinbaseApiError[] warnings;

    @JsonCreator
    public CoinbaseApiException(@JsonProperty("error") String error,
                                @JsonProperty("code") int code,
                                @JsonProperty("message") String message,
                                @JsonProperty("errors") CoinbaseApiError[] errors,
                                @JsonProperty("warnings") CoinbaseApiError[] warnings) {
        super(message);
        this.error = error;
        this.code = code;
        this.errors = errors;
        this.warnings = warnings;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (errors != null && errors.length > 0) {
            message += " | Errors: " + Arrays.toString(errors);
        }
        if (warnings != null && warnings.length > 0) {
            message += " | Warnings: " + Arrays.toString(warnings);
        }
        return message;
    }

    public String getError() {
        return error;
    }

    public int getCode() {
        return code;
    }

    public CoinbaseApiError[] getErrors() {
        return errors;
    }

    public CoinbaseApiError[] getWarnings() {
        return warnings;
    }
}
