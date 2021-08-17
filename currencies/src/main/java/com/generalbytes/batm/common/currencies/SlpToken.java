package com.generalbytes.batm.common.currencies;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum SlpToken {
    SPICE(8, "4de69e374a8ed21cbddd47f2338cc0f479dc58daa2bbe11cd604ca488eca0ddf"),
    DDB(2, "a7055d2d258cbdd66728edc843b0088eb68785411f6eaaf5c88a725f69a8f346"),
    SUNCASH(2,"35b354d165a1a8e6b9af3483acd23f8a55e65a2762429f7e4cacb2a11ec665a8"),
    ;


    public static final Map<String, SlpToken> SLP_TOKENS;

    static {
        HashMap<String, SlpToken> v = new HashMap<>();
        for (SlpToken slpToken : values()) {
            v.put(slpToken.name(), slpToken);
        }
        SLP_TOKENS = Collections.unmodifiableMap(v);
    }

    private final String tokenId;
    private final int decimals;

    SlpToken(int decimals, String tokenId) {
        CryptoCurrency.valueOfCode(name()); // make sure a cryptocurrency with the same name exists
        this.tokenId = tokenId;
        this.decimals = decimals;
    }

    public String getTokenId() {
        return tokenId;
    }

    public int getDecimals() {
        return decimals;
    }
}
