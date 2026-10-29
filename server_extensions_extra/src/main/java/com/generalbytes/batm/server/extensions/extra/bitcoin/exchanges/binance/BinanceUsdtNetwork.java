package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Binance network IDs that {@link CryptoCurrency#USDT} can be withdrawn to.
 */
public enum BinanceUsdtNetwork {
    /** Ethereum (ERC20) */
    ETH,
    /** BNB Smart Chain (BEP20) */
    BSC;

    public static final BinanceUsdtNetwork DEFAULT = ETH;

    /**
     * @param value value of the exchange parameter, case-insensitive; {@code null} or blank means not configured
     * @return the network matching the value, {@link #DEFAULT} if the value is not configured
     * @throws IllegalArgumentException if the value is configured but does not match any network
     */
    public static BinanceUsdtNetwork of(String value) {
        if (StringUtils.isBlank(value)) {
            return DEFAULT;
        }

        String trimmedValue = value.trim();
        return Arrays.stream(values())
            .filter(network -> network.name().equalsIgnoreCase(trimmedValue))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported USDT network: " + trimmedValue));
    }
}
