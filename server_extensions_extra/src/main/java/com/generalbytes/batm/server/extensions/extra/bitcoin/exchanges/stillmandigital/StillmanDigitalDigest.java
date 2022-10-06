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

import com.generalbytes.batm.server.coinutil.Hex;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class StillmanDigitalDigest implements ParamsDigest {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Mac mac;

    public StillmanDigitalDigest(String apiSecret) throws GeneralSecurityException {
        mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(new SecretKeySpec(apiSecret.getBytes(CHARSET), HMAC_SHA256_ALGORITHM));
    }

    public String digestParams(RestInvocation restInvocation) {
        // String dataForSign = method + BALANCE_URL_PART + validUntilSeconds + body;
        String dataForSign = restInvocation.getHttpMethod()
            + restInvocation.getMethodPath()
            + restInvocation.getHttpHeadersFromParams().get(IStillmanDigitalAPI.API_EXPIRES_HEADER)
            + restInvocation.getRequestBody();
        return signHmacSha256(dataForSign);
    }

    private String signHmacSha256(String data) {
        byte[] signData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Hex.bytesToHexString(signData);
    }
}




