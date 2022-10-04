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




