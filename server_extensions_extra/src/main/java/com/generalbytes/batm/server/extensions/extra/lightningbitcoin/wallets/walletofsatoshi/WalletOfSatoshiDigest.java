package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi;

import com.generalbytes.batm.server.coinutil.Hex;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.HeaderParam;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class WalletOfSatoshiDigest implements ParamsDigest {

    private static final String ALGORITHM = "HmacSHA256";
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Mac mac;
    private final String apiToken;

    public WalletOfSatoshiDigest(String apiToken, String apiSecret) throws NoSuchAlgorithmException, InvalidKeyException {
        this.apiToken = apiToken;
        this.mac = Mac.getInstance(ALGORITHM);
        this.mac.init(new SecretKeySpec(apiSecret.getBytes(CHARSET), ALGORITHM));
    }

    public String digestParams(RestInvocation restInvocation) {

        /*
        Authentication Code Sample:
        var nonce = getCurrentTimestamp();
        var uri = '/api/v1/wallet/payment';
        var requestBody = '{ "address": "3H9v5aZouYWSCLDjQjfyYp8hsneARCUqYD", "currency": "BTC", "amount": "0.00167" }';
        var signature = encodeHex(HmacSha256((uri + nonce + apiToken + requestBody), apiSecret));
         */

        mac.update(restInvocation.getPath().getBytes(CHARSET));
        mac.update(restInvocation.getParamValue(HeaderParam.class, "nonce").toString().getBytes(CHARSET));
        mac.update(apiToken.getBytes(CHARSET));
        mac.update(restInvocation.getRequestBody().getBytes(CHARSET));
        return Hex.bytesToHexString(mac.doFinal());
    }
}



