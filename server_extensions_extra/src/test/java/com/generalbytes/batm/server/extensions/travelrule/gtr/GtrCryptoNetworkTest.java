package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class GtrCryptoNetworkTest {

    private static Stream<Arguments> testNetworks_arguments() {
        return Stream.of(
                arguments(GtrCryptoNetwork.ADA, List.of("ADA", "BSC", "BNB")),
                arguments(GtrCryptoNetwork.BAT, List.of("ETH", "BSC", "BNB")),
                arguments(GtrCryptoNetwork.BCH, List.of("BCH", "BSC", "BNB")),
                arguments(GtrCryptoNetwork.BNB, List.of("BNB", "BSC", "ETH", "OPBNB", "BSC_PARITY")),
                arguments(GtrCryptoNetwork.BTC, List.of("BTC", "SEGWITBTC", "LIGHTNING", "ETH", "BSC", "BNB")),
                arguments(GtrCryptoNetwork.BUSD, List.of("BSC", "MATIC", "ETH", "BNB")),
                arguments(GtrCryptoNetwork.DAI, List.of("ETH", "BSC", "MATIC")),
                arguments(GtrCryptoNetwork.DASH, List.of("DASH")),
                arguments(GtrCryptoNetwork.DEX, List.of("ETH", "BSC")),
                arguments(GtrCryptoNetwork.DGB, List.of("DGB")),
                arguments(GtrCryptoNetwork.DOGE, List.of("DOGE", "BSC", "BNB")),
                arguments(GtrCryptoNetwork.ETC, List.of("ETC", "BSC", "BNB")),
                arguments(GtrCryptoNetwork.ETH, List.of(
                        "ETH", "BSC", "ARBITRUM", "OPTIMISM", "BASE", "ZKSYNCERA", "STARKNET", "MANTA", "BNB", "ETH_PARITY"
                )),
                arguments(GtrCryptoNetwork.EGLD, List.of("EGLD", "BSC")),
                arguments(GtrCryptoNetwork.KMD, List.of("KMD", "BSC")),
                arguments(GtrCryptoNetwork.LSK, List.of("LSK")),
                arguments(GtrCryptoNetwork.LTC, List.of("LTC", "BSC")),
                arguments(GtrCryptoNetwork.MKR, List.of("ETH", "BSC")),
                arguments(GtrCryptoNetwork.NBT, List.of("BSC")),
                arguments(GtrCryptoNetwork.NULS, List.of("NULS", "BSC")),
                arguments(GtrCryptoNetwork.PAXG, List.of("ETH")),
                arguments(GtrCryptoNetwork.SHIB, List.of("ETH", "BSC")),
                arguments(GtrCryptoNetwork.SYS, List.of("SYS")),
                arguments(GtrCryptoNetwork.TRX, List.of("TRX", "BSC", "ETH", "BNB")),
                arguments(GtrCryptoNetwork.USDC, List.of(
                        "ETH", "SOL", "MATIC", "BSC", "AVAXC", "ARBITRUM", "OPTIMISM", "BASE",
                        "TRX", "XLM", "ALGO", "NEAR", "RON", "STATEMINT"
                )),
                arguments(GtrCryptoNetwork.USDT, List.of(
                        "ETH", "TRX", "BSC", "MATIC", "SOL", "AVAXC", "ARBITRUM", "OPTIMISM",
                        "BNB", "EOS", "KAVAEVM", "OPBNB", "NEAR", "XTZ", "STATEMINT"
                )),
                arguments(GtrCryptoNetwork.XRP, List.of("XRP", "ETH", "BSC", "BNB"))
        );
    }

    @ParameterizedTest
    @MethodSource("testNetworks_arguments")
    void testNetworks(GtrCryptoNetwork cryptocurrency, List<String> networks) {
        assertTrue(cryptocurrency.getNetworks().length > 0);
        assertEquals(networks.size(), cryptocurrency.getNetworks().length);
        for (int i = 0; i < networks.size(); i++) {
            assertEquals(networks.get(i), cryptocurrency.getNetworks()[i]);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ADA", "BAT", "BCH", "BNB", "BTC", "BUSD", "DAI", "DASH", "DEX", "DGB", "DOGE", "ETC", "ETH", "EGLD",
        "KMD", "LSK", "LTC", "MKR", "NBT", "NULS", "PAXG", "SHIB", "SYS", "TRX", "USDC", "USDT", "XRP"
    })
    void testGetGtrCryptoNetwork(String cryptocurrency) {
        GtrCryptoNetwork cryptoNetwork = GtrCryptoNetwork.getGtrCryptoNetwork(cryptocurrency);
        assertInstanceOf(GtrCryptoNetwork.class, cryptoNetwork);
    }

    @Test
    void testGetGtrCryptoNetwork_notSupportedCryptocurrency() {
        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> GtrCryptoNetwork.getGtrCryptoNetwork("not-supported-cryptocurrency")
        );

        assertEquals("cryptocurrency 'not-supported-cryptocurrency' is not supported on GTR", exception.getMessage());
    }

}