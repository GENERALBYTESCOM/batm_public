package com.generalbytes.batm.server.coinutil;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class LnurlUtil {
    public static final String WITHDRAW_PATH = "withdraw";
    public static final String WITHDRAW_CONFIRM_PATH = "withdraw-confirm";
    public static final String LNURL_HRP = "lnurl";

    public static String getLock(String rid) {
        return ("LNURL" + rid).intern();
    }

    // some wallets will display the hostname, e.g. <hostname> is taking too long to pay [...] please contact <hostname>.

    public String getLnurlQrcode(String rid, String uuid, BigDecimal cryptoAmount, String baseUrl) {
        validateBaseUrl(baseUrl);
        Objects.requireNonNull(rid, "RID cannot be null");
        Objects.requireNonNull(uuid, "UUID cannot be null");
        String milliSats = Long.toString(CoinUnit.bitcoinToMSat(cryptoAmount));
        String lnurl = Bech32.encodeString(LNURL_HRP, baseUrl + "/" + WITHDRAW_PATH
            + "?uuid=" + uuid
            + "&rid=" + rid
            + "&millisats=" + milliSats);
        return "LIGHTNING:" + lnurl.toUpperCase();
    }

    public String getConfirmCallbackUrl(String uuid, String rid, String baseUrl) {
        validateBaseUrl(baseUrl);
        Objects.requireNonNull(rid, "RID cannot be null");
        Objects.requireNonNull(uuid, "UUID cannot be null");
        return baseUrl + "/" + WITHDRAW_CONFIRM_PATH
            + "?uuid=" + uuid
            + "&rid=" + rid;
    }

    /**
     * Decodes lnurl (with or without "lightning:" URI scheme prefix
     * @param lnurl lightning:LNURL... or LNURL...
     * @return the decoded URL
     */
    public String decode(String lnurl) throws AddressFormatException {
        Objects.requireNonNull(lnurl, "lnurl cannot be null");
        int colonIndex = lnurl.indexOf(':');
        if (colonIndex >= 0) {
            return Bech32.decodeString(LNURL_HRP, lnurl.substring(colonIndex + 1));
        }
        return Bech32.decodeString(LNURL_HRP, lnurl);
    }

    private String validateBaseUrl(String baseUrl) {
        if (baseUrl == null || !baseUrl.startsWith("https://") || baseUrl.endsWith("/")) {
            throw new RuntimeException("LNURL: Invalid base url configuration: '" + baseUrl + "'. "
                + "It must start with https:// and must not end with '/'");
        }
        return baseUrl;
    }
}
