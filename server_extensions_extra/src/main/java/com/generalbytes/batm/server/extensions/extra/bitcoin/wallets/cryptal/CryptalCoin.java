package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal;

import com.generalbytes.batm.common.currencies.CryptoCurrency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum CryptalCoin {

    BTC(CryptoCurrency.BTC, "BTC", "MAINNET", 8),
    LTC(CryptoCurrency.LTC, "LTC", "MAINNET", 8),
    BCH(CryptoCurrency.BCH, "BCH", "MAINNET", 8),
    ETH(CryptoCurrency.ETH, "ETH", "ERC20", 18),
    TRX(CryptoCurrency.TRX, "TRX", "TRC20", 6),
    USDT(CryptoCurrency.USDT, "USDT", "ERC20", 6),
    USDTTRON(CryptoCurrency.USDTTRON, "USDT", "TRC20", 6),
    USDC(CryptoCurrency.USDC, "USDC", "ERC20", 6);

    private final CryptoCurrency gbCurrency;
    private final String currency;
    private final String network;
    private final int decimals;

    CryptalCoin(CryptoCurrency gbCurrency, String currency, String network, int decimals) {
        this.gbCurrency = gbCurrency;
        this.currency = currency;
        this.network = network;
        this.decimals = decimals;
    }

    public static Optional<CryptalCoin> of(String gbCurrencyCode) {
        if (gbCurrencyCode == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
            .filter(coin -> coin.gbCurrency.getCode().equalsIgnoreCase(gbCurrencyCode))
            .findFirst();
    }

    public static Set<String> gbCodes() {
        return Arrays.stream(values()).map(coin -> coin.gbCurrency.getCode()).collect(Collectors.toSet());
    }

    public String getCurrency() {
        return currency;
    }

    public String getNetwork() {
        return network;
    }

    public String toApiAmount(BigDecimal amount) {
        return amount.setScale(decimals, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
    }
}
