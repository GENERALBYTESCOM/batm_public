package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class BinanceExchangeWithdrawalNetworkTest {

    @ParameterizedTest
    @MethodSource("withdrawalNetworkProvider")
    void testGetWithdrawalNetwork(BinanceUsdtNetwork usdtNetwork, String cryptocurrency, String expectedNetwork) {
        BinanceComExchange binance = new BinanceComExchange("apiKey", "secretKey", "EUR", usdtNetwork);

        assertThat(binance.getWithdrawalNetwork(cryptocurrency)).isEqualTo(expectedNetwork);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BTC", "ETH", "TRX"})
    void testGetWithdrawalNetwork_NotWithdrawnViaNetwork(String cryptocurrency) {
        BinanceComExchange binance = new BinanceComExchange("apiKey", "secretKey", "EUR", BinanceUsdtNetwork.ETH);

        assertThatThrownBy(() -> binance.getWithdrawalNetwork(cryptocurrency))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cryptocurrency " + cryptocurrency + " is not withdrawn via a network");
    }

    private static Stream<Arguments> withdrawalNetworkProvider() {
        return Stream.of(
            arguments(BinanceUsdtNetwork.ETH, "USDT", "ETH"),
            arguments(BinanceUsdtNetwork.BSC, "USDT", "BSC"),
            arguments(BinanceUsdtNetwork.ETH, "USDTTRON", "TRX"),
            arguments(BinanceUsdtNetwork.BSC, "USDTTRON", "TRX")
        );
    }
}
