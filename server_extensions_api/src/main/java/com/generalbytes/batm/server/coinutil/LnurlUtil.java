package com.generalbytes.batm.server.coinutil;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class LnurlUtil {
    public static final String WITHDRAW_PATH = "withdraw";
    public static final String WITHDRAW_CONFIRM_PATH = "withdraw-confirm";

    public final String baseUrl = getBaseUrlConfiguration();
    // some wallets will display the hostname, e.g. <hostname> is taking too long to pay [...] please contact <hostname>.

    public String getLnurlQrcode(String rid, String uuid, BigDecimal cryptoAmount) {
        if (baseUrl == null) {
            return null;
        }
        Objects.requireNonNull(rid, "RID cannot be null");
        Objects.requireNonNull(uuid, "UUID cannot be null");
        String milliSats = Long.toString(CoinUnit.bitcoinToMSat(cryptoAmount));
        String lnurl = Bech32.encodeString("lnurl", baseUrl + "/" + WITHDRAW_PATH
            + "?uuid=" + uuid
            + "&rid=" + rid
            + "&millisats=" + milliSats);
        return "LIGHTNING:" + lnurl.toUpperCase();
    }

    public String getConfirmCallbackUrl(String uuid, String rid) {
        if (baseUrl == null) {
            return null;
        }
        Objects.requireNonNull(rid, "RID cannot be null");
        Objects.requireNonNull(uuid, "UUID cannot be null");
        return baseUrl + "/" + WITHDRAW_CONFIRM_PATH
            + "?uuid=" + uuid
            + "&rid=" + rid;
    }

    /**
     * @return "base_url" property from /batm/config/lnurl file or null if the file does not exist.
     * @throws RuntimeException if the file exist but cannot be read
     */
    private String getBaseUrlConfiguration() throws RuntimeException {
        Path path = Paths.get(System.getProperty("batm.home", "/batm"), "config", "lnurl");
        if (!Files.exists(path)) {
            return null;
        }
        try (Reader r = Files.newBufferedReader(path)) {
            Properties properties = new Properties();
            properties.load(r);
            return validateBaseUrl(properties.getProperty("base_url"), path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String validateBaseUrl(String baseUrl, Path path) {
        if (baseUrl == null || !baseUrl.startsWith("https://") || baseUrl.endsWith("/")) {
            throw new RuntimeException("LNURL: Invalid base url configuration. "
                + "Use 'base_url' property in '" + path.toAbsolutePath().toString() + "' file. "
                + "It must start with https:// and must not end with '/'");
        }
        return baseUrl;
    }
}
