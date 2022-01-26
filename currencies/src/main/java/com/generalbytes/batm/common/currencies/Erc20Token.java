package com.generalbytes.batm.common.currencies;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Erc20Token {
    USDT(CryptoCurrency.USDT, 6, "0xdAC17F958D2ee523a2206206994597C13D831ec7"),
    DAI(CryptoCurrency.DAI, 18, "0x89d24A6b4CcB1B6fAA2625fE562bDD9a23260359"), // SAI stable coin
    ;

    /**
     * Maps crypto currency name to the token enum
     */
    public static final Map<String, Erc20Token> ERC20_TOKENS;

    static {
        HashMap<String, Erc20Token> v = new HashMap<>();
        for (Erc20Token erc20Token : values()) {
            v.put(erc20Token.name(), erc20Token);
        }
        ERC20_TOKENS = Collections.unmodifiableMap(v);
    }

    private final CryptoCurrency cryptoCurrency;
    private final int decimals;
    private final String contractAddress;

    Erc20Token(CryptoCurrency cryptoCurrency, int decimals, String contractAddress) {
        this.cryptoCurrency = cryptoCurrency;
        this.decimals = decimals;
        this.contractAddress = contractAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }

    public int getDecimals() {
        return decimals;
    }
}
