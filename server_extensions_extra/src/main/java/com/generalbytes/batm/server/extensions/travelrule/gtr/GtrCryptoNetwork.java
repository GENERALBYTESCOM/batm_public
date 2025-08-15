package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import lombok.Getter;

/**
 * Enum of supported cryptocurrencies on GTR including supported networks.
 * The networks should be sorted by the most used for a given cryptocurrency,
 * which should speed up address searches in {@link GtrVerifyAddressService#verifyAddress}.
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/ticker-and-network-mapping">Global Travel Rule (GTR) documentation</a>
 */
@Getter
public enum GtrCryptoNetwork {
    ADA("ADA", "BSC", "BNB"),
    BAT("ETH", "BSC", "BNB"),
    BCH("BCH", "BSC", "BNB"),
    BNB("BNB", "BSC", "ETH", "OPBNB", "BSC_PARITY"),
    BTC("BTC", "SEGWITBTC", "LIGHTNING", "ETH", "BSC", "BNB"),
    BUSD("BSC", "MATIC", "ETH", "BNB"),
    DAI("ETH", "BSC", "MATIC"),
    DASH("DASH"),
    DEX("ETH", "BSC"),
    DGB("DGB"),
    DOGE("DOGE", "BSC", "BNB"),
    EGLD("EGLD", "BSC"),
    ETC("ETC", "BSC", "BNB"),
    ETH("ETH", "BSC", "ARBITRUM", "OPTIMISM", "BASE", "ZKSYNCERA",
            "STARKNET", "MANTA", "BNB", "ETH_PARITY"),
    KMD("KMD", "BSC"),
    LSK("LSK"),
    LTC("LTC", "BSC"),
    MKR("ETH", "BSC"),
    NBT("BSC"),
    NULS("NULS", "BSC"),
    PAXG("ETH"),
    SHIB("ETH", "BSC"),
    SYS("SYS"),
    TRX("TRX", "BSC", "ETH", "BNB"),
    USDC("ETH", "SOL", "MATIC", "BSC", "AVAXC", "ARBITRUM", "OPTIMISM", "BASE",
            "TRX", "XLM", "ALGO", "NEAR", "RON", "STATEMINT"),
    USDT("ETH", "TRX", "BSC", "MATIC", "SOL", "AVAXC", "ARBITRUM", "OPTIMISM",
            "BNB", "EOS", "KAVAEVM", "OPBNB", "NEAR", "XTZ", "STATEMINT"),
    XRP("XRP", "ETH", "BSC", "BNB");

    private final String[] networks;

    GtrCryptoNetwork(String... networks) {
        this.networks = networks;
    }

    /**
     * Get {@link GtrCryptoNetwork} based on cryptocurrency in string format.
     *
     * @param cryptocurrency Cryptocurrency.
     * @return {@link GtrCryptoNetwork}
     */
    public static GtrCryptoNetwork getGtrCryptoNetwork(String cryptocurrency) {
        try {
            return GtrCryptoNetwork.valueOf(cryptocurrency);
        } catch (IllegalArgumentException e) {
            throw new TravelRuleProviderException("cryptocurrency '" + cryptocurrency + "' is not supported on GTR");
        }
    }
}

