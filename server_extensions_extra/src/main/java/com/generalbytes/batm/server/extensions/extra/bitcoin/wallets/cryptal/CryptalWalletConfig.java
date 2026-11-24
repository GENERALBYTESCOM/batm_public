package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal;

import java.util.StringTokenizer;

public record CryptalWalletConfig(String baseUrl, String apiKey, String privateKey, String addressName) {

    public static final String DEFAULT_ADDRESS_NAME = "GB ATM";

    public static CryptalWalletConfig parse(StringTokenizer st) {
        String scheme = "https";
        String host = st.nextToken();
        if ("https".equalsIgnoreCase(host) || "http".equalsIgnoreCase(host)) {
            scheme = host.toLowerCase();
            host = st.nextToken();
        }
        host = host.replaceAll("^/+|/+$", "");
        String path = "";
        int slash = host.indexOf('/');
        if (slash >= 0) {
            path = host.substring(slash);
            host = host.substring(0, slash);
        }

        String next = st.nextToken();
        String port = "";
        if (next.matches("\\d{1,5}")) {
            port = ":" + next;
            next = st.nextToken();
        }
        String apiKey = next;
        String privateKey = st.nextToken();
        String addressName = st.hasMoreTokens() ? st.nextToken().trim() : "";
        if (addressName.isEmpty()) {
            addressName = DEFAULT_ADDRESS_NAME;
        }
        return new CryptalWalletConfig(scheme + "://" + host + port + path, apiKey, privateKey, addressName);
    }

    @Override
    public String toString() {
        return "CryptalWalletConfig[baseUrl=" + baseUrl + ", addressName=" + addressName + "]";
    }
}
